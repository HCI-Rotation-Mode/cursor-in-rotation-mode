package com.java.hcicursor;

public interface CursorMovementListener {
    public void clickAt(float x,float y);
    public boolean dragDown(float x,float y);
    public void dragMove(float x,float y);
    public void dragUp(float x,float y);
    public float getScaleIndex(float x, float y, boolean isDragging);
}
