package com.trifork.jarnalyze.markup;

public class Item extends Markup {
    private ItemList itemList;

    Item(ItemList itemList, Markup parent) {
        super(parent);
        this.itemList = itemList;
    }
    
    public Item item() {
        return itemList.item();
    }

    public Item errorText(String text) {
        super.errorText(text);
        return this;
    }
    
    public Item strong(Object text) {
        super.strong(text);
        return this;
    }

    public Item plain(String text) {
        super.plain(text);
        return this;
    }

}
