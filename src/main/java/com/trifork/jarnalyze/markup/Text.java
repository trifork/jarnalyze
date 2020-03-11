package com.trifork.jarnalyze.markup;

abstract class Text implements LayoutComponent {
    final Object text;

    Text(Object text) {
        this.text = text;
    }

}
