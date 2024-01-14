package com.lzq.dawn.util.language;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.lzq.dawn.DawnBridge;

import java.util.Locale;

/**
 * @Name :LanguageUtils
 * @Time :2022/8/2 11:36
 * @Author :  Lzq
 * @Desc : 语言
 */
public final class LanguageUtils {
    private LanguageUtils() {
    }

    private static final String KEY_LOCALE = "KEY_LOCALE";
    private static final String VALUE_FOLLOW_SYSTEM = "VALUE_FOLLOW_SYSTEM";

    /**
     * 应用系统语言。
     */
    public static void applySystemLanguage() {
        applySystemLanguage(false);
    }

    /**
     * 应用系统语言
     *
     * @param isRelaunchApp true 重新启动应用程序，false 重新创建所有活动。
     */
    public static void applySystemLanguage(final boolean isRelaunchApp) {
        applyLanguageReal(null, isRelaunchApp);
    }

    /**
     * 应用语言
     *
     * @param locale 语言环境的语言。
     */
    public static void applyLanguage(@NonNull final Locale locale) {
        applyLanguage(locale, false);
    }


    /**
     * 应用语言
     *
     * @param locale        语言环境的语言。
     * @param isRelaunchApp true 重新启动应用程序，false 重新创建所有活动。
     */
    public static void applyLanguage(@NonNull final Locale locale,
                                     final boolean isRelaunchApp) {
        applyLanguageReal(locale, isRelaunchApp);
    }

    private static void applyLanguageReal(final Locale locale,
                                          final boolean isRelaunchApp) {


        Locale destLocal = locale == null ? getLocal(Resources.getSystem().getConfiguration()) : locale;
        updateAppContextLanguage(destLocal, new DawnBridge.Consumer<Boolean>() {
            @Override
            public void accept(Boolean success) {
                if (success) {
                    restart(isRelaunchApp);
                } else {
                    // use relaunch app
                    DawnBridge.relaunchApp();
                }
            }
        });
    }

    private static void restart(final boolean isRelaunchApp) {
        if (isRelaunchApp) {
            DawnBridge.relaunchApp();
        } else {
            for (Activity activity : DawnBridge.getActivityList()) {
                activity.recreate();
            }
        }
    }


    /**
     * 返回上下文的语言环境。
     *
     * @return 返回上下文的语言环境。
     */
    public static Locale getContextLanguage(Context context) {
        return getLocal(context.getResources().getConfiguration());
    }


    /**
     * 返回 applicationContext 的语言环境。
     *
     * @return 返回 applicationContext 的语言环境。
     */
    public static Locale getAppContextLanguage() {
        return getContextLanguage(DawnBridge.getApp());
    }

    /**
     * 返回系统的语言环境
     *
     * @return 返回系统的语言环境
     */
    public static Locale getSystemLanguage() {
        return getLocal(Resources.getSystem().getConfiguration());
    }

    /**
     * 更新 applicationContext 的语言环境。
     *
     * @param destLocale dest 语言环境。
     * @param consumer   The consumer.
     */
    public static void updateAppContextLanguage(@NonNull Locale destLocale, @Nullable DawnBridge.Consumer<Boolean> consumer) {
        pollCheckAppContextLocal(destLocale, 0, consumer);
    }

    public static void pollCheckAppContextLocal(final Locale destLocale, final int index, final  DawnBridge.Consumer<Boolean> consumer) {
        Resources appResources = DawnBridge.getApp().getResources();
        Configuration appConfig = appResources.getConfiguration();
        Locale appLocal = getLocal(appConfig);

        setLocal(appConfig, destLocale);

        DawnBridge.getApp().getResources().updateConfiguration(appConfig, appResources.getDisplayMetrics());

        if (consumer == null) {
            return;
        }

        if (isSameLocale(appLocal, destLocale)) {
            consumer.accept(true);
        } else {
            if (index < 20) {
                DawnBridge.runOnUiThreadDelayed(new Runnable() {
                    @Override
                    public void run() {
                        pollCheckAppContextLocal(destLocale, index + 1, consumer);
                    }
                }, 16);
                return;
            }
            Log.e("LanguageUtils", "appLocal didn't update.");
            consumer.accept(false);
        }
    }




    private static Locale string2LocaleReal(String str) {
        if (!isRightFormatLocalStr(str)) {
            return null;
        }

        try {
            int splitIndex = str.indexOf("$");
            return new Locale(str.substring(0, splitIndex), str.substring(splitIndex + 1));
        } catch (Exception ignore) {
            return null;
        }
    }

    private static boolean isRightFormatLocalStr(String localStr) {
        char[] chars = localStr.toCharArray();
        int count = 0;
        for (char c : chars) {
            if (c == '$') {
                if (count >= 1) {
                    return false;
                }
                ++count;
            }
        }
        return count == 1;
    }

    private static boolean isSameLocale(Locale l0, Locale l1) {
        return DawnBridge.equals(l1.getLanguage(), l0.getLanguage())
                && DawnBridge.equals(l1.getCountry(), l0.getCountry());
    }

    private static Locale getLocal(Configuration configuration) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return configuration.getLocales().get(0);
        } else {
            return configuration.locale;
        }
    }

    private static void setLocal(Configuration configuration, Locale locale) {
        configuration.setLocale(locale);
    }

}
