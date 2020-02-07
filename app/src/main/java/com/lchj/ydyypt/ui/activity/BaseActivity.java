package com.lchj.ydyypt.ui.activity;

import android.os.Bundle;
import android.os.PersistableBundle;

import androidx.appcompat.app.AppCompatActivity;
import butterknife.ButterKnife;

/**
 * Author by lchj,
 * Email 627107345 @qq.com, Date on 2020/1/20.
 */
public abstract class BaseActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        ButterKnife.bind(this);
        super.onCreate(savedInstanceState, persistentState);
    }
}
