package com.openetizen.cevysays.opennews.fragments;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Handler;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.openetizen.cevysays.opennews.R;
import com.openetizen.cevysays.opennews.activity.DetailPostActivity;
import com.openetizen.cevysays.opennews.adapters.CategoryOneAdapter;
import com.openetizen.cevysays.opennews.models.CategoryOneItem;
import com.twotoasters.jazzylistview.JazzyHelper;
import com.twotoasters.jazzylistview.JazzyListView;

import java.util.ArrayList;

import jp.co.recruit_lifestyle.android.widget.WaveSwipeRefreshLayout;

/**
 * A simple {@link Fragment} subclass.
 */
public class NewsFragment extends android.support.v4.app.Fragment implements AdapterView.OnItemClickListener {

    //    private ArrayList<CategoryOneItem> categoryOneItemArrayList;
    private JazzyListView listView;
    private ArrayList<CategoryOneItem> dataCatOne = new ArrayList<>();
    private View rootView;
    private Toolbar toolbar;
    private ArrayList<String> image = new ArrayList<String>();
    private ArrayList<String> title = new ArrayList<String>();
    private ArrayList<String> created_at = new ArrayList<String>();
    private ArrayList<String> username = new ArrayList<String>();
    private ArrayList<String> content = new ArrayList<String>();
    private ArrayList<String> category_cd = new ArrayList<String>();
    private ArrayList<String> article_id = new ArrayList<String>();
    private ArrayList<String> user_id = new ArrayList<String>();
    public static final String MyPREFERENCES = "MyPrefs";
    static SharedPreferences sharedpreferences;
    private WaveSwipeRefreshLayout mWaveSwipeRefreshLayout;


    public NewsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_category_one, container, false);
        toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar_actionbar);
        sharedpreferences = getActivity().getSharedPreferences(MyPREFERENCES,
                Context.MODE_PRIVATE);


        mWaveSwipeRefreshLayout = (WaveSwipeRefreshLayout) rootView.findViewById(R.id.main_swipe);
        mWaveSwipeRefreshLayout.setOnRefreshListener(new WaveSwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Do work to refresh the list here.
                dummyRefresh();
            }
        });


        getData();
        ///new DownloadData().execute();


        return rootView;
//        return inflater.inflate(R.layout.fragment_category_one, container, false);

    }

    private void dummyRefresh() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                mWaveSwipeRefreshLayout.setRefreshing(false);
            }
        }, 2000);

    }

    private void getData() {

        loadArray("image", image);
        loadArray("title", title);
        loadArray("created_at", created_at);
        loadArray("username", username);
        loadArray("category_cd", category_cd);
        loadArray("content", content);
        loadArray("article_id", article_id);
        loadArray("user_id", user_id);


        for (int i = 0; i < image.size(); i++) {
            if (category_cd.get(i).contains("CATE_TP_1")) {
                dataCatOne.add(new CategoryOneItem(image.get(i), title.get(i), created_at.get(i), username.get(i), content.get(i), category_cd.get(i), article_id.get(i), user_id.get(i)));
            }
        }

        listView = (JazzyListView) rootView.findViewById(R.id.list);
        listView.setTransitionEffect(JazzyHelper.GROW);
        listView.setOnItemClickListener(NewsFragment.this);
        listView.setAdapter(new CategoryOneAdapter(dataCatOne, getActivity()));


    }


    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Intent intent = new Intent(NewsFragment.this.getActivity(), DetailPostActivity.class);
        intent.putExtra("post", dataCatOne.get(i));
        startActivity(intent);

    }

    public static void loadArray(String key, ArrayList<String> sKey) {

        sKey.clear();
        int size = sharedpreferences.getInt(key + "_size", 0);

        for (int i = 0; i < size; i++) {
            sKey.add(sharedpreferences.getString(key + "_" + i, null));
        }
    }


}
