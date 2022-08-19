# G30L0
[![Build Status](https://travis-ci.org/esdk/g30l0.svg?branch=master)](https://travis-ci.org/esdk/g30l0)
[![Coverage Status](https://coveralls.io/repos/github/esdk/g30l0/badge.svg?branch=master)](https://coveralls.io/github/esdk/g30l0?branch=master)

This is the source code of g30l0, an abas Essentials App build on the abas Essentials SDK.

## General setup
If you are using proxies, add a gradle.properties file to your $GRADLE_USER_HOME.

```
#If you use a proxy add it here
systemProp.http.proxyHost=<webproxy>
systemProp.http.proxyPort=<port>
systemProp.https.proxyHost=<webproxy>
systemProp.https.proxyPort=<port>
```

Run `./initGradleProperties.sh` in your terminal (use Git Bash on Windows)

Use your favorite IDE to import the project.

## Installation
To install the project make sure you are running the docker-compose.yml file 
or else change the application.conf file accordingly to use another erp client.

To use the project's docker-compose.yml file, in the project's root directory run:
```shell
docker login --username <extranet user> --password <extranet password> sdp.registry.abas.sh
docker-compose up -d
```

Now you can install the project as follows:
```shell
./gradlew fullInstall
```

## Development
You can make changes such as manipulating the app's logic or adding/removing components.

First install the app as described in [Installation](#Installation) using:
```shell
./gradlew fullInstall
``` 

To deploy your changes run:
```shell
./gradlew syncCode
```
