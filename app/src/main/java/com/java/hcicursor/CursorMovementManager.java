package com.java.hcicursor;

public class CursorMovementManager {
    private static CursorMovementListener cursorMovementListener;
    public static void setCursorMovementListener(CursorMovementListener cMM){
        cursorMovementListener = cMM;
    }
    public static void cursorClick(float x,float y){
        cursorMovementListener.clickAt(x,y);
    }
}
