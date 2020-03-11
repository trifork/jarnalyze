package com.trifork.jarnalyze.markup;

import com.trifork.jarnalyze.renderer.Renderer;

public interface LayoutComponent {

    void visit(Renderer renderer);

}
