package com.openetizen.cevysays.opennews.activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.openetizen.cevysays.opennews.R;
import com.openetizen.cevysays.opennews.fragments.AgendaFragment;
import com.openetizen.cevysays.opennews.fragments.CategoryOneFragment;
import com.openetizen.cevysays.opennews.fragments.GalleryFragment;
import com.openetizen.cevysays.opennews.fragments.HistoryFragment;
import com.openetizen.cevysays.opennews.fragments.MyGalleryFragment;
import com.openetizen.cevysays.opennews.fragments.NavigationDrawerFragment;
import com.openetizen.cevysays.opennews.fragments.NavigationDrawerFragmentUser;
import com.openetizen.cevysays.opennews.fragments.NewsFragment;
import com.openetizen.cevysays.opennews.fragments.PromotionFragment;
import com.openetizen.cevysays.opennews.util.NavigationDrawerCallbacks;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import cz.msebera.android.httpclient.Header;


public class MainActivity extends ActionBarActivity
        implements NavigationDrawerCallbacks {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;
    private NavigationDrawerFragmentUser mNavigationDrawerFragmentUser;
    private Toolbar mToolbar;
//    public static final String MyPREFERENCES = "MyPrefs";
//    static SharedPreferences sharedpreferences;

    private String menuFragment = "";

    private FloatingActionButton menu_article;

    Bundle bundle = new Bundle();

    private SharedPreferences sharedPreferences;

    ProgressDialog prgDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        bundle = getIntent().getExtras();


        sharedPreferences = getSharedPreferences(LoginActivity.MyPREFERENCES, MODE_PRIVATE);

        if (sharedPreferences.getBoolean("login", false)) {
            setContentView(R.layout.activity_main_activity_user);

            prgDialog = new ProgressDialog(this);
            // Set Progress Dialog Text
            prgDialog.setMessage("Mohon menunggu...");
            // Set Cancelable as False
            prgDialog.setCancelable(false);


            menu_article = (FloatingActionButton) findViewById(R.id.menu);
            //menu_article.setIconAnimated(false);
            menu_article.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (menuFragment == "MyGallery") {
                        final Dialog dialog = new Dialog(MainActivity.this);
                        dialog.setContentView(R.layout.addgallery_dialog);
                        dialog.setTitle("Buat Album Baru");
                        dialog.show();
                        final EditText nameGallery = (EditText) dialog.findViewById(R.id.namaGaleri);
                        final EditText deskripsiGallery = (EditText) dialog.findViewById(R.id.deskripsiGaleri);
                        Button batal = (Button) dialog.findViewById(R.id.batalButton);
                        batal.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialog.dismiss();
                            }
                        });
                        Button buat = (Button) dialog.findViewById(R.id.buatButton);
                        buat.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                prgDialog.show();

                                RequestParams params = new RequestParams();
                                params.put("album[user_id]", sharedPreferences.getInt("loginUserID", 0));
                                params.put("album[name]", nameGallery.getText().toString());
                                params.put("album[description]", deskripsiGallery.getText().toString());

                                AsyncHttpClient client = new AsyncHttpClient();
                                client.post("http://openetizen.com/api/v1/albums", params, new AsyncHttpResponseHandler() {

                                            @Override
                                            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                                                prgDialog.hide();
                                                try {
                                                    JSONObject obj = new JSONObject(new String(responseBody));
                                                    if (obj.getString("status").equalsIgnoreCase("success")) {
                                                        Log.e("result", obj.toString());
                                                        Toast.makeText(getApplicationContext(), "Galeri berhasil dibuat!", Toast.LENGTH_LONG).show();
                                                        dialog.dismiss();
                                                    } else {
                                                        Toast.makeText(getApplicationContext(), obj.getString("error_msg"), Toast.LENGTH_LONG).show();
                                                    }

                                                } catch (JSONException e) {
                                                    // TODO Auto-generated catch block
                                                    Toast.makeText(getApplicationContext(), "Error Occured [Server's JSON response might be invalid]!", Toast.LENGTH_LONG).show();
                                                    e.printStackTrace();
                                                    Log.e("ERROR", "Response");


                                                }
                                            }

                                            @Override
                                            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                                                Log.e("errorResponse", /*responseBody.toString()*/  "  " + statusCode);

                                                prgDialog.hide();
                                                // When Http response code is '404'
                                                if (statusCode == 404) {
                                                    Toast.makeText(getApplicationContext(), "Requested resource not found", Toast.LENGTH_LONG).show();
                                                }
                                                // When Http response code is '500'
                                                else if (statusCode == 500) {
                                                    Toast.makeText(getApplicationContext(), "Something went wrong at server end", Toast.LENGTH_LONG).show();
                                                }
                                                // When Http response code other than 404, 500
                                                else {
                                                    Toast.makeText(getApplicationContext(), "Posting failed", Toast.LENGTH_LONG).show();
                                                    // Toast.makeText(getApplicationContext(), "Unexpected Error occcured! [Most common Error: Device might not be connected to Internet or remote server is not up and running]", Toast.LENGTH_LONG).show();
                                                }
                                            }

                                        }

                                );
                            }
                        });
                    } else {
                        Intent i = new Intent(MainActivity.this, PostingActivity.class);
                        startActivity(i);
                    }
                }
            });
            if (android.os.Build.VERSION.SDK_INT > 9) {
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);
            }
        } else {
            setContentView(R.layout.activity_main);

        }


        mToolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        setSupportActionBar(mToolbar);


