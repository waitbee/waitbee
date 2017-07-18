package farmers.tech.waitingbee.ReviewPagerCards;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;

import farmers.tech.waitingbee.Constants;
import farmers.tech.waitingbee.R;
import farmers.tech.waitingbee.Utils.TextViewExpandableAnimation;
import farmers.tech.waitingbee.Volley.VolleySingleton;


public class CardFragment extends Fragment {

    private CardView mCardView;
    String revname, revtext, revphoto;
    int revrating,revtime;
    TextView reviewer_title, reviewer_time;
    ImageButton review_photo;
    RatingBar review_rating;
    TextViewExpandableAnimation reviewer_text;

    public static CardFragment newInstance(String revname, String revtext, String revphoto, int revrating, int revtime) {
        CardFragment fragment = new CardFragment();
        Bundle bundleFeatures = new Bundle();
        bundleFeatures.putString("rev_name", revname);
        bundleFeatures.putString("rev_text", revtext);
        bundleFeatures.putString("rev_photo", revphoto);
        bundleFeatures.putInt("rev_rating", revrating);
        bundleFeatures.putInt("rev_time", revtime);
        fragment.setArguments(bundleFeatures);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            revname = getArguments().getString("rev_name");
            revtext = getArguments().getString("rev_text");
            revphoto = getArguments().getString("rev_photo");
            revrating = getArguments().getInt("rev_rating");
            revtime = getArguments().getInt("rev_time");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_review_adapter, container, false);
        reviewer_title = (TextView) view.findViewById(R.id.reviewer_title);
        review_photo = (ImageButton) view.findViewById(R.id.review_photo);
        reviewer_text = (TextViewExpandableAnimation) view.findViewById(R.id.reviewer_text);
        reviewer_time = (TextView) view.findViewById(R.id.reviewer_time);
        review_rating = (RatingBar) view.findViewById(R.id.review_rating);
        mCardView = (CardView) view.findViewById(R.id.cardView);
        reviewer_title.setText(revname);
        reviewer_text.setText(revtext);
        if(revrating == 0){
            review_rating.setRating(Float.parseFloat("0.0"));
        }else{
            review_rating.setRating(Float.parseFloat(revrating+""));
        }

        reviewer_time.setText(Epoch2DateString(revtime));
        setImage(revphoto);

        mCardView.setMaxCardElevation(mCardView.getCardElevation()
                * CardAdapter.MAX_ELEVATION_FACTOR);
        return view;
    }

    public CardView getCardView() {
        return mCardView;
    }

    public void setImage(String ref){
        String url = "https:" + ref;
        ImageRequest imgRequest = new ImageRequest(url,
                new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap response) {
                        review_photo.setImageBitmap(response);
                    }
                }, 0, 0, ImageView.ScaleType.FIT_XY, Bitmap.Config.ARGB_8888, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                review_photo.setBackgroundColor(Color.parseColor("#ff0000"));
                error.printStackTrace();
            }
        });

        VolleySingleton.getInstance().addToRequestQueue(imgRequest);
    }

    public static String Epoch2DateString(int epochSeconds) {
        long unixSeconds = Long.valueOf(epochSeconds);
        Date updatedate = new Date(unixSeconds * 1000);
        SimpleDateFormat format = new SimpleDateFormat("MMM/dd/yyyy HH:mm:ss z");
        return format.format(updatedate);
    }
}