package com.java.hcicursor;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.io.Serializable;
import java.sql.Array;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

enum STATE{
    STATE_IDLE, //还未开始实验
    STATE_UP,   //应当点击上方
    STATE_DOWN
}

class Parameters{
    float width;
    float distance;
    Parameters(float w,float d){
        width = w;
        distance = d;
    }
}

public class FittLawActivity extends AppCompatActivity implements CursorMovementListener,MagnetListener{


    private float scaleIndex = 2.0f;
    private float offset = 0.0f;

    List<Float> widths = new ArrayList<Float>(){{
        add(50f); //单位:px
        add(100f);
        add(200f);
    }};
    List<Float> distances = new ArrayList<Float>(){{
        add(300f); //单位:px
        add(600f);
        add(900f);
        //add(800f);
    }};

    int repeatTime = 10; //10次有效点击数

    List<Parameters> parameters= new ArrayList<>();
    int paramPos = 0; //该使用paramPos个参数组合了
    int validTime = 0;//在当前的参数组合下，已经获得了validTime次合法点击
    STATE state;

    View upBar,downBar;

    int activeColor = Color.parseColor("#116688");
    int idleColor = Color.parseColor("#99bbff");
    int notactiveColor = Color.parseColor("#aaaaaa");

    int screenWidth,screenHeight;
    long lastClickTime;
    List<ResultBean> results = new ArrayList<>();

    View.OnClickListener upBarListener,downBarListener;


