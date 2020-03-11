package com.trifork.jarnalyze.markup;

import com.trifork.jarnalyze.renderer.Renderer;

public class PlainText extends Text {
    PlainText(Object text) {
        super(text);
    }

    @Override
    public void visit(Renderer renderer) {
        renderer.plain(text.toString());
    }
}
