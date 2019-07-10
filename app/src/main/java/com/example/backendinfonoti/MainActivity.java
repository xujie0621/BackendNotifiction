package com.example.backendinfonoti;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private Button button1;
    private Button button2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //注册组件
        button1=(Button)findViewById(R.id.button1);
        button2=(Button)findViewById(R.id.button2);
        //程序开始开启Service，禁用开始按钮
        startService(new Intent(MainActivity.this,TimeService.class));
        button1.setEnabled(false);
        //两个按钮的点击事件
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                startService(new Intent(MainActivity.this,TimeService.class));
                button1.setEnabled(false);
                button2.setEnabled(true);
            }
        });
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                stopService(new Intent(MainActivity.this,TimeService.class));
                button1.setEnabled(true);
                button2.setEnabled(false);
            }
        });

    }

}
