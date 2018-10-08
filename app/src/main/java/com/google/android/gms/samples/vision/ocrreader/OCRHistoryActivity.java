package com.google.android.gms.samples.vision.ocrreader;

import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.samples.vision.ocrreader.database.DBHelper;

import java.util.ArrayList;

public class OCRHistoryActivity extends AppCompatActivity {

    private ListView listView ;
    private TextView noHistoryText;
    private DBHelper mydb;
    private String text;
    private ArrayList<String> texts = new ArrayList<String>();
    private ArrayAdapter<String> adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ocrhistory);
        mydb = new DBHelper(OCRHistoryActivity.this);
        texts = mydb.getAllTransaction();
//        for (int i = 0 ; i < texts.size(); i++ ){
//            text = texts.get(i);
//            if(text.length() > 30)
//                texts.set(i,text.substring(0, Math.min(text.length(), 30))+".....");
//        }
        // ca-app-pub-1243068719441957/5059413582
        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        if(texts.size() >0) {
            listView = (ListView) findViewById(R.id.list);
            adapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_list_item_1, android.R.id.text1, texts);
            listView.setAdapter(adapter);
            Snackbar.make(listView, "Tap to copy and more options",
                    Snackbar.LENGTH_LONG)
                    .show();
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                    startActivity(new Intent(OCRHistoryActivity.this, DetailTextActivity.class).putExtra("text",(String) listView.getItemAtPosition(position)));
                }

            });
        }else {
            noHistoryText = (TextView) findViewById(R.id.noHistoryText);
            noHistoryText.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_clear:
                if(texts.size() > 0) {
                    mydb.delateAllTransaction();
                    texts = mydb.getAllTransaction();
                    adapter = new ArrayAdapter<String>(this,
                            android.R.layout.simple_list_item_1, android.R.id.text1, texts);
                    listView.setAdapter(adapter);
                    Snackbar.make(listView, "History cleared!!!",
                            Snackbar.LENGTH_LONG)
                            .show();
                }else
                    Snackbar.make(listView, "Nothing available to clear!",
                            Snackbar.LENGTH_LONG)
                            .show();
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_history, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu (Menu menu) {
        texts = mydb.getAllTransaction();
        if(texts.size() == 0) {
            menu.findItem(R.id.action_clear).setEnabled(false);
        }
        return true;
    }
}
