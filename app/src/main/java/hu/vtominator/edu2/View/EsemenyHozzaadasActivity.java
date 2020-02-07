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
import java.text.Normalizer;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import hu.vtominator.edu2.R;
import hu.vtominator.edu2.Model.Constants;
import hu.vtominator.edu2.Controller.FcmVolley;

public class EsemenyHozzaadasActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {
    private Context mContext = EsemenyHozzaadasActivity.this;
    private static final String TAG = "EsemenyHozzaadasActivit";
    private Toolbar felsoPanel;

    private EditText etEventName, etShortDescription, etLink, etLocation, etDescription;
    private TextView tvDate, tvTime, tvPicture, tvCharCount;
    private Button bDate, bTime, bMake, bPicture;

    private Spinner sType, sMainCategory, sSideCategory, sPdf;
    private String type, main_category, side_category, pdf;

    private Calendar calendar;
    private DatePickerDialog datePickerDialog;

    private Bitmap bitmap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_esemeny_hozzaadas);


        felsoPanel = findViewById(R.id.felsoPanel);

        sType = findViewById(R.id.sType);
        sMainCategory = findViewById(R.id.sMain_category);
        sSideCategory = findViewById(R.id.sSide_category);
        sPdf = findViewById(R.id.sPdf);


        ArrayAdapter<CharSequence> typeAdapter = ArrayAdapter.createFromResource(mContext, R.array.type, android.R.layout.simple_spinner_item);
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sType.setAdapter(typeAdapter);
        sType.setOnItemSelectedListener(this);


        etEventName = findViewById(R.id.etEventname);
        etShortDescription = findViewById(R.id.etShortDescription);
        etLocation = findViewById(R.id.etLocation);
        etLink = findViewById(R.id.etLink);
        etDescription = findViewById(R.id.etDescription);

        tvDate = findViewById(R.id.tvDate);
        tvTime = findViewById(R.id.tvTime);
        tvPicture = findViewById(R.id.tvPicture);
        tvCharCount = findViewById(R.id.tvCharCount);


        bDate = findViewById(R.id.bDate);
        bTime = findViewById(R.id.bTime);
        bMake = findViewById(R.id.bMake);
        bPicture = findViewById(R.id.bPicture);

        bDate.setOnClickListener(this);
        bTime.setOnClickListener(this);
        bMake.setOnClickListener(this);
        bPicture.setOnClickListener(this);


        felsoPanelBeallitasai();
        charCount();
    }


    private void felsoPanelBeallitasai() {
        setSupportActionBar(felsoPanel);

        getSupportActionBar().setTitle("Létrehozás");

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

    private void addEvent() {
        final String Type = type;

        final String MainCategory;
        if (main_category != null) {
            MainCategory = main_category;
        } else {
            MainCategory = "";
        }

        final String SideCategory;
        if (side_category != null) {
            SideCategory = side_category;
        } else {
            SideCategory = "";
        }

        final String EventName = etEventName.getText().toString().trim();
        final String ShortDescription = etShortDescription.getText().toString().trim();
        final String Date = tvDate.getText().toString().trim();
        final String Time = tvTime.getText().toString().trim();
        final String Location = etLocation.getText().toString().trim();
        final String Link = etLink.getText().toString().trim();
        final String Description = etDescription.getText().toString().trim();

        if (type.equals("Jelentkezés")) {
            StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.URL_SETEVENTS_JELENTKEZES, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);

                        Toast.makeText(mContext, jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                        if (!jsonObject.getBoolean("error")) {
                            uploadPicture(EventName, getStringImage(bitmap));
                            if (MainActivity.ertesitesGomb){
                                ertesitesKuldese();
                            }
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
                    params.put("type", Type);
                    params.put("main_category", MainCategory);
                    params.put("side_category", SideCategory);
                    params.put("eventname", EventName);
                    params.put("date", Date);
                    params.put("time", Time);
                    params.put("location", "");
                    params.put("short_description", ShortDescription);
                    params.put("description", pdf);

                    return params;
                }
            };
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            requestQueue.add(stringRequest);
        } else if (type.equals("Esemény")) {

            StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.URL_SETEVENTS_ESEMENY, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);

                        Toast.makeText(mContext, jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                        if (!jsonObject.getBoolean("error")) {

                            ujEsemenyJelzo(main_category, side_category);

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
                    params.put("type", Type);
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
        } else if (type.equals("Kérdőív")){
            StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.URL_SETEVENTS_KERDOIV, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);

                        Toast.makeText(mContext, jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                        if (!jsonObject.getBoolean("error")) {

                            ujEsemenyJelzo(main_category, side_category);

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
                    params.put("type", Type);
                    params.put("main_category", MainCategory);
                    params.put("side_category", SideCategory);
                    params.put("eventname", EventName);
                    params.put("date", Date);
                    params.put("time", Time);
                    params.put("location", "");
                    params.put("short_description", ShortDescription);
                    params.put("description", Link);

                    return params;
                }
            };
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            requestQueue.add(stringRequest);

        }

    }
    private void ujEsemenyJelzo(String main_category, String side_category) {

        final String MainCategory;
        if (main_category != null) {
            if (main_category.equals("Kapcsolódj ki!")){
                MainCategory = "Kapcsolodj_ki";
            } else {
                MainCategory = main_category;
            }

        } else {
            MainCategory = "";
        }

        final String SideCategory;
        if (side_category != null) {
            SideCategory = side_category;
        } else {
            SideCategory = "NEMLETEZIK";
        }

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.URL_DELETESEEN_CSEMPE, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);

                    Toast.makeText(mContext, jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                    if (!jsonObject.getBoolean("error")) {


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
                params.put("main_category", Normalizer.normalize(MainCategory, Normalizer.Form.NFKD).replaceAll("[^\\p{ASCII}]", ""));
                params.put("side_category", Normalizer.normalize(SideCategory, Normalizer.Form.NFKD).replaceAll("[^\\p{ASCII}]", ""));

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
        if (bitmap != null) bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);

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
                        // Sikeres képfeltöltés esetén lefutó kód helye
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


    public void ertesitesKuldese() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.URL_SEND_MULTIPLE_PUSH,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(mContext, "Új esemény került az adatbázisba!", Toast.LENGTH_SHORT).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(mContext, error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("title", "Új esemény");
                params.put("body", "Részletekért kattintson a buborékra!");
                return params;
            }
        };
        FcmVolley.getInstance(this).addToRequestQueue(stringRequest);
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
            case R.id.bMake:
                if (tvPicture.getText().toString().toLowerCase().equals("")) {
                    Toast.makeText(mContext, "Minden mező kitöltése kötelező!", Toast.LENGTH_SHORT).show();
                } else {
                    addEvent();
                }
                break;
            case R.id.bPicture:
                selectPicture();
                break;
        }
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        type = parent.getItemAtPosition(position).toString();

        if (type.equals("Esemény")) {
            ArrayAdapter<CharSequence> mainCategoryAdapter = ArrayAdapter.createFromResource(mContext, R.array.main_category, android.R.layout.simple_spinner_item);
            mainCategoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            sMainCategory.setAdapter(mainCategoryAdapter);
            sMainCategory.setOnItemSelectedListener(this);
            sMainCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    main_category = parent.getItemAtPosition(position).toString();

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
            });


            etLocation.setVisibility(View.VISIBLE);
            etDescription.setVisibility(View.VISIBLE);
            tvCharCount.setVisibility(View.VISIBLE);

            etLink.setVisibility(View.GONE);
            sSideCategory.setVisibility(View.GONE);
            sPdf.setVisibility(View.GONE);


        } else if (type.equals("Jelentkezés")) {
            sPdf.setVisibility(View.VISIBLE);
            etLink.setVisibility(View.GONE);
            etLocation.setVisibility(View.GONE);
            etDescription.setVisibility(View.GONE);
            tvCharCount.setVisibility(View.GONE);


            ArrayAdapter<CharSequence> mainCategoryAdapter = ArrayAdapter.createFromResource(mContext, R.array.main_category, android.R.layout.simple_spinner_item);
            mainCategoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            sMainCategory.setAdapter(mainCategoryAdapter);
            sMainCategory.setOnItemSelectedListener(this);
            sMainCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    main_category = parent.getItemAtPosition(position).toString();

                    if (main_category.equals(getResources().getString(R.string.irodalom))) {
                        ArrayAdapter<CharSequence> pdfAdapter = ArrayAdapter.createFromResource(mContext, R.array.pdf_irodalom, android.R.layout.simple_spinner_item);
                        pdfAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        sPdf.setAdapter(pdfAdapter);
                        sPdf.setOnItemSelectedListener(this);
                        sPdf.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                pdf = parent.getItemAtPosition(position).toString();
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });

                    } else if (main_category.equals(getResources().getString(R.string.zene))) {
                        ArrayAdapter<CharSequence> pdfAdapter = ArrayAdapter.createFromResource(mContext, R.array.pdf_zene, android.R.layout.simple_spinner_item);
                        pdfAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        sPdf.setAdapter(pdfAdapter);
                        sPdf.setOnItemSelectedListener(this);
                        sPdf.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                pdf = parent.getItemAtPosition(position).toString();
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });
                    } else if (main_category.equals(getResources().getString(R.string.tanc))) {
                        ArrayAdapter<CharSequence> pdfAdapter = ArrayAdapter.createFromResource(mContext, R.array.pdf_tanc, android.R.layout.simple_spinner_item);
                        pdfAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        sPdf.setAdapter(pdfAdapter);
                        sPdf.setOnItemSelectedListener(this);
                        sPdf.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                pdf = parent.getItemAtPosition(position).toString();
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });
                    } else if (main_category.equals(getResources().getString(R.string.latvany))) {
                        ArrayAdapter<CharSequence> pdfAdapter = ArrayAdapter.createFromResource(mContext, R.array.pdf_latvany, android.R.layout.simple_spinner_item);
                        pdfAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        sPdf.setAdapter(pdfAdapter);
                        sPdf.setOnItemSelectedListener(this);
                        sPdf.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                pdf = parent.getItemAtPosition(position).toString();
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
            });

        } else if (type.equals("Kérdőív")) {
            etLink.setVisibility(View.VISIBLE);
            sPdf.setVisibility(View.GONE);
            etLocation.setVisibility(View.GONE);
            etDescription.setVisibility(View.GONE);
            tvCharCount.setVisibility(View.GONE);


            ArrayAdapter<CharSequence> mainCategoryAdapter = ArrayAdapter.createFromResource(mContext, R.array.main_category, android.R.layout.simple_spinner_item);
            mainCategoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            sMainCategory.setAdapter(mainCategoryAdapter);
            sMainCategory.setOnItemSelectedListener(this);
            sMainCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    main_category = parent.getItemAtPosition(position).toString();

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
            });

        }


    }
    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }


}
