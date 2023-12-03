# Trace HotSwap Agent
This is a java agent which attaches to a running jvm and outputs information for selected methods from selected class. 
The class and the methods to be traced is provided in command line using regular expression.

For each matching method of the following details will be printed in stderr:

Thread name, Method name, The arguments it is called with ( when the method is called), The return value and time it took to complete ( when the method returns)

Sample output:
Thread[WRAPPER-ReplId-f8fce-deb18-90c2d ] public java.lang.String org.apache.spark.sql.catalyst.plans.logical.EventTimeWatermark$.delayKey() called with []
Thread[ WRAPPER-ReplId-f8fce-deb18-90c2d ] public java.lang.String org.apache.spark.sql.catalyst.plans.logical.EventTimeWatermark$.delayKey() took 0 (ms) to execute. Returning spark.watermarkDelayMs

The already built binary can be downloaded from the release link: https://github.com/bimalenduc/java-trace-tool/releases/download/v1.0.0/java-trace-tool.tar.gz

To run it on a databricks notebook or web terminal you can use this sample code :

%sh<br />   
cd /databricks/driver/<br />   

wget https://github.com/bimalenduc/java-trace-tool/releases/download/v1.0.0/java-trace-tool.tar.gz<br />   
tar xvfz java-trace-tool.tar.gz 2>/dev/null<br />   
cd java-trace-tool<br />   
echo "Running command "<br />   
./jtrace.sh -d   "org.apache.spark.sql.catalyst.plans.[^EventTimeWatermark].*" "[a-z,A-Z].*"<br />   

Change the class name and method name regular expression to the class and method name you want to trace.


 ### Usage:-

The tool can be invoked from command line using the following command

#./jtrace [-d | -c | -p pid] [-t threadName] [ -g groupName] <className> <methodName>
-d  : This option is used to trace the driver process

-e  : Trace the executor process

 -i  : pid To trace any jvm process using its pid

-t   : threadName trace only the thread having the argument threadName in its threadname

-g  : groupName This option is not functional yet. It is supposed to be a name which will trace a predefined set of methods form certain classes to address any specific kind of issue. For ex:- say group name kafka can trace some important kafka api to gather the time spent in those API calls.

className : This is a mandatory argument. It is a regular expression to match the class name which needs to be traced. 

methodName : This is a mandatory argument. It is a regular expression to match any methods from the any of above matching classnames. 

The command loads the java agent and prints the information of all matching methods from the matching class names.

Example:-

*Thread[Thread-69 ] public org.apache.spark.sql.catalyst.plans.logical.LogicalPlan org.apache.spark.sql.catalyst.optimizer.SimplifyCasts$.apply(org.apache.spark.sql.catalyst.plans.logical.LogicalPlan) called with [DeserializeToObject createexternalrow(count(1)#12.toString, StructField(count(1),StringType,false)), obj#368: org.apache.spark.sql.Row
+- LocalRelation <empty>, [count(1)#12]
]*

*Thread[ Thread-69 ] public org.apache.spark.sql.catalyst.plans.logical.LogicalPlan org.apache.spark.sql.catalyst.optimizer.SimplifyCasts$.apply(org.apache.spark.sql.catalyst.plans.logical.LogicalPlan) took 0 (ms) to execute. Returning DeserializeToObject createexternalrow(count(1)#12.toString, StructField(count(1),StringType,false)), obj#368: org.apache.spark.sql.Row
+- LocalRelation <empty>, [count(1)#12]*
