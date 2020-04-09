package cn.suzhou.xj107359.i18n.base;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import cn.suzhou.xj107359.i18n.utils.LocaleHelper;

public abstract class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 兼容启动页没有布局的情况
        if (getLayoutId() > 0) {
            setContentView(getLayoutId());
        }

        this.initView();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
//        super.attachBaseContext(newBase);
        super.attachBaseContext(LocaleHelper.onAttachActivity(newBase));
    }

    /*********************子类实现*****************************/
    //获取布局文件
    protected abstract int getLayoutId();

    //初始化view
    protected abstract void initView();
}
