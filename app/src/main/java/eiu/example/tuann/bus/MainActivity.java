package eiu.example.tuann.bus;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeSet;

import static eiu.example.tuann.bus.MainFragment.fabDirection;
import static eiu.example.tuann.bus.MainFragment.fabWalking;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback, View.OnClickListener, GoogleApiClient.OnConnectionFailedListener, GoogleMap.OnMarkerClickListener, GoogleMap.InfoWindowAdapter, GoogleApiClient.ConnectionCallbacks, com.google.android.gms.location.LocationListener, GoogleMap.OnMapClickListener, GoogleMap.OnMapLongClickListener, GoogleMap.OnCameraChangeListener, AdapterView.OnItemSelectedListener {

    private static final String TAG = null;

    public static DrawerLayout drawer;

    public static PlaceAutocompleteAdapter mAdapterPlaceAutoComplete;

    public static MainActivity instance;

    private View header;
    private Button button_login_without;
    private NavigationView navigationView;
    private TextView button_t_register;
    private Button back_to_login;
    private Button register;
    private Button back_to_first;
    private Button login;

    private EditText mEmailViewLogin;
    private EditText mPasswordViewLogin;
    private EditText mNameViewRegister;
    private EditText mEmailViewRegister;
    private EditText mPasswordViewRegister;
    private EditText mConfirmPasswordViewRegister;
    private EditText mPhoneNumberRegister;
    private ImageView mAvatarLoged;
    private TextView mEmailLoged;
    private TextView mNameLoged;
    private ImageView buttonDropDown;
    public static View viewMap;

    private static final LatLngBounds BOUNDS_GREATER_SYDNEY = new LatLngBounds(new LatLng(-0, 0), new LatLng(-0, 0));

    private boolean loged = false;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    public static StorageReference storageReference;
    private Firebase firebasePutLocation;
    public static FirebaseUser user;
    private Firebase firebaseGetLocation;
    private FirebaseStorage firebaseStorage;
    private StorageReference mStorageRef;

    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    public static GoogleApiClient mGoogleApiClientAutoComplete;

    public static Marker findAddress;
    public static Marker startDirection;
    public static Marker endDirection;
    private static List<Marker> allBusStopMarker;
    private static List<Marker> allBusMarker;

    private HashMap<String, Marker> hashMapMarkerBus = new HashMap<String, Marker>();

    public static Location myCurrentLocation;

    private ProgressDialog progressDialog;

    private HashMap<String, Location> oldLocation = new HashMap<>();
    private double oldlat = 0;
    private double oldlong = 0;

    public static LatLng currentLocation;

    public static Polyline polyline = null;

    public static AppCompatActivity appCompatActivity;

    public static String travelMod;

    public static HashMap<Double, Double> hashMapNearByBusStop;

    private float currentLevelZoom = 0;

    private static FragmentManager manager;
    private MainFragment mainFragment = new MainFragment();
    private DirectionFragment directionFragment = new DirectionFragment();
    private PlacePickerFragment placePickerFragment = new PlacePickerFragment();
    private DrawingRote drawingRote;
    private static InformationDirectionFragment informationDirectionFragment = new InformationDirectionFragment();
    public static AnimationLoadingFragment animationLoadingFragment = new AnimationLoadingFragment();

    private Spinner routeNavigationMenuSpinner;
    private String[] nameRoute;
    public static Polyline brown = null;
    public static Polyline blue = null;
    public static Polyline red = null;
    public static Polyline green = null;
    public static Polyline yello = null;
    public static Polyline pink = null;

    private Polyline emulatorPolyline = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_navigation_drawer);
        appCompatActivity = MainActivity.this;

        manager = getSupportFragmentManager();

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Đang xử lý...");

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        viewMap = mapFragment.getView();

        buildGoogleApiClient();

        overridePendingTransition(R.animator.start_nothing, R.animator.start_nothing);

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setItemIconTintList(null);

        manager.beginTransaction().replace(R.id.main_layout_maps, mainFragment, mainFragment.getTag()).commit();

        drawingRote = new DrawingRote();

        nameRoute = new String[7];
        nameRoute[0] = "Không";
        nameRoute[1] = "Nâu";
        nameRoute[2] = "Xanh Biển";
        nameRoute[3] = "Đỏ";
        nameRoute[4] = "Xanh Lá";
        nameRoute[5] = "Vàng";
        nameRoute[6] = "Hồng";
        routeNavigationMenuSpinner = (Spinner) navigationView.getMenu().findItem(R.id.navigation_drawer_item3).getActionView();
        routeNavigationMenuSpinner.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, nameRoute));
        routeNavigationMenuSpinner.setOnItemSelectedListener(this);

        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        storageReference = FirebaseStorage.getInstance().getReference();
        firebaseStorage = FirebaseStorage.getInstance();
        mStorageRef = firebaseStorage.getReferenceFromUrl("gs://becamex-tokyu-bus.appspot.com");
        firebaseGetLocation = new Firebase("https://becamex-tokyu-bus.firebaseio.com/User/Staff/Driver");
        if (checkGooglePlayServices() == true) {
            firebaseGetLocation.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    Iterator iteratorName = dataSnapshot.child("Information").getChildren().iterator();
                    Iterator iteratorLocation = dataSnapshot.child("Location").getChildren().iterator();
                    while (iteratorLocation.hasNext()) {
                        double latitude = (((Double) ((DataSnapshot) iteratorLocation.next()).getValue()));
                        double longitude = (((Double) ((DataSnapshot) iteratorLocation.next()).getValue()));
                        iteratorName.next();
                        String name = (((String) ((DataSnapshot) iteratorName.next()).getValue()));
                        iteratorName.next();
                        Location prevLoc = new Location("preLocation");
                        prevLoc.setLatitude(oldlat);
                        prevLoc.setLongitude(oldlong);
                        oldLocation.put(dataSnapshot.getKey(), prevLoc);
                        Geocoder geocoder;
                        List<Address> addresses = null;
                        geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
                        try {
                            addresses = geocoder.getFromLocation(latitude, longitude, 1);
                            //                    arrayListMarkerBus.add(nameRoute);
                            Address address = addresses.get(0);
                            String spi = (String.valueOf(address.getAddressLine(0) + ", " + address.getAddressLine(1) + ", " + address.getAddressLine(2) + ", " + address.getAddressLine(3) + "\n" + "T?a ??: " + latitude + ", " + longitude));
                            LatLng latLng = new LatLng(latitude, longitude);
                            Marker nameMarker = mMap.addMarker(new MarkerOptions().position(latLng).title(name).snippet(spi).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_bus_marker)));
                            hashMapMarkerBus.put(name, nameMarker);
                            allBusMarker.add(nameMarker);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                    Iterator iteratorName = dataSnapshot.child("Information").getChildren().iterator();
                    Iterator iteratorLocation = dataSnapshot.child("Location").getChildren().iterator();
                    while (iteratorLocation.hasNext()) {
                        double latitude = (((Double) ((DataSnapshot) iteratorLocation.next()).getValue()));
                        double longitude = (((Double) ((DataSnapshot) iteratorLocation.next()).getValue()));
                        iteratorName.next();
                        String name = (((String) ((DataSnapshot) iteratorName.next()).getValue()));
                        if (hashMapMarkerBus.get(name) != null) {
                            hashMapMarkerBus.get(name).remove();
                        }
                        iteratorName.next();
                        String phoneNumber = (((String) ((DataSnapshot) iteratorName.next()).getValue()));

                        LatLng latLng = new LatLng(latitude, longitude);

                        Location prevLoc = oldLocation.get(dataSnapshot.getKey());
                        Location newLoc = new Location("newLocation");
                        newLoc.setLatitude(latitude);
                        newLoc.setLongitude(longitude);
                        float bearing = prevLoc.bearingTo(newLoc);

                        Marker markerName = mMap.addMarker(new MarkerOptions().position(latLng).title(name).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_bus_marker)).anchor(0.5f, 0.5f).rotation(bearing).flat(true));
                        hashMapMarkerBus.put(name, markerName);
                        allBusMarker.add(markerName);
                        oldLocation.put(dataSnapshot.getKey(), newLoc);
                    }
                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {

                }
            });
        }
        if (loged == true) {
            loged();
        } else {
            without_login();
        }

        mGoogleApiClientAutoComplete = new GoogleApiClient.Builder(this).enableAutoManage(this, 0 /* clientId */, this).addApi(Places.GEO_DATA_API).build();
        AutocompleteFilter autocompleteFilter = new AutocompleteFilter.Builder().setTypeFilter(Place.TYPE_COUNTRY).setCountry("VN").build();
        mAdapterPlaceAutoComplete = new PlaceAutocompleteAdapter(this, mGoogleApiClientAutoComplete, BOUNDS_GREATER_SYDNEY, autocompleteFilter);
    }

    public static int hight = 0;

    @Override
    public void onClick(View v) {
        if (v == button_login_without) {
            hideKeyboard(this);
            login();
        } else if (v == button_t_register) {
            hideKeyboard(this);
            register();
        } else if (v == register) {
            hideKeyboard(this);
            attemptRegister();
        } else if (v == login) {
            hideKeyboard(this);
            attemptLogin();
        } else if (v == mAvatarLoged) {
            hideKeyboard(this);
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, GALLERY_INTENT);
        } else if (v == back_to_login) {
            hideKeyboard(this);
            login();
        } else if (v == back_to_first) {
            hideKeyboard(this);
            without_login();
        } else if (v == mEmailLoged || v == buttonDropDown) {
            new AlertDialog.Builder(this).setCancelable(false).setTitle("Đăng xuất tài khoản này").setMessage("Bạn có muốn đăng xuất?").setPositiveButton("Đồng ý", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    FirebaseAuth.getInstance().signOut();
                    without_login();
                }
            }).setNegativeButton("Hủy bỏ", null).show();
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.navigation_drawer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_nomal) {
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        } else if (id == R.id.nav_hybrid) {
            mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        } else if (id == R.id.nav_stallite) {
            mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        } else if (id == R.id.nav_terrain) {
            mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
        } else if (id == R.id.nav_none) {
            mMap.setMapType(GoogleMap.MAP_TYPE_NONE);
        } else if (id == R.id.nav_nearby_bustop) {
            showAnimation();
            Location target = new Location("Busstop");
            hashMapNearByBusStop = new HashMap<Double, Double>();
            for (Map.Entry<String, BusStopInfomation> entry : WelcomeScreenActivity.instance.allBusStopInfomation.getAllBus().entrySet()) {
                double latitude = entry.getValue().getLatitude();
                double longitude = entry.getValue().getLongitude();
                target.setLatitude(latitude);
                target.setLongitude(longitude);
                if (myCurrentLocation.distanceTo(target) < 5000) {
                    hashMapNearByBusStop.put(latitude, longitude);
                }
            }
            Thread welcomeThread = new Thread() {
                @Override
                public void run() {
                    try {
                        super.run();
                        sleep(1000);
                    } catch (Exception e) {
                    } finally {
                        Intent i = new Intent(MainActivity.this, NearbyBusStopActivity.class);
                        startActivity(i);
                    }
                }
            };
            welcomeThread.start();

        } else if (id == R.id.nav_bug) {
            showAnimation();
            Thread welcomeThread = new Thread() {
                @Override
                public void run() {
                    try {
                        super.run();
                        sleep(500);
                    } catch (Exception e) {
                    } finally {
                        Intent i = new Intent(MainActivity.this, ReportActivity.class);
                        startActivity(i);
                    }
                }
            };
            welcomeThread.start();
        } else if (id == R.id.busstop) {
            SendBusStopFragment sendBusStopFragment = new SendBusStopFragment();
            manager.beginTransaction().replace(R.id.sendToFireBase, sendBusStopFragment, sendBusStopFragment.getTag()).commit();
        } else if (id == R.id.enable_tracking_location) {
            isTrackingLocationToFireBase = true;
        } else if (id == R.id.disable_tracking_location) {
            isTrackingLocationToFireBase = false;
        } else if (id == R.id.load) {
            File folderBusstop = new File(ReadWriteFileActivity.path + "/BusStop/");
            File folderUpdate = new File(ReadWriteFileActivity.path + "/Update/");
            ReadWriteFileActivity.deleteFile(folderBusstop);
            ReadWriteFileActivity.deleteFile(folderUpdate);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private boolean isTrackingLocationToFireBase = false;

    public static GoogleMap mMap;

    public static ArrayList<LatLng> markerPoints = new ArrayList<LatLng>();

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        mMap.getUiSettings().setMapToolbarEnabled(false);
        //   mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.setOnMarkerClickListener(this);
        mMap.setInfoWindowAdapter(this);
        mMap.setOnMapClickListener(this);
        mMap.setOnMapLongClickListener(this);
        mMap.setOnCameraChangeListener(this);
        allBusStopMarker = new ArrayList<Marker>();
        allBusMarker = new ArrayList<Marker>();
        setUpBusStop();

        emulatorPolyline();
    }


    private boolean isMainFragmentClickHide = false;
    private boolean isDirectionFragmentClickHide = false;
    public static boolean isMainFragmentshow = true;
    public static boolean isDirectionFragmentShow = false;

    @Override
    public void onMapClick(LatLng latLng) {
        if (isKeyBoardVisible() == true) {
            hideKeyboard(this);
        } else {
            if (isMainFragmentshow == true && isMainFragmentClickHide == false && isDirectionFragmentClickHide == false) {
                manager.beginTransaction().replace(R.id.main_layout_maps, mainFragment, mainFragment.getTag()).hide(mainFragment).commit();
                isMainFragmentClickHide = true;
                isMainFragmentshow = false;
            } else if (isDirectionFragmentShow == true && isDirectionFragmentClickHide == false && isMainFragmentClickHide == false) {
                manager.beginTransaction().replace(R.id.direction_layout_maps, directionFragment, directionFragment.getTag()).hide(directionFragment).commit();
                manager.beginTransaction().replace(R.id.layout_place_picker, placePickerFragment, placePickerFragment.getTag()).hide(placePickerFragment).commit();
                isDirectionFragmentClickHide = true;
                isDirectionFragmentShow = false;
            } else if (isMainFragmentshow == false && isMainFragmentClickHide == true && isDirectionFragmentClickHide == false) {
                manager.beginTransaction().replace(R.id.main_layout_maps, mainFragment, mainFragment.getTag()).show(mainFragment).commit();
                isMainFragmentClickHide = false;
                isMainFragmentshow = true;
            } else if (isDirectionFragmentShow == false && isDirectionFragmentClickHide == true && isMainFragmentClickHide == false) {
                manager.beginTransaction().replace(R.id.direction_layout_maps, directionFragment, directionFragment.getTag()).show(directionFragment).commit();
                manager.beginTransaction().replace(R.id.layout_place_picker, placePickerFragment, placePickerFragment.getTag()).show(placePickerFragment).commit();
                isDirectionFragmentClickHide = false;
                isDirectionFragmentShow = true;
            }
        }
        fabDirection.setVisibility(View.VISIBLE);
        fabWalking.setVisibility(View.GONE);
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        hideKeyboard(this);

        checkRemovePoint();

        manager.beginTransaction().replace(R.id.information_direction_layout_maps, informationDirectionFragment, informationDirectionFragment.getTag()).hide(informationDirectionFragment).commit();

        findAddress = mMap.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_action_marker)));

        fabDirection.setVisibility(View.GONE);
        fabWalking.setVisibility(View.VISIBLE);

        markerPoints.add(latLng);
        if (markerPoints.size() == 1) {
            markerPoints.add(currentLocation);
        }
    }

    private void setUpBusStop() {
        if (WelcomeScreenActivity.instance.allBusStopInfomation != null) {
            for (Map.Entry<String, BusStopInfomation> entry : WelcomeScreenActivity.instance.allBusStopInfomation.getAllBus().entrySet()) {
                allBusStopMarker.add(mMap.addMarker(new MarkerOptions().position(entry.getValue().getLatLng()).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_bus_stop))));
            }
        }
    }

