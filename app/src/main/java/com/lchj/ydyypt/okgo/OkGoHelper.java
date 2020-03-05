package com.lchj.ydyypt.okgo;

import android.app.Activity;
import android.os.Environment;

import com.lchj.ydyypt.bean.UserBean;
import com.lchj.ydyypt.bean.AppsList;
import com.lchj.ydyypt.common.OnAppMenuClickListener;
import com.lchj.ydyypt.okgo.callback.DialogCallback;
import com.lchj.ydyypt.okgo.callback.LogDownloadListener;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.FileCallback;
import com.lzy.okgo.request.GetRequest;
import com.lzy.okserver.OkDownload;
import com.lzy.okserver.task.XExecutor;

import java.io.File;

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

    public static void downloadApp(OnAppMenuClickListener activity, String url, LogDownloadListener downloadListener) {
        //这里只是演示，表示请求可以传参，怎么传都行，和okgo使用方法一样
        GetRequest<File> request = OkGo.<File>get(url)//
                .headers("Content-Type", "application/x-www-form-urlencoded");//
//                 .params("bbb", "222")
        String path = Environment.getExternalStorageDirectory() + "/liuchj/download";
        //这里第一个参数是tag，代表下载任务的唯一标识，传任意字符串都行，需要保证唯一,我这里用url作为了tag
        OkDownload.request(url, request)//
//                 .priority(apk.priority)//
//                 .extra1(apk)//
                .fileName(path)
                .save()//
                .register(downloadListener)//
                .start();

    }

    public static void saveAppMenu(Activity activity, String url, String userId, String appId, String json, DialogCallback<ResultInfo> jsonCallback) {
        OkGo.<ResultInfo>post(url)//
                .tag(activity)//
                .headers("Content-Type", "application/x-www-form-urlencoded")//
                .params("userId", userId)//
                .params("appId", appId)//
                .params("json", json)//
                .execute(jsonCallback);
    }

}
