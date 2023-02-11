package databricks.trace;

import com.sun.tools.attach.VirtualMachine;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.HelpFormatter;

import java.io.IOException;
import java.io.File;
import java.util.Optional;

/** Agent Loader to attach to a PID. */
public class AgentLoader {

  public static void main(String[] args) {

    Options options = new Options();
    options.addOption("t", true, "thread name to be traced");
    options.addOption("u", true, "Classes to be traced");
    options.addOption("p", true, "pid of java process");
    options.addOption("a", true, "action file name");
    options.addOption("l", true, "log file name");
    HelpFormatter formatter = new HelpFormatter();

    CommandLine cmd = null;

    try {
      CommandLineParser parser = new DefaultParser();
      cmd = parser.parse(options, args);
    } catch (org.apache.commons.cli.ParseException e) {
      e.printStackTrace();
    }

    StringBuilder finalArgs = new StringBuilder();
    String logFileName = null;

    String pid = "";

    if (cmd.hasOption("t")) {
      String s = cmd.getOptionValue("t");
      finalArgs.append("threadName:" + s + ",");
    }
    if (cmd.hasOption("l")) {
      logFileName = cmd.getOptionValue("l");
      finalArgs.append("logFileName:" + logFileName + ",");
    }
    if (cmd.hasOption("u")) {
      finalArgs.append("classInfo:").append(cmd.getOptionValue("u") + ",");
    } else if (cmd.hasOption("a")) {
      finalArgs.append("actionsFile:").append(cmd.getOptionValue("a") + ",");
    } else {
      formatter.printHelp("trace", options);
    }

    if (finalArgs.charAt(finalArgs.length() - 1) == ',')
      finalArgs.setLength(finalArgs.length() - 1);

    if (cmd.hasOption("p")) {
      pid = cmd.getOptionValue("p");
    } else {
      formatter.printHelp("trace", options);
      System.exit(1);
    }

    File agentFile =
        (args[0].contains("=actionsFile"))
            ? new File(args[0])
            : new File(args[0] + "=" + finalArgs.toString());

    try {
      File traceFile = new File("/tmp/trace-output.txt");
      traceFile.createNewFile();
    } catch (IOException e) {
      e.printStackTrace();
    }

    // Load agent
    String agentFileName = agentFile.getAbsolutePath();
    System.out.println(agentFileName);
    try {
      System.out.println("Attaching to target JVM with PID: " + pid);
      VirtualMachine jvm = VirtualMachine.attach(pid);
      jvm.loadAgent(agentFileName);
      jvm.detach();
      System.out.println("Succefully attached to target JVM and loaded Java agent");
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
