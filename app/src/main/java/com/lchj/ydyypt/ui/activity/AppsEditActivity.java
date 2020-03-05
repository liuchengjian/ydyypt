package com.lchj.ydyypt.ui.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.blankj.utilcode.util.GsonUtils;
import com.blankj.utilcode.util.SPStaticUtils;
import com.google.android.material.tabs.TabLayout;
import com.lchj.ydyypt.R;
import com.lchj.ydyypt.bean.AppBean;
import com.lchj.ydyypt.bean.AppModule;
import com.lchj.ydyypt.bean.SaveMenuBean;
import com.lchj.ydyypt.common.ARouterConst;
import com.lchj.ydyypt.common.Api;
import com.lchj.ydyypt.common.Const;
import com.lchj.ydyypt.common.OnAppMenuClickListener;
import com.lchj.ydyypt.common.event.SaveAppMenuEvent;
import com.lchj.ydyypt.db.GreenDaoUtils;
import com.lchj.ydyypt.okgo.OkGoHelper;
import com.lchj.ydyypt.okgo.ResultInfo;
import com.lchj.ydyypt.okgo.callback.DialogCallback;
import com.lchj.ydyypt.ui.adapter.AppsAdapter;
import com.lchj.ydyypt.utils.LiuUtils;
import com.lzy.okgo.model.Response;

import org.simple.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

@Route(path = ARouterConst.APP_EDIT)
public class AppsEditActivity extends BaseActivity {
    @BindView(R.id.mTvTitle)
    TextView mTvTitle;
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
    List<SaveMenuBean> saveMenuList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apps_edit);
        unbinder = ButterKnife.bind(this);
        mTvTitle.setText("应用编辑");
        mTvRight.setVisibility(View.VISIBLE);
        mTvRight.setImageResource(R.drawable.icon_app_save);
        initData();
        initRecyclerView();
    }

    private void initData() {
        localList.clear();
        appList.clear();
        tabTxt.clear();
        localList = GreenDaoUtils.getInstance().queryAllApps();
        Log.e("qqqqq", "appList" + appList);
        if (localList.isEmpty()) {
            LiuUtils.makeText(this, "没有App数据！");
            return;
        }
        for (AppBean appBean : localList) {
            List<AppModule> resMenu = GreenDaoUtils.getInstance().queryAllModule(appBean);
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
        //计算内容块所在的高度，全屏高度-状态栏高度-tablayout的高度(这里固定高度50dp)，用于recyclerView的最后一个item view填充高度
        int screenH = LiuUtils.getScreenHeidth(this);
        int statusBarH = LiuUtils.dip2px(this, 30);
        int tabH = LiuUtils.dip2px(this, 50);
        int lastH = screenH - statusBarH - tabH;
        manager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(manager);
        mAppsAdapter = new AppsAdapter(this, appList, tabTxt, lastH, true);
        mRecyclerView.setAdapter(mAppsAdapter);
        mAppsAdapter.setOnAppMenuClickListener(new OnAppMenuClickListener() {
            @Override
            public void onClick(View view, AppBean appBean, AppModule appModule, int pos, int itemPos) {
                if (GreenDaoUtils.getInstance().isLimitMenu(appModule)) {
                    if(appModule.getCustom().equals("false")){
                        LiuUtils.makeText(AppsEditActivity.this, "菜单选中数量不能超过8个");
                        return;
                    }
                }
                GreenDaoUtils.getInstance().changeMenuEditType(appModule);
                mAppsAdapter.notifyItemChanged(itemPos);
            }
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

    @OnClick({R.id.mTvLeft, R.id.mTvRight})
    void OnClick(View v) {
        switch (v.getId()) {
            case R.id.mTvLeft:
                finish();
                break;
            case R.id.mTvRight:
                saveMenu();
                break;
        }
    }

    /**
     * 保存app的模块到网络
     */
    private void saveMenu() {
        if (appList.isEmpty()) return;
        String appId = "";
        String userId = SPStaticUtils.getString(Const.userId);
        int i = 0;
        for (AppBean app : appList) {
            if (i == 0) {
                appId = app.getId();
            } else {
                appId = appId + app.getId();
            }
            if (!app.getResMenu().isEmpty()) {
                for (AppModule appModule : app.getResMenu()) {
                    if (appModule.getCustom().equals("true")) {
                        saveMenuList.add(new SaveMenuBean(userId, appModule.getResId(), app.getId()));
                    }
                }
            }
        }
        if (saveMenuList.isEmpty()) {
            return;
        }
        String json = GsonUtils.toJson(saveMenuList);
        OkGoHelper.saveAppMenu(this,
                Api.server_Url + "/user/saveCustomResource",
                userId,
                appId,
                json,
                new DialogCallback<ResultInfo>(this, "获取用户信息···") {
                    @Override
                    public void onSuccess(Response<ResultInfo> response) {
                        if (response.body().success) {
                            LiuUtils.makeText(AppsEditActivity.this, response.body().msg);
                            EventBus.getDefault().post(new SaveAppMenuEvent());
                            finish();
                        } else {
                            LiuUtils.makeText(AppsEditActivity.this, response.body().msg);
                        }

                    }

                    @Override
                    public void onError(Response<ResultInfo> response) {
                        super.onError(response);
                        LiuUtils.makeText(AppsEditActivity.this, response.getException().getMessage());
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}
