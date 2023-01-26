package in.finances.bankingwithinito.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import in.finances.bankingwithinito.R;

public class MapPickerActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMyLocationChangeListener {

    private GoogleMap mMap;
    public static int AUTOCOMPLETE_ACTIVITY = 180;
    private LatLng selectedLoc, currLoc;
    private String selectedAddress, selectedName;
    private Marker marker;
    private EditText location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_picker);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG);
        try {
            Places.initialize(MapPickerActivity.this, getString(R.string.google_maps_key));
        } catch (Exception e) {
        }
        location = findViewById(R.id.location);
        location.setOnClickListener(v ->
                startActivityForResult(new Autocomplete.IntentBuilder(
                        AutocompleteActivityMode.OVERLAY, fields)
                        .setCountry("IN")
                        .build(MapPickerActivity.this), AUTOCOMPLETE_ACTIVITY));

        findViewById(R.id.currentLoc).setOnClickListener(v -> {
            if (currLoc == null) {
                Toast.makeText(this, "Current location not yet fetched", Toast.LENGTH_SHORT).show();
                return;
            }
            selectedLoc = currLoc;
            selectedAddress = currLoc.toString();
            selectedName = getPlaceName(currLoc);
            location.setText(selectedName);
            rePositionMarker(selectedLoc);
        });

        findViewById(R.id.doneBtn).setOnClickListener(v -> {
            if (selectedLoc == null) {
                new AlertDialog.Builder(MapPickerActivity.this)
                        .setTitle("No location selected")
                        .setMessage("Continue anyway?")
                        .setNegativeButton("No", null)
                        .setPositiveButton("Yes", (d, i) -> {
                            setResult(RESULT_CANCELED);
                            finish();
                        })
                        .create()
                        .show();
                return;
            }
            Intent data = new Intent();
            data.putExtra("address", selectedAddress);
            data.putExtra("location", selectedLoc);
            data.putExtra("name", selectedName);
            setResult(RESULT_OK, data);
            finish();
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            boolean success = googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this, R.raw.style_json));

            if (!success) {
                Log.e("Maps", "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e("Maps", "Can't find style. Error: ", e);
        }

        checkGPS();
        LatLng temp = new LatLng(0, 0);
        marker = mMap.addMarker(new MarkerOptions().position(temp)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                .title("Selection"));
        marker.setVisible(false);
        mMap.setOnPoiClickListener(poi -> {
            if (poi != null) {
                selectedLoc = poi.latLng;
                selectedAddress = poi.latLng.toString();
                selectedName = poi.name.trim();
                if (selectedName.endsWith("Temporarily closed")) {
                    selectedName = selectedName.substring(0, selectedName.indexOf("Temporarily closed")).trim();
                }
                location.setText(selectedName);
                rePositionMarker(selectedLoc);
            }
        });
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        rePositionMarker(selectedLoc);

        mMap.setOnMapLongClickListener(latLng -> {
            rePositionMarker(latLng);
            selectedLoc = latLng;
            selectedAddress = latLng.toString();
            selectedName = getPlaceName(latLng);
            location.setText(selectedName);
        });
    }

    private void rePositionMarker(LatLng laln) {
        if (marker != null && laln != null) {
            marker.setVisible(true);
            marker.setPosition(laln);
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(laln, 12));
        }
    }

    @Override
    public void onMyLocationChange(Location loc) {
        if (loc == null)
            return;
        LatLng temp = new LatLng(loc.getLatitude(), loc.getLongitude());
        if (currLoc == null)//to prevent multiple animations
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(temp, 12));
        currLoc = temp;
    }

    private void checkGPS() {
        LocationRequest lr = LocationRequest.create();
        lr.setInterval(5000);
        lr.setFastestInterval(1000);
        lr.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(lr);
        SettingsClient client = LocationServices.getSettingsClient(MapPickerActivity.this);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());
        task.addOnSuccessListener(MapPickerActivity.this, locationSettingsResponse -> {
            if (PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(MapPickerActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                if (mMap != null) {
                    mMap.setOnMyLocationChangeListener(this);
                    mMap.setMyLocationEnabled(true);
                }
            } else {
                ActivityCompat.requestPermissions(MapPickerActivity.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        608);
            }
        });
        task.addOnFailureListener(MapPickerActivity.this, e -> {
            if (e instanceof ResolvableApiException) {
                try {
                    ResolvableApiException resolvable = (ResolvableApiException) e;
                    resolvable.startResolutionForResult(MapPickerActivity.this,
                            607);
                } catch (IntentSender.SendIntentException sendEx) {
                }
            }
        });
    }

    private String getPlaceName(LatLng loc) {
        Geocoder geocoder = new Geocoder(MapPickerActivity.this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(loc.latitude, loc.longitude, 1);
            Address a = addresses.get(0);
            String addr = a.getAddressLine(0).trim();
            if (a.getCountryName() != null && addr.endsWith(a.getCountryName()))
                addr = addr.substring(0, addr.length() - a.getCountryName().length()).trim();
            if (addr.endsWith(","))
                addr = addr.substring(0, addr.length() - 1).trim();
            if (a.getPostalCode() != null && addr.endsWith(a.getPostalCode()))
                addr = addr.substring(0, addr.length() - a.getPostalCode().length()).trim();
            if (addr.endsWith(","))
                addr = addr.substring(0, addr.length() - 1).trim();
            if (a.getAdminArea() != null && addr.endsWith(a.getAdminArea()))
                addr = addr.substring(0, addr.length() - a.getAdminArea().length()).trim();
            if (addr.endsWith(","))
                addr = addr.substring(0, addr.length() - 1).trim();
            return addr;
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Couldn't obtain name for the selected place", Toast.LENGTH_SHORT).show();
            return loc.toString();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == AUTOCOMPLETE_ACTIVITY) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                selectedAddress = place.getAddress();
                selectedName = place.getName();
                location.setText(selectedName);
                selectedLoc = place.getLatLng();
                rePositionMarker(selectedLoc);

            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                Status status = Autocomplete.getStatusFromIntent(data);
            }
        } else if (requestCode == 607 && resultCode == RESULT_OK)
            checkGPS();
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 608) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                checkGPS();
        }
    }
}
