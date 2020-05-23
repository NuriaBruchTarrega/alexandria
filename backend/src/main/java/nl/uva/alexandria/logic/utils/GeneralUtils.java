package nl.uva.alexandria.logic.utils;

import java.io.PrintWriter;
import java.io.StringWriter;

public final class GeneralUtils {

    private GeneralUtils() {
    }

    public static String stackTraceToString(Exception e) {
        StringWriter stringWriter = new StringWriter();
        e.printStackTrace(new PrintWriter(stringWriter));
        return stringWriter.toString();
    }
}
