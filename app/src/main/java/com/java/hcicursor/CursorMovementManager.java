package com.java.hcicursor;

import android.util.Log;

public class CursorMovementManager {
    private static CursorMovementListener cursorMovementListener;
    public static void setCursorMovementListener(CursorMovementListener cMM){
        cursorMovementListener = cMM;
    }
    public static void cursorClick(float x,float y){
        cursorMovementListener.clickAt(x,y);
        Log.d("xtx",String.format("cursor clickAt %f,%f",x,y));
    }
    public static void cursorDragDown(float x,float y){
        Log.d("xtx",String.format("cursor drag %f,%f----->",x,y));
        cursorMovementListener.dragDown(x,y);
    }
    public static void cursorDragUp(float x,float y){
        Log.d("xtx",String.format("cursor drag %f,%f",x,y));
        cursorMovementListener.dragUp(x,y);
    }
    public static void cursorDragMove(float x,float y){
        Log.d("xtx",String.format("cursor drag %f,%f<------",x,y));
        cursorMovementListener.dragMove(x,y);
    }
    public static float cursorAskScaleIndex(float x, float y, boolean isDragging){
        Log.d("xtx", String.format("cursor pos %f %f", x, y));
        return cursorMovementListener.getScaleIndex(x, y, isDragging);
    }
}
