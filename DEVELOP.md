# Project structure
There are couple of subprojects:
 - Web
 - Loader
 - Front
 - Common
 - Ansible

Also, project highly depends on Java library for interacting with CHPP Hattrick API: [hattrick-java-api](https://github.com/Blackmorse/hattrick-java-api)

## Database
Only [Clickhouse](https://clickhouse.tech) used in this project. It's NoSQL OLAP database. Without Clickhouse Hattid will be impossible.

## Subprojects
I will briefly describe every subproject and then provide and instruction for running them

### 1. Web
Web - web-application written in Scala. For building sbt is used. For front It uses Twirl templates, but I'm smoothly shifting it towards providing only REST Api, and extracting front to React (Front subproject). 
Web uses Clickhouse in read-only mode.

### 2. Loader
Loader loads all the data to the Clickhouse. Written in Java, also uses Spring Boot, JavaRX and gradle - as build tool.

### 3. Front
Under developments. React + Typescript will allows to provide dynamic content which will open a lot of capabilities for UI.

### 4. Common
Just common code, to avoid duplicates betwwen Web and Loader

### 5. Ansible
Tools for automatic deploy applications to server

## hattrick-java-api
<b>hattrick-java-api</b> is separate project, which just an Java wrapper for CHPP REST API


# Building the project

## Prerequisites
  - Java (11 or 14 version, I use OpenJDK 11)
  - Scala 2.12
  - Build tools: gradle, maven, sbt (latest versions)
  - Docker 

## Installing dependencies

First, you need to build and install to local repository dependencies

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

## Clickhouse
If you are using Linux you can easily install ClickHouse on your maching, just follow the instructions from [documentation](https://clickhouse.tech/docs/en/getting-started/install/)

Also the good choice is to use Docker. [Here](https://hub.docker.com/r/yandex/clickhouse-server/) you can find Clickhouse container for Docker. You can use latest version of Docker (at production I use <i>20.4</i>, for local development - <i>20.6</i>).

You'll need 3 things to configure docker image properly:
 - Publish port 8123 to use official [Clickhouse JDBC driver](https://github.com/ClickHouse/clickhouse-jdbc)
 - Mount volume `/var/lib/clickhouse/` to avoid loosing data after container restart
 - create database `hattrick` with schema from `sql/init_scripts/`

 You can use next command for creating docker container:

 ```
docker run  --name hattrick-clickhouse-server --ulimit nofile=262144:262144 -v <path_to_repo>/sql/init_scripts:/docker-entrypoint-initdb.d -p 8123:8123 -v <volume_location>:/var/lib/clickhouse/  yandex/clickhouse-server
 ```

 - `<path_to_repo>` - absolute path to place where you've clonned repo. Docker likes absolute paths. At Linux I just use `$(pwd)` when running from repo directory
 - `<volume_location>` - just any path at your local system, docker will mount ClickHouse files here. 

 To connect to server container by `clickhouse-client` you can also use docker container:
 ```
docker run -it --rm --link hattrick-clickhouse-server:clickhouse-server yandex/clickhouse-client --host hattrick-clickhouse-server
 ```

## Loader
To start using Web you need to have data at Clickhouse. To load them, Loader should be used.

### Configure 
Copy `ansible/install-loader/templates/application.yml.j2` to `loader/src/main/resources/` and rename it to `application.yml`.
Replace values in double curly brackets(braces should be also removed) to actual values:
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

<b>!!!!!</b> To avoid errors, among others country <i>Salvador</i> should be loaded. It is the country which plays last series matches, so web uses information about it.

## Web

### Configure
Copy `ansible/install-web/templates/application.conf.j2` to `web/conf` and rename it to `application.conf`. Edit in it like you did for Loader

 - Fill in CHPP keys
 - `clickhouse.jdbc_url` - put there `jdbc:clickhouse://localhost:8123` 
 - `db.default.user`,`db.default.password` - delete this lines (<i>default user will be used</i>)

 ### Run

 ```
cd web
sbt "run <port>"
 ```

  - `<port>` - you can choose port where web app will be running. By typing `sbt run` application will run on 9000 port

After that you can open `https://localhost:<port>`. 

Thats all!