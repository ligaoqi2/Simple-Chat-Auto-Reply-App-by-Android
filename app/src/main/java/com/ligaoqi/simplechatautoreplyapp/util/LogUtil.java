package com.ligaoqi.simplechatautoreplyapp.util;

import android.util.Log;

public class LogUtil {
    /**
     * log输出等级
     */
    private static final int LOG_LEVEL = 0;
    /**
     * 默认日志标签
     */
    private static final String DEFAULT_TAG = "AUTH_AI_MUSIC";

    public static void verbose(String msg) {
        verbose(DEFAULT_TAG, msg);
    }

    public static void verbose(String tag, String msg) {
        if (LOG_LEVEL < Log.VERBOSE) {
            Log.v(tag, msg);
        }
    }

    public static void debug(String msg) {
        debug(DEFAULT_TAG, msg);
    }

    public static void debug(String tag, String msg) {
        if (LOG_LEVEL < Log.DEBUG) {
            Log.d(tag, msg);
        }
    }

    public static void info(String msg) {
        info(DEFAULT_TAG, msg);
    }

    public static void info(String tag, String msg) {
        if (LOG_LEVEL < Log.INFO) {
            Log.i(tag, msg);
        }
    }

    public static void warn(String msg) {
        warn(DEFAULT_TAG, msg);
    }

    public static void warn(String tag, String msg) {
        if (LOG_LEVEL < Log.WARN) {
            Log.w(tag, msg);
        }
    }
    public static void warn(String tag, String msg, Throwable tr) {
        if (LOG_LEVEL < Log.WARN) {
            Log.w(tag, msg, tr);
        }
    }
    public static void warn(String msg, Throwable tr) {
        if (LOG_LEVEL < Log.WARN) {
            Log.w(DEFAULT_TAG, msg, tr);
        }
    }
    public static void error(String msg) {
        error(DEFAULT_TAG, msg);
    }

    public static void error(String tag, String msg) {
        if (LOG_LEVEL < Log.ERROR) {
            Log.e(tag, msg);
        }
    }
    public static void error(String tag, String msg, Throwable tr) {
        if (LOG_LEVEL < Log.ERROR) {
            Log.e(tag, msg, tr);
        }
    }
    public static void error(String msg, Throwable tr) {
        if (LOG_LEVEL < Log.ERROR) {
            Log.e(DEFAULT_TAG, msg, tr);
        }
    }
}
