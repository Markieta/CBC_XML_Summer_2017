package com.example.mareklaskowski.cbc_xml_summer_2017;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Vector;

public class MainActivity extends AppCompatActivity {

    //for now, let's declare a Vector of strings to hold headlines
    //think about how you would modify this idea to accomplish the lab task
    Vector<String> headlines = new Vector<String>();

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

        //onPostExecute will be called by the framework when our AsyncTask finished
        protected void onPostExecute(Long result){
            //once finished all we do here is print out the headlines
            //you will extend this idea to refresh any views (if necessary)
            Log.d("ASYNCTASK COMPLETE", "Downloaded " + result + "files");
            Log.d("ASYNCTASK COMPLETE", "Printing " + headlines.size() + "headlines");
            for(String headline : headlines){
                Log.d("ASYNCTASK COMPLETE", "Headline: " + headline);

            }


        }
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
            /*
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
            */

            //now that we have the downloaded file, use the XMLPullParser API to parse the xml and extract the information we need
            //get an instance of XmlPullParser from XmlPullParserFactory (recall: factory pattern)
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            //configure the factory to create the specific xml parser we will use
            factory.setNamespaceAware(true);
            //get the XmlPullParser instance from the factory
            XmlPullParser xmlPullParser = factory.newPullParser();
            //tell the xmlpullparser where to get its data from
            //note: we can pass a variety of IO streaming type classes here
            xmlPullParser.setInput(inputStreamReader);
            //recall: XmlPullParser communicates with your code using "Events"
            int event = xmlPullParser.getEventType();

            //pattern: use a flag to remember that we are inside a title element
            boolean insideTitle = false;

            while(event != XmlPullParser.END_DOCUMENT){
                //process events inside this loop
                if(event == XmlPullParser.START_DOCUMENT){ //this will be executed at the start of the document
                    Log.d("PARSING XML", "We reached the start of the document");
                }else if(event == XmlPullParser.START_TAG){ //this will be executed at the start of EACH tag (element)
                    String tagName = xmlPullParser.getName();
                    Log.d("PARSING XML", "We reached the start of tag: " + tagName);
                    //TODO: you can use the name here to determine whether you've reached a news "item" and/or "link" in the xml
                    if(tagName.equalsIgnoreCase("title")){
                        //store this title in the headlines
                        Log.d("PARSING XML", "found a title tag: " + tagName);
                        insideTitle = true;
                    }
                }else if(event == XmlPullParser.END_TAG){ //executed at the end of each tag (element)
                    Log.d("PARSING XML", "We reached the end of tag: " + xmlPullParser.getName());
                    insideTitle = false;
                }else if(event == XmlPullParser.TEXT){
                    String text = xmlPullParser.getText();
                    Log.d("PARSING XML", "found text: " + text);
                    if(insideTitle){
                        headlines.add(text);
                    }
                }
                //don't forget to get the next event in the xml file
                event = xmlPullParser.next();
            }
            Log.d("PARSING XML", "reached the end of the file");


        } catch (Exception e){
            Log.d("DOWNLOAD ERROR", e.getMessage());
        }
    }
}













