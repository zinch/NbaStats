## Prerequisites

By default, web scraper will use `Google Chrome`, so it should be installed beforehand.

## How to build

    ./gradlew shadowJar


## How to run

    java -jar ./app/build/libs/app-all.jar 'Luka Doncic'

## How it works
If a player's name matches multiple players like _Luka_,
then app will print statistics for each match instead of throwing an exception