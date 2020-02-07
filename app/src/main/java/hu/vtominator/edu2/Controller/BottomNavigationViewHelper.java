package hu.vtominator.edu2.Controller;

import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import android.view.MenuItem;

import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import hu.vtominator.edu2.View.BeallitasokActivity;
import hu.vtominator.edu2.View.FunActivity;
import hu.vtominator.edu2.View.KapcsolodjKiActivity;
import hu.vtominator.edu2.View.MainActivity;
import hu.vtominator.edu2.R;

public class BottomNavigationViewHelper {

    public static void setupBottomNavigationView(BottomNavigationViewEx bottomNavigationViewEx) {

        bottomNavigationViewEx.enableAnimation(false);
        bottomNavigationViewEx.enableShiftingMode(false);
        bottomNavigationViewEx.enableItemShiftingMode(false);
        bottomNavigationViewEx.setTextVisibility(false);
    }

    public static void enableNavigation(final Context context, final BottomNavigationViewEx view) {
        view.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.ic_home:
                        if (!(context instanceof MainActivity)) {
                            Intent intent1 = new Intent(context, MainActivity.class);
                            context.startActivity(intent1);
                        }
                        break;
                    case R.id.ic_kapcsolodj_ki:
                        if (!(context instanceof  KapcsolodjKiActivity)){
                            Intent intent2 = new Intent(context, KapcsolodjKiActivity.class);
                            context.startActivity(intent2);
                        }
                        break;
                    case R.id.ic_fun:
                        if (!(context instanceof FunActivity)){
                            Intent intent3 = new Intent(context, FunActivity.class);
                            context.startActivity(intent3);
                        }
                        break;
                    case R.id.ic_beallitasok:
                        if (!(context instanceof BeallitasokActivity)){
                            Intent intent4 = new Intent(context, BeallitasokActivity.class);
                            context.startActivity(intent4);
                        }
                        break;
                }

                return false;
            }
        });
    }
}
