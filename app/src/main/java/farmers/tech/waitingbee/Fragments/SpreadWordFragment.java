package farmers.tech.waitingbee.Fragments;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import farmers.tech.waitingbee.MainActivity;
import farmers.tech.waitingbee.R;

/**
 * Created by GauthamVejandla on 8/10/16.
 */
public class SpreadWordFragment extends Fragment implements View.OnClickListener{

    private OnSpreadWordFragmentListener mListener;
    LinearLayout FBshare, Twittershare, FBlike, Twitterlike;

    public SpreadWordFragment() {
    }

    public static SpreadWordFragment newInstance() {
        Bundle args = new Bundle();
        SpreadWordFragment fragment = new SpreadWordFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        final View view = inflater.inflate(R.layout.fragment_spreadword, container, false);
        FBshare = (LinearLayout) view.findViewById(R.id.fb_share);
        Twittershare = (LinearLayout) view.findViewById(R.id.twitter_share);
        FBlike = (LinearLayout) view.findViewById(R.id.fb_like);
        Twitterlike = (LinearLayout) view.findViewById(R.id.twitter_follow);

        FBshare.setOnClickListener(this);
        Twittershare.setOnClickListener(this);
        FBlike.setOnClickListener(this);
        Twitterlike.setOnClickListener(this);

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
        if (context instanceof OnSpreadWordFragmentListener) {
            mListener = (OnSpreadWordFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnSettingsFragmentListener");
        }
    }




    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity) getActivity()).getToolbar().setTitle("Spread Word");
    }

    public interface OnSpreadWordFragmentListener {
        void disableCollapse();
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.fb_share:

                break;

            case R.id.twitter_share:
                String tweetUrl = "https://twitter.com/intent/tweet?text=Hey checkout this cool app &url="
                        + "https://www.google.com";
                Uri uri = Uri.parse(tweetUrl);
                startActivity(new Intent(Intent.ACTION_VIEW, uri));

                break;
            case R.id.fb_like:
                Intent fb = getOpenFacebookIntent(getActivity().getPackageManager());
                startActivity(fb);
                break;
            case R.id.twitter_follow:
                launchTwitter(getActivity().getPackageManager());
                break;
            default:
                break;
        }

    }

    public static Intent getOpenFacebookIntent(PackageManager pm) {

        try {
            pm.getPackageInfo("com.facebook.katana", 0); //Checks if FB is even installed.
            return new Intent(Intent.ACTION_VIEW,
                    Uri.parse("fb://facewebmodal/f?href=https://www.facebook.com/WaitingBee")); //Trys to make intent with FB's URI
        } catch (Exception e) {
            return new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://www.facebook.com/WaitingBee")); //catches and opens a url to the desired page
        }
    }

    public void launchTwitter(PackageManager pm){

        Intent intent = null;
        try {
            // get the Twitter app if possible
            pm.getPackageInfo("com.twitter.android", 0);
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("twitter://user?user_id=759892833247592448"));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        } catch (Exception e) {
            // no Twitter app, revert to browser
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/waitingbee"));
        }
        this.startActivity(intent);
    }
}
