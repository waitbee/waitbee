package farmers.tech.waitingbee.Fragments;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
import android.widget.Gallery;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import farmers.tech.waitingbee.AWS.Waittimes;
import farmers.tech.waitingbee.Constants;
import farmers.tech.waitingbee.MainActivity;
import farmers.tech.waitingbee.R;
import farmers.tech.waitingbee.ReviewPagerCards.CardFragmentPagerAdapter;
import farmers.tech.waitingbee.ReviewPagerCards.ShadowTransformer;
import farmers.tech.waitingbee.SplashActivity;
import farmers.tech.waitingbee.Volley.VolleySingleton;
import farmers.tech.waitingbee.Widgets.TextChronometer;

/**
 * Created by GauthamVejandla on 8/7/16.
 */
public class PropertyDetailFragment extends Fragment implements View.OnClickListener,OnMapReadyCallback {

    private static final String Property_NAME = "prop_name";
    private static final String Property_ID = "prop_id";
    private static final String Property_Rating = "prop_rating";
    private static final String Property_Photoref = "prop_photoref";
    private String propName,propid,proprating, photoref,propAdress,propPhNum,propWebsite;
    private OnPropertyDetailFragmentListener mListener;
    static String[] property_weekday_text = new String[7];
    TextView prop_waititme,prop_opentimes,prop_weektimes, prop_address, prop_phnum, prop_website;
    RatingBar prop_rating;
    private TextChronometer prop_updatedago;
    ImageView backdrop;
    static double proplat,proplng;
    //private SupportMapFragment googleMap;
    SimpleDateFormat sdf = new SimpleDateFormat("EEEE");
    Date d = new Date();
    String dayOfTheWeek = sdf.format(d);
    ImageButton showalltimes;
    boolean clicked = false;
    RotateAnimation ra;
    DynamoDBMapper mapper;
    int numberofreviews;
    private static List<String> listReviewrname, listreviewerphoto, listreviewetext;
    private static List<Integer>  listreviewtime, listreviewerrating;

    private ViewPager mViewPager;
    private ShadowTransformer mCardShadowTransformer;
    private CardFragmentPagerAdapter mFragmentCardAdapter;
    private ShadowTransformer mFragmentCardShadowTransformer;
    SupportMapFragment mapFragment;


    public PropertyDetailFragment() {
    }

