package hu.vtominator.edu2.View;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
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
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import hu.vtominator.edu2.Controller.BottomNavigationViewHelper;
import hu.vtominator.edu2.Model.Constants;
import hu.vtominator.edu2.Model.Event;
import hu.vtominator.edu2.Model.SharedPrefManager;
import hu.vtominator.edu2.R;

import static hu.vtominator.edu2.View.EsemenyekListazasa.eventList;

public class ReszletesActivity extends AppCompatActivity {
    private Context mContext = ReszletesActivity.this;
    public static String KATEGORIA_NEVE = EsemenyekListazasa.KATEGORIA_NEVE;
    public static int KATEGORIA_SZINE = EsemenyekListazasa.KATEGORIA_SZINE;

    private static final String TAG = "ReszletesActivity";

    private Toolbar felsoPanel;

    private int position;
    private ImageView kepHelye;
    private TextView esemenyNeve, esemenyLeirasa, esemenyHelye, rovidLeiras, esemenyDatuma, esemenyIdeje;
    private String nev, hely, datum, ido, rovid_leiras, leiras, kep;
    private Button bOttleszek, bErdekel, bTetszik;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_reszletes);

        felsoPanel = findViewById(R.id.felsoPanel);
        kepHelye = findViewById(R.id.esemenyKepe);
        esemenyNeve = findViewById(R.id.esemenyNeve);
        esemenyHelye = findViewById(R.id.esemenyHelye);
        esemenyDatuma = findViewById(R.id.esemenyDatuma);
        esemenyIdeje = findViewById(R.id.esemenyIdeje);
        rovidLeiras = findViewById(R.id.rovidLeiras);
        esemenyLeirasa = findViewById(R.id.esemenyLeiras);

        bOttleszek = findViewById(R.id.bOttleszek);
        bErdekel = findViewById(R.id.bErdekel);
        bTetszik = findViewById(R.id.bTetszik);

        felsoPanelBeallitasai();
        alsoNavigaciosMenusor();
        if (ErdeklodesekActivity.sajatListaEsemenyei) sajatListaEsemenyeinekBetoltese();
        else {
            ErdeklodesekActivity.sajatListaEsemenyei = false;
            esemenyinformaciokBetoltese();
        }

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
                finish();
            }
        });
    }

    private void alsoNavigaciosMenusor() {
        BottomNavigationViewEx bottomNavigationViewEx = (BottomNavigationViewEx) findViewById(R.id.alsoMenusor);

        BottomNavigationViewHelper.setupBottomNavigationView(bottomNavigationViewEx);

        BottomNavigationViewHelper.enableNavigation(mContext, bottomNavigationViewEx);

        Menu menu = bottomNavigationViewEx.getMenu();
        MenuItem menuItem = menu.getItem(0);
        menuItem.setChecked(true);
    }

    private void sajatListaEsemenyeinekBetoltese() {
        Bundle extras = getIntent().getExtras();

        position = extras.getInt("position");
        if (!(ErdeklodesekActivity.myEventsList.isEmpty())) {
            final Event currentEvent = ErdeklodesekActivity.myEventsList.get(position);


            nev = currentEvent.getEventname();
            hely = currentEvent.getLocation();
            datum = currentEvent.getDate();
            ido = currentEvent.getTime();
            rovid_leiras = currentEvent.getShort_description();
            leiras = currentEvent.getDescription();
            kep = Constants.ROOT_URL + currentEvent.getPicture();

            Picasso.get().load(kep).into(kepHelye);
            esemenyNeve.setText(nev);
            esemenyHelye.setText(hely);
            rovidLeiras.setText(rovid_leiras);
            esemenyDatuma.setText(datum);
            esemenyIdeje.setText(ido);
            esemenyLeirasa.setText(leiras);


            bOttleszek.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!currentEvent.isParticipate()) addParticipate(currentEvent);
                    else deleteParticipate(currentEvent);
                }
            });
            bErdekel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!currentEvent.isInterest()) addInterest(currentEvent);
                    else deleteInterest(currentEvent);
                }
            });
            bTetszik.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!currentEvent.isFavorite()) addFavorite(currentEvent);
                    else deleteFavorite(currentEvent);
                }
            });

            ottleszekGombAllapota(currentEvent);
            erdekelGombAllapota(currentEvent);
            tetszikGombAllapota(currentEvent);
        }
    }

    private void esemenyinformaciokBetoltese() {
        Bundle extras = getIntent().getExtras();

        position = extras.getInt("position");
        final Event currentEvent = eventList.get(position);


        kep = Constants.ROOT_URL + currentEvent.getPicture();
        nev = currentEvent.getEventname();
        hely = currentEvent.getLocation();
        datum = currentEvent.getDate();
        ido = currentEvent.getTime().substring(0, 5);
        rovid_leiras = currentEvent.getShort_description();
        leiras = currentEvent.getDescription();


        Picasso.get().load(kep).into(kepHelye);
        esemenyNeve.setText(nev);
        esemenyHelye.setText(hely);
        rovidLeiras.setText(rovid_leiras);
        esemenyDatuma.setText(datum);
        esemenyIdeje.setText(ido);
        esemenyLeirasa.setText(leiras);


        if (!SharedPrefManager.getInstance(mContext).getUsername().equals("Vendég")) {
            bOttleszek.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!currentEvent.isParticipate()) addParticipate(currentEvent);
                    else deleteParticipate(currentEvent);
                }
            });
            bErdekel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!currentEvent.isInterest()) addInterest(currentEvent);
                    else deleteInterest(currentEvent);
                }
            });
            bTetszik.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!currentEvent.isFavorite()) addFavorite(currentEvent);
                    else deleteFavorite(currentEvent);
                }
            });
        }

        ottleszekGombAllapota(currentEvent);
        erdekelGombAllapota(currentEvent);
        tetszikGombAllapota(currentEvent);
    }


    private void addParticipate(final Event currentEvent) {

        final String user_id = SharedPrefManager.getInstance(mContext).getUserId();
        final int event_id = currentEvent.getEvent_id();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.URL_SETPARTICIPATE, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (!jsonObject.getBoolean("error")) {

                        currentEvent.setParticipate(true);

                        finish();
                        overridePendingTransition(0, 0);
                        startActivity(getIntent());
                        overridePendingTransition(0, 0);
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

    private void addInterest(final Event currentEvent) {

        final String user_id = SharedPrefManager.getInstance(mContext).getUserId();
        final int event_id = currentEvent.getEvent_id();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.URL_SETINTEREST, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    Toast.makeText(mContext, jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                    if (!jsonObject.getBoolean("error")) {

                        currentEvent.setInterest(true);

                        finish();
                        overridePendingTransition(0, 0);
                        startActivity(getIntent());
                        overridePendingTransition(0, 0);

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

    private void addFavorite(final Event currentEvent) {

        final String user_id = SharedPrefManager.getInstance(mContext).getUserId();
        final int event_id = currentEvent.getEvent_id();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.URL_SETFAVORITE, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    Toast.makeText(mContext, jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                    if (!jsonObject.getBoolean("error")) {

                        currentEvent.setFavorite(true);

                        finish();
                        overridePendingTransition(0, 0);
                        startActivity(getIntent());
                        overridePendingTransition(0, 0);

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


    private void deleteParticipate(final Event currentEvent) {
        final String user_id = SharedPrefManager.getInstance(mContext).getUserId();

        final int event_id = currentEvent.getEvent_id();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.URL_DELETEPARTICIPATE, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    Toast.makeText(mContext, jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                    if (!jsonObject.getBoolean("error")) {

                        currentEvent.setParticipate(false);

                        finish();
                        overridePendingTransition(0, 0);
                        startActivity(getIntent());
                        overridePendingTransition(0, 0);
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

    private void deleteInterest(final Event currentEvent) {
        final String user_id = SharedPrefManager.getInstance(mContext).getUserId();
        final int event_id = currentEvent.getEvent_id();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.URL_DELETEINTEREST, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    Toast.makeText(mContext, jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                    if (!jsonObject.getBoolean("error")) {

                        currentEvent.setInterest(false);

                        finish();
                        overridePendingTransition(0, 0);
                        startActivity(getIntent());
                        overridePendingTransition(0, 0);
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

    private void deleteFavorite(final Event currentEvent) {
        final String user_id = SharedPrefManager.getInstance(mContext).getUserId();
        final int event_id = currentEvent.getEvent_id();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.URL_DELETEFAVORITE, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    Toast.makeText(mContext, jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                    if (!jsonObject.getBoolean("error")) {

                        currentEvent.setFavorite(false);

                        finish();
                        overridePendingTransition(0, 0);
                        startActivity(getIntent());
                        overridePendingTransition(0, 0);
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

    private void ottleszekGombAllapota(final Event currentEvent) {

        final String currentUserId = SharedPrefManager.getInstance(mContext).getUserId();
        final int currentEventId = currentEvent.getEvent_id();
        final GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(mContext);


        StringRequest stringRequest = new StringRequest(Request.Method.GET, Constants.URL_GETPARTICIPATES, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject object = new JSONObject(response);

                    if (!object.getBoolean("error")) {
                        JSONArray favorites = object.getJSONArray("events");

                        for (int i = 0; i < favorites.length(); i++) {
                            JSONObject favObj = favorites.getJSONObject(i);

                            String user_id = favObj.getString("user_id");
                            int event_id = favObj.getInt("event_id");

                            Log.d(TAG, "asd "+currentUserId);

                            if (currentUserId == null) {
                                if (account == null) {
                                    bOttleszek.setClickable(false);
                                }
                            } else if (currentUserId.equals(user_id)) {

                                if (currentEventId == event_id) {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                        bOttleszek.setBackground(getResources().getDrawable(R.drawable.gomb_lenyomott));
                                    }
                                    bOttleszek.setTextColor(getResources().getColor(R.color.yellow));

                                    currentEvent.setParticipate(true);
                                    return;
                                }
                            }
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                bOttleszek.setBackground(getResources().getDrawable(R.drawable.gomb_nem_lenyomott));
                            }
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                bOttleszek.setElevation(16f);
                            }
                        }
                    } else {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                            bOttleszek.setBackground(getResources().getDrawable(R.drawable.gomb_nem_lenyomott));
                        }
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            bOttleszek.setElevation(16f);
                        }
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

    private void erdekelGombAllapota(final Event currentEvent) {

        final String currentUserId = SharedPrefManager.getInstance(mContext).getUserId();
        final int currentEventId = currentEvent.getEvent_id();
        final GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(mContext);


        StringRequest stringRequest = new StringRequest(Request.Method.GET, Constants.URL_GETINTERESTS, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject object = new JSONObject(response);

                    if (!object.getBoolean("error")) {
                        JSONArray favorites = object.getJSONArray("events");

                        for (int i = 0; i < favorites.length(); i++) {
                            JSONObject favObj = favorites.getJSONObject(i);

                            String user_id = favObj.getString("user_id");
                            int event_id = favObj.getInt("event_id");


                            if (currentUserId == null) {
                                if (account == null) {
                                    bErdekel.setClickable(false);
                                }
                            } else if (currentUserId.equals(user_id)) {

                                if (currentEventId == event_id) {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                        bErdekel.setBackground(getResources().getDrawable(R.drawable.gomb_lenyomott));
                                    }
                                    bErdekel.setTextColor(getResources().getColor(R.color.white));

                                    currentEvent.setInterest(true);
                                    return;
                                }
                            }
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                bErdekel.setBackground(getResources().getDrawable(R.drawable.gomb_nem_lenyomott));
                            }
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                bErdekel.setElevation(16f);
                            }
                        }
                    } else {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                            bErdekel.setBackground(getResources().getDrawable(R.drawable.gomb_nem_lenyomott));
                        }
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            bErdekel.setElevation(16f);
                        }
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

    private void tetszikGombAllapota(final Event currentEvent) {

        final String currentUserId = SharedPrefManager.getInstance(mContext).getUserId();
        final int currentEventId = currentEvent.getEvent_id();
        final GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(mContext);


        StringRequest stringRequest = new StringRequest(Request.Method.GET, Constants.URL_GETFAVORITES, new Response.Listener<String>() {
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
                                    bTetszik.setClickable(false);
                                }
                            } else if (currentUserId.equals(user_id)) {

                                if (currentEventId == event_id) {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                        bTetszik.setBackground(getResources().getDrawable(R.drawable.gomb_lenyomott));
                                    }
                                    bTetszik.setTextColor(getResources().getColor(R.color.white));

                                    currentEvent.setFavorite(true);
                                    return;
                                }
                            }
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                bTetszik.setBackground(getResources().getDrawable(R.drawable.gomb_nem_lenyomott));
                            }
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                bTetszik.setElevation(16f);
                            }
                        }
                    } else {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                            bTetszik.setBackground(getResources().getDrawable(R.drawable.gomb_nem_lenyomott));
                        }
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            bTetszik.setElevation(16f);
                        }
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

    @Override
    public void onBackPressed() {

    }
}
