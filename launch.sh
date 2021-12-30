#!/bin/sh
#
# This script expects the standdard JMeter command parameters.
#

set -e
freeMem=`awk '/MemFree/ { print int($2/1024) }' /proc/meminfo`
s=$(($freeMem/10*8))
x=$(($freeMem/10*8))
n=$(($freeMem/10*2))
export JVM_ARGS="-Xmn${n}m -Xms${s}m -Xmx${x}m"

echo "START Running Apache JMeter on `date`"
echo "JVM_ARGS=${JVM_ARGS}"
echo "$CMD args=$@"

timestamp=$(date +%Y%m%d_%H%M%S)
jmeter_path="/mnt/jmeter"
# Keep entrypoint simple: we must pass the standard JMeter arguments
jmeter -n -X -Jclient.rmi.localport=7000 -Jserver.rmi.ssl.disable=true -t ${jmeter_path}/jmx/$@.jmx -l ${jmeter_path}/client/result_${timestamp}.csv -j ${jmeter_path}/client/jmeter_${timestamp}.log
echo "END Running Jmeter on `date`"

export SMTP_HOST_NAME="email-smtp.us-east-1.amazonaws.com"
export SMTP_AUTH_USER="AKIAWTZ3RP5V45XOY3TH"
export SMTP_AUTH_PWD="BNiLedQuVHvhCOgIcnK1OgTkmok2ExHyIyzAISNBF2EK"
export FROM="vivek.topiya@thegatewaycorp.co.in"
export TO="vivek.topiya@thegatewaycorp.co.in"
export ZIP_FILENAME="${jmeter_path}/JmeterRun-$@-${timestamp}.zip"

zip -r ${ZIP_FILENAME} ${jmeter_path}/client/

java -jar ${jmeter_path}/SesMailer.jar ${ZIP_FILENAME}