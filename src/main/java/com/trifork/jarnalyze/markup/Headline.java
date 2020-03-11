package com.trifork.jarnalyze.markup;

import com.trifork.jarnalyze.renderer.Renderer;

public class Headline extends Text {

    public Headline(Object text) {
        super(text);
    }

    @Override
    public void visit(Renderer renderer) {
        renderer.headline(text.toString());
    }
}
