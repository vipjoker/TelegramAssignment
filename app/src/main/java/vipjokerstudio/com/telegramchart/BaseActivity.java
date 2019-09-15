package vipjokerstudio.com.telegramchart;

import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import vipjokerstudio.com.telegramchart.util.Preferences;

public abstract class BaseActivity extends AppCompatActivity {



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        if (isDarkTheme()) {
            setTheme(R.style.DarkTheme);

        } else {
           setTheme(R.style.LightTheme);
        }


        setStatusBar();


    }

    private  void setStatusBar() {
        Window window = getWindow();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            View decor = getWindow().getDecorView();
            if (!isDarkTheme()) {
                decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            } else {
                decor.setSystemUiVisibility(0);
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);


            final int primaryColor = getPrimaryColor();

//            final int color = getResources().getColor(R.color.c);
            window.setStatusBarColor(primaryColor);
            ;
        }
    }

    protected int getPrimaryColor() {
        TypedValue typedValue = new TypedValue();
        Resources.Theme theme = getTheme();
        theme.resolveAttribute(R.attr.colorPrimary, typedValue, true);
        return typedValue.data;
    }

    protected int getSecondaryColor() {
        TypedValue typedValue = new TypedValue();
        Resources.Theme theme = getTheme();
        theme.resolveAttribute(R.attr.colorPrimaryDark, typedValue, true);
        return typedValue.data;
    }

    protected int getTextColor(){
        if(isDarkTheme()){
            return Color.WHITE;
        }else{
            return Color.BLACK;
        }
    }


    public boolean isDarkTheme() {
        return Preferences.isDarkTheme();
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.theme_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_switch_theme) {
            Preferences.saveThemeDarkTheme(!Preferences.isDarkTheme());

            recreate();

            return true;
        }
        return false;
    }

    public void addFragmentWithoutView(Fragment fragment, String tag) {
        getSupportFragmentManager()
                .beginTransaction()
                .add(fragment, tag)
                .commitAllowingStateLoss();
    }


    public <T extends  Fragment>T findFragmentByTag(String tag){
        return (T)getSupportFragmentManager().findFragmentByTag(tag);
    }
}