//    private void setUpBusStop(int width, int height) {
//        if (WelcomeScreenActivity.instance.allBusStopInfomation != null) {
//            for (Map.Entry<String, BusStopInfomation> entry : WelcomeScreenActivity.instance.allBusStopInfomation.getAllBus().entrySet()) {
//                allBusStopMarker.add(mMap.addMarker(new MarkerOptions().position(entry.getValue().getLatLng()).icon(BitmapDescriptorFactory.fromBitmap(resizeMarkerBusStopIcons(width, height)))));
//            }
//        }
//    }

//    private void setUpBus(int width, int height) {
//        for (Map.Entry<String, Marker> entry : hashMapMarkerBus.entrySet()) {
//            allBusMarker.add(mMap.addMarker(new MarkerOptions().position(entry.getValue().getPosition()).title(entry.getKey()).icon(BitmapDescriptorFactory.fromBitmap(resizeMarkerBusIcons(width, height)))));
//        }
//    }

    @Override
    public void onCameraChange(CameraPosition cameraPosition) {
        if (currentLevelZoom != cameraPosition.zoom) {
            currentLevelZoom = cameraPosition.zoom;
//            for (Marker marker : allBusStopMarker) {
//                marker.remove();
//            }
//            allBusStopMarker.clear();
//            for (Marker marker : allBusMarker) {
//                marker.remove();
//            }
//            allBusMarker.clear();
//            setUpBusStop((int) currentLevelZoom * 4, (int) currentLevelZoom * 4);
//            setUpBus((int) currentLevelZoom * 4, (int) currentLevelZoom * 4);
        }
    }

    private Bitmap resizeMarkerBusStopIcons(int width, int height) {
        Bitmap image = BitmapFactory.decodeResource(getResources(), R.drawable.ic_bus_stop);
        Bitmap resized = Bitmap.createScaledBitmap(image, width, height, true);
        return resized;
    }

    private Bitmap resizeMarkerBusIcons(int width, int height) {
        Bitmap image = BitmapFactory.decodeResource(getResources(), R.drawable.ic_bus_marker);
        Bitmap resized = Bitmap.createScaledBitmap(image, width, height, true);
        return resized;
    }

    public static LatLng locationMarkerClicked;

    @Override
    public boolean onMarkerClick(Marker marker) {
        String s = marker.getSnippet();
        if (marker.getTitle() == null && !marker.equals(findAddress) && !marker.equals(startDirection) && !marker.equals(endDirection)) {
            locationMarkerClicked = marker.getPosition();
            startActivity(new Intent(MainActivity.this, InformationMarkerActivity.class));
        }
        return false;
    }

    private boolean markerSetup = false;

    @Override
    public void onLocationChanged(Location location) {
        myCurrentLocation = location;
        if (isTrackingLocationToFireBase == true) {
            TrackingLocationToFireBase();
        }
        if (myCurrentLocation != null && markerSetup == false) {
            markerSetup = true;
            currentLocation = new LatLng(myCurrentLocation.getLatitude(), myCurrentLocation.getLongitude());
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 16));
        }
        if (myCurrentLocation != null) {
            currentLocation = new LatLng(myCurrentLocation.getLatitude(), myCurrentLocation.getLongitude());
        }
        if (user != null && user.getEmail().contains("@bustokyu.com")) {
            firebasePutLocation = new Firebase("https://becamex-tokyu-bus.firebaseio.com/User/Staff/Driver/" + user.getUid() + "/Location");
            HashMap<String, Double> locationRealTime = new HashMap<String, Double>();
            locationRealTime.put("latitude", location.getLatitude());
            locationRealTime.put("longitude", location.getLongitude());
            firebasePutLocation.setValue(locationRealTime);
        }
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private boolean isKeyBoardVisible() {
        InputMethodManager imm = (InputMethodManager) getApplicationContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);

        if (imm.isAcceptingText()) {
            return true;
        } else {
            return false;
        }
    }

    private final int GALLERY_INTENT = 2;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_INTENT && resultCode == RESULT_OK) {
            progressDialog.setMessage("?ang c?p nh?t ?nh ??i di?n...");
            progressDialog.show();
            Uri uir = data.getData();
            StorageReference filePath = mStorageRef.child("Users").child("Client").child(user.getUid()).child("Profile Image");
            filePath.putFile(uir).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                    Toast.makeText(MainActivity.this, "Upload is " + progress + "% done", Toast.LENGTH_LONG).show();
                }
            }).addOnPausedListener(new OnPausedListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onPaused(UploadTask.TaskSnapshot taskSnapshot) {
                    System.out.println("Upload is paused");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Uri downloadUri = taskSnapshot.getDownloadUrl();
                    mAvatarLoged.setBackground(null);
                    Picasso.with(MainActivity.this).load(downloadUri).fit().centerCrop().into(mAvatarLoged);
                    databaseReference.child("Users").child("Client").child(user.getUid()).child("Information").child("Avatar").setValue(downloadUri.toString());
                    Toast.makeText(MainActivity.this, "C?p nh?t ?nh ??i di?n thành công", Toast.LENGTH_LONG).show();
                    progressDialog.dismiss();
                }
            });
        }
    }

    public void moveLatLgnClickNearby() {
        NearbyBusStopActivity nearbyBusStopActivity = new NearbyBusStopActivity();
        LatLng latLng = nearbyBusStopActivity.getLatLngClickNearBy();
        if (latLng != null) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));
            latLng = null;
            nearbyBusStopActivity.setLatLngClickNearBy(latLng);
        }
    }

    public static void checkRemovePoint() {
        if (MainActivity.startDirection != null) {
            MainActivity.startDirection.remove();
        }
        if (MainActivity.endDirection != null) {
            MainActivity.endDirection.remove();
        }
        if (MainActivity.findAddress != null) {
            MainActivity.findAddress.remove();
        }
        if (MainActivity.markerPoints != null) {
            MainActivity.markerPoints.clear();
        }
        if (MainActivity.polyline != null) {
            MainActivity.polyline.remove();
        }
    }

    private boolean checkGooglePlayServices() {
        final int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (status != ConnectionResult.SUCCESS) {
            Log.e(TAG, GooglePlayServicesUtil.getErrorString(status));
            // ask user to update google play services.
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(status, this, 1);
            dialog.show();
            return false;
        } else {
            Log.i(TAG, GooglePlayServicesUtil.getErrorString(status));
            // google play services is updated.
            //your code goes here...
            return true;
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        if (nameRoute[i] == "Không") {
            drawingRote.drawingRemove();
        } else if (nameRoute[i] == "Nâu") {
            drawingRote.drawingBrown();
        } else if (nameRoute[i] == "Xanh Biển") {
            drawingRote.drawingBlue();
        } else if (nameRoute[i] == "Đỏ") {
            drawingRote.drawingRed();
        } else if (nameRoute[i] == "Xanh Lá") {
            drawingRote.drawingGreen();
        } else if (nameRoute[i] == "Vàng") {
            drawingRote.drawingYello();
        } else if (nameRoute[i] == "Hồng") {
            drawingRote.drawingPink();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    public static void showAnimation() {
        manager.beginTransaction().replace(R.id.animation_loading, animationLoadingFragment, animationLoadingFragment.getTag()).show(animationLoadingFragment).commit();
    }

    public static void hideAnimation() {
        manager.beginTransaction().replace(R.id.animation_loading, animationLoadingFragment, animationLoadingFragment.getTag()).hide(animationLoadingFragment).commit();
    }

    private TreeSet<String> treeSet = new TreeSet<String>();

    private void TrackingLocationToFireBase() {
        String key = databaseReference.push().getKey();
        Firebase firebase = new Firebase("https://becamex-tokyu-bus.firebaseio.com/Bus Stop Information/" + key);
        HashMap<String, Double> hashMap = new HashMap<String, Double>();
        if (!treeSet.contains(currentLocation.toString())) {
            treeSet.add(currentLocation.toString());
            hashMap.put("Latitude", currentLocation.latitude);
            hashMap.put("Longitude", currentLocation.longitude);
        }
        firebase.setValue(hashMap);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //??ng nh?p, ??ng k?//
    private boolean isEmailValid(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 5;
    }

    private void attemptLogin() {
        // Store values at the time of the login attempt.
        String email = mEmailViewLogin.getText().toString().trim();
        String password = mPasswordViewLogin.getText().toString().trim();

        boolean cancel = false;
        View focusView = null;

        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordViewRegister.setError(Html.fromHtml("<font color='red'>M?t kh?u quá ng?n</font>"));
            focusView = mPasswordViewRegister;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailViewLogin.setError(Html.fromHtml("<font color='red'>Vui lòng nh?p email</font>"));
            focusView = mEmailViewLogin;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailViewLogin.setError(Html.fromHtml("<font color='red'>Email không h?p l?</font>"));
            focusView = mEmailViewLogin;
            cancel = true;
        } else if (TextUtils.isEmpty(password)) {
            mPasswordViewLogin.setError(Html.fromHtml("<font color='red'>Vui lòng nh?p m?t kh?u</font>"));
            focusView = mPasswordViewLogin;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            progressDialog.show();
            firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        progressDialog.dismiss();
                        user = firebaseAuth.getCurrentUser();
                        loged();
                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), "Đăng nhập thất bại", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

    private void attemptRegister() {
        final String email = mEmailViewRegister.getText().toString().trim();
        String password = mPasswordViewRegister.getText().toString().trim();
        boolean cancel = false;
        View focusView = null;
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordViewRegister.setError(Html.fromHtml("<font color='red'>Mật khẩu quá ngắn</font>"));
            focusView = mPasswordViewRegister;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailViewRegister.setError(Html.fromHtml("<font color='red'>Vui lòng nhập email</font>"));
            focusView = mEmailViewRegister;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailViewRegister.setError(Html.fromHtml("<font color='red'>Email không hợp lệ</font>"));
            focusView = mEmailViewRegister;
            cancel = true;
        } else if (TextUtils.isEmpty(password)) {
            mPasswordViewRegister.setError(Html.fromHtml("<font color='red'>Vui lòng nhập mật khẩu</font>"));
            focusView = mPasswordViewRegister;
            cancel = true;
        } else if (!mConfirmPasswordViewRegister.getText().toString().equals(mPasswordViewRegister.getText().toString())) {
            mPasswordViewRegister.setError(Html.fromHtml("<font color='red'>Mật khẩu không khớpp</font>"));
            mConfirmPasswordViewRegister.setError(Html.fromHtml("<font color='red'>Mật khẩu không khớpp</font>"));
            focusView = mPasswordViewRegister;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            progressDialog.show();
            firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        String name = mNameViewRegister.getText().toString().trim();
                        String password = mPasswordViewRegister.getText().toString().trim();
                        String phoneNumber = mPhoneNumberRegister.getText().toString().trim();
                        user = firebaseAuth.getCurrentUser();
                        UserProfileChangeRequest userProfileChangeRequest = new UserProfileChangeRequest.Builder().setDisplayName(name).build();
                        user.updateProfile(userProfileChangeRequest);
                        Firebase firebaseRegister;
                        if (!email.contains("@bustokyu.com")) {
                            firebaseRegister = new Firebase("https://becamex-tokyu-bus.firebaseio.com/User/Client/" + user.getUid() + "/Information");
                        } else {
                            firebaseRegister = new Firebase("https://becamex-tokyu-bus.firebaseio.com/User/Staff/Driver/" + user.getUid() + "/Information");
                        }
                        HashMap<String, String> register = new HashMap<String, String>();
                        register.put("Name", name);
                        register.put("Email", user.getEmail());
                        register.put("Phone Number", phoneNumber);
                        register.put("Password", password);
                        register.put("Avatar", null);
                        firebaseRegister.setValue(register);
                        hideKeyboard(MainActivity.this);
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), "??ng k? thành công", Toast.LENGTH_LONG).show();
                        login();
                    } else {
                        hideKeyboard(MainActivity.this);
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), "Email ?ã t?n t?i", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

    private void loged() {
        navigationView.removeHeaderView(header);
        header = LayoutInflater.from(this).inflate(R.layout.nav_header_navigation_drawer_loged, null);
        navigationView.addHeaderView(header);

        mAvatarLoged = (ImageView) (header.findViewById(R.id.loged_avatar));
        mEmailLoged = (TextView) (header.findViewById(R.id.loged_email));
        mNameLoged = (TextView) (header.findViewById(R.id.loged_name));
        buttonDropDown = (ImageView) (header.findViewById(R.id.button_drop_down));

        user = firebaseAuth.getCurrentUser();
        String id1 = user.getUid().toString();
        firebasePutLocation = new Firebase("https://becamex-tokyu-bus.firebaseio.com/User/Staff/Driver/" + user.getUid());
        storageReference = firebaseStorage.getReferenceFromUrl("gs://becamex-tokyu-bus.appspot.com");
        storageReference.child("Users").child("Client").child(user.getUid()).child("Profile Image").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.with(MainActivity.this).load(uri).fit().centerCrop().into(mAvatarLoged);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Picasso.with(MainActivity.this).load("https://firebasestorage.googleapis.com/v0/b/becamex-tokyu-bus.appspot.com/o/ic_bus_tokyu.png?alt=media&token=d64fcc44-3398-430d-a9db-eda8dbe2e015").fit().centerCrop().into(mAvatarLoged);
                Toast.makeText(MainActivity.this, "Nh?n vào iCon Bus Tokyu ?? ??i ?nh ??i di?n", Toast.LENGTH_LONG).show();
            }
        });
        mEmailLoged.setText(user.getEmail());
        mNameLoged.setText(user.getDisplayName());
        mAvatarLoged.setOnClickListener(this);
        buttonDropDown.setOnClickListener(this);
        mEmailLoged.setOnClickListener(this);
    }

    private void without_login() {
        navigationView.removeHeaderView(header);
        header = LayoutInflater.from(this).inflate(R.layout.nav_header_navigation_drawer_without_login, null);
        navigationView.addHeaderView(header);
        button_login_without = (Button) (header.findViewById(R.id.button_login_without));
        button_login_without.setOnClickListener(this);
    }

    private void login() {
        navigationView.removeHeaderView(header);
        header = LayoutInflater.from(this).inflate(R.layout.nav_header_navigation_drawer_login, null);
        navigationView.addHeaderView(header);
        button_t_register = (TextView) (header.findViewById(R.id.open_layout_register));
        button_t_register.setOnClickListener(this);
        back_to_first = (Button) (header.findViewById(R.id.login_back_first));
        back_to_first.setOnClickListener(this);
        login = (Button) (header.findViewById(R.id.button_login));
        login.setOnClickListener(this);
        mEmailViewLogin = (EditText) (findViewById(R.id.login_email));
        mPasswordViewLogin = (EditText) (findViewById(R.id.login_password));
    }

    private void register() {
        navigationView.removeHeaderView(header);
        header = LayoutInflater.from(this).inflate(R.layout.nav_header_navigation_drawer_register, null);
        navigationView.addHeaderView(header);
        register = (Button) (header.findViewById(R.id.button_register));
        back_to_login = (Button) (header.findViewById(R.id.register_back_login));
        register.setOnClickListener(this);
        back_to_login.setOnClickListener(this);

        mNameViewRegister = (EditText) (findViewById(R.id.register_name));
        mEmailViewRegister = (EditText) (findViewById(R.id.register_email));
        mPasswordViewRegister = (EditText) (findViewById(R.id.register_password));
        mConfirmPasswordViewRegister = (EditText) (findViewById(R.id.register_confirm_password));
        mPhoneNumberRegister = (EditText) (findViewById(R.id.register_phone_number));
    }
    //??ng nh?p, ??ng k?//
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onPause() {
        super.onPause();
//        stop location updates when Activity is no longer active
        if (mGoogleApiClient != null) {
            if (checkGooglePlayServices()) {
                LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            }
        }
    }


    /**
     * Called when the Activity could not connect to Google Play services and the auto manager
     * could resolve the error automatically.
     * In this case the API is not available and notify the user.
     *
     * @param connectionResult can be inspected to determine the cause of the failure
     */
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

        Log.e(TAG, "onConnectionFailed: ConnectionResult.getErrorCode() = "
                + connectionResult.getErrorCode());

        // TODO(Developer): Check error code and notify the user of error state and resolution.
