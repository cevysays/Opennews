package com.openetizen.cevysays.opennews.fragments;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.openetizen.cevysays.opennews.R;
import com.openetizen.cevysays.opennews.activity.PhotosActivity;
import com.openetizen.cevysays.opennews.adapters.CategoryOneAdapter;
import com.openetizen.cevysays.opennews.adapters.GridViewAdapter;
import com.openetizen.cevysays.opennews.models.CategoryOneItem;
import com.openetizen.cevysays.opennews.models.GridItem;
import com.openetizen.cevysays.opennews.util.Utils;
import com.twotoasters.jazzylistview.JazzyHelper;
import com.twotoasters.jazzylistview.JazzyListView;

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

import cz.msebera.android.httpclient.Header;
import jp.co.recruit_lifestyle.android.widget.WaveSwipeRefreshLayout;

/**
 * A simple {@link Fragment} subclass.
 */
public class GalleryFragment extends Fragment {
    private static final String TAG = GalleryFragment.class.getSimpleName();
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


    public GalleryFragment() {
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

        getActivity().setTitle("Gallery");
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
                getData(true);
            }
        });

        getData(false);

        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                Log.e("album_ID",""+mGridData.get(position).getAlbum_ID());
                /*bundle.putInt("album_ID",mGridData.get(position).getAlbum_ID());
                ((MainActivity) getActivity()).replaceFragments(new PhotosFragment(),bundle);*/
                Intent i = new Intent(getActivity(), PhotosActivity.class);
                i.putExtra("album_ID",mGridData.get(position).getAlbum_ID());
                i.putExtra("album_Name",mGridData.get(position).getTitle());
                i.putExtra("Fragment","Gallery");
                startActivity(i);

            }
        });
        mProgressBar.setVisibility(View.VISIBLE);

        return rootView;
    }


    private void getData(final boolean isRefresh) {

        AsyncHttpClient client = new AsyncHttpClient();
        client.get("http://openetizen.com/api/v1/albums", null, new JsonHttpResponseHandler() {

            ProgressDialog progress;


            @Override
            public void onFinish() {
                super.onFinish();
                if (!isRefresh) {
                    progress.dismiss();
                }
            }

            @Override
            public void onStart() {
                super.onStart();
                if (!isRefresh) {
                    progress = ProgressDialog.show(getActivity(), "",
                            "Memuat data...", true);
                }

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject jsonObject) {
                // Pull out the first event on the public timeline
                mWaveSwipeRefreshLayout.setRefreshing(false);
                Log.e("Coba", "KELUAR");
//                JSONArray openArray null;
//                JSONObject openObject = null;
                String test = "";
                try {
                    JSONObject response = jsonObject;
                    JSONArray posts = response.optJSONArray("album");
                    GridItem item;
                    for (int i = 0; i < posts.length(); i++) {
                        JSONObject post = posts.optJSONObject(i);
                        String title = post.optString("name");
                        item = new GridItem();
                        item.setTitle(title);

                        JSONArray picture = response.optJSONArray("album");
                        JSONObject images = picture.getJSONObject(i);
                        if(images.get("cover")!=JSONObject.NULL) {
                            String image = images.getJSONObject("cover").getJSONObject("photo").getJSONObject("full").getString("url");
                            item.setImage("http://openetizen.com" + image.toString());
                            Log.d("cover", images.getJSONObject("cover").getJSONObject("photo").getJSONObject("full").getString("url"));
                            Log.d("foto", image.toString());
                        }
                        item.setAlbum_ID(images.getInt("album_id"));



                        mGridData.add(item);
                    }
                } catch (JSONException e) {
                    Log.e("ERROR",e.getMessage());
                }

                mGridAdapter.setGridData(mGridData);



            }
        });
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
                String title = post.optString("name");
                item = new GridItem();
                item.setTitle(title);

                JSONArray picture = response.optJSONArray("album");
                JSONObject images = picture.getJSONObject(i);
                item.setAlbum_ID(images.getInt("album_id"));

                if(images.get("cover")!=null) {
                    String image = images.getJSONObject("cover").getJSONObject("photo").getJSONObject("full").getString("url");
                    Log.d("cover", images.getJSONObject("cover").getJSONObject("photo").getJSONObject("full").getString("url"));
                    item.setImage("http://openetizen.com" + image.toString());
                    Log.d("foto", image.toString());
                }

                mGridData.add(item);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }




}
