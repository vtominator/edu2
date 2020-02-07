package hu.vtominator.edu2.View;

import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationManagerCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
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

import java.util.HashMap;
import java.util.Map;

import hu.vtominator.edu2.R;
import hu.vtominator.edu2.Controller.BottomNavigationViewHelper;
import hu.vtominator.edu2.Model.Constants;
import hu.vtominator.edu2.Model.SharedPrefManager;


public class MainActivity extends AppCompatActivity {
    private Context mContext = MainActivity.this;
    private static final int ACTIVITY_NUM = 0;
    private static final String TAG = "MainActivity";

    private NotificationManagerCompat notificationManager;

    private TextView imgSeenTanc, imgSeenZene, imgSeenLatvany, imgSeenIrodalom;
    private TextView imgSeenKapcsolodj_ki, imgSeenFun;

    private Button tTanc, tZene, tLatvany, tIrodalom;
    private Button tKapcsolodj_ki, tFun;
    private BottomNavigationViewEx alsoNavigaciosMenu;

    public static boolean ertesitesGomb;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        notificationManager = NotificationManagerCompat.from(this);

        //"Új" feliratok
        imgSeenTanc = findViewById(R.id.imgSeenTanc);
        imgSeenZene = findViewById(R.id.imgSeenZene);
        imgSeenLatvany = findViewById(R.id.imgSeenLatvany);
        imgSeenIrodalom = findViewById(R.id.imgSeenIrodalom);
        imgSeenKapcsolodj_ki = findViewById(R.id.imgSeenKapcsolodj_ki);
        imgSeenFun = findViewById(R.id.imgSeenFun);


        //A kezdőképernyő felső részén található "csempegombok"
        tTanc = findViewById(R.id.tTanc);
        tZene = findViewById(R.id.tZene);
        tLatvany = findViewById(R.id.tLatvany);
        tIrodalom = findViewById(R.id.tIrodalom);
        felsoCsempeGombok();


        //A középső rész nagy gombjai
        tKapcsolodj_ki = findViewById(R.id.tKapcsolodj_ki);
        tFun = findViewById(R.id.tFun);
        kozepsoCsempeGombok();


        //Alsó navigációs menüsor
        alsoNavigaciosMenu = findViewById(R.id.alsoMenusor);
        alsoNavigaciosMenusor();

