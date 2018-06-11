package com.omkar.bmccarparkinguser.Activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.kunzisoft.switchdatetime.SwitchDateTimeDialogFragment;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.omkar.bmccarparkinguser.Helpers.Encryption;
import com.omkar.bmccarparkinguser.Helpers.ServiceDetails;
import com.omkar.bmccarparkinguser.Model.ParkingLot;
import com.omkar.bmccarparkinguser.R;
import com.rengwuxian.materialedittext.MaterialEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import java.util.Date;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;


public class BookParkingActivity extends AppCompatActivity {

    String[] vehicle_types = {"Select Vehicle type", "Bike", "Car", "Bus"};
    MaterialEditText vehicleEditText;
    TextView tv_lotName, tv_parking_space;
    Spinner spinner_type;
    Button button_book;
    SharedPreferences userDetails;
    private static final String user_log_prefs = "User_Log";
    Encryption encryption;
    TextView tv_booking_time;
    ParkingLot parkingLot;
    private Dialog dialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_parking);
        vehicleEditText = findViewById(R.id.et_vehicle_no);
        spinner_type = findViewById(R.id.spinner_type);
        tv_lotName = findViewById(R.id.tv_lotName);
        tv_parking_space = findViewById(R.id.tv_parking_space);


        encryption = Encryption.getDefault("Key", "random", new byte[16]);
        userDetails = getSharedPreferences(user_log_prefs, MODE_PRIVATE);


        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>
                (this, android.R.layout.simple_spinner_dropdown_item,
                        vehicle_types);
        spinner_type.setAdapter(spinnerArrayAdapter);
        button_book = findViewById(R.id.button_book);
        parkingLot = (ParkingLot) getIntent().getSerializableExtra("parkingLot");
        tv_booking_time = findViewById(R.id.tv_booking_time);
        tv_lotName.setText(parkingLot.getLotname());
        tv_parking_space.setText((parkingLot.getParkedcapacity() - parkingLot.getParkedvehicle()) + "");
        final SwitchDateTimeDialogFragment dateTimeDialogFragment = SwitchDateTimeDialogFragment.newInstance(
                "Select Booking Time",
                "OK",
                "Cancel"
        );

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.HOUR_OF_DAY, 2);
        Date default_time = cal.getTime();
        dateTimeDialogFragment.setDefaultDateTime(default_time);
        tv_booking_time.setText(dateTimeDialogFragment.getDay() + "/" + dateTimeDialogFragment.getMonth() + "/" + dateTimeDialogFragment.getYear() + " " + dateTimeDialogFragment.getHourOfDay() + ":" + dateTimeDialogFragment.getMinute());
        Date min_date = cal.getTime();
        cal.add(Calendar.HOUR_OF_DAY, 24);
        Date max_date = cal.getTime();
        dateTimeDialogFragment.setMinimumDateTime(min_date);
        dateTimeDialogFragment.set24HoursMode(true);
        dateTimeDialogFragment.setMaximumDateTime(max_date);
        dateTimeDialogFragment.setCancelable(false);
        button_book.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (vehicleEditText.getText().toString().length() > 9) {
                    if (!spinner_type.getSelectedItem().equals("Select Vehicle type")) {
                        try {
                            Book_vehicle();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }

                    } else {
                        Snackbar.make(getWindow().getDecorView().getRootView(), "Please Select Vehicle Type", Snackbar.LENGTH_LONG).show();
                    }
                } else {
                    Snackbar.make(getWindow().getDecorView().getRootView(), "Please Enter 10 Digit  Vehicle Number", Snackbar.LENGTH_LONG).show();
                }

            }
        });


        tv_booking_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();

                dateTimeDialogFragment.setOnButtonClickListener(new SwitchDateTimeDialogFragment.OnButtonWithNeutralClickListener() {
                    @Override
                    public void onNeutralButtonClick(Date date) {
                        tv_booking_time.setText(dateTimeDialogFragment.getDay() + "/" + dateTimeDialogFragment.getMonth() + "/" + dateTimeDialogFragment.getYear() + " " + dateTimeDialogFragment.getHourOfDay() + ":" + dateTimeDialogFragment.getMinute());

                    }

                    @Override
                    public void onPositiveButtonClick(Date date) {
                        tv_booking_time.setText(dateTimeDialogFragment.getDay() + "/" + dateTimeDialogFragment.getMonth() + "/" + dateTimeDialogFragment.getYear() + " " + dateTimeDialogFragment.getHourOfDay() + ":" + dateTimeDialogFragment.getMinute());
                    }

                    @Override
                    public void onNegativeButtonClick(Date date) {
                        tv_booking_time.setText(dateTimeDialogFragment.getDay() + "/" + dateTimeDialogFragment.getMonth() + "/" + dateTimeDialogFragment.getYear() + " " + dateTimeDialogFragment.getHourOfDay() + ":" + dateTimeDialogFragment.getMinute());

                    }
                });
                dateTimeDialogFragment.show(getSupportFragmentManager(), "dialog_time");
            }
        });

    }

    private void Book_vehicle() throws JSONException, UnsupportedEncodingException {
        userDetails = getSharedPreferences(user_log_prefs, MODE_PRIVATE);
        String userMobileNo = encryption.decryptOrNull(userDetails.getString("userMobileNo", ""));
        String userName = encryption.decryptOrNull(userDetails.getString("userName", ""));

        JSONObject requestParams = new JSONObject();
        requestParams.put("Vehicle_no", vehicleEditText.getText().toString().trim());
        requestParams.put("Lot_id", parkingLot.getLotid());
        requestParams.put("Owner_mobile", userMobileNo);
        requestParams.put("Owner_id", userName);
        requestParams.put("Vehicle_type", spinner_type.getSelectedItem().toString());
        requestParams.put("Booking_duration", tv_booking_time.getText().toString());
        StringEntity entity = new StringEntity(requestParams.toString());
        AsyncHttpClient client = new AsyncHttpClient();
        client.post(getApplicationContext(), ServiceDetails._URL +"InsertBookedVehiclesDetails", entity, "application/json", new AsyncHttpResponseHandler() {
            @Override
            public void onProgress(long bytesWritten, long totalSize) {
                super.onProgress(bytesWritten, totalSize);
            }

            @Override
            public void onStart() {
                dialog = ProgressDialog.show(BookParkingActivity.this, "Please Wait", "Fetching Current Parking Lot", true);
            }

            @Override
            public void onRetry(int retryNo) {
                super.onRetry(retryNo);
            }

            @Override
            public void onCancel() {
                dialog.dismiss();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(new String(responseBody));
                    String responseData = jsonObject.getString("data");
                    if(responseData.equals(""))
                    {
                        Snackbar.make(getWindow().getDecorView().getRootView(), jsonObject.getString("message"), Snackbar.LENGTH_LONG).show();

                    }else
                    {
                        showQrCodeActivity(responseData);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                dialog.dismiss();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                dialog.dismiss();
                Snackbar.make(getWindow().getDecorView().getRootView(), "Something Went Wrong.", Snackbar.LENGTH_LONG).setAction("Dismiss", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                }).show();
            }
        });
    }

    private void showQrCodeActivity(String tokenData)
    {
        Intent QRCodeActivityIntent = new Intent(BookParkingActivity.this,QRCodeActivity.class);
        QRCodeActivityIntent.putExtra("tokenData",tokenData);
        startActivity(QRCodeActivityIntent);
        finish();

    }

}