    public static PropertyDetailFragment newInstance(String title, String placeid, String rating, String photoref) {
        Bundle args = new Bundle();
        args.putString(Property_NAME, title);
        args.putString(Property_ID, placeid);
        args.putString(Property_Rating, rating);
        args.putString(Property_Photoref, photoref);
        PropertyDetailFragment fragment = new PropertyDetailFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            propName = getArguments().getString(Property_NAME);
            propid = getArguments().getString(Property_ID);
            proprating = getArguments().getString(Property_Rating);
            photoref = getArguments().getString(Property_Photoref);
        }
        placedetailsvolley();
        setImage(photoref);
        setHasOptionsMenu(true);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.action_location).setVisible(false);
        menu.findItem(R.id.action_search).setVisible(false);
        menu.findItem(R.id.action_categorymenu).setVisible(false);
        menu.findItem(R.id.action_save).setVisible(false);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_property_detail, container, false);
        prop_waititme = (TextView) view.findViewById(R.id.prop_waititme);
        prop_updatedago = (TextChronometer) view.findViewById(R.id.prop_updatedago);
        prop_rating = (RatingBar) view.findViewById(R.id.prop_rating);
        prop_opentimes = (TextView) view.findViewById(R.id.prop_opentimes);
        prop_weektimes = (TextView) view.findViewById(R.id.prop_weektimes);
        prop_address = (TextView) view.findViewById(R.id.prop_address);
        prop_phnum = (TextView) view.findViewById(R.id.prop_phnum);
        prop_website = (TextView) view.findViewById(R.id.prop_website);
        mViewPager = (ViewPager) view.findViewById(R.id.viewPager);
        //googleMap = ((SupportMapFragment) getFragmentManager().findFragmentById(R.id.prop_map)).getMap();
         mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.prop_map);
        mapFragment.getMapAsync(this);
        prop_address.setOnClickListener(this);
        prop_weektimes.setVisibility(View.GONE);
        showalltimes = (ImageButton) view.findViewById(R.id.show_alltimes);

        ra =new RotateAnimation(0, 180);
        ra.setFillAfter(true);
        ra.setDuration(100);
        showalltimes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(clicked) {
                    prop_weektimes.setVisibility(View.GONE);
                    prop_opentimes.setVisibility(View.VISIBLE);
                    clicked = false;
                    showalltimes.setImageResource(R.mipmap.ic_keyboard_arrow_up_black);
                }else{
                    prop_weektimes.setVisibility(View.VISIBLE);
                    prop_opentimes.setVisibility(View.GONE);
                    clicked = true;
                    showalltimes.setImageResource(R.mipmap.ic_keyboard_arrow_up_black);
                }
            }
        });

        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(this);

        CognitoCachingCredentialsProvider credentialsProvider =
                new CognitoCachingCredentialsProvider(getActivity(), "us-east-1:YOUR_AWS_AUTH_STRING", Regions.US_EAST_1);

        AmazonDynamoDBClient ddbClient = new AmazonDynamoDBClient(credentialsProvider);
        mapper = new DynamoDBMapper(ddbClient);

        synchronizewaittime();

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mListener.enableCollapse();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnPropertyDetailFragmentListener) {
            mListener = (OnPropertyDetailFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnCheeseDetailFragmentListener");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity) getActivity()).getCollapsingToolbar().setTitle(propName);
        backdrop = ((MainActivity) getActivity()).getBackdropImage();
        ((MainActivity) getActivity()).getBackdropImage().setImageResource(R.mipmap.ic_launcher);

    }

    public interface OnPropertyDetailFragmentListener {
        void enableCollapse();
    }


    public void placedetailsvolley(){
        String url = "https://maps.googleapis.com/maps/api/place/details/json?placeid="+propid+"&key=" + Constants.Google_key;
        Log.d("",url);

        JsonObjectRequest jsonRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // the response is already constructed as a JSONObject!
                        try {
                            if (response.has("result")) {

                                JSONObject jsonArray = response.getJSONObject("result");



                                if (jsonArray.has("name")) {

                                    propAdress = (jsonArray.optString("formatted_address"));

                                    JSONArray typesArray = jsonArray.getJSONObject("opening_hours").getJSONArray("weekday_text");

                                    for (int j = 0; j < typesArray.length(); j++) {
                                        property_weekday_text[j]= (typesArray.getString(j));
                                    }

                                    propPhNum = (jsonArray.optString("formatted_phone_number"));
                                    propWebsite = (jsonArray.optString("website"));

                                    proplat = (jsonArray.getJSONObject("geometry").getJSONObject("location").optDouble("lat"));
                                    proplng = (jsonArray.getJSONObject("geometry").getJSONObject("location").optDouble("lng"));

                                    numberofreviews = jsonArray.getJSONArray("reviews").length();

                                    if (jsonArray.has("reviews")) {
                                        listreviewetext = new ArrayList<String>();
                                        listreviewerphoto = new ArrayList<String>();
                                        listReviewrname = new ArrayList<String>();
                                        listreviewerrating = new ArrayList<Integer>();
                                        listreviewtime = new ArrayList<Integer>();
                                        numberofreviews = jsonArray.getJSONArray("reviews").length();
                                        for (int z = 0; z < numberofreviews; z++) {
                                            listreviewetext.add(z, jsonArray.getJSONArray("reviews").getJSONObject(z).optString("text"));
                                            listreviewerphoto.add(z, jsonArray.getJSONArray("reviews").getJSONObject(z).optString("profile_photo_url"));
                                            listreviewerrating.add(z, jsonArray.getJSONArray("reviews").getJSONObject(z).optInt("rating"));
                                            listReviewrname.add(z, jsonArray.getJSONArray("reviews").getJSONObject(z).optString("author_name"));
                                            listreviewtime.add(z, jsonArray.getJSONArray("reviews").getJSONObject(z).optInt("time"));
                                        }
                                    }
                                }
                                setPropertyValues();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                });

        VolleySingleton.getInstance().addToRequestQueue(jsonRequest);
    }

    public void setPropertyValues(){

        mFragmentCardAdapter = new CardFragmentPagerAdapter(getFragmentManager(),
                dpToPixels(2, getActivity()),numberofreviews,listReviewrname,listreviewetext, listreviewerphoto, listreviewerrating, listreviewtime );

        mFragmentCardShadowTransformer = new ShadowTransformer(mViewPager, mFragmentCardAdapter);
        mFragmentCardShadowTransformer.enableScaling(true);

        mViewPager.setAdapter(mFragmentCardAdapter);
        mViewPager.setPageTransformer(false, mCardShadowTransformer);
        mViewPager.setOffscreenPageLimit(3);


        prop_address.setText(propAdress);
        prop_phnum.setText(propPhNum);
        String webs = "<a href='"+propWebsite+"'>"+propWebsite+"</a>";
        prop_website.setText(Html.fromHtml(webs));
        prop_website.setMovementMethod(LinkMovementMethod.getInstance());

        for (int j = 0; j < 7; j++) {
            if(property_weekday_text[j].contains(dayOfTheWeek)){
                prop_opentimes.setText(property_weekday_text[j]);
            }
        }
        prop_weektimes.setText(property_weekday_text[0] + "\n" + property_weekday_text[1] + "\n" + property_weekday_text[2] + "\n" +property_weekday_text[3] + "\n"+ property_weekday_text[4] + "\n"+property_weekday_text[5] + "\n"+property_weekday_text[6]);
        if (proprating.equals("")) {
        }else{
            prop_rating.setRating(Float.parseFloat(proprating));
        }
    }

    private void setUpMapIfNeeded(GoogleMap map) {



    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMap.addMarker(new MarkerOptions()
                .position(new LatLng(proplat, proplng))
                .title(propName));
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        MarkerOptions mp = new MarkerOptions();
        mp.position(new LatLng(proplat,proplng));
        googleMap.addMarker(mp);
        CameraUpdate center=
                CameraUpdateFactory.newLatLng(new LatLng(proplat,proplng));
        CameraUpdate zoom= CameraUpdateFactory.zoomTo(15);

        googleMap.moveCamera(center);
        googleMap.animateCamera(zoom);
    }

    public void setImage(String ref){
        String url = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference=" + ref + "&key=" + Constants.Google_key;
        ImageRequest imgRequest = new ImageRequest(url,
                new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap response) {
                        backdrop.setImageBitmap(response);
                    }
                }, 0, 0, ImageView.ScaleType.FIT_XY, Bitmap.Config.ARGB_8888, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                backdrop.setBackgroundColor(Color.parseColor("#ff0000"));
                error.printStackTrace();
            }
        });

        VolleySingleton.getInstance().addToRequestQueue(imgRequest);
    }

    public void onAddressClick(View v) {


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.prop_address:

                String uri = String.format(Locale.ENGLISH, "http://maps.google.com/maps?saddr=%f,%f&daddr=%f,%f", proplat, proplng, 0.0, 0.0);
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");

                try
                {
                    startActivity(intent);
                }
                catch(ActivityNotFoundException ex)
                {
                    try
                    {
                        Intent unrestrictedIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                        startActivity(unrestrictedIntent);
                    }
                    catch(ActivityNotFoundException innerEx)
                    {
                        Toast.makeText(getActivity(), "Please install a maps application", Toast.LENGTH_LONG).show();
                    }
                }

                break;
            case R.id.fab:

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                LayoutInflater inflater = getActivity().getLayoutInflater();
                View theView = inflater.inflate(R.layout.number_picker, null);

                final NumberPicker unit_hr = (NumberPicker) theView.findViewById(R.id.hr_picker);
                final NumberPicker unit_min = (NumberPicker) theView.findViewById(R.id.min_picker);
                unit_hr.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
                unit_min.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);

                builder.setView(theView)
                        .setPositiveButton("OK",new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if(unit_hr.getValue() == 0){
                                    long wt = TimeUnit.MINUTES.toMillis(unit_min.getValue() * 5);
                                    syncWaittime(wt);
                                }else{
                                    long hrs_milli = TimeUnit.HOURS.toMillis(unit_hr.getValue());
                                    long min_milli = TimeUnit.MINUTES.toMillis(unit_min.getValue() * 5);

                                    long wt = hrs_milli+min_milli;
                                    syncWaittime(wt);
                                }
                            }
                        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });



                unit_hr.setMinValue(0);
                unit_hr.setMaxValue(12);

                String mins[] = new String[20];
                for(int i = 0;i < 100; i+=5) {
                    if( i < 10 )
                        mins[i/5] = "0"+i;
                    else
                        mins[i/5] = ""+i;
                }
                unit_min.setDisplayedValues(mins);

                unit_min.setMinValue(0);
                unit_min.setMaxValue(11);
                unit_min.setValue(0);

                builder.show();

                break;
        }
    }

    public void syncWaittime(final long waittime_input) {

        final Runnable runnable = new Runnable() {
            public void run() {

                Waittimes wt_aws = new Waittimes();
                wt_aws.setPlaceid(propid);
                wt_aws.setWaititme(waittime_input);
                wt_aws.setUpdatetime(System.currentTimeMillis());
                wt_aws.setUpvote(0);
                mapper.save(wt_aws);
                synchronizewaittime();
            }
        };
        Thread mythread = new Thread(runnable);
        mythread.start();
    }

    private void synchronizewaittime() {
        final Runnable syncwt = new Runnable() {
            public void run() {
                final Waittimes synctime = mapper.load(Waittimes.class, propid);
                if(synctime != null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            long pwt = synctime.getWaititme();
                            long minute = (pwt / (1000 * 60)) % 60;
                            long hour = (pwt / (1000 * 60 * 60)) % 24;
                            if(hour == 0){
                                prop_waititme.setText(minute+" min");
                            }else{
                                prop_waititme.setText(hour+"hr : "+minute+" min");
                            }

                            long timeAWS = synctime.getUpdatetime();
                            prop_updatedago.setTime(timeAWS);
                        }
                    });
                }
            }
        };
        Thread mythread1 = new Thread(syncwt);
        mythread1.start();
    }

    public static float dpToPixels(int dp, Context context) {
        return dp * (context.getResources().getDisplayMetrics().density);
    }


}
