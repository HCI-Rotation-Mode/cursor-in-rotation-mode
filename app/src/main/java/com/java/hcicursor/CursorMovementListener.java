package com.java.hcicursor;

public interface CursorMovementListener {
    public void clickAt(float x,float y);
    public int dragDown(float x,float y);
    public void dragMove(float x,float y, int index);
    public void dragUp(float x,float y, int index);
    public float getScaleIndex(float x, float y, boolean isDragging);
}
