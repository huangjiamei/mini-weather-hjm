package com.example.damei.miniweather;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by damei on 17/11/29.
 * 引导页面
 */

public class Guide extends Activity implements ViewPager.OnPageChangeListener{

    private ViewPagerAdapter vpAdapter; //声明
    private ViewPager vp;
    private List<View> views;

    private ImageView[] dots; //声明一个ImageView数组
    private int[] ids = {R.id.iv1, R.id.iv2, R.id.iv3}; //存放三个小圆点控件的id

    private Button btn; //声明


    //重写onCreate方法
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.guide); //绑定布局
        initViews(); //调用初始化方法
        initDots();

         /*
        在ViewPager中增加Button控件
         */
        //初始化Button，先找到相应的视图，再调用findViewById方法，因为是第三个视图，所以是2
        btn = (Button) views.get(2).findViewById(R.id.btn);
        //增加onClick监听器，实例化一个Intent对象，单击时跳转到MainActivity
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Guide.this, MainActivity.class);
                startActivity(i);
                finish();
            }
        });

    }

    //将三个小圆点控件对象，存入dots数组
    void initDots(){
        dots = new ImageView[views.size()];
        for(int i=0; i<views.size(); i++)
            dots[i] = (ImageView) findViewById(ids[i]);
    }

    private void initViews(){
        //通过LayoutInflater来动态加载这些视图
        LayoutInflater inflater = LayoutInflater.from(this); //获取LayoutInflater对象
        views = new ArrayList<View>(); //构造View类型的数组
        //向views集合中加入三个view对象
        //通过LayoutInflater动态加载布局文件并转化的
        views.add(inflater.inflate(R.layout.page1, null));
        views.add(inflater.inflate(R.layout.page2, null));
        views.add(inflater.inflate(R.layout.page3, null));
        //实例化一个ViewPagerAdapter对象
        vpAdapter = new ViewPagerAdapter(views, this);
        //通过findViewById方法获取ViewPager对象
        vp = (ViewPager) findViewById(R.id.viewpager);
        //设置ViewPager的适配器
        vp.setAdapter(vpAdapter);

        //为ViewPager控件设置页面变化的监听事件
        vp.setOnPageChangeListener(this);
    }

    /*
    增加页面变化的监听事件，动态地修改三个导航小圆点的属性
     */

    //重写ViewPager.OnPageChangeListener接口的方法
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    //视图发生变化后调用此方法，动态修改小圆点属性，实现相应的导航效果
    @Override
    public void onPageSelected(int position) {
        for(int a=0; a<ids.length; a++){
            //若是选中的视图，则图片src属性为被选中的效果
            if(a == position)
                dots[a].setImageResource(R.drawable.page_indicator_focused);
            else
                dots[a].setImageResource(R.drawable.page_indicator_unfocused);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
