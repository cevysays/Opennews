package com.openetizen.cevysays.opennews.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import com.openetizen.cevysays.opennews.R;
import com.openetizen.cevysays.opennews.fragments.AgendaFragment;
import com.openetizen.cevysays.opennews.fragments.CategoryOneFragment;
import com.openetizen.cevysays.opennews.fragments.GalleryFragment;
import com.openetizen.cevysays.opennews.fragments.HistoryFragment;
import com.openetizen.cevysays.opennews.fragments.NavigationDrawerFragment;
import com.openetizen.cevysays.opennews.fragments.NavigationDrawerFragmentUser;
import com.openetizen.cevysays.opennews.fragments.PromotionFragment;
import com.openetizen.cevysays.opennews.util.NavigationDrawerCallbacks;


public class MainActivity extends ActionBarActivity
        implements NavigationDrawerCallbacks {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;
    private NavigationDrawerFragmentUser mNavigationDrawerFragmentUser;
    private Toolbar mToolbar;

    Bundle bundle = new Bundle();

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        bundle = getIntent().getExtras();


        sharedPreferences = getSharedPreferences(LoginActivity.MyPREFERENCES, MODE_PRIVATE);

        if (sharedPreferences.getBoolean("login", false)) {
            setContentView(R.layout.activity_main_activity_user);
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
            mNavigationDrawerFragmentUser = (NavigationDrawerFragmentUser)
                    getFragmentManager().findFragmentById(R.id.fragment_drawer_user);
            mNavigationDrawerFragmentUser.setup(R.id.fragment_drawer_user, (DrawerLayout) findViewById(R.id.drawer), mToolbar);
            mNavigationDrawerFragmentUser.setUserData(sharedPreferences.getString("loginName", ""), sharedPreferences.getString("loginEmail", ""), BitmapFactory.decodeResource(getResources(), R.drawable.ic_person_black_18dp));

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
        onNavigationDrawerItemSelected(0);

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
                //getSupportActionBar().setTitle(R.string.title_news);
                break;
            case 1:
                transaction.replace(R.id.container, new AgendaFragment());
                transaction.setTransition(android.support.v4.app.FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                transaction.commit();
                getSupportActionBar().setTitle(R.string.title_agenda);

                break;
            case 2:
                transaction.replace(R.id.container, new PromotionFragment());
                transaction.setTransition(android.support.v4.app.FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                transaction.commit();
                getSupportActionBar().setTitle(R.string.title_promotion);
                break;
            case 3:
                transaction.replace(R.id.container, new GalleryFragment());
                transaction.setTransition(android.support.v4.app.FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                transaction.commit();
                getSupportActionBar().setTitle(R.string.title_gallery);
                break;
            case 4:
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

    public void logoutButton(View view) {
        AlertDialog.Builder builder =
                new AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle);
        builder.setTitle(R.string.title_dialog_delete);
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


}

