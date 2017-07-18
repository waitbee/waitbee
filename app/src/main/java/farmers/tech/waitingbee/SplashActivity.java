package farmers.tech.waitingbee;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telecom.TelecomManager;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.FacebookSdk;

import java.util.ArrayList;
import java.util.List;

import farmers.tech.waitingbee.GPS.GPSTracker;
import farmers.tech.waitingbee.Location.OnLocationUpdatedListener;
import farmers.tech.waitingbee.Location.OnReverseGeocodingListener;
import farmers.tech.waitingbee.Location.SmartLocation;
import farmers.tech.waitingbee.Location.location.providers.LocationGooglePlayServicesProvider;
import farmers.tech.waitingbee.Widgets.AnimatedSvgView;

/**
 * Created by GauthamVejandla on 8/2/16.
 */
public class SplashActivity extends Activity implements OnLocationUpdatedListener {
    private static int SPLASH_TIME_OUT = 500;
    public static Location gpslocation;
    private AnimatedSvgView svgView;
    ImageView title;
    private Animation textAnimation;
    private LocationGooglePlayServicesProvider provider;
    private static final int LOCATION_PERMISSION_ID = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_splash);
        title = (ImageView) findViewById(R.id.text_image_title);
        textAnimation = AnimationUtils.loadAnimation(this, R.anim.text);

        if (ContextCompat.checkSelfPermission(SplashActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(SplashActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_ID);
            return;
        }
        startLocation();

        svgView = (AnimatedSvgView) findViewById(R.id.animated_svg_view);

        svgView.postDelayed(new Runnable() {

            @Override public void run() {
                svgView.start();
            }
        }, 500);


        svgView.setOnStateChangeListener(new AnimatedSvgView.OnStateChangeListener() {

            @Override public void onStateChange(int state) {
                if (state == AnimatedSvgView.STATE_TRACE_STARTED) {


                } else if (state == AnimatedSvgView.STATE_FINISHED) {

                    title.startAnimation(textAnimation);
                    title.setVisibility(View.VISIBLE);
                    new Handler().postDelayed(new Runnable() {

                        @Override
                        public void run() {
                            Intent i = new Intent(SplashActivity.this, MainActivity.class);
                            startActivity(i);
                        }
                    }, SPLASH_TIME_OUT);

                }
            }
        });
    }

    public void showSettingsAlert(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

        // Setting Dialog Title
        alertDialog.setTitle("GPS is settings");

        // Setting Dialog Message
        alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");

        // On pressing Settings button
        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        });

        // on pressing cancel button
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == LOCATION_PERMISSION_ID && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startLocation();
        }
    }

    public void startLocation() {

        provider = new LocationGooglePlayServicesProvider();
        provider.setCheckLocationSettings(true);

        SmartLocation smartLocation = new SmartLocation.Builder(this).logging(true).build();

        smartLocation.location(provider).start(this);
    }

    private void showLocation(Location location) {
        if (location != null) {
            gpslocation = location;

//            // We are going to get the address for the current position
//            SmartLocation.with(this).geocoding().reverse(location, new OnReverseGeocodingListener() {
//                @Override
//                public void onAddressResolved(Location original, List<Address> results) {
//                    if (results.size() > 0) {
//                        Address result = results.get(0);
//                        StringBuilder builder = new StringBuilder(text);
//                        builder.append("\n[Reverse Geocoding] ");
//                        List<String> addressElements = new ArrayList<>();
//                        for (int i = 0; i <= result.getMaxAddressLineIndex(); i++) {
//                            addressElements.add(result.getAddressLine(i));
//                        }
//                        builder.append(TextUtils.join(", ", addressElements));
//                        locationText.setText(builder.toString());
//                    }
//                }
//            });
        } else {
            showSettingsAlert();
        }
    }

    @Override
    public void onLocationUpdated(Location location) {
        showLocation(location);
    }

    public void setNewLocation(Location newloc){
        gpslocation=newloc;
    }
}
