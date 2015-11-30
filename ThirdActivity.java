package actual.newactivity1;

import android.app.Activity;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
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
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;

public class ThirdActivity extends Activity {

    ImageView imageView;
    Intent intent = null, chooser = null;
    String structureId = "1";
    int full_status_flag = 0;
    //0 --> Available Count = 0 - (Parking Structure is full)
    //1 --> Available Count = 1 - (Parking Structure is almost full)


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.third_layout);

        Bundle getBundle = null;
        getBundle = this.getIntent().getExtras();
        structureId = getBundle.getString("PS_Id");

        ImageView imgs[] = {
                (ImageView)findViewById(R.id.imageView1),
                (ImageView)findViewById(R.id.imageView2),
                (ImageView)findViewById(R.id.imageView3),
                (ImageView)findViewById(R.id.imageView4),
                (ImageView)findViewById(R.id.imageView5),
                (ImageView)findViewById(R.id.imageView6),
                (ImageView)findViewById(R.id.imageView7),
                (ImageView)findViewById(R.id.imageView8),
                (ImageView)findViewById(R.id.imageView9),
                (ImageView)findViewById(R.id.imageView10),
                (ImageView)findViewById(R.id.imageView11),
                (ImageView)findViewById(R.id.imageView12),
                (ImageView)findViewById(R.id.imageView13),
                (ImageView)findViewById(R.id.imageView14),
                (ImageView)findViewById(R.id.imageView15),
                (ImageView)findViewById(R.id.imageView16)


    };
        int temp_availability[] = {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1};
        try {
            LongRunningGetIO task = new LongRunningGetIO();
            String data = task.execute().get();
            //String data = results;
            data = data.substring(3, data.length() - 3);
            String[] recievedData = data.split("\\|");
            String occupiedString = recievedData[1];

            String[] occupiedArray = occupiedString.split("|");
            //temp_availability = new int[occupiedArray.length];
            for (int i = 1; i < Math.min(16,occupiedArray.length); i++) {
                temp_availability[i-1] = Integer.parseInt(occupiedArray[i]);
            }
        }
        catch (Exception e){

        }




    String temp ;
        for(int i=0;i<temp_availability.length;i++){

            // Display the status of the parking spots

            if (temp_availability[i]==0){
                imageView = imgs[i];
                imageView.setImageResource(R.drawable.available);
            }
            if (temp_availability[i]==1) {
                imageView = imgs[i];
                imageView.setImageResource(R.drawable.parked);
            }

        }

    }

    public void locate_on_map(View view){

        //startService();

      /*  mServiceIntent = new Intent(getActivity(), RSSPullService.class);
        mServiceIntent.setData(Uri.parse(dataUrl));*/

        Bundle getBundle = null;
        getBundle = this.getIntent().getExtras();

        String temp_lat = getBundle.getString("PS_Latitude");
        String temp_lng = getBundle.getString("PS_Longitude");

        checkAvailability();

        intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("http://maps.google.com/maps?saddr=33.423801,-111.939515&daddr="+temp_lat+","+temp_lng));
        /*geo:33.4162208,-111.9403442));*/
        chooser = intent.createChooser(intent,"Launch Maps");
        startActivity(chooser);

    }

    TimerTask scanTask;
    final Handler handler = new Handler();
    Timer t = new Timer();

    public void checkAvailability(){

        scanTask = new TimerTask() {
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        try {
                            String data = new LongRunningGetIO().execute().get();
                            data = data.substring(3, data.length()-3);
                            String[] recievedData = data.split("\\|");
                            int availableCount = Integer.parseInt(recievedData[0]);
                            if(availableCount == 0 && full_status_flag !=0)
                            {
                                full_status_flag =0;
                                Toast.makeText(getApplicationContext(), "Parking Full !", Toast.LENGTH_LONG).show();
                            }
                            else if (availableCount == 1 && full_status_flag !=1)
                            {
                                full_status_flag =1;
                                Toast.makeText(getApplicationContext(), "Parking Almost Full !", Toast.LENGTH_LONG).show();
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }};


        t.schedule(scanTask, 300, 3000);

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_third, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // Method to broadcast the message
    private void sendMessage() {
        Log.d("sender", "Broadcasting message");
        Intent intent = new Intent("custom-event-name");
        // You can also include some extra data.
        intent.putExtra("message", "This is my message!");
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private void startService()
    {
        Intent msgIntent = new Intent(this, AvailabilityService.class);
        msgIntent.putExtra("Id", "1");
        startService(msgIntent);
    }

    // Method to

    // Method to get data from service
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

            String url = "http://52.33.111.221/paos/Service1.svc/GetAvailability?structureId="+structureId;

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
