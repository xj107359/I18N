package cn.suzhou.xj107359.i18n.ui;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;

import cn.suzhou.xj107359.i18n.R;
import cn.suzhou.xj107359.i18n.base.BaseActivity;
import cn.suzhou.xj107359.i18n.utils.LocaleHelper;
import cn.suzhou.xj107359.i18n.utils.PopUtils;

public class MainActivity extends BaseActivity {
    private PopupWindow mPopupWindow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);

        TextView toSelectLanguage = findViewById(R.id.tv_select_language);
        toSelectLanguage.setOnClickListener(
                v -> showSelectPop()
        );
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    public void initView() {

    }

    private void showSelectPop() {
        // 设置contentView
        View contentView = LayoutInflater.from(this)
                .inflate(R.layout.pop_select_language, null);
        mPopupWindow = new PopupWindow(contentView);
        mPopupWindow.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        mPopupWindow.setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        //设置点击事件
        TextView tvDefault = contentView.findViewById(R.id.tv_default_language);
        tvDefault.setText(
                String.format(getResources().getString(R.string.defualt_language_detail),
                        LocaleHelper.getDisplayOfSystemLanguage(this))
        );
        tvDefault.setOnClickListener(v -> {
            mPopupWindow.dismiss();
            toSetLanguage(LocaleHelper.LANGUAGE_TYPE_DEFAULT);
        });
        contentView.findViewById(R.id.tv_english).setOnClickListener(v -> {
            mPopupWindow.dismiss();
            toSetLanguage(LocaleHelper.LANGUAGE_TYPE_ENGLISH);
        });

        contentView.findViewById(R.id.tv_chinese).setOnClickListener(v -> {
            mPopupWindow.dismiss();
            toSetLanguage(LocaleHelper.LANGUAGE_TYPE_CHINESE);
        });

        contentView.findViewById(R.id.tv_thai).setOnClickListener(v -> {
            mPopupWindow.dismiss();
            toSetLanguage(LocaleHelper.LANGUAGE_TYPE_THAILAND);
        });

        // 外部是否可以点击
        mPopupWindow.setBackgroundDrawable(new ColorDrawable(0x00000000));
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.setFocusable(true);
        //设置动画
        mPopupWindow.setAnimationStyle(R.style.betSharePopAnim);
        PopUtils.setBackgroundAlpha(this, 0.5f);//设置屏幕透明度
        // 显示PopupWindow
        mPopupWindow.showAtLocation(contentView, Gravity.BOTTOM | Gravity.START, 0, 0);
        contentView.setOnClickListener(v -> mPopupWindow.dismiss());
        mPopupWindow.setOnDismissListener(() -> {
            // popupWindow隐藏时恢复屏幕正常透明度
            PopUtils.setBackgroundAlpha(MainActivity.this, 1.0f);
        });
    }

    private void toSetLanguage(int languageType) {
        LocaleHelper.setLanguage(this, languageType);
    }
}
