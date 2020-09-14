# Project structure
There are following subprojects:
 - Web
 - Loader
 - Front
 - Common
 - Ansible

Also, project heavily depends on [hattrick-java-api](https://github.com/Blackmorse/hattrick-java-api) - Java library for interacting with CHPP Hattrick API.

## Database
Only [ClickHouse](https://clickhouse.tech) NoSQL OLAP database is used in this project. It is impossible to run the project without ClickHouse.

## Subprojects
### 1. Web
Web-application written in Scala. For building sbt is used. For front it uses Twirl templates, but it's currently being shifted towards providing only REST Api and extracting front to React (Front subproject). 
Web uses ClickHouse in read-only mode.

### 2. Loader
Loader loads all the data to the ClickHouse. Written in Java, also uses Spring Boot, JavaRX and gradle.

### 3. Front
Currently, under developments. React + Typescript are used. Eventually, this subproject will be responsible for dynamic content displaying which will unveil a lot of UI capabilities.

### 4. Common
Common code to avoid duplicates between Web and Loader.

### 5. Ansible
Tools for automatic applications deployment.

## hattrick-java-api
<b>hattrick-java-api</b> is a separated project which is just a Java wrapper for CHPP REST API.


# Building the project

## Prerequisites
  - Java (11 or 14 version, I use OpenJDK 11)
  - Scala 2.12
  - Build tools: gradle, maven, sbt (latest versions)
  - Docker 

## Installing dependencies

First, you need to build and install dependencies to your local repository. 

### Common
```
cd common  
gradle publishToMavenLocal
```

### hattrick-java-api

```
git clone https://github.com/Blackmorse/hattrick-java-api.git
cd hattrick-java-api
mvn install
```

## ClickHouse
If you are using Linux you can easily install ClickHouse on your machine with instructions from [documentation](https://clickhouse.tech/docs/en/getting-started/install/)

It is strongly advised to use Docker. You can find the ClickHouse container container [here](https://hub.docker.com/r/yandex/clickhouse-server/). Please, can use the latest Docker version (in production I use <i>20.4</i>, for local development - <i>20.6</i>).

Follow 3 steps below to configure docker image:
 - Publish port 8123 to use official [ClickHouse JDBC driver](https://github.com/ClickHouse/clickhouse-jdbc)
 - Mount volume `/var/lib/clickhouse/` to avoid loosing data after container restart
 - create database `hattrick` with schema from `sql/init_scripts/`

 You can use next command to create a docker container:

 ```
docker run  --name hattrick-clickhouse-server --ulimit nofile=262144:262144 -v <path_to_repo>/sql/init_scripts:/docker-entrypoint-initdb.d -p 8123:8123 -v <volume_location>:/var/lib/clickhouse/  yandex/clickhouse-server
 ```

 - `<path_to_repo>` - absolute path to place where you have cloned repo. Docker likes absolute paths. At Linux I just use `$(pwd)` when running from repo directory
 - `<volume_location>` - just any path at your local system, docker will mount ClickHouse files here. 

 To connect to server container by `clickhouse-client` you can also use docker container:
 ```
docker run -it --rm --link hattrick-clickhouse-server:clickhouse-server yandex/clickhouse-client --host hattrick-clickhouse-server
 ```

## Loader
To start using Web you need to have data at ClickHouse. You should populate it using the Loader.

### Configure 
Copy `ansible/install-loader/templates/application.yml.j2` to `loader/src/main/resources/` and rename it to `application.yml`.
Replace values in double curly brackets (brackets should be also removed) to actual values:
 - `threads` - number of threads which loader will use for work. Try 20, for example, you may tune it in the future
 - `customerKey`, `customerSecret`, `accessToken`, `accessTokenSecret` - your credentials for using CHPP
 - `loader.databaseName` - hattrick
 - `web.port` - type port that will be used by web.
 - `telegram` section can be filled by random values. It doesn't work now :)

### Run
You can run loader from your IDE (main class: `com.blackmorse.hattrick.LoaderApplication`). For loading countries you must specify command-line arguments `load <countries>`.
 - `<countries>` - comma-separated list of countries <b>written with you HT-language</b>

Also you can run it from command line. First, build jar:
```
cd loader
gradle assemble
```
Then run jar:
```
build/libs/loader-0.0.1-SNAPSHOT.jar load <countries>
```

<b>!!!!!</b> To avoid errors <i>Salvador</i> must be **always** loaded. It is the country which plays last series matches, so web uses information about it.

## Web

### Configure
Copy `ansible/install-web/templates/application.conf.j2` to `web/conf` and rename it to `application.conf`. Edit in it in the same way you did for the Loader

 - Fill in CHPP keys
 - `clickhouse.jdbc_url` - put there `jdbc:clickhouse://localhost:8123` 
 - `db.default.user`,`db.default.password` - delete this lines (<i>default user will be used</i>)

 ### Run

 ```
cd web
sbt "run <port>"
 ```

  - `<port>` - you can choose port where web app will be running. By typing `sbt run` application will run on port 9000

After that you can open `https://localhost:<port>`. 

Installation complete!
