package databricks.trace.advice;

import databricks.trace.ArgUtils;
import databricks.trace.ArgumentsCollection;
import databricks.trace.CommonActionArgs;
import databricks.trace.DefaultArguments;

import net.bytebuddy.asm.Advice;

import java.util.function.*;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.log4j.LogManager;
import org.apache.log4j.Level;

public class ChangeLogLevel {

  private static String LOG_THRESHOLD_NANO = "log_threshold_nano";

  private static List<String> KNOWN_ARGS =
      Arrays.asList(CommonActionArgs.IS_DATE_LOGGED, LOG_THRESHOLD_NANO);

  private CommonActionArgs commonActionArgs;

  private static long logThresholdNano;

  public ChangeLogLevel(String actionArgs, DefaultArguments defaults) {
    ArgumentsCollection parsed = ArgUtils.parseOptionalArgs(KNOWN_ARGS, actionArgs);
    this.commonActionArgs = new CommonActionArgs(parsed, defaults);
    this.logThresholdNano = parsed.parseLong(LOG_THRESHOLD_NANO, 0);
  }

  @Advice.OnMethodEnter
  public static void enter() {
    LogManager.getRootLogger().setLevel(Level.DEBUG);
  }

  @Advice.OnMethodExit(onThrowable = Throwable.class)
  public static void exit(@Advice.Enter long start, @Advice.Origin String origin) {}
}
