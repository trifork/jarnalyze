package com.trifork.jarnalyze;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class Findings {
    static Logger log;

    static {
        setupLogging();
    }

    private static void setupLogging() {
        System.setProperty("java.util.logging.SimpleFormatter.format",
                "%5$s %6$s%n");

        SimpleFormatter fmt = new SimpleFormatter();

        final StandardOutConsoleHandler consoleHandler = new StandardOutConsoleHandler();
        consoleHandler.setLevel(Level.FINEST);
        consoleHandler.setFormatter(fmt);

        log = Logger.getLogger("findings");
        log.setUseParentHandlers(false);
        log.setLevel(Level.INFO);
        log.addHandler(consoleHandler);
    }
    
    public static void setVerbose(boolean verbose) {
        if (verbose) {
            log.setLevel(Level.FINE);
        } else {
            log.setLevel(Level.INFO);
        }
    }
}
