package org.robocup.common.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class LogFormatter extends Formatter {
    //
    // Create a DateFormat to format the logger timestamp.
    //
    private static final DateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
    public static String newline = System.getProperty("line.separator");
    
    public String format(LogRecord record) {
	        StringBuilder builder = new StringBuilder(1000);
	        builder.append(df.format(new Date(record.getMillis()))).append(" - ");
	        builder.append(formatMessage(record));
	        builder.append(newline);
	        return builder.toString();
    }
}