//        Toast.makeText(this,
//                "Could not connect to Google API Client: Error " + connectionResult.getErrorCode(),
//                Toast.LENGTH_SHORT).show();

    }

    private void emulatorPolyline() {
        List<LatLng> allPoints = new ArrayList<LatLng>();
        allPoints.add(new LatLng(11.0532233,106.668649));
        allPoints.add(new LatLng(11.053026626866,106.66867105042));
        allPoints.add(new LatLng(11.052829953731,106.66869310084));
        allPoints.add(new LatLng(11.052633280597,106.66871515126));
        allPoints.add(new LatLng(11.052436607463,106.66873720168));
        allPoints.add(new LatLng(11.052239934328,106.66875925211));
        allPoints.add(new LatLng(11.052043261194,106.66878130253));
        allPoints.add(new LatLng(11.051846588059,106.66880335295));
        allPoints.add(new LatLng(11.051649914925,106.66882540337));
        allPoints.add(new LatLng(11.051453241791,106.66884745379));
        allPoints.add(new LatLng(11.051256568656,106.66886950421));
        allPoints.add(new LatLng(11.051059895522,106.66889155463));
        allPoints.add(new LatLng(11.050863222388,106.66891360505));
        allPoints.add(new LatLng(11.050666549253,106.66893565547));
        allPoints.add(new LatLng(11.050469876119,106.6689577059));
        allPoints.add(new LatLng(11.050273202985,106.66897975632));
        allPoints.add(new LatLng(11.05007652985,106.66900180674));
        allPoints.add(new LatLng(11.049879856716,106.66902385716));
        allPoints.add(new LatLng(11.049683183582,106.66904590758));
        allPoints.add(new LatLng(11.049486510447,106.669067958));
        allPoints.add(new LatLng(11.049289837313,106.66909000842));
        allPoints.add(new LatLng(11.049093164178,106.66911205884));
        allPoints.add(new LatLng(11.048896491044,106.66913410926));
        allPoints.add(new LatLng(11.04869981791,106.66915615969));
        allPoints.add(new LatLng(11.048503144775,106.66917821011));
        allPoints.add(new LatLng(11.048306471641,106.66920026053));
        allPoints.add(new LatLng(11.048109798507,106.66922231095));
        allPoints.add(new LatLng(11.047913125372,106.66924436137));
        allPoints.add(new LatLng(11.047716452238,106.66926641179));
        allPoints.add(new LatLng(11.047519779104,106.66928846221));
        allPoints.add(new LatLng(11.047323105969,106.66931051263));
        allPoints.add(new LatLng(11.047126432835,106.66933256305));
        allPoints.add(new LatLng(11.0469297597,106.66935461348));
        allPoints.add(new LatLng(11.046733086566,106.6693766639));
        allPoints.add(new LatLng(11.046536413432,106.66939871432));
        allPoints.add(new LatLng(11.046339740297,106.66942076474));
        allPoints.add(new LatLng(11.046143067163,106.66944281516));
        allPoints.add(new LatLng(11.045946394029,106.66946486558));
        allPoints.add(new LatLng(11.045749720894,106.669486916));
        allPoints.add(new LatLng(11.04555304776,106.66950896642));
        allPoints.add(new LatLng(11.045356374626,106.66953101684));
        allPoints.add(new LatLng(11.045159701491,106.66955306727));
        allPoints.add(new LatLng(11.044963028357,106.66957511769));
        allPoints.add(new LatLng(11.044766355222,106.66959716811));
        allPoints.add(new LatLng(11.044569682088,106.66961921853));
        allPoints.add(new LatLng(11.044373008954,106.66964126895));
        allPoints.add(new LatLng(11.044176335819,106.66966331937));
        allPoints.add(new LatLng(11.043979662685,106.66968536979));
        allPoints.add(new LatLng(11.043782989551,106.66970742021));
        allPoints.add(new LatLng(11.043586316416,106.66972947063));
        allPoints.add(new LatLng(11.043389643282,106.66975152106));
        allPoints.add(new LatLng(11.043192970148,106.66977357148));
        allPoints.add(new LatLng(11.042996297013,106.6697956219));
        allPoints.add(new LatLng(11.042799623879,106.66981767232));
        allPoints.add(new LatLng(11.0427637,106.6698217));
        allPoints.add(new LatLng(11.042776898568,106.67002284396));
        allPoints.add(new LatLng(11.042790097135,106.67022398792));
        allPoints.add(new LatLng(11.042803295703,106.67042513188));
        allPoints.add(new LatLng(11.04281649427,106.67062627584));
        allPoints.add(new LatLng(11.042829692838,106.6708274198));
        allPoints.add(new LatLng(11.042842891406,106.67102856376));
        allPoints.add(new LatLng(11.042856089973,106.67122970772));
        allPoints.add(new LatLng(11.042869288541,106.67143085168));
        allPoints.add(new LatLng(11.042882487109,106.67163199564));
        allPoints.add(new LatLng(11.042895685676,106.6718331396));
        allPoints.add(new LatLng(11.042908884244,106.67203428356));
        allPoints.add(new LatLng(11.042922082811,106.67223542752));
        allPoints.add(new LatLng(11.042935281379,106.67243657148));
        allPoints.add(new LatLng(11.042948479947,106.67263771544));
        allPoints.add(new LatLng(11.042961678514,106.6728388594));
        allPoints.add(new LatLng(11.042974877082,106.67304000336));
        allPoints.add(new LatLng(11.04298807565,106.67324114732));
        allPoints.add(new LatLng(11.043001274217,106.67344229129));
        allPoints.add(new LatLng(11.043014472785,106.67364343525));
        allPoints.add(new LatLng(11.043027671352,106.67384457921));
        allPoints.add(new LatLng(11.04304086992,106.67404572317));
        allPoints.add(new LatLng(11.043054068488,106.67424686713));
        allPoints.add(new LatLng(11.043067267055,106.67444801109));
        allPoints.add(new LatLng(11.043080465623,106.67464915505));
        allPoints.add(new LatLng(11.043093664191,106.67485029901));
        allPoints.add(new LatLng(11.0430981,106.6749179));
        allPoints.add(new LatLng(11.042900242098,106.6749188867));
        allPoints.add(new LatLng(11.042702384195,106.67491987341));
        allPoints.add(new LatLng(11.042504526293,106.67492086011));
        allPoints.add(new LatLng(11.042306668391,106.67492184682));
        allPoints.add(new LatLng(11.042108810489,106.67492283352));
        allPoints.add(new LatLng(11.041910952586,106.67492382023));
        allPoints.add(new LatLng(11.041713094684,106.67492480693));
        allPoints.add(new LatLng(11.041515236782,106.67492579364));
        allPoints.add(new LatLng(11.041317378879,106.67492678034));
        allPoints.add(new LatLng(11.041119520977,106.67492776705));
        allPoints.add(new LatLng(11.040921663075,106.67492875375));
        allPoints.add(new LatLng(11.040723805173,106.67492974046));
        allPoints.add(new LatLng(11.04052594727,106.67493072716));
        allPoints.add(new LatLng(11.040328089368,106.67493171387));
        allPoints.add(new LatLng(11.040130231466,106.67493270057));
        allPoints.add(new LatLng(11.039932373563,106.67493368727));
        allPoints.add(new LatLng(11.039734515661,106.67493467398));
        allPoints.add(new LatLng(11.039536657759,106.67493566068));
        allPoints.add(new LatLng(11.039338799857,106.67493664739));
        allPoints.add(new LatLng(11.039140941954,106.67493763409));
        allPoints.add(new LatLng(11.038943084052,106.6749386208));
        allPoints.add(new LatLng(11.03874522615,106.6749396075));
        allPoints.add(new LatLng(11.038547368247,106.67494059421));
        allPoints.add(new LatLng(11.038349510345,106.67494158091));
        allPoints.add(new LatLng(11.038151652443,106.67494256762));
        allPoints.add(new LatLng(11.037953794541,106.67494355432));
        allPoints.add(new LatLng(11.037755936638,106.67494454103));
        allPoints.add(new LatLng(11.037558078736,106.67494552773));
        allPoints.add(new LatLng(11.037360220834,106.67494651444));
        allPoints.add(new LatLng(11.037162362932,106.67494750114));
        allPoints.add(new LatLng(11.036964505029,106.67494848784));
        allPoints.add(new LatLng(11.036766647127,106.67494947455));
        allPoints.add(new LatLng(11.036568789225,106.67495046125));
        allPoints.add(new LatLng(11.036370931322,106.67495144796));
        allPoints.add(new LatLng(11.03617307342,106.67495243466));
        allPoints.add(new LatLng(11.035975215518,106.67495342137));
        allPoints.add(new LatLng(11.035777357616,106.67495440807));
        allPoints.add(new LatLng(11.035579499713,106.67495539478));
        allPoints.add(new LatLng(11.035381641811,106.67495638148));
        allPoints.add(new LatLng(11.035183783909,106.67495736819));
        allPoints.add(new LatLng(11.034985926006,106.67495835489));
        allPoints.add(new LatLng(11.034788068104,106.6749593416));
        allPoints.add(new LatLng(11.034590210202,106.6749603283));
        allPoints.add(new LatLng(11.0343923523,106.67496131501));
        allPoints.add(new LatLng(11.034194494397,106.67496230171));
        allPoints.add(new LatLng(11.033996636495,106.67496328841));
        allPoints.add(new LatLng(11.033798778593,106.67496427512));
        allPoints.add(new LatLng(11.03360092069,106.67496526182));
        allPoints.add(new LatLng(11.033403062788,106.67496624853));
        allPoints.add(new LatLng(11.033205204886,106.67496723523));
        allPoints.add(new LatLng(11.033007346984,106.67496822194));
        allPoints.add(new LatLng(11.032809489081,106.67496920864));
        allPoints.add(new LatLng(11.032611631179,106.67497019535));
        allPoints.add(new LatLng(11.032413773277,106.67497118205));
        allPoints.add(new LatLng(11.032215915374,106.67497216876));
        allPoints.add(new LatLng(11.032018057472,106.67497315546));
        allPoints.add(new LatLng(11.03182019957,106.67497414217));
        allPoints.add(new LatLng(11.031622341668,106.67497512887));
        allPoints.add(new LatLng(11.031424483765,106.67497611557));
        allPoints.add(new LatLng(11.031226625863,106.67497710228));
        allPoints.add(new LatLng(11.031028767961,106.67497808898));
        allPoints.add(new LatLng(11.030830910058,106.67497907569));
        allPoints.add(new LatLng(11.030633052156,106.67498006239));
        allPoints.add(new LatLng(11.030435194254,106.6749810491));
        allPoints.add(new LatLng(11.030237336352,106.6749820358));
        allPoints.add(new LatLng(11.030039478449,106.67498302251));
        allPoints.add(new LatLng(11.029841620547,106.67498400921));
        allPoints.add(new LatLng(11.029643762645,106.67498499592));
        allPoints.add(new LatLng(11.029445904743,106.67498598262));
        allPoints.add(new LatLng(11.02924804684,106.67498696933));
        allPoints.add(new LatLng(11.029050188938,106.67498795603));
        allPoints.add(new LatLng(11.028852331036,106.67498894274));
        allPoints.add(new LatLng(11.028654473133,106.67498992944));
        allPoints.add(new LatLng(11.028456615231,106.67499091614));
        allPoints.add(new LatLng(11.028258757329,106.67499190285));
        allPoints.add(new LatLng(11.028060899427,106.67499288955));
        allPoints.add(new LatLng(11.027863041524,106.67499387626));
        allPoints.add(new LatLng(11.027665183622,106.67499486296));
        allPoints.add(new LatLng(11.02746732572,106.67499584967));
        allPoints.add(new LatLng(11.027269467817,106.67499683637));
        allPoints.add(new LatLng(11.027071609915,106.67499782308));
        allPoints.add(new LatLng(11.026873752013,106.67499880978));
        allPoints.add(new LatLng(11.026675894111,106.67499979649));
        allPoints.add(new LatLng(11.026478036208,106.67500078319));
        allPoints.add(new LatLng(11.026280178306,106.6750017699));
        allPoints.add(new LatLng(11.026082320404,106.6750027566));
        allPoints.add(new LatLng(11.025884462501,106.67500374331));
        allPoints.add(new LatLng(11.025686604599,106.67500473001));
        allPoints.add(new LatLng(11.025488746697,106.67500571671));
        allPoints.add(new LatLng(11.025290888795,106.67500670342));
        allPoints.add(new LatLng(11.025093030892,106.67500769012));
        allPoints.add(new LatLng(11.02489517299,106.67500867683));
        allPoints.add(new LatLng(11.024697315088,106.67500966353));
        allPoints.add(new LatLng(11.024499457185,106.67501065024));
        allPoints.add(new LatLng(11.024301599283,106.67501163694));
        allPoints.add(new LatLng(11.024103741381,106.67501262365));
        allPoints.add(new LatLng(11.023905883479,106.67501361035));
        allPoints.add(new LatLng(11.023708025576,106.67501459706));
        allPoints.add(new LatLng(11.023510167674,106.67501558376));
        allPoints.add(new LatLng(11.023312309772,106.67501657047));
        allPoints.add(new LatLng(11.023114451869,106.67501755717));
        allPoints.add(new LatLng(11.022916593967,106.67501854388));
        allPoints.add(new LatLng(11.022718736065,106.67501953058));
        allPoints.add(new LatLng(11.022520878163,106.67502051728));
        allPoints.add(new LatLng(11.02232302026,106.67502150399));
        allPoints.add(new LatLng(11.022125162358,106.67502249069));
        allPoints.add(new LatLng(11.021927304456,106.6750234774));
        allPoints.add(new LatLng(11.021729446553,106.6750244641));
        allPoints.add(new LatLng(11.021531588651,106.67502545081));
        allPoints.add(new LatLng(11.021333730749,106.67502643751));
        allPoints.add(new LatLng(11.021135872847,106.67502742422));
        allPoints.add(new LatLng(11.020938014944,106.67502841092));
        allPoints.add(new LatLng(11.020740157042,106.67502939763));
        allPoints.add(new LatLng(11.02054229914,106.67503038433));
        allPoints.add(new LatLng(11.020344441238,106.67503137104));
        allPoints.add(new LatLng(11.020146583335,106.67503235774));
        allPoints.add(new LatLng(11.019948725433,106.67503334445));
        allPoints.add(new LatLng(11.019750867531,106.67503433115));
        allPoints.add(new LatLng(11.019553009628,106.67503531785));
        allPoints.add(new LatLng(11.019355151726,106.67503630456));
        allPoints.add(new LatLng(11.019157293824,106.67503729126));
        allPoints.add(new LatLng(11.018959435922,106.67503827797));
        allPoints.add(new LatLng(11.018761578019,106.67503926467));
        allPoints.add(new LatLng(11.018563720117,106.67504025138));
        allPoints.add(new LatLng(11.018365862215,106.67504123808));
        allPoints.add(new LatLng(11.018168004312,106.67504222479));
        allPoints.add(new LatLng(11.01797014641,106.67504321149));
        allPoints.add(new LatLng(11.017772288508,106.6750441982));
        allPoints.add(new LatLng(11.017574430606,106.6750451849));
        allPoints.add(new LatLng(11.017376572703,106.67504617161));
        allPoints.add(new LatLng(11.017178714801,106.67504715831));
        allPoints.add(new LatLng(11.016980856899,106.67504814502));
        allPoints.add(new LatLng(11.016782998996,106.67504913172));
        allPoints.add(new LatLng(11.016585141094,106.67505011842));
        allPoints.add(new LatLng(11.016387283192,106.67505110513));
        allPoints.add(new LatLng(11.01618942529,106.67505209183));
        allPoints.add(new LatLng(11.015991567387,106.67505307854));
        allPoints.add(new LatLng(11.015793709485,106.67505406524));
        allPoints.add(new LatLng(11.015595851583,106.67505505195));
        allPoints.add(new LatLng(11.01539799368,106.67505603865));
        allPoints.add(new LatLng(11.015200135778,106.67505702536));
        allPoints.add(new LatLng(11.015002277876,106.67505801206));
        allPoints.add(new LatLng(11.014804419974,106.67505899877));
        allPoints.add(new LatLng(11.014606562071,106.67505998547));
        allPoints.add(new LatLng(11.014408704169,106.67506097218));
        allPoints.add(new LatLng(11.014210846267,106.67506195888));
        allPoints.add(new LatLng(11.014012988364,106.67506294559));
        allPoints.add(new LatLng(11.013815130462,106.67506393229));
        allPoints.add(new LatLng(11.01361727256,106.67506491899));
        allPoints.add(new LatLng(11.013419414658,106.6750659057));
        allPoints.add(new LatLng(11.013221556755,106.6750668924));
        allPoints.add(new LatLng(11.013023698853,106.67506787911));
        allPoints.add(new LatLng(11.0129393,106.6750683));
        allPoints.add(new LatLng(11.013018773417,106.67525289757));
        allPoints.add(new LatLng(11.013098246835,106.67543749513));
        allPoints.add(new LatLng(11.013177720252,106.6756220927));
        allPoints.add(new LatLng(11.01325719367,106.67580669026));
        allPoints.add(new LatLng(11.013336667087,106.67599128783));
        allPoints.add(new LatLng(11.013416140505,106.67617588539));
        allPoints.add(new LatLng(11.013495613922,106.67636048296));
        allPoints.add(new LatLng(11.0135193,106.6764155));
        allPoints.add(new LatLng(11.013323432589,106.67638696403));
        allPoints.add(new LatLng(11.013127565177,106.67635842807));
        allPoints.add(new LatLng(11.012931697766,106.6763298921));
        allPoints.add(new LatLng(11.012735830354,106.67630135614));
        allPoints.add(new LatLng(11.012539962943,106.67627282017));
        allPoints.add(new LatLng(11.012344095531,106.6762442842));
        allPoints.add(new LatLng(11.01214822812,106.67621574824));
        allPoints.add(new LatLng(11.011952360708,106.67618721227));
        allPoints.add(new LatLng(11.011756493297,106.67615867631));
        allPoints.add(new LatLng(11.011560625885,106.67613014034));
        allPoints.add(new LatLng(11.011364758474,106.67610160437));
        allPoints.add(new LatLng(11.011168891062,106.67607306841));
        allPoints.add(new LatLng(11.010973023651,106.67604453244));
        allPoints.add(new LatLng(11.010777156239,106.67601599648));
        allPoints.add(new LatLng(11.010581288828,106.67598746051));
        allPoints.add(new LatLng(11.010385421416,106.67595892455));
        allPoints.add(new LatLng(11.010189554005,106.67593038858));
        allPoints.add(new LatLng(11.009993686594,106.67590185261));
        allPoints.add(new LatLng(11.009797819182,106.67587331665));
        allPoints.add(new LatLng(11.009601951771,106.67584478068));
        allPoints.add(new LatLng(11.009406084359,106.67581624472));
        allPoints.add(new LatLng(11.009210216948,106.67578770875));
        allPoints.add(new LatLng(11.009014349536,106.67575917278));
        allPoints.add(new LatLng(11.008818482125,106.67573063682));
        allPoints.add(new LatLng(11.008622614713,106.67570210085));
        allPoints.add(new LatLng(11.008426747302,106.67567356489));
        allPoints.add(new LatLng(11.00823087989,106.67564502892));
        allPoints.add(new LatLng(11.008035012479,106.67561649295));
        allPoints.add(new LatLng(11.007839145067,106.67558795699));
        allPoints.add(new LatLng(11.007643277656,106.67555942102));
        allPoints.add(new LatLng(11.007447410244,106.67553088506));
        allPoints.add(new LatLng(11.007251542833,106.67550234909));
        allPoints.add(new LatLng(11.007055675421,106.67547381312));
        allPoints.add(new LatLng(11.00685980801,106.67544527716));
        allPoints.add(new LatLng(11.006663940599,106.67541674119));
        allPoints.add(new LatLng(11.006468073187,106.67538820523));
        allPoints.add(new LatLng(11.006272205776,106.67535966926));
        allPoints.add(new LatLng(11.006076338364,106.67533113329));
        allPoints.add(new LatLng(11.005880470953,106.67530259733));
        allPoints.add(new LatLng(11.005684603541,106.67527406136));
        allPoints.add(new LatLng(11.00548873613,106.6752455254));
        allPoints.add(new LatLng(11.005292868718,106.67521698943));
        allPoints.add(new LatLng(11.005097001307,106.67518845346));
        allPoints.add(new LatLng(11.004901133895,106.6751599175));
        allPoints.add(new LatLng(11.004705266484,106.67513138153));
        allPoints.add(new LatLng(11.004509399072,106.67510284557));
        allPoints.add(new LatLng(11.004313531661,106.6750743096));
        allPoints.add(new LatLng(11.004117664249,106.67504577364));
        allPoints.add(new LatLng(11.003921796838,106.67501723767));
        allPoints.add(new LatLng(11.003725929426,106.6749887017));
        allPoints.add(new LatLng(11.003530062015,106.67496016574));
        allPoints.add(new LatLng(11.003334194604,106.67493162977));
        allPoints.add(new LatLng(11.003138327192,106.67490309381));
        allPoints.add(new LatLng(11.002942459781,106.67487455784));
        allPoints.add(new LatLng(11.002746592369,106.67484602187));
        allPoints.add(new LatLng(11.002550724958,106.67481748591));
        allPoints.add(new LatLng(11.002354857546,106.67478894994));
        allPoints.add(new LatLng(11.002158990135,106.67476041398));
        allPoints.add(new LatLng(11.001963122723,106.67473187801));
        allPoints.add(new LatLng(11.001767255312,106.67470334204));
        allPoints.add(new LatLng(11.0015713879,106.67467480608));
        allPoints.add(new LatLng(11.001375520489,106.67464627011));
        allPoints.add(new LatLng(11.001179653077,106.67461773415));
        allPoints.add(new LatLng(11.000983785666,106.67458919818));
        allPoints.add(new LatLng(11.000787918254,106.67456066221));
        allPoints.add(new LatLng(11.000592050843,106.67453212625));
        allPoints.add(new LatLng(11.000396183431,106.67450359028));
        allPoints.add(new LatLng(11.00020031602,106.67447505432));
        allPoints.add(new LatLng(11.000004448609,106.67444651835));
        allPoints.add(new LatLng(10.999808581197,106.67441798238));
        allPoints.add(new LatLng(10.999612713786,106.67438944642));
        allPoints.add(new LatLng(10.999416846374,106.67436091045));
        allPoints.add(new LatLng(10.999220978963,106.67433237449));
        allPoints.add(new LatLng(10.999025111551,106.67430383852));
        allPoints.add(new LatLng(10.99882924414,106.67427530255));
        allPoints.add(new LatLng(10.998633376728,106.67424676659));
        allPoints.add(new LatLng(10.998437509317,106.67421823062));
        allPoints.add(new LatLng(10.998241641905,106.67418969466));
        allPoints.add(new LatLng(10.998045774494,106.67416115869));
        allPoints.add(new LatLng(10.997849907082,106.67413262273));
        allPoints.add(new LatLng(10.997654039671,106.67410408676));
        allPoints.add(new LatLng(10.997458172259,106.67407555079));
        allPoints.add(new LatLng(10.997262304848,106.67404701483));
        allPoints.add(new LatLng(10.997066437436,106.67401847886));
        allPoints.add(new LatLng(10.996870570025,106.6739899429));
        allPoints.add(new LatLng(10.996674702614,106.67396140693));
        allPoints.add(new LatLng(10.996478835202,106.67393287096));
        allPoints.add(new LatLng(10.996282967791,106.673904335));
        allPoints.add(new LatLng(10.996087100379,106.67387579903));
        allPoints.add(new LatLng(10.995891232968,106.67384726307));
        allPoints.add(new LatLng(10.995695365556,106.6738187271));
        allPoints.add(new LatLng(10.995499498145,106.67379019113));
        allPoints.add(new LatLng(10.995303630733,106.67376165517));
        allPoints.add(new LatLng(10.995107763322,106.6737331192));
        allPoints.add(new LatLng(10.99491189591,106.67370458324));
        allPoints.add(new LatLng(10.994716028499,106.67367604727));
        allPoints.add(new LatLng(10.994520161087,106.6736475113));
        allPoints.add(new LatLng(10.994324293676,106.67361897534));
        allPoints.add(new LatLng(10.994128426264,106.67359043937));
        allPoints.add(new LatLng(10.993932558853,106.67356190341));
        allPoints.add(new LatLng(10.993736691441,106.67353336744));
        allPoints.add(new LatLng(10.99354082403,106.67350483147));
        allPoints.add(new LatLng(10.993344956619,106.67347629551));
        allPoints.add(new LatLng(10.993149089207,106.67344775954));
        allPoints.add(new LatLng(10.992953221796,106.67341922358));
        allPoints.add(new LatLng(10.992757354384,106.67339068761));
        allPoints.add(new LatLng(10.992561486973,106.67336215165));
        allPoints.add(new LatLng(10.992365619561,106.67333361568));
        allPoints.add(new LatLng(10.99216975215,106.67330507971));
        allPoints.add(new LatLng(10.991973884738,106.67327654375));
        allPoints.add(new LatLng(10.991778017327,106.67324800778));
        allPoints.add(new LatLng(10.991582149915,106.67321947182));
        allPoints.add(new LatLng(10.991386282504,106.67319093585));
        allPoints.add(new LatLng(10.991190415092,106.67316239988));
        allPoints.add(new LatLng(10.990994547681,106.67313386392));
        allPoints.add(new LatLng(10.990798680269,106.67310532795));
        allPoints.add(new LatLng(10.990602812858,106.67307679199));
        allPoints.add(new LatLng(10.990406945446,106.67304825602));
        allPoints.add(new LatLng(10.990211078035,106.67301972005));
        allPoints.add(new LatLng(10.990097,106.6730031));
        allPoints.add(new LatLng(10.989902690164,106.67304111118));
        allPoints.add(new LatLng(10.989708380328,106.67307912237));
        allPoints.add(new LatLng(10.989514070493,106.67311713355));
        allPoints.add(new LatLng(10.989319760657,106.67315514474));
        allPoints.add(new LatLng(10.989125450821,106.67319315592));
        allPoints.add(new LatLng(10.988931140985,106.67323116711));
        allPoints.add(new LatLng(10.988736831149,106.67326917829));
        allPoints.add(new LatLng(10.988542521313,106.67330718948));
        allPoints.add(new LatLng(10.988348211478,106.67334520066));
        allPoints.add(new LatLng(10.988153901642,106.67338321185));
        allPoints.add(new LatLng(10.987959591806,106.67342122303));
        allPoints.add(new LatLng(10.98776528197,106.67345923421));
        allPoints.add(new LatLng(10.987570972134,106.6734972454));
        allPoints.add(new LatLng(10.987376662299,106.67353525658));
        allPoints.add(new LatLng(10.987182352463,106.67357326777));
        allPoints.add(new LatLng(10.986988042627,106.67361127895));
        allPoints.add(new LatLng(10.986793732791,106.67364929014));
        allPoints.add(new LatLng(10.986599422955,106.67368730132));
        allPoints.add(new LatLng(10.986405113119,106.67372531251));
        allPoints.add(new LatLng(10.986210803284,106.67376332369));
        allPoints.add(new LatLng(10.986016493448,106.67380133488));
        allPoints.add(new LatLng(10.985822183612,106.67383934606));
        allPoints.add(new LatLng(10.985627873776,106.67387735724));
        allPoints.add(new LatLng(10.98543356394,106.67391536843));
        allPoints.add(new LatLng(10.985239254104,106.67395337961));
        allPoints.add(new LatLng(10.985044944269,106.6739913908));
        allPoints.add(new LatLng(10.984850634433,106.67402940198));
        allPoints.add(new LatLng(10.984656324597,106.67406741317));
        allPoints.add(new LatLng(10.984462014761,106.67410542435));
        allPoints.add(new LatLng(10.984267704925,106.67414343554));
        allPoints.add(new LatLng(10.98407339509,106.67418144672));
        allPoints.add(new LatLng(10.983879085254,106.67421945791));
        allPoints.add(new LatLng(10.983684775418,106.67425746909));
        allPoints.add(new LatLng(10.983490465582,106.67429548027));
        allPoints.add(new LatLng(10.983296155746,106.67433349146));
        allPoints.add(new LatLng(10.98310184591,106.67437150264));
        allPoints.add(new LatLng(10.982907536075,106.67440951383));
        allPoints.add(new LatLng(10.982713226239,106.67444752501));
        allPoints.add(new LatLng(10.982518916403,106.6744855362));
        allPoints.add(new LatLng(10.9825099,106.6744873));
        allPoints.add(new LatLng(10.982476169125,106.67428869897));
        allPoints.add(new LatLng(10.98244243825,106.67409009794));
        allPoints.add(new LatLng(10.982408707375,106.67389149691));
        allPoints.add(new LatLng(10.9823749765,106.67369289589));
        allPoints.add(new LatLng(10.982341245624,106.67349429486));
        allPoints.add(new LatLng(10.982307514749,106.67329569383));
        allPoints.add(new LatLng(10.982273783874,106.6730970928));
        allPoints.add(new LatLng(10.982240052999,106.67289849177));
        allPoints.add(new LatLng(10.982206322124,106.67269989074));
        allPoints.add(new LatLng(10.982172591249,106.67250128971));
        allPoints.add(new LatLng(10.982138860374,106.67230268868));
        allPoints.add(new LatLng(10.982105129499,106.67210408766));
        allPoints.add(new LatLng(10.982071398623,106.67190548663));
        allPoints.add(new LatLng(10.9820562,106.671816));
        allPoints.add(new LatLng(10.9818584224,106.67182182575));
        allPoints.add(new LatLng(10.9816606448,106.6718276515));
        allPoints.add(new LatLng(10.9814628672,106.67183347726));
        allPoints.add(new LatLng(10.981265089601,106.67183930301));
        allPoints.add(new LatLng(10.981067312001,106.67184512876));
        allPoints.add(new LatLng(10.980869534401,106.67185095451));
        allPoints.add(new LatLng(10.980671756801,106.67185678026));
        allPoints.add(new LatLng(10.980473979201,106.67186260602));
        allPoints.add(new LatLng(10.980276201601,106.67186843177));
        allPoints.add(new LatLng(10.980078424001,106.67187425752));
        allPoints.add(new LatLng(10.979880646401,106.67188008327));
        allPoints.add(new LatLng(10.979682868802,106.67188590902));
        allPoints.add(new LatLng(10.979485091202,106.67189173478));
        allPoints.add(new LatLng(10.979287313602,106.67189756053));
        allPoints.add(new LatLng(10.979089536002,106.67190338628));
        allPoints.add(new LatLng(10.978891758402,106.67190921203));
        allPoints.add(new LatLng(10.978693980802,106.67191503778));
        allPoints.add(new LatLng(10.978496203202,106.67192086353));
        allPoints.add(new LatLng(10.978298425602,106.67192668929));
        allPoints.add(new LatLng(10.978100648003,106.67193251504));
        allPoints.add(new LatLng(10.977902870403,106.67193834079));
        allPoints.add(new LatLng(10.977705092803,106.67194416654));
        allPoints.add(new LatLng(10.977507315203,106.67194999229));
        allPoints.add(new LatLng(10.977309537603,106.67195581805));
        allPoints.add(new LatLng(10.977111760003,106.6719616438));
        allPoints.add(new LatLng(10.976913982403,106.67196746955));
        allPoints.add(new LatLng(10.976716204804,106.6719732953));
        allPoints.add(new LatLng(10.976518427204,106.67197912105));
        allPoints.add(new LatLng(10.976320649604,106.67198494681));
        allPoints.add(new LatLng(10.9762815,106.6719861));
        emulatorPolyline = mMap.addPolyline(new PolylineOptions().addAll(allPoints).width(9).color(Color.parseColor("#1565C0")));
    }
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
}
