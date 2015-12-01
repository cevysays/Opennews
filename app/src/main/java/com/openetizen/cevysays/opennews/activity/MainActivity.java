package com.openetizen.cevysays.opennews.activity;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import com.openetizen.cevysays.opennews.util.Utils;
import com.openetizen.cevysays.opennews.fragments.AgendaFragment;
import com.openetizen.cevysays.opennews.fragments.CategoryOneFragment;
import com.openetizen.cevysays.opennews.fragments.HistoryFragment;
import com.openetizen.cevysays.opennews.fragments.GalleryFragment;
import com.openetizen.cevysays.opennews.fragments.NavigationDrawerFragmentUser;
import com.openetizen.cevysays.opennews.util.NavigationDrawerCallbacks;
import com.openetizen.cevysays.opennews.R;
import com.openetizen.cevysays.opennews.fragments.NavigationDrawerFragment;
import com.parse.Parse;


public class MainActivity extends ActionBarActivity
        implements NavigationDrawerCallbacks {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;
    private NavigationDrawerFragmentUser mNavigationDrawerFragmentUser;
    private Toolbar mToolbar;

    String APPLICATION_ID = "JskEVHlU3OWG8tgcSVUDVU2tr0nru42WIr8VcIDp";
    String CLIENT_ID = "UT2FSR0bYf102HQ92veMmbk9othIaoTmvxSsiVDo";

    Bundle bundle = new Bundle();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        bundle = getIntent().getExtras();
        /*try{*/
        if (bundle == null) {
            setContentView(R.layout.activity_main);
        } else {
            setContentView(R.layout.activity_main_activity_user);
        }
//        }catch (NullPointerException e){
//            setContentView(R.layout.activity_main);
//        }


        mToolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        setSupportActionBar(mToolbar);

        if (Utils.isInitialized == false) {
            Parse.initialize(this, APPLICATION_ID, CLIENT_ID);
            Utils.isInitialized = true;
        }

        if (bundle == null) {
            LinearLayout login = (LinearLayout) findViewById(R.id.login);
            login.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(MainActivity.this, LoginActivity.class);

                    startActivity(i);
                }
            });
        }


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


        if (bundle != null) {
            mNavigationDrawerFragmentUser = (NavigationDrawerFragmentUser)
                    getFragmentManager().findFragmentById(R.id.fragment_drawer_user);
            mNavigationDrawerFragmentUser.setup(R.id.fragment_drawer_user, (DrawerLayout) findViewById(R.id.drawer), mToolbar);
            mNavigationDrawerFragmentUser.setUserData(bundle.getString("login_name"), "", BitmapFactory.decodeResource(getResources(), R.drawable.ic_person_black_18dp));
        } else {
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
        onNavigationDrawerItemSelected(0);

        //StartFloating Button

        //End Floating Button
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
                //getSupportActionBar().setTitle(R.string.title_news);
                break;
            case 1:
                transaction.replace(R.id.container, new AgendaFragment());
                transaction.setTransition(android.support.v4.app.FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                transaction.commit();
                getSupportActionBar().setTitle(R.string.title_agenda);

                break;
            case 2:
                transaction.replace(R.id.container, new GalleryFragment());
                transaction.setTransition(android.support.v4.app.FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                transaction.commit();
                getSupportActionBar().setTitle(R.string.title_gallery);
                break;
            case 3:
                transaction.replace(R.id.container, new HistoryFragment());
                transaction.setTransition(android.support.v4.app.FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                transaction.commit();
                getSupportActionBar().setTitle(R.string.title_history);
                break;

            default:
                break;
        }
    }

    public void createArticle(View view) {
        Intent i = new Intent(this, PostingActivity.class);
        startActivity(i);
    }

    public void uploadPhoto(View view) {
        Intent i = new Intent(this, UploadPhotoActivity.class);
        startActivity(i);
    }

    @Override
    public void onBackPressed() {
        if (bundle == null) {
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
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (bundle == null) {
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
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent i = new Intent(this, SettingsActivity.class);
            startActivity(i);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

