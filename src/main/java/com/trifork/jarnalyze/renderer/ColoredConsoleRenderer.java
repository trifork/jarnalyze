package com.trifork.jarnalyze.renderer;

import static com.trifork.jarnalyze.renderer.AnsiColors.*;

import java.io.OutputStream;
import java.io.PrintWriter;

public class ColoredConsoleRenderer extends ConsoleRenderer {
    public ColoredConsoleRenderer(OutputStream os) {
        super(os);
    }

    public ColoredConsoleRenderer(PrintWriter w) {
        super(w);
    }

    @Override
    public void errorText(String text) {
        w.println();
        w.print(ANSI_RED);
        w.print(text);
        w.print(ANSI_RESET);
    }

    @Override
    public void warningText(String text) {
        w.println();
        w.print(ANSI_YELLOW);
        w.print(text);
        w.print(ANSI_RESET);
    }

    @Override
    public void strong(String text) {
        w.print(ANSI_BLUE);
        super.strong(text);
        w.print(ANSI_RESET);
    }

}