    public Pair magnetTo(Pair cursorPos){
        //upBar.setY(0.5f*(screenHeight+parameters.get(paramPos).distance-parameters.get(paramPos).width)+offset);
        //downBar.setY(0.5f*(screenHeight-parameters.get(paramPos).distance-parameters.get(paramPos).width)+offset);
        float downU = downBar.getY()+downBar.getHeight()*0.5f+0.4f*parameters.get(paramPos).distance;
        float downD = downBar.getY()+downBar.getHeight()*0.5f-0.4f*parameters.get(paramPos).distance;
        float upU = upBar.getY()+upBar.getHeight()*0.5f+0.4f*parameters.get(paramPos).distance;
        float upD = upBar.getY()+upBar.getHeight()*0.5f-0.4f*parameters.get(paramPos).distance;
        //float downU = 0.5f*(screenHeight-parameters.get(paramPos).distance) + 0.4f * parameters.get(paramPos).distance + offset ;
        //float downD = 0.5f*(screenHeight-parameters.get(paramPos).distance) - 0.4f * parameters.get(paramPos).distance + offset ;
        //float upU = 0.5f*(screenHeight+parameters.get(paramPos).distance) + 0.4f * parameters.get(paramPos).distance + offset ;
        //float upD = 0.5f*(screenHeight+parameters.get(paramPos).distance) - 0.4f * parameters.get(paramPos).distance + offset ;
        if(downD <= cursorPos.y && cursorPos.y <= downU){
            cursorPos.y = downBar.getY()+downBar.getHeight()*0.5f;
            return cursorPos;
        }
        if(upD <= cursorPos.y && cursorPos.y <= upU){
            cursorPos.y = upBar.getY()+upBar.getHeight()*0.5f;
            return cursorPos;
        }
        return null;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MagnetManager.setMagnetListener(this);
        setContentView(R.layout.activity_fitt_law);
        upBar = new View(this);
        downBar = new View(this);
        RelativeLayout relativeLayout = findViewById(R.id.relativeLayout);
        relativeLayout.addView(upBar);
        relativeLayout.addView(downBar);
        for(Float w : widths) {
            for (Float d : distances) {
                parameters.add(new Parameters(w, d));
            }
        }
        Display display = getWindowManager().getDefaultDisplay();
        Point point = new Point();
        display.getSize(point);
        screenWidth = point.x;
        screenHeight = point.y;
        state = STATE.STATE_IDLE;
        refreshBars();
        upBarListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(state == STATE.STATE_UP||state == STATE.STATE_IDLE)
                    click(false);
                else
                    click(true);
            }
        };
        upBar.setOnClickListener(upBarListener);
        downBarListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(state == STATE.STATE_DOWN)
                    click(false);
                else
                    click(true);
            }
        };
        downBar.setOnClickListener(downBarListener);

        CursorMovementManager.setCursorMovementListener(this);
        View nav_view = findViewById(R.id.nav_view);
        nav_view.setOnTouchListener(new View.OnTouchListener() {
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
                            Toast.makeText(FittLawActivity.this, "single hand mode on", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(FittLawActivity.this, TouchPad.class);
                            startActivity(intent);
                        }
                        break;
                }
                return true;
            }
        });
    }

    void refreshBars(){
        RelativeLayout.LayoutParams pars = (RelativeLayout.LayoutParams) upBar.getLayoutParams();
        pars.width = screenWidth;
        pars.height = (int)parameters.get(paramPos).width;
        upBar.setLayoutParams(pars);
        pars = (RelativeLayout.LayoutParams) downBar.getLayoutParams();
        pars.width = screenWidth;
        pars.height = (int)parameters.get(paramPos).width;
        //pars.topMargin =
        downBar.setLayoutParams(pars);

        upBar.setX(0);
        downBar.setX(0);
        //offset = 0.5f*parameters.get(paramPos).distance-50;
        upBar.setY(0.5f*(screenHeight+parameters.get(paramPos).distance-parameters.get(paramPos).width)+offset);
        downBar.setY(0.5f*(screenHeight-parameters.get(paramPos).distance-parameters.get(paramPos).width)+offset);

        switch(state){
            case STATE_IDLE:
                upBar.setBackgroundColor(idleColor);
                downBar.setBackgroundColor(notactiveColor);
                break;
            case STATE_UP:
                upBar.setBackgroundColor(activeColor);
                downBar.setBackgroundColor(notactiveColor);
                break;
            case STATE_DOWN:
                upBar.setBackgroundColor(notactiveColor);
                downBar.setBackgroundColor(activeColor);
                break;
        }
    }

    private void click(boolean miss){
        //Log.e("xtx",state.toString());
        if(state == STATE.STATE_IDLE){
            if(miss)return;
            state = STATE.STATE_DOWN;
            validTime = 0;
            refreshBars();
            lastClickTime = System.currentTimeMillis();
        }else{
            long delay = System.currentTimeMillis() - lastClickTime;
            results.add(new ResultBean(parameters.get(paramPos).width,parameters.get(paramPos).distance,delay,!miss));
            validTime ++;
            if(validTime==repeatTime){
                validTime = 0;
                paramPos ++;
                state = STATE.STATE_IDLE;
            }
            if(paramPos >= parameters.size()){
                state = STATE.STATE_IDLE;
                paramPos = 0;
                validTime = 0;
                Intent intent = new Intent(FittLawActivity.this,ResultActivity.class);
                intent.putExtra("result",(Serializable)results);
                startActivity(intent);
                results.clear();
            }
            //Log.e("xtx-here",state.toString());
            if(state==STATE.STATE_UP)
                state = STATE.STATE_DOWN;
            else if(state == STATE.STATE_DOWN)
                    state = STATE.STATE_UP;
            //Log.e("xtx-here",state.toString());
            refreshBars();
            lastClickTime = System.currentTimeMillis();
        }
        //Log.e("xtx",state.toString());
    }
    @Override
    public boolean onTouchEvent (MotionEvent event) {
        if(event.getAction() == MotionEvent.ACTION_DOWN){
            click(true);
        }
        return true;
    }
    public void clickAt(float x,float y){

        float left = upBar.getX(), top = upBar.getY();
        float right = left + upBar.getWidth(), bottom = top + upBar.getHeight();
        if (left <= x && x <= right && top <= y && y <= bottom) {
            upBarListener.onClick(upBar);
            return;
        }

        left = downBar.getX(); top = downBar.getY();
        right = left + downBar.getWidth(); bottom = top + downBar.getHeight();
        if (left <= x && x <= right && top <= y && y <= bottom) {
            downBarListener.onClick(downBar);
            return;
        }
        click(true);
    }
    public int dragDown(float x,float y){
        return -1;
    }
    public void dragMove(float x,float y, int index){}
    public void dragUp(float x,float y, int index){}
    public float getScaleIndex(float x, float y, boolean isDragging){
        return this.scaleIndex;
    }
}
