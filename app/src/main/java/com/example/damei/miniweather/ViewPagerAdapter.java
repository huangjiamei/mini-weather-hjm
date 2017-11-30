package com.example.damei.miniweather;

/**
 * Created by damei on 17/11/30.
 */

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by damei on 17/11/29.
 */

public class ViewPagerAdapter extends PagerAdapter{
    private List<View> views; //声明一个view集合
    private Context context; //声明一个Context对象

    //定义ViewPagerAdapter构造函数，用于初始化veiws和context
    public ViewPagerAdapter(List<View> views, Context context) {
        this.views = views;
        this.context = context;
    }

    //重写方法
    //返回滑动的视图个数
    @Override
    public int getCount() {
        //return 0;
        return views.size();
    }

    //创建position所在位置的视图
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        //return super.instantiateItem(container, position);
        //添加position位置的视图，并返回这个视图对象
        container.addView(views.get(position));
        return views.get(position);
    }

    //删除position位置所指定的视图
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        //super.destroyItem(container, position, object);
        container.removeView(views.get(position));
    }

    //判断instantiateItem返回对象是否与当前View代表的是同一个对象
    @Override
    public boolean isViewFromObject(View view, Object object) {
        //return false;
        return (view == object);
    }

}

