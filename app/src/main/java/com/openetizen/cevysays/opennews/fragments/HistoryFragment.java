package com.openetizen.cevysays.opennews.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.openetizen.cevysays.opennews.R;
import com.openetizen.cevysays.opennews.activity.DetailPostActivity;
import com.openetizen.cevysays.opennews.activity.PostingActivity;
import com.openetizen.cevysays.opennews.adapters.HistoryAdapter;
import com.openetizen.cevysays.opennews.models.CategoryOneItem;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

public class HistoryFragment extends Fragment {
    private ArrayList<CategoryOneItem> dataCatOne = new ArrayList<>();
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

    private HistoryAdapter mAdapter;
    private SwipeMenuListView mListView;
    private View rootView;

    private ProgressDialog prgDialog;

    public HistoryFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_history, container, false);
        sharedpreferences = getActivity().getSharedPreferences(MyPREFERENCES,
                Context.MODE_PRIVATE);

//        image = new ArrayList<String>();
//        title = new ArrayList<String>();
//        created_at = new ArrayList<String>();
//        username = new ArrayList<String>();
//        content = new ArrayList<String>();
//        category_cd = new ArrayList<String>();
//        article_id = new ArrayList<String>();
//        user_id = new ArrayList<String>();

//        loadArray("image", image);
//        loadArray("title", title);
//        loadArray("created_at", created_at);
//        loadArray("username", username);
//        loadArray("category_cd", category_cd);
//        loadArray("content", content);
//        loadArray("article_id", article_id);
//        loadArray("user_id", user_id);



        prgDialog = new ProgressDialog(getActivity());
        // Set Progress Dialog Text
        prgDialog.setMessage("Mohon menunggu...");
        // Set Cancelable as False
        prgDialog.setCancelable(false);


//        for (int i = 0; i < title.size(); i++) {
//            if (user_id.get(i).equals(String.valueOf(sharedpreferences.getInt("loginUserID", 0)))) {
//                dataCatOne.add(new CategoryOneItem(image.get(i), title.get(i), created_at.get(i), username.get(i), content.get(i), category_cd.get(i), article_id.get(i), user_id.get(i)));
//            }
//        }

        mAdapter = new HistoryAdapter(dataCatOne, getActivity());

        mListView = (SwipeMenuListView) rootView.findViewById(R.id.listView);
        mListView.setAdapter(mAdapter);

        // step 1. create a MenuCreator
        SwipeMenuCreator creator = new SwipeMenuCreator() {

            @Override
            public void create(SwipeMenu menu) {
                // create "open" item
                SwipeMenuItem openItem = new SwipeMenuItem(
                        getActivity().getApplicationContext());
                // set item background
                openItem.setBackground(new ColorDrawable(Color.rgb(0xC9, 0xC9,
                        0xCE)));
                // set item width
                openItem.setWidth(dp2px(90));
                // set item title
                openItem.setTitle("Edit");
                // set item title fontsize
                openItem.setTitleSize(18);
                // set item title font color
                openItem.setTitleColor(Color.WHITE);
                // add to menu
                menu.addMenuItem(openItem);

                // create "delete" item
                SwipeMenuItem deleteItem = new SwipeMenuItem(
                        getActivity().getApplicationContext());
                // set item background
                deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9,
                        0x3F, 0x25)));
                // set item width
                deleteItem.setWidth(dp2px(90));
                // set a icon
                deleteItem.setIcon(R.drawable.ic_delete);
                // add to menu
                menu.addMenuItem(deleteItem);
            }
        };
