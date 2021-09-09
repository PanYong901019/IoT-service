#!/bin/bash

service_list=/root/script/service_list.txt
echo $service_list
echo "===========================================|bym-测试环境-service|==|`date +%Y年%m月%d日—%H:%M:%S`|=================================================="
printf "\033[1m%-30s \t %-8s \t %-15s \t %-17s \t %-17s \033[0m\n" "项目名:端口       " "进程号      " "启动状态      " "运行时长      " "项目路径"
cat $service_list|grep -v "^#"|grep -v "^$"|while read line
do
        server=`echo $line|awk -F: '{print $1}'`
        project_name=`echo $line|awk -F: '{print $2}'`
        port=`echo $line|awk -F: '{print $3}'`
        project_type=`echo $line|awk -F: '{print $4}'`
        project_path=`echo $line|awk -F: '{print $5}'`
        pid=`netstat -tnlp|grep $port |awk '{print $7}'| awk -F"/" '{print $1}'`
        if [ "$pid" != "" ];then
                run_time=`ps -eo pid,etime | grep $pid | grep -v grep | awk '{print $2}'`
                if [ `ps -ef |grep -w $pid|grep debug|wc -l` -gt 0 ];then
                        printf "%-30s \t %-8s \t \033[0;31m%-15s \t \033[0m%-17s %-17s \n" "$project_name:$port" "$pid" "debugging...." "$run_time" "$project_path"
                else
                        printf "%-30s \t %-8s \t \033[0;32m%-15s \t \033[0m%-17s %-17s \n" "$project_name:$port" "$pid" "running...." "$run_time" "$project_path"
                fi
        else
                printf "%-30s \t %-8s \t \033[0;31m%-15s \t \033[0m%-17s %-17s \n" "$project_name:$port" "0" "stoped...." "00:00:00" "$project_path"
        fi
done
echo "=============================================================================================================================================="
