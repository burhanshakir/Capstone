package com.example.burhan.gpstracker;

import android.Manifest;
import android.app.NotificationManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;

import com.example.burhan.gpstracker.database.ContentProvider;
import com.example.burhan.gpstracker.database.FeedReaderDbHelper;
import com.google.android.gms.location.LocationListener;

import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;



public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    @BindView(R.id.tvCurrent)
    TextView current;
    @BindView(R.id.bStart)
    Button button;
    @BindView(R.id.fab)
    FloatingActionButton fab;
    PlaceAutocompleteFragment target;
    String t = "",targetLocation;
    Geocoder geocoder;
    List<Address> addresses;
    Location currentLoc, targetLoc;
    int flag = 0;
    double currentLong, currentLat, targetLong, targetLat, distance;
    GoogleApiClient mGoogleApiClient;
    LocationRequest mLocationRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        target = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
        target.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                Log.i("Place", "Place: " + place.getName());

                t = String.valueOf(place.getAddress());
                targetLocation = String.valueOf(place.getName());
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i("Place", "An error occurred: " + status);
            }
        });

        buildGoogleApiClient();
        createLocationRequest();

        currentLoc = new Location("");
        targetLoc = new Location("");
        geocoder = new Geocoder(this, Locale.getDefault());

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            checkLocationPermission();

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this,History.class);
                startActivity(i);
            }
        });
    }

    @Override
    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    @Override
    protected void onStop() {
        //mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

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
        currentLoc = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (currentLoc != null) {

            currentLong = currentLoc.getLongitude();
            currentLat = currentLoc.getLatitude();

            try {
                addresses = geocoder.getFromLocation(currentLat, currentLong, 1);
            } catch (IOException e) {
                e.printStackTrace();
            }
            String address = addresses.get(0).getAddressLine(0) + " " + addresses.get(0).getLocality();
            current.setText(address);

        } else
            Toast.makeText(this, "No Location found", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    public void start(View v) {
        if (flag == 0) {

            if (!t.isEmpty() && t != null) {
                flag++;
                button.setText("Stop");
                button.setContentDescription("Stop");

                ContentValues values = new ContentValues();
                values.put(FeedReaderDbHelper.KEY_LOCATION,targetLocation);
                values.put(FeedReaderDbHelper.KEY_DATE,new SimpleDateFormat("yyyy-MM-dd").format(new Date()));

                Uri uri = getContentResolver().insert(
                        ContentProvider.CONTENT_URI, values);

                //Toast.makeText(getBaseContext(),
                  //      uri.toString(), Toast.LENGTH_LONG).show();
                startLocationUpdates();
                try {
                    addresses = geocoder.getFromLocationName(t, 1);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (addresses.size() > 0) {
                    targetLat = addresses.get(0).getLatitude();
                    targetLong = addresses.get(0).getLongitude();

                    targetLoc.setLongitude(targetLong);
                    targetLoc.setLatitude(targetLat);

                    distance = currentLoc.distanceTo(targetLoc);

                    //Toast.makeText(MainActivity.this, "Lat:"+targetLat+",Long:"+targetLong, Toast.LENGTH_LONG).show();
                    //Toast.makeText(MainActivity.this, "Distance:" + distance, Toast.LENGTH_LONG).show();

                    if (distance <= 1000) {
                        NotificationCompat.Builder mBuilder =
                                new NotificationCompat.Builder(this)
                                        .setSmallIcon(R.drawable.map_location)
                                        .setContentTitle("GPS Tracker")
                                        .setContentText("Within 1km of " + t);

                        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                        notificationManager.notify(123, mBuilder.build());
                        stopLocationUpdates();

                        stopLocationUpdates();
                        flag = 0;
                        button.setText("Start");
                        button.setContentDescription("Start");
                    }
                }
            } else
                Toast.makeText(MainActivity.this, "Enter a location..", Toast.LENGTH_SHORT).show();
        } else {
            targetLoc.reset();

            /*String URL = "content://com.example.burhan.gpstracker.database/history";

            Uri uri = Uri.parse(URL);
            Cursor c = managedQuery(uri, null, null, null, "id");

            if (c.moveToFirst()) {
                do{
                    Toast.makeText(this,
                            c.getString(c.getColumnIndex(FeedReaderDbHelper.KEY_ID)) +
                                    ", " +  c.getString(c.getColumnIndex(FeedReaderDbHelper.KEY_LOCATION)) +
                                    ", " + c.getString(c.getColumnIndex(FeedReaderDbHelper.KEY_DATE)),
                            Toast.LENGTH_SHORT).show();
                } while (c.moveToNext());
            }*/
            finish();
        }
    }

    private void startLocationUpdates() {

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
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);

    }

    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this);
    }

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {


            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)) {

                //TODO:
                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);


            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    if (ContextCompat.checkSelfPermission(this,
                            android.Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient();
                        }

                    }

                } else {

                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }


    protected synchronized void buildGoogleApiClient() {
        //Toast.makeText(this, "buildGoogleApiClient", Toast.LENGTH_SHORT).show();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(15000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

    }

    @Override
    public void onLocationChanged(Location location) {

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
        currentLoc = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (currentLoc != null && flag == 1) {

            currentLong = currentLoc.getLongitude();
            currentLat = currentLoc.getLatitude();

            try {
                addresses = geocoder.getFromLocation(currentLat, currentLong, 1);
            } catch (IOException e) {
                e.printStackTrace();
            }
            String address = addresses.get(0).getAddressLine(0) + " " + addresses.get(0).getLocality();
            current.setText(address);

            distance = currentLoc.distanceTo(targetLoc);

            //Toast.makeText(MainActivity.this,"Distance:"+distance, Toast.LENGTH_LONG).show();
            if (distance <= 1000) {
                NotificationCompat.Builder mBuilder =
                        new NotificationCompat.Builder(MainActivity.this)
                                .setSmallIcon(R.drawable.map_location)
                                .setContentTitle("GPS Tracker")
                                .setContentText("Within 1km of " + t);

                NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.notify(123, mBuilder.build());

                stopLocationUpdates();
                flag = 0;
                button.setText("Start");
                button.setContentDescription("Start");
            }
        } else
            Toast.makeText(this, "No Location found", Toast.LENGTH_SHORT).show();

    }

}
