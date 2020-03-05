package com.lchj.ydyypt.common;

import android.view.View;

import com.lchj.ydyypt.bean.AppBean;
import com.lchj.ydyypt.bean.AppModule;

public interface OnMenuAddClickListener {
    void onClick(View view, AppModule appModule, int pos);
}
