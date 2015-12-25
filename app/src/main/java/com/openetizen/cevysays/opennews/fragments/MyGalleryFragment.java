package com.openetizen.cevysays.opennews.fragments;


import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.openetizen.cevysays.opennews.R;
import com.openetizen.cevysays.opennews.activity.PhotosActivity;
import com.openetizen.cevysays.opennews.adapters.GridViewAdapter;
import com.openetizen.cevysays.opennews.models.GridItem;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import jp.co.recruit_lifestyle.android.widget.WaveSwipeRefreshLayout;

/**
 * A simple {@link Fragment} subclass.
 */
public class MyGalleryFragment extends android.support.v4.app.Fragment {
    private static final String TAG = MyGalleryFragment.class.getSimpleName();
    private View rootView;
    protected FragmentActivity mActivity;

    private GridView mGridView;
    private ProgressBar mProgressBar;

    private GridViewAdapter mGridAdapter;
    private ArrayList<GridItem> mGridData;
    private String FEED_URL = "http://openetizen.com/api/v1/albums";
    private int albumID;

    private Bundle bundle = new Bundle();


    private WaveSwipeRefreshLayout mWaveSwipeRefreshLayout;


    public MyGalleryFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = (FragmentActivity) activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_gallery, container, false);

        mGridView = (GridView) rootView.findViewById(R.id.gridView);
        mProgressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);

        getActivity().setTitle("My Gallery");
        //Initialize with empty data
        mGridData = new ArrayList<>();
        mGridAdapter = new GridViewAdapter(getActivity(), R.layout.grid_item_layout, mGridData);
        mGridView.setAdapter(mGridAdapter);

        //Grid view click event

        mWaveSwipeRefreshLayout = (WaveSwipeRefreshLayout) rootView.findViewById(R.id.main_swipe);
        mWaveSwipeRefreshLayout.setOnRefreshListener(new WaveSwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Do work to refresh the list here.
                mGridData = new ArrayList<>();
                new AsyncHttpTask().execute(FEED_URL);
            }
        });

        //Start download
        new AsyncHttpTask().execute(FEED_URL);
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                Log.e("album_ID", "" + mGridData.get(position).getAlbum_ID());
                /*bundle.putInt("album_ID",mGridData.get(position).getAlbum_ID());
                ((MainActivity) getActivity()).replaceFragments(new PhotosFragment(),bundle);*/
                Intent i = new Intent(getActivity(), PhotosActivity.class);
                i.putExtra("album_ID",mGridData.get(position).getAlbum_ID());
                i.putExtra("album_Name",mGridData.get(position).getTitle());
                startActivity(i);
            }
        });
        mProgressBar.setVisibility(View.VISIBLE);

        return rootView;
    }


    //Downloading data asynchronously
    public class AsyncHttpTask extends AsyncTask<String, Void, Integer> {

        @Override
        protected Integer doInBackground(String... params) {
            Integer result = 0;
            try {
                // Create Apache HttpClient
                HttpClient httpclient = new DefaultHttpClient();
                HttpResponse httpResponse = httpclient.execute(new HttpGet(params[0]));
                int statusCode = httpResponse.getStatusLine().getStatusCode();

                // 200 represents HTTP OK
                if (statusCode == 200) {
                    String response = streamToString(httpResponse.getEntity().getContent());
                    parseResult(response);
                    result = 1; // Successful
                } else {
                    result = 0; //"Failed
                }
            } catch (Exception e) {
                Log.d(TAG, e.getLocalizedMessage());
            }

            return result;
        }

        @Override
        protected void onPostExecute(Integer result) {
            // Download complete. Lets update UI

            if (result == 1) {
                mWaveSwipeRefreshLayout.setRefreshing(false);
                mGridAdapter.setGridData(mGridData);
            } else {
                Toast.makeText(getActivity(), "Failed to fetch data!", Toast.LENGTH_SHORT).show();
            }

            //Hide progressbar
            mProgressBar.setVisibility(View.GONE);
        }
    }


    String streamToString(InputStream stream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(stream));
        String line;
        String result = "";
        while ((line = bufferedReader.readLine()) != null) {
            result += line;
        }

        // Close stream
        if (null != stream) {
            stream.close();
        }
        return result;
    }

    /**
     * Parsing the feed results and get the list
     *
     * @param result
     */
    private void parseResult(String result) {
        try {
            JSONObject response = new JSONObject(result);
            JSONArray posts = response.optJSONArray("album");
            GridItem item;
            for (int i = 0; i < posts.length(); i++) {
                JSONObject post = posts.optJSONObject(i);
                String title = post.optString("description");
                item = new GridItem();
                item.setTitle(title);

                JSONArray picture = response.optJSONArray("album");
                JSONObject images = picture.getJSONObject(i);
                String image = images.getJSONObject("cover").getJSONObject("photo").getJSONObject("full").getString("url");
                item.setAlbum_ID(images.getJSONObject("cover").getInt("album_id"));
                Log.d("cover", images.getJSONObject("cover").getJSONObject("photo").getJSONObject("full").getString("url"));
                item.setImage("http://openetizen.com" + image.toString());
                Log.d("foto", image.toString());

                mGridData.add(item);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }




}