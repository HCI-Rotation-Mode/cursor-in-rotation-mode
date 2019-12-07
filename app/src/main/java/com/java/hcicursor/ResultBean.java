package com.java.hcicursor;
import java.io.Serializable;
public class ResultBean implements Serializable{
    /*
        Fitt's Law experiment
        单次点击的宽度 距离 用时 是否正确点击
     */
    private float width;
    private float distance;
    private float time;
    private boolean correct;

    public ResultBean(){}
    public ResultBean(float width,float distance,float time, boolean correct){
        this.width = width;
        this.distance = distance;
        this.time = time;
        this.correct = correct;

    }

    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public float getDistance() {
        return distance;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }

    public float getTime() {
        return time;
    }

    public void setTime(float time) {
        this.time = time;
    }
    public boolean getCorrect(){
        return correct;
    }

    public void setCorrect(boolean correct) {
        this.correct = correct;
    }
}
