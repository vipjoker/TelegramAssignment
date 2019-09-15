package vipjokerstudio.com.telegramchart;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;


public class MainActivity extends BaseActivity {
    public static final String TAG = MainActivity.class.getName();
    private boolean isBlackTheme;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_main);


        Toolbar toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        if (savedInstanceState == null) {
            ChartDataRepository.getInstance().loadData2(getAssets());
            addFragment(new ChartListFragment(), ChartListFragment.TAG);

        }

        if(getSupportFragmentManager().findFragmentByTag(HolderFragment.TAG) == null){
            addFragmentWithoutView(new HolderFragment(),HolderFragment.TAG);
        }

        isBlackTheme = isDarkTheme();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(isBlackTheme != isDarkTheme()){
            recreate();
        }
    }

    public void replaceFragment(Fragment fragment, String tag) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.flContainer, fragment, tag)
                .addToBackStack(null)
                .commitAllowingStateLoss();
    }

    public void addFragment(Fragment fragment, String tag) {
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.flContainer, fragment, tag)
                .commitAllowingStateLoss();
    }






}
