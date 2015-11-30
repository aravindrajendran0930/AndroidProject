package actual.newactivity1;

import android.content.Intent;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    Double dest_lattitude=0.0, dest_longitude=0.0;
    EditText address,city,state;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        address =(EditText)findViewById(R.id.address1);
        city =(EditText)findViewById(R.id.city1);
        address.setText("1216, E.Vista Del Cerro Drive");
        city.setText("Tempe");
        state =(EditText)findViewById(R.id.state1);
        state.setText("AZ 85281");


    }
    public void locate_spots(View view) throws JSONException, ExecutionException, InterruptedException {

        Double dest_lattitude, dest_longitude;
        // TODO : Get from geo Coding
        Double s[] = getLocationFromAddress(address.getText()+", "+city.getText()+", "+state.getText());
        dest_lattitude = s[0];
        dest_longitude = s[1];

            LongRunningGetIO task =new LongRunningGetIO();
            String data = task.execute().get();

        //Toast.makeText(getBaseContext(),data, Toast.LENGTH_LONG).show();

            Bundle bundle = new Bundle();

            // Passing on the coordinates the second activity
            bundle.putString("dest_lattitude", String.valueOf(dest_lattitude));
            bundle.putString("dest_longitude", String.valueOf(dest_longitude));
            bundle.putString("StructureList", String.valueOf(data));

            // Start the second activity
            Intent intent = new Intent(this,SecondActivity.class);
            intent.putExtras(bundle);
            startActivity(intent);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


        // Method to get the coordinates based on the Address
    public Double[] getLocationFromAddress(String strAddress) throws JSONException {

        Geocoder coder = new Geocoder(this);
        List<Address> address;
        Double p1[] = new Double[2];

        //JSONObject jsonObj = parser_Json.getJSONfromURL("http://52.33.111.221/paos/Service1.svc/GetNearestParkingStructures?destinationLatitude=0&destinationLongitude=0&count=2");
        //Toast.makeText(getBaseContext(), jsonObj.getString("Name"), Toast.LENGTH_LONG).show();
        try {
            address = coder.getFromLocationName(strAddress, 1);
            if (address == null) {
                return null;
            }
            Address location = address.get(0);
            p1[0] = (double)location.getLatitude();
            p1[1] = (double)location.getLongitude();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return p1;
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

            String url = "http://52.33.111.221/paos/Service1.svc/GetNearestParkingStructures?destinationlatitude="+dest_lattitude.toString()+"&destinationLongitude="+dest_longitude.toString()+"&count=3";
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
