package com.trifork.jarnalyze.renderer;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.nio.charset.StandardCharsets;

public class HtmlRenderer implements Renderer {
    private static final String PREAMBLE;
    private static final String EPILOGUE;
    static {
        String[] split;
        try {
            String template = "";
            try (InputStream is = HtmlRenderer.class.getResourceAsStream("template.html"); Reader rd = new InputStreamReader(is, StandardCharsets.UTF_8)) {

                int count;
                char[] buf = new char[1024];
                while ((count = rd.read(buf)) > 0) {
                    String s = new String(buf, 0, count);
                    template += s;
                }
            }
            // String template = Files.readString(Paths.get(HtmlRenderer.class.getResource("template.html").toURI()),
            //     Charset.defaultCharset());

            split = template.split("_CONTENT_");
        } catch (IOException e) {
            e.printStackTrace();
            split = new String[] { "<html><body>", "</body></head>" };
        }

        PREAMBLE = split[0];
        EPILOGUE = split[1];
    }

    protected PrintWriter w;

    public HtmlRenderer(OutputStream os) {
        this(new PrintWriter(os));

        w.print(PREAMBLE);
    }

    public HtmlRenderer(PrintWriter w) {
        this.w = w;
    }

    @Override
    public void headline(String text) {
        w.print("<h2>");
        w.print(text);
        w.print("</h2>");
    }

    @Override
    public void strong(String text) {
        w.print("<span class='strong'>");
        w.print(text);
        w.print("</span>");
    }

    @Override
    public void plain(String text) {
        w.print(text);
    }

    @Override
    public void itemListStart() {
        w.print("<ul>");
    }

    @Override
    public void itemStart() {
        w.print("<li>");
    }

    @Override
    public void itemEnd() {
        w.print("</li>");
    }

    @Override
    public void itemListEnd() {
        w.print("</ul>");
    }

    @Override
    public void close() {
        w.print(EPILOGUE);
        w.close();
    }

}
