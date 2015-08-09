package com.grilla.hereseum.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import com.grilla.hereseum.R;
import com.grilla.hereseum.helper.SharedPrefsHelper;


public class LaunchActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);

        String accessToken = SharedPrefsHelper.getString(this, SharedPrefsHelper.KEY_ACCESS_TOKEN);
        if (accessToken != null) {
            // Already authenticated
            Intent i = new Intent(this, MainActivity.class);
            i.putExtra(MainActivity.EXTRA_ACCESS_TOKEN, accessToken);
            startActivity(i);
            finish();
        }

        ProgressBar.class.cast(findViewById(R.id.launch_loading)).setVisibility(View.GONE);
        Button.class.cast(findViewById(R.id.login_button)).setVisibility(View.VISIBLE);
    }

    public void login(View v) {
        Intent i = new Intent(this, InstagramAuthActivity.class);
        startActivity(i);
    }
}
