package hu.vtominator.edu2.View;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import hu.vtominator.edu2.R;
import hu.vtominator.edu2.Model.Constants;
import hu.vtominator.edu2.Model.Event;

import static hu.vtominator.edu2.View.EsemenyekListazasa.eventList;

public class EsemenyModositasActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {
    private Context mContext = EsemenyModositasActivity.this;
    private static final String TAG = "EsemenyModositasActivit";
    private Toolbar felsoPanel;
    private int position, event_id;
    private String tipus, fokategoria, alkategoria, nev, hely, datum, ido, rovid_leiras, leiras;

    private EditText etEventName, etLocation, etShortDescription, etDescription;
    private TextView tvDate, tvTime, tvPicture, tvCharCount;

    private Spinner sType, sMainCategory, sSideCategory, sPdf;
    private String type, main_category, side_category, pdf;

    private Button bDate, bTime, bPicture, bModositas, bMegse;

    private Calendar calendar;
    private DatePickerDialog datePickerDialog;

    private Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_esemeny_modositas);

        felsoPanel = findViewById(R.id.felsoPanel);

        sType = findViewById(R.id.sType);
        sMainCategory = findViewById(R.id.sMain_category);
        sSideCategory = findViewById(R.id.sSide_category);
        sPdf = findViewById(R.id.sPdf);

        ArrayAdapter<CharSequence> mainCategoryAdapter = ArrayAdapter.createFromResource(mContext, R.array.main_category, android.R.layout.simple_spinner_item);
        mainCategoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sMainCategory.setAdapter(mainCategoryAdapter);
        sMainCategory.setOnItemSelectedListener(this);


        etEventName = findViewById(R.id.etEventname);
        etLocation = findViewById(R.id.etLocation);
        etShortDescription = findViewById(R.id.etShortDescription);
        etDescription = findViewById(R.id.etDescription);

        tvDate = findViewById(R.id.tvDate);
        tvTime = findViewById(R.id.tvTime);
        tvPicture = findViewById(R.id.tvPicture);
        tvCharCount = findViewById(R.id.tvCharCount);

        bDate = findViewById(R.id.bDate);
        bTime = findViewById(R.id.bTime);
        bPicture = findViewById(R.id.bPicture);
        bModositas = findViewById(R.id.bModositas);
        bMegse = findViewById(R.id.bMegse);

        bDate.setOnClickListener(this);
        bTime.setOnClickListener(this);
        bPicture.setOnClickListener(this);
        bModositas.setOnClickListener(this);
        bMegse.setOnClickListener(this);

        esemenyinformaciokBetoltese();

        felsoPanelBeallitasai();
        charCount();

    }

    private void esemenyinformaciokBetoltese() {
        Bundle extras = getIntent().getExtras();

        position = extras.getInt("position");
        final Event currentEvent = eventList.get(position);
        event_id = currentEvent.getEvent_id();


        tipus = currentEvent.getType();
        fokategoria = currentEvent.getMain_category();
        alkategoria = currentEvent.getSide_category();
        nev = currentEvent.getEventname();
        hely = currentEvent.getLocation();
        rovid_leiras = currentEvent.getShort_description();
        datum = currentEvent.getDate();
        ido = currentEvent.getTime();
        leiras = currentEvent.getDescription();
        pdf = currentEvent.getDescription();

        String compareValue = tipus;
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.type, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sType.setAdapter(adapter);
        if (compareValue != null) {
            int spinnerPosition = adapter.getPosition(compareValue);
            sType.setSelection(spinnerPosition);
        }

        compareValue = fokategoria;
        adapter = ArrayAdapter.createFromResource(this, R.array.main_category, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sMainCategory.setAdapter(adapter);
        if (compareValue != null) {
            int spinnerPosition = adapter.getPosition(compareValue);
            sMainCategory.setSelection(spinnerPosition);
        }



        if (tipus.equals("Jelentkezés")) {
            sPdf.setVisibility(View.VISIBLE);
            compareValue = pdf;

            if (fokategoria.equals(getResources().getString(R.string.irodalom))) {
                adapter = ArrayAdapter.createFromResource(this, R.array.pdf_irodalom, android.R.layout.simple_spinner_item);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                sPdf.setAdapter(adapter);
                int spinnerPosition = adapter.getPosition(compareValue);
                sPdf.setSelection(spinnerPosition);


            } else if (fokategoria.equals(getResources().getString(R.string.zene))) {
                adapter = ArrayAdapter.createFromResource(this, R.array.pdf_zene, android.R.layout.simple_spinner_item);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                sPdf.setAdapter(adapter);
                int spinnerPosition = adapter.getPosition(compareValue);
                sPdf.setSelection(spinnerPosition);

            } else if (fokategoria.equals(getResources().getString(R.string.tanc))) {
                adapter = ArrayAdapter.createFromResource(this, R.array.pdf_tanc, android.R.layout.simple_spinner_item);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                sPdf.setAdapter(adapter);
                int spinnerPosition = adapter.getPosition(compareValue);
                sPdf.setSelection(spinnerPosition);

            } else if (fokategoria.equals(getResources().getString(R.string.latvany))) {
                adapter = ArrayAdapter.createFromResource(this, R.array.pdf_latvany, android.R.layout.simple_spinner_item);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                sPdf.setAdapter(adapter);
                int spinnerPosition = adapter.getPosition(compareValue);
                sPdf.setSelection(spinnerPosition);

            }
        } else if (tipus.equals("Esemény")) {
            sPdf.setVisibility(View.GONE);

            if (fokategoria.equals(getResources().getString(R.string.fun))) {
                compareValue = alkategoria;
                adapter = ArrayAdapter.createFromResource(this, R.array.side_category_fun, android.R.layout.simple_spinner_item);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                sSideCategory.setAdapter(adapter);
                if (compareValue != null) {
                    int spinnerPosition = adapter.getPosition(compareValue);
                    sSideCategory.setSelection(spinnerPosition);
                }
            } else if (fokategoria.equals(getResources().getString(R.string.kapcsolodj_ki))) {
                sSideCategory.setVisibility(View.VISIBLE);
                ArrayAdapter<CharSequence> sideCategoryAdapter = ArrayAdapter.createFromResource(mContext, R.array.side_category_kapcsolodj_ki, android.R.layout.simple_spinner_item);
                sideCategoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                sSideCategory.setAdapter(adapter);
                if (compareValue != null) {
                    int spinnerPosition = adapter.getPosition(compareValue);
                    sSideCategory.setSelection(spinnerPosition);
                }
            }
        }


        etEventName.setText(nev);
        etLocation.setText(hely);
        etShortDescription.setText(rovid_leiras);
        tvDate.setText(datum);
        tvTime.setText(ido);
        etDescription.setText(leiras);


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

    private void modifyEvent() {

        final String MainCategory = main_category;
        final String SideCategory;
        if (side_category != null) {
            SideCategory = side_category;
        } else {
            SideCategory = "";
        }
        final String EventName = etEventName.getText().toString().trim();
        final String Date = tvDate.getText().toString().trim();
        final String Time = tvTime.getText().toString().trim();
        final String Location = etLocation.getText().toString().trim();
        final String ShortDescription = etShortDescription.getText().toString().trim();
        final String Description = etDescription.getText().toString().trim();


        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.URL_MODIFYEVENT, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);

                    Toast.makeText(mContext, jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                    if (!jsonObject.getBoolean("error")) {
                        uploadPicture(EventName, getStringImage(bitmap));
                        startActivity(new Intent(mContext, MainActivity.class));
                        finish();
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
                params.put("event_id", String.valueOf(event_id));
                params.put("main_category", MainCategory);
                params.put("side_category", SideCategory);
                params.put("eventname", EventName);
                params.put("date", Date);
                params.put("time", Time);
                params.put("location", Location);
                params.put("short_description", ShortDescription);
                params.put("description", Description);

                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }

    private void selectTime() {
        calendar = Calendar.getInstance();

        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(mContext, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                tvTime.setText(hourOfDay + ":" + String.format("%02d", minute));
            }
        }, hour, minute, true);

        timePickerDialog.show();
    }

    private void selectDate() {
        calendar = Calendar.getInstance();

        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);

        datePickerDialog = new DatePickerDialog(mContext, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int mYear, int mMonth, int mDay) {
                mMonth += 1;
                // tvEventDate.setText(mYear+". "+monthMap.get(mMonth)+" "+mDay+".");
                tvDate.setText(mYear + "-" + String.format("%02d", mMonth) + "-" + String.format("%02d", mDay));
            }
        }, year, month, day);

        datePickerDialog.show();
    }

    private void selectPicture() {
        chooseFile();
    }

    private void charCount() {
        etDescription.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                tvCharCount.setText(500 - s.toString().length() + "/500 karakter");
            }
        });
    }

    private void chooseFile() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Válassz képet"), 1);
    }

    private String getStringImage(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);

        byte[] imageByteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(imageByteArray, Base64.DEFAULT); //String encodedimage;


    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri filePath = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
            } catch (IOException e) {
                e.printStackTrace();
            }
            String picturePath = Environment.getExternalStorageDirectory().getAbsolutePath();
            tvPicture.setText(picturePath);
        }
    }

    private void uploadPicture(final String eventname, final String photo) {

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Feltöltés... ");

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.URL_UPLOAD_EVENTPICTURE, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String success = jsonObject.getString("success");

                    if (success.equals("1")) {
                        //Sikeres képfeltöltés esetén lefutó kód
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    progressDialog.dismiss();
                    Toast.makeText(mContext, e.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        Toast.makeText(mContext, error.toString(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("eventname", eventname);
                params.put("photo", photo);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bDate:
                selectDate();
                break;
            case R.id.bTime:
                selectTime();
                break;
            case R.id.bModositas:
                if (tvPicture.getText().toString().toLowerCase().equals("")) {
                    Toast.makeText(mContext, "Minden mező kitöltése kötelező!", Toast.LENGTH_SHORT).show();
                } else {
                    modifyEvent();
                }
                break;
            case R.id.bMegse:
                finish();
                break;
            case R.id.bPicture:
                selectPicture();
                break;
        }
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        main_category = parent.getItemAtPosition(position).toString();
        sSideCategory.setVisibility(View.GONE);

        if (main_category.equals(getResources().getString(R.string.fun))) {
            sSideCategory.setVisibility(View.VISIBLE);
            ArrayAdapter<CharSequence> sideCategoryAdapter = ArrayAdapter.createFromResource(mContext, R.array.side_category_fun, android.R.layout.simple_spinner_item);
            sideCategoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            sSideCategory.setAdapter(sideCategoryAdapter);
            sSideCategory.setOnItemSelectedListener(this);
            sSideCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    side_category = parent.getItemAtPosition(position).toString();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

        } else if (main_category.equals(getResources().getString(R.string.kapcsolodj_ki))) {
            sSideCategory.setVisibility(View.VISIBLE);
            ArrayAdapter<CharSequence> sideCategoryAdapter = ArrayAdapter.createFromResource(mContext, R.array.side_category_kapcsolodj_ki, android.R.layout.simple_spinner_item);
            sideCategoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            sSideCategory.setAdapter(sideCategoryAdapter);
            sSideCategory.setOnItemSelectedListener(this);
            sSideCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    side_category = parent.getItemAtPosition(position).toString();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }


}
