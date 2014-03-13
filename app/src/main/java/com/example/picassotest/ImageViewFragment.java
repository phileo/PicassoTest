package com.example.picassotest;

import android.app.Fragment;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.File;

/**
 * Created by ac-1 on 3/12/2014.
 */
public class ImageViewFragment extends Fragment {
    private static final String TAG = ImageViewFragment.class.getSimpleName();
    public static final String IMG_URL_KEY = TAG+ "IMAGE_URL";
    private static final int MAX_NUM_RETRIES = 3;
    private static final String TYPE_KEY = TAG + ".TYPE_KEY";
    private String imageUrl;
    private ImageView mImageView;
    private Uri mUri;
    private Picasso mPicasso;

    private ProgressBar progressSpinner;
    private int retryCount = 0;
    private Picasso imageLoader;


    public ImageViewFragment() {
    }

    /**
     * factory method to construct a fragment with the passed in parameters
     *
     * @param url String containing path of image to show
     * @return
     */
    public static ImageViewFragment newInstance(String url) {
        ImageViewFragment f = new ImageViewFragment();
        // Get arguments passed in, if any
        Bundle args = f.getArguments();
        if (args == null) {
            args = new Bundle();
        }

        args.putString(IMG_URL_KEY, url);
        f.setArguments(args);
        return f;
    }

    public static ImageViewFragment newInstance(String url, GalleryPageAdapter.ImageSource source) {
        ImageViewFragment f = new ImageViewFragment();
        // Get arguments passed in, if any
        Bundle args = f.getArguments();
        if (args == null) {
            args = new Bundle();
        }

        args.putString(TYPE_KEY, source.getType());
        args.putString(IMG_URL_KEY, url);
        f.setArguments(args);
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        imageUrl = getArguments().getString(IMG_URL_KEY);
        if (imageUrl == null) {
            Log.e(TAG, "empty parameter passed in!!");
            return null;
        } else {
            Log.d(TAG, "inflating image: " + imageUrl);
        }
        Context ctxt = container.getContext();
        View rootView = (View) inflater.inflate( R.layout.fragment_imageview, container, false);
        int padding = ctxt.getResources().getDimensionPixelSize(R.dimen.viewpager_image);

        progressSpinner = (ProgressBar) rootView.findViewById(R.id.progress);
        mImageView = (ImageView) rootView.findViewById(R.id.imageView1);
        mImageView.setPadding(padding, padding, padding, padding);
        // layout dimensions can be adjusted here if needed
        return rootView;

    }


    @Override
    public void onResume() {
        super.onResume();
        setBackgroundImage(getActivity());
    }

    @Override
    public void onPause() {
        super.onPause();

        // Always cancel the request here, this is safe to call even if the image has been loaded.
        // This ensures that the anonymous callback we have does not prevent the fragment hosting it from
        // being garbage collected. It also prevents our callback from getting invoked even after leaving
        // this fragment
        // cancel the original request
        imageLoader.cancelRequest(mImageView);
        // cancel the retry request
        if (mPicasso != null ) {
            mPicasso.cancelRequest(mImageView);
        }

    }





    private Picasso.Listener networkImageLoadStatus = new Picasso.Listener() {

        @Override
        public void onImageLoadFailed(Picasso picasso, Uri uri, Exception e) {
            Log.w(TAG, "networkImageLoad failed! " + uri + retryCount);
            if (e != null ) {
                e.printStackTrace();
            }

            if (retryCount > MAX_NUM_RETRIES) {

                stopProgressSpinner();
                Toast.makeText(getActivity(), "network error! ", Toast.LENGTH_LONG).show();
                return;
            } else {
                retryCount++;
            }
            // retry loading with picasso here
        }
    };

    private Callback.EmptyCallback imageLoadFinished = new Callback.EmptyCallback() {

        @Override
        public void onSuccess() {
            super.onSuccess();
            stopProgressSpinner();
        }

        @Override
        public void onError() {
            super.onError();
            Log.e(TAG, "image target load failed: " + imageUrl);
        }
    };

    private Picasso getImageLoader(Context context) {
        Picasso.Builder picasso = new Picasso.Builder(context);

        picasso.listener(networkImageLoadStatus);

        return picasso.build();
    }


    private void setBackgroundImage(Context ctxt) {
        String thisType = getArguments().getString(TYPE_KEY);

        // show the ProgressBar spinner while we load the image
        startProgressSpinner();
        imageLoader = getImageLoader(ctxt);

        if (thisType == GalleryPageAdapter.ImageSource.FILE.getType()) {
            imageLoader.load(new File(imageUrl))
                    // drawable to insert while waiting for image to download
                    .placeholder(R.drawable.spinner_black_16)
                            // optionally use Picasso's Callback interface to prevent GC
                    .into(mImageView, imageLoadFinished);
        } else if (thisType == GalleryPageAdapter.ImageSource.URL.getType()) {
            imageLoader.load(imageUrl)
                    // drawable to insert while waiting for image to download
                    .placeholder(R.drawable.spinner_black_16)
                            // optionally use Picasso's Callback interface to prevent GC
                    .into(mImageView, imageLoadFinished);
        }

        Log.d(TAG, "setBackgroundImage()-> " + Picasso.with(ctxt).getSnapshot().toString());
    }


    public void startProgressSpinner() {

        // has the rootView included progress_spinner.xml ?
        if (progressSpinner != null ) {
            progressSpinner.setIndeterminate(true);
            progressSpinner.setVisibility(ProgressBar.VISIBLE);
        }
    }

    public void stopProgressSpinner() {

        if (progressSpinner != null) {
            progressSpinner.setIndeterminate(false);
            progressSpinner.setVisibility(ProgressBar.GONE);
        }
    }
}
