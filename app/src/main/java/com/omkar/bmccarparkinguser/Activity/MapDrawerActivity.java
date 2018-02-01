package com.omkar.bmccarparkinguser.Activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.omkar.bmccarparkinguser.Helpers.ServiceDetails;
import com.omkar.bmccarparkinguser.Model.ParkingSpot;
import com.omkar.bmccarparkinguser.R;


import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MapDrawerActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback, android.location.LocationListener, com.google.android.gms.location.LocationListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, ResultCallback<LocationSettingsResult> {
    //region LOCATION VARIABLES
    private static final String TAG = "Maps Activity";
    private LocationManager mLocationManager = null;
    private static final int LOCATION_INTERVAL = 100;
    private static final float LOCATION_DISTANCE = 10f;
    Location mLastLocation;
    protected static final int REQUEST_CHECK_SETTINGS = 0x3;
    private static final long INTERVAL = 1000 * 10;
    private static final long FASTEST_INTERVAL = 1000 * 5;
    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    protected LocationSettingsRequest mLocationSettingsRequest;
    String mLastUpdateTime;
    Marker bmcMarker;

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    //endregion
    private GoogleMap mMap;
    int PERMISSION_ALL = 1;
    String[] PERMISSIONS = {android.Manifest.permission.READ_PHONE_STATE, android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.SEND_SMS, android.Manifest.permission.READ_EXTERNAL_STORAGE};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_drawer);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setTitle("BMC CAR PARKING");
        setSupportActionBar(toolbar);
        if (!isGooglePlayServicesAvailable()) {
            finish();
        }
        createLocationRequest();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        mLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        if (!hasPermissions(MapDrawerActivity.this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(MapDrawerActivity.this, PERMISSIONS, PERMISSION_ALL);
        } else {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            }
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE, this);
            mLastLocation = new Location(LocationManager.GPS_PROVIDER);
        }
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.locationFab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LatLng coordinate = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()); //Store these lat lng values somewhere. These should be constant.
                CameraUpdate location = CameraUpdateFactory.newLatLngZoom(
                        coordinate, 16);
                mMap.animateCamera(location);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        new Fetch_Parking_Spot().execute();
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
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.map_drawer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();


        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "Firing onLocationChanged..............................................");
        mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
        mLastLocation = location;
        Log.i("Location", "Latitude : " + mLastLocation.getLatitude() + " Longitude  : " + mLastLocation.getLongitude());
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

        buildLocationSettingsRequest();
        checkLocationSettings();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d(TAG, "onConnected - isConnected ...............: " + mGoogleApiClient.isConnected());
        startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "Connection failed: " + connectionResult.toString());

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.setTrafficEnabled(true);


        LatLng snowqualmie = new LatLng(19.118150, 72.888694);

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(snowqualmie)
                .title("Snowqualmie Falls")
                .snippet("Snoqualmie Falls is located 25 miles east of Seattle.")
                .icon(BitmapDescriptorFactory.defaultMarker( BitmapDescriptorFactory.HUE_BLUE));

        ParkingSpot info = new ParkingSpot();
        info.setSpotName("snowqualmie");
        info.setSpotID("1");
        info.setAddress("Powai");
        info.setLat("19.118150");
        info.setLongi("72.888694");
        info.setParkCapicity(20);
        info.setParkCapicity(12);

        CustomInfoWindowGoogleMap customInfoWindow = new CustomInfoWindowGoogleMap(this);
        mMap.setInfoWindowAdapter(customInfoWindow);

        Marker m = mMap.addMarker(markerOptions);
        m.setTag(info);
        //m.showInfoWindow();

        mMap.moveCamera(CameraUpdateFactory.newLatLng(snowqualmie));
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                Map<String, Integer> perms = new HashMap<>();
                // Initialize the map with both permissions
                perms.put(android.Manifest.permission.READ_PHONE_STATE, PackageManager.PERMISSION_GRANTED);
                perms.put(android.Manifest.permission.SEND_SMS, PackageManager.PERMISSION_GRANTED);
                perms.put(android.Manifest.permission.ACCESS_FINE_LOCATION, PackageManager.PERMISSION_GRANTED);
                perms.put(android.Manifest.permission.READ_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);

                // Fill with actual results from user
                if (grantResults.length > 0) {
                    for (int i = 0; i < permissions.length; i++)
                        perms.put(permissions[i], grantResults[i]);
                    if (perms.get(android.Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED
                            && perms.get(android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                            && perms.get(android.Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED
                            && perms.get(android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {

                    } else {
                        Toast.makeText(this, "Please go to settings and enable permission", Toast.LENGTH_LONG).show();
                        finish();

                    }
                }
                return;
            }

        }
    }

    @Override
    public void onResult(@NonNull LocationSettingsResult locationSettingsResult) {
        final Status status = locationSettingsResult.getStatus();
        switch (status.getStatusCode()) {
            case LocationSettingsStatusCodes.SUCCESS:
                LatLng UserLocation = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(UserLocation, 16));
                break;
            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                Log.i(TAG, "Location settings are not satisfied. Show the user a dialog to" +
                        "upgrade location settings ");

                try {
                    // Show the dialog by calling startResolutionForResult(), and check the result
                    // in onActivityResult().
                    status.startResolutionForResult(MapDrawerActivity.this, REQUEST_CHECK_SETTINGS);
                } catch (IntentSender.SendIntentException e) {
                    Log.i(TAG, "PendingIntent unable to execute request.");
                }
                break;
            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                Log.i(TAG, "Location settings are inadequate, and cannot be fixed here. Dialog " +
                        "not created.");
                break;
        }
    }

    private boolean isGooglePlayServicesAvailable() {
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (ConnectionResult.SUCCESS == status) {
            return true;
        } else {
            GooglePlayServicesUtil.getErrorDialog(status, this, 0).show();
            return false;
        }
    }

    /**
     * Uses a {@link LocationSettingsRequest.Builder} to build
     * a {@link LocationSettingsRequest} that is used for checking
     * if a device has the needed location settings.
     */
    protected void buildLocationSettingsRequest() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        builder.setAlwaysShow(true);
        mLocationSettingsRequest = builder.build();
    }

    /**
     * Check if the device's location settings are adequate for the app's needs using the
     * {@link com.google.android.gms.location.SettingsApi#checkLocationSettings(GoogleApiClient,
     * LocationSettingsRequest)} method, with the results provided through a {@code PendingResult}.
     */
    protected void checkLocationSettings() {
        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(
                        mGoogleApiClient,
                        mLocationSettingsRequest
                );
        result.setResultCallback(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart fired ..............");
        mGoogleApiClient.connect();


    }


    @Override
    protected void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
        Log.i(TAG, "onstop");
    }

    protected void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(MapDrawerActivity.this, "Please go to Settings->Apps->Permissions \n Check Location Option.", Toast.LENGTH_SHORT).show();

            return;
        }
        PendingResult<Status> pendingResult = LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
        Log.d(TAG, "Location update started ..............: ");

    }







