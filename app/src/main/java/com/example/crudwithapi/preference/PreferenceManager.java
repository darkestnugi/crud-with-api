package com.example.crudwithapi.preference;
import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

public class PreferenceManager {
    SharedPreferences pref_global;
    SharedPreferences.Editor editor;
    Context _context;

    // shared pref mode
    int Private_Mode = 0;

    // Shared preferences file name
    private static final String Pref_Global = "mysharedreference";

    public static final String MyToken = "MyToken" ;
    public static final String MyID = "MyID" ;
    public static final String MyName = "MyName" ;
    public static final String MyEmail = "MyEmail" ;
    public static final String MyPosition = "MyPosition" ;
    public static final String MyOffice = "MyOffice" ;
    public static final String MyProvince = "MyProvince" ;
    public static final String MyCity = "MyCity" ;
    public static final String MyFCMToken = "MyFCMToken" ;

    public PreferenceManager(Context context) {
        this._context = context;
        pref_global = _context.getSharedPreferences(Pref_Global, Private_Mode);
        editor = pref_global.edit();
    }

    public void setMyToken(String myToken) {
        editor.putString(MyToken, myToken);
        editor.commit();
    }

    public String getMyToken() {
        return pref_global.getString(MyToken, null);
    }

    public void setMyID(String myID) {
        editor.putString(MyID, myID);
        editor.commit();
    }

    public String getMyID() {
        return pref_global.getString(MyID, null);
    }

    public void setMyName(String myName) {
        editor.putString(MyName, myName);
        editor.commit();
    }

    public String getMyName() {
        return pref_global.getString(MyName, null);
    }

    public void setMyEmail(String myEmail) {
        editor.putString(MyEmail, myEmail);
        editor.commit();
    }

    public String getMyEmail() {
        return pref_global.getString(MyEmail, null);
    }

    public void setMyPosition(String myPosition) {
        editor.putString(MyPosition, myPosition);
        editor.commit();
    }

    public String getMyPosition() {
        return pref_global.getString(MyPosition, null);
    }

    public void setMyOffice(String myOffice) {
        editor.putString(MyOffice, myOffice);
        editor.commit();
    }

    public String getMyOffice() {
        return pref_global.getString(MyOffice, null);
    }

    public void setMyProvince(String myProvince) {
        editor.putString(MyProvince, myProvince);
        editor.commit();
    }

    public String getMyProvince() {
        return pref_global.getString(MyProvince, null);
    }

    public void setMyCity(String myCity) {
        editor.putString(MyCity, myCity);
        editor.commit();
    }

    public String getMyCity() {
        return pref_global.getString(MyCity, null);
    }

    public void setMyFCMToken(String myFCMToken) {
        editor.putString(MyFCMToken, myFCMToken);
        editor.commit();
    }

    public String getMyFCMToken() {
        return pref_global.getString(MyFCMToken, null);
    }

    public void removeAllPreference()
    {
        editor.clear();
        editor.commit();
    }

    public String getLocalIpAddress(Context myContext) {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                        return inetAddress.getHostAddress();
                    }
                }
            }
        } catch (Exception ex) {
            Toast.makeText(myContext, "Error IP: " + ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
        return null;
    }
}
