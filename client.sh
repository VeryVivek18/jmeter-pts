#!/bin/sh
#1
#declare -a SERVER_IPS="10.188.228.169"

#2
timestamp=$(date +%Y%m%d_%H%M%S)
volume_path=$(pwd)
jmeter_path=/mnt/jmeter

#5
echo "Create client and execute test"
docker run \
  --network host \
  -v "${volume_path}":${jmeter_path} \
  --rm \
  knovel-jmeter:1.0 \
  -n -X \
  -Jclient.rmi.localport=7000 -Jserver.rmi.ssl.disable=true \
  -t ${jmeter_path}/jmx/SearchSubstancesInternalSolr.jmx \
  -l ${jmeter_path}/client/result_${timestamp}.csv \
  -j ${jmeter_path}/client/jmeter_${timestamp}.log

#  -R $(echo $(printf ",%s" "${SERVER_IPS[@]}") | cut -c 2-) \