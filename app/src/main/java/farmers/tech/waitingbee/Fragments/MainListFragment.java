package farmers.tech.waitingbee.Fragments;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import 	android.support.v7.widget.SearchView;
import android.widget.Button;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import farmers.tech.waitingbee.Adapters.MainListAdapter;
import farmers.tech.waitingbee.Constants;
import farmers.tech.waitingbee.R;
import farmers.tech.waitingbee.SplashActivity;
import farmers.tech.waitingbee.Volley.VolleySingleton;
import farmers.tech.waitingbee.Widgets.NewtonCradleLoading;

/**
 * Created by GauthamVejandla on 8/2/16.
 */
public class MainListFragment extends Fragment implements View.OnClickListener {
    static MainListAdapter mainadapter;
    public static Location loc;
    double mLatitude=0;
    double mLongitude=0;
    static RecyclerView recyclerView;
    private static List<String> listTitle, listrating, listdistance, listvicinity, liststatus, listphotoref, listplaceid;
    private OnMainListFragmentListener mListener;
    private Button nearbybtn,ratingbtn,waittimebtn,updatedbtn,showallbtn;
    private NewtonCradleLoading newtonCradleLoading;
    private String search_property, next_page_token;
    private int radius;

    public static final String SETTINGSPREFERENCES = "settings_prefs" ;
    public static final String radius_key = "radius_key";
    SharedPreferences sharedpreferences;

