package yzx.gogoPlayer.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import yzx.gogoPlayer.AppApplication;
import yzx.gogoPlayer.R;
import yzx.gogoPlayer.widget.AppTitleBar;

/**
 * Created by Administrator on 2016/9/14
 */
public abstract class BaseActivity extends AppCompatActivity{

    private AppTitleBar titleBar;
    public AppTitleBar getTitleBar() {
        return titleBar;
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        /* check sign checked */
        AppApplication.checkChecked();

        /* set ToolBar */
        titleBar= (AppTitleBar) findViewById(R.id.app_title_bar);
        if(titleBar != null) {
            setSupportActionBar(titleBar);
            titleBar.setNavigationOnClickListener(view ->{
                finish();
            });
        }


    }

}
