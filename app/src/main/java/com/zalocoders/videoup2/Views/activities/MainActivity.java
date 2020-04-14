package com.zalocoders.videoup2.Views.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.os.Bundle;
import android.os.Handler;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.storage.StorageTask;
import com.zalocoders.videoup2.R;
import com.zalocoders.videoup2.Views.Adapter.VideoAdapter;

import fm.jiecao.jcvideoplayer_lib.JCVideoPlayer;
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayerStandard;

public class MainActivity extends AppCompatActivity  {

    BottomNavigationView bottomNavigationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        navigation();
    }



    //setting up navigation Components to handle bottom navigation click
    public void navigation() {

        bottomNavigationView = findViewById(R.id.bottom_nav);

      final NavController  navController = Navigation.findNavController(this, R.id.nav_host_fragment);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                //setting up the nav controller
                NavigationUI.setupWithNavController(bottomNavigationView, navController);
                navController.addOnDestinationChangedListener(new NavController.OnDestinationChangedListener() {
                    @Override
                    public void onDestinationChanged(@NonNull NavController controller, @NonNull NavDestination destination, @Nullable Bundle arguments) {

                    }
                });
            }
        },200);

    }

}
