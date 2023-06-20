# Trace HotSwap Agent
This is a java agent which attaches to a running jvm and outputs information for selected methods. 
It prints the following details when a matching method from matching class is called :

Thread name

Method name

The arguments it is called with ( when the method is called)

The return value and time it took to complete ( when the method returns)

      Sample oputput is in following form and can be found in stderr log

     [ThreadName] <Function Name> called with [ arguments]

     OR

     [ThreadName] <Function Name> took 0 (ms) to execute. Returning < return values>

The github link to code is  GitHub - bimalenduc/java-trace-tool: Tool to trace java methods in JVM 

The built binary can be downloaded from the release link: https://github.com/bimalenduc/java-trace-tool/releases/download/v1.0.0/java-trace-tool.tar.gz

 

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
]

Thread[ Thread-69 ] public org.apache.spark.sql.catalyst.plans.logical.LogicalPlan org.apache.spark.sql.catalyst.optimizer.SimplifyCasts$.apply(org.apache.spark.sql.catalyst.plans.logical.LogicalPlan) took 0 (ms) to execute. Returning DeserializeToObject createexternalrow(count(1)#12.toString, StructField(count(1),StringType,false)), obj#368: org.apache.spark.sql.Row
+- LocalRelation <empty>, [count(1)#12]*
