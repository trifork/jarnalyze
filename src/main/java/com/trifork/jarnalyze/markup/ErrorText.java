package com.trifork.jarnalyze.markup;

import com.trifork.jarnalyze.renderer.Renderer;

public class ErrorText extends Text {

    public ErrorText(Object text) {
        super(text);
    }

    @Override
    public void visit(Renderer renderer) {
        renderer.errorText(text.toString());
    }
}
