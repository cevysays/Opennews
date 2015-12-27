package com.openetizen.cevysays.opennews.fragments;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
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
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;
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
    public static final String MyPREFERENCES = "MyPrefs";
    static SharedPreferences sharedpreferences;

    private GridViewAdapter mGridAdapter;
    private ArrayList<GridItem> mGridData;
    private String FEED_URL = "http://openetizen.com/api/v1/albums";
    private int albumID;
    int album_ID = 0;
    // Progress Dialog Object
    ProgressDialog prgDialog;

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
        sharedpreferences = getActivity().getSharedPreferences(MyPREFERENCES,
                Context.MODE_PRIVATE);

        mGridView = (GridView) rootView.findViewById(R.id.gridView);
        mProgressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);
        mProgressBar.setVisibility(View.VISIBLE);

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
                i.putExtra("album_ID", mGridData.get(position).getAlbum_ID());
                i.putExtra("album_Name", mGridData.get(position).getTitle());
                i.putExtra("Fragment", "MyGallery");
                startActivity(i);
            }
        });

        mGridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int i, final long l) {

                AlertDialog.Builder builder =
                        new AlertDialog.Builder(getActivity(), R.style.AppCompatAlertDialogStyle);
                builder.setTitle(R.string.title_dialog_delete_album);
                builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        deleteAlbum(mGridData.get((int) l).getAlbum_ID(), (int) l);
                    }
                });
                builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                builder.show();

                return true;
            }
        });

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
                String title = post.optString("name");
                item = new GridItem();
                item.setTitle(title);

                JSONArray picture = response.optJSONArray("album");
                JSONObject images = picture.getJSONObject(i);
                if (images.getInt("user_id") == sharedpreferences.getInt("loginUserID", 0)) {
                    item.setAlbum_ID(images.getInt("album_id"));
                    if (images.get("cover") != JSONObject.NULL) {
                        String image = images.getJSONObject("cover").getJSONObject("photo").getJSONObject("full").getString("url");
                        Log.d("cover", images.getJSONObject("cover").getJSONObject("photo").getJSONObject("full").getString("url"));
                        item.setImage("http://openetizen.com" + image.toString());
                        Log.d("foto", image.toString());
                    }

                    mGridData.add(item);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void deleteAlbum(int album_ID, final int position) {

        mProgressBar.setVisibility(View.VISIBLE);

        JSONObject jsonPosting = new JSONObject();
        try {
            jsonPosting.put("album_id", album_ID);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        StringEntity entity = null;
        try {
            entity = new StringEntity(jsonPosting.toString());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        AsyncHttpClient client = new AsyncHttpClient();
        client.delete(getActivity(), "http://openetizen.com/api/v1/albums/" + album_ID, entity, "application/json", new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject jsonObject) {
                // Hide Progress Dialog
                mProgressBar.setVisibility(View.GONE);

                try {
                    JSONObject obj = new JSONObject(new String(String.valueOf(jsonObject)));
                    Log.d("Opo", String.valueOf(jsonObject));
                    mGridData.remove(position);
                    mGridAdapter = new GridViewAdapter(getActivity(), R.layout.grid_item_layout, mGridData);
                    mGridView.setAdapter(mGridAdapter);
                    //invokeWS(json);
                    Toast.makeText(getActivity().getApplicationContext(), "Album berhasil dihapus!", Toast.LENGTH_LONG).show();


                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    Toast.makeText(getActivity().getApplicationContext(), "Error Occured [Server's JSON response might be invalid]!", Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                    Log.e("ERROR", "Response");
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                // Hide Progress Dialog

                Log.e("errorResponse", errorResponse.toString() + "  " + statusCode);

                mProgressBar.setVisibility(View.GONE);
                // When Http response code is '404'
                if (statusCode == 404) {
                    Toast.makeText(getActivity().getApplicationContext(), "Requested resource not found", Toast.LENGTH_LONG).show();
                }
                // When Http response code is '500'
                else if (statusCode == 500) {
                    Toast.makeText(getActivity().getApplicationContext(), "Something went wrong at server end", Toast.LENGTH_LONG).show();
                }
                // When Http response code other than 404, 500
                else {
                    Toast.makeText(getActivity().getApplicationContext(), "Posting failed", Toast.LENGTH_LONG).show();
                    // Toast.makeText(getApplicationContext(), "Unexpected Error occcured! [Most common Error: Device might not be connected to Internet or remote server is not up and running]", Toast.LENGTH_LONG).show();
                }
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }

        });
    }


}