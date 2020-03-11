package com.trifork.jarnalyze.markup;

import java.util.ArrayList;

import com.trifork.jarnalyze.renderer.Renderer;

public class ItemList implements LayoutComponent {
    private Markup parent;
    ArrayList<Item> items = new ArrayList<>();
    
    ItemList(Markup parent) {
        this.parent = parent;
    }
    
    public Item item() {
        Item item = new Item(this, parent);
        items.add(item);
        return item;
    }
    
    @Override
    public void visit(Renderer renderer) {
        renderer.itemListStart();
        for (Item item: items) {
            renderer.itemStart();
            item.visit(renderer);
            renderer.itemEnd();
        }
        
        renderer.itemListEnd();
    }
}
