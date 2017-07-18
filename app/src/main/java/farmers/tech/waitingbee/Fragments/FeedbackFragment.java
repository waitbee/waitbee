package farmers.tech.waitingbee.Fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import farmers.tech.waitingbee.MainActivity;
import farmers.tech.waitingbee.R;

/**
 * Created by GauthamVejandla on 8/10/16.
 */
public class FeedbackFragment extends Fragment implements View.OnClickListener{

    private OnFeedbackFragmentListener mListener;
    Button fab;

    public FeedbackFragment() {
    }

    public static FeedbackFragment newInstance() {
        Bundle args = new Bundle();
        FeedbackFragment fragment = new FeedbackFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu (true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_feedback, container, false);

        fab = (Button) view.findViewById(R.id.feedback_submit);
        fab.setOnClickListener(this);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mListener.disableCollapse();
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.action_location).setVisible(false);
        menu.findItem(R.id.action_search).setVisible(false);
        menu.findItem(R.id.action_categorymenu).setVisible(false);
        menu.findItem(R.id.action_save).setVisible(false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFeedbackFragmentListener) {
            mListener = (OnFeedbackFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnSettingsFragmentListener");
        }
    }



    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity) getActivity()).getToolbar().setTitle("Feedback");
    }

    public interface OnFeedbackFragmentListener {
        void disableCollapse();
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.feedback_submit:

                AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
                alertDialog.setTitle("Thank You");
                alertDialog.setMessage("We appreciate your feedback!");
                alertDialog.setIcon(R.drawable.ic_menu_manage);

                alertDialog.setNegativeButton("Close",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                                getActivity().getSupportFragmentManager().popBackStack();
                            }
                        });

                alertDialog.show();

                break;
            default:
                break;
        }

    }

}