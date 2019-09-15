package vipjokerstudio.com.telegramchart;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.DateFormat;

import vipjokerstudio.com.telegramchart.model.ChartData;
import vipjokerstudio.com.telegramchart.render.RenderTextureView;
import vipjokerstudio.com.telegramchart.render.Renderer;
import vipjokerstudio.com.telegramchart.util.DateUtil;
import vipjokerstudio.com.telegramchart.view.CustomSwitch;
import vipjokerstudio.com.telegramchart.view.LabelLayout;


public class TextureViewActivity extends BaseActivity {
    public static final String TAG = TextureViewActivity.class.getName();
    public static final String ID_ARG = "id_arg";

    private LabelLayout llCheckboxes;
    private Renderer renderer;
    private Renderer miniChartRenderer;
    private ChartData chartData;
    private HolderFragment fragment;
    private TextView tvName;
    private TextView tvDate;


    public static void startChartActvity(Activity activity, int id) {


        Intent intent = new Intent(activity, TextureViewActivity.class);
//        ActivityOptions options =
//                ActivityOptions.makeCustomAnimation(activity, android.R.anim.fade_in, android.R.anim.fade_out);
        intent.putExtra(ID_ARG, id);
//        activity.startActivity(intent,options.toBundle());
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_texture_view);
        Toolbar toolbar = findViewById(R.id.toolbar);
        tvDate = findViewById(R.id.tvDate);
        tvName = findViewById(R.id.tvName);

        setSupportActionBar(toolbar);

        RenderTextureView mainTextureView = findViewById(R.id.mainTextureView);
        RenderTextureView miniTextureView = findViewById(R.id.miniTextureView);


        llCheckboxes = findViewById(R.id.llCheckboxes);
        mainTextureView.setOpaque(false);
        miniTextureView.setOpaque(false);
        final int id = getIntent().getIntExtra(ID_ARG, -1);
        chartData = ChartDataRepository.getInstance().getChartDataByIndex(id);
        fragment = findFragmentByTag(HolderFragment.TAG);
        if (fragment == null) {

            fragment = new HolderFragment();
            fragment.initRenderer(chartData);
            addFragmentWithoutView(fragment, HolderFragment.TAG);

        }
        renderer = fragment.getRenderer();
        miniChartRenderer = fragment.getMiniChartRenderer();


        renderer.setMainColor(getPrimaryColor());
        renderer.setTextColor(getTextColor());
        if (isDarkTheme()) {
            renderer.setSecondaryColor(getSecondaryColor());
        } else {
            renderer.setSecondaryColor(getPrimaryColor());
        }


        miniTextureView.setRenderer(miniChartRenderer);
        mainTextureView.setRenderer(renderer);
//        renderer.initShadowPaint(mainTextureView);

        setupLabels();

        initCheckBoxes(chartData);
    }

    private void setupLabels() {
        tvDate.setText(DateUtil.formatDateWithYear(chartData.getX(0)) + " - " + DateUtil.formatDateWithYear(chartData.getX(chartData.size()-1)));
        tvName.setText(chartData.getName());
    }


    private void initCheckBoxes(ChartData chartData) {
        for (LineData lineData : chartData.getyData()) {
            CustomSwitch customSwitch = new CustomSwitch(this);
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            customSwitch.setTitle(lineData.getName());
            customSwitch.setLayoutParams(params);
            customSwitch.setPadding(50, 30, 50, 30);
            customSwitch.setCustomColor(lineData.getColor());
            customSwitch.setChecked(lineData.isActive());

            customSwitch.setOnCheckedChangeListener(this::onCheckChanged);
            llCheckboxes.addView(customSwitch);
        }
    }

    private void onCheckChanged(View view, boolean b) {
        final int index = llCheckboxes.indexOfChild(view);
        fragment.getScaleData().setShowInfo(false);
        chartData.getyData()[index].setActive(b);

    }


}
