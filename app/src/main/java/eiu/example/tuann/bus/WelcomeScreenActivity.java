package eiu.example.tuann.bus;

import eiu.example.tuann.bus.BuildConfig;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.Manifest;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.model.LatLng;
import com.wang.avi.AVLoadingIndicatorView;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class WelcomeScreenActivity extends AppCompatActivity {

    public static WelcomeScreenActivity instance;

    private ImageView logo;

    private HashMap<String, BusStopInfomation> hashMap = new HashMap<String, BusStopInfomation>();

    private boolean connectedInternet = false;

    public AllBusStopInfomation allBusStopInfomation;

    private File folderBusstop;
    private File folderUpdate;
    private File fileUpdate;
    private int versionCode;

    private int waitingTime = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_welcome_screen);

        ImageView imageViewBackGround = (ImageView) findViewById(R.id.backgroundgif);
        Glide.with(this).load(R.drawable.background_gif).into(imageViewBackGround);

        logo = (ImageView) (findViewById(R.id.logo));
        logo.postDelayed(new Runnable() {
            public void run() {
                logo.setVisibility(View.GONE);
            }
        }, 1600);
        AVLoadingIndicatorView avLoadingIndicatorView = (AVLoadingIndicatorView) (findViewById(R.id.avi));
        avLoadingIndicatorView.show();

        versionCode = BuildConfig.VERSION_CODE;
        folderBusstop = new File(ReadWriteFileActivity.path + "/BusStop/");
        folderUpdate = new File(ReadWriteFileActivity.path + "/Update/");
        fileUpdate = new File(ReadWriteFileActivity.path + "/Update/V" + versionCode + ".txt");

        checkLocationPermission();

        if (!folderUpdate.exists() || !folderBusstop.exists() || (!fileUpdate.exists() && !folderUpdate.isDirectory())) {
            waitingTime = 6000;
        }

        if (isNetworkAvailable() == false) {
            NoConnection();
        } else {
            Connected();
        }
    }

    private Boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }

    public boolean isInternetAvailable() {
        try {
            InetAddress ipAddr = InetAddress.getByName("www.google.com");
            return !ipAddr.equals("");

        } catch (Exception e) {
            return false;
        }
    }

    public void NoConnection() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(WelcomeScreenActivity.this);
        builder.setCancelable(false);

        TextView title = new TextView(WelcomeScreenActivity.this);
        title.setText("Không có kết nối!");
        title.setBackgroundColor(Color.DKGRAY);
        title.setPadding(10, 10, 10, 10);
        title.setGravity(Gravity.CENTER);
        title.setTextColor(Color.WHITE);
        title.setTextSize(20);
        builder.setCustomTitle(title);

        builder.setPositiveButton("Thử lại?", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (isNetworkAvailable() == false) {
                    NoConnection();
                } else {
                    Connected();
                }
            }
        });


        final AlertDialog dialog = builder.create();
        dialog.show(); //show() should be called before dialog.getButton().


        final Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        LinearLayout parent = (LinearLayout) positiveButton.getParent();
        parent.setGravity(Gravity.CENTER_HORIZONTAL);
        View leftSpacer = parent.getChildAt(1);
        leftSpacer.setVisibility(View.GONE);
    }

    public void Connected() {
        Thread welcomeThread = new Thread() {
            @Override
            public void run() {
                try {
                    super.run();
                    sleep(waitingTime);
                } catch (Exception e) {

                } finally {
                    if (!fileUpdate.exists() && !fileUpdate.isDirectory()) {
                        ReadWriteFileActivity.saveToFile("V" + versionCode, "true", "/Update/");
                        if (folderBusstop.isDirectory()) {
                            ReadWriteFileActivity.deleteFile(folderBusstop);
                        }
                        buildBusStop();
                    }
                    if (!folderBusstop.exists()) {
                        buildBusStop();
                    }
                    if (folderBusstop.exists()) {
                        for (final File fileEntry : folderBusstop.listFiles()) {
                            String nameFile = fileEntry.getName();
                            String addressS = ReadWriteFileActivity.loadFile(WelcomeScreenActivity.this, nameFile, "/BusStop/");
                            String[] parts = nameFile.split(", ");
                            String part1 = parts[0];
                            String part2 = parts[1];
                            double latitude = Double.parseDouble(part1);
                            double longitude = Double.parseDouble(part2);
                            LatLng latLng = new LatLng(latitude, longitude);
                            BusStopInfomation busStopInfomation = new BusStopInfomation(addressS, latitude, longitude, latLng);
                            hashMap.put(latitude + ", " + longitude, busStopInfomation);
                        }
                    }
                    allBusStopInfomation = new AllBusStopInfomation(hashMap);
                    makeInstance();
                    Intent i = new Intent(WelcomeScreenActivity.this,
                            MainActivity.class);
                    startActivity(i);
                }
            }
        };
        welcomeThread.start();
    }

    private void buildBusStop() {
        HashMap<Double, Double> hashMapBusStop;
        hashMapBusStop = new HashMap<Double, Double>();
        hashMapBusStop.put(11.053468, 106.667168);
        hashMapBusStop.put(11.055205, 106.665759);
        hashMapBusStop.put(11.057658, 106.675506);
        hashMapBusStop.put(11.057868, 106.683938);
        hashMapBusStop.put(11.056834, 106.684646);
        hashMapBusStop.put(11.055155, 106.687408);
        hashMapBusStop.put(11.052869, 106.686166);
        hashMapBusStop.put(11.046721, 106.678201);
        hashMapBusStop.put(11.054940, 106.680702);
        hashMapBusStop.put(11.026950, 106.678080);
        hashMapBusStop.put(11.048760, 106.673754);
        Geocoder geocoder = new Geocoder(WelcomeScreenActivity.this, Locale.getDefault());
        for (Map.Entry<Double, Double> entry : hashMapBusStop.entrySet()) {
            Double latitude = entry.getKey();
            Double longitude = entry.getValue();
            List<Address> addresses = null;
            try {
                addresses = geocoder.getFromLocation(latitude, longitude, 1);
                Address address = addresses.get(0);
                String addressS = address.getAddressLine(0);
                ReadWriteFileActivity.saveToFile(latitude + ", " + longitude, addressS, "/BusStop/");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(WelcomeScreenActivity.this);
                builder.setCancelable(false);

                TextView title = new TextView(WelcomeScreenActivity.this);
                title.setText("Ứng dụng này cần sự cho phép định vị để sử dụng!");
                title.setBackgroundColor(Color.DKGRAY);
                title.setPadding(10, 10, 10, 10);
                title.setGravity(Gravity.CENTER);
                title.setTextColor(Color.WHITE);
                title.setTextSize(20);
                builder.setCustomTitle(title);

                builder.setPositiveButton("Đồng ý", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions(WelcomeScreenActivity.this,
                                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                MY_PERMISSIONS_REQUEST_LOCATION);
                    }
                });

                final AlertDialog dialog = builder.create();
                dialog.show();

                final Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                LinearLayout parent = (LinearLayout) positiveButton.getParent();
                parent.setGravity(Gravity.CENTER_HORIZONTAL);
                View leftSpacer = parent.getChildAt(1);
                leftSpacer.setVisibility(View.GONE);
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
        } else {
            if (connectedInternet == true) {
                Connected();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        if (connectedInternet == true) {
                            Connected();
                        }
                    }

                } else {
                    //     checkLocationPermission();
                }
                return;
            }
        }
    }

    private void makeInstance() {
        instance = this;
    }
}
