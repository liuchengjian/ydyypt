package com.lchj.ydyypt.ui.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lchj.ydyypt.R;
import com.lchj.ydyypt.bean.AppBean;
import com.lchj.ydyypt.db.GreenDaoUtils;

import java.util.List;

/**
 *
 */
public class AppsFragment extends Fragment {


    public static AppsFragment newInstance() {
        AppsFragment fragment = new AppsFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_apps, container, false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        List<AppBean> appList = GreenDaoUtils.getInstance().queryAllApps();
        Log.e("qqqqq","appList"+appList);
    }
}
