package com.java.hcicursor;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.os.MessageQueue;
import android.os.SystemClock;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewDebug;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import android.util.Log;

class para{
    float R;
    float dis;
    para(float _R, float _dis){
        dis = _dis;
        R = _R;
    }
}

public class MoveLawActivity extends AppCompatActivity implements CursorMovementListener {

    private float scaleIndex = 2.0f;
    private float stX = 1800.0f;

    List<Float> Rs = new ArrayList<Float>(){{
       //add(50f);
       add(75f);
       //add(100f);
    }};

    List<Float> diss = new ArrayList<Float>(){{
        add(100f);
        add(200f);
        add(300f);
        add(400f);
        add(500f);
        add(600f);
        add(700f);
        add(800f);
        add(900f);
    }};

    private int repeatTime = 3;
    private float checkHeight;
    private List<para> Para= new ArrayList<>();
    private int paramPos = 0; //该使用paramPos个参数组合了
    private int validTime = 0;//在当前的参数组合下，已经获得了validTime次合法点击

    View src;
    TextView dst;
    View.OnTouchListener ViewTouchListener, ViewTouchListenerCursor, ViewTouchListenerHand;
    float dstX, dstY;
    private int state;

    int activeColor = Color.parseColor("#116688");
    int idleColor = Color.parseColor("#99bbff");
    int notactiveColor = Color.parseColor("#aaaaaa");

    int screenWidth,screenHeight;
    private long lastTime;
    List<ResultBean> results = new ArrayList<>();

    private void changeTouchListener(View.OnTouchListener listener){
        this.ViewTouchListener = listener;
        src.setOnTouchListener(listener);
    }

    private float getDis(float nx, float ny, float x, float y){
        return (float)(Math.sqrt((nx-x)*(nx-x)+(ny-y)*(ny-y)));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_move_law);
        dst = new TextView(this);
        dst.setEms(1);
        src = new View(this);
        RelativeLayout relativeLayout = findViewById(R.id.relativeLayout);
        relativeLayout.addView(dst);
        relativeLayout.addView(src);
        for(Float R: Rs){
            for(Float dis:diss){
                Para.add(new para(R, dis));
            }
        }
        Display display = getWindowManager().getDefaultDisplay();
        Point point = new Point();
        display.getSize(point);
        screenHeight = point.y;
        screenWidth = point.x;

        validTime = 0;
        paramPos = 0;

        modeInit();

