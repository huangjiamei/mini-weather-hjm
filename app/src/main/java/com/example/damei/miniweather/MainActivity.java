package com.example.damei.miniweather;

import android.app.Activity;
import android.app.Notification;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.os.Message;
import android.content.Intent;

import cn.edu.pku.huangjiamei.bean.TodayWeather;
import cn.edu.pku.huangjiamei.util.NetUtil;

/**
 * Created by damei on 17/9/27.
 */

public class MainActivity extends Activity implements View.OnClickListener, ViewPager.OnPageChangeListener{

    private static final int UPDATE_TODAY_WEATHER = 1;

    private ImageView mUpdateBtn; //更新按钮ImageView

    private ImageView mCitySelect; //选择城市ImageView

    //一周七天的天气
    ArrayList<TodayWeather> weatherArrayList;

    //private ProgressBar mProgressAnim;

    //定义"更新今日天气数据"所需的相关控件对象
    private TextView cityTv, timeTv, humidityTv, weekTv, pmDataTv, pmQualityTv, temperatureTv, climateTv, windTv, city_name_Tv;
    private  ImageView weatherImg, pmImg;

    //定义未来6天天气所需的变量
    private ViewPager viewPager;
    private ViewPagerAdapter viewPagerAdapter;
    private List<View> views;
    private ImageView[] dots;
    private int[] ids = {R.id.iv1_weather, R.id.iv2_weather};

    private TextView w1_date1, w1_temperature1, w1_climate1, w1_wind1;
    private ImageView w1_img1;
    private TextView w1_date2, w1_temperature2, w1_climate2, w1_wind2;
    private ImageView w1_img2;
    private TextView w1_date3, w1_temperature3, w1_climate3, w1_wind3;
    private ImageView w1_img3;
    private TextView w2_date1, w2_temperature1, w2_climate1, w2_wind1;
    private ImageView w2_img1;
    private TextView w2_date2, w2_temperature2, w2_climate2, w2_wind2;
    private ImageView w2_img2;
    private TextView w2_date3, w2_temperature3, w2_climate3, w2_wind3;
    private ImageView w2_img3;


