package hu.vtominator.edu2.Controller;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class FcmVolley {

    private static FcmVolley mInstance;
    private RequestQueue mRequestQueue;
    private static Context mContext;

    private FcmVolley(Context context) {
        mContext = context;
        mRequestQueue = getRequestQueue();
    }

    public static synchronized FcmVolley getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new FcmVolley(context);
        }
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(mContext.getApplicationContext());
        }
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> request) {
        getRequestQueue().add(request);
    }

}