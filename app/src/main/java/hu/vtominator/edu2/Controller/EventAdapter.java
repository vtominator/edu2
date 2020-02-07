package hu.vtominator.edu2.Controller;


import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
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
import com.squareup.picasso.Picasso;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import hu.vtominator.edu2.Model.Constants;
import hu.vtominator.edu2.Model.Event;
import hu.vtominator.edu2.Model.SharedPrefManager;
import hu.vtominator.edu2.View.EsemenyekListazasa;
import hu.vtominator.edu2.R;

import static android.view.View.GONE;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> implements Filterable {
    private List<Event> eventList;
    private List<Event> eventListFull;
    private final int itemLimit = 21;

    private static final String TAG = "EventAdapter";
    private static final int TYPE_ONE = 1;
    private static final int TYPE_TWO = 2;
    private static final int TYPE_THREE = 3;

    private OnItemClickListener mListener;


    public interface OnItemClickListener {
        void onEventClick(int position);

        void onPinnedClick(int position);

        void onDeleteClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }


    public static class EventViewHolder extends RecyclerView.ViewHolder {
        private ImageView imgPin, imgDelete, eventPicture;
        private TextView imgSeen, tvEventName, tvEventDate, tvEventTime, tvLocation, tvShortDescription, tvOttleszek, tvErdeklodesek, tvKedvelesek;


        public EventViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);


            eventPicture = itemView.findViewById(R.id.eventPicture);

            tvEventName = itemView.findViewById(R.id.tvEventName);
            tvEventDate = itemView.findViewById(R.id.tvEventDate);
            tvEventTime = itemView.findViewById(R.id.tvEventTime);
            tvLocation = itemView.findViewById(R.id.tvLocation);
            tvShortDescription = itemView.findViewById(R.id.tvShortDescription);

            tvOttleszek = itemView.findViewById(R.id.tvOttlesz);
            tvErdeklodesek = itemView.findViewById(R.id.tvErdeklodo);
            tvKedvelesek = itemView.findViewById(R.id.tvKedveles);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        listener.onEventClick(position);
                    }
                }
            });

            imgSeen = itemView.findViewById(R.id.imgSeen);
            imgPin = itemView.findViewById(R.id.imgPin);
            imgDelete = itemView.findViewById(R.id.imgDelete);

            imgPin.setVisibility(GONE);
            imgDelete.setVisibility(GONE);

            final Context mContext = tvEventName.getContext();
            final String currentUserName = SharedPrefManager.getInstance(mContext).getUsername();
            GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(mContext);

            if (account == null) {

            } else if (currentUserName.equals("admin")) {

                if (!(mContext.getClass().getSimpleName().equals("ErdeklodesekActivity") || mContext.getClass().getSimpleName().equals("ModositasActivity"))) {
                    imgPin.setVisibility(View.VISIBLE);
                    imgDelete.setVisibility(View.VISIBLE);
                }


                imgPin.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (listener != null) {
                            int position = getAdapterPosition();
                            listener.onPinnedClick(position);
                        }
                    }
                });

                imgDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (listener != null) {
                            int position = getAdapterPosition();
                            listener.onDeleteClick(position);
                        }
                    }
                });

            } else if (currentUserName.equals("admin_tanc") && EsemenyekListazasa.KATEGORIA_NEVE.equals("Tánc")) {
                imgPin.setVisibility(View.VISIBLE);
                imgDelete.setVisibility(View.VISIBLE);
            } else if (currentUserName.equals("admin_zene") && EsemenyekListazasa.KATEGORIA_NEVE.equals("Zene")) {
                imgPin.setVisibility(View.VISIBLE);
                imgDelete.setVisibility(View.VISIBLE);
            } else if (currentUserName.equals("admin_latvany") && EsemenyekListazasa.KATEGORIA_NEVE.equals("Látvány")) {
                imgPin.setVisibility(View.VISIBLE);
                imgDelete.setVisibility(View.VISIBLE);
            } else if (currentUserName.equals("admin_irodalom") && EsemenyekListazasa.KATEGORIA_NEVE.equals("Irodalom")) {
                imgPin.setVisibility(View.VISIBLE);
                imgDelete.setVisibility(View.VISIBLE);

            } else if (currentUserName.equals("admin_kapcsolodjki") && (EsemenyekListazasa.KATEGORIA_NEVE.equals("Szülők") ||
                    EsemenyekListazasa.KATEGORIA_NEVE.equals("Fiatalok") || EsemenyekListazasa.KATEGORIA_NEVE.equals("Szakemberek"))) {
                imgPin.setVisibility(View.VISIBLE);
                imgDelete.setVisibility(View.VISIBLE);
            } else if (currentUserName.equals("admin_fun") && (EsemenyekListazasa.KATEGORIA_NEVE.equals("Szórakozás") ||
                    EsemenyekListazasa.KATEGORIA_NEVE.equals("Esemény") || EsemenyekListazasa.KATEGORIA_NEVE.equals("Lehetőség"))) {
                imgPin.setVisibility(View.VISIBLE);
                imgDelete.setVisibility(View.VISIBLE);
            }

        }
    }

    public EventAdapter(List<Event> eventList) {
        eventListFull = new ArrayList<>(eventList);
        this.eventList = eventList;

    }


    @Override
    public int getItemViewType(int position) {
        Event currentEvent = eventList.get(position);
        if (currentEvent.getType().equals("Esemény")) return TYPE_ONE;
        else if ((currentEvent.getType().equals("Jelentkezés")) || (currentEvent.getType().equals("Kérdőív")))
            return TYPE_TWO;
        else return TYPE_THREE;
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_home_list_event, viewGroup, false);
        EventViewHolder eventViewHolder = new EventViewHolder(view, mListener);

        if (i == 2) {
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_home_list_jelentkezes, viewGroup, false);
            eventViewHolder = new EventViewHolder(view, mListener);
        }

        return eventViewHolder;
    }


    @Override
    public void onBindViewHolder(@NonNull EventViewHolder viewHolder, int i) {
        final Event currentEvent = eventList.get(i);

        viewHolder.tvEventName.setText(currentEvent.getEventname());
        viewHolder.tvEventDate.setText(currentEvent.getDate());
        viewHolder.tvEventTime.setText(currentEvent.getTime().substring(0, 5));
        viewHolder.tvLocation.setText(currentEvent.getLocation());
        viewHolder.tvShortDescription.setText(currentEvent.getShort_description());


        String url = Constants.ROOT_URL + currentEvent.getPicture();
        Picasso.get().load(url).into(viewHolder.eventPicture);


        if (currentEvent.isSeen()) {
            viewHolder.imgSeen.setVisibility(GONE);
        }

        if (currentEvent.getPinned() == 1) {
            viewHolder.imgPin.setVisibility(GONE);
        }

        if (viewHolder.getItemViewType() != 2) {
            ottleszekSzama(viewHolder, currentEvent);
            erdeklodesekSzama(viewHolder, currentEvent);
            kedvelesekSzama(viewHolder, currentEvent);
        }
    }


    private void ottleszekSzama(final EventViewHolder viewHolder, final Event currentEvent) {
        final Context mContext = viewHolder.tvKedvelesek.getContext();
        final String currentUserName = SharedPrefManager.getInstance(mContext).getUsername();
        final GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(mContext);

        final int event_id = currentEvent.getEvent_id();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.URL_GETPARTICIPATES_NUMBER, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject likeObject = new JSONObject(response);

                    if (!likeObject.getBoolean("error")) {
                        JSONArray jsonArray = likeObject.getJSONArray("participates");
                        JSONObject o = jsonArray.getJSONObject(0);

                        int emberekSzama = o.getInt("participates");


                        currentEvent.setEmberekSzama(emberekSzama);
                        //                       if (account == null && currentUserName.equals("Vendég"))
                        //                           viewHolder.tvKedveles.setText("A kedveléshez be kell jelentkeznie!");
                        //                           viewHolder.tvOttleszek.setText("");
                        /*else*/
                        viewHolder.tvOttleszek.setText(Integer.toString(currentEvent.getEmberekSzama()));

                    } else {
//                        if (account == null && currentUserName.equals("Vendég"))
//                            viewHolder.tvKedveles.setText("A kedveléshez be kell jelentkeznie!");
//                            viewHolder.tvOttleszek.setText("");
                        /*else*/
                        viewHolder.tvOttleszek.setText(Integer.toString(0));
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

    private void erdeklodesekSzama(final EventViewHolder viewHolder, final Event currentEvent) {
        final Context mContext = viewHolder.tvKedvelesek.getContext();
        final String currentUserName = SharedPrefManager.getInstance(mContext).getUsername();
        final GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(mContext);

        final int event_id = currentEvent.getEvent_id();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.URL_GETINTERESTS_NUMBER, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject likeObject = new JSONObject(response);

                    if (!likeObject.getBoolean("error")) {
                        JSONArray jsonArray = likeObject.getJSONArray("interests");
                        JSONObject o = jsonArray.getJSONObject(0);

                        int like = o.getInt("interests");


                        currentEvent.setEmberekSzama(like);
                        //                       if (account == null && currentUserName.equals("Vendég"))
                        //                           viewHolder.tvKedveles.setText("A kedveléshez be kell jelentkeznie!");
                        //                           viewHolder.tvErdeklodesek.setText("");
                        /*else*/
                        viewHolder.tvErdeklodesek.setText(Integer.toString(currentEvent.getEmberekSzama()));

                    } else {
//                        if (account == null && currentUserName.equals("Vendég"))
//                            viewHolder.tvKedveles.setText("A kedveléshez be kell jelentkeznie!");
//                            viewHolder.tvErdeklodesek.setText("");
                        /*else*/
                        viewHolder.tvErdeklodesek.setText(Integer.toString(0));
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

    private void kedvelesekSzama(final EventViewHolder viewHolder, final Event currentEvent) {
        final Context mContext = viewHolder.tvKedvelesek.getContext();
        final String currentUserName = SharedPrefManager.getInstance(mContext).getUsername();
        final GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(mContext);

        final int event_id = currentEvent.getEvent_id();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.URL_GETFAVORITES_NUMBER, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject likeObject = new JSONObject(response);

                    if (!likeObject.getBoolean("error")) {
                        JSONArray jsonArray = likeObject.getJSONArray("favorites");
                        JSONObject o = jsonArray.getJSONObject(0);

                        int like = o.getInt("favorites");


                        currentEvent.setEmberekSzama(like);
                        //                       if (account == null && currentUserName.equals("Vendég"))
                        //                           viewHolder.tvKedveles.setText("A kedveléshez be kell jelentkeznie!");
                        //                           viewHolder.tvKedvelesek.setText("");
                        /*else*/
                        viewHolder.tvKedvelesek.setText(Integer.toString(currentEvent.getEmberekSzama()));

                    } else {
//                        if (account == null && currentUserName.equals("Vendég"))
//                            viewHolder.tvKedveles.setText("A kedveléshez be kell jelentkeznie!");
//                            viewHolder.tvKedvelesek.setText("");
                        /*else*/
                        viewHolder.tvKedvelesek.setText(Integer.toString(0));
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


    @Override
    public int getItemCount() {
        //return eventList.size();
        if (eventList.size() > itemLimit) {
            return itemLimit;
        } else {
            return eventList.size();
        }
    }

    @Override
    public Filter getFilter() {
        return eventFilter;
    }

    public Filter eventFilter = new Filter() {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Event> eloSzurt = new ArrayList<>();
            List<Event> filteredList = new ArrayList<>();
            String filterPattern;

            for (Event event : eventListFull) {
                filterPattern = EsemenyekListazasa.KATEGORIA_NEVE.toLowerCase();

                // Előszűrt lista a kategória alapján
                if (event.getMain_category().toLowerCase().contains(filterPattern) || event.getSide_category().toLowerCase().contains(filterPattern)) {
                    eloSzurt.add(event);
                }
            }

            if (constraint.equals(EsemenyekListazasa.KATEGORIA_NEVE) || constraint == null || constraint.length() == 0) {
                filteredList.addAll(eloSzurt);
            } else {

                filterPattern = constraint.toString().toLowerCase().trim();


                // Szűrés eventre név / leírás / rövid leírás / hely alapján
                for (Event event : eloSzurt) {
                    if (event.getEventname().toLowerCase().contains(filterPattern) || event.getDescription().toLowerCase().contains(filterPattern) ||
                            event.getShort_description().toLowerCase().contains(filterPattern) || event.getLocation().toLowerCase().contains(filterPattern)) {
                        filteredList.add(event);
                    }
                }
            }


            FilterResults results = new FilterResults();
            results.values = filteredList;

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            eventList.clear();
            eventList.addAll((List) results.values);

            Collections.sort(eventList, Event.BY_PRIOR);
            notifyDataSetChanged();
        }
    };

}