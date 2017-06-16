package com.example.mareklaskowski.cbc_xml_summer_2017;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //here we use our AsyncTask class to do the xml download

        //instantiate a URL object used to point to the xml resource
        URL url = null;
        try{
            url = new URL("http://www.cbc.ca/cmlink/rss-topstories");
        }catch (Exception e){
            Log.d("AN EXCEPTION HAPPENED", e.getMessage());
        }

        //use our custom AsyncTask to download (and process) the xml resource available at the indicated URL
        //call a framework method that will eventually call doInBackground found below
        new DownloadAndParseXMLTask().execute(url);
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

    /*
    download and parse the xml file indicated by the url
     */
    public void downloadFile(URL url){

        //we need a try-catch block right away - almost all this stuff can throw exceptions
        try{
            //create a new http url connection
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            //create an InputStreamReader that we can use to read data from the http url connection
            InputStreamReader inputStreamReader = new InputStreamReader(httpURLConnection.getInputStream());
            //do something with the data
            //display the downloaded xml for debugging purposes
            //wrap the inputStreamReader in a BufferedReader
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            //create a string variable to store each line as we read it from bufferedReader
            String inputline = null;
            //here's a cool one liner to read a line and make sure it's not null (in case of EOF)
            while((inputline = bufferedReader.readLine()) != null){
                //display for debugging purposes
                Log.d("HEADLINES DOWNLOADED", inputline);
            }

        } catch (Exception e){
            Log.d("DOWNLOAD ERROR", e.getMessage());
        }
    }
}













