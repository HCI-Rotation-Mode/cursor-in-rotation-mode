package com.java.hcicursor;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.method.Touch;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements CursorMovementListener{

    private float scaleIndex = 2.0f;
    private float rCircle;
    private float RCircle;
    private int imViewNum = 2;

    imView[] imageView = new imView[imViewNum];
    View.OnTouchListener imageViewTouchListenerHand,imageViewTouchListenerCursor,touchListener;
    View navView;

    /*private void changeImage(){
        imageNumber[curIndex] = (imageNumber[curIndex]^1);
        imageView[curIndex].setImageBitmap(image[imageNumber[curIndex]]);
    }*/

    private float getDistance(float x, float y, float nx, float ny){
        float xDis = x-nx;
        float yDis = y-ny;
        return (float)(Math.sqrt(xDis*xDis+yDis*yDis));
    }

    private float scaleFunction(float distance){
        /*
        一些要考虑的问题:
            1、移动速度随着离组件的距离缩小而增加
            2、当离组件距离缩小到一定程度之后移动速度应当降低
            3、不能设计过于复杂的函数会影响性能和交互体验
            4、目前的实现只适合单组件，多组件的求解不太好设计
         */
        /*Log.d("ad", String.format("distance %f RCircle %f rCircle %f", distance, RCircle, rCircle));
        if(distance > rCircle)return scaleIndex*(2.0f-distance/RCircle);
        else return scaleIndex*(2.0f-rCircle/RCircle)*(0.5f+0.5f*distance/rCircle);*/
        return scaleIndex;
    }

    void setImageViewTouchListener(View.OnTouchListener touchListener){
        for (imView iv : imageView)iv.setOnTouchListener(touchListener);
        this.touchListener = touchListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View decorView = this.getWindow().getDecorView();
        int option = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE;
        decorView.setSystemUiVisibility(option);
        setContentView(R.layout.activity_main);
        CursorMovementManager.setCursorMovementListener(this);
        imageView[0] = findViewById(R.id.iv_onTouch);
        imageView[1] = findViewById(R.id.iv_onTouch2);
        navView = findViewById(R.id.nav_view);

        imageView[0].loadBitMap(R.drawable.poker, R.drawable.poker_clicked);
        imageView[1].loadBitMap(R.drawable.poker2, R.drawable.poker2_clicked);

        //获取屏幕宽度
        WindowManager wm = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        final float width = outMetrics.widthPixels;
        final float height = outMetrics.heightPixels;
        RCircle = Math.max(width, height);

        imageViewTouchListenerCursor = new View.OnTouchListener() {
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
                        if (imageX + moveX + viewWidth > width) {
                            //靠右
                            v.setX(width - viewWidth);
                        } else if (imageX + moveX <= 0) {
                            //靠右
                            v.setX(0);
                        } else {
                            //正常
                            v.setX(imageX + moveX);
                        }
                        //Y当超出屏幕,取最大值
                        if (imageY + moveY + viewHeight > height) {
                            //靠下
                            v.setY(height - viewHeight);
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
                        //按下时与松手时X值一致的话，就干点别的事情
                        if (Math.abs(imageX-upX)<10&&Math.abs(imageY-upY)<10) {
                            ((imView)v).changeImage();
                        }
                        return true;
                }

                return false;
            }
        };
        imageViewTouchListenerHand = new View.OnTouchListener() {
            private float ivDownX,ivDownY;
            private float downViewX,downViewY;
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {

                    case MotionEvent.ACTION_DOWN:
                        //按下
                        ivDownX = event.getX();
                        ivDownY = event.getY();
                        downViewX = v.getX();
                        downViewY = v.getY();
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
                        if (viewX + moveX + viewWidth > width) {
                            //靠右
                            v.setX(width - viewWidth);
                        } else if (viewX + moveX <= 0) {
                            //靠右
                            v.setX(0);
                        } else {
                            //正常
                            v.setX(viewX + moveX);
                        }
                        //Y当超出屏幕,取最大值
                        if (viewY + moveY + viewHeight > height) {
                            //靠下
                            v.setY(height - viewHeight);
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
                        //按下时与松手时X值一致的话，就干点别的事情
                        if (Math.abs(downViewX-upX)<10&&Math.abs(downViewY-upY)<10) {
                            ((imView)v).changeImage();
                        }
                        Log.d("up",String.format("%f,%f",event.getX(),event.getY()));
                        return true;
                }

                return false;
            }
        };
        setImageViewTouchListener(imageViewTouchListenerHand);
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
                            Toast.makeText(MainActivity.this, "single hand mode on", Toast.LENGTH_SHORT).show();
                            setImageViewTouchListener(imageViewTouchListenerCursor);
                            Intent intent = new Intent(MainActivity.this, TouchPad.class);
                            startActivityForResult(intent,1);
                        }
                        break;
                }
                return true;
            }
        });
    }
    @Override
    public void clickAt(float x,float y){
        for(int nowIndex = imViewNum-1; nowIndex >= 0; --nowIndex) {
            float left = imageView[nowIndex].getX(), top = imageView[nowIndex].getY();
            float right = left + imageView[nowIndex].getWidth(), bottom = top + imageView[nowIndex].getHeight();
            if (left <= x && x <= right && top <= y && y <= bottom) {
                final long downTime = SystemClock.uptimeMillis();
                final MotionEvent downEvent = MotionEvent.obtain(
                        downTime, downTime, MotionEvent.ACTION_DOWN, x, y, 0);
                final MotionEvent upEvent = MotionEvent.obtain(
                        downTime, SystemClock.uptimeMillis(), MotionEvent.ACTION_UP, x, y, 0);
                touchListener.onTouch(imageView[nowIndex], downEvent);
                touchListener.onTouch(imageView[nowIndex], upEvent);
                downEvent.recycle();
                upEvent.recycle();
            }
        }
    }

    @Override
    public int dragDown(float x, float y) {
        for(int nowIndex = imViewNum-1; nowIndex >= 0; --nowIndex){
            float left = imageView[nowIndex].getX(),top = imageView[nowIndex].getY();
            float right = left+imageView[nowIndex].getWidth(),bottom=top+imageView[nowIndex].getHeight();
            if(left<=x&&x<=right&&top<=y&&y<=bottom){
                final long startTime = SystemClock.uptimeMillis();
                final MotionEvent downEvent = MotionEvent.obtain(
                        startTime, SystemClock.uptimeMillis(), MotionEvent.ACTION_DOWN, x, y, 0);
                touchListener.onTouch(imageView[nowIndex],downEvent);
                downEvent.recycle();
                return nowIndex;
            }
        }return -1;
    }

    @Override
    public void dragMove(float x, float y, int index) {
        if(index == -1)return;
        float left = imageView[index].getX(), top = imageView[index].getY();
        float right = left + imageView[index].getWidth(), bottom = top + imageView[index].getHeight();
        final long startTime = SystemClock.uptimeMillis();
        final MotionEvent moveEvent = MotionEvent.obtain(startTime, SystemClock.uptimeMillis(), MotionEvent.ACTION_MOVE, x, y, 0);
        touchListener.onTouch(imageView[index], moveEvent);
        moveEvent.recycle();
    }
    @Override
    public void dragUp(float x, float y, int index) {
        if(index == -1)return;
        float left = imageView[index].getX(),top = imageView[index].getY();
        float right = left+imageView[index].getWidth(),bottom=top+imageView[index].getHeight();
        final long startTime = SystemClock.uptimeMillis();
        final MotionEvent upEvent = MotionEvent.obtain(startTime, SystemClock.uptimeMillis(), MotionEvent.ACTION_UP, x, y, 0);
        touchListener.onTouch(imageView[index],upEvent);
        upEvent.recycle();
    }

    @Override
    public float getScaleIndex(float x, float y, boolean isDragging){
        //待修改
        /*float centerX = imageView.getX()+imageView.getWidth()/2.0f;
        float centerY = imageView.getY()+imageView.getHeight()/2.0f;
        float distance = getDistance(x, y, centerX, centerY);
        if(!isDragging)return scaleFunction(distance);
        else return scaleIndex;*/
        return scaleIndex;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch(requestCode){
            case 1:
                Log.d("xtx", "onActivityResult!");
                setImageViewTouchListener(imageViewTouchListenerHand);
                break;
            default:
                break;
        }
    }

    /*@Override
    public void onWindowFocusChanged(boolean hasFocus) {
        //待修改
        super.onWindowFocusChanged(hasFocus);
        rCircle = Math.min(imageView[0].getWidth(), imageView[0].getHeight());
        Log.d("ad", String.format("rCircle %f", rCircle));
    }*/

}
