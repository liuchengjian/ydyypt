package com.lchj.ydyypt.ui.fragment;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import android.os.Environment;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.blankj.utilcode.util.LogUtils;
import com.google.android.material.tabs.TabLayout;
import com.lchj.ydyypt.R;
import com.lchj.ydyypt.bean.AppBean;
import com.lchj.ydyypt.bean.AppModule;
import com.lchj.ydyypt.common.ARouterConst;
import com.lchj.ydyypt.common.Api;
import com.lchj.ydyypt.common.OnAppMenuClickListener;
import com.lchj.ydyypt.common.OnMenuAddClickListener;
import com.lchj.ydyypt.common.event.SaveAppMenuEvent;
import com.lchj.ydyypt.db.GreenDaoUtils;
import com.lchj.ydyypt.okgo.OkGoHelper;
import com.lchj.ydyypt.okgo.callback.DownLoadFileUtils;
import com.lchj.ydyypt.okgo.callback.LogDownloadListener;
import com.lchj.ydyypt.replugin.RePluginHelper;
import com.lchj.ydyypt.ui.adapter.AppsAdapter;
import com.lchj.ydyypt.utils.LiuUtils;
import com.lzy.okgo.callback.FileCallback;
import com.lzy.okgo.model.Progress;
import com.lzy.okgo.request.base.Request;
import com.tbruyelle.rxpermissions.RxPermissions;

