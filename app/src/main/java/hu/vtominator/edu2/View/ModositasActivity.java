package hu.vtominator.edu2.View;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import hu.vtominator.edu2.R;
import hu.vtominator.edu2.Controller.BottomNavigationViewHelper;
import hu.vtominator.edu2.Model.Constants;
import hu.vtominator.edu2.Model.Event;
import hu.vtominator.edu2.Controller.EventAdapter;

public class ModositasActivity extends AppCompatActivity {
    private Context mContext = ModositasActivity.this;
    private Toolbar felsoPanel;

    private RecyclerView mRecyclerView;
    private EventAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    public static boolean modositasNezet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_modositas);

        felsoPanel = findViewById(R.id.felsoPanel);

        felsoPanelBeallitasai();
        keresoMenu();
        alsoNavigaciosMenusor();
        loadEvents();


    }


    private void felsoPanelBeallitasai() {
        setSupportActionBar(felsoPanel);

        getSupportActionBar().setTitle("Módosítás");

        final Drawable backArrow = getResources().getDrawable(R.drawable.ic_vissza_nyil);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(backArrow);
        felsoPanel.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mContext, BeallitasokActivity.class));
                finish();
            }
        });
    }
    private void keresoMenu() {
        Toolbar searchBar = findViewById(R.id.keresoMenu);
        setSupportActionBar(searchBar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.searchbar, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);
        searchView.setIconifiedByDefault(false);

        searchView.setQueryHint("Keresés...");
        searchView.setElevation(10.0f);
        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.setBackgroundColor(getResources().getColor(R.color.white));
        searchView.setPadding(-16, 0, 0, 0);


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                mAdapter.getFilter().filter(s);
                return false;
            }
        });
        return true;
    }

    private void listToView() {
        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(this);

        mAdapter = new EventAdapter(EsemenyekListazasa.eventList);

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);


        mAdapter.setOnItemClickListener(new EventAdapter.OnItemClickListener() {
            @Override
            public void onEventClick(int position) {
                Bundle mBundle = new Bundle();

                mBundle.putInt("position", position);

                Intent mIntent = new Intent(mContext, EsemenyModositasActivity.class);
                mIntent.putExtras(mBundle);

                startActivity(mIntent);
                finish();
            }

            @Override
            public void onPinnedClick(int position) {
            }

            @Override
            public void onDeleteClick(int position) {
            }
        });
    }
    private void loadEvents() {
        EsemenyekListazasa.eventList.clear();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Constants.URL_GETALLEVENTS,
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


                                    Event eventItem = new Event(event_id, type, main_category, side_category, eventname,  date, time, location, short_description, description, picture, pinned);
                                    eventItem.setSeen(true);
                                    EsemenyekListazasa.eventList.add(eventItem);


                                }
                            } else {
                                Toast.makeText(mContext, jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                            }

                            listToView(); // A lista beletöltése a viewba

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


    private void alsoNavigaciosMenusor() {
        BottomNavigationViewEx bottomNavigationViewEx = (BottomNavigationViewEx) findViewById(R.id.alsoMenusor);

        BottomNavigationViewHelper.setupBottomNavigationView(bottomNavigationViewEx);

        BottomNavigationViewHelper.enableNavigation(mContext, bottomNavigationViewEx);

        Menu menu = bottomNavigationViewEx.getMenu();
        MenuItem menuItem = menu.getItem(0);
        menuItem.setChecked(true);
    }
}
