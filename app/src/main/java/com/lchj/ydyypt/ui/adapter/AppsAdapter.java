package com.lchj.ydyypt.ui.adapter;

import android.content.Context;

import com.lchj.ydyypt.bean.AppBean;
import com.lchj.ydyypt.common.OnAppMenuClickListener;
import com.lchj.ydyypt.common.OnMenuAddClickListener;
import com.zhy.adapter.recyclerview.MultiItemTypeAdapter;

import java.util.List;

public class AppsAdapter extends MultiItemTypeAdapter<AppBean> {
    private AppDelagate appDelagate;

    public AppsAdapter(Context context, List<AppBean> datas, List<String> tabTxt, int lastH,boolean isEdit) {
        super(context, datas);
        appDelagate = new AppDelagate(context, tabTxt, lastH,isEdit);
        addItemViewDelegate(appDelagate);
    }

    public void setOnAppMenuClickListener(OnAppMenuClickListener onAppMenuClickListener) {
        appDelagate.setOnAppMenuClickListener(onAppMenuClickListener);
    }

    public void setOnMenuAddClickListener(OnMenuAddClickListener onMenuAddClickListener) {
        appDelagate.setOnMenuAddClickListener(onMenuAddClickListener);
    }
}
