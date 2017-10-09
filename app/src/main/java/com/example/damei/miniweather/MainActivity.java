package com.example.damei.miniweather;

import android.app.Activity;
import android.os.Bundle;

/**
 * Created by damei on 17/9/27.
 */

public class MainActivity extends Activity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weather_info); //加载布局
    }
}
