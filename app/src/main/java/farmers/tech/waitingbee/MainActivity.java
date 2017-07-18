package farmers.tech.waitingbee;

import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.InflateException;
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
import 	android.support.v7.widget.SearchView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

import farmers.tech.waitingbee.Adapters.MainListAdapter;
import farmers.tech.waitingbee.CategoryMenu.SlideMenuItem;
import farmers.tech.waitingbee.CategoryMenu.ViewAnimator;
import farmers.tech.waitingbee.Fragments.AboutUsFragment;
import farmers.tech.waitingbee.Fragments.FeedbackFragment;
import farmers.tech.waitingbee.Fragments.MainListFragment;
import farmers.tech.waitingbee.Fragments.ProfileFragment;
import farmers.tech.waitingbee.Fragments.PropertyDetailFragment;
import farmers.tech.waitingbee.Fragments.SettingsFragment;
import farmers.tech.waitingbee.Fragments.SpreadWordFragment;
import farmers.tech.waitingbee.GPS.GPSTracker;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        PlaceSelectionListener,
        MainListFragment.OnMainListFragmentListener,
        PropertyDetailFragment.OnPropertyDetailFragmentListener,
        SettingsFragment.OnSettingsFragmentListener,
        SpreadWordFragment.OnSpreadWordFragmentListener,
        FeedbackFragment.OnFeedbackFragmentListener,
        ProfileFragment.OnProfileFragmentListener,
        AboutUsFragment.OnAboutUsFragmentListener,
        ViewAnimator.ViewAnimatorListener{

    private DrawerLayout mDrawerLayout;
    private ImageView imageView;
    private ImageButton currentlocation_button;
    private CollapsingToolbarLayout collapsingToolbar;
    private AppBarLayout appBar;
    private Toolbar toolbar;
    AlertDialog.Builder alert;
    AlertDialog dialog;
    PlaceAutocompleteFragment autocompleteFragment;
    private static final int REQUEST_SELECT_PLACE = 1000;
    private static final String LOG_TAG = "PlaceSelectionListener";
    private static String search_prop = "restaurant";
    private Place new_place;
    private LinearLayout categorymenu_linearLayout;
    private ActionBarDrawerToggle drawerToggle;
    private ViewAnimator viewAnimator;
    private List<SlideMenuItem> catergorymenu_list = new ArrayList<>();
    public static final String CLOSE = "Close";
    public static final String AIRPORT = "airport";
    public static final String RESTAURANT = "restaurant";
    public static final String SHOPPINGMALL = "shopping_mall";
    public static final String BAR = "bar";
    public static final String MOVIE = "movie_theater";
    SplashActivity spl = new SplashActivity();

    public CollapsingToolbarLayout getCollapsingToolbar() {
        return collapsingToolbar;
    }

    public Toolbar getToolbar() {
        return toolbar;
    }

    public ImageView getBackdropImage() {
        return imageView;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        appBar = (AppBarLayout) findViewById(R.id.appbar);
        imageView = (ImageView) findViewById(R.id.backdrop);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        final ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.mipmap.ic_launcher);
        ab.setDisplayHomeAsUpEnabled(true);


        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        if (navigationView != null) {
            setupDrawerContent(navigationView);
        }

        launchMainListFragment(search_prop);

        categorymenu_linearLayout = (LinearLayout) findViewById(R.id.category_drawer);
        categorymenu_linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawerLayout.closeDrawers();
            }
        });

        setActionBar();
        createMenuList();
        viewAnimator = new ViewAnimator<>(this, catergorymenu_list, mDrawerLayout, this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            syncFrags();
        }

    }

    private void syncFrags() {
        android.support.v4.app.Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.mainlist_fragment);
        if (fragment instanceof MainListFragment) {
            disableCollapse();
        } else if (fragment instanceof PropertyDetailFragment || fragment instanceof SettingsFragment || fragment instanceof SpreadWordFragment || fragment instanceof FeedbackFragment || fragment instanceof ProfileFragment || fragment instanceof AboutUsFragment) {
            enableCollapse();
            super.onBackPressed();
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                break;
            case R.id.action_categorymenu:
                mDrawerLayout.openDrawer(Gravity.RIGHT);
                break;
            case R.id.action_location:

                LayoutInflater inflater = getLayoutInflater();

                View alertLayout = null;
                try {
                    alertLayout = inflater.inflate(R.layout.location, null);

                    alert = new AlertDialog.Builder(this);
                    // this is set the view from XML inside AlertDialog
                    alert.setView(alertLayout);
                    // disallow cancel of AlertDialog on click of back button and outside touch
                    //alert.setCancelable(false);

                    AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
                            .setTypeFilter(AutocompleteFilter.TYPE_FILTER_GEOCODE)
                            .build();

                    autocompleteFragment = (PlaceAutocompleteFragment)
                            getFragmentManager().findFragmentById(R.id.place_fragment);
                    currentlocation_button = (ImageButton) alertLayout.findViewById(R.id.current_location);
                    currentlocation_button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            getCurrentLocation();
                        }
                    });
                    autocompleteFragment.setOnPlaceSelectedListener(this);
                    autocompleteFragment.setHint("Search a Location");
                    autocompleteFragment.setFilter(typeFilter);


                    alert.setNegativeButton("close", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

                    alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            setNewPlace(new_place);
                        }
                    });
                    dialog = alert.create();
                    dialog.show();
                } catch (InflateException e) {
                    dialog.show();
                }




                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        menuItem.setChecked(true);
                        int id = menuItem.getItemId();

                        if (id == R.id.nav_login) {
                            launchProfileFragment();
                        } else if (id == R.id.nav_settings) {
                            launchSettingsDetailFragment();
                        } else if (id == R.id.nav_spread) {
                            launchSpreadWordFragment();
                        } else if (id == R.id.nav_rateus) {
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.twitter.android"));
                            startActivity(intent);
                        } else if (id == R.id.nav_aboutus) {
                            launchAboutusFragment();
                        } else if (id == R.id.nav_feedback) {
                            launchFeedbackFragment();
                        }

                        mDrawerLayout.closeDrawers();
                        return true;
                    }
                });
    }


    @Override
    public void disableCollapse() {
        imageView.setVisibility(View.GONE);
        collapsingToolbar.setTitleEnabled(false);

    }

    @Override
    public void enableCollapse() {
        imageView.setVisibility(View.VISIBLE);
        collapsingToolbar.setTitleEnabled(true);
    }

    private void launchMainListFragment(String search_prop) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.mainlist_fragment, MainListFragment.newInstance(search_prop));
        ft.addToBackStack(null);
        ft.commit();
    }

    private void launchPropertyDetailFragment(String title, String placeid, String rating, String photoref) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.mainlist_fragment, PropertyDetailFragment.newInstance(title, placeid, rating, photoref));
        ft.addToBackStack(null);
        ft.commit();
    }

    private void launchSettingsDetailFragment() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.mainlist_fragment, SettingsFragment.newInstance());
        ft.addToBackStack(null);
        ft.commit();
    }

    private void launchSpreadWordFragment() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.mainlist_fragment, SpreadWordFragment.newInstance());
        ft.addToBackStack(null);
        ft.commit();
    }

    private void launchFeedbackFragment() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.mainlist_fragment, FeedbackFragment.newInstance());
        ft.addToBackStack(null);
        ft.commit();
    }

    private void launchProfileFragment() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.mainlist_fragment, ProfileFragment.newInstance());
        ft.addToBackStack(null);
        ft.commit();
    }

    private void launchAboutusFragment() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.mainlist_fragment, AboutUsFragment.newInstance());
        ft.addToBackStack(null);
        ft.commit();
    }

    @Override
    public void onPropertyClick(String title, String placeid, String rating, String photoref) {
        launchPropertyDetailFragment(title, placeid, rating, photoref);
    }

    @Override
    public void onShowFiltersClick() {
        launchSettingsDetailFragment();
    }

    @Override
    public void onPlaceSelected(Place place) {
        Log.i(LOG_TAG, "Place Selected: " + place.getName());
        autocompleteFragment.setHint(place.getAddress());
        new_place = place;

    }

    @Override
    public void onError(Status status) {
        Log.e(LOG_TAG, "onError: Status = " + status.toString());
        Toast.makeText(this, "Place selection failed: " + status.getStatusMessage(),
                Toast.LENGTH_SHORT).show();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SELECT_PLACE) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);
                this.onPlaceSelected(place);
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                this.onError(status);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public  void setNewPlace(Place newplace){
        Location newloc = new Location("");
        newloc.setLatitude(newplace.getLatLng().latitude);
        newloc.setLongitude(newplace.getLatLng().longitude);
        spl.setNewLocation(newloc);
        launchMainListFragment(search_prop);
    }

    private void createMenuList() {
        SlideMenuItem menuItem0 = new SlideMenuItem(CLOSE, R.mipmap.icn_close);
        catergorymenu_list.add(menuItem0);
        SlideMenuItem menuItem1 = new SlideMenuItem(RESTAURANT, R.drawable.ic_restaurant);
        catergorymenu_list.add(menuItem1);
        SlideMenuItem menuItem2 = new SlideMenuItem(AIRPORT, R.drawable.ic_airport);
        catergorymenu_list.add(menuItem2);
        SlideMenuItem menuItem5 = new SlideMenuItem(SHOPPINGMALL, R.mipmap.icn_shop);
        catergorymenu_list.add(menuItem5);
        SlideMenuItem menuItem6 = new SlideMenuItem(BAR, R.mipmap.icn_bar);
        catergorymenu_list.add(menuItem6);
        SlideMenuItem menuItem7 = new SlideMenuItem(MOVIE, R.mipmap.icn_movie);
        catergorymenu_list.add(menuItem7);
    }


    private void setActionBar() {
        drawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                toolbar,  /* nav drawer icon to replace 'Up' caret */
                R.string.category_drawer_open,  /* "open drawer" description */
                R.string.category_drawer_close  /* "close drawer" description */
        ) {

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                categorymenu_linearLayout.removeAllViews();
                categorymenu_linearLayout.invalidate();
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
                if (slideOffset > 0.6 && categorymenu_linearLayout.getChildCount() == 0)
                    viewAnimator.showMenuContent();
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };
        mDrawerLayout.setDrawerListener(drawerToggle);
    }

    @Override
    public void addViewToContainer(View view) {
        categorymenu_linearLayout.addView(view);
    }

    @Override
    public void clickedItem(String name, int pos) {
        search_prop = name;
        launchMainListFragment(search_prop);
    }

    public  void getCurrentLocation(){
        spl.startLocation();
    }
}
