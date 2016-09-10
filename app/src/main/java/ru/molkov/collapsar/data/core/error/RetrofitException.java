package ru.molkov.collapsar.data.core.error;

import java.io.IOException;

import retrofit.HttpException;
import retrofit.Response;
import ru.molkov.collapsar.App;
import ru.molkov.collapsar.R;

public class RetrofitException extends RuntimeException {
    private final String mUrl;
    private final Response mResponse;
    private final Kind mKind;

    RetrofitException(String message, Throwable exception, String url, Response response, Kind kind) {
        super(message, exception);
        mUrl = url;
        mResponse = response;
        mKind = kind;
    }

    public static RetrofitException forbiddenError(HttpException exception) {
        return new RetrofitException(getExceptionMessage(R.string.error_forbidden), exception, getExceptionUrl(exception), exception.response(), Kind.FORBIDDEN);
    }

    public static RetrofitException tooManyRequestsError(HttpException exception) {
        return new RetrofitException(getExceptionMessage(R.string.error_too_many_requests), exception, getExceptionUrl(exception), exception.response(), Kind.TOO_MANY_REQUESTS);
    }

    public static RetrofitException clientError(HttpException exception) {
        return new RetrofitException(getExceptionMessage(R.string.error_client), exception, getExceptionUrl(exception), exception.response(), Kind.CLIENT);
    }

    public static RetrofitException serverError(HttpException exception) {
        return new RetrofitException(getExceptionMessage(R.string.error_server), exception, getExceptionUrl(exception), exception.response(), Kind.SERVER);
    }

    public static RetrofitException networkError(IOException exception) {
        return new RetrofitException(getExceptionMessage(R.string.error_network), exception, null, null, Kind.NETWORK);
    }

    public static RetrofitException unexpectedError(Throwable exception) {
        return new RetrofitException(getExceptionMessage(R.string.error_unexpected), exception, null, null, Kind.UNEXPECTED);
    }

    private static String getExceptionMessage(int id) {
        return App.getContext().getResources().getString(id);
    }

    private static String getExceptionUrl(HttpException exception) {
        Response response = exception.response();
        return response.raw().toString();
    }

    public String getUrl() {
        return mUrl;
    }

    public Response getResponse() {
        return mResponse;
    }

    public Kind getKind() {
        return mKind;
    }

    public enum Kind {
        /**
         * Called for 403 responses.
         */
        FORBIDDEN,
        /**
         * Called for 429 responses.
         */
        TOO_MANY_REQUESTS,
        /**
         * Called for [400, 500) responses, except 403 and 429.
         */
        CLIENT,
        /**
         * Called for [500, 600) responses.
         */
        SERVER,
        /**
         * Called for network errors while making the call.
         */
        NETWORK,
        /**
         * Called for unexpected errors while making the call.
         */
        UNEXPECTED
    }
}
