#!/bin/bash

HOST=$1
PORT=$2
DATABASE=$3

clickhouse-client  --host $HOST --port $PORT --query "CREATE DATABASE IF NOT EXISTS $DATABASE"
ls | grep .sql | xargs -I {} cat {} | clickhouse-client -m -n --host $HOST --port $PORT --database $DATABASE 
