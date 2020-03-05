package hu.vtominator.edu2.View;

import android.content.Context;
import android.content.Intent;
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

import androidx.appcompat.app.AppCompatActivity;
import hu.vtominator.edu2.Controller.BottomNavigationViewHelper;
import hu.vtominator.edu2.Model.Constants;
import hu.vtominator.edu2.Model.SharedPrefManager;
import hu.vtominator.edu2.R;


public class FunActivity extends AppCompatActivity {
    private Context mContext = FunActivity.this;
    private static final int ACTIVITY_NUM = 2;

    private TextView imgSeenSzorakozas, imgSeenEsemeny, imgSeenLehetoseg;
    private Button tSzorakozas, tEsemeny, tLehetoseg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_fun);

        imgSeenSzorakozas = findViewById(R.id.imgSeenSzorakozas);
        imgSeenEsemeny = findViewById(R.id.imgSeenEsemeny);
        imgSeenLehetoseg = findViewById(R.id.imgSeenLehetoseg);

        tSzorakozas = findViewById(R.id.tSzorakozas);
        tEsemeny = findViewById(R.id.tEsemeny);
        tLehetoseg = findViewById(R.id.tLehetoseg);

        kozepsoCsempeGombok();
        ujdonsagEllenorzo();
        setupBottomNavigationView();
    }


    private void kozepsoCsempeGombok() {
        tSzorakozas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!SharedPrefManager.getInstance(mContext).getUsername().equals("Vendég")) {
                    addSeenCsempe(v);
                }
                EsemenyekListazasa.KATEGORIA_NEVE = getString(R.string.szorazkozas);
                EsemenyekListazasa.KATEGORIA_SZINE = getResources().getColor(R.color.black);
                startActivity(new Intent(mContext, EsemenyekListazasa.class));
                finish();
            }
        });
        tEsemeny.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!SharedPrefManager.getInstance(mContext).getUsername().equals("Vendég")) {
                    addSeenCsempe(v);
                }
                EsemenyekListazasa.KATEGORIA_NEVE = getString(R.string.esemeny);
                EsemenyekListazasa.KATEGORIA_SZINE = getResources().getColor(R.color.black);
                startActivity(new Intent(mContext, EsemenyekListazasa.class));
                finish();
            }
        });
        tLehetoseg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!SharedPrefManager.getInstance(mContext).getUsername().equals("Vendég")) {
                    addSeenCsempe(v);
                }
                EsemenyekListazasa.KATEGORIA_NEVE = getString(R.string.lehetoseg);
                EsemenyekListazasa.KATEGORIA_SZINE = getResources().getColor(R.color.black);
                startActivity(new Intent(mContext, EsemenyekListazasa.class));
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

    private void getSeenCsempe(View csempe) {


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
                                    //csempeNemLatott(csempe_neve);
                                }
                            } else if (currentUserId.equals(user_id)) {

                                if (currentCsempeNeve.equals(csempe_neve)) {
                                    csempeLatott(csempe_neve);
                                    return;
                                }
                            }
                            // csempeNemLatott(csempe_neve);
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

    private void csempeLatott(String csempe) {
        switch (csempe) {
            case "tSzorakozas":
                imgSeenSzorakozas.setVisibility(View.GONE);
                break;
            case "tEsemeny":
                imgSeenEsemeny.setVisibility(View.GONE);
                break;
            case "tLehetoseg":
                imgSeenLehetoseg.setVisibility(View.GONE);
                break;

        }
    }

    private void ujdonsagEllenorzo() {
        getSeenCsempe(tSzorakozas);
        getSeenCsempe(tEsemeny);
        getSeenCsempe(tLehetoseg);
    }

    private void setupBottomNavigationView() {
        BottomNavigationViewEx bottomNavigationViewEx = (BottomNavigationViewEx) findViewById(R.id.alsoMenusor);

        BottomNavigationViewHelper.setupBottomNavigationView(bottomNavigationViewEx);

        BottomNavigationViewHelper.enableNavigation(mContext, bottomNavigationViewEx);

        Menu menu = bottomNavigationViewEx.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
        menuItem.setChecked(true);
    }

    @Override
    public void onBackPressed() {
    }

}
