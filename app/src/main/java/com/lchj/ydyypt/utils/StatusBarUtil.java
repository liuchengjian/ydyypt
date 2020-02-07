package com.lchj.ydyypt.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.regex.Pattern;

import androidx.annotation.ColorRes;
import androidx.core.content.ContextCompat;

public class StatusBarUtil {
    private static final String KEY_VERSION_OPPO = "ro.build.version.opporom";    //OPPO
    private static final String KEY_VERSION_MIUI = "ro.miui.ui.version.name";     //小米
    private static final String KEY_VERSION_EMUI = "ro.build.version.emui";       //华为
    private static final String KEY_VERSION_SMARTISAN = "ro.smartisan.version";   //锤子
    private static final String KEY_VERSION_VIVO = "ro.vivo.os.version";          //vivo

    /**
     * 设置状态栏透明度、背景颜色、文字颜色
     *
     * @param isTranslate 是否透明，若为true，则bgColor设置无效
     * @param isDarkText  字体颜色，只有黑白两色，无论什么色值，都只会转为黑白两色
     * @param bgColor     背景色，即状态栏颜色，isTranslate若为true则此值无效
     */
    public static void setStatusColor(Activity activity, boolean isTranslate, boolean isDarkText, @ColorRes int bgColor) {
        //如果系统为6.0及以上，就可以使用Google自带的方式
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Window window = activity.getWindow();
            View decorView = window.getDecorView();
            //可有可无
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            decorView.setSystemUiVisibility((isTranslate ? View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN : 0) | (isDarkText ? View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR : 0));
            window.setStatusBarColor(isTranslate ? Color.TRANSPARENT : ContextCompat.getColor(activity, bgColor));
        } else { //如果不是6.0及以上则分情况适配
            if (isColorOS_3() && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) { //如果是OPPO Color3.0 & Android 5.1
                //控制字体颜色，只有黑白两色
                final int SYSTEM_UI_FLAG_OP_STATUS_BAR_TINT = isDarkText ? 0x00000010 : 0x00190000;
                Window window = activity.getWindow();
                View decorView = window.getDecorView();
                decorView.setSystemUiVisibility((isTranslate ? View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN : 0) | SYSTEM_UI_FLAG_OP_STATUS_BAR_TINT);
                //可有可无
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(isTranslate ? Color.TRANSPARENT : ContextCompat.getColor(activity, bgColor));
            } else if (isMIUI6Later()) { //如果是Android 6.0以下，MIUI6及以上
                setMIUI6Translate(activity, isTranslate);
                setMIUI6StatusBarDarkMode(activity, isDarkText);
            } else if (isFlyme4Later()) {
                darModeForFlyme4(activity, isDarkText);
                if (isTranslate) {
                    setStatusTranslate(activity);
                }
            } else {
                //既不属于MIUI6,也不属于OPPO，还不属于魅族，极有可能是华为、三星、索尼、诺基亚、VIVO、锤子、360等
                if (isTranslate) {
                    setStatusTranslate(activity);
                }
//                setStatusBarColor(activity, isTranslate ? Color.TRANSPARENT : ContextCompat.getColor(activity, bgColor));
                setStatusBarColor(activity, isTranslate ? Color.TRANSPARENT : bgColor);
            }
        }
    }

    /**
     * 是否是MIUI6及以后版本
     *
     * @return 是否是MIUI6
     */
    private static boolean isMIUI6Later() {
        try {
            Class<?> cla = Class.forName("android.os.SystemProperties");
            Method mtd = cla.getMethod("get", String.class);
            String val = (String) mtd.invoke(null, KEY_VERSION_MIUI);
            if (val == null || val.length() == 0) {
                return false;
            }
            val = val.replaceAll("[vV]", "");
            int version = Integer.parseInt(val);
            return version >= 6;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 设置MIUI6及以上状态栏透明,字体为默认白色，Android6.0以上也可以
     *
     * @param on 是否为透明
     */
    private static void setMIUI6Translate(Activity activity, boolean on) {
        Window win = activity.getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

    /**
     * 设置MIUI6及以上，Android6.0以下版本状态栏黑色字符
     * Android 6.0以上此方法无效
     * 此方法是官方给的，应该没有错，我没有MIUI6的手机，无法测试
     */
    private static void setMIUI6StatusBarDarkMode(Activity activity, boolean darkmode) {
        Class<? extends Window> clazz = activity.getWindow().getClass();
        try {
            int darkModeFlag = 0;
            Class<?> layoutParams = Class.forName("android.view.MiuiWindowManager$LayoutParams");
            Field field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE");
            darkModeFlag = field.getInt(layoutParams);
            Method extraFlagField = clazz.getMethod("setExtraFlags", int.class, int.class);
            extraFlagField.invoke(activity.getWindow(), darkmode ? darkModeFlag : 0, darkModeFlag);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 是否是ColorOS_3.0系统 Android 5.1
     *
     * @return 是否是colorOS_3.0
     */
    private static boolean isColorOS_3() {
        try {
            Class<?> cla = Class.forName("android.os.SystemProperties");
            Method mtd = cla.getMethod("get", String.class);
            String val = (String) mtd.invoke(null, KEY_VERSION_OPPO);
            if (val == null || val.length() == 0) {
                return false;
            }
            val = val.replaceAll("[vV]", "");
            val = val.substring(0, 1);
            int version = Integer.parseInt(val);
            return version >= 3;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 判断是否是魅族Flyme4
     *
     * @return 是否是魅族Flyme4
     */
    private static boolean isFlyme4Later() {
        if ("MEIZU".equals(Build.BRAND.trim().toUpperCase())) {
            return Build.FINGERPRINT.contains("Flyme_OS_4") || Build.VERSION.INCREMENTAL.contains("Flyme_OS_4") || Pattern.compile("Flyme_OS_[4|5]", Pattern.CASE_INSENSITIVE)
                    .matcher(Build.DISPLAY).find();
        }
        return false;
    }

    /**
     * 设置魅族Flyme4以后 状态栏黑色字体
     * 没有手机，从网上找的方法，应该没问题
     *
     * @param dark 是否黑色
     */
    private static void darModeForFlyme4(Activity activity, boolean dark) {
        boolean result = false;
        try {
            WindowManager.LayoutParams e = activity.getWindow().getAttributes();
            Field darkFlag = WindowManager.LayoutParams.class.getDeclaredField("MEIZU_FLAG_DARK_STATUS_BAR_ICON");
            Field meizuFlags = WindowManager.LayoutParams.class.getDeclaredField("meizuFlags");
            darkFlag.setAccessible(true);
            meizuFlags.setAccessible(true);
            int bit = darkFlag.getInt(null);
            int value = meizuFlags.getInt(e);
            if (dark) {
                value |= bit;
            } else {
                value &= ~bit;
            }
            meizuFlags.setInt(e, value);
            activity.getWindow().setAttributes(e);
        } catch (Exception var8) {
            Log.e("StatusBar", "darkIcon: failed");
        }
    }

    /**
     * 设置状态栏透明，Android4.4、Android5.0以上方法不一
     * 我感觉5.0以下的系统已经很少了吧，应该不需要适配了吧
     * 如果只是设置状态栏透明，就使用这个就好了
     */
    public static void setStatusTranslate(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            View decorView = activity.getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            activity.getWindow().setStatusBarColor(Color.TRANSPARENT);
        } else {
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }

    /**
     * 设置状态栏背景颜色，不适配4.4以下系统
     *
     * @param color 颜色值
     */
    public static void setStatusBarColor(Activity activity, int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            Window window = activity.getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(activity.getResources().getColor(color));//calculateStatusColor(color, 112)
//            window.setStatusBarColor(calculateStatusColor(color, 112));//
//            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//            activity.getWindow().setStatusBarColor(activity.getResources().getColor(color));
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            // 生成一个状态栏大小的矩形

            View statusView = createStatusBarView(activity, calculateStatusColor(color, 112));
            // 添加 statusView 到布局中
            ViewGroup decorView = (ViewGroup) activity.getWindow().getDecorView();
            decorView.addView(statusView);
            setRootView(activity);
        }
    }

    /**
     * 生成一个和状态栏大小相同的半透明矩形条
     *
     * @param activity 需要设置的activity
     * @param color    状态栏颜色值
     * @return 状态栏矩形条
     */
    private static View createStatusBarView(Activity activity, int color) {
        // 绘制一个和状态栏一样高的矩形
        View statusBarView = new View(activity);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                getStatusBarHeight(activity));
        statusBarView.setLayoutParams(params);
        statusBarView.setBackgroundColor(color);
//        statusBarView.setBackgroundColor(color);
        return statusBarView;
    }

    /**
     * 计算状态栏颜色
     *
     * @param color color值
     * @param alpha alpha值
     * @return 最终的状态栏颜色
     */
    private static int calculateStatusColor(int color, int alpha) {
        float a = 1 - alpha / 255f;
        int red = color >> 16 & 0xff;
        int green = color >> 8 & 0xff;
        int blue = color & 0xff;
        red = (int) (red * a + 0.5);
        green = (int) (green * a + 0.5);
        blue = (int) (blue * a + 0.5);
        return 0xff << 24 | red << 16 | green << 8 | blue;
    }

    /**
     * 获取状态栏高度
     *
     * @param context context
     * @return 状态栏高度
     */
    private static int getStatusBarHeight(Context context) {
        // 获得状态栏高度
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        return context.getResources().getDimensionPixelSize(resourceId);
    }


    /**
     * 设置根布局参数
     */
    private static void setRootView(Activity activity) {
        ViewGroup rootView = (ViewGroup) ((ViewGroup) activity.findViewById(android.R.id.content)).getChildAt(0);
        rootView.setFitsSystemWindows(true);
        rootView.setClipToPadding(true);
    }

}