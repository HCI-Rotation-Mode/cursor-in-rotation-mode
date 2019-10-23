package com.java.hcicursor;

public interface CursorMovementListener {
    public void clickAt(float x,float y);
    public void dragDown(float x,float y);
    public void dragMove(float x,float y);
    public void dragUp(float x,float y);
}
