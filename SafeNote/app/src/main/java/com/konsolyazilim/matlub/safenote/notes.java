package com.konsolyazilim.matlub.safenote;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

public class notes extends AppCompatActivity {


    public ListView lv = null;
    public ArrayAdapter<note> adaptor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        lv= (ListView) findViewById(R.id.notlarListView);
        try {
            JSONObject jo = new JSONObject(temp.notlarJSON);
            JSONArray ja = jo.getJSONArray("note");
            for(int i=0; i<ja.length();i++)
            {
                JSONObject joo = ja.getJSONObject(i);
                String s = decrypt(joo.getString("text"),"***somekey***");
                note n = new note(s, joo.getInt("id"));
                temp.notlar.add(n);
                Log.d("yeninottonotlararray", n.getText());
            }

        }
        catch (Exception e)
        {

        }


        adaptor = new ArrayAdapter<note>(this, android.R.layout.simple_list_item_1, temp.notlar);
        lv.setAdapter(adaptor);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,int position, long arg3)
            {
                temp.suanokunan = position;
                startActivity(new Intent(notes.this, not_duzenle.class));
                }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_notes, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.add) {
            arkaplan ar = new arkaplan();

            ar.execute(getResources().getString(R.string.ip) + "/notekle.php?username=" + temp.username + "&password=" + encrypt(temp.password.getText().toString(),"***somekey***") + "&text=");
            ar = new arkaplan();
            Toast.makeText(this, "Not Eklendi.", Toast.LENGTH_SHORT).show();



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
                    int result = Integer.parseInt(all);
                    note n = new note("",result);
                    temp.notlar.add(n);
                    temp.suanokunan=temp.notlar.size()-1;
                    Intent i;
                    i = new Intent(notes.this, not_duzenle.class);
                    startActivity(i);

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

    private String encrypt(String data, String password) throws Exception
    {
        SecretKeySpec key = generateKey(temp.password + password);
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


    @Override
    public void onBackPressed() {
        temp.notlar= new ArrayList<note>();
        temp.notlarJSON ="";
        finish();
        super.onBackPressed();
    }

    @Override
    protected void onResume() {
        adaptor = new ArrayAdapter<note>(this, android.R.layout.simple_list_item_1, temp.notlar);
        lv.setAdapter(adaptor);

        super.onResume();
    }

    @Override
    protected void onRestart() {

        super.onRestart();
    }

}
