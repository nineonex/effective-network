package cc.seedland.inf.network;

import android.util.Log;

/**
 * 日志工具类
 *
 *
 * Created by xuchunlei on 2017/11/13.
 */

class LogUtil {

    private static final String LOG_TAG = "seed-network";

    private LogUtil(){

    }

    public static void d(String tag, String msg) {
        if(BuildConfig.API_ENV) {
            Log.d(tag, msg);
        }
    }

    public static void d(String msg) {
        d(LOG_TAG, msg);
    }

    public static void i(String tag, String msg) {
        if(BuildConfig.API_ENV) {
            Log.i(tag, msg);
        }
    }

    public static void i(String msg) {
        i(LOG_TAG, msg);
    }

    public static void w(String tag, String msg) {
        if(BuildConfig.API_ENV) {
            Log.w(tag, msg);
        }
    }

    public static void w(String msg) {
        w(LOG_TAG, msg);
    }

    public static void e(String tag, String msg) {
        if(BuildConfig.API_ENV) {
            Log.e(tag, msg);
        }
    }

    public static void e(String msg) {
        e(LOG_TAG, msg);
    }
}
