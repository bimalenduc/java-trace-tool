package databricks.trace;

import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.asm.Advice;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.lang.instrument.Instrumentation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.log4j.LogManager;
import org.apache.log4j.Level;

/** Read action file and based on the matcher install actions */
public class TraceAgent {

  private TraceAgentArgs traceAgentArgs;

  private Instrumentation instrumentation;

  // Read the action lines and create an Advice object for each

  private AdviceWrapper readAction(String line) {
    String[] actionWithArgs = line.split("\\s+");
    final AdviceWrapper traceAction;
    if (actionWithArgs.length == 4) {
      traceAction =
          new AdviceWrapper(
              actionWithArgs[0],
              actionWithArgs[1],
              actionWithArgs[2],
              actionWithArgs[3],
              traceAgentArgs);
    } else if (actionWithArgs.length == 3) {
      traceAction =
          new AdviceWrapper(
              actionWithArgs[0], actionWithArgs[1], actionWithArgs[2], traceAgentArgs);
    } else {
      traceAction = null;
    }
    return traceAction;
  }

  private static boolean isBlank(String line) {
    char[] chars = line.toCharArray();
    for (char c : chars) {
      if (c != ' ' && c != '\t') {
        return false;
      }
    }
    return true;
  }

  private static boolean isComment(String line) {
    return line.charAt(0) == '#';
  }

  private List<AdviceWrapper> readActionsFormArgs() {

    List<AdviceWrapper> actions = new ArrayList<AdviceWrapper>();
    String classInfo = traceAgentArgs.getClassNames();
    String[] classList = classInfo.split("!");
    for (String s : classList) {
      String[] mtl = s.split("@@");
      for (String t : mtl[1].split("@")) {
        actions.add(new AdviceWrapper("elapsed_time_in_ms", mtl[0], t, traceAgentArgs));
      }
    }
    return actions;
  }

  private List<AdviceWrapper> readActions(InputStream in) {
    List<AdviceWrapper> actions = new ArrayList<AdviceWrapper>();
    try {
      try (BufferedReader buffReader = new BufferedReader(new InputStreamReader(in))) {
        String line = null;
        while ((line = buffReader.readLine()) != null) {
          // blank lines and comments are skipped
          if (!isBlank(line) && !isComment(line)) {
            AdviceWrapper traceAction = readAction(line);
            if (traceAction == null) {
              System.err.println("TraceAgent skips the rule: " + line);
            } else {
              actions.add(traceAction);
            }
          }
        }
      }
    } catch (IOException e) {
      e.printStackTrace(System.err);
    }
    return actions;
  }

  // Install all the Advice in the List
  private void installActions(List<AdviceWrapper> actions) {

    for (AdviceWrapper action : actions) {

      if (action.getAdviceName().equals("log4jlevel")) {
        LogManager.getRootLogger().setLevel(Level.DEBUG);

      } else {
        final Advice advice = action.getAdvice(traceAgentArgs);
        if (advice != null) {

          System.err.println(
              "Installing "
                  + ":"
                  + action.getAdviceName()
                  + ":"
                  + action.getClassMatcher()
                  + ":"
                  + action.getMethodMatcher());
          new AgentBuilder.Default()
              .disableClassFormatChanges()
              .with(AgentBuilder.InitializationStrategy.NoOp.INSTANCE) //
              .with(AgentBuilder.RedefinitionStrategy.RETRANSFORMATION)
              .with(AgentBuilder.TypeStrategy.Default.REDEFINE) //
              .type(action.getClassMatcher())
              .transform(
                  (builder, type, classLoader, module) -> {
                    return builder.visit(advice.on(action.getMethodMatcher()));
                  })
              .installOn(instrumentation);
        }
      }
    }
  }

  private void install() {

    String externalActionFile = traceAgentArgs.getExternalActionFilePath();
    if (externalActionFile == null) {
      installActions(readActionsFormArgs());
    } else {
      try {
        List<AdviceWrapper> allactions = new ArrayList();
        allactions.addAll(readActions(new FileInputStream(externalActionFile)));
        installActions(allactions);
      } catch (FileNotFoundException fnf) {
        System.err.println(
            "TraceAgent does not find the external action file: " + externalActionFile);
      }
    }
  }

  private TraceAgent(TraceAgentArgs traceAgentArgs, Instrumentation instrumentation) {
    this.traceAgentArgs = traceAgentArgs;
    this.instrumentation = instrumentation;
  }

  public static void premain(String arguments, Instrumentation instrumentation) {
    System.out.println("DEBUG " + arguments);
    TraceAgentArgs traceAgentArgs = new TraceAgentArgs(arguments);
    TraceAgent traceAgent = new TraceAgent(traceAgentArgs, instrumentation);
    System.err.println("Calling install main");
    traceAgent.install();
  }

  public static void agentmain(String arguments, Instrumentation instrumentation) {
    premain(arguments, instrumentation);
  }
}
