#!/bin/bash

POSITIONAL_ARGS=()
pidCount=0
tname=""

while [[ $# -gt 0 ]]; do
  case $1 in
    -d|--driver)
      pid=`jps | grep DriverDaemon| awk '{ print $1}'`
      ((pidCount++))
      shift # past argument
      ;;
    -c|--executor)
      pid=`jps | grep DriverDaemon| awk '{ print $1}'`
      ((pidCount++))
      shift # past argument
      ;;
    -p|--pid)
      pid=$2
      ((pidCount++))
      shift # past argument
      shift # past value
      ;;
    -t|--thread)
      tname=" -t $2"
      shift # past argument
      shift # past value
      ;;
    -g|--group)
      gname=$2
      shift # past argument
      shift # past value
      ;;
    -*|--*)
      echo "Unknown option $1"
      echo "Usage  jtrace [-d | -c | -p pid] [-t threadName] [ -g groupName] <className> <methodName>"
      exit 1
      ;;
    *)
      POSITIONAL_ARGS+=("$1") # save positional arg
      shift # past argument
      ;;
  esac
done


set -- "${POSITIONAL_ARGS[@]}" # restore positional parameters


if [[ $pidCount -ne 1 || $# -le 1 ]]
then
        echo "Usage  jtrace [-d | -c | -p pid] [-t threadName] [ -g groupName] <className> <methodName>"
        exit 1
fi


actionFile=actions.$$
echo "elapsed_time_in_ms REGEXP("$1") REGEXP("$2")" >$actionFile

java -cp `pwd`/trace-agent/target/trace-agent-1.0-SNAPSHOT.jar:$JAVA_HOME/lib/tools.jar:./ databricks.trace.AgentLoader `pwd`/trace-agent/target/trace-agent-1.0-SNAPSHOT.jar $tname -a `pwd`/$actionFile -p $pid
rm $actionFile
