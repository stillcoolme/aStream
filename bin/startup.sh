BASEPATH=$(cd `dirname $0`; pwd)
SPARK_BIN=/data/spark-2.1.1-bin-hadoop2.6
PHOENIX_BIN=/data/phoenix-4.6.0-HBase-1.0-bin
jarsLib=$(echo /data/sparkStream/libs/*.jar | tr ' ' ',')
executor_memory=120g
cores=40
total_cores=120
master_ip=mongo16
zk_qurom=172.16.21.189:2181

kafka_param="172.16.21.189:9092 astream monitor"

#打印帮助信息
function usage(){
cat << eof
sparkAgent日志保存在/data/sparkAgent/logs/目录
Usage:	`basename $0` [-m v] [-z v] [-e v] [-h]
  -m：spark集群master主机名或ip，不指定默认为master
  -z：zookeeper地址，不指定默认为master:2181
  -e：启动sparkAgent时executor内存,不指定默认为10g
  -h：打印帮助信息
Example: `basename $0`
     or: `basename $0` -m master -e 10g 
eof
exit 1
}


#解析参数
while getopts 'm:z:e:h' OPT; do
    case $OPT in
	m)
	    master_ip="$OPTARG";;
	z)
	    zk_qurom="$OPTARG";;
	e)
	    executor_memory="$OPTARG";;
	h)
	    usage;;
	?)
	    usage;; 
    esac
done

mkdir -p ${BASEPATH}/logs/

nohup $SPARK_BIN/bin/spark-submit \
--jars=$jarsLib \
--class com.stillcoolme.service.AStream \
--total-executor-cores $total_cores \
--executor-cores $cores \
--executor-memory $executor_memory \
--master spark://$master_ip:7077 ${BASEPATH}/aStream-1.0.0.jar $kafka_param \
>> ${BASEPATH}/logs/aStream.log 2>&1 &

