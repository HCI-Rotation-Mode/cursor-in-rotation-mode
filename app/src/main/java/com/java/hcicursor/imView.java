package com.java.hcicursor;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.AttributeSet;
import androidx.appcompat.widget.AppCompatImageView;


public class imView extends AppCompatImageView {
    private Bitmap[] image = new Bitmap[2];
    private int curIndex = 0;
    public imView(Context context) {
        super(context);
    }

    public imView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public imView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void loadBitMap(int initId, int initClickId){
        image[0] = BitmapFactory.decodeResource(getResources(), initId);
        image[1] = BitmapFactory.decodeResource(getResources(), initClickId);
        this.setImageBitmap(image[0]);
    }

    public void changeImage(){
        curIndex = (curIndex^1);
        this.setImageBitmap(image[curIndex]);
    }
}
