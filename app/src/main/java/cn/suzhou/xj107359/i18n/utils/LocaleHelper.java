package cn.suzhou.xj107359.i18n.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.LocaleList;
import android.preference.PreferenceManager;
import android.util.Log;

import java.util.Locale;

import cn.suzhou.xj107359.i18n.base.AppApplication;
import cn.suzhou.xj107359.i18n.base.BaseActivity;
import cn.suzhou.xj107359.i18n.ui.MainActivity;

public class LocaleHelper {
    private static final String TAG = "LocaleHelper";

    private static final String SELECTED_LANGUAGE = "utils.I18NUtils.selected.language";

    private static final int FIRST_LOCAL_DEFAULT = 0;
    private static final int FIRST_LOCAL_NON_DEFAULT = 1;

    public static final int LANGUAGE_TYPE_DEFAULT = 0;
    public static final int LANGUAGE_TYPE_ENGLISH = 1;
    public static final int LANGUAGE_TYPE_CHINESE = 2;
    public static final int LANGUAGE_TYPE_THAILAND = 3;

    // 在Application和AppCompatActivity的attachBaseContext中调用
    public static Context onAttachApplication(Context context) {
        int type = getPersistedLanguageType(context);
        return setLocaleOnAttach(context, type);
    }

    public static Context onAttachActivity(Context context) {
        int type = getPersistedLanguageType(AppApplication.getAppContext());
        return setLocale(context, type);
    }

    // 根据languageType设置语言
    public static void setLanguage(BaseActivity activity, int languageType) {
//        boolean sameLanguage = LocaleHelper.isSameLanguage(activity, languageType);
//        if (!sameLanguage) {
        setLocale(activity, languageType);
        // 前面取系统语言时判断spType=0时取第一值，所以设置完语言后缓存type
        putPersistedLanguageType(AppApplication.getAppContext(), languageType);
        toRestartMainActivity(activity);
//        } else {
//            // 缓存用户此次选择的类型，可能出现type不同而locale一样的情况（如：系统默认泰语type = 0，而我选择的也是泰语type = 3）
//            LocaleHelper.putPersistedLanguageType(activity, languageType);
//        }
    }

    // 获取用户选择的语言
    @SuppressWarnings("unused")
    public static String getDisplayOfSelectedLanguage(Context context) {
        Locale localSelected = getLocale();
        return localSelected.getDisplayLanguage(localSelected);
    }

    // 获取系统语言
    public static String getDisplayOfSystemLanguage(Context context) {
        Locale localeSystem = getSystemLocale();
        Locale localSelected = getLocale();
        return localeSystem.getDisplayLanguage(localSelected);
    }

    // sp获取本地存储语言类型
    private static int getPersistedLanguageType(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getInt(SELECTED_LANGUAGE, LANGUAGE_TYPE_DEFAULT);
    }