    //通过消息机制，将解析的天气对象，通过消息发送给主线程，主线程接收到消息数据后，调用updateTodayWeather函数，更新UI界面上的数据
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UPDATE_TODAY_WEATHER:
                    //updateTodayWeather((TodayWeather) msg.obj);
                    updateTodayWeather((ArrayList<TodayWeather>) msg.obj);
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weather_info); //加载布局

        //为更新按钮ImageView添加单击事件
        mUpdateBtn = (ImageView) findViewById(R.id.title_update_btn);
        mUpdateBtn.setOnClickListener(this);

        //为选择城市ImageView添加单击事件
        mCitySelect = (ImageView) findViewById(R.id.title_city_manager);
        mCitySelect.setOnClickListener(this);

        //mProgressAnim = (ProgressBar) findViewById(R.id.title_update_progress);

        //调用检测网络连接状态方法getNetworkState()
        if(NetUtil.getNetworkState(this) != NetUtil.NETWORK_NONE){
            Log.d("myWeather", "网络OK");
            Toast.makeText(MainActivity.this,"网络OK！", Toast.LENGTH_LONG).show();
        }else{
            Log.d("myWeather", "网络挂了");
            Toast.makeText(MainActivity.this,"网络挂了！", Toast.LENGTH_LONG).show();
        }

        //调用initView()方法
        initView();

        initViews();

        initDots();

    }

    //初始化界面控件的内容
    void initView(){
        city_name_Tv = (TextView) findViewById(R.id.title_city_name);
        cityTv = (TextView) findViewById(R.id.city);
        timeTv = (TextView) findViewById(R.id.time);
        humidityTv = (TextView) findViewById(R.id.humidity);
        weekTv = (TextView) findViewById(R.id.week_today);
        pmDataTv = (TextView) findViewById(R.id.pm_data);
        pmQualityTv = (TextView) findViewById(R.id.pm2_5_quality);
        pmImg = (ImageView) findViewById(R.id.pm2_5_img);
        temperatureTv = (TextView) findViewById(R.id.temperature);
        climateTv = (TextView) findViewById(R.id.climate);
        windTv = (TextView) findViewById(R.id.wind);
        weatherImg = (ImageView) findViewById(R.id.weather_img);
        city_name_Tv.setText("N/A");
        cityTv.setText("N/A");
        timeTv.setText("N/A");
        humidityTv.setText("N/A");
        pmDataTv.setText("N/A");
        pmQualityTv.setText("N/A");
        weekTv.setText("N/A");
        temperatureTv.setText("N/A");
        climateTv.setText("N/A");
        windTv.setText("N/A");
    }

    private void initViews(){
        //通过LayoutInflater来动态加载这些视图
        LayoutInflater inflater = LayoutInflater.from(this); //获取LayoutInflater对象
        views = new ArrayList<View>(); //构造View类型的数组
        //向views集合中加入三个view对象
        //通过LayoutInflater动态加载布局文件并转化的
        views.add(inflater.inflate(R.layout.page1_weather, null));
        views.add(inflater.inflate(R.layout.page2_weather, null));
        //实例化一个ViewPagerAdapter对象
        viewPagerAdapter = new ViewPagerAdapter(views, this);
        //通过findViewById方法获取ViewPager对象
        viewPager = (ViewPager) findViewById(R.id.viewpager_weather);
        //设置ViewPager的适配器
        viewPager.setAdapter(viewPagerAdapter);
        //为ViewPager控件设置页面变化的监听事件
        viewPager.setOnPageChangeListener(this);

        //初始化所有控件
        w1_date1 = (TextView)views.get(0).findViewById(R.id.w1_date1);
        w1_temperature1 = (TextView)views.get(0).findViewById(R.id.w1_temperature1);
        w1_climate1 = (TextView)views.get(0).findViewById(R.id.w1_climate1);
        w1_wind1 = (TextView)views.get(0).findViewById(R.id.w1_wind1);
        w1_img1 = (ImageView)views.get(0).findViewById(R.id.w1_img1);

        w1_date2 = (TextView)views.get(0).findViewById(R.id.w1_date2);
        w1_temperature2 = (TextView)views.get(0).findViewById(R.id.w1_temperature2);
        w1_climate2 = (TextView)views.get(0).findViewById(R.id.w1_climate2);
        w1_wind2 = (TextView)views.get(0).findViewById(R.id.w1_wind2);
        w1_img2 = (ImageView)views.get(0).findViewById(R.id.w1_img2);

        w1_date3 = (TextView)views.get(0).findViewById(R.id.w1_date3);
        w1_temperature3 = (TextView)views.get(0).findViewById(R.id.w1_temperature3);
        w1_climate3 = (TextView)views.get(0).findViewById(R.id.w1_climate3);
        w1_wind3 = (TextView)views.get(0).findViewById(R.id.w1_wind3);
        w1_img3 = (ImageView)views.get(0).findViewById(R.id.w1_img3);

        w2_date1 = (TextView)views.get(1).findViewById(R.id.w2_date1);
        w2_temperature1 = (TextView)views.get(1).findViewById(R.id.w2_temperature1);
        w2_climate1 = (TextView)views.get(1).findViewById(R.id.w2_climate1);
        w2_wind1 = (TextView)views.get(1).findViewById(R.id.w2_wind1);
        w2_img1 = (ImageView)views.get(1).findViewById(R.id.w2_img1);

        w2_date2 = (TextView)views.get(1).findViewById(R.id.w2_date2);
        w2_temperature2 = (TextView)views.get(1).findViewById(R.id.w2_temperature2);
        w2_climate2 = (TextView)views.get(1).findViewById(R.id.w2_climate2);
        w2_wind2 = (TextView)views.get(1).findViewById(R.id.w2_wind2);
        w2_img2 = (ImageView)views.get(1).findViewById(R.id.w2_img2);

        w2_date3 = (TextView)views.get(1).findViewById(R.id.w2_date3);
        w2_temperature3 = (TextView)views.get(1).findViewById(R.id.w2_temperature3);
        w2_climate3 = (TextView)views.get(1).findViewById(R.id.w2_climate3);
        w2_wind3 = (TextView)views.get(1).findViewById(R.id.w2_wind3);
        w2_img3 = (ImageView)views.get(1).findViewById(R.id.w2_img3);
    }

    //将三个小圆点控件对象，存入dots数组
    void initDots(){
        dots = new ImageView[views.size()];
        for(int i=0; i<views.size(); i++)
            dots[i] = (ImageView) findViewById(ids[i]);
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

    //解析函数，解析出城市名称以及更新时间信息
    //7.3修改解析函数，将解析的数据存入TodayWeather对象中
    private ArrayList<TodayWeather> parseXML(String xmldata) {

        // 获取当前时间，判断是白天还是晚上
        SimpleDateFormat sdf = new SimpleDateFormat("HH");
        String strHour = sdf.format(new Date());
        int hour = Integer.parseInt(strHour);
        boolean isDay;
        if( hour >= 0 && hour < 18){
            isDay = true;
        }else{
            isDay = false;
        }

        TodayWeather todayWeather = null;
        ArrayList<TodayWeather> weathersList = new ArrayList<TodayWeather>();

        int fengxiangCount = 0;
        int fengliCount = 0;
        int dateCount = 0;
        int highCount = 0;
        int lowCount = 0;
        int typeCount = 0;

        try{
            XmlPullParserFactory fac = XmlPullParserFactory.newInstance();
            XmlPullParser xmlPullParser = fac.newPullParser();
            xmlPullParser.setInput(new StringReader(xmldata));
            int eventType = xmlPullParser.getEventType();
            Log.d("myWeather", "parseXML");
            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    //判断当前事件是否为文档开始事件
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    //判断当前事件是否为标签元素开始事件
                    case XmlPullParser.START_TAG:
                        //根元素，创建一个天气对象
                        if(xmlPullParser.getName().equals("resp")){
                            todayWeather = new TodayWeather();
                        }
                        // 未来天气元素，创建一个天气对象
                        if(xmlPullParser.getName().equals("weather") && !weathersList.isEmpty()){
                            todayWeather = (TodayWeather) todayWeather.clone();
                        }
                        //判断todayWeather对象是否为空
                        if(todayWeather != null) {
                            if (xmlPullParser.getName().equals("city")) {
                                eventType = xmlPullParser.next();
                                //Log.d("myWeather", "city:    "+xmlPullParser.getText());
                                todayWeather.setCity(xmlPullParser.getText());
                            } else if (xmlPullParser.getName().equals("updatetime")) {
                                eventType = xmlPullParser.next();
                                //Log.d("myWeather", "updatetime:    "+xmlPullParser.getText());
                                todayWeather.setUpdatetime(xmlPullParser.getText());
                            } else if (xmlPullParser.getName().equals("shidu")) {
                                eventType = xmlPullParser.next();
                                //Log.d("myWeather", "shidu:    "+xmlPullParser.getText());
                                todayWeather.setShidu(xmlPullParser.getText());
                            } else if (xmlPullParser.getName().equals("wendu")) {
                                eventType = xmlPullParser.next();
                                //Log.d("myWeather", "wendu:    "+xmlPullParser.getText());
                                todayWeather.setWendu(xmlPullParser.getText());
                            } else if (xmlPullParser.getName().equals("pm25")) {
                                eventType = xmlPullParser.next();
                                //Log.d("myWeather", "pm25:    "+xmlPullParser.getText());
                                todayWeather.setPm25(xmlPullParser.getText());
                            } else if (xmlPullParser.getName().equals("quality")) {
                                eventType = xmlPullParser.next();
                                //Log.d("myWeather", "quality:    "+xmlPullParser.getText());
                                todayWeather.setQuality(xmlPullParser.getText());
                            } /*else if (xmlPullParser.getName().equals("fengxiang") && fengxiangCount == 0) {
                                eventType = xmlPullParser.next();
                                //Log.d("myWeather", "fengxiang:    "+xmlPullParser.getText());
                                todayWeather.setFengxiang(xmlPullParser.getText());
                                fengxiangCount++;
                            } else if (xmlPullParser.getName().equals("fengli") && fengliCount == 0) {
                                eventType = xmlPullParser.next();
                                //Log.d("myWeather", "fengli:    "+xmlPullParser.getText());
                                todayWeather.setFengli(xmlPullParser.getText());
                                fengliCount++;
                            }  else if (xmlPullParser.getName().equals("date") && dateCount == 0) {
                                eventType = xmlPullParser.next();
                                //Log.d("myWeather", "date:    "+xmlPullParser.getText());
                                todayWeather.setDate(xmlPullParser.getText());
                                dateCount++;
                            } else if (xmlPullParser.getName().equals("high") && highCount == 0) {
                                eventType = xmlPullParser.next();
                                //Log.d("myWeather", "high:    "+xmlPullParser.getText());
                                todayWeather.setHigh(xmlPullParser.getText());
                                highCount++;
                            }else if (xmlPullParser.getName().equals("low") && lowCount == 0) {
                                eventType = xmlPullParser.next();
                                //Log.d("myWeather", "low:    "+xmlPullParser.getText());
                                todayWeather.setLow(xmlPullParser.getText());
                                lowCount++;
                            } else if (xmlPullParser.getName().equals("type") && typeCount == 0) {
                                eventType = xmlPullParser.next();
                                //Log.d("myWeather", "type:    "+xmlPullParser.getText());
                                todayWeather.setType(xmlPullParser.getText());
                                typeCount++;
                            }*/else if (xmlPullParser.getName().equals("fengxiang")) {
                                eventType = xmlPullParser.next();
                                if( isDay && fengxiangCount == 0){
                                    todayWeather.setFengxiang(xmlPullParser.getText());
                                }
                                if( !isDay && fengxiangCount == 1){
                                    todayWeather.setFengxiang(xmlPullParser.getText());
                                }
                                fengxiangCount++;
                            } else if (xmlPullParser.getName().equals("fx_1")) {
                                eventType = xmlPullParser.next();
                                if( isDay && fengxiangCount == 0){
                                    todayWeather.setFengxiang(xmlPullParser.getText());
                                }
                                if( !isDay && fengxiangCount == 1){
                                    todayWeather.setFengxiang(xmlPullParser.getText());
                                }
                                fengxiangCount++;
                            } else if (xmlPullParser.getName().equals("fengli")) {
                                eventType = xmlPullParser.next();
                                //todayWeather.setFengli(xmlPullParser.getText());
                                if( isDay && fengliCount == 0 ){
                                    todayWeather.setFengli(xmlPullParser.getText());
                                }else
                                if( !isDay && fengliCount == 1 ){
                                    todayWeather.setFengli(xmlPullParser.getText());
                                }
                                fengliCount++;
                            } else if (xmlPullParser.getName().equals("fl_1") ) {
                                eventType = xmlPullParser.next();
                                //todayWeather.setFengxiang(xmlPullParser.getText());
                                if( isDay && fengliCount == 0 ){
                                    todayWeather.setFengli(xmlPullParser.getText());
                                }
                                if( !isDay && fengliCount == 1 ){
                                    todayWeather.setFengli(xmlPullParser.getText());
                                }
                                fengxiangCount++;
                            } else if (xmlPullParser.getName().equals("date")) {
                                eventType = xmlPullParser.next();
                                todayWeather.setDate(xmlPullParser.getText());
                                dateCount++;
                            } else if (xmlPullParser.getName().equals("date_1")) {
                                eventType = xmlPullParser.next();
                                todayWeather.setDate(xmlPullParser.getText());
                                dateCount++;
                            } else if (xmlPullParser.getName().equals("high")) {
                                eventType = xmlPullParser.next();
                                todayWeather.setHigh(xmlPullParser.getText().substring(2).trim());
                                highCount++;
                            } else if (xmlPullParser.getName().equals("high_1")) {
                                eventType = xmlPullParser.next();
                                todayWeather.setHigh(xmlPullParser.getText().substring(2).trim());
                                highCount++;
                            } else if (xmlPullParser.getName().equals("low")) {
                                eventType = xmlPullParser.next();
                                todayWeather.setLow(xmlPullParser.getText().substring(2).trim());
                                lowCount++;
                            } else if (xmlPullParser.getName().equals("low_1")) {
                                eventType = xmlPullParser.next();
                                todayWeather.setLow(xmlPullParser.getText().substring(2).trim());
                                lowCount++;
                            }else if (xmlPullParser.getName().equals("type")) {
                                eventType = xmlPullParser.next();
                                //todayWeather.setType(xmlPullParser.getText());
                                if( isDay && typeCount == 0){
                                    todayWeather.setType(xmlPullParser.getText());
                                }
                                if( !isDay && typeCount == 1){
                                    todayWeather.setType(xmlPullParser.getText());
                                }
                                typeCount++;
                            } else if (xmlPullParser.getName().equals("type_1") ) {
                                eventType = xmlPullParser.next();
                                //todayWeather.setType(xmlPullParser.getText());
                                if( isDay && typeCount == 0){
                                    todayWeather.setType(xmlPullParser.getText());
                                }
                                if( !isDay && typeCount == 1){
                                    todayWeather.setType(xmlPullParser.getText());
                                }
                                typeCount++;
                            }
                        }
                        break;
                    //判断当前事件是否为标签元素结束事件
                    case XmlPullParser.END_TAG:

                        if(xmlPullParser.getName().equals("weather") || xmlPullParser.getName().equals("yesterday")) {
                            Log.d("myWeather",todayWeather.toString());
                            fengliCount = 0;
                            fengxiangCount = 0;
                            typeCount = 0;
                            weathersList.add(todayWeather);
                        }

                        break;
                }
                //进入下一个元素并触发相应事件
                eventType = xmlPullParser.next();
            }
        }catch (XmlPullParserException e) {
            e.printStackTrace();
        }catch (IOException e) {
            e.printStackTrace();
        }
        //return todayWeather;
        return weathersList;
    }

    //利用TodayWeather对象更新UI中的控件
    /*
    void updateTodayWeather(TodayWeather todayWeather) {
        city_name_Tv.setText(todayWeather.getCity());
        cityTv.setText(todayWeather.getCity());
        timeTv.setText(todayWeather.getUpdatetime() + "发布");
        humidityTv.setText("湿度：" + todayWeather.getShidu());
        pmDataTv.setText(todayWeather.getPm25());
        pmQualityTv.setText(todayWeather.getQuality());
        weekTv.setText(todayWeather.getDate());
        temperatureTv.setText(todayWeather.getHigh().substring(3) + '~' + todayWeather.getLow().substring(3) + '℃');
        climateTv.setText(todayWeather.getType());
        windTv.setText("风力：" + todayWeather.getFengli());
        Toast.makeText(MainActivity.this, "更新成功！", Toast.LENGTH_SHORT).show();
    }
    */

    void updateTodayWeather(ArrayList<TodayWeather> weatherArrayList) {
        city_name_Tv.setText(weatherArrayList.get(1).getCity());
        cityTv.setText(weatherArrayList.get(1).getCity());
        timeTv.setText(weatherArrayList.get(1).getUpdatetime() + "发布");
        humidityTv.setText("湿度：" + weatherArrayList.get(1).getShidu());
        pmDataTv.setText(weatherArrayList.get(1).getPm25());
        pmQualityTv.setText(weatherArrayList.get(1).getQuality());
        weekTv.setText(weatherArrayList.get(1).getDate());
        temperatureTv.setText(weatherArrayList.get(1).getHigh()+ '~' + weatherArrayList.get(1).getLow());
        climateTv.setText(weatherArrayList.get(1).getType());
        windTv.setText("风力：" + weatherArrayList.get(1).getFengli());
        Toast.makeText(MainActivity.this, "更新成功！", Toast.LENGTH_SHORT).show();

        /*
        // 将本次查询得到的数据存入SharedPreferences，方便下次初始化时使用
        SharedPreferences settings = getSharedPreferences("dm", MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("city_name_Tv",city_name_Tv.getText().toString());
        editor.putString("cityTv",cityTv.getText().toString());
        editor.putString("timeTv",timeTv.getText().toString());
        editor.putString("humidityTv",humidityTv.getText().toString());
        editor.putString("pmDataTv",pmDataTv.getText().toString());
        editor.putString("pmQualityTv",pmQualityTv.getText().toString());
        editor.putString("weekTv",weekTv.getText().toString());
        editor.putString("temperatureTv",temperatureTv.getText().toString());
        editor.putString("climateTv",climateTv.getText().toString());
        editor.putString("windTv",windTv.getText().toString());
        editor.putString("type",weatherArrayList.get(0).getType());
        editor.commit();//提交操作

        */
        updateWeathers(weatherArrayList);
    }


    void updateWeathers(ArrayList<TodayWeather> weathersList){
        w1_climate1.setText(weathersList.get(0).getType());
        w1_date1.setText(weathersList.get(0).getDate());
        w1_temperature1.setText(weathersList.get(0).getHigh()+"~"+weathersList.get(0).getLow());
        w1_wind1.setText(weathersList.get(0).getFengli());
        updateWeatherImage(weathersList.get(0).getType(), w1_img1);

        w1_climate2.setText(weathersList.get(1).getType());
        w1_date2.setText(weathersList.get(1).getDate());
        w1_temperature2.setText(weathersList.get(1).getHigh()+"~"+weathersList.get(1).getLow());
        w1_wind2.setText(weathersList.get(1).getFengli());
        updateWeatherImage(weathersList.get(1).getType(), w1_img2);

        w1_climate3.setText(weathersList.get(2).getType());
        w1_date3.setText(weathersList.get(2).getDate());
        w1_temperature3.setText(weathersList.get(2).getHigh()+"~"+weathersList.get(2).getLow());
        w1_wind3.setText(weathersList.get(2).getFengli());
        updateWeatherImage(weathersList.get(2).getType(), w1_img3);

        w2_climate1.setText(weathersList.get(3).getType());
        w2_date1.setText(weathersList.get(3).getDate());
        w2_temperature1.setText(weathersList.get(3).getHigh()+"~"+weathersList.get(3).getLow());
        w2_wind1.setText(weathersList.get(3).getFengli());
        updateWeatherImage(weathersList.get(3).getType(), w2_img1);

        w2_climate2.setText(weathersList.get(4).getType());
        w2_date2.setText(weathersList.get(4).getDate());
        w2_temperature2.setText(weathersList.get(4).getHigh()+"~"+weathersList.get(4).getLow());
        w2_wind2.setText(weathersList.get(4).getFengli());
        updateWeatherImage(weathersList.get(4).getType(), w2_img2);

        w2_climate3.setText(weathersList.get(5).getType());
        w2_date3.setText(weathersList.get(5).getDate());
        w2_temperature3.setText(weathersList.get(5).getHigh()+"~"+weathersList.get(5).getLow());
        w2_wind3.setText(weathersList.get(5).getFengli());
        updateWeatherImage(weathersList.get(5).getType(), w2_img3);
    }


    void updateWeatherImage(String weatherStr, ImageView weatherImg){
        //weatherImg.setImageResource(R.drawable.biz_plugin_weather_qing);
        if(weatherStr.equals("暴雪")){
            weatherImg.setImageResource(R.drawable.biz_plugin_weather_baoxue);
        }else if(weatherStr.equals("暴雨")){
            weatherImg.setImageResource(R.drawable.biz_plugin_weather_baoyu);
        }else if(weatherStr.equals("大暴雨")){
            weatherImg.setImageResource(R.drawable.biz_plugin_weather_dabaoyu);
        }else if(weatherStr.equals("大雪")){
            weatherImg.setImageResource(R.drawable.biz_plugin_weather_daxue);
        }else if(weatherStr.equals("大雨")){
            weatherImg.setImageResource(R.drawable.biz_plugin_weather_dayu);
        }else if(weatherStr.equals("多云")){
            weatherImg.setImageResource(R.drawable.biz_plugin_weather_duoyun);
        }else if(weatherStr.equals("雷阵雨")){
            weatherImg.setImageResource(R.drawable.biz_plugin_weather_leizhenyu);
        }else if(weatherStr.equals("雷阵雨冰雹")){
            weatherImg.setImageResource(R.drawable.biz_plugin_weather_leizhenyubingbao);
        }else if(weatherStr.equals("晴")){
            weatherImg.setImageResource(R.drawable.biz_plugin_weather_qing);
        }else if(weatherStr.equals("沙尘暴")){
            weatherImg.setImageResource(R.drawable.biz_plugin_weather_shachenbao);
        }else if(weatherStr.equals("特大暴雨")){
            weatherImg.setImageResource(R.drawable.biz_plugin_weather_tedabaoyu);
        }else if(weatherStr.equals("雾")){
            weatherImg.setImageResource(R.drawable.biz_plugin_weather_wu);
        }else if(weatherStr.equals("小雪")){
            weatherImg.setImageResource(R.drawable.biz_plugin_weather_xiaoxue);
        }else if(weatherStr.equals("小雨")){
            weatherImg.setImageResource(R.drawable.biz_plugin_weather_xiaoyu);
        }else if(weatherStr.equals("阴")){
            weatherImg.setImageResource(R.drawable.biz_plugin_weather_yin);
        }else if(weatherStr.equals("雨夹雪")){
            weatherImg.setImageResource(R.drawable.biz_plugin_weather_yujiaxue);
        }else if(weatherStr.equals("阵雪")){
            weatherImg.setImageResource(R.drawable.biz_plugin_weather_zhenxue);
        }else if(weatherStr.equals("阵雨")){
            weatherImg.setImageResource(R.drawable.biz_plugin_weather_zhenyu);
        }else if(weatherStr.equals("中雪")){
            weatherImg.setImageResource(R.drawable.biz_plugin_weather_zhongxue);
        }else if(weatherStr.equals("中雨")){
            weatherImg.setImageResource(R.drawable.biz_plugin_weather_zhongyu);
        }
    }

    //获取网络数据
    private void queryWeatherCode(String cityCode) {
        final String address = "http://wthrcdn.etouch.cn/WeatherApi?citykey=" + cityCode;
        Log.d("myWeather", address);
        //Log.d("myWeather", "666");
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection con = null;
                //TodayWeather todayWeather = null;
                ArrayList<TodayWeather> weathersList = new ArrayList<TodayWeather>();
                try{
                    URL url = new URL(address);
                    con = (HttpURLConnection) url.openConnection();
                    con.setRequestMethod("GET");
                    con.setConnectTimeout(8000);
                    con.setReadTimeout(8000);
                    InputStream in = con.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder response = new StringBuilder();
                    String str;
                    while ((str = reader.readLine()) != null){
                        response.append(str);
                        Log.d("myWeather", str);
                    }
                    //Log.d("myWeather", "666");
                    String responseStr = response.toString();
                    Log.d("myWeather", responseStr);
                    //调用parseXML并返回TodayWeather对象
                    //parseXML(responseStr); //调用解析函数
                    /*
                    todayWeather = parseXML(responseStr);
                    if(todayWeather!=null){
                        Log.d("myWeather", todayWeather.toString());

                        Message msg = new Message();
                        msg.what = UPDATE_TODAY_WEATHER;
                        msg.obj = todayWeather;
                        mHandler.sendMessage(msg);
                    }
                    */
                    weathersList = parseXML(responseStr);
                    //System.out.println(weathersList.size());
                    if(!weathersList.isEmpty()) {
                        Log.d("myWeather", weathersList.get(0).toString());
                        Message msg = new Message();
                        msg.what = UPDATE_TODAY_WEATHER;
                        msg.obj = weathersList;
                        mHandler.sendMessage(msg);
                    }

                }catch (Exception e){
                    e.printStackTrace();
                }finally {
                    if(con != null)
                        con.disconnect();
                }
            }
        }).start();
    }


    //单击事件
    @Override
    public void onClick(View view) {
        //点击选择城市
        if(view.getId() == R.id.title_city_manager){
            Intent i = new Intent(this, SelectCity.class);
            //startActivity(i);
            //返回主界面时，传递城市代码数据
            startActivityForResult(i, 1);
        }

        //点击更新天气信息
        if(view.getId() == R.id.title_update_btn){
            //通过SharedPreferences读取城市id，若没定义默认为北京（101010100）
            SharedPreferences sharedPreferences = getSharedPreferences("config", MODE_PRIVATE);
            String cityCode = sharedPreferences.getString("main_city_code","101010100");
            Log.d("myWeather",cityCode);
            //调用获取网络数据的函数
            if(NetUtil.getNetworkState(this) != NetUtil.NETWORK_NONE){
                Log.d("myWeather","网络OK");
                queryWeatherCode(cityCode);
            }else {
                Log.d("myWeather", "网络挂了");
                Toast.makeText(MainActivity.this,"网络挂了！", Toast.LENGTH_LONG).show();
            }
        }
    }

    //返回主界面时，传递城市代码
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 1 && resultCode == RESULT_OK) {
            String newCityCode = data.getStringExtra("cityCode");
            Log.d("myWeather", "选择的城市代码为" + newCityCode);
            if (NetUtil.getNetworkState(this) != NetUtil.NETWORK_NONE) {
                Log.d("myWeather", "网络OK");
                queryWeatherCode(newCityCode);
            } else {
                Log.d("myWeather", "网络挂了");
                Toast.makeText(MainActivity.this, "网络挂了！", Toast.LENGTH_LONG).show();
            }
        }
    }

}
