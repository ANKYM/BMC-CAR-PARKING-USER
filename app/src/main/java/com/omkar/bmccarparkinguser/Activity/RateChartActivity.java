package com.omkar.bmccarparkinguser.Activity;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.omkar.bmccarparkinguser.Helpers.ServiceDetails;
import com.omkar.bmccarparkinguser.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import de.codecrafters.tableview.TableView;
import de.codecrafters.tableview.model.TableColumnModel;
import de.codecrafters.tableview.toolkit.SimpleTableDataAdapter;
import de.codecrafters.tableview.toolkit.SimpleTableHeaderAdapter;

public class RateChartActivity extends AppCompatActivity implements  SwipeRefreshLayout.OnRefreshListener {
    TableView tableView ;
    SwipeRefreshLayout swipeRefreshLayout;
    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate_chart);
        tableView = (TableView) findViewById(R.id.tableView);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        Fetch_Parking_Rate();
        swipeRefreshLayout.setOnRefreshListener(this);
        final String[] TABLE_HEADERS = { "HRS", "Amount (Rs)"};
        tableView.setHeaderAdapter(new SimpleTableHeaderAdapter(RateChartActivity.this,TABLE_HEADERS));
        tableView.setHeaderBackground(R.color.colorBackGround1);
        Fetch_Parking_Rate();
        swipeRefreshLayout.setOnRefreshListener(this);

    }

    private void Fetch_Parking_Rate() {
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(ServiceDetails._URL +"GetParkingRate", new AsyncHttpResponseHandler() {
            @Override



            public void onProgress(long bytesWritten, long totalSize) {
                super.onProgress(bytesWritten, totalSize);
            }

            @Override
            public void onStart() {
                super.onStart();
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

                try {
                    JSONObject jsonObject = null;
                    jsonObject = new JSONObject(new String(responseBody));
                    String jsonArrayString = jsonObject.getString("data");
                    JSONArray jsonArray = new JSONArray(jsonArrayString);
                    String[][] DATA_TO_SHOW = new String[jsonArray.length()][2];
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject parkingRate = (JSONObject) jsonArray.get(i);
                        DATA_TO_SHOW[i][0] = parkingRate.get("hrs_duration")+"";
                        DATA_TO_SHOW[i][1] = parkingRate.get("rate")+"";
                    }
                    tableView.setDataAdapter(new SimpleTableDataAdapter(RateChartActivity.this, DATA_TO_SHOW));
                } catch (JSONException e) {
                    Snackbar.make(getWindow().getDecorView().getRootView(), "Something Went Wrong.", Snackbar.LENGTH_LONG).setAction("Retry", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Fetch_Parking_Rate();
                        }
                    }).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Snackbar.make(getWindow().getDecorView().getRootView(), "Something Went Wrong.", Snackbar.LENGTH_LONG).setAction("Retry", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Fetch_Parking_Rate();
                    }
                }).show();
            }
        });

    }


    @Override
    public void onRefresh() {
        swipeRefreshLayout.setRefreshing(false);
        Fetch_Parking_Rate();
    }
}