//        if (bundle == null) {
//            LinearLayout login = (LinearLayout) findViewById(R.id.login);
//            login.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
////                    Intent i = new Intent(MainActivity.this, LoginActivity.class);
////
////                    startActivity(i);
//                }
//            });
//        }


        LinearLayout about = (LinearLayout) findViewById(R.id.about);
        about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Starting a new Intent for about
                Intent i = new Intent(MainActivity.this, AboutActivity.class);

                startActivity(i);
            }
        });

        LinearLayout developer = (LinearLayout) findViewById(R.id.about_developer);
        developer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Starting a new Intent for about
                Intent i = new Intent(MainActivity.this, DeveloperActivity.class);

                startActivity(i);
            }
        });


        if (sharedPreferences.getBoolean("login", false)) {
            // user login
            Log.e("isi", sharedPreferences.getString("loginImage", ""));
            mNavigationDrawerFragmentUser = (NavigationDrawerFragmentUser)
                    getFragmentManager().findFragmentById(R.id.fragment_drawer_user);
            mNavigationDrawerFragmentUser.setup(R.id.fragment_drawer_user, (DrawerLayout) findViewById(R.id.drawer), mToolbar);
            mNavigationDrawerFragmentUser.setUserData(sharedPreferences.getString("loginName", ""), sharedPreferences.getString("loginEmail", ""), getBitmapFromURL(this, "http://openetizen.com" + sharedPreferences.getString("loginImage", "")));


        } else {
            // user dereng login
            mNavigationDrawerFragment = (NavigationDrawerFragment)
                    getFragmentManager().findFragmentById(R.id.fragment_drawer);
            mNavigationDrawerFragment.setup(R.id.fragment_drawer, (DrawerLayout) findViewById(R.id.drawer), mToolbar);
            mNavigationDrawerFragment.setUserData(BitmapFactory.decodeResource(getResources(), R.drawable.login), BitmapFactory.decodeResource(getResources(), R.drawable.googleplus), BitmapFactory.decodeResource(getResources(), R.drawable.facebook));

        }

//        ParseUser currentUser = ParseUser.getCurrentUser();
//        if (currentUser != null) {
//            Intent i = new Intent(MainActivity.this, MainActivity.class);
//            i.putExtra("login_name",currentUser.getUsername());
//            startActivity(i);
//        } else {
//            // show the signup or login screen
//        }

