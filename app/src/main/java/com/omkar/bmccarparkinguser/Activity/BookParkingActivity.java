package com.omkar.bmccarparkinguser.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.omkar.bmccarparkinguser.Helpers.Encryption;
import com.omkar.bmccarparkinguser.Model.ParkingLot;
import com.omkar.bmccarparkinguser.R;
import com.rengwuxian.materialedittext.MaterialEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;
import io.blackbox_vision.datetimepickeredittext.view.DatePickerEditText;
import io.blackbox_vision.datetimepickeredittext.view.TimePickerEditText;


public class BookParkingActivity extends AppCompatActivity {

    String [] vehicle_types = {"Select Vehicle type" , "Bike" , "Car" , "Bus"};
    MaterialEditText vehicleEditText;
    TextView tv_lotName,tv_parking_space;
    Spinner spinner_type;
    Button button_book;
   // DatePickerEditText datePickerEditText ;
    TimePickerEditText timePickerEditText;

    SharedPreferences userDetails;
    private static final String user_log_prefs = "User_Log";
    Encryption encryption;

    ParkingLot parkingLot;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_parking);
        vehicleEditText = findViewById(R.id.et_vehicle_no);
        spinner_type = findViewById(R.id.spinner_type);
       // datePickerEditText = (DatePickerEditText) findViewById(R.id.datePickerEditText);
        timePickerEditText= (TimePickerEditText)findViewById(R.id.timePickerEditText);
       // datePickerEditText.setManager(getSupportFragmentManager());
        timePickerEditText.setManager(getSupportFragmentManager());
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

        tv_lotName.setText(parkingLot.getLotname());
        tv_parking_space.setText((parkingLot.getParkedcapacity() - parkingLot.getParkedvehicle()) + "");

        button_book.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Book_vehicle();
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    private void Book_vehicle() throws JSONException, UnsupportedEncodingException {
        userDetails = getSharedPreferences(user_log_prefs, MODE_PRIVATE);
        String userMobileNo  = encryption.decryptOrNull(userDetails.getString("userMobileNo", ""));
        String userName  = encryption.decryptOrNull(userDetails.getString("userName", ""));

        JSONObject requestParams = new JSONObject();
        requestParams.put("Vehicle_no", vehicleEditText.getText().toString().trim());
        requestParams.put("Lot_id", parkingLot.getLotid());
        requestParams.put("Owner_mobile", userMobileNo);
        requestParams.put("Owner_id", userName);
        requestParams.put("Vehicle_type", spinner_type.getSelectedItem().toString());
        requestParams.put("Booking_duration", "24/02/2018 20:18:22");
        StringEntity entity = new StringEntity(requestParams.toString());
        AsyncHttpClient client = new AsyncHttpClient();
        client.post(getApplicationContext(), "http://192.168.1.11:3660/Service.svc/InsertBookedVehiclesDetails", entity, "application/json", new AsyncHttpResponseHandler() {
            @Override
            public void onProgress(long bytesWritten, long totalSize) {
                super.onProgress(bytesWritten, totalSize);
            }

            @Override
            public void onStart() {

            }

            @Override
            public void onRetry(int retryNo) {
                super.onRetry(retryNo);
            }

            @Override
            public void onCancel() {
                super.onCancel();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.i("Error", statusCode + "");
                Log.i("Error", error.toString());
                Snackbar.make(getWindow().getDecorView().getRootView(), "Something Went Wrong.", Snackbar.LENGTH_LONG).setAction("Dismiss", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                }).show();
            }
        });
    }

}
