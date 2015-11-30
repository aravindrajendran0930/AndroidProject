package actual.newactivity1;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.io.InputStream;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;

/**
 * Created by Aravind on 11/17/2015.
 */

    public class AvailabilityService extends IntentService {

    public AvailabilityService(String name) {
        super(name);
    }

    @Override
        protected void onHandleIntent(Intent workIntent) {
            // Gets data from the incoming Intent
            String dataString = workIntent.getDataString();
            Timer timer = new Timer();
            final int FPS = 40;
            TimerTask task = new GetAvailabilityTask();
            timer.scheduleAtFixedRate(task, 0, 1000/FPS);

            Toast.makeText(getBaseContext(), dataString, Toast.LENGTH_LONG).show();
        }
    }

class GetAvailabilityTask extends TimerTask {
    public String data;
    public int availability;

    public void run() {
        try {
            data = new LongRunningGetIO().execute().get();
            data = data.substring(3, data.length() - 3);
            String[] recievedData = data.split("\\|");
            availability = Integer.parseInt(recievedData[0]);
            if(availability == 0){
                //Toast.makeText(getBaseContext(), "No Parking Spots Available", Toast.LENGTH_LONG).show();
                //Intent intent = new Intent(this,ThirdActivity.class);
                //intent.putExtras(bundle);
                //startActivity(intent);

            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }



    private class LongRunningGetIO extends AsyncTask<Void, Void, String> {
        protected String getASCIIContentFromEntity(HttpEntity entity) throws IllegalStateException, IOException {
            InputStream in = entity.getContent();
            StringBuffer out = new StringBuffer();
            int n = 1;
            while (n>0) {
                byte[] b = new byte[4096];
                n =  in.read(b);
                if (n>0) out.append(new String(b, 0, n));
            }
            return out.toString();
        }

        @Override
        protected String doInBackground(Void... params) {
            HttpClient httpClient = new DefaultHttpClient();
            HttpContext localContext = new BasicHttpContext();

            String url = "http://52.33.111.221/paos/Service1.svc/GetAvailability?structureId=1";

            HttpGet httpGet = new HttpGet(url);
            String text = null;
            try {
                HttpResponse response = httpClient.execute(httpGet, localContext);
                HttpEntity entity = response.getEntity();
                text = getASCIIContentFromEntity(entity);
            } catch (Exception e) {
                return e.getLocalizedMessage();
            }
            return text;
        }

        protected void onPostExecute(String results) {
            if (results!=null) {

                try {

                    String data = results;
                    Object json = new JSONTokener(data.replace("\\", "")).nextValue();
                    if (json instanceof JSONObject){
                        //you have an object
                        JSONObject obj = new JSONObject(results);
                        String s = obj.getString("name");
                    }

                    else if (json instanceof JSONArray){
                        JSONArray obj = new JSONArray(results);
                        String query = obj.getString(0);
                        //you have an array
                    }

                    JSONObject obj = new JSONObject(results);
                    String s = obj.getString("name");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }
    }
}

