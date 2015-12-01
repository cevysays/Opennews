package com.openetizen.cevysays.opennews.activity;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.openetizen.cevysays.opennews.R;
import com.openetizen.cevysays.opennews.fragments.AgendaFragment;
import com.openetizen.cevysays.opennews.fragments.HistoryFragment;
import com.openetizen.cevysays.opennews.fragments.GalleryFragment;
import com.openetizen.cevysays.opennews.fragments.MainFragment;
import com.openetizen.cevysays.opennews.fragments.NavigationDrawerFragmentUser;
import com.openetizen.cevysays.opennews.util.NavigationDrawerCallbacks;

public class MainActivityUser extends ActionBarActivity
        implements NavigationDrawerCallbacks {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragmentUser mNavigationDrawerFragmentUser;
    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_activity_user);
        mToolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        setSupportActionBar(mToolbar);

        //Drawer item
        ImageView login = (ImageView) findViewById(R.id.login);


        LinearLayout about = (LinearLayout) findViewById(R.id.about);
        about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Starting a new Intent for about
                Intent i = new Intent(MainActivityUser.this, AboutActivity.class);

                startActivity(i);
            }
        });

//        LinearLayout help = (LinearLayout) findViewById(R.id.help);
//        help.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                //Starting a new Intent for about
//                Intent i = new Intent(MainActivityUser.this, DeveloperActivity.class);
//
//                startActivity(i);
//            }
//        });

        LinearLayout logout = (LinearLayout) findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Starting a new Intent for about
                Intent i = new Intent(MainActivityUser.this, LoginActivity.class);

                startActivity(i);
            }
        });

        mNavigationDrawerFragmentUser = (NavigationDrawerFragmentUser) getFragmentManager().findFragmentById(R.id.fragment_drawer_user);

        // Set up the drawer.
        mNavigationDrawerFragmentUser.setup(R.id.fragment_drawer_user, (DrawerLayout) findViewById(R.id.drawer), mToolbar);
        // populate the navigation drawer
        mNavigationDrawerFragmentUser.setUserData("John Doe", "johndoe@doe.com", BitmapFactory.decodeResource(getResources(), R.drawable.avatar));
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        //Toast.makeText(this, "Menu item selected -> " + position, Toast.LENGTH_SHORT).show();
        FragmentManager fragmentManager = getSupportFragmentManager();
        android.support.v4.app.FragmentTransaction transaction
                = fragmentManager.beginTransaction();
        switch (position) {
            case 0:
                transaction.replace(R.id.container, new MainFragment());
                transaction.setTransition(android.support.v4.app.FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                transaction.commit();
                break;
            case 1:
                startActivity(new Intent(this, null));
                break;

            case 2:
                startActivity(new Intent(this, PostingActivity.class));
                break;

            case 3:
                transaction.replace(R.id.container, new AgendaFragment());
                transaction.setTransition(android.support.v4.app.FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                transaction.commit();
                getSupportActionBar().setTitle(R.string.title_agenda);

                break;
            case 4:
                transaction.replace(R.id.container, new GalleryFragment());
                transaction.setTransition(android.support.v4.app.FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                transaction.commit();
                getSupportActionBar().setTitle(R.string.title_gallery);
                break;
            case 5:
                transaction.replace(R.id.container, new HistoryFragment());
                transaction.setTransition(android.support.v4.app.FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                transaction.commit();
                getSupportActionBar().setTitle(R.string.title_document);
                break;

            default:
                break;
        }
    }


    @Override
    public void onBackPressed() {
        if (mNavigationDrawerFragmentUser.isDrawerOpen())
            mNavigationDrawerFragmentUser.closeDrawer();
        else
            super.onBackPressed();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragmentUser.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            return true;
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
