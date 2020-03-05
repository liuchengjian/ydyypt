package com.lchj.ydyypt.common;

import android.view.View;

import com.lchj.ydyypt.bean.AppBean;
import com.lchj.ydyypt.bean.AppModule;

public interface OnAppMenuClickListener {
    void onClick(View view,AppBean appBean, AppModule appModule, int pos,int itemPos);
}
