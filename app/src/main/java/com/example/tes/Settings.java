package com.example.tes;

import android.content.pm.PackageInfo;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class Settings extends AppCompatActivity {

    ListView lstView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);


        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        lstView = findViewById(R.id.lstView);


        // Info application
        PackageInfo pInfo = null;
        try {
             pInfo = this.getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (Exception e)
        {

        }
        List<String> tmp = new ArrayList<>();
        tmp.add("Nom du projet : "+pInfo.packageName);
        tmp.add("Numero de la version : "+ pInfo.versionName);
        tmp.add("Chemin d'installation : " + pInfo.installLocation);
        tmp.add("Informations d'application : " + pInfo.applicationInfo);


        // Print info in listView
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, tmp);
        adapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
        lstView.setAdapter(adapter);

    }


    // When back arrows is clicked
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()) {
            case android.R.id.home:
                setResult(RESULT_OK, getIntent());
                super.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
