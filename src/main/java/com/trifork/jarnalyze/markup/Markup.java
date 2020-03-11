package com.trifork.jarnalyze.markup;

import java.util.ArrayList;

import com.trifork.jarnalyze.renderer.Renderer;

public class Markup {
    private Markup parent;
    ArrayList<LayoutComponent> parts = new ArrayList<>();

    public Markup() {
        this(null);
    }

    public Markup(Markup parent) {
        this.parent = parent;
    }

    public Markup headline(String text) {
        parts.add(new Headline(text));
        return this;
    }

    public Markup strong(Object text) {
        parts.add(new StrongText(text));
        return this;
    }

    public Markup plain(String text) {
        parts.add(new PlainText(text));
        return this;
    }

    public ItemList itemList() {
        ItemList itemList = new ItemList(this);
        parts.add(itemList);
        return itemList;
    }

    public Markup end() {
        return parent;
    }

    public void visit(Renderer renderer) {
        for (LayoutComponent part: parts) {
            part.visit(renderer);
        }
    }

}
