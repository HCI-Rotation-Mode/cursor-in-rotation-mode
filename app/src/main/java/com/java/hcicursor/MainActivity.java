package com.java.hcicursor;

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

    ImageView imageView;
    View.OnTouchListener imageViewTouchListener;
    View navView;
    private float ivDownX,ivDownY;
    private float downViewX,downViewY;
    Bitmap image1,image2;
    int imageNumber;

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
        image1 = BitmapFactory.decodeResource(getResources(),R.drawable.yao);
        image2 = BitmapFactory.decodeResource(getResources(),R.drawable.zhang);
        imageView.setImageBitmap(image1);
        //获取屏幕宽度
        WindowManager wm = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        final int width = outMetrics.widthPixels;
        final int height = outMetrics.heightPixels;

        imageViewTouchListener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.e("xtx","TOUCH!!!!");
                switch (event.getAction()) {

                    case MotionEvent.ACTION_DOWN:
                        //按下
                        ivDownX = event.getX();
                        ivDownY = event.getY();
                        downViewX = imageView.getX();
                        downViewY = imageView.getY();
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
                        return true;


                    case MotionEvent.ACTION_UP:
                        //松手
                        float upX = imageView.getX();
                        float upY = imageView.getY();
                        //按下时与松手时X值一致的话，就干点别的事情
                        if (Math.abs(downViewX-upX)<10&&Math.abs(downViewY-upY)<10) {
                            changeImage();
                        }
                        return true;
                }

                return false;
            }
        };
        imageView.setOnTouchListener(imageViewTouchListener);
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
                        if(Math.abs(startX-nowX)>10)
                            Toast.makeText(MainActivity.this,"single hand mode on",Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(MainActivity.this, TouchPad.class));
                        break;
                }
                return true;
            }
        });
    }
    @Override
    public void clickAt(float x,float y){
        float left = imageView.getLeft(),top = imageView.getTop(),right = imageView.getRight(),bottom=imageView.getBottom();
        if(left<=x&&x<=right&&top<=y&&y<=bottom){
            Log.e("xtx","click at"+x+" "+y);
            final long downTime = SystemClock.uptimeMillis();
            final MotionEvent downEvent = MotionEvent.obtain(
                    downTime, downTime, MotionEvent.ACTION_DOWN, x, y, 0);
            final MotionEvent upEvent = MotionEvent.obtain(
                    downTime, SystemClock.uptimeMillis(), MotionEvent.ACTION_UP, x, y, 0);
            imageViewTouchListener.onTouch(imageView,downEvent);
            imageViewTouchListener.onTouch(imageView,upEvent);
            downEvent.recycle();
            upEvent.recycle();
        }
    }
}