    // sp存储本地语言类型
    private static void putPersistedLanguageType(Context context, int type) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor edit = sp.edit();
        edit.putInt(SELECTED_LANGUAGE, type);
        edit.commit();
    }

    // 设置locale
    private static Context setLocaleOnAttach(Context context, int type) {
        Locale locale = getLocaleByType(context, type, true);
        putPersistedLanguageType(context, type);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return updateResources(context, locale);
        }
        return updateResourcesLegacy(context, locale);
    }
    // 设置locale
    private static Context setLocale(Context context, int type) {
        Locale locale = getLocaleByType(context, type, false);
        putPersistedLanguageType(AppApplication.getAppContext(), type);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            AppApplication.getAppContext().getResources().getConfiguration().setLocale(locale);
            return updateResources(context, locale);
        }
        return updateResourcesLegacy(context, locale);
    }

    @TargetApi(Build.VERSION_CODES.N)
    private static Context updateResources(Context context, Locale locale) {
        Configuration configuration = context.getResources().getConfiguration();
        configuration.setLocale(locale);
        configuration.setLayoutDirection(locale);
        return context.createConfigurationContext(configuration);
    }

    @SuppressWarnings("deprecation")
    private static Context updateResourcesLegacy(Context context, Locale locale) {
        Resources resources = context.getResources();
        Configuration configuration = resources.getConfiguration();
        configuration.locale = locale;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            configuration.setLayoutDirection(locale);
        }
        resources.updateConfiguration(configuration, resources.getDisplayMetrics());
        return context;
    }

    // 获取系统locale
    private static Locale getSystemLocale() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            return getSystemLocaleN();
        return getSystemLocaleLegacy();
    }

    @TargetApi(Build.VERSION_CODES.N)
    private static Locale getSystemLocaleN() {
        Locale locale;
        int type = getPersistedLanguageType(AppApplication.getAppContext());

        LocaleList localeList = LocaleList.getDefault();
        // 如果app已选择不跟随系统语言，则取第二个数据为系统默认语言
        if (type != LANGUAGE_TYPE_DEFAULT && localeList.size() > 1) {
            locale = localeList.get(FIRST_LOCAL_NON_DEFAULT);
        } else {
            locale = localeList.get(FIRST_LOCAL_DEFAULT);
        }
        return locale;
    }

    @SuppressWarnings("deprecation")
    private static Locale getSystemLocaleLegacy() {
        Locale locale;
        locale = Locale.getDefault();
        return locale;
    }

    // 获取locale
    private static Locale getLocale() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            return getLocaleN();
        return getLocaleLegacy();
    }

    @TargetApi(Build.VERSION_CODES.N)
    private static Locale getLocaleN() {
        Locale locale;
        int type = getPersistedLanguageType(AppApplication.getAppContext());
        switch (type) {
            case LANGUAGE_TYPE_DEFAULT:
                LocaleList localeList = LocaleList.getDefault();
                locale = localeList.get(FIRST_LOCAL_DEFAULT);
                break;
            case LANGUAGE_TYPE_CHINESE:
                locale = Locale.CHINESE;
                break;
            case LANGUAGE_TYPE_THAILAND:
                locale = new Locale("th");
                break;
            case LANGUAGE_TYPE_ENGLISH:
            default:
                locale = Locale.ENGLISH;
                break;
        }
        return locale;
    }

    @SuppressWarnings("deprecation")
    private static Locale getLocaleLegacy() {
        Locale locale;
        int type = getPersistedLanguageType(AppApplication.getAppContext());
        switch (type) {
            case LANGUAGE_TYPE_DEFAULT:
                locale = Locale.getDefault();
                break;
            case LANGUAGE_TYPE_CHINESE:
                locale = Locale.CHINESE;
                break;
            case LANGUAGE_TYPE_THAILAND:
                locale = new Locale("th");
                break;
            case LANGUAGE_TYPE_ENGLISH:
            default:
                locale = Locale.ENGLISH;
                break;
        }
        return locale;
    }

    // 根据type获取locale
    private static Locale getLocaleByType(Context context, int type, boolean bInit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            return getLocaleByTypeN(context, type, bInit);
        return getLocaleByTypeLegacy(type);
    }

    @TargetApi(Build.VERSION_CODES.N)
    private static Locale getLocaleByTypeN(Context context, int type, boolean bInit) {
        Locale locale;
        switch (type) {
            case LANGUAGE_TYPE_DEFAULT:
                LocaleList localeList = LocaleList.getDefault();
                int spType;
                if (bInit)
                    spType = getPersistedLanguageType(context);
                else
                    spType = getPersistedLanguageType(AppApplication.getAppContext());
                // 如果app已选择不跟随系统语言，则取第二个数据为系统默认语言
                if (spType != LANGUAGE_TYPE_DEFAULT && localeList.size() > 1) {
                    Log.e(TAG, "不跟随系统语言");
                    locale = localeList.get(FIRST_LOCAL_NON_DEFAULT);
                } else {
                    Log.e(TAG, "跟随系统语言");
                    locale = localeList.get(FIRST_LOCAL_DEFAULT);
                }
                break;
            case LANGUAGE_TYPE_CHINESE:
                locale = Locale.CHINESE;
                break;
            case LANGUAGE_TYPE_THAILAND:
                locale = new Locale("th");
                break;
            case LANGUAGE_TYPE_ENGLISH:
            default:
                locale = Locale.ENGLISH;
                break;
        }
        return locale;
    }

    @SuppressWarnings("deprecation")
    private static Locale getLocaleByTypeLegacy(int type) {
        Locale locale;
        switch (type) {
            case LANGUAGE_TYPE_DEFAULT:
                locale = Locale.getDefault();
                break;
            case LANGUAGE_TYPE_CHINESE:
                locale = Locale.CHINESE;
                break;
            case LANGUAGE_TYPE_THAILAND:
                locale = new Locale("th");
                break;
            case LANGUAGE_TYPE_ENGLISH:
            default:
                locale = Locale.ENGLISH;
                break;
        }
        return locale;
    }

    // 判断是否是相同语言
    @SuppressWarnings("unused")
    private static boolean isSameLanguage(Context context) {
        int type = getPersistedLanguageType(AppApplication.getAppContext());
        return isSameLanguage(context, type);
    }

    // 判断是否是相同语言
    @SuppressWarnings("deprecation")
    private static boolean isSameLanguage(Context context, int type) {
        Locale locale = getLocaleByType(context, type, false);
        Locale appLocale;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            appLocale = context.getResources().getConfiguration().getLocales().get(0);
        } else {
            appLocale = context.getResources().getConfiguration().locale;
        }
        boolean equals = appLocale.equals(locale);
        Log.e(TAG, "isSameLanguage: " + locale.toString() + " / " + appLocale.toString() + " / " + equals);
        return equals;
    }

    // 跳转主页
    private static void toRestartMainActivity(Activity activity) {
        Intent intent = new Intent(activity, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        activity.startActivity(intent);
        // 杀掉进程，如果是跨进程则杀掉当前进程
//        android.os.Process.killProcess(android.os.Process.myPid());
//        System.exit(0);
    }
}
