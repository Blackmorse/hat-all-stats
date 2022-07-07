#!/bin/bash

declare -a tables=("match_details" "player_stats" "team_details" "team_rankings")

for table in "${tables[@]}"; do
  count=$(clickhouse-client --query "select count() from hattrick.$table") 

  if [[ $count -eq 0 ]]; then
    cat $(dirname "$0")/sample_data/$table.csv | clickhouse-client --query "insert into hattrick.$table format CSV"
  fi
done