import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class AppsFragment extends Fragment {
    @BindView(R.id.mTvTitle)
    TextView mTvTitle;
    @BindView(R.id.mTvLeft)
    ImageView mTvLeft;
    @BindView(R.id.mTvRight)
    ImageView mTvRight;
    @BindView(R.id.mTabLayout)
    TabLayout mTabLayout;
    @BindView(R.id.mRecyclerView)
    RecyclerView mRecyclerView;
    private boolean firstAlreadyInflated = true;
    private ViewGroup firstFloorVg;
    private LinearLayoutManager manager;
    Unbinder unbinder;
    private List<AppBean> localList = new ArrayList<>();
    private List<AppBean> appList = new ArrayList<>();
    private AppsAdapter mAppsAdapter;
    List<String> tabTxt = new ArrayList<>();
    //判读是否是recyclerView主动引起的滑动，true- 是，false- 否，由tablayout引起的
    private boolean isRecyclerScroll;
    //记录上一次位置，防止在同一内容块里滑动 重复定位到tablayout
    private int lastPos;
    //用于recyclerView滑动到指定的位置
    private boolean canScroll;
    private int scrollToPosition;

    //    private RxPermissions rxPermissions
    public static AppsFragment newInstance() {
        AppsFragment fragment = new AppsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @OnClick({R.id.mTvRight})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mTvRight:
                // 2. 跳转并携带参数
                ARouter.getInstance().build(ARouterConst.APP_EDIT)
                        .withString("key3", "888")
//                                    .withObject("key4", new Test("Jack", "Rose"))
                        .navigation();
                break;
            default:
                break;
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_apps, container, false);
        EventBus.getDefault().register(this);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        unbinder = ButterKnife.bind(this, view);
        mTvTitle.setText("应用");
        mTvLeft.setVisibility(View.INVISIBLE);
        mTvRight.setVisibility(View.VISIBLE);
        mTvRight.setImageResource(R.drawable.icon_app_header_edit);
        initData();
        initRecyclerView();
    }

    private void initData() {
        localList.clear();
        appList.clear();
        tabTxt.clear();
        mTabLayout.removeAllTabs();
        localList = GreenDaoUtils.getInstance().queryAllApps();
        Log.e("qqqqq", "appList" + appList);
        if (localList.isEmpty()) {
            LiuUtils.makeText(getActivity(), "没有App数据！");
            return;
        }
        for (AppBean appBean : localList) {
            List<AppModule> resMenu = GreenDaoUtils.getInstance().queryAllModuleAdd(appBean);
            if (!resMenu.isEmpty() && !TextUtils.isEmpty(appBean.getResMenu().get(0).getResId())) {
                for (AppModule appModule : resMenu) {
                    appModule.setAppId(appBean.getId());
                }
                tabTxt.add(appBean.getAppName());
                appList.add(appBean);
                mTabLayout.addTab(mTabLayout.newTab().setText(appBean.getAppName()));
            }
        }
    }

    /**
     * 初始化RecyclerView 包含RecyclerView和TabLayout直接的联动
     */
    @SuppressLint("ClickableViewAccessibility")
    private void initRecyclerView() {

        manager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(manager);
        if (mAppsAdapter == null) {
            //计算内容块所在的高度，全屏高度-状态栏高度-tablayout的高度(这里固定高度50dp)，用于recyclerView的最后一个item view填充高度
            int screenH = LiuUtils.getScreenHeidth(getActivity());
            int statusBarH = LiuUtils.dip2px(getActivity(), 30);
            int tabH = LiuUtils.dip2px(getActivity(), 50);
            int lastH = screenH - statusBarH - tabH;
            mAppsAdapter = new AppsAdapter(getActivity(), appList, tabTxt, lastH, false);
            mRecyclerView.setAdapter(mAppsAdapter);
        } else {
            mAppsAdapter.notifyDataSetChanged();
        }
        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        mAppsAdapter.setOnAppMenuClickListener(new OnAppMenuClickListener() {
            @Override
            public void onClick(View view, AppBean appBean, AppModule appModule, int pos, int itemPos) {
//                 LiuUtils.makeText(getActivity(), appModule.getResName());
                String imageUrl = Api.DOWNLOAD_URL + "/downloadAttachById?id=" + appBean.getAttachFileId();//http://47.98.121.127/upload/advert/20180706/1530849977.jpg
                String localPath = getApkPath();// /storage/emulated/0/image/
                String mDestFileName = appBean.getAttachFileId() + ".apk";
//                if (appBean.getIsDownload()) {
//                    RePluginHelper.installAndJumpToActivity(getActivity(), localPath + "/" + mDestFileName, appBean.getAppCode(), appBean.getAppCode() + "." + appModule.getResCode());
//                } else {
                    DownLoadFileUtils.downloadFile(getActivity(), imageUrl, new FileCallback(localPath, mDestFileName) { //文件下载时指定下载的路径以及下载的文件的名称
                        @Override
                        public void onStart(Request<File, ? extends Request> request) {
                            super.onStart(request);
                        }

                        @Override
                        public void onSuccess(com.lzy.okgo.model.Response<File> response) {
                            LogUtils.e("下载文件成功" + "DDDDD" + response.body().length());
                        }

                        @Override
                        public void onFinish() {
                            super.onFinish();
                            progressDialog.dismiss();
                            RePluginHelper.installAndJumpToActivity(getActivity(), localPath + "/" + mDestFileName, appBean.getAppCode(), appBean.getAppCode() + "." + appModule.getResCode());
//                            GreenDaoUtils.getInstance().changeAppDownload(appBean);
                        }

                        @Override
                        public void onError(com.lzy.okgo.model.Response<File> response) {
                            super.onError(response);
                            LogUtils.e("下载文件出错" + "DDDDD" + response.message());
                        }

                        @Override
                        public void downloadProgress(final Progress progress) {
                            super.downloadProgress(progress);
//                    dialog = DialogUtils.ZLoadingDialog(LoginActivity.this, "开始下载");
                            int dLProgress = (int) (progress.fraction * 100);
                            LogUtils.e("下载文件出错" + "DDDDD" + dLProgress);
//                        String currentSize = Formatter.formatFileSize(LoginActivity.this, progress.currentSize);
                            String totalSize = Formatter.formatFileSize(getActivity(), progress.totalSize);
                            progressDialog.setIcon(R.mipmap.ic_launcher);
                            progressDialog.setTitle("下载");
                            progressDialog.setMessage("正在下载中");
//                        progressDialog.setMax(200);
                            progressDialog.setProgress(dLProgress);
                            //ProgressDialog.STYLE_SPINNER  默认进度条是转圈
                            //ProgressDialog.STYLE_HORIZONTAL  横向进度条
                            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                            progressDialog.show();

                        }

                    });
                }
//            }
        });

        mRecyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //当滑动由recyclerView触发时，isRecyclerScroll 置true
                if (event.getAction() == MotionEvent.ACTION_MOVE) {
                    isRecyclerScroll = true;
                }
                return false;
            }
        });

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (isRecyclerScroll) {
                    //第一个可见的view的位置，即tablayou需定位的位置
                    int position = manager.findFirstVisibleItemPosition();
                    if (lastPos != position) {
                        mTabLayout.setScrollPosition(position, 0, true);
                    }
                    lastPos = position;
                }
            }
        });
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (canScroll) {
                    canScroll = false;
                    moveToPosition(manager, recyclerView, scrollToPosition);
                }
            }
        });
        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                //点击标签，使recyclerView滑动，isRecyclerScroll置false
                int pos = tab.getPosition();
                isRecyclerScroll = false;
                moveToPosition(manager, mRecyclerView, pos);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }


    /**
     * 移动到指定位置
     *
     * @param manager
     * @param mRecyclerView
     * @param position
     */
    public void moveToPosition(LinearLayoutManager manager, RecyclerView mRecyclerView, int position) {
        // 第一个可见的view的位置
        int firstItem = manager.findFirstVisibleItemPosition();
        // 最后一个可见的view的位置
        int lastItem = manager.findLastVisibleItemPosition();
        if (position <= firstItem) {
            // 如果跳转位置firstItem 之前(滑出屏幕的情况)，就smoothScrollToPosition可以直接跳转，
            mRecyclerView.smoothScrollToPosition(position);
        } else if (position <= lastItem) {
            // 跳转位置在firstItem 之后，lastItem 之间（显示在当前屏幕），smoothScrollBy来滑动到指定位置
            int top = mRecyclerView.getChildAt(position - firstItem).getTop();
            mRecyclerView.smoothScrollBy(0, top);
        } else {
            // 如果要跳转的位置在lastItem 之后，则先调用smoothScrollToPosition将要跳转的位置滚动到可见位置
            // 再通过onScrollStateChanged控制再次调用当前moveToPosition方法，执行上一个判断中的方法
            mRecyclerView.smoothScrollToPosition(position);
            scrollToPosition = position;
            canScroll = true;
        }
    }

    @Subscriber
    private void onMessageEvent(SaveAppMenuEvent event) {
        initData();
        mAppsAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    private String getApkPath() {
        String path = Environment.getExternalStorageDirectory() + ApkPath();
        File file = new File(path);
        if (file.mkdirs()) {
            return path;
        }
        return path;
    }

    private String ApkPath() {
        return "/download";
    }
}
