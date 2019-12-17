package com.java.hcicursor;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.bin.david.form.core.SmartTable;
import com.bin.david.form.data.CellInfo;
import com.bin.david.form.data.format.bg.BaseBackgroundFormat;
import com.bin.david.form.data.style.FontStyle;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import java.util.Date;
import java.text.SimpleDateFormat;


public class ResultActivity extends Activity {

    private SmartTable resultTableView;
    private List<ResultTableItem> resultTable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_result);
        Intent intent = getIntent();
        Serializable se = intent.getSerializableExtra("result");
        List<ResultBean> resultList = (List<ResultBean>)se;

        /*List<ResultBean> resultList = new ArrayList<>();
        resultList.add(new ResultBean(0.2f,0.5f,1011,true));
        resultList.add(new ResultBean(0.2f,0.5f,1011,false));
        resultList.add(new ResultBean(0.2f,0.5f,1011,false));*/
        resultTableView = findViewById(R.id.resultTable);
        resultTable = new ArrayList<>();

        StringBuilder sb = new StringBuilder(); // send email

        for(ResultBean resultBean :resultList){
            String t = "" + resultBean.getWidth() + " " + resultBean.getDistance() + " " + resultBean.getTime() + " " + resultBean.getCorrect() + "\n";
            sb.append(t);
            resultTable.add(new ResultTableItem(resultBean.getWidth(),resultBean.getDistance(),resultBean.getTime(),resultBean.getCorrect()?"":"MISS"));
        }

        final String emailText = sb.toString();
        //Log.e("xtx","HERE!!!");
        resultTableView.setData(resultTable);
        resultTableView.getConfig().setContentStyle(new FontStyle(50, Color.BLUE));

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
                    //resultTable.forEach();
                    DataExporter.send(df.format(new Date()), emailText);
                } catch (Exception e) {
                    Log.i("fail","send failed");
                    e.printStackTrace();
                }
            }
        }).start();

        Button returnButton = findViewById(R.id.returnButton);
        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}