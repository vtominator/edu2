package hu.vtominator.edu2.View;

import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;

import hu.vtominator.edu2.R;
import hu.vtominator.edu2.Model.Constants;
import hu.vtominator.edu2.Model.Event;
import hu.vtominator.edu2.Controller.EventAdapter;
import hu.vtominator.edu2.Model.SharedPrefManager;



public class ErdeklodesekActivity extends AppCompatActivity {

    private Context mContext = ErdeklodesekActivity.this;
    public static boolean sajatListaEsemenyei;

    private Toolbar felsoPanel;

    private RecyclerView mRecyclerView;
    private EventAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    public static ArrayList<Event> myEventsList = new ArrayList<>();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_erdeklodesek);

        felsoPanel = findViewById(R.id.felsoPanel);

        felsoPanelBeallitasai();
        esemenyekBetoltese();
    }

    private void felsoPanelBeallitasai() {
        setSupportActionBar(felsoPanel);

        getSupportActionBar().setTitle(getResources().getString(R.string.erdeklodesek));

        final Drawable backArrow = getResources().getDrawable(R.drawable.ic_vissza_nyil);
        felsoPanel.setTitleTextColor(getResources().getColor(R.color.black));
        backArrow.setColorFilter(getResources().getColor(R.color.black), PorterDuff.Mode.SRC_ATOP);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(backArrow);
        felsoPanel.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mContext, MainActivity.class));
                finish();
            }
        });
    }

    private void listaNezetbeToltese() {
        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(this);

        mAdapter = new EventAdapter(myEventsList);

        Collections.sort(myEventsList, Event.BY_PRIOR);

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener(new EventAdapter.OnItemClickListener() {
            @Override
            public void onEventClick(int position) {
                sajatListaEsemenyei = true;

                Intent intent = new Intent(mContext, ReszletesActivity.class);
                intent.putExtra("position", position);
                startActivity(intent);
            }

            @Override
            public void onPinnedClick(int position) {

            }

            @Override
            public void onDeleteClick(int position) {

            }
        });
    }

    private void esemenyekBetoltese(){
        ottLeszekEsemenyekBetoltese();
        erdekelEsemenyekBetoltese();
    }




    private void ottLeszekEsemenyekBetoltese() {
        myEventsList.clear();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Constants.URL_GETPARTICIPATES,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {

                        try {

                            JSONObject jsonObject = new JSONObject(response);

                            if (!jsonObject.getBoolean("error")) {

                                JSONArray events = jsonObject.getJSONArray("events");

                                for (int i = 0; i < events.length(); i++) {

                                    JSONObject eventObject = events.getJSONObject(i);

                                    int event_id = eventObject.getInt("event_id");
                                    String type = eventObject.getString("type");
                                    String main_category = eventObject.getString("main_category");
                                    String side_category = eventObject.getString("side_category");
                                    String eventname = eventObject.getString("eventname");
                                    String date = eventObject.getString("date");
                                    String time = eventObject.getString("time");
                                    String location = eventObject.getString("location");
                                    String short_description = eventObject.getString("short_description");
                                    String description = eventObject.getString("description");
                                    String picture = eventObject.getString("picture");
                                    int pinned = eventObject.getInt("pinned");

                                    String user_id = eventObject.getString("user_id");
                                    String my_id = SharedPrefManager.getInstance(mContext).getUserId();
                                    ArrayList<String> tempArray = new ArrayList<>();

                                    Event eventItem = new Event(event_id, type, main_category, side_category, eventname, date, time, location, short_description, description, picture, pinned);
                                    eventItem.setSeen(true);

                                    for (Event event : myEventsList) {
                                        tempArray.add(event.getEventname());
                                    }

                                    if (my_id.equals(user_id) && !tempArray.contains(eventname)){
                                        myEventsList.add(eventItem);
                                    }


                                }
                            } else {
                                Toast.makeText(mContext, jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                            }

                            listaNezetbeToltese(); // A lista beletöltése a viewba

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(mContext, error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        Volley.newRequestQueue(this).add(stringRequest);
    }
    private void erdekelEsemenyekBetoltese() {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Constants.URL_GETINTERESTS,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {

                        try {

                            JSONObject jsonObject = new JSONObject(response);

                            if (!jsonObject.getBoolean("error")) {

                                JSONArray events = jsonObject.getJSONArray("events");

                                for (int i = 0; i < events.length(); i++) {

                                    JSONObject eventObject = events.getJSONObject(i);

                                    int event_id = eventObject.getInt("event_id");
                                    String type = eventObject.getString("type");
                                    String main_category = eventObject.getString("main_category");
                                    String side_category = eventObject.getString("side_category");
                                    String eventname = eventObject.getString("eventname");
                                    String date = eventObject.getString("date");
                                    String time = eventObject.getString("time");
                                    String location = eventObject.getString("location");
                                    String short_description = eventObject.getString("short_description");
                                    String description = eventObject.getString("description");
                                    String picture = eventObject.getString("picture");
                                    int pinned = eventObject.getInt("pinned");

                                    String user_id = eventObject.getString("user_id");
                                    String my_id = SharedPrefManager.getInstance(mContext).getUserId();
                                    ArrayList<String> tempArray = new ArrayList<>();

                                    Event eventItem = new Event(event_id, type, main_category, side_category, eventname, date, time, location, short_description, description, picture, pinned);
                                    eventItem.setSeen(true);

                                    for (Event event : myEventsList) {
                                        tempArray.add(event.getEventname());
                                    }

                                    if (my_id.equals(user_id) && !tempArray.contains(eventname)){
                                        myEventsList.add(eventItem);
                                    }



                                }
                            } else {
                                Toast.makeText(mContext, jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                            }

                            listaNezetbeToltese(); // A lista beletöltése a viewba

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(mContext, error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        Volley.newRequestQueue(this).add(stringRequest);
    }


}
