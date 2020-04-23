package com.lchj.ydyypt.replugin;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.qihoo360.replugin.RePlugin;
import com.qihoo360.replugin.model.PluginInfo;

import javax.security.auth.callback.Callback;

public class RePluginHelper {
    public static void installAndJumpToActivity(Context context, String path, String packageName, String cls) {
        PluginInfo pi = RePlugin.install(path);
        if (pi != null) {
            RePlugin.preload(pi);
        }
        // cls =="com.lchj.dome.PhotoAddActivity"
        Intent intent = RePlugin.createIntent(packageName, cls);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if(!RePlugin.startActivity(context, intent)){
        }
    }
}
