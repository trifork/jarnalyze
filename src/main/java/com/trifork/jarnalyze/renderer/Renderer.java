package com.trifork.jarnalyze.renderer;

public interface Renderer {
    public void errorText(String text);

    public void warningText(String text);
    
    public void strong(String text);
    
    public void plain(String text);

    public void itemListStart();
    
    public void itemStart();
    
    public void itemEnd();
    
    public void itemListEnd();

    public void close();
    
}
