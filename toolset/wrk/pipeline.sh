#!/bin/bash

let max_threads=$(cat /proc/cpuinfo | grep processor | wc -l)

for c in $levels
do
echo ""
echo "---------------------------------------------------------"
echo " Concurrency: $c for $name"
echo " wrk -H 'Host: $server_host' -H 'Accept: $accept' -H 'Connection: keep-alive' --latency -d $duration -c $c --timeout 8 -t $(($c>$max_threads?$max_threads:$c)) $url -s pipeline.lua -- $pipeline"
echo "---------------------------------------------------------"
echo ""
STARTTIME=$(date +"%s")
wrk -H "Host: $server_host" -H "Accept: $accept" -H "Connection: keep-alive" --latency -d $duration -c $c --timeout 8 -t "$(($c>$max_threads?$max_threads:$c))" $url -s pipeline.lua -- $pipeline
echo "STARTTIME $STARTTIME"
echo "ENDTIME $(date +"%s")"
sleep 2
done
