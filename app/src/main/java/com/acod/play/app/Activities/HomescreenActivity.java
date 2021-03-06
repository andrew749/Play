package com.acod.play.app.Activities;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.acod.play.app.Constants;
import com.acod.play.app.Fragments.HomeFragment;
import com.acod.play.app.Models.Song;
import com.acod.play.app.R;
import com.acod.play.app.Searching.SearchMuzikApi;
import com.inscription.ChangeLogDialog;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;


/**
 * Created by andrew on 12/21/14.
 */

/**
 * @author Andrew Codispoti
 *         This is the main activtiy that will contain the vairous fragments and also do all of the searching system wide.
 */
public class HomescreenActivity extends AppCompatActivity {

    //Constants
    public static final String PLAY_ACTION = "com.acod.play.playmusic";
    public static final String PAUSE_ACTION = "com.acod.play.pausemusic";
    public static final String STOP_ACTION = "com.acod.play.stopmusic";

    //To turn on and off debugging code
    public static final boolean debugMode = false;

    //For stageout rollouts.
    public static float APP_VERSION = 1;

    //Fragment manager to handle transactions
    FragmentManager manager;

    FragmentTransaction fragmentTransaction;

    private DrawerLayout drawerLayout;
    private ListView drawerList;

    //The strings for the navigation drawer.
    private String[] drawertitles;

    //Handles the drawer icon
    private ActionBarDrawerToggle toggle;

    //The main fragment to hold content.
    private HomeFragment frag;

    public static boolean checkNetworkState(Context context) {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE") || ni.getTypeName().equalsIgnoreCase("Cellular"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    private void handleGooglePlus() {
        String communityPage = "communities/112916674490953671434";
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setClassName("com.google.android.apps.plus",
                    "com.google.android.apps.plus.phone.UrlGatewayActivity");
            intent.putExtra("customAppUri", communityPage);
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            // fallback if G+ app is not installed
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://plus.google.com/" + communityPage)));
        }
    }

    public void handleTwitter() {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("twitter://user?screen_name=andrewcod749"));
            startActivity(intent);

        } catch (Exception e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://twitter.com/#!/andrewcod749")));
        }
    }

    public static Intent getOpenFacebookIntent(Context context) {
        try {
            context.getPackageManager().getPackageInfo("com.facebook.katana", 0);
            return new Intent(Intent.ACTION_VIEW, Uri.parse("fb://page/296814327167093"));
        } catch (Exception e) {
            return new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/pages/Play/296814327167093"));
        }
    }

    public void startEmailIntent() {
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("message/rfc822");
        i.putExtra(Intent.EXTRA_EMAIL, new String[]{"andrewcod749@gmail.com"});
        i.putExtra(Intent.EXTRA_SUBJECT, "Play (Android)");
        i.putExtra(Intent.EXTRA_TEXT, "");
        try {
            startActivity(Intent.createChooser(i, "Send mail..."));
        } catch (ActivityNotFoundException ex) {
            Toast.makeText(this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!checkNetworkState(this)) {
            Toast.makeText(this, "Check your internet connection", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homescreen);

        drawertitles = getResources().getStringArray(R.array.menutiems);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerList = (ListView) findViewById(R.id.left_drawer);
        drawerList.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, drawertitles));
        drawerList.setOnItemClickListener(new DrawerItemClickListener());
        toggle = new ActionBarDrawerToggle(this, drawerLayout, R.drawable.ic_drawer, R.string.opendrawer, R.string.app_name);
        manager = getFragmentManager();
        fragmentTransaction = manager.beginTransaction();
        frag = new HomeFragment();
        fragmentTransaction.replace(R.id.content_frame, frag).addToBackStack(null);
        fragmentTransaction.commit();
        drawerLayout.setDrawerListener(toggle);
        drawerLayout.post(new Runnable() {
            @Override
            public void run() {
                toggle.syncState();
            }
        });

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_drawer);

        //setup the navigation drawer
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        toggle.setDrawerIndicatorEnabled(true);
    }

    @Override
    public void onPostCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onPostCreate(savedInstanceState, persistentState);
        toggle.syncState();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case android.R.id.home:
                if (!drawerLayout.isDrawerOpen(Gravity.LEFT))
                    drawerLayout.openDrawer(Gravity.LEFT);
                else drawerLayout.closeDrawer(Gravity.LEFT);
                break;
            case R.id.changes:
                ChangeLogDialog dialog = new ChangeLogDialog(getApplicationContext());
                dialog.show();
                break;
            case R.id.report:
                startEmailIntent();
                break;
        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        toggle.onConfigurationChanged(newConfig);
        setContentView(R.layout.activity_homescreen);
        getFragmentManager().beginTransaction().replace(R.id.content_frame, frag).commit();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        toggle.syncState();
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            switch (i) {
                case 0:
                    //my songs section
                    startActivity(new Intent(getApplicationContext(), SavedActivity.class));
                    break;
                case 1:
                    //twitter list
                    handleTwitter();
                    break;
                case 2:
                    //facebook page
                    startActivity(getOpenFacebookIntent(getApplicationContext()));
                    break;
                case 3:
                    //google plus community
                    handleGooglePlus();
                    break;

            }
        }
    }


}
