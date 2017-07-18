package farmers.tech.waitingbee.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.SearchView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import farmers.tech.waitingbee.Constants;
import farmers.tech.waitingbee.Fragments.MainListFragment;
import farmers.tech.waitingbee.R;

/**
 * Created by GauthamVejandla on 8/2/16.
 */
public class MainListAdapter extends RecyclerView.Adapter<MainListAdapter.MyViewHolder> {

    //private MainListFragment.OnMainListFragmentInteractionListener mListener;
    private MainListFragment.OnMainListFragmentListener mListener;
    Context mContext;
    TextView main_title,main_placeid, main_closed, main_opennow,main_notknown,main_distance,main_vicinity,main_iconref;
    RatingBar main_rating;
    private List<String> title, rating,distance, vicinity, status, photoref, placeid,orginallist;
    ItemFilter mFilter = new ItemFilter();

    public MainListAdapter(Context context,List<String> title,List<String> rating,List<String>distance,List<String>vicinity, List<String>status, List<String>photoref, List<String>placeid) {
        mContext = context;
        this.title = title;
        this.photoref = photoref;
        this.rating = rating;
        this.distance = distance;
        this.vicinity = vicinity;
        this.status = status;
        this.placeid = placeid;
        this.orginallist = title;
        mListener = (MainListFragment.OnMainListFragmentListener) context;

    }
    public class MyViewHolder extends RecyclerView.ViewHolder {


        public final View mView;
        public String mBoundString;

        public MyViewHolder(View view) {
            super(view);
            mView = view;
            main_title = (TextView) view.findViewById(R.id.Main_list_title);
            main_placeid = (TextView) view.findViewById(R.id.PlaceID);
            main_closed = (TextView)view.findViewById(R.id.Main_list_Closed);
            main_notknown = (TextView)view.findViewById(R.id.Main_list_notknown);
            main_opennow = (TextView)view.findViewById(R.id.Main_list_opennow);
            main_distance = (TextView) view.findViewById(R.id.Main_list_Distance);
            main_vicinity = (TextView) view.findViewById(R.id.Main_list_Vicinity);
            main_iconref = (TextView) view.findViewById(R.id.hiddeniconref);
            main_rating = (RatingBar) view.findViewById(R.id.mainlist_rating);


        }
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_main_row, parent, false);

        return new MyViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        main_title.setText(title.get(position).toString());
        main_placeid.setText(placeid.get(position).toString());
        main_placeid.setVisibility(View.INVISIBLE);

        String current_status  = status.get(position).toString();
        if (current_status.equals("YES")){
            main_closed .setVisibility(View.INVISIBLE);
            main_notknown .setVisibility(View.INVISIBLE);
        } else if (current_status.equals("NO")){
            main_opennow .setVisibility(View.INVISIBLE);
            main_notknown .setVisibility(View.INVISIBLE);
        } else if (current_status.equals("Not Known")){
            main_opennow .setVisibility(View.INVISIBLE);
            main_closed .setVisibility(View.INVISIBLE);
        }
        main_distance.setText(distance.get(position).toString()+ Constants.distance_string);
        main_vicinity.setText(vicinity.get(position).toString());
        main_iconref.setText(photoref.get(position).toString());
        main_iconref.setVisibility(View.INVISIBLE);

        if(rating.get(position).toString() == ""){
            main_rating.setRating(Float.parseFloat("0.0"));
        }else{
            main_rating.setRating(Float.parseFloat(rating.get(position).toString()));
        }
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onPropertyClick(title.get(position), placeid.get(position).toString(), rating.get(position).toString(), photoref.get(position).toString());
            }
        });

    }


    @Override
    public int getItemCount() {
        return title.size();
    }

    @Override
    public int getItemViewType(int position) {

        return position;
    }

    public Filter getFilter() {
        return mFilter;
    }

    private class ItemFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            FilterResults results = new Filter.FilterResults();
            List<String> filtertitle = orginallist;

            //No constraint is sent to filter by so we're going to send back the original array
            if (constraint == null || constraint.length() == 0)
            {
                results.values = orginallist;
                results.count = orginallist.size();
            }
            else {
                String filterString = constraint.toString().toLowerCase();
                final List<String> list = filtertitle;

                int count = list.size();
                final ArrayList<String> nlist = new ArrayList<String>(count);

                String filterableString;

                for (int i = 0; i < count; i++) {
                    filterableString = list.get(i);
                    if (filterableString.toLowerCase().contains(filterString)) {
                        nlist.add(filterableString);
                    }
                }

                results.values = nlist;
                results.count = nlist.size();

            }
            return results;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            title = (ArrayList<String>) results.values;
            notifyDataSetChanged();
        }

    }


    public void flushFilter(){
        title = orginallist;
        notifyDataSetChanged();
    }

    public void setFilter(String queryText) {

        if (queryText == null || queryText.length() == 0)
        {
            flushFilter();
        }
        else {
            List<String> allObjects = title;
            List<String> newlist = new ArrayList<>();

            for (String item : allObjects) {
                if (item.toLowerCase().contains(queryText))
                    newlist.add(item);
            }
            clearData();
            title.addAll(newlist);
            notifyDataSetChanged();
        }
    }

    public void clearData() {
        title.clear(); //clear list
        notifyDataSetChanged(); //let your adapter know about the changes and reload view.
    }

}
