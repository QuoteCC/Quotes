package com.ccorliss.quotes;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import android.view.View.OnClickListener;


public class MainActivity extends AppCompatActivity implements OnClickListener {

    Button bttn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bttn = (Button) findViewById(R.id.GetNew);

        bttn.setOnClickListener(this);
    }

    public void onClick(View view){
        switch(view.getId()){
            case R.id.GetNew:
                new GetQuote().execute();
                break;
        }

    }

    class GetQuote extends AsyncTask<Void, Void, String> {

        String API_URLWIKI =  "https://en.wikiquote.org/w/api.php?";
        String API_URLREDDIT = "https://www.reddit.com/r/quotes/hot.json?";
        //String[] authors = {"Barack_Obama", "Bill_Nye"};

        protected void onPreExecute(){
            ProgressBar pB = (ProgressBar)findViewById(R.id.loading);
            pB.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(Void... urls) {

            JSONObject returned = redditQuotes();
            try{
                JSONArray children =  returned.getJSONObject("data").getJSONArray("children");
                String title = children.getJSONObject(0).getJSONObject("data").get("title").toString();
                return title;
            }
            catch (Exception e){
                return e.toString();
            }


        }

        JSONObject redditQuotes(){
        /*

        Use the reddit api to get the newest quote from the /r/quotes subreddit

        sort=new&limit=1&prop=title

        */

            try{
                URL toQuery = new URL(API_URLREDDIT + "sort=new"+"&limit=1" +"&prop=title" );
                HttpURLConnection urlConnection = (HttpURLConnection) toQuery.openConnection();
                try{
                    BufferedReader buffRead = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while((line = buffRead.readLine()) != null){
                        response.append(line).append("\n");
                    }
                    buffRead.close();
                    return new JSONObject(response.toString());

                }
                finally{
                    urlConnection.disconnect();
                }

            }
            catch(Exception e){
                e.printStackTrace();
            }


            return null;
        }

        JSONObject queryTitles(String author){
        /*
        Use a given author name to get a list of pageIds associated with them
        format json
        action query
        redirects ""
        titles author
         */
            try {
                URL toQuery = new URL(API_URLWIKI + "format:json"+"&action=query" + "&redirects=\"\""+"&titles="+author);
                HttpURLConnection urlConnection =  (HttpURLConnection) toQuery.openConnection();
                try{
                    BufferedReader buffRead = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = buffRead.readLine()) != null){
                        response.append(line).append("\n");
                    }
                    buffRead.close();
                    return new JSONObject(response.toString());

                }
                finally{
                    urlConnection.disconnect();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        public void onPostExecute(String response){
            boolean real = true;
            if(response == null){
                response = "THERE WAS AN ERROR";
                real = false;
            }
            ProgressBar pB = (ProgressBar)findViewById(R.id.loading);
            pB.setVisibility(View.GONE);
            TextView body = (TextView)findViewById(R.id.Quote);
            TextView author = (TextView)findViewById(R.id.Author);
            body.setVisibility(View.VISIBLE);
            author.setVisibility(View.VISIBLE);
            if (real){
                String[] splt = response.split("\"");
                if(splt.length == 3) {
                    body.setText(splt[1]);
                    author.setText(splt[2]);
                }
                else{
                    body.setText(response);
                    author.setText("");
                }


            }



        }
    }
}
