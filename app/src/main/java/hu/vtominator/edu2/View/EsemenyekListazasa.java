package hu.vtominator.edu2.View;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.webkit.MimeTypeMap;
import android.webkit.URLUtil;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import hu.vtominator.edu2.R;
import hu.vtominator.edu2.Controller.BottomNavigationViewHelper;
import hu.vtominator.edu2.Model.Constants;
import hu.vtominator.edu2.Model.Event;
import hu.vtominator.edu2.Controller.EventAdapter;
import hu.vtominator.edu2.Model.SharedPrefManager;

public class EsemenyekListazasa extends AppCompatActivity {
    private Context mContext = EsemenyekListazasa.this;
    public static String KATEGORIA_NEVE;
    public static int KATEGORIA_SZINE;

    private Toolbar felsoPanel;

    private RecyclerView mRecyclerView;
    private EventAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private TextView tvNo, tvYes;

    public static ArrayList<Event> eventList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_esemenyek_listazasa);

        felsoPanel = findViewById(R.id.felsoPanel);

        felsoPanelBeallitasai();
        keresoMenu();
        alsoNavigaciosMenusor();
        esemenyekBetoltese();
    }

    @Override
    public void onRestart() {
        super.onRestart();
        mAdapter.notifyDataSetChanged();
    }


    private void felsoPanelBeallitasai() {
        setSupportActionBar(felsoPanel);

        getSupportActionBar().setTitle(KATEGORIA_NEVE);

        final Drawable backArrow = getResources().getDrawable(R.drawable.ic_vissza_nyil);
        felsoPanel.setTitleTextColor(KATEGORIA_SZINE);
        backArrow.setColorFilter(KATEGORIA_SZINE, PorterDuff.Mode.SRC_ATOP);


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

    private void listaNezetbeToltese() {
        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(this);

        mAdapter = new EventAdapter(eventList);

        mAdapter.getFilter().filter(KATEGORIA_NEVE);

        Collections.sort(eventList, Event.BY_PRIOR);

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener(new EventAdapter.OnItemClickListener() {
            @Override
            public void onEventClick(int position) {
                final Event currentEvent = eventList.get(position);


                if (!SharedPrefManager.getInstance(mContext).getUsername().equals("Vendég")) {
                    addSeen(currentEvent);
                }

                if (currentEvent.getType().equals("Kérdőív")) {
                    Bundle mBundle = new Bundle();

                    mBundle.putInt("position", position);

                    Intent mIntent = new Intent(mContext, KerdoivActivity.class);
                    mIntent.putExtras(mBundle);

                    startActivity(mIntent);
                } else if (currentEvent.getType().equals("Jelentkezés")) {

                    Uri uri = null;
                    switch (currentEvent.getDescription()) {
                        case "versmondás":
                            uri = Uri.parse("https://www.edugyula.com/app/download/7400799611/versmondas_IRD1.pdf?t=1550137330");
                            break;
                        case "prózamondás":
                            uri = Uri.parse("https://www.edugyula.com/app/download/7400807511/prozamondas_IRD2.pdf?t=1550137330");
                            break;
                        case "irodalmi színpad":
                            uri = Uri.parse("https://www.edugyula.com/app/download/7400807611/irodalmi_szinpad_IRD3.pdf?t=1550137330");
                            break;
                        case "önálló irodalmi alkotás":
                            uri = Uri.parse("https://www.edugyula.com/app/download/7400808111/onallo_irodalmi_alkotas_IRD4.pdf?t=1550137330");
                            break;
                        case "slam poetry":
                            uri = Uri.parse("https://www.edugyula.com/app/download/7400813711/slam_poetry_IRD5.pdf?t=1550137330");
                            break;

                        case "szólóének":
                            uri = Uri.parse("https://www.edugyula.com/app/download/7400816911/szoloenek_Z1.pdf?t=1550137330");
                            break;
                        case "énekkar - kamarakórus":
                            uri = Uri.parse("https://www.edugyula.com/app/download/7400816611/enekkar_kamarakorus_Z2.pdf?t=1550137330");
                            break;
                        case "népdal - hangszeres népzene":
                            uri = Uri.parse("https://www.edugyula.com/app/download/7400817011/nepdal_hangszeres_nepzene_Z3.pdf?t=1550137330");
                            break;
                        case "szólóhangszer - kamaraegyüttes":
                            uri = Uri.parse("https://www.edugyula.com/app/download/7400817211/szolohangszer_kamaraegyuttes_Z4.pdf?t=1550137330");
                            break;
                        case "könnyűzenei együttes":
                            uri = Uri.parse("https://www.edugyula.com/app/download/7400817311/konnyuzenei_egyuttes_Z5.pdf?t=1550137330");
                            break;

                        case "néptánc":
                            uri = Uri.parse("https://www.edugyula.com/app/download/7400861711/neptanc_T1.pdf?t=1550137330");
                            break;
                        case "tánc és mozgás":
                            uri = Uri.parse("https://www.edugyula.com/app/download/7400862011/tanc_es_mozgas_T2.pdf?t=1550137330");
                            break;

                        case "képző- és iparművészet":
                            uri = Uri.parse("https://www.edugyula.com/app/download/7400862211/kepzomuveszet_L1.pdf?t=1550137330");
                            break;
                        case "film":
                            uri = Uri.parse("https://www.edugyula.com/app/download/7400862511/film_L3.pdf?t=1550137330");
                            break;
                        case "fotó":
                            uri = Uri.parse("https://www.edugyula.com/app/download/7400862711/foto_L2.pdf?t=1550137330");
                            break;
                    }
                    fajlLetoltese(uri);


                } else {

                    Bundle mBundle = new Bundle();

                    mBundle.putInt("position", position);

                    Intent mIntent = new Intent(mContext, ReszletesActivity.class);
                    mIntent.putExtras(mBundle);

                    startActivity(mIntent);
                }

            }

            @Override
            public void onPinnedClick(int position) {
                final Event currentEvent = eventList.get(position);
                esemenyKiemelese(currentEvent);

                finish();
                overridePendingTransition(0, 0);
                startActivity(getIntent());
                overridePendingTransition(0, 0);

            }

            @Override
            public void onDeleteClick(int position) {
                final Event currentEvent = eventList.get(position);
                felugroAblak(currentEvent);
            }
        });
    }

    private void esemenyekBetoltese() {
        eventList.clear();
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


                                    Event eventItem = new Event(event_id, type, main_category, side_category, eventname, date, time, location, short_description, description, picture, pinned);
                                    getSeen(eventItem);

                                    eventList.add(eventItem);


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

    private void esemenyKiemelese(final Event currentEvent) {
        final int event_id = currentEvent.getEvent_id();
        final String main_category = currentEvent.getMain_category();
        final String side_category = currentEvent.getSide_category();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.URL_SETPINNED, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    Toast.makeText(mContext, jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                    if (!jsonObject.getBoolean("error")) {


                        mAdapter.notifyDataSetChanged();


                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(mContext, error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }) {
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("event_id", Integer.toString(event_id));
                params.put("main_category", main_category);
                params.put("side_category", side_category);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(mContext);
        requestQueue.add(stringRequest);
    }

    private void esemenyTorlese(Event currentEvent, PopupWindow popupWindow) {

        popupWindow.dismiss();

        final int event_id = currentEvent.getEvent_id();

        final Event tempEvent = currentEvent;

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.URL_DELETEEVENT, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);

                    if (!jsonObject.getBoolean("error")) {

                        eventList.remove(tempEvent);
                        Intent refresh = new Intent(mContext, EsemenyekListazasa.class);
                        refresh.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        mContext.startActivity(refresh);
                        finish();

                    } else {
                        Toast.makeText(mContext, jsonObject.getString("error"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(mContext, error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }) {
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("event_id", Integer.toString(event_id));
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(mContext);
        requestQueue.add(stringRequest);
    }

    private void felugroAblak(final Event currentEvent) {

        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.delete_popup_window, null);

        tvNo = (TextView) popupView.findViewById(R.id.tvNo);
        tvYes = (TextView) popupView.findViewById(R.id.tvYes);

        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        boolean focusable = true;
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

        popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);

        tvNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });

        tvYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                esemenyTorlese(currentEvent, popupWindow);
            }
        });
    }

    public void fajlLetoltese(Uri uri) {

        DownloadManager mManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(uri);
        String nameOfFile = URLUtil.guessFileName(String.valueOf(uri), null, MimeTypeMap.getFileExtensionFromUrl(String.valueOf(uri)));
        request.setDescription(nameOfFile);
        request.setTitle(nameOfFile);

        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        mManager.enqueue(request);

    }

    private void addSeen(final Event currentEvent) {

        final String user_id = SharedPrefManager.getInstance(mContext).getUserId();
        final int event_id = currentEvent.getEvent_id();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.URL_SETSEEN, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);


                    if (!jsonObject.getBoolean("error")) {

                        currentEvent.setSeen(true);

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(mContext, error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }) {
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", user_id);
                params.put("event_id", Integer.toString(event_id));
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(mContext);
        requestQueue.add(stringRequest);
    }

    private void getSeen(final Event currentEvent) {


        final String currentUserId = SharedPrefManager.getInstance(mContext).getUserId();
        final int currentEventId = currentEvent.getEvent_id();
        final GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(mContext);


        StringRequest stringRequest = new StringRequest(Request.Method.GET, Constants.URL_GETSEEN, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject object = new JSONObject(response);

                    if (!object.getBoolean("error")) {
                        JSONArray favorites = object.getJSONArray("result");

                        for (int i = 0; i < favorites.length(); i++) {
                            JSONObject favObj = favorites.getJSONObject(i);

                            String user_id = favObj.getString("user_id");
                            int event_id = favObj.getInt("event_id");


                            if (currentUserId == null) {
                                if (account == null) {
                                    currentEvent.setSeen(false);
                                }
                            } else if (currentUserId.equals(user_id)) {

                                if (currentEventId == event_id) {
                                    currentEvent.setSeen(true);
                                    return;
                                }
                            }
                            currentEvent.setSeen(false);
                        }
                    } else {
                        currentEvent.setSeen(false);
                    }
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
        RequestQueue requestQueue = Volley.newRequestQueue(mContext);
        requestQueue.add(stringRequest);
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
