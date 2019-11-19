package com.java.hcicursor;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;
import android.util.Log;

public class TouchPad extends Activity{

    View navView,touchView;
    ImageView cursor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View decorView = this.getWindow().getDecorView();
        int option = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE;
        decorView.setSystemUiVisibility(option);
        setContentView(R.layout.activity_touch_pad);

        cursor = findViewById(R.id.cursor);
        navView = findViewById(R.id.nav_view);

        navView.setOnTouchListener(new View.OnTouchListener() {
            private float startX,startY;
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch(motionEvent.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        startX = motionEvent.getX();
                        startY = motionEvent.getY();
                        break;
                    case MotionEvent.ACTION_UP:
                        float nowX = motionEvent.getX();
                        float nowY = motionEvent.getY();
                        if(Math.abs(startX-nowX)>20) {
                            Toast.makeText(TouchPad.this, "single hand mode off", Toast.LENGTH_SHORT).show();
                            finish();
                            break;
                        }
                }
                return true;
            }
        });
        //获取屏幕宽度
        WindowManager wm = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        final int width = outMetrics.widthPixels;
        final int height = outMetrics.heightPixels;

        touchView = findViewById(R.id.touch_view);
        touchView.setOnTouchListener(new View.OnTouchListener(){
            private float startX,startY,cursorX,cursorY;
            private boolean dragReady=false,isDraging = false;
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch(motionEvent.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        startX = motionEvent.getX();
                        startY = motionEvent.getY();
                        cursorX = cursor.getX();
                        cursorY = cursor.getY();
                        if(dragReady) {
                            isDraging = true;
                            dragReady = false;
                            CursorMovementManager.cursorDragDown(cursor.getX()+cursor.getWidth(), cursor.getY());
                        }
                        break;
                    case MotionEvent.ACTION_MOVE:
                        float moveX = motionEvent.getX() - startX;// event.getX() 移动的X距离
                        float moveY = motionEvent.getY() - startY;// event.getY() 移动的Y距离
                        float nowScaleIndex = CursorMovementManager.cursorAskScaleIndex(cursor.getX(), cursor.getY(), isDraging);
                        moveX *= nowScaleIndex;
                        moveY *= nowScaleIndex;

                        int viewWidth = cursor.getWidth();
                        int viewHeight = cursor.getHeight();

                        //X当超出屏幕,取最大值
                        if (cursorX + moveX + viewWidth > width) {
                            //靠右
                            cursor.setX(width - viewWidth);
                        } else if (cursorY + moveX <= 0) {
                            //靠右
                            cursor.setX(0);
                        } else {
                            //正常
                            cursor.setX(cursorX + moveX);
                        }
                        //Y当超出屏幕,取最大值
                        if (cursorY + moveY + viewHeight > height) {
                            //靠下
                            cursor.setY(height - viewHeight);
                        } else if (cursorY + moveY <= 0) {
                            //靠上
                            cursor.setY(0);
                        } else {
                            //正常
                            cursor.setY(cursorY + moveY);
                        }
                        if(isDraging)
                            CursorMovementManager.cursorDragMove(cursor.getX()+cursor.getWidth(),cursor.getY());
                        break;
                    case MotionEvent.ACTION_UP:
                        float upX = cursor.getX();
                        float upY = cursor.getY();
                        if (Math.abs(cursorX-upX)<10&&Math.abs(cursorY-upY)<10) {
                            if(isDraging){
                                isDraging = false;
                                CursorMovementManager.cursorDragUp(cursor.getX()+cursor.getWidth(),cursor.getY());
                            }else{
                                dragReady = true;
                                Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        if(dragReady){
                                            dragReady = false;
                                            CursorMovementManager.cursorClick(cursorX+cursor.getWidth(), cursorY);
                                        }
                                    }
                                }, 200);
                            }
                        }else{
                            isDraging = false;
                            dragReady = false;
                        }
                        break;
                }
                return true;
            }
        });
    }

}
