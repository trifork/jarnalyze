package com.trifork.jarnalyze;

import java.io.OutputStream;
import java.util.logging.ConsoleHandler;

public class StandardOutConsoleHandler extends ConsoleHandler {
    protected void setOutputStream(OutputStream out) throws SecurityException {
        super.setOutputStream(System.out);
    }
}
