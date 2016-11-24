package com.qty.nvramviewer;


import android.os.Build;

public class Log {

    private static final String TAG = "NvRAMViewer";

    private static final boolean ENABLED_LOG = true;
    private static final boolean DEBUG = true; //(ENABLED_LOG && Build.TYPE.equals("eng") && android.util.Log.isLoggable(TAG, android.util.Log.DEBUG));
    private static final boolean VERBOSE = true; //(ENABLED_LOG && Build.TYPE.equals("eng") && android.util.Log.isLoggable(TAG, android.util.Log.VERBOSE));
    private static final boolean INFO = true; //(ENABLED_LOG && Build.TYPE.equals("eng") && android.util.Log.isLoggable(TAG, android.util.Log.INFO));
    private static final boolean WARN = true; //(ENABLED_LOG && Build.TYPE.equals("eng") && android.util.Log.isLoggable(TAG, android.util.Log.WARN));

    public static int v(String msg) {
        if (VERBOSE) {
            return android.util.Log.v(TAG, msg);
        }
        return -1;
    }

    public static int v(String msg, Throwable tr) {
        if (VERBOSE) {
            return android.util.Log.v(TAG, msg, tr);
        }
        return -1;
    }

    public static int v(Object obj, String msg) {
        if (VERBOSE) {
            return android.util.Log.v(TAG, getPrefix(obj) + msg);
        }
        return -1;
    }

    public static int v(Object obj, String msg, Throwable tr) {
        if (VERBOSE) {
            return android.util.Log.v(TAG, getPrefix(obj) + msg, tr);
        }
        return -1;
    }

    public static int d(String msg) {
        if (DEBUG) {
            return android.util.Log.d(TAG, msg);
        }
        return -1;
    }

    public static int d(String msg, Throwable tr) {
        if (DEBUG) {
            return android.util.Log.d(TAG, msg, tr);
        }
        return -1;
    }

    public static int d(Object obj, String msg) {
        if (DEBUG) {
            return android.util.Log.d(TAG, getPrefix(obj) + msg);
        }
        return -1;
    }

    public static int d(Object obj, String msg, Throwable tr) {
        if (DEBUG) {
            return android.util.Log.d(TAG, getPrefix(obj) + msg, tr);
        }
        return -1;
    }

    public static int i(String msg) {
        if (INFO) {
            return android.util.Log.i(TAG, msg);
        }
        return -1;
    }

    public static int i(String msg, Throwable tr) {
        if (INFO) {
            return android.util.Log.i(TAG, msg, tr);
        }
        return -1;
    }

    public static int i(Object obj, String msg) {
        if (INFO) {
            return android.util.Log.i(TAG, getPrefix(obj) + msg);
        }
        return -1;
    }

    public static int i(Object obj, String msg, Throwable tr) {
        if (INFO) {
            return android.util.Log.i(TAG, getPrefix(obj) + msg, tr);
        }
        return -1;
    }

    public static int w(String msg) {
        if (WARN) {
            return android.util.Log.w(TAG, msg);
        }
        return -1;
    }

    public static int w(String msg, Throwable tr) {
        if (WARN) {
            return android.util.Log.w(TAG, msg, tr);
        }
        return -1;
    }

    public static int w(Object obj, String msg) {
        if (WARN) {
            return android.util.Log.w(TAG, getPrefix(obj) + msg);
        }
        return -1;
    }

    public static int w(Object obj, String msg, Throwable tr) {
        if (WARN) {
            return android.util.Log.w(TAG, getPrefix(obj) + msg, tr);
        }
        return -1;
    }

    public static int e(String msg) {
        return android.util.Log.e(TAG, msg);
    }

    public static int e(String msg, Throwable tr) {
        return android.util.Log.e(TAG, msg, tr);
    }

    public static int e(Object obj, String msg) {
        return android.util.Log.e(TAG, msg);
    }

    public static int e(Object obj, String msg, Throwable tr) {
        return android.util.Log.e(TAG, getPrefix(obj) + msg, tr);
    }

    public static int wtf(String msg) {
        return android.util.Log.wtf(TAG, msg);
    }

    public static int wtf(String msg, Throwable tr) {
        return android.util.Log.wtf(TAG, msg, tr);
    }

    public static int wtf(Object obj, String msg) {
        return android.util.Log.wtf(TAG, getPrefix(obj) + msg);
    }

    public static int wtf(Object obj, String msg, Throwable tr) {
        return android.util.Log.wtf(TAG, getPrefix(obj) + msg, tr);
    }

    private static String getPrefix(Object obj) {
        String prefix = "";
        if (obj != null) {
            prefix = "[" + obj.toString() + "]";
        }
        return prefix;
    }
}
