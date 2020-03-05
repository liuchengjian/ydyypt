package com.lchj.ydyypt.ui.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lchj.ydyypt.R;
import com.lchj.ydyypt.bean.AppBean;
import com.lchj.ydyypt.bean.AppModule;
import com.lchj.ydyypt.common.OnAppMenuClickListener;
import com.lchj.ydyypt.common.OnMenuAddClickListener;
import com.lchj.ydyypt.db.GreenDaoHelper;
import com.lchj.ydyypt.db.GreenDaoUtils;
import com.lchj.ydyypt.ui.widget.AutoLinefeedLayout;
import com.lchj.ydyypt.utils.LiuUtils;
import com.zhy.adapter.recyclerview.base.ItemViewDelegate;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import java.util.ArrayList;
import java.util.List;


public class AppDelagate implements ItemViewDelegate<AppBean> {
    private Context mContext;
    List<String> tabTxt;
    private OnAppMenuClickListener onAppMenuClickListener;
    private OnMenuAddClickListener onMenuAddClickListener;
    int lastH;
    private boolean isEdit;

    public AppDelagate(Context context, List<String> tabTxt, int lastH, boolean isEdit) {
        this.mContext = context;
        this.tabTxt = tabTxt;
        this.lastH = lastH;
        this.isEdit = isEdit;
    }

    public void setOnAppMenuClickListener(OnAppMenuClickListener onAppMenuClickListener) {
        this.onAppMenuClickListener = onAppMenuClickListener;
    }

    public void setOnMenuAddClickListener(OnMenuAddClickListener onMenuAddClickListener) {
        this.onMenuAddClickListener = onMenuAddClickListener;
    }

    @Override
    public int getItemViewLayoutId() {
        return R.layout.rv_all_apps_list;
    }

    @Override
    public boolean isForViewType(AppBean item, int position) {
        return !item.getResMenu().isEmpty();
    }

    @Override
    public void convert(ViewHolder holder, AppBean appBean, int position) {
        LinearLayout mLLOneAppList = holder.getView(R.id.mLLOneAppList);
        if (!appBean.getResMenu().isEmpty()) {
            if (!TextUtils.isEmpty(appBean.getResMenu().get(0).getResId())) {
                mLLOneAppList.setVisibility(View.VISIBLE);
                holder.setText(R.id.mTvAppTitle, appBean.getAppName());
                AutoLinefeedLayout mLLAppAll = holder.getView(R.id.mLLAppAll);
                mLLAppAll.removeAllViews();
                //                List<AppModule> resMenu = appBean.getResMenu();
                List<AppModule> resMenu = isEdit?GreenDaoUtils.getInstance().queryAllModule(appBean):GreenDaoUtils.getInstance().queryAllModuleAdd(appBean);
                int size = resMenu.size();
                if (!resMenu.isEmpty()) {
                    for (int i = 0; i < size; i++) {
                        // 用以下方法将layout布局文件换成view
                        View view = menuView(i, appBean,resMenu,position);
                        mLLAppAll.addView(view);
                    }
                }
                //判断最后一个view
//                if (position == tabTxt.size() - 1) {
//                    if (mLLAppAll.getHeight() < lastH) {
//                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
//                        params.height = lastH;
//                        params.leftMargin = LiuUtils.dip2px(mContext, 10);
//                        mLLAppAll.setLayoutParams(params);
//                    }
//                }
            } else {
                mLLOneAppList.setVisibility(View.GONE);
            }


        }


//        holder.setText(R.id.mTvMenuTitle, appBean.getResMenu().get(position).getResName());
//        ImageView mIvAppLogo = holder.getView(R.id.mIvAppLogo);
//        ImageView mIvAppAdd = holder.getView(R.id.mIvAppAdd);
////                if(resMenu.get(i).getResId())
//        Glide.with(mContext)
//                .load(appBean.getResMenu().get(position).getUrl())
//                .into(mIvAppLogo);
    }

    private View menuView(final int i,AppBean appBean, List<AppModule> resMenu,int position) {
        final LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.rv_apps_list, null);
        LinearLayout mLLMenu = view.findViewById(R.id.mLLMenu);
        TextView mTvMenuTitle = view.findViewById(R.id.mTvMenuTitle);
        ImageView mIvAppAdd = view.findViewById(R.id.mIvAppAdd);
        mIvAppAdd.setVisibility(isEdit ? View.VISIBLE : View.INVISIBLE);
        if (resMenu.get(i).getCustom().equals("true")) {
            mIvAppAdd.setImageResource(R.drawable.icon_app_add);
        } else {
            mIvAppAdd.setImageResource(R.drawable.icon_add_no);
        }
//        mIvAppAdd.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (onMenuAddClickListener != null)
//                    onMenuAddClickListener.onClick(view, resMenu.get(i), i);
//            }
//        });
        LinearLayout mLLApp = view.findViewById(R.id.mLLApp);
        mTvMenuTitle.setText(resMenu.get(i).getResName());
        ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) mLLApp.getLayoutParams();
        lp.width = LiuUtils.getScreenWidth(mContext) / 5;
        mLLApp.setLayoutParams(lp);
        mLLMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onAppMenuClickListener != null) {
                    onAppMenuClickListener.onClick(view, appBean,resMenu.get(i), i,position);
                }
            }
        });
        return view;
    }

}
