# Trace HotSwap Agent

### This is a java agent which attaches to a running jvm and can collect various Instrumentation info.

Name on fucntion getting called
The arguments being passed to the function
The thread name 
Name of function being returned and the time it took to finish


### Usage  jtrace [-d | -c | -p pid] [-t threadName] [ -g groupName] <className> <methodName

  -d  trace driver process
  
  -e  trace executor process 
  
  -i  pid to trace any other jvm pid 
  
  -t  threadName   Thread name to be traced.
  
  -g groupName The groupName will be name of a set of class and functions which will gather details for specific functionality. 
     For example we have a group named kafka which is a set of methods and function which will capture the required details of important kafka client functions. 
     
  className regex class name : A regex which matches all the class to traced
  
  regex method name: A regex matching all the method to be traced  of the class 
  



