package hackathon.petcare;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.util.Pair;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.AmazonClientException;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBScanExpression;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.PaginatedScanList;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.jaredrummler.materialspinner.MaterialSpinner;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.Manifest;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import hackathon.petcare.demo.nosql.CallsDO;
import hackathon.petcare.demo.nosql.DemoNoSQLCallsResult;
import hackathon.petcare.demo.nosql.DemoNoSQLOperationBase;
import hackathon.petcare.demo.nosql.DemoNoSQLResult;
import hackathon.petcare.mobile.AWSMobileClient;

/**
 * Created by pradhumanswami on 10/2/16.
 */
public class PetActivityScreenFour extends AppCompatActivity implements AdapterView.OnItemClickListener, GoogleMap.OnMarkerClickListener,
        GoogleMap.OnInfoWindowClickListener, OnMapReadyCallback,
        GoogleMap.OnInfoWindowLongClickListener,
        GoogleMap.OnInfoWindowCloseListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener, ActivityCompat.OnRequestPermissionsResultCallback {

    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

    private AutoCompleteTextView mPlaceDetailsText;
    ProgressDialog pDialog;
    SharedPreferences sharedpreferences;
    private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
    private static final String TYPE_AUTOCOMPLETE = "/autocomplete";
    private static final String OUT_JSON = "/json?";
    public static final String API_KEY = "AIzaSyDmcrJIEfkdCTP61PQVGmCF1ZMzyaeeIiM";
    //Arjun Code start for force nearby search query
    public static final String TYPE_NEARBY = "/nearbysearch";
    public static final String LOCATION = "location=";
    public static final String RADIUS = "&radius=5000";
    public static final String VARIABLES = "&keyword=apartment||apartment+complex";
    private String filterType = "";
    public static final String VARIABLES_VET = "&types=veterinary_care";
    public static final String VARIABLES_STORES = "&types=pet_store";
    public static final String VARIABLES_TRAINER = "&pet+trainer||dog+trainer";
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    public double latitude, longitude;
    public DynamoDBMapper mapper;
    private Button filter;
    private Location mLocation;

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (location == null) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        } else {
            mLocation = location;
            USERXY = location.getLatitude() + "," + location.getLongitude();
            new PushData().execute();
            callByFilterType();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {

        USERXY = location.getLatitude() + "," + location.getLongitude();
        callByFilterType();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(this, CONNECTION_FAILURE_RESOLUTION_REQUEST);
                /*
                 * Thrown if Google Play services canceled the original
                 * PendingIntent
                 */
            } catch (IntentSender.SendIntentException e) {
                // Log the error
                e.printStackTrace();
            }
        } else {
            /*
             * If no resolution is available, display a dialog to the
             * user with the error.
             */

        }
    }

    /**
     * Demonstrates customizing the info window and/or its contents.
     */
    class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

        // These are both viewgroups containing an ImageView with id "badge" and two TextViews with id
        // "title" and "snippet".
        private final View mWindow;

        private final View mContents;

        CustomInfoWindowAdapter() {
            mWindow = getLayoutInflater().inflate(R.layout.custom_info_window, null);
            mContents = getLayoutInflater().inflate(R.layout.custom_info_contents, null);
        }

        @Override
        public View getInfoWindow(Marker marker) {
            render(marker, mWindow);
            return mWindow;
        }

        @Override
        public View getInfoContents(Marker marker) {
            /*if (mOptions.getCheckedRadioButtonId() != R.id.custom_info_contents) {
                // This means that the default info contents will be used.
                return null;
            }*/
            render(marker, mContents);
            return mContents;
        }

        private void render(Marker marker, View view) {
            int badge;
            // Use the equals() method on a Marker to check for equals.  Do not use ==.

            //badge = R.drawable.custom_info_bubble;

            //((ImageView) view.findViewById(R.id.badge)).setVisibility(View.GONE);
            String title = marker.getTitle();
            TextView titleUi = ((TextView) view.findViewById(R.id.title));
            if (title != null) {
                // Spannable string allows us to edit the formatting of the text.
                SpannableString titleText = new SpannableString(title);
                titleText.setSpan(new ForegroundColorSpan(Color.RED), 0, titleText.length(), 0);
                titleUi.setText(titleText);
            } else {
                titleUi.setText("");
            }
            SharedPreferences sharedpreferences = getSharedPreferences("PetCare", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putString("last", title);
            editor.commit();
            String snippet = "Experience " + "Good   /  Affordable YES";
            TextView snippetUi = ((TextView) view.findViewById(R.id.snippet));
            if (snippet != null && snippet.length() > 12) {
                SpannableString snippetText = new SpannableString(snippet);
                snippetText.setSpan(new ForegroundColorSpan(Color.MAGENTA), 0, 10, 0);
                snippetText.setSpan(new ForegroundColorSpan(Color.BLUE), 12, snippet.length(), 0);
                snippetUi.setText("Experience " + "Good   /  Affordable YES");
            } else {
                snippetUi.setText("");
            }
        }
    }

    private GoogleMap mMap;
    private Dialog dialog;
    private String USERXY = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pet_main_screen_four);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            int hasLocationPermission = checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        ActivityCompat.requestPermissions(PetActivityScreenFour.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

        sharedpreferences = getSharedPreferences("PetCare", Context.MODE_PRIVATE);
        filterType = sharedpreferences.getString("filter", "");
        mPlaceDetailsText = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView);
        filter = (Button) findViewById(R.id.filter);
        filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog dialog = new Dialog(PetActivityScreenFour.this);
                dialog.setContentView(R.layout.custom_layout_filter);
                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                lp.copyFrom(dialog.getWindow().getAttributes());
                lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
                lp.gravity = Gravity.CENTER;

                dialog.getWindow().setAttributes(lp);
                MaterialSpinner spinner = (MaterialSpinner) dialog.findViewById(R.id.spinner);
                spinner.setItems("Medical Attention", "Housing Issues", "Behaviorial Issues", "Pet Stores");

                spinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {

                    @Override
                    public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
                        SharedPreferences.Editor editor = sharedpreferences.edit();
                        editor.putString("filter", item);
                        filterType = item;
                        editor.commit();
                    }
                });
                Button dialogButton = (Button) dialog.findViewById(R.id.button_ok);
                // if button is clicked, close the custom dialog
                dialogButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        callByFilterType();
                    }
                });

                dialog.show();
            }
        });
        mPlaceDetailsText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onPickButtonClick(view);
            }
        });

        String last = sharedpreferences.getString("last", "");
        if (!last.equalsIgnoreCase("")) {
            LayoutInflater inflater = (LayoutInflater) PetActivityScreenFour.this.getSystemService(LAYOUT_INFLATER_SERVICE);
            View layout = inflater.inflate(R.layout.custom_layout, null);
            dialog = new Dialog(this, android.R.style.Theme_Translucent_NoTitleBar);
            dialog.setContentView(layout);
            TextView titleText = (TextView) dialog.findViewById(R.id.title_ans);
            RatingBar ratingBar2 = (RatingBar) dialog.findViewById(R.id.rating_two);
            ratingBar2.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                @Override
                public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    editor.putString("last", "");
                    editor.commit();
                    performDBEntry();
                    dialog.dismiss();
                }
            });
            titleText.setText(last);
            TextView skip = (TextView) dialog.findViewById(R.id.skip);
            skip.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    editor.putString("last", "");
                    editor.commit();
                    performDBEntry();
                    dialog.dismiss();
                }
            });
            try {
                dialog.show();
            } catch (Exception e) {

            }
        }
        String name = sharedpreferences.getString("place_name", "");
        mPlaceDetailsText.setText(name);
       /* mPlaceDetailsText.setAdapter(new GooglePlacesAutocompleteAdapter(this, R.layout.list_item));
        mPlaceDetailsText.setOnItemClickListener(this);*/
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY)
                .setInterval(10 * 1000)        // 10 seconds, in milliseconds
                .setFastestInterval(1 * 1000);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private void performDBEntry() {
        //new PushData().execute();
    }
    public void onPickButtonClick(View v) {
        // Construct an intent for the place picker
        try {
            PlacePicker.IntentBuilder intentBuilder =
                    new PlacePicker.IntentBuilder();
            Intent intent = intentBuilder.build(this);
            // Start the intent by requesting a result,
            // identified by a request code.
            startActivityForResult(intent, 3);

        } catch (GooglePlayServicesRepairableException e) {
            // ...
        } catch (GooglePlayServicesNotAvailableException e) {
            // ...
        }
    }

    @Override
    protected void onActivityResult(int requestCode,
                                    int resultCode, Intent data) {

        if (requestCode == 3
                && resultCode == Activity.RESULT_OK) {

            // The user has selected a place. Extract the name and address.
            final Place place = PlacePicker.getPlace(data, this);

            final CharSequence name = place.getName();
            final CharSequence address = place.getAddress();
            final LatLng latlng = place.getLatLng();
            String attributions = PlacePicker.getAttributions(data);
            if (attributions == null) {
                attributions = "";
            }
            mPlaceDetailsText.setText(name);
            USERXY = latlng.latitude + "," + latlng.longitude;
            callByFilterType();
            /*mViewName.setText(name);
            mViewAddress.setText(address);
            mViewAttributions.setText(Html.fromHtml(attributions));*/

        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {

                    Toast.makeText(getApplicationContext(), "Permission Denied, You cannot access location data.", Toast.LENGTH_LONG).show();

                }
                break;

        }
    }

    private void callByFilterType() {
        String queryString = "";
        if (filterType.equals("Medical Attention")) {
            StringBuilder sb2 = new StringBuilder(PLACES_API_BASE + TYPE_NEARBY + OUT_JSON + LOCATION + USERXY + RADIUS + VARIABLES_VET);
            sb2.append("&key=" + API_KEY);
            queryString = sb2.toString();
        } else if (filterType.equals("Housing Issues"))

        {
            StringBuilder sb = new StringBuilder(PLACES_API_BASE + TYPE_NEARBY + OUT_JSON + LOCATION + USERXY + RADIUS + VARIABLES);
            sb.append("&key=" + API_KEY);
            queryString = sb.toString();
        } else if (filterType.equals("Behavioral Issues"))

        {
            StringBuilder sb3 = new StringBuilder(PLACES_API_BASE + TYPE_NEARBY + OUT_JSON + LOCATION + USERXY + RADIUS + VARIABLES_STORES);
            sb3.append("&key=" + API_KEY);
            queryString = sb3.toString();
        } else if (filterType.equals("Pet Stores"))

        {
            StringBuilder sb4 = new StringBuilder(PLACES_API_BASE + TYPE_NEARBY + OUT_JSON + LOCATION + USERXY + RADIUS + VARIABLES_TRAINER);
            sb4.append("&key=" + API_KEY);
            queryString = sb4.toString();
        } else

        {
            StringBuilder sb2 = new StringBuilder(PLACES_API_BASE + TYPE_NEARBY + OUT_JSON + LOCATION + USERXY + RADIUS + VARIABLES_VET);
            sb2.append("&key=" + API_KEY);
            queryString = sb2.toString();
        }
        AppController.getInstance(this).getUserLocation(queryString);
    }

    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;

        // Hide the zoom controls as the button panel will cover it.
        mMap.getUiSettings().setZoomControlsEnabled(false);

        // Add lots of markers to the map.
        // Setting an info window adapter allows us to change the both the contents and look of the
        // info window.
        mMap.setInfoWindowAdapter(new CustomInfoWindowAdapter());

        // Set listeners for marker events.  See the bottom of this class for their behavior.
        mMap.setOnMarkerClickListener(this);
        mMap.setOnInfoWindowClickListener(this);
        mMap.setOnInfoWindowCloseListener(this);
        mMap.setOnInfoWindowLongClickListener(this);

        // Override the default content description on the view, for accessibility mode.
        // Ideally this string would be localised.
        map.setContentDescription("Map with lots of markers.");

        // Pan to see all markers in view.
        // Cannot zoom to bounds until the map has a size.
        final View mapView = getSupportFragmentManager().findFragmentById(R.id.map).getView();

    }

    private boolean checkReady() {
        if (mMap == null) {
            return false;
        }
        return true;
    }

    /**
     * Called when the Clear button is clicked.
     */
    public void onClearMap(View view) {
        if (!checkReady()) {
            return;
        }
        mMap.clear();
    }

    /**
     * Called when the Reset button is clicked.
     */
    public void onResetMap(View view) {
        if (!checkReady()) {
            return;
        }
        // Clear the map because we don't want duplicates of the markers.
        mMap.clear();
    }

    @Override
    public boolean onMarkerClick(final Marker marker) {
        // Markers have a z-index that is settable and gettable.
        float zIndex = marker.getZIndex() + 1.0f;
        marker.setZIndex(zIndex);


        // Markers can store and retrieve a data object via the getTag/setTag methods.
        // Here we use it to retrieve the number of clicks stored for this marker.
        Integer clickCount = (Integer) marker.getTag();
        // Check if a click count was set.
        if (clickCount != null) {
            clickCount = clickCount + 1;
            // Markers can store and retrieve a data object via the getTag/setTag methods.
            // Here we use it to store the number of clicks for this marker.
            marker.setTag(clickCount);
        }
        // We return false to indicate that we have not consumed the event and that we wish
        // for the default behavior to occur (which is for the camera to move such that the
        // marker is centered and for the marker's info window to open, if it has one).
        return false;
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
    }

    @Override
    public void onInfoWindowClose(Marker marker) {
        //Toast.makeText(this, "Close Info Window", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onInfoWindowLongClick(Marker marker) {
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        String str = (String) adapterView.getItemAtPosition(i);
        mPlaceDetailsText.setText(str);
        hideKeypad();
    }

    private void hideKeypad() {
        View view = this.getCurrentFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Subscribe
    public void onEventMainThread(CobbocEvent event) {
        if (event.getType() == CobbocEvent.USER_OWN_LOCATION) {
            if (pDialog != null) {
                pDialog.dismiss();
            }
            JSONObject object = null;
            try {
                object = new JSONObject(String.valueOf(event.getValue().toString()));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            JSONArray results = null;
            try {
                results = object.getJSONArray("results");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            ArrayList<String> location_names = new ArrayList<String>();
            ArrayList<Pair<Double, Double>> loc = new ArrayList<Pair<Double, Double>>();
            for (int i = 0; i < results.length(); i++) {
                JSONObject c = null;
                try {
                    c = results.getJSONObject(i);
                    String name = c.getString("name");
                    location_names.add(name);
                    JSONObject g = c.getJSONObject("geometry");
                    JSONObject l = g.getJSONObject("location");
                    Pair<Double, Double> loc_pair = new Pair(l.getDouble("lat"), l.getDouble("lng"));
                    loc.add(loc_pair);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            plotData(location_names, loc);
        }

    }

    private void plotData(ArrayList<String> location, ArrayList<Pair<Double, Double>> loc) {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        mMap.clear();
        if (mMap != null) {
            for (int i = 0; i < location.size(); i++) {
                LatLng lngTemp = new LatLng(loc.get(i).first, loc.get(i).second);
                mMap.addMarker(new MarkerOptions().position(lngTemp).title(location.get(i)));
                builder.include(lngTemp);
            }
        }
        if (loc.size() > 0 && location.size() > 0) {
            LatLngBounds bounds = builder.build();
            int padding = 0; // offset from edges of the map in pixels
            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
            mMap.animateCamera(cu);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().unregister(this);

        /*if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }*/
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }
        if(dialog!=null) {
            if(dialog.isShowing()) {
                dialog.dismiss();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //mGoogleApiClient.connect();
        mGoogleApiClient.connect();
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    private ArrayList autocomplete(String input) {
        ArrayList resultList = null;
        HttpURLConnection conn = null;
        StringBuilder jsonResults = new StringBuilder();
        try {
            StringBuilder sb = new StringBuilder(PLACES_API_BASE + TYPE_AUTOCOMPLETE + OUT_JSON);
            sb.append("?key=" + API_KEY);
            sb.append("&input=" + URLEncoder.encode(input, "utf8"));

            URL url = new URL(sb.toString());
            conn = (HttpURLConnection) url.openConnection();
            InputStreamReader in = new InputStreamReader(conn.getInputStream());

            // Load the results into a StringBuilder
            int read;
            char[] buff = new char[1024];
            while ((read = in.read(buff)) != -1) {
                jsonResults.append(buff, 0, read);
            }
        } catch (MalformedURLException e) {
            return resultList;
        } catch (IOException e) {
            return resultList;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }

        try {
            // Create a JSON object hierarchy from the results
            JSONObject jsonObj = new JSONObject(jsonResults.toString());
            JSONArray predsJsonArray = jsonObj.getJSONArray("predictions");

            // Extract the Place descriptions from the results
            resultList = new ArrayList(predsJsonArray.length());
            for (int i = 0; i < predsJsonArray.length(); i++) {
                System.out.println(predsJsonArray.getJSONObject(i).getString("description"));
                System.out.println("============================================================");
                resultList.add(predsJsonArray.getJSONObject(i).getString("description"));
            }
        } catch (JSONException e) {
        }

        return resultList;
    }

    public class DemoScanWithoutFilterFScreen extends DemoNoSQLOperationBase {

        private PaginatedScanList<CallsDO> results;
        private Iterator<CallsDO> resultsIterator;

        DemoScanWithoutFilterFScreen(final Context context) {
            super(context.getString(R.string.nosql_operation_title_scan_without_filter),
                    context.getString(R.string.nosql_operation_example_scan_without_filter));
        }

        @Override
        public boolean executeOperation() {
            try {
                mapper = AWSMobileClient.defaultMobileClient().getDynamoDBMapper();
                latitude = mLocation.getLatitude();
                longitude = mLocation.getLongitude();
                CallsDO firstItem = new CallsDO();
                SharedPreferences sharedpreferences = getSharedPreferences("PetCare", Context.MODE_PRIVATE);
                int sizeDB = sharedpreferences.getInt("sizeOfDB",0);
                firstItem.setplaceID(sizeDB+1);
                firstItem.setplaceName(mPlaceDetailsText.getText().toString());
                firstItem.setlongitude(longitude);
                firstItem.setlatitude(latitude);
                firstItem.setOExpFactor((double) sharedpreferences.getFloat("oExp", 5));
                firstItem.setAffFactor((double) sharedpreferences.getFloat("aFactor", 5));
                firstItem.setAType((int) sharedpreferences.getInt("problemType", 1));
                firstItem.setbreedType(0);
                firstItem.setcatFactor((double) sharedpreferences.getFloat("catFactor", 0));
                firstItem.setdogFactor((double) sharedpreferences.getFloat("dogFactor", 0));
                    mapper.save(firstItem);
                } catch (final Exception ex) {
                    //lastException = ex;
                }
            return false;
        }

        @Override
        public List<DemoNoSQLResult> getNextResultGroup() {
            return getNextResultsGroupFromIterator(resultsIterator);
        }

        @Override
        public boolean isScan() {
            return true;
        }

        @Override
        public void resetResults() {
            resultsIterator = results.iterator();
        }
    }

    private static List<DemoNoSQLResult> getNextResultsGroupFromIterator(final Iterator<CallsDO> resultsIterator) {
        if (!resultsIterator.hasNext()) {
            return null;
        }
        List<DemoNoSQLResult> resultGroup = new LinkedList<>();
        int itemsRetrieved = 0;
        do {
            // Retrieve the item from the paginated results.
            final CallsDO item = resultsIterator.next();
            // Add the item to a group of results that will be displayed later.
            resultGroup.add(new DemoNoSQLCallsResult(item));
            itemsRetrieved++;
        } while ((itemsRetrieved < 40) && resultsIterator.hasNext());
        return resultGroup;
    }

    private class PushData extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            try {
                new DemoScanWithoutFilterFScreen(PetActivityScreenFour.this).executeOperation();
            } catch (final AmazonClientException ex) {

            } finally {
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {

        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }
    }

    class GooglePlacesAutocompleteAdapter extends ArrayAdapter implements Filterable {
        private ArrayList resultList;

        public GooglePlacesAutocompleteAdapter(Context context, int textViewResourceId) {
            super(context, textViewResourceId);
        }

        @Override
        public int getCount() {
            return resultList.size();
        }

        @Override
        public String getItem(int index) {
            return String.valueOf(resultList.get(index));
        }

        @Override
        public Filter getFilter() {
            Filter filter = new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    FilterResults filterResults = new FilterResults();
                    if (constraint != null) {
                        // Retrieve the autocomplete results.
                        resultList = autocomplete(constraint.toString());

                        // Assign the data to the FilterResults
                        filterResults.values = resultList;
                        filterResults.count = resultList.size();
                    }
                    return filterResults;
                }

                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    if (results != null && results.count > 0) {
                        notifyDataSetChanged();
                    } else {
                        notifyDataSetInvalidated();
                    }
                }
            };
            return filter;
        }
    }
}
