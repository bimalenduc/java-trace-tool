package databricks.trace;

import databricks.trace.advice.*;
import net.bytebuddy.asm.Advice;

import net.bytebuddy.description.NamedElement;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.ElementMatchers;

import static net.bytebuddy.matcher.ElementMatchers.*;

import java.util.Map;

class AdviceWrapper {

  private final String actionId;

  private final String classMatcherExp;

  private final String methodMatcherExp;

  private final String actionArgs;
  private final TraceAgentArgs agentArgs;

  public AdviceWrapper(
      String actionId,
      String classMatcherExp,
      String methodMatcherExp,
      String actionArgs,
      TraceAgentArgs t) {
    this.actionId = actionId;
    this.classMatcherExp = classMatcherExp;
    this.methodMatcherExp = methodMatcherExp;
    this.actionArgs = actionArgs;
    this.agentArgs = t;
  }

  public AdviceWrapper(
      String actionId, String classMatcherExp, String methodMatcherExp, TraceAgentArgs t) {
    this(actionId, classMatcherExp, methodMatcherExp, null, t);
  }

  public String getAdviceName() {
    if (actionId.equals("log4jlevel")) return actionId;
    else return actionId;
  }

  public Advice getAdvice(DefaultArguments defaultArguments) {
    final Advice advice;
    if (actionId.equals("elapsed_time_in_ms")) {
      System.out.println("Installing " + actionId + " with " + agentArgs.getThreadNames());
      // Advice.to(MsTimeMeasurementAdvice.class);

      /*
      advice =
          Advice.withCustomMapping()
              .bind(LogFileName.class, agentArgs.getLogFileName())
              .bind(ThreadNames.class, agentArgs.getThreadNames())
              .to(MsTimeMeasurementAdvice.class);
          if(agentArgs.getThreadNames() != null)
         advice =
             Advice.withCustomMapping().bind(LogFileName.class, agentArgs.getLogFileName()).bind(ThreadNames.class, agentArgs.getThreadNames()).to(MsTimeMeasurementAdvice.class);
         else
         advice =
             Advice.withCustomMapping().bind(LogFileName.class, agentArgs.getLogFileName()).to(MsTimeMeasurementAdvice.class);
      advice = Advice.to(MsTimeMeasurementAdvice.class);
      */
      advice =
          Advice.withCustomMapping()
              .bind(ThreadName.class, agentArgs.getThreadNames())
              .to(MsTimeMeasurementAdvice.class);
    } else if (actionId.equals("stack_trace")) {
      advice = Advice.to(StackAdvice.class);
    } else if (actionId.equals("counter")) {
      advice = Advice.to(CounterAdvice.class);
    } else {
      System.err.println("TraceAgent detected an invalid action: " + actionId);
      advice = null;
    }
    return advice;
  }

  public <T extends NamedElement> ElementMatcher.Junction<T> getClassMatcher() {
    return toMatcher(classMatcherExp);
  }

  public <T extends NamedElement> ElementMatcher.Junction<T> getMethodMatcher() {
    // return any();
    return toMatcher(methodMatcherExp);
  }

  private <T extends NamedElement> ElementMatcher.Junction<T> toMatcher(String inputExpression) {
    final ElementMatcher.Junction<T> res;
    int i = inputExpression.indexOf('(');
    if (i == -1) {
      res = named(inputExpression);
    } else {
      String matchFn = inputExpression.substring(0, i);
      String pattern = inputExpression.substring(i + 1, inputExpression.length() - 1);
      if (matchFn.equals("REGEXP")) {
        res = nameMatches(pattern);
      } else {
        res = named(pattern);
      }
    }
    return res;
  }
}
