package com.omkar.bmccarparkinguser.Activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.provider.Telephony;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.omkar.bmccarparkinguser.Adaptor.MyBookingAdaptor;
import com.omkar.bmccarparkinguser.Helpers.Encryption;
import com.omkar.bmccarparkinguser.Helpers.ServiceDetails;
import com.omkar.bmccarparkinguser.Model.MyBooking;
import com.omkar.bmccarparkinguser.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

public class MyBookingActivity extends AppCompatActivity {

    private RecyclerView recycle_view_booking;
    private List<MyBooking> myBookingArrayList = new ArrayList<MyBooking>();
    private MyBookingAdaptor myBookingAdaptor;
    private Dialog dialog;
    SharedPreferences userDetails;
    private static final String user_log_prefs = "User_Log";
    Encryption encryption;
    String userName, userEmail, userMobileNo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_booking);
        recycle_view_booking = findViewById(R.id.recycle_view_booking);
        encryption = Encryption.getDefault("Key", "random", new byte[16]);
        userDetails = getSharedPreferences(user_log_prefs, MODE_PRIVATE);
        userMobileNo = encryption.decryptOrNull(userDetails.getString("userMobileNo", ""));
        userName = encryption.decryptOrNull(userDetails.getString("userName", ""));
        userEmail = encryption.decryptOrNull(userDetails.getString("userEmail", ""));

        recycle_view_booking.addOnItemTouchListener(
                new RecyclerItemClickListener(getApplicationContext(), new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        MyBooking myBooking = myBookingAdaptor.getMyBookingObject(position);
                        Intent QRCodeActivityIntent = new Intent(MyBookingActivity.this,QRCodeActivity.class);
                        QRCodeActivityIntent.putExtra("tokenData",myBooking.getBookingToken());
                        startActivity(QRCodeActivityIntent);
                    }
                })
        );
    }


    private void GetAllParkingData(final String userMobileNo, final String userName, final String userEmail) throws JSONException, UnsupportedEncodingException {
        JSONObject requestParams = new JSONObject();
        requestParams.put("Mobile_no", userMobileNo);
        requestParams.put("User_id", userName);
        requestParams.put("UserEmail", userEmail);
        StringEntity entity = new StringEntity(requestParams.toString());
        AsyncHttpClient client = new AsyncHttpClient();
        client.post(getApplicationContext(), ServiceDetails._URL + "GetAllParkingDetailsOfUser", entity, "application/json", new AsyncHttpResponseHandler() {
            @Override
            public void onProgress(long bytesWritten, long totalSize) {
                super.onProgress(bytesWritten, totalSize);
            }

            @Override
            public void onStart() {
                dialog = ProgressDialog.show(MyBookingActivity.this, "Please Wait", "Fetching Current Parking Lot", true);
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
                dialog.dismiss();
                try {
                    ArrayList<MyBooking> myBookingArrayList = new ArrayList<>();
                    JSONObject jsonObject = null;
                    jsonObject = new JSONObject(new String(responseBody));
                    String jsonArrayString = jsonObject.getString("data");
                    JSONArray jsonArray = new JSONArray(jsonArrayString);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject myBookingObject = (JSONObject) jsonArray.get(i);
                        myBookingArrayList.add(new MyBooking(myBookingObject.getString("vehicle_no"), myBookingObject.getString("lot_id"), myBookingObject.getString("lot_name"), myBookingObject.getString("owner_id"), myBookingObject.getString("vehicle_owner_mobile_no"), myBookingObject.getString("vehicle_type"), myBookingObject.getString("booking_time"), myBookingObject.getString("booking_duration"), myBookingObject.getInt("booking_confirmerd"), myBookingObject.getString("booking_token"),myBookingObject.getInt("booking_status")));
                    }


                    AddBookingInfo(myBookingArrayList);
                } catch (JSONException e) {
                    Snackbar.make(getWindow().getDecorView().getRootView(), "Something Went Wrong.", Snackbar.LENGTH_LONG).setAction("Retry", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                        }
                    }).show();
                }
                dialog.dismiss();

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                dialog.dismiss();
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

    private void AddBookingInfo(ArrayList<MyBooking> myBookings) {
        myBookingAdaptor = new MyBookingAdaptor(getApplicationContext(), myBookings);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recycle_view_booking.setLayoutManager(mLayoutManager);
        recycle_view_booking.setItemAnimator(new DefaultItemAnimator());
        recycle_view_booking.setAdapter(myBookingAdaptor);
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            GetAllParkingData(userMobileNo, userName, userEmail);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
}

class RecyclerItemClickListener implements RecyclerView.OnItemTouchListener {
    private OnItemClickListener mListener;

    public interface OnItemClickListener {
        public void onItemClick(View view, int position);
    }

    GestureDetector mGestureDetector;

    public RecyclerItemClickListener(Context context, OnItemClickListener listener) {
        mListener = listener;
        mGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }
        });
    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView view, MotionEvent e) {
        View childView = view.findChildViewUnder(e.getX(), e.getY());
        if (childView != null && mListener != null && mGestureDetector.onTouchEvent(e)) {
            mListener.onItemClick(childView, view.getChildAdapterPosition(childView));
        }
        return false;
    }

    @Override
    public void onTouchEvent(RecyclerView view, MotionEvent motionEvent) {
    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

    }

}
