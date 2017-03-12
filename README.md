# Collapsar

[![Build status](https://travis-ci.org/amolkov/collapsar.svg?branch=master)](https://travis-ci.org/amolkov/collapsar)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/3c6d5ab71a6e45b9997fbc9d5f9264a7)](https://www.codacy.com/app/amolkov/collapsar?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=amolkov/collapsar&amp;utm_campaign=Badge_Grade)

A simple android app that helps user to discover and read about Astronomy Picture of the Day (APOD). Each day a different image of our universe along with a brief explanation.

## Features

- Discover the best astronomy pictures
- Read explanation for each picture
- Save favorite picture locally or share with others

## Screenshots

<img width="45%" src=".github/screen1.png" /> <img width="45%" src=".github/screen2.png" />

## Setting up

#### Clone the repo

```
$ git clone https://github.com/amolkov/collapsar.git
$ cd collapsar
```

#### API key

The app uses [NASA Astronomy Picture of the Day (APOD) API](https://api.nasa.gov/api.html#apod) to get pictures. You must provide your own [API key](https://api.nasa.gov/index.html#apply-for-an-api-key) in order to build the app.

Just put your API key into **_gradle.properties_**:

```
NASA_API_KEY="abc123"
```

#### Building

You can build the app with Android Studio or with `$ ./gradlew build` command.

## Libraries

* [**ButterKnife**](https://github.com/JakeWharton/butterknife): for android views binding.
* [**Glide**](https://github.com/bumptech/glide): for image loading and caching.
* [**OkHttp**](https://github.com/square/okhttp): as http client.
* [**Retrofit**](https://github.com/square/retrofit): for communicating with web services.
* [**RxAndroid**](https://github.com/ReactiveX/RxAndroid): for android specific reactive extensions.
* [**RxJava**](https://github.com/ReactiveX/RxJava): for java reactive extensions.
* [**SQLBrite**](https://github.com/square/sqlbrite): for wrapping and introducing reactive stream semantics to sql queries.
* [**Timber**](https://github.com/JakeWharton/timber): for logging.

## License

    Copyright 2016 Alexander Molkov

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
