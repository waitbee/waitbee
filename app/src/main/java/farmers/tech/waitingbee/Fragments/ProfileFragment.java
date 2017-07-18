package farmers.tech.waitingbee.Fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

import farmers.tech.waitingbee.MainActivity;
import farmers.tech.waitingbee.R;
import farmers.tech.waitingbee.Volley.VolleySingleton;

/**
 * Created by GauthamVejandla on 8/11/16.
 */
public class ProfileFragment extends Fragment {


    private OnProfileFragmentListener mListener;
    AlertDialog.Builder loginDialog;
    LoginButton facebooksingin;
    final CallbackManager callbackManager = CallbackManager.Factory.create();
    String profile_image, full_name, f_email, f_bday,f_gender;
    public static final String User_Profile_Prefs = "User_Profile_Prefs" ;
    ImageButton user_pic;
    TextView user_name;

    public ProfileFragment() {
    }

    public static ProfileFragment newInstance() {
        Bundle args = new Bundle();
        ProfileFragment fragment = new ProfileFragment();
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
        final View view = inflater.inflate(R.layout.fragment_profile, container, false);

        SharedPreferences prefs = getActivity().getSharedPreferences(
                User_Profile_Prefs, Context.MODE_PRIVATE);
        user_name = (TextView) view.findViewById(R.id.user_profile_name);
        user_pic = (ImageButton) view.findViewById(R.id.user_profile_photo);

        if(prefs.contains("usr_full_name")){
            full_name = prefs.getString("usr_full_name", "");
            profile_image = prefs.getString("usr_image_url", "");
            f_email = prefs.getString("usr_email", "");
            f_bday = prefs.getString("usr_bday", "");
            user_name.setText(full_name);
            setImage(profile_image);

        }else{
            prompt();
        }
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
        if (context instanceof OnProfileFragmentListener) {
            mListener = (OnProfileFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnSettingsFragmentListener");
        }
    }



    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity) getActivity()).getToolbar().setTitle("Profile");
    }

    public interface OnProfileFragmentListener {
        void disableCollapse();
    }

    public void prompt(){
        LayoutInflater li = LayoutInflater.from(getActivity());
        View promptsView = li.inflate(R.layout.signin_prompt, null);

        loginDialog = new AlertDialog.Builder(getActivity());
        loginDialog.setTitle("Login");
        loginDialog.setView(promptsView);

        loginDialog.setNegativeButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });


        final AlertDialog aDialog = loginDialog.create();

        facebooksingin = (LoginButton) promptsView.findViewById(R.id.facebook_login_button);
        facebooksingin.setReadPermissions(Arrays.asList(
                "public_profile", "email", "user_birthday"));

        aDialog.show();

        facebooksingin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                aDialog.dismiss();
                facebooksingin.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {

                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        // App code
                        GraphRequest request = GraphRequest.newMeRequest(
                                loginResult.getAccessToken(),
                                new GraphRequest.GraphJSONObjectCallback() {
                                    @Override
                                    public void onCompleted(JSONObject object, GraphResponse response) {

                                        // Application code
                                        try {
                                            f_email = object.getString("email");
                                            f_bday = object.getString("birthday"); // 01/31/1980 format
                                            f_gender = object.getString("gender");

                                            Profile profile = Profile.getCurrentProfile();
                                            if (profile != null) {
                                                full_name=profile.getName();
                                                profile_image=profile.getProfilePictureUri(400, 400).toString();
                                            }

                                            user_name.setText(full_name);
                                            setImage(profile_image);
                                            //loginprompt.setVisibility(View.INVISIBLE);
                                            SharedPreferences wrprefs = getActivity().getSharedPreferences(
                                                    User_Profile_Prefs, Context.MODE_PRIVATE);
                                            SharedPreferences.Editor editor = wrprefs.edit();
                                            editor.putString("usr_full_name", full_name);
                                            editor.putString("usr_image_url", profile_image);
                                            editor.putString("usr_email", f_email);
                                            editor.putString("usr_bday", f_bday);
                                            editor.commit();
                                        }catch (JSONException ex){
                                        }
                                    }
                                });
                        Bundle parameters = new Bundle();
                        parameters.putString("fields", "id,name,email,gender,birthday");
                        request.setParameters(parameters);
                        request.executeAsync();
                    }

                    @Override
                    public void onCancel() {
                        Toast.makeText(getActivity(), "Logged in failed!", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        Log.d("facebook", exception.toString());
                        Toast.makeText(getActivity(), "Logged in error!", Toast.LENGTH_SHORT).show();
                    }
                });
            }

        });
    }

    public void setImage(String url){
        ImageRequest imgRequest = new ImageRequest(url,
                new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap response) {
                        Bitmap bmp = getRoundedCornerBitmap(response,800);
                        user_pic.setImageBitmap(bmp);
                    }
                }, 0, 0, ImageView.ScaleType.FIT_XY, Bitmap.Config.ARGB_8888, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                user_pic.setBackgroundColor(Color.parseColor("#ff0000"));
                error.printStackTrace();
            }
        });

        VolleySingleton.getInstance().addToRequestQueue(imgRequest);
    }

    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, int pixels) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap
                .getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPx = pixels;

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }
}