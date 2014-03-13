package com.example.picassotest;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.support.v13.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by ac-1 on 3/12/2014.
 */
public class GalleryPageAdapter extends FragmentStatePagerAdapter{
    private Context mCtxt;
    private ArrayList<String> mImageList;

    public enum ImageSource {
        URL("URL"),
        FILE("FILE"),
        RESOURCE("RESOURCE");

        private String type;
        ImageSource(String type) {
            this.type = type;
        }

        public String getType() {
            return type;
        }
    }
    /**
     * A {@link FragmentStatePagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     * @param fm
     *
     */
    public GalleryPageAdapter(Context ctxt, FragmentManager fm, ArrayList<String> urls) {
        super(fm);
        mCtxt = ctxt;
        mImageList = urls;

    }

    /* (non-Javadoc)
     * @see android.support.v4.app.FragmentPagerAdapter#getItem(int)
     */
    @Override
    public Fragment getItem(int position) {

        // getItem is called to instantiate the fragment for the given page.
        Fragment fragment =  ImageViewFragment.newInstance(mImageList.get(position) , ImageSource.URL);

        return fragment;
    }

    /*
     * (non-Javadoc)
     * @see android.support.v4.view.PagerAdapter#getCount()
     *
     */
    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return mImageList.size();
    }


    @Override
    public CharSequence getPageTitle(int position) {
        Locale l = Locale.getDefault();
        switch (position) {
            case 0:
                return mCtxt.getString(R.string.title_section1).toUpperCase(l);
            case 1:
                return mCtxt.getString(R.string.title_section2).toUpperCase(l);
            case 2:
                return mCtxt.getString(R.string.title_section3).toUpperCase(l);
            case 3:
                return mCtxt.getString(R.string.title_section4).toUpperCase(l);
        }
        return null;
    }
}
