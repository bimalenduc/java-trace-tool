package databricks.trace.advice;

import net.bytebuddy.asm.Advice;
import java.util.Arrays;

public class StackAdvice {

  @Advice.OnMethodEnter
  public static long enter() {
    Exception e = new Exception("TraceAgent (stack trace)");
    StackTraceElement[] stElements = Thread.currentThread().getStackTrace();
    e.setStackTrace(Arrays.copyOfRange(stElements, 2, stElements.length));
    e.printStackTrace(System.out);

    return System.currentTimeMillis();
  }

  @Advice.OnMethodExit(onThrowable = Throwable.class)
  public static void exit(@Advice.Enter long start, @Advice.Origin String origin) {}
}