        ujdonsagEllenorzo();
        ertesitesAllapota();

    }



    private void felsoCsempeGombok() {
        tTanc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addSeenCsempe(v);
                EsemenyekListazasa.KATEGORIA_NEVE = getString(R.string.tanc);
                EsemenyekListazasa.KATEGORIA_SZINE = getResources().getColor(R.color.tanc);
                startActivity(new Intent(mContext, EsemenyekListazasa.class));

            }
        });
        tZene.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addSeenCsempe(v);
                EsemenyekListazasa.KATEGORIA_NEVE = getString(R.string.zene);
                EsemenyekListazasa.KATEGORIA_SZINE = getResources().getColor(R.color.zene);
                startActivity(new Intent(mContext, EsemenyekListazasa.class));
            }
        });
        tLatvany.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addSeenCsempe(v);
                EsemenyekListazasa.KATEGORIA_NEVE = getString(R.string.latvany);
                EsemenyekListazasa.KATEGORIA_SZINE = getResources().getColor(R.color.latvany);
                startActivity(new Intent(mContext, EsemenyekListazasa.class));
            }
        });
        tIrodalom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addSeenCsempe(v);
                EsemenyekListazasa.KATEGORIA_NEVE = getString(R.string.irodalom);
                EsemenyekListazasa.KATEGORIA_SZINE = getResources().getColor(R.color.irodalom);
                startActivity(new Intent(mContext, EsemenyekListazasa.class));

            }
        });
    }

    private void kozepsoCsempeGombok() {
        tKapcsolodj_ki.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addSeenCsempe(v);
                startActivity(new Intent(mContext, KapcsolodjKiActivity.class));
                finish();
            }
        });
        tFun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addSeenCsempe(v);
                startActivity(new Intent(mContext, FunActivity.class));
                finish();
            }
        });
    }



    private void addSeenCsempe(View csempe) {

        final String user_id = SharedPrefManager.getInstance(mContext).getUserId();
        final String csempe_neve = getResources().getResourceEntryName(csempe.getId());

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.URL_SETSEEN_CSEMPE, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);

                    if (!jsonObject.getBoolean("error")) {
                        csempeLatott(csempe_neve);
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
                params.put("csempe_neve", csempe_neve);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(mContext);
        requestQueue.add(stringRequest);
    }
    private void getSeenCsempe(final View csempe) {


        final String currentUserId = SharedPrefManager.getInstance(mContext).getUserId();
        final String currentCsempeNeve = getResources().getResourceEntryName(csempe.getId());
        final GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(mContext);


        StringRequest stringRequest = new StringRequest(Request.Method.GET, Constants.URL_GETSEEN_CSEMPE, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject object = new JSONObject(response);

                    if (!object.getBoolean("error")) {
                        JSONArray favorites = object.getJSONArray("result");

                        for (int i = 0; i < favorites.length(); i++) {
                            JSONObject favObj = favorites.getJSONObject(i);

                            String user_id = favObj.getString("user_id");
                            String csempe_neve = favObj.getString("csempe_neve");

                            if (currentUserId == null) {
                                if (account == null) {
                                }
                            } else if (currentUserId.equals(user_id)) {

                                if (currentCsempeNeve.equals(csempe_neve)) {
                                    csempeLatott(csempe_neve);
                                    return;
                                }
                            }

                        }
                    } else {
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
    private void csempeLatott(String csempe){
        switch (csempe){
            case "tZene":
                imgSeenZene.setVisibility(View.GONE);
                break;
            case "tTanc":
                imgSeenTanc.setVisibility(View.GONE);
                break;
            case "tLatvany":
                imgSeenLatvany.setVisibility(View.GONE);
                break;
            case "tIrodalom":
                imgSeenIrodalom.setVisibility(View.GONE);
                break;

            case "tKapcsolodj_ki":
                imgSeenKapcsolodj_ki.setVisibility(View.GONE);
                break;
            case "tFun":
                imgSeenFun.setVisibility(View.GONE);
                break;

        }
    }
    private void ujdonsagEllenorzo() {
        getSeenCsempe(tTanc);
        getSeenCsempe(tZene);
        getSeenCsempe(tLatvany);
        getSeenCsempe(tIrodalom);

        getSeenCsempe(tKapcsolodj_ki);
        getSeenCsempe(tFun);
    }
    private void ertesitesAllapota() {

        final String currentUserId = SharedPrefManager.getInstance(mContext).getUserId();
        final GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(mContext);


        StringRequest stringRequest = new StringRequest(Request.Method.GET, Constants.URL_GETNOTIFICATION, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject object = new JSONObject(response);

                    if (!object.getBoolean("error")) {
                        JSONArray favorites = object.getJSONArray("result");

                        for (int i = 0; i < favorites.length(); i++) {
                            JSONObject favObj = favorites.getJSONObject(i);

                            String user_id = favObj.getString("user_id");

                            if (currentUserId == null) {
                                if (account == null) {
                                    ertesitesGomb = false;
                                }
                            } else if (currentUserId.equals(user_id)) {
                                ertesitesGomb = true;
                            }

                        }
                    } else {

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
        //Navigációs menü animációjának módosítása
        BottomNavigationViewHelper.setupBottomNavigationView(alsoNavigaciosMenu);

        //Bármelyik tabról bármelyik tabra átkerülhetünk: contextként kell deklarálni objektum osztályon belül
        BottomNavigationViewHelper.enableNavigation(mContext, alsoNavigaciosMenu);

        //Menüsoron a megfelelő ikon kijelölése
        Menu menu = alsoNavigaciosMenu.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
        menuItem.setChecked(true);

    }



}
