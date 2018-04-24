package com.abt.key;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;

import com.abt.key.constant.GlobalConstant;
import com.abt.key.util.ActivityUtils;
import com.orhanobut.logger.Logger;

import java.util.List;

/**
 * Created by hwq on 2018/4/24.
 */
public class MainActivity extends AppCompatActivity {

    private CountDown mCountDown;
    private boolean mLeftPress = false;
    private boolean mRightPress = false;
    private volatile boolean mCounting = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Logger.d("onKeyDown() keyCode = " + keyCode);
        if (keyCode == GlobalConstant.RIGHT_KEY_CODE) {
            mRightPress = true;
            if (mLeftPress) {
                listenLongPress();
            } else {
                unlistenLongPress();
            }
            return true;
        } else if (keyCode == GlobalConstant.LEFT_KEY_CODE) {
            mLeftPress = true;
            if (mRightPress) {
                listenLongPress();
            } else {
                unlistenLongPress();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        Logger.d("onKeyUp() keyCode = " + keyCode);
        if (keyCode == GlobalConstant.RIGHT_KEY_CODE) {
            mRightPress = false;
            unlistenLongPress();
        } else if (keyCode == GlobalConstant.LEFT_KEY_CODE) {
            mLeftPress = false;
            unlistenLongPress();
        }
        return super.onKeyUp(keyCode, event);
    }

    /**
     * 取消监听长按事件
     */
    private void unlistenLongPress() {
        if (null != mCountDown) {
            mCountDown.cancel();
            mCountDown = null;
        }
        mCounting = false;
    }

    /**
     * 监听长按事件
     */
    private void listenLongPress() {
        if (null != mCountDown) {
            if (mCounting) {
                // 正在监听，什么都不用做
                return;
            } else {
                // 没在监听，重置
                mCountDown.cancel();
                mCountDown = null;
            }
        }

        /**
         * 发起长按监听
         */
        if (mCountDown == null && !mCounting) {
            mCountDown = new CountDown(5 * 1000, 1000);
            mCountDown.start();
            mCounting = true;
        }
    }

    /**
     * 处理长按返回Launcher倒计时
     */
    private class CountDown extends CountDownTimer {

        public CountDown(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
            Logger.d("CountDown millisInFuture = " + millisInFuture);
            Logger.d("CountDown countDownInterval = " + countDownInterval);
        }

        @Override
        public void onTick(long l) {
            Logger.d("onTick l = " + l);
        }

        @Override
        public void onFinish() {
            if (mLeftPress && mRightPress) {
                if (enableLongPressAction()) {
                    onLongPressAction();
                } else {
                    String pkgLauncher = "com.android.launcher3";
                    String pkgSettings = "com.android.settings";
                    //getActivities(MainActivity.this, pkgSettings);
                    //ActivityUtils.startHomeActivity();
                    startAPPFromPackageName(MainActivity.this, pkgSettings);
                    jumpToLauncherAction();
                }
            } else {
                unlistenLongPress();
                Logger.d("提前退出长按事件");
            }
        }
    }

    protected void jumpToLauncher() {
        Logger.d("jumpToLauncher()");
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addCategory(Intent.CATEGORY_HOME);
        MainActivity.this.startActivity(intent);
    }

    private void finishThis() {
        // MainActivity.this.finish();
        Logger.d("MainActivity.this.finish()");
    }

    private void getActivities(Activity activity, String packageName) {
        Intent localIntent = new Intent("android.intent.action.MAIN",null);
        localIntent.addCategory("android.intent.category.LAUNCHER");
        List<ResolveInfo> appList = activity.getPackageManager().queryIntentActivities(localIntent,0);
        Logger.e("getActivities -> = ");
        for (int i = 0; i < appList.size(); i++) {
            ResolveInfo resolveInfo = appList.get(i);
            String packageStr = resolveInfo.activityInfo.packageName;
            Logger.e("packageStr -> " + packageStr);
            if (packageStr.equals(packageName)) {
                //这个就是你想要的那个Activity
                Logger.e("activityInfo -> name = " + resolveInfo.activityInfo.name);
                break;
            }
        }
    }

    /**
     * 通过packagename启动应用
     * @param context
     * @param packageName
     * */
    public static void startAPPFromPackageName(Context context, String packageName) {
        Intent intent = isExit(context,packageName);
        if (intent==null) {
            Logger.i(packageName+" not found!");
            //Intent launcher = new Intent(context, com.android.launcher3.Launcher.class);
            return;
        }
        context.startActivity(intent);
    }

    /**
     * 通过packagename判断应用是否安装
     * @param context
     * @param packageName
     * @return 跳转的应用主activity Intent
     * */
    public static Intent isExit(Context context, String packageName) {
        PackageManager packageManager = context.getPackageManager();
        Intent it = packageManager.getLaunchIntentForPackage(packageName);
        return it;
    }

    protected boolean enableLongPressAction() {
        return false;
    }
    protected void onLongPressAction() { }
    protected void jumpToLauncherAction() { }

}
