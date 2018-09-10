package com.ighub.agrocity.Activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatTextView;
import android.view.View;
import android.widget.Toast;

import com.ighub.agrocity.R;
import com.ighub.agrocity.Utils.JsonParser;
import com.ighub.agrocity.Utils.Util;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.valdesekamdem.library.mdtoast.MDToast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class Login extends AppCompatActivity implements View.OnClickListener{

    private final AppCompatActivity activity = Login.this;

    private NestedScrollView nestedScrollView;

    private TextInputLayout textInputLayoutEmail;
    private TextInputLayout textInputLayoutPassword;

    private TextInputEditText textInputEditTextEmail;
    private TextInputEditText textInputEditTextPassword;

    private AppCompatButton appCompatButtonLogin;

    private AppCompatTextView textViewLinkRegister;

    private InputValidation inputValidation;

    private Util util;
    private KProgressHUD hud;

    public static String PREFS_NAME = "dispatcher";
    public static String PREF_PHONE = "phone";
    private SharedPreferences preference;
    private SharedPreferences.Editor editor;
//    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();

        initViews();
        initListeners();
        initObjects();
    }

    private void initViews() {

        nestedScrollView = (NestedScrollView) findViewById(R.id.nestedScrollView);

        textInputLayoutEmail = (TextInputLayout) findViewById(R.id.textInputLayoutEmail);
        textInputLayoutPassword = (TextInputLayout) findViewById(R.id.textInputLayoutPassword);

        textInputEditTextEmail = (TextInputEditText) findViewById(R.id.textInputEditTextEmail);
        textInputEditTextPassword = (TextInputEditText) findViewById(R.id.textInputEditTextPassword);

        appCompatButtonLogin = (AppCompatButton) findViewById(R.id.appCompatButtonLogin);

        textViewLinkRegister = (AppCompatTextView) findViewById(R.id.textViewLinkRegister);

    }

    private void initListeners() {

        appCompatButtonLogin.setOnClickListener(this);
        textViewLinkRegister.setOnClickListener(this);


    }

    private  void initObjects() {
        inputValidation = new InputValidation(activity);

    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.appCompatButtonLogin:
                process();
                break;
            case R.id.textViewLinkRegister:
                // Navigate to RegisterActivity
                Intent intentRegister = new Intent(getApplicationContext(), Registeration.class);
                startActivity(intentRegister);
                break;
        }

    }

    private void process() {

        if (!inputValidation.isInputEditTextFilled(textInputEditTextEmail, textInputLayoutEmail, getString(R.string.error_message_email))) {
            return;
        }
        if (!inputValidation.isInputEditTextEmail(textInputEditTextEmail, textInputLayoutEmail, getString(R.string.error_message_email))) {
            return;
        }
        if (!inputValidation.isInputEditTextFilled(textInputEditTextPassword, textInputLayoutPassword, getString(R.string.error_message_email))) {
            return;
        }
        if (util.isNetworkAvailable(getApplicationContext())) {
            try {
                new sendLoginRequest();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            util.toastMessage(getApplicationContext(), "Check your network connection");
        }
    }

    public class sendLoginRequest extends AsyncTask<String, Void, JSONObject> {

        private static final String REGISTER_URL =
                "https://agrocity.herokuapp.com/login";
        JsonParser jsonParser = new JsonParser();
        String email = textInputEditTextEmail.getText().toString().trim();
        String password = textInputEditTextPassword.getText().toString().trim();


        @Override
        protected void onPreExecute() {
            hud = KProgressHUD.create(Login.this)
                    .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                    .setLabel("Please wait")
                    .setDetailsLabel("Creating login session..." + email)
                    .setCancellable(true)
                    .setBackgroundColor(Color.BLACK)
                    .setAnimationSpeed(2)
                    .setDimAmount(0.5f)
                    .show();
            super.onPreExecute();

        }

        @Override
        protected JSONObject doInBackground(String... strings) {

            try {

                HashMap<String, String> params = new HashMap<>();
                params.put("email", email);
                params.put("password", password);


                JSONObject json = jsonParser.makeHttpRequest(REGISTER_URL, "POST", params);

                if (json != null) {

                    return json;
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            hud.dismiss();
                            util.toastMessage(getApplicationContext(),
                                    "Network error, please try again later!");
                        }
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();

            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONObject json) {

            hud.dismiss();

            try {
                if (json != null && json.getString("status").equals("200") && json.getString("message").equals("account logged in and verified")) {

                    preference = getApplicationContext().getSharedPreferences(PREFS_NAME, Activity.MODE_PRIVATE);
                    editor = preference.edit();
//                            .putString("firstName", firstName)
                    editor.apply();
//
//                    MDToast.makeText(getApplicationContext(), "Account successfully " +
//                                    "created, Please check your email to activate your account and then login",
//                            MDToast.LENGTH_SHORT, MDToast.TYPE_SUCCESS).show();

                    Toast.makeText(getApplicationContext(), "Welcome", Toast.LENGTH_LONG ).show();

                    Intent intent = new Intent(Login.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                } else if(json != null && json.getString("status").equals("500") && json.getString("message").equals("No account found for this user")) {
                    new SweetAlertDialog(Login.this, SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("Error Message")
                            .setContentText("please ensure you are registered user before attempting to login")
                            .setConfirmText("OK")
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sDialog) {
                                    sDialog.dismissWithAnimation();

                                }
                            })
                            .show();
                } else {
                    new SweetAlertDialog(activity, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("User Login!")
                            .setContentText("Something went wrong. Please try again later")
                            .setConfirmText("Ok")
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                    sweetAlertDialog.dismiss();
                                }
                            });
                }
            } catch (NullPointerException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }
}
