package cn.suzhou.xj107359.i18n.base;

import android.app.Application;
import android.content.Context;

import cn.suzhou.xj107359.i18n.utils.LocaleHelper;

public class AppApplication extends Application {

    private static AppApplication baseApplication;

    @Override
    public void onCreate() {
        super.onCreate();
        baseApplication = this;
    }

    @Override
    protected void attachBaseContext(Context base) {
//        super.attachBaseContext(base);
        super.attachBaseContext(LocaleHelper.onAttachApplication(base));
    }

    public static Context getAppContext() {
        return baseApplication;
    }
}
