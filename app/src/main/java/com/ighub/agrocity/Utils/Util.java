package com.ighub.agrocity.Utils;

/**
 * Created by sammy on 9/2/18.
 */

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;

import com.valdesekamdem.library.mdtoast.MDToast;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class Util {

    // Method for checking availability of network
    public boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectMgr;
        connectMgr = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectMgr.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    // Method for dismissing the keyboard
    public void dismissKeyboard(Activity activity) {
        InputMethodManager inputManager = (InputMethodManager)
                activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (null != activity.getCurrentFocus())
            inputManager.hideSoftInputFromWindow(activity.getCurrentFocus()
                    .getApplicationWindowToken(), 0);
    }

    public void toastMessage(Context context, String msg) {
        MDToast.makeText(context,msg,
                MDToast.LENGTH_SHORT, MDToast.TYPE_ERROR).show();
    }

    public boolean isValidEmail(Context context, CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }
    public boolean isValidPhoneNumber(Context context, String mobile) {
        String regEx = "^[0-9]{11}$";
        return mobile.matches(regEx);
    }

    public String getFormatedAmount(double amount){
        return NumberFormat.getNumberInstance(Locale.US).format(amount);
    }

}
