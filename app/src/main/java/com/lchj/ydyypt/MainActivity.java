package com.lchj.ydyypt;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.qihoo360.replugin.RePlugin;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void OnClick(View v) {
//        Intent intent = new Intent();
//        intent.setAction("com.qihoo360.repluginapp.replugin.receiver.ACTION3");
//        intent.putExtra("name", "jerry");
//        sendBroadcast(intent);

        // 方法1（最“单品”）
        Intent intent = new Intent();
        intent.setComponent(new ComponentName("demo2",
                "com.qihoo360.replugin.sample.demo2.databinding.DataBindingActivity"));
        startActivity(intent);

// 方法2（快速创建Intent）
        Intent intent1 = RePlugin.createIntent("demo2",
                "com.qihoo360.replugin.sample.demo2.databinding.DataBindingActivity");
        startActivity(intent1);

// 方法3（一行搞定）
        RePlugin.startActivity(v.getContext(), new Intent(), "demo2",
                "com.qihoo360.replugin.sample.demo2.databinding.DataBindingActivity");
    }
}
