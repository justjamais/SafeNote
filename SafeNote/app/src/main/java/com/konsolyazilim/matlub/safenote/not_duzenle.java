package com.konsolyazilim.matlub.safenote;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Iterator;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import javax.net.ssl.HttpsURLConnection;

public class not_duzenle extends AppCompatActivity {


    EditText et = null;
    String cevap="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_not_duzenle);
        et = (EditText) findViewById(R.id.notduzenleedittext);
        et.setText(temp.notlar.get(temp.suanokunan).getText());

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);





    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.sil) {

            arkaplan ar = new arkaplan();
            ar.execute(getResources().getString(R.string.ip) + "/notsil.php?username=" + temp.username + "&password=" + encrypt(temp.password.getText().toString(),"***somekey***") +  "&id="  + temp.notlar.get(temp.suanokunan).getId());
            temp.notlar.remove(temp.suanokunan);
            Toast.makeText(this, "Not silindi.", Toast.LENGTH_SHORT).show();
            finish();

        }
        if (id == R.id.kaydet) {
            new SendPostRequest().execute();
            Toast.makeText(this, "Kaydedildi.", Toast.LENGTH_SHORT).show();
        }
        if (id == R.id.kaydetme) {
            Toast.makeText(this, "Değişiklikler silindi.", Toast.LENGTH_SHORT).show();
            finish();
            }


        return super.onOptionsItemSelected(item);
    }



    class arkaplan extends  AsyncTask<String, String, String>{
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
                    finish();
                }
            } catch (Exception e) {
                e.printStackTrace();

            }
            return "";

        }

        @Override
        protected void onPostExecute(String s) {
            Log.d("GULLULULULU", s);
        }


    }


    public class SendPostRequest extends AsyncTask<String, Void, String> {

        protected void onPreExecute(){}

        protected String doInBackground(String... arg0) {
            temp.notlar.get(temp.suanokunan).setText(et.getText().toString());
            try {

                URL url = new URL(getResources().getString(R.string.ip) + "/notduzenle.php"); // here is your URL path

                JSONObject postDataParams = new JSONObject();
                postDataParams.put("username", temp.username);
                postDataParams.put("password", temp.password);
                postDataParams.put("id", temp.notlar.get(temp.suanokunan).getId());
                postDataParams.put("text", encrypt(et.getText().toString(), "***somekey***"));
                Log.e("params",postDataParams.toString());

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(15000 /* milliseconds */);
                conn.setConnectTimeout(15000 /* milliseconds */);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);

                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(getPostDataString(postDataParams));

                writer.flush();
                writer.close();
                os.close();

                int responseCode=conn.getResponseCode();

                if (responseCode == HttpURLConnection.HTTP_OK) {

                    BufferedReader in=new BufferedReader(new
                            InputStreamReader(
                            conn.getInputStream()));

                    StringBuffer sb = new StringBuffer("");
                    String line="";

                    while((line = in.readLine()) != null) {

                        sb.append(line);
                        break;
                    }

                    in.close();
                    cevap = sb.toString();
                    temp.notlar.get(temp.suanokunan).setText(et.getText().toString());
                    finish();
                    return sb.toString();

                }
                else {
                    temp.notlar.get(temp.suanokunan).setText(et.getText().toString());
                    return new String("false : "+responseCode);
                }
            }
            catch(Exception e){
                temp.notlar.get(temp.suanokunan).setText(et.getText().toString());
                return new String("Exception: " + e.getMessage());

            }

        }

        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(getApplicationContext(), result,
                    Toast.LENGTH_LONG).show();
        }
    }

    public String getPostDataString(JSONObject params) throws Exception {

        StringBuilder result = new StringBuilder();
        boolean first = true;

        Iterator<String> itr = params.keys();

        while(itr.hasNext()){

            String key= itr.next();
            Object value = params.get(key);

            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(key, "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(value.toString(), "UTF-8"));

        }
        return result.toString();
    }



    private String encrypt(String data, String password) throws Exception
    {
        SecretKeySpec key = generateKey(temp.password+ password);
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
        SecretKeySpec key = generateKey(temp.password + password);
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



