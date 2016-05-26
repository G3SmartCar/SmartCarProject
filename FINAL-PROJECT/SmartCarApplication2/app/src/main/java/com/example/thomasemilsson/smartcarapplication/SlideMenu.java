package com.example.thomasemilsson.smartcarapplication;

        import android.app.FragmentManager;
        import android.os.Bundle;
        import android.support.design.widget.FloatingActionButton;
        import android.support.design.widget.Snackbar;
        import android.transition.Slide;
        import android.view.View;
        import android.support.design.widget.NavigationView;
        import android.support.v4.view.GravityCompat;
        import android.support.v4.widget.DrawerLayout;
        import android.support.v7.app.ActionBarDrawerToggle;
        import android.support.v7.app.AppCompatActivity;
        import android.support.v7.widget.Toolbar;
        import android.view.Menu;
        import android.view.MenuItem;
        import android.content.Intent;
        import android.widget.ListView;
        import android.widget.AdapterView;


public class SlideMenu extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    boolean synthetic = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //   FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        //   fab.setOnClickListener(new View.OnClickListener() {
        //       @Override
        //       public void onClick(View view) {
        //           Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
        //                   .setAction("Action", null).show();
        //       }
        //   });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.END)) {
            drawer.closeDrawer(GravityCompat.END);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {


        int id = item.getItemId();

        if(id == R.id.nav_first_layout){
            Intent intent = new Intent(SlideMenu.this, ControlActivity.class);
            startActivity(intent);
        }

        /*else if(id == R.id.nav_second_layout){
            Intent intent = new Intent(SlideMenu.this, TiltControl.class);
            startActivity(intent);
        }*/

        // Handle navigation view item clicks here.
        //int id = item.getItemId();

        // FragmentManager fm = getFragmentManager();

        // MenuItem to enable Joystick
        //if (id == R.id.nav_first_layout) {
        //    fm.beginTransaction()
        //            .replace(R.id.content_joystick,
        //                    new Test())
        //            .commit();
        // MenuItem to enable Tilt
        //  } else if (id == R.id.nav_second_layout) {
        //     fm.beginTransaction()
        //             .replace(R.id.content_tilt,
        //                     new Tilt())
        //             .commit();
        //  Intent intent

        //}


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.END);
        return true;
    }


    private class DrawerItemClickListener implements NavigationView.OnNavigationItemSelectedListener {

        public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

            switch (position) {
                case 0: {
                    Intent intent = new Intent(SlideMenu.this, ControlActivity.class);
                    startActivity(intent);
                    break;
                }
                /*case 1: {
                    Intent intent = new Intent(SlideMenu.this, TiltControl.class);
                    startActivity(intent);
                    break;
                }*/
                default:
                    break;
            }
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.END);
        }

        @SuppressWarnings("StatementWithEmptyBody")
        @Override
        public boolean onNavigationItemSelected(MenuItem item) {
            boolean internal;
            // Handle navigation view item clicks here.
            //int id = item.getItemId();

            // FragmentManager fm = getFragmentManager();

            // MenuItem to enable Joystick
            //if (id == R.id.nav_first_layout) {
            //    fm.beginTransaction()
            //            .replace(R.id.content_joystick,
            //                    new Test())
            //            .commit();
            // MenuItem to enable Tilt
            //  } else if (id == R.id.nav_second_layout) {
            //     fm.beginTransaction()
            //             .replace(R.id.content_tilt,
            //                     new Tilt())
            //             .commit();
            //  Intent intent


            // }

            if (synthetic) {
                synthetic = false;
                internal = true;
            }
            else {
                internal = false;
            }
            return internal;
        }


        // private void goToMenu(int item){
        //     Intent intent;
        //     switch(item){
        //         case NAV_JOY_ITEM:
        //         intent = new Intent(this, JoystickCamera.class);
        //         startActivity(intent);
        //         finish();

        //         case NAV_TILT_ITEM;
        //         intent = new Intent(this, Tilt.class);
        //         startActivity(intent);
        //         finish();

        //        break;
        //    }
        //}
    }
}