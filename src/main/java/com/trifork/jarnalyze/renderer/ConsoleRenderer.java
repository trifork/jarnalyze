package com.trifork.jarnalyze.renderer;

import java.io.OutputStream;
import java.io.PrintWriter;

public class ConsoleRenderer implements Renderer {

    private static final String SINGLE_INDENT = "    ";
    protected PrintWriter w;
    private int indentLevel;

    public ConsoleRenderer(OutputStream os) {
        this(new PrintWriter(os));
    }

    public ConsoleRenderer(PrintWriter w) {
        this.w = w;
    }

    @Override
    public void errorText(String text) {
        w.println();
        w.print(text);
    }

    @Override
    public void warningText(String text) {
        w.println();
        w.print(text);
    }

    @Override
    public void strong(String text) {
        w.print(text);
    }

    @Override
    public void plain(String text) {
        w.print(text);
    }

    @Override
    public void itemListStart() {
        indentLevel++;
    }

    @Override
    public void itemStart() {
        w.println();
        indent(indentLevel);
    }

    @Override
    public void itemEnd() {
    }

    @Override
    public void itemListEnd() {
        indentLevel--;
    }

    @Override
    public void close() {
        w.close();
    }

    private void indent(int count) {
        for (int i = 0; i < count; i++) {
            w.print(SINGLE_INDENT);
        }
    }

}
