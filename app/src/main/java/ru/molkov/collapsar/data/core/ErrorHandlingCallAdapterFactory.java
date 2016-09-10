package ru.molkov.collapsar.data.core;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import retrofit2.Call;
import retrofit2.CallAdapter;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.HttpException;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import ru.molkov.collapsar.data.core.error.RetrofitException;
import rx.Observable;
import rx.functions.Func1;

public class ErrorHandlingCallAdapterFactory extends CallAdapter.Factory {
    private final RxJavaCallAdapterFactory mOriginal;

    private ErrorHandlingCallAdapterFactory() {
        mOriginal = RxJavaCallAdapterFactory.create();
    }

    public static CallAdapter.Factory create() {
        return new ErrorHandlingCallAdapterFactory();
    }

    @Override
    public CallAdapter<?> get(Type returnType, Annotation[] annotations, Retrofit retrofit) {
        return new CallAdapterWrapper(retrofit, mOriginal.get(returnType, annotations, retrofit));
    }

    private static class CallAdapterWrapper implements CallAdapter<Observable<?>> {
        private final Retrofit mRetrofit;
        private final CallAdapter<?> mWrapped;

        public CallAdapterWrapper(Retrofit retrofit, CallAdapter<?> wrapped) {
            mRetrofit = retrofit;
            mWrapped = wrapped;
        }

        @Override
        public Type responseType() {
            return mWrapped.responseType();
        }

        @SuppressWarnings("unchecked")
        @Override
        public <R> Observable<?> adapt(Call<R> call) {
            return ((Observable) mWrapped.adapt(call)).onErrorResumeNext(new Func1<Throwable, Observable>() {
                @Override
                public Observable call(Throwable throwable) {
                    return Observable.error(asRetrofitException(throwable));
                }
            });
        }

        private RetrofitException asRetrofitException(Throwable throwable) {
            if (throwable instanceof HttpException) {
                HttpException httpException = (HttpException) throwable;
                int code = httpException.code();

                if (code == 403) {
                    return RetrofitException.forbiddenError(httpException);
                } else if (code == 429) {
                    return RetrofitException.tooManyRequestsError(httpException);
                } else if (code >= 400 && code < 500) {
                    return RetrofitException.clientError(httpException);
                } else if (code >= 500 && code < 600) {
                    return RetrofitException.serverError(httpException);
                }
            } else if (throwable instanceof IOException) {
                return RetrofitException.networkError((IOException) throwable);
            }

            return RetrofitException.unexpectedError(throwable);
        }
    }

}
