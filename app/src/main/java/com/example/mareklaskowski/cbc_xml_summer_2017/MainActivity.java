package com.example.mareklaskowski.cbc_xml_summer_2017;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.net.URL;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
    
    private class DownloadAndParseXMLTask extends AsyncTask<URL, Integer, Long> {

        //FYI - doInBackground is a "slot" method where we have to fill in its behaviour to compile
        /*the framework requires us to be able to accept more than one "task",
        so this method has to be able to accept multiple arguments (variadic)
         in this way it's more reusable in different application versions
         */
        @Override
        protected Long doInBackground(URL... urls) {
            long count = urls.length;//this tells us how many urls we have to process. in this app it will always be one URL passed
            long totalSize = 0; // count how many files we have actually downloaded

            //loop to download files.
            for (int i = 0; i < count; i++) {
                //delegate the actual file download to a method in MainActivity (so we can access all the functionality of an Android Context)
                downloadFile(urls[i]);
                totalSize++;
            }

            return totalSize;//tell how many urls will downloaded
        }

        //FYI the methods below are "hook" methods because they already have default implementations that we can override
    }

    public void downloadFile(URL url){

    }
}
