package com.lchj.ydyypt.okgo.callback;

import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.FileCallback;

import java.io.File;

public class DownLoadFileUtils {
    private static String mBasePath; //本地文件存储的完整路径  /storage/emulated/0/book/恰似寒光遇骄阳.txt
    private static final int PROGRESS_START = 0x01;
    private static final int PROGRESS_MESSAGE = 0x02;
    private static final int PROGRESS_END = 0x03;
    private static Context mContext;
    private static Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case PROGRESS_START:

                    break;

                case PROGRESS_MESSAGE:
//                    dialog = DialogUtils.ZLoadingDialog(mContext, "下载进度:" + msg.obj + "%");
                    break;

                case PROGRESS_END:
//                    dialog.setHintText((String) msg.obj);
//                    dialog.dismiss();
                    break;
            }
        }
    };

    /**
     * @param context          上下文
     * @param fileUrl          下载完整url
     *                         （我们从服务器端获取到的数据都是相对的地址）例如： "filepath": "/movie/20180511/1526028508.txt"
     */
    public static void downloadFile(final Context context, String fileUrl, FileCallback fileCallback) {
        mContext = context;
        OkGo.<File>get(fileUrl).tag(context).execute(fileCallback);
    }

    //拼接一个本地的完整的url 供下载文件时传入一个本地的路径
    private static final String mSDPath = Environment.getExternalStorageDirectory().getPath();
    //分类别路径
    private static String mClassifyPath = null;

    public static String customLocalStoragePath(String differentName) {
        File basePath = new File(mSDPath); // /storage/emulated/0
        mClassifyPath = mSDPath + "/" + differentName + "/";  //如果传来的是 book 拼接就是 /storage/emulated/0/book/
        //如果传来的是game  那拼接就是 /storage/emulated/0/game/
        if (!basePath.exists()) {
            basePath.mkdirs();
            System.out.println("文件夹创建成功");
        }
        return mClassifyPath;
    }


    //截取一个文件加载显示时传入的一个本地完整路径
    public static String subFileFullName(String fileName, String fileUrl) {
        String cutName = fileName + fileUrl.substring(fileUrl.lastIndexOf("."), fileUrl.length());  //这里获取的是  恰似寒光遇骄阳.txt
        return cutName;
    }
}
