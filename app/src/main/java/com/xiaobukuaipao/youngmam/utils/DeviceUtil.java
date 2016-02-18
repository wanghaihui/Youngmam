package com.xiaobukuaipao.youngmam.utils;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by xiaobu1 on 15-5-22.
 */
public class DeviceUtil {

    private Context context;
    public DeviceUtil(Context context) {
        this.context = context;
    }

    // 1 The IMEI
    // only useful for Android Phone(android.permission.READ_PHONE_STATE in Manifest)
    public String getIMEI() {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String imei = telephonyManager.getDeviceId();
        return imei;
    }

    // 2.Pseudo-Unique ID
    // useful for phone/pad
    // 通过取出ROM版本、制造商、CPU型号、以及其他硬件信息来实现这一点。这样计算出来的ID不是唯一的（因为如果两个手机应用了同样的硬件以及Rom镜像）
    public String getPUID(){
        String m_szDevIDShort = "35" + //make this look like a valid IMEI
                Build.BOARD.length()%10 +
                Build.BRAND.length()%10 +
                Build.CPU_ABI.length()%10 +
                Build.DEVICE.length()%10 +
                Build.DISPLAY.length()%10 +
                Build.HOST.length()%10 +
                Build.ID.length()%10 +
                Build.MANUFACTURER.length()%10 +
                Build.MODEL.length()%10 +
                Build.PRODUCT.length()%10 +
                Build.TAGS.length()%10 +
                Build.TYPE.length()%10 +
                Build.USER.length()%10 ; //13 digits
        return m_szDevIDShort;
    }

    // 3.Android ID
    // sometimes it will be null,cause this id can be changed by the manufacturer
    public String getAndroidId() {
        String androidId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        return androidId;
    }

    // 4.The WLAN MAC Address String
    public String getWLANMAC() {
        WifiManager wm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        String wlanMAC = wm.getConnectionInfo().getMacAddress();
        return wlanMAC;
    }

    // 5.the BT MAC Address String
    // need android.permission.BLUETOOTH,or it will return null
    public String getBTMAC() {
        BluetoothAdapter bluetoothAdapter = null;
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        String btMac = bluetoothAdapter.getAddress();
        return btMac;
    }

    // Combined Device ID
    public String getCombinedId() {
        String imei = getIMEI();
        String devIdShort = getPUID();
        String androidId = getAndroidId();
        String wlanMac = getWLANMAC();
        String btMac = getBTMAC();

        String longId = imei + devIdShort + androidId + wlanMac + btMac;

        // Compute MD5
        MessageDigest m = null;
        try {
            m = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        m.update(longId.getBytes(), 0, longId.length());

        // get md5 bytes
        byte md5Data[] = m.digest();
        // create a hex string
        String uniqueId = new String();

        for(int i=0; i < md5Data.length; i++) {
            int b = (0xFF & md5Data[i]);
            // if it is a single digit, make sure it have 0 in front (proper padding)
            if (b <= 0xF) {
                uniqueId += "0";
            }
            // add number to string
            uniqueId += Integer.toHexString(b);
        }

        uniqueId = uniqueId.toUpperCase();

        return uniqueId;
    }

}
