package databricks.trace.advice;

import java.io.FileWriter;
import java.io.IOException;
import java.util.function.*;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.text.SimpleDateFormat;
import java.util.Date;
import net.bytebuddy.asm.Advice;
import java.util.Objects;

import net.bytebuddy.asm.Advice;

/*
public class MsTimeMeasurementAdvice {
  @Advice.OnMethodEnter
  public static long enter(
      @Advice.Origin String origin,
      @Advice.AllArguments Object[] args,
      @LogFileName String logFileName,
      @ThreadNames String threadName) {

    String tname = Thread.currentThread().getName();
    // Return if the thread name does not match
    if (threadName != null && !tname.contains(threadName)) return 0;

    String s =
        new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date())
            + " Thread[ "
            + tname
            + " ] "
            + origin
            + " called, args:  "
            + Arrays.toString(args);

    if (logFileName != null) {
      try {
        FileWriter fw = new FileWriter(logFileName, true);
        fw.write(s + "\n");
        fw.close();
      } catch (Exception e) {
        e.getStackTrace();
      }
    } else {

      System.out.println(s);
    }

    return System.currentTimeMillis();
  }

  @Advice.OnMethodExit(onThrowable = Throwable.class)
  public static void exit(
      @Advice.Enter long start,
      @Advice.Origin String origin,
      @LogFileName String logFileName,
      @ThreadNames String threadName) {
    String tname = Thread.currentThread().getName();
    if (threadName != null && !tname.contains(threadName)) return;
    long executionTime = System.currentTimeMillis() - start;
    String s =
        new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date())
            + " Thread[ "
            + tname
            + " ] "
            + origin
            + " took "
            + executionTime
            + " (ms) to execute";

    if (logFileName != null) {
      try {
        FileWriter fw = new FileWriter(logFileName, true);
        fw.write(s + "\n");
        fw.close();
      } catch (Exception e) {
        e.getStackTrace();
      }
    } else {
      System.out.println(s);
    }
  }
}
*/
public class MsTimeMeasurementAdvice {
  @Advice.OnMethodEnter
  public static long enter(
      @Advice.Origin String origin,
      @ThreadName String threadName,
      @Advice.AllArguments Object[] args) {
    String tname = Thread.currentThread().getName();
    if (threadName != null && !tname.contains(threadName)) return System.currentTimeMillis();
    System.err.println(
        "Thread[" + tname + " ] " + origin + " called with " + Arrays.toString(args));
    return System.currentTimeMillis();
  }

  @Advice.OnMethodExit(onThrowable = Throwable.class)
  public static void exit(
      @Advice.Enter long start,
      @ThreadName String threadName,
      @Advice.Origin String origin,
      @Advice.Return String retValue) {
    long executionTime = System.currentTimeMillis() - start;
    String tname = Thread.currentThread().getName();
    if (threadName == null || tname.contains(threadName))
      if (retValue == null)
        System.err.println(
            "Thread[ "
                + tname
                + " ] "
                + origin
                + " took "
                + executionTime
                + " (ms) to execute. Returning Nothing");
      else
        System.err.println(
            "Thread[ "
                + tname
                + " ] "
                + origin
                + " took "
                + executionTime
                + " (ms) to execute. Returning "
                + retValue);
  }
}
