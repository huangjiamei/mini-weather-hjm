package cn.edu.pku.huangjiamei.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by damei on 17/10/17.
 */

public class NetUtil {
    public static final int NETWORK_NONE = 0;
    public static final int NETWORK_WIFI = 1;
    public static final int NETWORK_MOBILE = 2;
    //检测网络连接状态方法
    public static int getNetworkState(Context context) {
        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService((Context.CONNECTIVITY_SERVICE));
        NetworkInfo networkInfo = connManager.getActiveNetworkInfo();
        if(networkInfo == null)
            return NETWORK_NONE;
        int nType = networkInfo.getType();
        if(nType == ConnectivityManager.TYPE_MOBILE)
            return NETWORK_MOBILE;
        else if(nType == ConnectivityManager.TYPE_WIFI)
            return NETWORK_WIFI;
        return NETWORK_NONE;
    }
}
