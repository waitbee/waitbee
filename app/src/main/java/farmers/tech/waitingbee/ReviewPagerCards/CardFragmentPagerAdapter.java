package farmers.tech.waitingbee.ReviewPagerCards;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v7.widget.CardView;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class CardFragmentPagerAdapter extends FragmentStatePagerAdapter implements CardAdapter {

    private List<CardFragment> mFragments;
    private float mBaseElevation;
    private List<String> reviewername, reviewertext,reviewerphoto;
    private List<Integer> reviewerrating, reviewertime;
    private CardView mCardView;
    private int numberofreviews;

    public CardFragmentPagerAdapter(FragmentManager fm, float baseElevation, int numberofreviews,List<String>reviewername, List<String>reviewertext, List<String>reviewerphoto, List<Integer>reviewerrating, List<Integer>reviewertime) {
        super(fm);
        mFragments = new ArrayList<>();
        mBaseElevation = baseElevation;
        this.reviewername = reviewername;
        this.reviewertext = reviewertext;
        this.reviewerphoto = reviewerphoto;
        this.reviewerrating = reviewerrating;
        this.reviewertime = reviewertime;
        this.numberofreviews = numberofreviews;

        for(int i = 0; i< numberofreviews; i++){
            addCardFragment(new CardFragment(), reviewername.get(i).toString(),reviewertext.get(i).toString(),reviewerphoto.get(i).toString(),reviewerrating.get(i),reviewertime.get(i));
        }
    }

    @Override
    public float getBaseElevation() {
        return mBaseElevation;
    }

    @Override
    public CardView getCardViewAt(int position) {
        return mFragments.get(position).getCardView();
    }

    @Override
    public int getCount() {
        return mFragments.size();
    }

    @Override
    public Fragment getItem(int position) {
        return mFragments.get(position);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Object fragment = super.instantiateItem(container, position);
        mFragments.set(position, (CardFragment) fragment);
        return fragment;
    }

    public void addCardFragment(CardFragment fragment, String revname, String revtext, String revphoto, int revrating, int revtime) {

        mFragments.add(fragment.newInstance(revname, revtext, revphoto, revrating, revtime));


    }


}
