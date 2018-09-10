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

import com.ighub.agrocity.R;
import com.ighub.agrocity.Utils.JsonParser;
import com.ighub.agrocity.Utils.Util;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.valdesekamdem.library.mdtoast.MDToast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class Registeration extends AppCompatActivity implements View.OnClickListener {

    private final AppCompatActivity activity = Registeration.this;

    private NestedScrollView nestedScrollView;

    private TextInputLayout textInputLayoutName;
    private TextInputLayout textInputLayoutLName;
    private TextInputLayout textInputLayoutEmail;
    private TextInputLayout textInputLayoutPassword;
    private TextInputLayout textInputLayoutConfirmPassword;

    private TextInputEditText textInputEditTextName;
    private TextInputEditText textInputEditTextLName;
    private TextInputEditText textInputEditTextEmail;
    private TextInputEditText textInputEditTextPassword;
    private TextInputEditText textInputEditTextConfirmPassword;

    private AppCompatButton appCompatButtonRegister;
    private AppCompatTextView appCompatTextViewLoginLink;

    private InputValidation inputValidation;

    private Util util;
    private KProgressHUD hud;

    public static String PREFS_NAME = "dispatcher";
    public static String PREF_PHONE = "phone";
    private SharedPreferences preference;
    private SharedPreferences.Editor editor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registeration);
        getSupportActionBar().hide();

        initViews();
        initListeners();
        initObjects();
    }

    private void initViews() {
        nestedScrollView = (NestedScrollView) findViewById(R.id.nestedScrollView);

        textInputLayoutName = (TextInputLayout) findViewById(R.id.textInputLayoutName);
        textInputLayoutName = (TextInputLayout) findViewById(R.id.textInputLayoutLName);
        textInputLayoutEmail = (TextInputLayout) findViewById(R.id.textInputLayoutEmail);
        textInputLayoutPassword = (TextInputLayout) findViewById(R.id.textInputLayoutPassword);
        textInputLayoutConfirmPassword = (TextInputLayout) findViewById(R.id.textInputLayoutConfirmPassword);

        textInputEditTextName = (TextInputEditText) findViewById(R.id.textInputEditTextName);
        textInputEditTextLName = (TextInputEditText) findViewById(R.id.textInputEditTextLName);
        textInputEditTextEmail = (TextInputEditText) findViewById(R.id.textInputEditTextEmail);
        textInputEditTextPassword = (TextInputEditText) findViewById(R.id.textInputEditTextPassword);
        textInputEditTextConfirmPassword = (TextInputEditText) findViewById(R.id.textInputEditTextConfirmPassword);

        appCompatButtonRegister = (AppCompatButton) findViewById(R.id.appCompatButtonRegister);

        appCompatTextViewLoginLink = (AppCompatTextView) findViewById(R.id.appCompatTextViewLoginLink);

    }

    /**
     * This method is to initialize listeners
     */
    private void initListeners() {
        appCompatButtonRegister.setOnClickListener(this);
        appCompatTextViewLoginLink.setOnClickListener(this);

    }

    private void initObjects() {
        inputValidation = new InputValidation(activity);

    }

    private void emptyInputEditText() {
        textInputEditTextName.setText(null);
        textInputEditTextEmail.setText(null);
        textInputEditTextPassword.setText(null);
        textInputEditTextConfirmPassword.setText(null);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.appCompatButtonRegister:
                process();
                break;

            case R.id.appCompatTextViewLoginLink:
                finish();
                break;
        }
    }

    private void process() {
        if (!inputValidation.isInputEditTextFilled(textInputEditTextName, textInputLayoutName, getString(R.string.error_message_name))) {
            return;
        }
        if (!inputValidation.isInputEditTextFilled(textInputEditTextLName, textInputLayoutLName, getString(R.string.error_message_lname))) {
            return;
        }
        if (!inputValidation.isInputEditTextFilled(textInputEditTextEmail, textInputLayoutEmail, getString(R.string.error_message_email))) {
            return;
        }
        if (!inputValidation.isInputEditTextEmail(textInputEditTextEmail, textInputLayoutEmail, getString(R.string.error_message_email))) {
            return;
        }
        if (!inputValidation.isInputEditTextFilled(textInputEditTextPassword, textInputLayoutPassword, getString(R.string.error_message_password))) {
            return;
        }
        if (!inputValidation.isInputEditTextMatches(textInputEditTextPassword, textInputEditTextConfirmPassword,
                textInputLayoutConfirmPassword, getString(R.string.error_password_match))) {
            return;
        }
        if (util.isNetworkAvailable(getApplicationContext())) {
            try {
                new sendPostRequest();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            util.toastMessage(getApplicationContext(), "Check your network connection");
        }
    }

    public class sendPostRequest extends AsyncTask<String, Void, JSONObject> {

        private static final String REGISTER_URL =
                "https://agrocity.herokuapp.com/register";
        JsonParser jsonParser = new JsonParser();
//        String phone = etPhone2.getText().toString().trim();
        String firstName = textInputEditTextName.getText().toString().trim();
        String lastName = textInputEditTextLName.getText().toString().trim();
        String name = firstName + " " + lastName;
        String email = textInputEditTextEmail.getText().toString().trim();
        String password = textInputEditTextPassword.getText().toString().trim();


        @Override
        protected void onPreExecute() {
            hud = KProgressHUD.create(Registeration.this)
                    .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                    .setLabel("Please wait")
                    .setDetailsLabel("Registering..." + name)
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

//                params.put("phone", phone);
//                params.put("firstName", firstName);
                params.put("name", name);
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
                if (json != null && json.getString("status").equals("200") && json.get("message").equals("successfully created an account")) {

                    preference = getApplicationContext().getSharedPreferences(PREFS_NAME, Activity.MODE_PRIVATE);
                    editor = preference.edit();
//                            .putString("firstName", firstName)
                    editor.apply();

                    MDToast.makeText(getApplicationContext(), "Account successfully " +
                                    "created, Please check your email to activate your account and then login",
                            MDToast.LENGTH_SHORT, MDToast.TYPE_SUCCESS).show();

                    Intent intent = new Intent(Registeration.this, Login.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                } else if(json != null && json.getString("status").equals("500")){
                    new SweetAlertDialog(Registeration.this, SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("Error Message")
                            .setContentText("Email already exists")
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
                            .setTitleText("User Registration!")
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
