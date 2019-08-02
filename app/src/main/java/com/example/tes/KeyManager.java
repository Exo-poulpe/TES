package com.example.tes;

import android.Manifest;
import android.content.Context;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.File;
import java.io.FileWriter;
import java.io.OutputStreamWriter;
import java.lang.reflect.Array;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class KeyManager extends AppCompatActivity {


    Button btnGen,btnDelete;
    EditText tbxPass,tbxName;
    Spinner spinerFiles;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_key_manager);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        btnGen = (Button) findViewById(R.id.btnGenerate);
        btnDelete = (Button) findViewById(R.id.btnDeleteKey);
        tbxName = (EditText) findViewById(R.id.tbxKeyName);
        tbxPass = (EditText) findViewById(R.id.tbxKeyValue);
        spinerFiles = (Spinner) findViewById(R.id.spnKey);

        // Event
        btnGen.setOnClickListener(new Clickgen());
        btnDelete.setOnClickListener(new DeleteKeyPass());


        tbxName.requestFocus();
        ListAllKeyFile();

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


    // Refresh spinner list
    private void ListAllKeyFile()
    {
        String[] tmp = new File(MainActivity.path + File.separator + MainActivity.fold ).list();
        List<String>files = new ArrayList<>();
        for (String f:tmp ) {
            if (f.contains(".key"))
            {
                files.add(f);
            }
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, files);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinerFiles.setAdapter(adapter);
    }


    private class Clickgen implements View.OnClickListener
    {
        @Override
        public void onClick(View view) {
            try
            {
                String fileName = tbxName.getText().toString() + ".key";
                byte[] tmp = MessageDigest.getInstance("md5").digest(tbxPass.getText().toString().getBytes());
                StringBuffer hexString = new StringBuffer();
                for (int i=0; i<tmp.length; i++)
                {
                    hexString.append(Integer.toHexString(0xFF & tmp[i]));
                }
                String data = hexString.toString().substring(0,8);

                String filePath = MainActivity.path + File.separator + MainActivity.fold + File.separator + fileName;
                 new File(filePath).createNewFile();
                FileWriter out = new FileWriter(new File(filePath));
                out.write(data);
                out.close();
                Toast.makeText(getApplicationContext(),"Mot de passe créer",Toast.LENGTH_SHORT).show();
                tbxPass.setText("");
                tbxName.setText("");
                ListAllKeyFile();
            }
            catch (Exception e)
            {
                Toast.makeText(getApplicationContext(),"Erreur lors de la création de la clé",Toast.LENGTH_SHORT).show();
            }
        }
    }


    private class DeleteKeyPass implements View.OnClickListener
    {
        @Override
        public void onClick(View view) {
            if (new File(MainActivity.path + File.separator + MainActivity.fold + File.separator + spinerFiles.getSelectedItem().toString()).exists())
            {
                new File(MainActivity.path + File.separator + MainActivity.fold + File.separator + spinerFiles.getSelectedItem().toString()).delete();
                ListAllKeyFile();
            }
        }
    }


}
