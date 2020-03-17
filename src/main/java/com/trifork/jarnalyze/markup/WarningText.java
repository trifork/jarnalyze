package com.trifork.jarnalyze.markup;

import com.trifork.jarnalyze.renderer.Renderer;

public class WarningText extends Text {

    public WarningText(Object text) {
        super(text);
    }

    @Override
    public void visit(Renderer renderer) {
        renderer.warningText(text.toString());
    }
}