// set creator
        mListView.setMenuCreator(creator);
        // step 2. listener item click event
        mListView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(final int position, SwipeMenu menu, int index) {
                final CategoryOneItem item = dataCatOne.get(position);
                switch (index) {
                    case 0:
                        // open
                        open(item);
                        break;
                    case 1:
                        // delete
                        AlertDialog.Builder builder =
                                new AlertDialog.Builder(getActivity(), R.style.AppCompatAlertDialogStyle);
                        builder.setTitle(R.string.title_dialog_delete);
                        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                delete(Integer.parseInt(item.getArticle_id()),position);
                                image.remove(position);
                                title.remove(position);
                                created_at.remove(position);
                                username.remove(position);
                                category_cd.remove(position);
                                content.remove(position);
                                article_id.remove(position);
                                user_id.remove(position);
                                saveArray("image", image);
                                saveArray("title", title);
                                saveArray("created_at", created_at);
                                saveArray("username", username);
                                saveArray("category_cd", category_cd);
                                saveArray("content", content);
                                saveArray("article_id", article_id);
                                saveArray("user_id", user_id);
                            }
                        });
                        builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        });
                        builder.show();
                        break;
                }
                return false;
            }
        });

        // set SwipeListener
        mListView.setOnSwipeListener(new SwipeMenuListView.OnSwipeListener() {

            @Override
            public void onSwipeStart(int position) {
                // swipe start
            }

            @Override
            public void onSwipeEnd(int position) {
                // swipe end
            }
        });

        // set MenuStateChangeListener
        mListView.setOnMenuStateChangeListener(new SwipeMenuListView.OnMenuStateChangeListener() {
            @Override
            public void onMenuOpen(int position) {
            }

            @Override
            public void onMenuClose(int position) {
            }
        });

        // other setting
//		listView.setCloseInterpolator(new BounceInterpolator());

        // test item long click
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(HistoryFragment.this.getActivity(), DetailPostActivity.class);
                intent.putExtra("post", dataCatOne.get(i));
                startActivity(intent);
            }
        });

        getData();
        return rootView;

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
            if (user_id.get(i).equals(String.valueOf(sharedpreferences.getInt("loginUserID", 0)))) {
                dataCatOne.add(new CategoryOneItem(image.get(i), title.get(i), created_at.get(i), username.get(i), content.get(i), category_cd.get(i), article_id.get(i), user_id.get(i)));
            }
        }


    }


    private void delete(int article_ID, final int position) {

        prgDialog.show();

        JSONObject jsonPosting = new JSONObject();
        try {
            jsonPosting.put("article_id",article_ID);
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
        client.delete(getActivity(), "http://openetizen.com/api/v1/articles/" + article_ID, entity, "application/json", new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject jsonObject) {
                // Hide Progress Dialog
                prgDialog.hide();

                try {
                    JSONObject obj = new JSONObject(new String(String.valueOf(jsonObject)));
                    Log.d("Opo", String.valueOf(jsonObject));
                    Toast.makeText(getActivity().getApplicationContext(), "Artikel berhasil dihapus!", Toast.LENGTH_LONG).show();


                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    Toast.makeText(getActivity().getApplicationContext(), "Error Occured [Server's JSON response might be invalid]!", Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                    Log.e("ERROR", "Response");


                }

                dataCatOne.remove(position);

                mAdapter = new HistoryAdapter(dataCatOne, getActivity());

                mListView.setAdapter(mAdapter);

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                // Hide Progress Dialog

                Log.e("errorResponse", errorResponse.toString() + "  " + statusCode);

                prgDialog.hide();
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
                    Toast.makeText(getActivity().getApplicationContext(), "Gagal menghapus artikel!", Toast.LENGTH_LONG).show();
                    // Toast.makeText(getApplicationContext(), "Unexpected Error occcured! [Most common Error: Device might not be connected to Internet or remote server is not up and running]", Toast.LENGTH_LONG).show();
                }
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }

        });
    }

    private void open(CategoryOneItem item) {
        // open app
        Intent i = new Intent(getActivity(), PostingActivity.class);
        i.putExtra("title",item.getTitle());
        i.putExtra("kategori",item.getCategory_cd());
        i.putExtra("konten",item.getContent());
        i.putExtra("article_id",item.getArticle_id());
        startActivity(i);

    }


    private class BaseSwipListAdapter {
        public void notifyDataSetChanged() {
        }
    }

    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                getResources().getDisplayMetrics());
    }

    public static void loadArray(String key, ArrayList<String> sKey) {

        sKey.clear();
        int size = sharedpreferences.getInt(key + "_size", 0);

        for (int i = 0; i < size; i++) {
            sKey.add(sharedpreferences.getString(key + "_" + i, null));
        }
    }

    public static boolean saveArray(String key, ArrayList<String> sKey) {
        SharedPreferences.Editor mEdit1 = sharedpreferences.edit();
        mEdit1.putInt(key + "_size", sKey.size()); /* sKey is an array */

        for (int i = 0; i < sKey.size(); i++) {
            mEdit1.remove(key + "_" + i);
            mEdit1.putString(key + "_" + i, sKey.get(i));
        }

        return mEdit1.commit();
    }

}