        ViewTouchListenerCursor = new View.OnTouchListener() {
            private float startX,startY,imageX,imageY;
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {

                    case MotionEvent.ACTION_DOWN:
                        //按下
                        startX = event.getX();
                        startY = event.getY();
                        Log.d("touch",String.format("%f,%f",startX,startY));
                        imageX = v.getX();
                        imageY = v.getY();
                        Log.d("image",String.format("%f,%f",imageX,imageY));
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        //移动
                        //移动的距离
                        float moveX = event.getX() - startX;// event.getX() 移动的X距离
                        float moveY = event.getY() - startY;// event.getY() 移动的Y距离
                        //view的宽高
                        int viewWidth = v.getWidth();
                        int viewHeight = v.getHeight();

                        //X当超出屏幕,取最大值
                        if (imageX + moveX + viewWidth > screenWidth) {
                            //靠右
                            v.setX(screenWidth - viewWidth);
                        } else if (imageX + moveX <= 0) {
                            //靠右
                            v.setX(0);
                        } else {
                            //正常
                            v.setX(imageX + moveX);
                        }
                        //Y当超出屏幕,取最大值
                        if (imageY + moveY + viewHeight > screenHeight) {
                            //靠下
                            v.setY(screenHeight - viewHeight);
                        } else if (imageY + moveY <= 0) {
                            //靠上
                            v.setY(0);
                        } else {
                            //正常
                            v.setY(imageY + moveY);
                        }
                        Log.d("touch",String.format("%f,%f",event.getX(),event.getY()));
                        Log.d("image",String.format("%f,%f",v.getX(),v.getY()));
                        return true;
                    case MotionEvent.ACTION_UP:
                        //松手
                        float upX = v.getX();
                        float upY = v.getY();
                        //if(getDis(upX, upY, dstX, dstY) < checkR)modeChange(true);
                        //else modeChange(false);
                        modeChange(upX, upY);
                        return true;
                }

                return false;
            }
        };
        ViewTouchListenerHand = new View.OnTouchListener() {
            private float ivDownX,ivDownY;
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        //按下
                        ivDownX = event.getX();
                        ivDownY = event.getY();
                        Log.d("down",String.format("%f,%f",event.getX(),event.getY()));
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        //移动
                        //移动的距离
                        float moveX = event.getX() - ivDownX;// event.getX() 移动的X距离
                        float moveY = event.getY() - ivDownY;// event.getY() 移动的Y距离
                        //当前view= X,Y坐标
                        float viewX = v.getX();
                        float viewY = v.getY();
                        //view的宽高
                        int viewHeight = v.getWidth();
                        int viewWidth = v.getHeight();

                        //X当超出屏幕,取最大值
                        if (viewX + moveX + viewWidth > screenWidth) {
                            //靠右
                            v.setX(screenWidth - viewWidth);
                        } else if (viewX + moveX <= 0) {
                            //靠右
                            v.setX(0);
                        } else {
                            //正常
                            v.setX(viewX + moveX);
                        }
                        //Y当超出屏幕,取最大值
                        if (viewY + moveY + viewHeight > screenHeight) {
                            //靠下
                            v.setY(screenHeight - viewHeight);
                        } else if (viewY + moveY <= 0) {
                            //靠上
                            v.setY(0);
                        } else {
                            //正常
                            v.setY(viewY + moveY);
                        }
                        Log.d("move",String.format("%f,%f",event.getX(),event.getY()));
                        return true;

                    case MotionEvent.ACTION_UP:
                        //松手
                        float upX = v.getX();
                        float upY = v.getY();
                        //if(getDis(upX, upY, dstX, dstY) < checkR)modeChange(true);
                        //else modeChange(false);
                        modeChange(upX, upY);
                        Log.d("up",String.format("%f,%f",event.getX(),event.getY()));
                        return true;
                }
                return false;
            }
        };
        //dst.setOnTouchListener(ViewTouchListener);
        changeTouchListener(ViewTouchListenerHand);

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
                            Toast.makeText(MoveLawActivity.this, "single hand mode on", Toast.LENGTH_SHORT).show();
                            changeTouchListener(ViewTouchListenerCursor);
                            Intent intent = new Intent(MoveLawActivity.this, TouchPad.class);
                            startActivityForResult(intent,520);
                        }
                        break;
                }
                return true;
            }
        });
    }

    void modeInit(){
        float R = Para.get(paramPos).R;
        float dis = Para.get(paramPos).dis;
        RelativeLayout.LayoutParams pars = (RelativeLayout.LayoutParams) src.getLayoutParams();
        pars.width = pars.height = (((int) R) << 1);
        dst.setLayoutParams(pars);
        src.setLayoutParams(pars);
        //dst.setX(screenWidth * 0.5f - R);
        //dst.setY(screenHeight * 0.5f - 0.5f * dis - R);
        src.setX(dis);
        src.setY(stX);
        //dstX = screenWidth * 0.5f - R;
        //dstY = screenHeight * 0.5f - 0.5f * dis - R;
        //dst.setBackgroundColor(notactiveColor);
        src.setBackgroundColor(idleColor);
        //checkR = 0.4f * R;
        checkHeight = dis;
        Log.d("ad","checkR");
    }

    /*void posChange(int state){
        Log.d("ad", "posChange!");
        src.setBackgroundColor(activeColor);
        src.setBackgroundColor(activeColor);
        float R = Para.get(paramPos).R;
        float dis = Para.get(paramPos).dis;
        if(state == 2){
            dst.setX(screenWidth * 0.5f - R);
            dst.setY(screenHeight * 0.5f + 0.5f * dis + R);
            src.setX(screenWidth * 0.5f - R);
            src.setY(screenHeight * 0.5f - 0.5f * dis - R);
            dstX = screenWidth*0.5f - R;
            dstY = screenHeight*0.5f + 0.5f*dis + R;
        }else{
            dst.setX(screenWidth * 0.5f - R);
            dst.setY(screenHeight * 0.5f - 0.5f * dis - R);
            src.setX(screenWidth * 0.5f - R);
            src.setY(screenHeight * 0.5f + 0.5f * dis + R);
            dstX = screenWidth*0.5f - R;
            dstY = screenHeight * 0.5f - 0.5f * dis - R;
        }return;
    }*/

    void modeChange(float upX, float upY){
        Log.d("ad", "modeChange!");
        if(checkHeight+100 > upX && checkHeight-100 < upX) {
            validTime++;
            results.add(new ResultBean(Para.get(paramPos).R, Para.get(paramPos).dis, stX-upY, true));
            if (validTime == repeatTime) {
                validTime = 0;
                paramPos++;
                if (paramPos < Para.size()) modeInit();
                else {
                    paramPos = 0;
                    Intent intent = new Intent(MoveLawActivity.this, ResultActivity.class);
                    intent.putExtra("result", (Serializable) results);
                    startActivity(intent);
                    results.clear();
                }
            } else modeInit();
        }else modeInit();

        /*if(state == 0){
            if(!isCorrect){
                modeInit();
                Log.d("ad", "???");
                return;
            }
            state = 2;
            validTime = 0;
            lastTime = System.currentTimeMillis();
            posChange(state);
        }else{
            long delay = System.currentTimeMillis() - lastTime;
            results.add(new ResultBean(Para.get(paramPos).R,Para.get(paramPos).dis,delay,isCorrect));
            validTime++;
            if(validTime == repeatTime){
                validTime = 0;
                paramPos ++;
                state = 0;
                if(paramPos < Para.size())modeInit();
                else{
                    paramPos = 0;
                    Intent intent = new Intent(MoveLawActivity.this,ResultActivity.class);
                    intent.putExtra("result",(Serializable)results);
                    startActivity(intent);
                    results.clear();
                }
            }
            //Log.e("xtx-here",state.toString());
            if(state == 1)state = 2;
            else if(state == 2)state = 1;
            //Log.e("xtx-here",state.toString());
            posChange(state);
            lastTime = System.currentTimeMillis();
        }*/
    }

    public void clickAt(float x,float y){return;}
    public int dragDown(float x,float y){
        float left = src.getX(),top = src.getY();
        float right = left+src.getWidth(),bottom=top+src.getHeight();
        if(left<=x&&x<=right&&top<=y&&y<=bottom){
            final long startTime = SystemClock.uptimeMillis();
            final MotionEvent downEvent = MotionEvent.obtain(
                    startTime, SystemClock.uptimeMillis(), MotionEvent.ACTION_DOWN, x, y, 0);
            ViewTouchListener.onTouch(src,downEvent);
            downEvent.recycle();
            return 0;
        }return -1;
    }
    public void dragMove(float x,float y, int index){
        if(index == -1)return;
        float left = src.getX(), top = src.getY();
        float right = left + src.getWidth(), bottom = top + src.getHeight();
        final long startTime = SystemClock.uptimeMillis();
        final MotionEvent moveEvent = MotionEvent.obtain(startTime, SystemClock.uptimeMillis(), MotionEvent.ACTION_MOVE, x, y, 0);
        ViewTouchListener.onTouch(src, moveEvent);
        moveEvent.recycle();
    }
    public void dragUp(float x,float y, int index){
        if(index == -1)return;
        float left = src.getX(),top = src.getY();
        float right = left+src.getWidth(),bottom=top+src.getHeight();
        final long startTime = SystemClock.uptimeMillis();
        final MotionEvent upEvent = MotionEvent.obtain(startTime, SystemClock.uptimeMillis(), MotionEvent.ACTION_UP, x, y, 0);
        ViewTouchListener.onTouch(src,upEvent);
        upEvent.recycle();
    }
    public float getScaleIndex(float x, float y, boolean isDragging){return this.scaleIndex;}

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch(requestCode){
            case 520:
                Log.d("ad", "onActivityResult!");
                changeTouchListener(ViewTouchListenerHand);
                break;
            default:
                break;
        }
    }
}
