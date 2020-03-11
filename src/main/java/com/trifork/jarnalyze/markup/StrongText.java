package com.trifork.jarnalyze.markup;

import com.trifork.jarnalyze.renderer.Renderer;

public class StrongText extends Text {

    public StrongText(Object text) {
        super(text);
    }

    @Override
    public void visit(Renderer renderer) {
        renderer.strong(text.toString());
    }

}
