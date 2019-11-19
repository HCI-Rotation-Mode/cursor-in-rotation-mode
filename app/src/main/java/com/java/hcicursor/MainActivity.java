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

    private float scaleIndex = 2.5f;
    private float rCircle;

    ImageView imageView;
    View.OnTouchListener imageViewTouchListenerHand,imageViewTouchListenerCursor,touchListener;
    View navView;
    Bitmap image1,image2;
    int imageNumber;

    private float ivDownX,ivDownY;
    private float downViewX,downViewY;
    private float startX,startY,imageX,imageY;
    private void changeImage(){
        switch (imageNumber){
            case 1:
                imageNumber = 2;
                imageView.setImageBitmap(image2);
                break;
            case 2:
                imageNumber = 1;
                imageView.setImageBitmap(image1);
                break;
        }
    }

    private float getDistance(float x, float y, float nx, float ny){
        float x_dis = x-nx;
        float y_dis = y-ny;
        return (float)(Math.sqrt(x_dis*x_dis+y_dis*y_dis));
    }

    void setImageViewTouchListener(View.OnTouchListener touchListener){
        imageView.setOnTouchListener(touchListener);
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
        imageView = findViewById(R.id.iv_onTouch);
        navView = findViewById(R.id.nav_view);
        imageNumber = 1;
        image1 = BitmapFactory.decodeResource(getResources(),R.drawable.poker);
        image2 = BitmapFactory.decodeResource(getResources(),R.drawable.poker_clicked);
        imageView.setImageBitmap(image1);
        //获取屏幕宽度
        WindowManager wm = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        final float width = outMetrics.widthPixels;
        final float height = outMetrics.heightPixels;
        rCircle = Math.max(width, height);

        imageViewTouchListenerCursor = new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {

                    case MotionEvent.ACTION_DOWN:
                        //按下
                        startX = event.getX();
                        startY = event.getY();
                        Log.d("touch",String.format("%f,%f",startX,startY));
                        imageX = imageView.getX();
                        imageY = imageView.getY();
                        Log.d("image",String.format("%f,%f",imageX,imageY));
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        //移动
                        //移动的距离
                        float moveX = event.getX() - startX;// event.getX() 移动的X距离
                        float moveY = event.getY() - startY;// event.getY() 移动的Y距离
                        //view的宽高
                        int viewWidth = imageView.getWidth();
                        int viewHeight = imageView.getHeight();

                        //X当超出屏幕,取最大值
                        if (imageX + moveX + viewWidth > width) {
                            //靠右
                            imageView.setX(width - viewWidth);
                        } else if (imageX + moveX <= 0) {
                            //靠右
                            imageView.setX(0);
                        } else {
                            //正常
                            imageView.setX(imageX + moveX);
                        }
                        //Y当超出屏幕,取最大值
                        if (imageY + moveY + viewHeight > height) {
                            //靠下
                            imageView.setY(height - viewHeight);
                        } else if (imageY + moveY <= 0) {
                            //靠上
                            imageView.setY(0);
                        } else {
                            //正常
                            imageView.setY(imageY + moveY);
                        }
                        Log.d("touch",String.format("%f,%f",event.getX(),event.getY()));
                        Log.d("image",String.format("%f,%f",imageView.getX(),imageView.getY()));
                        return true;


                    case MotionEvent.ACTION_UP:
                        //松手
                        float upX = imageView.getX();
                        float upY = imageView.getY();
                        //按下时与松手时X值一致的话，就干点别的事情
                        if (Math.abs(imageX-upX)<10&&Math.abs(imageY-upY)<10) {
                            changeImage();
                        }
                        return true;
                }

                return false;
            }
        };
        imageViewTouchListenerHand = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {

                    case MotionEvent.ACTION_DOWN:
                        //按下
                        ivDownX = event.getX();
                        ivDownY = event.getY();
                        downViewX = imageView.getX();
                        downViewY = imageView.getY();
                        Log.d("down",String.format("%f,%f",event.getX(),event.getY()));
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        //移动
                        //移动的距离
                        float moveX = event.getX() - ivDownX;// event.getX() 移动的X距离
                        float moveY = event.getY() - ivDownY;// event.getY() 移动的Y距离
                        //当前view= X,Y坐标
                        float viewX = imageView.getX();
                        float viewY = imageView.getY();
                        //view的宽高
                        int viewHeight = imageView.getWidth();
                        int viewWidth = imageView.getHeight();

                        //X当超出屏幕,取最大值
                        if (viewX + moveX + viewWidth > width) {
                            //靠右
                            imageView.setX(width - viewWidth);
                        } else if (viewX + moveX <= 0) {
                            //靠右
                            imageView.setX(0);
                        } else {
                            //正常
                            imageView.setX(viewX + moveX);
                        }
                        //Y当超出屏幕,取最大值
                        if (viewY + moveY + viewHeight > height) {
                            //靠下
                            imageView.setY(height - viewHeight);
                        } else if (viewY + moveY <= 0) {
                            //靠上
                            imageView.setY(0);
                        } else {
                            //正常
                            imageView.setY(viewY + moveY);
                        }
                        Log.d("move",String.format("%f,%f",event.getX(),event.getY()));
                        return true;


                    case MotionEvent.ACTION_UP:
                        //松手
                        float upX = imageView.getX();
                        float upY = imageView.getY();
                        //按下时与松手时X值一致的话，就干点别的事情
                        if (Math.abs(downViewX-upX)<10&&Math.abs(downViewY-upY)<10) {
                            changeImage();
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
        float left = imageView.getX(),top = imageView.getY(),right = left+imageView.getWidth(),bottom=top+imageView.getHeight();
        if(left<=x&&x<=right&&top<=y&&y<=bottom){
            final long downTime = SystemClock.uptimeMillis();
            final MotionEvent downEvent = MotionEvent.obtain(
                    downTime, downTime, MotionEvent.ACTION_DOWN, x, y, 0);
            final MotionEvent upEvent = MotionEvent.obtain(
                    downTime, SystemClock.uptimeMillis(), MotionEvent.ACTION_UP, x, y, 0);
            touchListener.onTouch(imageView,downEvent);
            touchListener.onTouch(imageView,upEvent);
            downEvent.recycle();
            upEvent.recycle();
        }
    }

    @Override
    public void dragDown(float x, float y) {
        float left = imageView.getX(),top = imageView.getY(),right = left+imageView.getWidth(),bottom=top+imageView.getHeight();
        if(left<=x&&x<=right&&top<=y&&y<=bottom){
            final long startTime = SystemClock.uptimeMillis();
            final MotionEvent downEvent = MotionEvent.obtain(
                    startTime, SystemClock.uptimeMillis(), MotionEvent.ACTION_DOWN, x, y, 0);
            touchListener.onTouch(imageView,downEvent);
            downEvent.recycle();
        }
    }

    @Override
    public void dragMove(float x, float y) {
        float left = imageView.getX(), top = imageView.getY(), right = left + imageView.getWidth(), bottom = top + imageView.getHeight();
        final long startTime = SystemClock.uptimeMillis();
        final MotionEvent moveEvent = MotionEvent.obtain(startTime, SystemClock.uptimeMillis(), MotionEvent.ACTION_MOVE, x, y, 0);
        touchListener.onTouch(imageView, moveEvent);
        moveEvent.recycle();
    }
    @Override
    public void dragUp(float x, float y) {
        float left = imageView.getX(),top = imageView.getY(),right = left+imageView.getWidth(),bottom=top+imageView.getHeight();
        final long startTime = SystemClock.uptimeMillis();
        final MotionEvent upEvent = MotionEvent.obtain(startTime, SystemClock.uptimeMillis(), MotionEvent.ACTION_UP, x, y, 0);
        touchListener.onTouch(imageView,upEvent);
        upEvent.recycle();
    }

    @Override
    public float getScaleIndex(float x, float y, boolean isDragging){
        float centerX = imageView.getX()+imageView.getWidth()/2.0f;
        float centerY = imageView.getX()+imageView.getHeight()/2.0f;
        float distance = getDistance(x, y, centerX, centerY);
        if(isDragging)return scaleIndex * (1.0f + 1.5f*distance / rCircle);
        else return scaleIndex;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch(resultCode){
            case 1:
                setImageViewTouchListener(imageViewTouchListenerHand);
                break;
            default:
                break;
        }
    }
}
