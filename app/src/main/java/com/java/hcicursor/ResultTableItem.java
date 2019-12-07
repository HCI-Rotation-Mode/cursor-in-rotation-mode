package com.java.hcicursor;

import com.bin.david.form.annotation.SmartColumn;
import com.bin.david.form.annotation.SmartTable;

@SmartTable(name="Fitt's law 实验结果")
public class ResultTableItem {
    public ResultTableItem(float width,float distance,float time,String msg){
        this.width = width;
        this.distance = distance;
        this.time = time;
        this.msg = msg;
    }
    @SmartColumn(id=0,name="宽度(px)")
    private float width;
    @SmartColumn(id=1,name="距离(px)")
    private float distance;
    @SmartColumn(id=2,name="用时(ms)")
    private float time;
    @SmartColumn(id=3,name="命中")
    private String msg;

}
