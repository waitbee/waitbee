package farmers.tech.waitingbee.Fragments;

/**
 * Created by GauthamVejandla on 8/9/16.
 */
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;

import farmers.tech.waitingbee.CustomPreferences.DiscreteSeekBar;
import farmers.tech.waitingbee.MainActivity;
import farmers.tech.waitingbee.R;

/**
 * Created by gvejandl on 8/9/2016.
 */
public class SettingsFragment extends Fragment{
    DiscreteSeekBar discreteSeekBar;
    CheckBox ShowOpen,ShowReviews;
    RadioButton checkedfiltervalue;
    boolean openvalue,reviewvalue;
    TextView seekbarvalue;
    RadioGroup filters;
    int radius, filtervalue;

    public static final String SETTINGSPREFERENCES = "settings_prefs" ;
    public static final String showopen_key = "showopenKey";
    public static final String showreview_key = "showreview_key";
    public static final String radius_key = "radius_key";
    public static final String filter_key = "filter_key";
    SharedPreferences sharedpreferences;

    private OnSettingsFragmentListener mListener;

    public SettingsFragment() {
    }

    public static SettingsFragment newInstance() {
        Bundle args = new Bundle();
        SettingsFragment fragment = new SettingsFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu,MenuInflater inflater) {
        // Do something that differs the Activity's menu here
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.action_location).setVisible(false);
        menu.findItem(R.id.action_search).setVisible(false);
        menu.findItem(R.id.action_categorymenu).setVisible(false);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_settings, container, false);
        initComponents(view);
        loadvalues(view);

        discreteSeekBar.setNumericTransformer(new DiscreteSeekBar.NumericTransformer() {
            @Override
            public int transform(int value) {
                radius = value;
                seekbarvalue.setText("current value: "+radius+" miles");
                return radius;
            }
        });

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mListener.disableCollapse();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnSettingsFragmentListener) {
            mListener = (OnSettingsFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnSettingsFragmentListener");
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                filtervalue = filters.getCheckedRadioButtonId();
                if(ShowOpen.isChecked()){openvalue = true;}
                if(ShowReviews.isChecked()){reviewvalue = true;}
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putBoolean(showopen_key, openvalue);
                editor.putBoolean(showreview_key, reviewvalue);
                editor.putInt(radius_key, radius);
                editor.putInt(filter_key, filtervalue);
                editor.commit();
                getActivity().onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity) getActivity()).getToolbar().setTitle("Settings");
    }

    public interface OnSettingsFragmentListener {
        void disableCollapse();
    }

    public void initComponents(View view){
        discreteSeekBar = (DiscreteSeekBar) view.findViewById(R.id.seekbar_preference);
        seekbarvalue = (TextView) view.findViewById(R.id.seekbar_value);
        ShowOpen = (CheckBox) view.findViewById(R.id.show_open);
        ShowReviews = (CheckBox) view.findViewById(R.id.show_reviews);
        filters = (RadioGroup) view.findViewById(R.id.filter_radioGroup);
        sharedpreferences = getActivity().getSharedPreferences(SETTINGSPREFERENCES, Context.MODE_PRIVATE);
    }
    public void loadvalues(View view){
        radius = sharedpreferences.getInt(radius_key, 1);
        filtervalue = sharedpreferences.getInt(filter_key, 2131558544);
        openvalue = sharedpreferences.getBoolean(showopen_key, false);
        reviewvalue = sharedpreferences.getBoolean(showreview_key, false);

        ShowOpen.setChecked(openvalue);
        ShowReviews.setChecked(reviewvalue);

//        checkedfiltervalue = (RadioButton) view.findViewById(filtervalue);
//        checkedfiltervalue.setChecked(true);

        discreteSeekBar.setProgress(radius);
    }
}
