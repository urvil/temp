package com.reveautomation.revesmartsecuritykit_online;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.reveautomation.revesmartsecuritykit_online.example1_scanning.DashboardActivity;


public class DrawerUtil {

    public static void getDrawer(final Activity activity, Toolbar toolbar) {
        //if you want to update the items at a later time it is recommended to keep it in a variable
        PrimaryDrawerItem drawerEmptyItem= new PrimaryDrawerItem().withIdentifier(0).withName("");
        drawerEmptyItem.withEnabled(false);

        PrimaryDrawerItem drawerItemManagePlayers = new PrimaryDrawerItem().withIdentifier(1)
                .withName(R.string.app_name).withIcon(R.drawable.add);
        PrimaryDrawerItem drawerItemManagePlayersTournaments = new PrimaryDrawerItem()
                .withIdentifier(2).withName(R.string.app_name).withIcon(R.drawable.add);


        SecondaryDrawerItem drawerItemSettings = new SecondaryDrawerItem().withIdentifier(3)
                .withName(R.string.action_settings).withIcon(R.drawable.ic_menu_manage);
        SecondaryDrawerItem drawerItemAbout = new SecondaryDrawerItem().withIdentifier(4)
                .withName(R.string.start_scan).withIcon(R.drawable.ic_menu_gallery);
        SecondaryDrawerItem drawerItemHelp = new SecondaryDrawerItem().withIdentifier(5)
                .withName(R.string.app_name).withIcon(R.drawable.ic_menu_send);
        SecondaryDrawerItem drawerItemDonate = new SecondaryDrawerItem().withIdentifier(6)
                .withName(R.string.app_name).withIcon(R.drawable.ic_menu_gallery);





        //create the drawer and remember the `Drawer` result object
        Drawer result = new DrawerBuilder()
                .withActivity(activity)
                .withToolbar(toolbar)
                .withActionBarDrawerToggle(true)
                .withActionBarDrawerToggleAnimated(true)
                .withCloseOnClick(true)
                .withSelectedItem(-1)
                .addDrawerItems(
                        drawerEmptyItem,drawerEmptyItem,drawerEmptyItem,
                        drawerItemManagePlayers,
                        drawerItemManagePlayersTournaments,
                        new DividerDrawerItem(),
                        drawerItemAbout,
                        drawerItemSettings,
                        drawerItemHelp,
                        drawerItemDonate
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        if (drawerItem.getIdentifier() == 2 && !(activity instanceof DashboardActivity)) {
                            // load tournament screen
                            Intent intent = new Intent(activity, DashboardActivity.class);
                            view.getContext().startActivity(intent);
                        }
                        return true;
                    }
                })
                .build();
    }
}