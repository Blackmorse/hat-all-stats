#!/bin/bash

declare -a tables=("match_details" "player_stats" "team_details" "team_rankings")

for table in "${tables[@]}"; do
  cat $(dirname "$0")/sample_data/$table.csv | clickhouse-client --query "insert into hattrick.$table format CSV"
done
