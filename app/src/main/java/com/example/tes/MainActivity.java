package com.example.tes;

import android.Manifest;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class MainActivity extends AppCompatActivity {


    Button btnCrypt,btnCopy,btnClear,btnPaste,btnEdit;
    EditText password,text;
    Switch switchCrypt;
    Toolbar tool;
    Spinner spnPas;


    public static String path;
    public static String fold;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnCrypt = (Button) findViewById(R.id.btnCrypt);
        btnCopy = (Button) findViewById(R.id.btnCopy);
        btnEdit = (Button) findViewById(R.id.btnEdit);
        btnClear = (Button) findViewById(R.id.btnClear);
        btnPaste = (Button) findViewById(R.id.btnPaste);
        password = (EditText) findViewById(R.id.tbxPassword);
        text = (EditText) findViewById(R.id.tbxText);
        switchCrypt = (Switch) findViewById(R.id.switchCrypt);
        spnPas = (Spinner) findViewById(R.id.spnPassword);

        // Event on click
        btnCrypt.setOnClickListener(new DESCrypt());
        btnCopy.setOnClickListener(new CopyText());
        btnEdit.setOnClickListener(new EditPassword());
        btnClear.setOnClickListener(new ResetText());
        btnPaste.setOnClickListener(new PasteText());
        switchCrypt.setOnClickListener(new SwitchText());


        // Create folder String
        fold = "TES";
        path = Environment.getExternalStorageDirectory().toString();
        CreateFolderKey();
        spnPas.requestFocus();
        ListAllKeyFile();

    }



    // Create folder and Default file
    private void CreateFolderKey()
    {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE  )
                != PackageManager.PERMISSION_GRANTED) {

            if (ContextCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions( new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE},1 );
                //Toast.makeText(getApplicationContext(),"Application doit pouvoir lire et créer des fichiers",Toast.LENGTH_LONG).show();
            }

        }
        if (!new File(path + File.separator + fold).exists())
        {
            new File(path + File.separator + fold).mkdir();
            try
            {
                new File(path + File.separator + fold + File.separator + "default").createNewFile();
            } catch (Exception e)
            {
                Toast.makeText(getApplicationContext(),"Erreur création de fichier",Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Refresh spinner list
    private void ListAllKeyFile()
    {
        String[] tmp = new File(MainActivity.path + File.separator + MainActivity.fold ).list();
        List<String> files = new ArrayList<>();
        for (String f:tmp ) {
            if (f.contains(".key"))
            {
                files.add(f);
            }
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, files);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnPas.setAdapter(adapter);
    }

    // Create menu item
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_drawer, menu);
        return true;
    }

    // Get click on menu item
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.nav_first_fragment:
                Intent KeyMan = new Intent(this, KeyManager.class);
                startActivityForResult(KeyMan,1);
                break;
            case R.id.nav_second_fragment:
                Intent Setting = new Intent(this, Settings.class);
                startActivityForResult(Setting,1);
                break;
            default:
                Toast.makeText(getApplicationContext(),"Erreur",Toast.LENGTH_SHORT).show();
                break;
        }

        return true;
    }

    // Event when sub activity stop
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK)
        {
            ListAllKeyFile();
        }
    }

    // Switch button text & toggle text
    private class SwitchText implements View.OnClickListener
    {

        @Override
        public void onClick(View v) {

            if (switchCrypt.isChecked())
            {
                switchCrypt.setText("Dechiffre");
                btnCrypt.setText(R.string.decrypt);
            } else
            {
                switchCrypt.setText("Chiffre");
                btnCrypt.setText(R.string.crypt);
            }

        }
    }

    // Select methods to use
    private class DESCrypt implements View.OnClickListener
    {
        @Override
        public void onClick(View v) {
            if(switchCrypt.isChecked())
            {
                if (password.getVisibility() == View.VISIBLE)
                {
                    text.setText(DecryptString(password.getText().toString(),text.getText().toString()));
                }
                else if (spnPas.getVisibility() == View.VISIBLE)
                {
                    try
                    {
                        String filePath = MainActivity.path + File.separator + MainActivity.fold + File.separator +  spnPas.getSelectedItem().toString();
                        new File(filePath);
                        FileInputStream fs = new FileInputStream(filePath);
                        BufferedReader buf = new BufferedReader(new InputStreamReader(fs));
                        byte[] tmp = buf.readLine().getBytes();
                        buf.close();
                        text.setText(DecryptString(tmp,text.getText().toString()));
                    }
                    catch (Exception e)
                    {
                        Toast.makeText(getApplicationContext(),"Erreur lors de la selection du fichier clé",Toast.LENGTH_LONG).show();
                    }
                }
            } else
            {

                if (password.getVisibility() == View.VISIBLE)
                {
                    text.setText(EncryptString(password.getText().toString(),text.getText().toString()));
                }
                else if (spnPas.getVisibility() == View.VISIBLE)
                {
                    try
                    {
                        String filePath = MainActivity.path + File.separator + MainActivity.fold + File.separator + spnPas.getSelectedItem().toString();
                        //new File(filePath);
                        FileInputStream fs = new FileInputStream(filePath);
                        BufferedReader buf = new BufferedReader(new InputStreamReader(fs));
                        byte[] tmp = buf.readLine().getBytes();
                        buf.close();
                        text.setText(EncryptString(tmp,text.getText().toString()));
                    }
                    catch (Exception e)
                    {
                        Toast.makeText(getApplicationContext(),"Erreur lors de la selection du fichier clé",Toast.LENGTH_LONG).show();
                    }
                }
            }
        }
    }

    // Copy text to clipboard
    private class CopyText implements View.OnClickListener
    {
        @Override
        public void onClick(View v) {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(getApplicationContext().CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("CLIP",text.getText().toString());
            clipboard.setPrimaryClip(clip);
            Toast.makeText(getApplicationContext(),"Text copier",Toast.LENGTH_SHORT).show();
        }
    }

    // Clear text of textbox
    private class ResetText implements View.OnClickListener
    {
        @Override
        public void onClick(View v) {
            text.setText("");
        }
    }

    // Paste text to editText
    private class PasteText implements View.OnClickListener
    {
        @Override
        public void onClick(View v) {


            ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
            text.setText(clipboard.getPrimaryClip().getItemAt(0).getText());
            text.setText(text.getText().toString().replace(" ",""));
            text.setText(text.getText().toString().replace("\n",""));
        }
    }

    // Show password Edit Text
    private class EditPassword implements View.OnClickListener
    {
        @Override
        public void onClick(View view) {
            ListAllKeyFile();
            if (password.getVisibility() == View.INVISIBLE)
            {
                password.setVisibility(View.VISIBLE);
                spnPas.setVisibility(View.INVISIBLE);
            } else
            {
                spnPas.setVisibility(View.VISIBLE);
                password.setVisibility(View.INVISIBLE);
            }

        }
    }

    // Encrypt string with DES algo
    public String EncryptString(String pPassword,String pText)
    {
        String encryptText ="";
        try
        {
            final MessageDigest md = MessageDigest.getInstance("md5");
            final byte[] digestOfPassword = md.digest(pPassword.getBytes("utf-8"));
            final byte[] keyBytes = Arrays.copyOf(digestOfPassword, digestOfPassword.length/2);


            final SecretKey key = new SecretKeySpec(keyBytes, "DES");
            final Cipher decipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
            decipher.init(Cipher.ENCRYPT_MODE, key);

            final byte[] plainText = decipher.doFinal(pText.getBytes());
            encryptText = Base64.encodeToString(plainText,Base64.DEFAULT);

        }
        catch (Exception ex)
        {
            Toast.makeText(getApplicationContext(),"Erreur lors du chiffrement : "+ ex.getMessage(), Toast.LENGTH_LONG).show();
            encryptText = pText;
        }

        return encryptText;
    }

    public String EncryptString(byte[] HashedPasswords,String pText)
    {
        String encryptText ="";
        try
        {
            final SecretKey key = new SecretKeySpec(HashedPasswords, "DES");
            final Cipher decipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
            decipher.init(Cipher.ENCRYPT_MODE, key);

            final byte[] plainText = decipher.doFinal(pText.getBytes());
            encryptText = Base64.encodeToString(plainText,Base64.DEFAULT);

        }
        catch (Exception ex)
        {
            Toast.makeText(getApplicationContext(),"Erreur lors du chiffrement : "+ ex.getMessage(), Toast.LENGTH_LONG).show();
            encryptText = pText;
        }

        return encryptText;
    }

    // Decrypt stirng with DES algo
    public String DecryptString(String pPassword,String pText)
    {
        String decryptText ="";
        try
        {
            final MessageDigest md = MessageDigest.getInstance("md5");
            final byte[] digestOfPassword = md.digest(pPassword.getBytes("utf-8"));
            final byte[] keyBytes = Arrays.copyOf(digestOfPassword, digestOfPassword.length/2);


            final SecretKey key = new SecretKeySpec(keyBytes, "DES");
            final Cipher decipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
            decipher.init(Cipher.DECRYPT_MODE, key);

            if(pText.indexOf('\n') != -1)
            {
                pText = pText.substring(0,pText.indexOf('\n'));
            }

            final byte[] plainText = decipher.doFinal(Base64.decode(pText,Base64.DEFAULT));
            decryptText = new String(plainText);

        }
        catch (Exception ex)
        {
            Toast.makeText(getApplicationContext(),"Erreur lors du déchiffrement : "+ ex.getMessage(), Toast.LENGTH_LONG).show();
            decryptText = pText;
        }

        return decryptText;
    }

    public String DecryptString(byte[] Hashedpassword,String pText)
    {
        String decryptText ="";
        try
        {
            final SecretKey key = new SecretKeySpec(Hashedpassword, "DES");
            final Cipher decipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
            decipher.init(Cipher.DECRYPT_MODE, key);

            if(pText.indexOf('\n') != -1)
            {
                pText = pText.substring(0,pText.indexOf('\n'));
            }

            final byte[] plainText = decipher.doFinal(Base64.decode(pText,Base64.DEFAULT));
            decryptText = new String(plainText);

        }
        catch (Exception ex)
        {
            Toast.makeText(getApplicationContext(),"Erreur lors du déchiffrement : "+ ex.getMessage(), Toast.LENGTH_LONG).show();
            decryptText = pText;
        }

        return decryptText;
    }


}
