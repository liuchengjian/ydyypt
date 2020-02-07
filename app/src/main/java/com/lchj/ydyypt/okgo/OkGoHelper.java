package com.lchj.ydyypt.okgo;

import android.app.Activity;

import com.lchj.ydyypt.bean.UserBean;
import com.lchj.ydyypt.bean.AppsList;
import com.lchj.ydyypt.okgo.callback.DialogCallback;
import com.lzy.okgo.OkGo;

/**
 * Author by lchj,
 * Email 627107345 @qq.com, Date on 2020/1/20.
 */
public class OkGoHelper {
    /**
     * 登录
     *
     * @param activity
     * @param url
     * @param userName
     * @param passWord
     * @param jsonCallback
     */
    public static void login(Activity activity, String url, String userName, String passWord, DialogCallback<ResultInfo<UserBean>> jsonCallback) {
        OkGo.<ResultInfo<UserBean>>post(url)//
                .tag(activity)//
                .headers("Content-Type", "application/x-www-form-urlencoded")//
                .params("username", userName)//
                .params("password", passWord)//
                .execute(jsonCallback);
    }

    public static void initAppData(Activity activity, String url, String userName, DialogCallback<ResultInfo> jsonCallback) {
        OkGo.<ResultInfo>get(url)//
                .tag(activity)//
                .headers("Content-Type", "application/x-www-form-urlencoded")//
                .params("userName", userName)//
                .execute(jsonCallback);
    }
    public static void getAppVerification(Activity activity, String url, String userId, DialogCallback<ResultInfo<AppsList>> jsonCallback) {
        OkGo.<ResultInfo<AppsList>>get(url)//
                .tag(activity)//
                .headers("Content-Type", "application/x-www-form-urlencoded")//
                .params("userId", userId)//
                .params("deviceType", "1")//
                .execute(jsonCallback);
    }
}
