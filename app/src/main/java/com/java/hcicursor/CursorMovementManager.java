package com.java.hcicursor;

import android.util.Log;
import android.view.View;

public class CursorMovementManager {
    private static CursorMovementListener cursorMovementListener;
    private static int draggingIndex = -1;
    public static void setCursorMovementListener(CursorMovementListener cMM){
        cursorMovementListener = cMM;
    }
    public static void cursorClick(float x,float y){
        cursorMovementListener.clickAt(x,y);
        Log.d("xtx",String.format("cursor clickAt %f,%f",x,y));
    }
    public static void cursorDragDown(float x,float y){
        draggingIndex = cursorMovementListener.dragDown(x, y);
        if(draggingIndex != -1)Log.d("xtx",String.format("cursor drag %f,%f----->",x,y));
    }
    public static void cursorDragUp(float x,float y){
        if(draggingIndex != -1)Log.d("xtx",String.format("cursor drag %f,%f",x,y));
        cursorMovementListener.dragUp(x, y, draggingIndex);
        draggingIndex = -1;
    }
    public static void cursorDragMove(float x,float y){
        if(draggingIndex != -1)Log.d("xtx",String.format("cursor drag %f,%f<------",x,y));
        cursorMovementListener.dragMove(x, y, draggingIndex);

    }
    public static float cursorAskScaleIndex(float x, float y, boolean isDragging){
        float ans = cursorMovementListener.getScaleIndex(x, y, isDragging);
        Log.d("ad", String.format("cursor pos %f %f speed %f", x, y, ans));
        return ans;
    }
}