//        ParseUser.logOut();
//        ParseUser currentUser = ParseUser.getCurrentUser(); // this will now be null
        // Set up the drawer.

        // populate the navigation drawer


//         display the first navigation drawer view on app launch
//        if (bundle != null && bundle.containsKey("Fragment") && bundle.getString("Fragment").equals("MyGallery")) {
        if (bundle != null) {
            if (bundle.containsKey("Fragment")) {
                if (bundle.getString("Fragment").equals("MyGallery")) {
                    onNavigationDrawerItemSelected(6);
                    getSupportActionBar().setTitle(R.string.title_my_gallery);
                }
            } else {
                onNavigationDrawerItemSelected(0);

            }
        }

//        }

        //StartFloating Button

        //End Floating Button


    }

    public void loginButton(View view) {
//        new MaterialDialog.Builder(this)
//                .title(R.string.title_dialog)
//                .content(R.string.content_dialog)
//                .positiveText(R.string.yes)
//                .negativeText(R.string.no)
//                .show();

        AlertDialog.Builder builder =
                new AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle);
        builder.setTitle(R.string.title_dialog);
        builder.setMessage(R.string.content_dialog);
        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
        builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.show();

    }


    @Override
    public void onNavigationDrawerItemSelected(int position) {

        FragmentManager fragmentManager = getSupportFragmentManager();
        android.support.v4.app.FragmentTransaction transaction
                = fragmentManager.beginTransaction();
        switch (position) {
            case 0:
                transaction.replace(R.id.container, new CategoryOneFragment());
                transaction.setTransition(android.support.v4.app.FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                transaction.commit();
                if (menuFragment == "MyGallery") {
                    menu_article.setImageResource(R.drawable.ic_create_white_24dp);
                    menuFragment = "";
                }
                //getSupportActionBar().setTitle(R.string.title_news);
                break;
            case 1:
                transaction.replace(R.id.container, new NewsFragment());
                transaction.setTransition(android.support.v4.app.FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                transaction.commit();
                getSupportActionBar().setTitle(R.string.title_news);
                if (menuFragment == "MyGallery") {
                    menu_article.setImageResource(R.drawable.ic_create_white_24dp);
                    menuFragment = "";
                }

                break;
            case 2:
                transaction.replace(R.id.container, new AgendaFragment());
                transaction.setTransition(android.support.v4.app.FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                transaction.commit();
                getSupportActionBar().setTitle(R.string.title_agenda);
                if (menuFragment == "MyGallery") {
                    menu_article.setImageResource(R.drawable.ic_create_white_24dp);
                    menuFragment = "";
                }

                break;
            case 3:
                transaction.replace(R.id.container, new PromotionFragment());
                transaction.setTransition(android.support.v4.app.FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                transaction.commit();
                getSupportActionBar().setTitle(R.string.title_promotion);
                if (menuFragment == "MyGallery") {
                    menu_article.setImageResource(R.drawable.ic_create_white_24dp);
                    menuFragment = "";
                }
                break;
            case 4:
                transaction.replace(R.id.container, new GalleryFragment());
                transaction.setTransition(android.support.v4.app.FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                transaction.commit();
                getSupportActionBar().setTitle(R.string.title_gallery);
                if (menuFragment == "MyGallery") {
                    menu_article.setImageResource(R.drawable.ic_create_white_24dp);
                    menuFragment = "";
                }
                break;
            case 5:
                transaction.replace(R.id.container, new HistoryFragment());
                transaction.setTransition(android.support.v4.app.FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                transaction.commit();
                getSupportActionBar().setTitle(R.string.title_history);
                if (menuFragment == "MyGallery") {
                    menu_article.setImageResource(R.drawable.ic_create_white_24dp);
                    menuFragment = "";
                }
                break;
            case 6:
                transaction.replace(R.id.container, new MyGalleryFragment());
                transaction.setTransition(android.support.v4.app.FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                transaction.commit();
                getSupportActionBar().setTitle(R.string.title_my_gallery);
                menu_article.setImageResource(R.drawable.ic_add_to_photos_white_24dp);
                menuFragment = "MyGallery";
                break;

            default:
                break;
        }
    }

    public void actionMenu(View view) {


    }

    public void uploadPhoto(View view) {
        Intent i = new Intent(this, UploadPhotoActivity.class);
        startActivity(i);
    }

    public void logoutButton(View view) {
        AlertDialog.Builder builder =
                new AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle);
        builder.setTitle(R.string.title_dialog_logout);
        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                SharedPreferences sharedpreferences = getSharedPreferences(LoginActivity.MyPREFERENCES, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putBoolean("login", false);
                editor.clear();
                editor.commit();
                Intent a = new Intent(MainActivity.this, MainActivity.class);
                startActivity(a);
            }
        });
        builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.show();

    }

    @Override

    public void onBackPressed() {
        if (!sharedPreferences.getBoolean("login", false)) {
            if (mNavigationDrawerFragment.isDrawerOpen())
                mNavigationDrawerFragment.closeDrawer();
            else
                super.onBackPressed();
        } else {
            if (mNavigationDrawerFragmentUser.isDrawerOpen())
                mNavigationDrawerFragmentUser.closeDrawer();
            else
                super.onBackPressed();
        }

        /*int count = getFragmentManager().getBackStackEntryCount();

        if (count == 3) {
            replaceFragments(new GalleryFragment(),null);
        } else {
            super.onBackPressed();
        }*/
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!sharedPreferences.getBoolean("login", false)) {
            if (!mNavigationDrawerFragment.isDrawerOpen()) {
                // Only show items in the action bar relevant to this screen
                // if the drawer is not showing. Otherwise, let the drawer
                // decide what to show in the action bar.
                getMenuInflater().inflate(R.menu.main, menu);
                return true;
            }
        } else {
            if (!mNavigationDrawerFragmentUser.isDrawerOpen()) {
                // Only show items in the action bar relevant to this screen
                // if the drawer is not showing. Otherwise, let the drawer
                // decide what to show in the action bar.
                getMenuInflater().inflate(R.menu.main, menu);
                return true;
            }
        }

        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            Intent i = new Intent(this, SettingsActivity.class);
//            startActivity(i);
//            return true;
//        }
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                Intent upIntent = NavUtils.getParentActivityIntent(this);
                if (NavUtils.shouldUpRecreateTask(this, upIntent)) {
                    // This activity is NOT part of this app's task, so create a new task
                    // when navigating up, with a synthesized back stack.
                    TaskStackBuilder.create(this)
                            // Add all of this activity's parents to the back stack
                            .addNextIntentWithParentStack(upIntent)
                                    // Navigate up to the closest parent
                            .startActivities();
                } else {
                    // This activity is part of this app's task, so simply
                    // navigate up to the logical parent activity.
                    NavUtils.navigateUpTo(this, upIntent);
                }
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /*public void replaceFragments(Fragment fragment, Bundle bundle) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        android.support.v4.app.FragmentTransaction transaction
                = fragmentManager.beginTransaction();
        if(bundle!=null) {
            fragment.setArguments(bundle);
        }
        transaction.replace(R.id.container, fragment);
        transaction.setTransition(android.support.v4.app.FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        transaction.commit();
        if(bundle!=null) {
            getSupportActionBar().setTitle("Photo");
        }else{
            getSupportActionBar().setTitle("Gallery");
        }

    }*/

    public static Bitmap getBitmapFromURL(Context context, String src) {
        Bitmap myBitmap = null;
        try {
            URL url = new URL(src);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            if (isNetworkAvailable(context)) {
                InputStream input = connection.getInputStream();
                myBitmap = BitmapFactory.decodeStream(input);
            } else {
                myBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.icon_opennews);
            }
            return myBitmap;
        } catch (IOException e) {
            myBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.icon_opennews);
            return myBitmap;
        }
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


}

