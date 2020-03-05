package hu.vtominator.edu2.View;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;
import hu.vtominator.edu2.Model.Event;
import hu.vtominator.edu2.R;

import static hu.vtominator.edu2.View.EsemenyekListazasa.eventList;

public class KerdoivActivity extends AppCompatActivity {
    private int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_kerdoiv);

        Bundle extras = getIntent().getExtras();
        position = extras.getInt("position");

        final Event currentEvent = eventList.get(position);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(currentEvent.getDescription()));
        startActivity(intent);

        supportFinishAfterTransition();
    }

    @Override
    public void onBackPressed() {
    }

}