//    private void fetchParkingSpot()
//    {
//
//        RequestQueue queue = Volley.newRequestQueue(this);
//        String url ="http://192.168.1.3:987/Service.svc/GetSpotDetails";
//        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
//                new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) {
//                        try {
//                            JSONObject json = (JSONObject) new JSONParser().parse(response);
//
//                        } catch (ParseException e) {
//                            e.printStackTrace();
//                        }
//
//                        Log.i("Response",response);
//                    }
//                }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//            }
//        });
//// Add the request to the RequestQueue.
//        queue.add(stringRequest);
//    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
              case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        Snackbar.make(getWindow().getDecorView().getRootView(), "GPS Enable", Snackbar.LENGTH_LONG).show();
                        break;
                    case Activity.RESULT_CANCELED:
                        Snackbar.make(getWindow().getDecorView().getRootView(), "GPS Disable", Snackbar.LENGTH_LONG).show();
                        finish();
                        break;
                }
                break;
        }
    }

    private class Fetch_Parking_Spot extends AsyncTask<String , Void, String> {
        ArrayList<ParkingSpot> all_parking_spots = new ArrayList<>();



        @Override
        protected void onPreExecute() {
            if (!isFinishing()) {
                //dialog = ProgressDialog.show(Complaint_List_Activity.this, getResources().getString(R.string.wait_Heading), getResources().getString(R.string.Update_message3), true);
            }
        }

        @Override
        protected String doInBackground(String... params) {

                SoapObject request = new SoapObject(ServiceDetails.NAMESPACE, ServiceDetails.GET_SPOT_DETAILS);
                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                envelope.dotNet = true;
                envelope.setOutputSoapObject(request);
                HttpTransportSE androidHttpTransport = new HttpTransportSE( ServiceDetails._URL, 80000);
                try {
                    androidHttpTransport.call(ServiceDetails.GET_SPOT_DETAILS_SOAP_ACTION, envelope);
                    SoapObject root = (SoapObject) envelope.bodyIn;
                    SoapObject t = (SoapObject) root.getProperty(0);
                    SoapObject t1 = (SoapObject) t.getProperty(1);

                    if (t1.toString().equals("anyType{}")) {
                        return "false";
                    } else {
                        SoapObject t2 = (SoapObject) t1.getProperty(0);
                        for (int i = 0; i < t2.getPropertyCount(); i++) {
                            SoapObject parking_spot = (SoapObject) t2.getProperty(i);
                            String spot_id =  parking_spot.getProperty(0).toString();
                            String spot_name = parking_spot.getProperty(1).toString();
                            String spot_address =  parking_spot.getProperty(2).toString();
                            String spot_lat =  parking_spot.getProperty(3).toString();
                            String spot_long = parking_spot.getProperty(4).toString();
                            int  spot_park_capacity = Integer.parseInt(parking_spot.getProperty(5).toString());
                            int  spot_park_vehicle = Integer.parseInt(parking_spot.getProperty(6).toString());
                            all_parking_spots.add(new ParkingSpot(spot_id,spot_name,spot_address,spot_lat,spot_long,spot_park_capacity,spot_park_vehicle));
                        }

                        return "true";
                    }
                } catch (Exception e) {
                    return "false";
                }

        }

        @Override
        protected void onPostExecute(String b) {
            if(b.equals("true"))
            {
                for(int i = 0 ; i<all_parking_spots.size();i++){
                    mMap.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(all_parking_spots.get(i).getLat()),Double.parseDouble(all_parking_spots.get(i).getLongi()))).title(all_parking_spots.get(i).getSpotName()));

                }
            }
        }
    }
}
