package databricks.trace;

import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class TraceAgentArgs implements DefaultArguments {

  private static final String EXTERNAL_ACTION_FILE_PATH = "actionsFile";
  private static final String DATE_TIME_FORMAT = "dateTimeFormat";
  private static final String LOG_FILE_NAME = "logFileName";
  private static final String THREAD_NAMES = "threadName";
  private static final String CLASS_NAMES = "classInfo";

  private final String externalActionFilePath;

  private final DateTimeFormatter dateTimeFormatter;
  private final String logFileName;
  private final String classNames;
  private final String threadNames;

  private final Boolean isDateLoggedFlag;

  public TraceAgentArgs(String arguments) {
    Map<String, String> parsedArgs =
        ArgUtils.parseOptionalArgs(
            Arrays.asList(
                EXTERNAL_ACTION_FILE_PATH,
                DATE_TIME_FORMAT,
                CommonActionArgs.IS_DATE_LOGGED,
                LOG_FILE_NAME,
                THREAD_NAMES,
                CLASS_NAMES),
            arguments);
    this.externalActionFilePath = parsedArgs.get(EXTERNAL_ACTION_FILE_PATH);
    System.out.println("Using " + this.externalActionFilePath);
    final String dateTimeFormatStr = parsedArgs.get(DATE_TIME_FORMAT);
    if (dateTimeFormatStr == null) {
      dateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    } else {
      dateTimeFormatter = DateTimeFormatter.ofPattern(dateTimeFormatStr);
    }
    // parse the common arguments
    this.isDateLoggedFlag = Boolean.valueOf(parsedArgs.get(CommonActionArgs.IS_DATE_LOGGED));
    this.logFileName = parsedArgs.get(LOG_FILE_NAME);
    String tn = parsedArgs.get(THREAD_NAMES);
    this.threadNames = tn;
    this.classNames = parsedArgs.get(CLASS_NAMES);
  }

  private List<String> toList(String threadnames) {

    List<String> items = Arrays.asList(threadnames.split("\\s*,\\s*"));
    return items;
  }

  public String getExternalActionFilePath() {
    return this.externalActionFilePath;
  }

  public DateTimeFormatter getDateTimeFormatter() {
    return this.dateTimeFormatter;
  }

  public boolean isDateLogged() {
    return this.isDateLoggedFlag;
  }

  public String getLogFileName() {
    return this.logFileName != null ? this.logFileName : "/tmp/trace-out.txt";
  }

  public String getThreadNames() {
    return this.threadNames;
  }

  public String getClassNames() {
    return this.classNames;
  }
}
