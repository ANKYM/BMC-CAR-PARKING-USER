package com.omkar.bmccarparkinguser.Activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.omkar.bmccarparkinguser.Helpers.ConnectionDetector;
import com.omkar.bmccarparkinguser.Helpers.Encryption;
import com.omkar.bmccarparkinguser.R;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {

    //Region DECLARATION
    EditText mobileNo_editText, userName_editText, userEmail_editText;
    Button register_button;
    //endregion


    SharedPreferences userDetails;
    private static final String user_log_prefs = "User_Log";
    Encryption encryption;
    private boolean isRegister = false;

    int PERMISSION_ALL = 1;
    String[] PERMISSIONS = {android.Manifest.permission.READ_PHONE_STATE, android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.SEND_SMS, android.Manifest.permission.READ_EXTERNAL_STORAGE};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        //region INITIALIZATION
        mobileNo_editText = findViewById(R.id.et_user_mobile_no);
        userName_editText = findViewById(R.id.et_user_name);
        userEmail_editText = findViewById(R.id.et_user_email_id);
        register_button = findViewById(R.id.button_register);
        //endregion

        encryption = Encryption.getDefault("Key", "random", new byte[16]);
        userDetails = getSharedPreferences(user_log_prefs, MODE_PRIVATE);
        isRegister = userDetails.getBoolean("isRegister", false);

        if (!hasPermissions(RegisterActivity.this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(RegisterActivity.this, PERMISSIONS, PERMISSION_ALL);
        } else {
            if (isRegister) {
                Intent loginIntent = new Intent(getApplicationContext(), MapDrawerActivity.class);
                startActivity(loginIntent);
                finish();
            }
        }

        register_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ConnectionDetector.isInternetConnection(getApplicationContext())) {

                    if (mobileNo_editText.getText().toString().trim().length() == 10) {

                        if (userEmail_editText.getText().toString().trim().length() > 10 && isValidEmailId(userEmail_editText.getText().toString().trim())) {
                            if (userName_editText.getText().toString().trim().length() > 5) {
                                if (Register_User(mobileNo_editText.getText().toString().trim(), userName_editText.getText().toString().trim(), userEmail_editText.getText().toString().trim())) {
                                    Intent loginIntent = new Intent(getApplicationContext(), MapDrawerActivity.class);
                                    startActivity(loginIntent);
                                    finish();
                                } else {
                                    Snackbar.make(v, "Something Went Wrong", Snackbar.LENGTH_LONG).show();
                                }
                            } else {
                                Snackbar.make(v, "Please Enter Valid User Name", Snackbar.LENGTH_LONG).show();
                            }
                        } else {
                            Snackbar.make(v, "Please Enter Valid Email id", Snackbar.LENGTH_LONG).show();
                        }
                    } else {
                        Snackbar.make(v, "Please Enter Valid 10 Digit Number", Snackbar.LENGTH_LONG).show();
                    }
                } else {
                    Snackbar.make(v, "Please Check Internet Connection", Snackbar.LENGTH_LONG).show();
                }
            }
        });


    }

    private boolean isValidEmailId(String email) {

        return Pattern.compile("^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
                + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
                + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$").matcher(email).matches();
    }

    private Boolean Register_User(String userMobileNo, String userName, String userEmail) {
        userDetails = getSharedPreferences(user_log_prefs, MODE_PRIVATE);
        SharedPreferences.Editor session_editor = userDetails.edit();
        session_editor.putString("userMobileNo", encryption.encryptOrNull(userMobileNo));
        session_editor.putString("userName", encryption.encryptOrNull(userName));
        session_editor.putString("userEmail", encryption.encryptOrNull(userEmail));
        session_editor.putBoolean("isRegister", true);
        session_editor.commit();
        return true;
    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                int i = ActivityCompat.checkSelfPermission(context, permission);
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                Map<String, Integer> perms = new HashMap<>();
                perms.put(android.Manifest.permission.READ_PHONE_STATE, PackageManager.PERMISSION_GRANTED);
                perms.put(android.Manifest.permission.SEND_SMS, PackageManager.PERMISSION_GRANTED);
                perms.put(android.Manifest.permission.ACCESS_FINE_LOCATION, PackageManager.PERMISSION_GRANTED);
                perms.put(android.Manifest.permission.READ_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.READ_CONTACTS, PackageManager.PERMISSION_GRANTED);
                if (grantResults.length > 0) {
                    for (int i = 0; i < permissions.length; i++)
                        perms.put(permissions[i], grantResults[i]);
                    if (perms.get(android.Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED
                            && perms.get(android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                            && perms.get(android.Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED
                            && perms.get(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                            && perms.get(Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
                    }else
                    {
                        finish();
                    }
                    return;
                }
            }
        }
    }


}
