#!/bin/bash

docker run -it --rm --link hattrick-clickhouse:clickhouse-server --network hattrick_network yandex/clickhouse-client:20.4.4.18 --host clickhouse-server
