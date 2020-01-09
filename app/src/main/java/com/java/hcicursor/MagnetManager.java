package com.java.hcicursor;

public class MagnetManager {
    private static MagnetListener magnetListener;
    public static void setMagnetListener(MagnetListener magnetListener1){
        magnetListener = magnetListener1;
    }
    public static Pair magnetTo(Pair pos){
        return magnetListener.magnetTo(pos);
    }
}
