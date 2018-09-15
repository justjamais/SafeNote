package com.konsolyazilim.matlub.safenote;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

public class register extends AppCompatActivity {

    arkaplan ar = new arkaplan();
    EditText username;
    EditText password;
    EditText fakepassword;
    EditText passwordagain;
    Button bt;
    ProgressDialog pd;
    Button btlogin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        bt = (Button) findViewById(R.id.registerbutton);
        username = (EditText) findViewById(R.id.registerusername);
        password = (EditText) findViewById(R.id.registerpassword);
        passwordagain = (EditText) findViewById(R.id.registeragainpassword);
        btlogin = (Button) findViewById(R.id.gologinbutton);
        fakepassword = (EditText) findViewById(R.id.registerfakepassword);

        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                    pd = new ProgressDialog(register.this);
                    pd.setMessage("Lütfen Bekleyin...");
                    pd.show();
                    try {
                        ar.execute(getResources().getString(R.string.ip) + "/userekle.php?username=" + username.getText() + "&password=" + encrypt(password.getText().toString(),"***somekey***") + "&fakepassword=" + fakepassword.getText().toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    ar = new arkaplan();



            }
        });
        btlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(register.this, MainActivity.class));
            }
        });

    }



    class arkaplan extends AsyncTask<String, String, String> {


        protected String doInBackground(String... params){
            HttpURLConnection connection = null;
            BufferedReader br = null;



            try{

                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                InputStream is = connection.getInputStream();
                br = new BufferedReader(new InputStreamReader(is));
                String all = "";
                String satir = "";
                while((satir = br.readLine()) != null )
                    all += satir;

                Log.d("GULELLELELELELE", all+ "");
                if((all.length()>0)) {
                    Log.d("aSDASDASDs", "İçerdeyim");
                    temp.username = username.getText().toString();
                    temp.password = password.getText().toString();
                    url = new URL(getResources().getString(R.string.ip) + "/notgetir.php?username=" + temp.username + "&password=" + encrypt(password.getText().toString(),"***somekey***"));
                    connection = (HttpURLConnection) url.openConnection();
                    connection.connect();
                    is = connection.getInputStream();
                    br = new BufferedReader(new InputStreamReader(is));
                    all = "";
                    satir = "";
                    while ((satir = br.readLine()) != null) {
                        all += satir;
                    }

                    temp.notlarJSON = all;

                    SharedPreferences sharedPref = register.this.getPreferences(Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString("username", username.getText().toString());
                    editor.commit();
                    pd=null;
                    Intent i;
                    i = new Intent(register.this, notes.class);
                    startActivity(i);

                    finish();
                }
            } catch (Exception e) {
                e.printStackTrace();

            }
            return "";

        }

        @Override
        protected void onPostExecute(String s) {

        }


    }


    private String encrypt(String data, String password) throws Exception
    {
        SecretKeySpec key = generateKey(password);
        try {
            Cipher c = Cipher.getInstance("AES");
            c.init(Cipher.ENCRYPT_MODE,key);
            byte[] encVal = c.doFinal(data.getBytes());
            String encryptedValue = Base64.encodeToString(encVal, Base64.DEFAULT);
            return encryptedValue;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        }
        return  "";
    }

    private String decrypt(String data, String password) throws Exception
    {
        SecretKeySpec key = generateKey(password);
        try {
            Cipher c = Cipher.getInstance("AES");
            c.init(Cipher.DECRYPT_MODE,key);
            byte[] decodedVal =  Base64.decode(data,Base64.DEFAULT);
            byte[] decValue = c.doFinal(decodedVal);
            String last = new String(decValue);
            return  last;


        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        }
        return  "";
    }

    private SecretKeySpec generateKey(String password) throws  Exception{
        final MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] bytes = password.getBytes("UTF-8");
        digest.update(bytes, 0, bytes.length);
        byte[] key = digest.digest();
        SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");
        return secretKeySpec;

    }


}
