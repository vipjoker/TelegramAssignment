package vipjokerstudio.com.telegramchart;

import android.content.res.ColorStateList;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatCheckBox;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;

import java.util.HashMap;
import java.util.Map;

import vipjokerstudio.com.telegramchart.model.ChartData;
import vipjokerstudio.com.telegramchart.view.ChartView;
import vipjokerstudio.com.telegramchart.view.MiniChartView;

public class ChartFragment extends Fragment {

    public static final String TAG = ChartFragment.class.getName();
    public static final String INDEX_ARG = "position_arg";
    public static final String MIN_ARG = "min_arg";
    public static final String MAX_ARG = "max_arg";
    Map<CheckBox, Integer> map = new HashMap<>();
    private ChartData chartData;


    public static ChartFragment getInstance(int index) {
        Bundle bundle = new Bundle();
        bundle.putInt(INDEX_ARG, index);
        ChartFragment fragment = new ChartFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    private MiniChartView miniChartView;
    private ChartView chartView;
    private LinearLayout llCheckboxes;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_chart, container, false);
        findViews(root);
        initCharts();
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void findViews(View root) {
        miniChartView = root.findViewById(R.id.miniChartView);
        llCheckboxes = root.findViewById(R.id.llCheckboxes);
        chartView = root.findViewById(R.id.chartView);

    }

    private void initCharts() {
        final int index = getArguments().getInt(INDEX_ARG);
        final float min = getArguments().getFloat(MIN_ARG, 0.0f);
        final float max = getArguments().getFloat(MAX_ARG, 0.3f);
        miniChartView.setRangeListener(this::onRangeChanged);

        chartData = ChartDataRepository.getInstance().getChartDataByIndex(index);
        miniChartView.setData(chartData);
        chartView.setChartData(chartData);

        chartView.setChartScale(min, max);
        miniChartView.setInitScales(min, max);
        initCheckBoxes(chartData);

    }

    private void initCheckBoxes(ChartData chartDataByIndex) {
        LayoutInflater inflater = LayoutInflater.from(getActivity());

        map.clear();
        for (int i = 0; i < chartDataByIndex.getyData().length; i++) {
            final LineData lineData = chartDataByIndex.getyData()[i];
            View root = inflater.inflate(R.layout.item_checkbox, llCheckboxes, false);
            AppCompatCheckBox box = root.findViewById(R.id.tvCheckBox);

            box.setText(lineData.getName());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                box.setButtonTintList(ColorStateList.valueOf(lineData.getColor()));
            }


            box.setOnCheckedChangeListener(null);
            box.setChecked(lineData.isActive());
            box.setOnClickListener(this::onCheckChanged);
            llCheckboxes.addView(root);
            map.put(box, i);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {

        super.onSaveInstanceState(outState);
    }

    private boolean isAllUnchekced() {
        for (LineData lineData : chartData.getyData()) {

            if (lineData.isActive()) {
                return false;
            }
        }
        return true;
    }

    private void onCheckChanged(View view) {
            CheckBox compoundButton = (CheckBox) view;
            final Integer integer = map.get(compoundButton);
            final LineData lineData = chartData.getyData()[integer];
        lineData.setActive(compoundButton.isChecked());
//
        if (isAllUnchekced()) {
            compoundButton.setChecked(true);
            lineData.setActive(true);
        }



            chartView.updateChart();
            miniChartView.invalidate();

    }

    private void onRangeChanged(float start, float end) {
        getArguments().putFloat(MIN_ARG, start);
        getArguments().putFloat(MAX_ARG, end);


        chartView.setChartScale(start, end);
    }


}
