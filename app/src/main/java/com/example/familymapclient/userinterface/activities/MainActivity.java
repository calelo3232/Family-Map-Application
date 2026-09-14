package com.example.familymapclient.userinterface.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;
import com.joanzapata.iconify.fonts.FontAwesomeModule;
import com.example.familymapclient.R;
import com.example.familymapclient.userinterface.activities.fragments.LoginFragment;
import com.example.familymapclient.userinterface.activities.fragments.MapsFragment;

public class MainActivity extends AppCompatActivity implements LoginFragment.Listener {
    private boolean menuFlag;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentById(R.id.fragmentContainer);
        menuFlag = false;
        if (fragment == null) {
            fragment = createLoginFragment();
            fragmentManager.beginTransaction().add(R.id.fragmentContainer, fragment).commit();
        } else {
            if (fragment instanceof LoginFragment) {
                ((LoginFragment) fragment).registerListener(this);
            }
        }
        Iconify.with(new FontAwesomeModule());
    }

    private Fragment createLoginFragment() {
        LoginFragment fragment = new LoginFragment();
        fragment.registerListener(this);
        return fragment;
    }

    @Override
    public void notifyDone() {
        setMenuFlag(true);
        invalidateOptionsMenu();
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = new MapsFragment();
        fragmentManager.beginTransaction().replace(R.id.fragmentContainer, fragment).commit();
    }

    public void setMenuFlag(boolean menuFlag) {
        this.menuFlag = menuFlag;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (menuFlag) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.main_menu, menu);
            MenuItem searchMenuItem = menu.findItem(R.id.searchMenuItem);
            MenuItem settingsMenuItem = menu.findItem(R.id.settingsMenuItem);
            searchMenuItem.setIcon(new IconDrawable(this, FontAwesomeIcons.fa_search).colorRes(R.color.colorWhite).actionBarSize());
            settingsMenuItem.setIcon(new IconDrawable(this, FontAwesomeIcons.fa_gear).colorRes(R.color.colorWhite).actionBarSize());
            return true;
        }
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        Intent intent = new Intent();
        switch(menuItem.getItemId()) {
            case R.id.searchMenuItem:
                intent.setClass(this, SearchActivity.class);
                this.startActivity(intent);
                return true;
            case R.id.settingsMenuItem:
                intent.setClass(this, SettingsActivity.class);
                this.startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(menuItem);
        }
    }
}