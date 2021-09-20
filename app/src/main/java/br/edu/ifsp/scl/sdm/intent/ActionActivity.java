package br.edu.ifsp.scl.sdm.intent;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import br.edu.ifsp.scl.sdm.intent.databinding.ActivityActionBinding;

public class ActionActivity extends AppCompatActivity {

    private ActivityActionBinding activityActionBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_action);
        activityActionBinding = ActivityActionBinding.inflate(getLayoutInflater());
        setContentView(activityActionBinding.getRoot());

        activityActionBinding.mainTb.appTb.setTitle(this.getClass().getSimpleName());
        activityActionBinding.mainTb.appTb.setSubtitle((getIntent().getAction()));
        setSupportActionBar(activityActionBinding.mainTb.appTb);

        activityActionBinding.parameterTv.setText(getIntent().getStringExtra(Intent.EXTRA_TEXT));
    }
}