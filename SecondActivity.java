package actual.newactivity1;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
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
import java.util.ArrayList;
import java.util.List;

public class SecondActivity extends AppCompatActivity {
    Spinner spinner;                             // DROP DOWN LIST
    ArrayAdapter<ParkingStructure> adapter;         //TO POPULATE THE SPINNER
    String temp_lat;
    String temp_lng;
    String temp_struct_id = "1";

    String tmp_lat = "0.0";
    String tmp_lng = "0.0";
    ParkingStructure[] structureList = new ParkingStructure[3];
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.second_layout);
        spinner = (Spinner) findViewById(R.id.parking_spinner);

        Bundle getBundle = null;
        getBundle = this.getIntent().getExtras();
        String bundleData = getBundle.getString("StructureList");

        try {

            String data =bundleData.replaceAll("^\"+", "").replaceAll("\"+$", "");
            data = data.replace("\\","");
            Object json = new JSONTokener(data).nextValue();
            if (json instanceof JSONObject){
                //you have an object

                JSONObject obj = new JSONObject(data);
                String listString = obj.getString("StructureList");
                Object listObject = new JSONTokener(listString).nextValue();
                if (listObject instanceof JSONArray){
                    JSONArray listArray = new JSONArray(listString);
                    for(int i = 0; i< listArray.length(); i++)
                    {
                        JSONObject structureJson = new JSONObject(listArray.getString(i));
                        ParkingStructure parkingStructure = new ParkingStructure(Integer.parseInt(structureJson.getString("Id")),structureJson.getString("Name"));
                        parkingStructure.lat = structureJson.getString("Latitude");
                        parkingStructure.lng = structureJson.getString("Longitude");
                        parkingStructure.idString = structureJson.getString("Id");
                        structureList[i] = parkingStructure;//new ParkingStructure(Integer.parseInt(structureJson.getString("Id")),structureJson.getString("Name"));
                    }
                    //you have an array
                }

            }
        }
        catch (Exception e){

        }
        ArrayAdapter spinnerArrayAdapter = new ArrayAdapter(this,
                android.R.layout.simple_spinner_dropdown_item, structureList);


        //new LongRunningGetIO().execute();

        Toast.makeText(getBaseContext(), tmp_lat+tmp_lng, Toast.LENGTH_LONG).show();


        spinner.setAdapter(spinnerArrayAdapter );
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                temp_lat = structureList[position].lat;
                temp_lng = structureList[position].lng;
                temp_struct_id = structureList[position].idString;

               // Toast.makeText(getBaseContext(), parent.getItemAtPosition(position) + "selected", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_second, menu);
        return true;
    }

    public void calculateroute(View view) {

        /*String buttonText;
        buttonText = ((Button)view).getText().toString();
        if(buttonText.equals("Calculate Route"))*/
        {

            Bundle bundle = new Bundle();
            bundle.putString("PS_Latitude", String.valueOf(temp_lat));
            bundle.putString("PS_Longitude", String.valueOf(temp_lng));
            bundle.putString("PS_Id", String.valueOf(temp_struct_id));

            Intent intent = new Intent(this, ThirdActivity.class);
            intent.putExtras(bundle);
            startActivity(intent);
        }
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



}