    public static MainListFragment newInstance(String Property) {
        Bundle args = new Bundle();
        MainListFragment fragment = new MainListFragment();
        args.putString("search_prop", Property);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        search_property = getArguments().getString("search_prop");
        setHasOptionsMenu(true);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.action_save).setVisible(false);
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle(getString(R.string.app_name));
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
         View rootView = inflater.inflate(R.layout.mainlist_fragment, container, false);
        setHasOptionsMenu(true);
        loc = SplashActivity.gpslocation;
        mLatitude = loc.getLatitude();
        mLongitude = loc.getLongitude();
        sharedpreferences = getActivity().getSharedPreferences(SETTINGSPREFERENCES, Context.MODE_PRIVATE);
        radius = sharedpreferences.getInt(radius_key, 1);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.main_list);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        nearbybtn = (Button) rootView.findViewById(R.id.nearby_btn);
        nearbybtn.setOnClickListener(this);

        ratingbtn = (Button) rootView.findViewById(R.id.rating_btn);
        ratingbtn.setOnClickListener(this);

        waittimebtn = (Button) rootView.findViewById(R.id.waititme_btn);
        waittimebtn.setOnClickListener(this);

        updatedbtn = (Button) rootView.findViewById(R.id.updated_btn);
        updatedbtn.setOnClickListener(this);

        showallbtn = (Button) rootView.findViewById(R.id.showall_btn);
        showallbtn.setOnClickListener(this);

        newtonCradleLoading = (NewtonCradleLoading) rootView.findViewById(R.id.newton_cradle_loading_mainlist);

        placesvolley();
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mListener.disableCollapse();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnMainListFragmentListener) {
            mListener = (OnMainListFragmentListener) context;
        }else {
            throw new RuntimeException(context.toString()
                    + " must implement OnCheeseCategoriesFragmentListener");
        }
    }

    public interface OnMainListFragmentInteractionListener {
        void onPropertyClick(String title, String placeid, String rating, String photoref);
        void onShowFiltersClick();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Do something that differs the Activity's menu here
        super.onCreateOptionsMenu(menu, inflater);

        MenuItem menuItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menuItem);
        //SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                mainadapter.setFilter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mainadapter.setFilter(newText);
                return false;
            }
        });
    }


    public void placesvolley(){
        String url ="https://maps.googleapis.com/maps/api/place/nearbysearch/json?rankBy=distance&location=" + mLatitude + "," + mLongitude + "&radius="+(radius*1609.344)+"&types="+search_property+"&sensor=true&key=" + Constants.Google_key;
        newtonCradleLoading.start();
        JsonObjectRequest jsonRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            listTitle = new ArrayList<String>();
                            listrating = new ArrayList<String>();
                            listdistance = new ArrayList<String>();
                            listvicinity = new ArrayList<String>();
                            liststatus = new ArrayList<String>();
                            listphotoref = new ArrayList<String>();
                            listplaceid = new ArrayList<String>();

                            if (response.has("results")) {
                                JSONArray jsonArray = response.getJSONArray("results");
                                for (int i = 0; i < jsonArray.length(); i++) {

                                    if (jsonArray.getJSONObject(i).has("name")) {
                                        listTitle.add(i, (jsonArray.getJSONObject(i).optString("name")));
                                        listplaceid.add(i, (jsonArray.getJSONObject(i).optString("place_id")));
                                        listrating.add(i, (jsonArray.getJSONObject(i).optString("rating")));
                                        if (jsonArray.getJSONObject(i).has("photos")){
                                            listphotoref.add(i, (jsonArray.getJSONObject(i).getJSONArray("photos").getJSONObject(0).optString("photo_reference")));

                                        }else {
                                            listphotoref.add(i, "");
                                        }
                                        double endinglat = jsonArray.getJSONObject(i).getJSONObject("geometry").getJSONObject("location").optDouble("lat");
                                        double endinglng = jsonArray.getJSONObject(i).getJSONObject("geometry").getJSONObject("location").optDouble("lng");

                                        LatLng StartP = new LatLng(mLatitude, mLongitude);
                                        LatLng EndP = new LatLng(endinglat, endinglng);
                                        String radius = CalculationByDistance(StartP, EndP);
                                        listdistance.add(i, radius);

                                        if (jsonArray.getJSONObject(i).has("opening_hours")) {
                                            if (jsonArray.getJSONObject(i).getJSONObject("opening_hours").has("open_now")) {
                                                if (jsonArray.getJSONObject(i).getJSONObject("opening_hours").getString("open_now").equals("true")) {
                                                    liststatus.add(i, ("YES"));
                                                } else {
                                                    liststatus.add(i, ("NO"));
                                                }
                                            }
                                        } else {
                                            liststatus.add(i, ("Not Known"));
                                        }
                                        if (jsonArray.getJSONObject(i).has("vicinity")) {
                                            listvicinity.add(i, (jsonArray.getJSONObject(i).optString("vicinity")));
                                        }
                                    }
                                }
                            }

                            if (response.has("next_page_token")) {
                                next_page_token = response.optString("next_page_token");
                                nextPagePlaces(next_page_token);
                            }else{
                                mainadapter = new MainListAdapter(getActivity(), listTitle,listrating,listdistance, listvicinity, liststatus, listphotoref, listplaceid);
                                //Collections.sort(listdistance, StringAscComparator);
                                recyclerView.setAdapter(mainadapter);
                                newtonCradleLoading.stop();
                                newtonCradleLoading.setVisibility(View.GONE);
                            }
                            //nextPagePlaces(next_page_token);

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
    public String CalculationByDistance(LatLng StartP, LatLng EndP) {
        int Radius = 6371;// radius of earth in Km
        double lat1 = StartP.latitude;
        double lat2 = EndP.latitude;
        double lon1 = StartP.longitude;
        double lon2 = EndP.longitude;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2)
                * Math.sin(dLon / 2);
        double c = 2 * Math.asin(Math.sqrt(a));
        double km = Radius * c;
        double mi = km * 0.62137;
        DecimalFormat newFormat = new DecimalFormat("#.#");
        String miles = newFormat.format(mi);

        return miles;
    }

    public interface OnMainListFragmentListener {
        void disableCollapse();
        void onPropertyClick(String title, String placeid, String rating, String photoref);
        void onShowFiltersClick();
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.nearby_btn:
                initButtons();
                nearbybtn.setBackground(getResources().getDrawable(R.drawable.button_clicked));
                sort(1);
                break;

            case R.id.rating_btn:
                initButtons();
                ratingbtn.setBackground(getResources().getDrawable(R.drawable.button_clicked));
                sort(2);
                break;

            case R.id.waititme_btn:
                initButtons();
                waittimebtn.setBackground(getResources().getDrawable(R.drawable.button_clicked));
                sort(3);
                break;
            case R.id.updated_btn:
                initButtons();
                updatedbtn.setBackground(getResources().getDrawable(R.drawable.button_clicked));
                sort(4);
                break;
            case R.id.showall_btn:
                initButtons();
                mListener.onShowFiltersClick();
                //showallbtn.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                break;
            default:
                break;
        }

    }

    public void initButtons(){

        nearbybtn.setBackground(getResources().getDrawable(R.drawable.button_shape));
        ratingbtn.setBackground(getResources().getDrawable(R.drawable.button_shape));
        waittimebtn.setBackground(getResources().getDrawable(R.drawable.button_shape));
        updatedbtn.setBackground(getResources().getDrawable(R.drawable.button_shape));
    }


    public static void sort(int pos){
        if(pos == 1){
            Collections.sort(listdistance, StringAscComparator);
        }else if(pos == 2){
            Collections.sort(listrating, StringDescComparator);
        }if(pos == 3){
            //Collections.sort(listwaititmes, StringAscComparator);
        }if(pos == 4){
            //Collections.sort(listTitle, StringAscComparator);
        }
        recyclerView.setAdapter(mainadapter);
    }
    public static Comparator<String> StringAscComparator = new Comparator<String>() {

        public int compare(String app1, String app2) {

            String stringName1 = app1;
            String stringName2 = app2;

            return stringName1.compareToIgnoreCase(stringName2);
        }
    };

    //Comparator for Descending Order
    public static Comparator<String> StringDescComparator = new Comparator<String>() {

        public int compare(String app1, String app2) {

            String stringName1 = app1;
            String stringName2 = app2;

            return stringName2.compareToIgnoreCase(stringName1);
        }
    };

    public void nextPagePlaces(String nextPageToken){
        String url ="https://maps.googleapis.com/maps/api/place/nearbysearch/json?rankBy=distance&location=" + mLatitude + "," + mLongitude + "&radius="+(radius*1609.344)+"&types="+search_property+"&hasNextPage=true&nextPage()=true&sensor=true&key=" + Constants.Google_key + "&pagetoken=" + nextPageToken;
        JsonObjectRequest jsonRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            if (response.has("results")) {
                                JSONArray jsonArray = response.getJSONArray("results");
                                for (int i = 0; i < jsonArray.length(); i++) {

                                    if (jsonArray.getJSONObject(i).has("name")) {
                                        listTitle.add(i, (jsonArray.getJSONObject(i).optString("name")));
                                        listplaceid.add(i, (jsonArray.getJSONObject(i).optString("place_id")));
                                        listrating.add(i, (jsonArray.getJSONObject(i).optString("rating")));
                                        if (jsonArray.getJSONObject(i).has("photos")){
                                            listphotoref.add(i, (jsonArray.getJSONObject(i).getJSONArray("photos").getJSONObject(0).optString("photo_reference")));

                                        }else {
                                            listphotoref.add(i, "");
                                        }
                                        double endinglat = jsonArray.getJSONObject(i).getJSONObject("geometry").getJSONObject("location").optDouble("lat");
                                        double endinglng = jsonArray.getJSONObject(i).getJSONObject("geometry").getJSONObject("location").optDouble("lng");

                                        LatLng StartP = new LatLng(mLatitude, mLongitude);
                                        LatLng EndP = new LatLng(endinglat, endinglng);
                                        String radius = CalculationByDistance(StartP, EndP);
                                        listdistance.add(i, radius);

                                        if (jsonArray.getJSONObject(i).has("opening_hours")) {
                                            if (jsonArray.getJSONObject(i).getJSONObject("opening_hours").has("open_now")) {
                                                if (jsonArray.getJSONObject(i).getJSONObject("opening_hours").getString("open_now").equals("true")) {
                                                    liststatus.add(i, ("YES"));
                                                } else {
                                                    liststatus.add(i, ("NO"));
                                                }
                                            }
                                        } else {
                                            liststatus.add(i, ("Not Known"));
                                        }
                                        if (jsonArray.getJSONObject(i).has("vicinity")) {
                                            listvicinity.add(i, (jsonArray.getJSONObject(i).optString("vicinity")));
                                        }
                                    }
                                }
                            }
                            if (response.has("next_page_token")) {
                                next_page_token = response.optString("next_page_token");
                                nextPagePlaces(next_page_token);
                            }else{
                                mainadapter = new MainListAdapter(getActivity(), listTitle,listrating,listdistance, listvicinity, liststatus, listphotoref, listplaceid);
                                //Collections.sort(listdistance, StringAscComparator);
                                recyclerView.setAdapter(mainadapter);
                                newtonCradleLoading.stop();
                                newtonCradleLoading.setVisibility(View.GONE);
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

}
