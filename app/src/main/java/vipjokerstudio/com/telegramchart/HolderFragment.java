package vipjokerstudio.com.telegramchart;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import vipjokerstudio.com.telegramchart.charts.MiniPercentageStackedAreaRenderer;
import vipjokerstudio.com.telegramchart.charts.bar.BarChartRenderer;
import vipjokerstudio.com.telegramchart.charts.line.LineChartRenderer;
import vipjokerstudio.com.telegramchart.charts.bar.MiniBarchartRenderer;
import vipjokerstudio.com.telegramchart.charts.line.MiniChartRenderer;
import vipjokerstudio.com.telegramchart.charts.line.MiniScaledChartRenderer;
import vipjokerstudio.com.telegramchart.charts.bar.MiniStackedBarChartRenderer;
import vipjokerstudio.com.telegramchart.charts.PercentageStackedAreaChart;
import vipjokerstudio.com.telegramchart.charts.line.ScaledYLineChartRenderer;
import vipjokerstudio.com.telegramchart.charts.bar.StackedBarChart;
import vipjokerstudio.com.telegramchart.model.ChartData;
import vipjokerstudio.com.telegramchart.model.DrawData;
import vipjokerstudio.com.telegramchart.render.Renderer;

public class HolderFragment extends Fragment {

    public static final String TAG = HolderFragment.class.getName();
    private DrawData scaleData;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setRetainInstance(true);
        return null;
    }

    private Renderer renderer;
    private Renderer miniChartRenderer;


    public Renderer getMiniChartRenderer() {
        return miniChartRenderer;
    }



    public Renderer getRenderer() {
        return renderer;
    }

    public void setRenderer(Renderer renderer) {
        this.renderer = renderer;
    }

    public void initRenderer(ChartData chartData) {

        scaleData = new DrawData(0, 0.3f, chartData.size());
        final LineData lineData = chartData.getyData()[0];
        final String type = lineData.getType();


        switch (type) {
            case "bar":

                if (chartData.isStacked()) {
                    renderer = new StackedBarChart(scaleData, chartData);
                    miniChartRenderer = new MiniStackedBarChartRenderer(scaleData,chartData);
                } else {
                    renderer = new BarChartRenderer(scaleData, chartData);
                    miniChartRenderer = new MiniBarchartRenderer(scaleData,chartData);
                }
                break;
            case "area":


                renderer = new PercentageStackedAreaChart(scaleData, chartData);
                miniChartRenderer = new MiniPercentageStackedAreaRenderer(scaleData,chartData);
                break;
            case "line":
                if (chartData.isyScaled()) {
                    renderer = new ScaledYLineChartRenderer(scaleData, chartData);
                    miniChartRenderer = new MiniScaledChartRenderer(scaleData,chartData);
                } else {
                    renderer = new LineChartRenderer(scaleData, chartData);
                    miniChartRenderer = new MiniChartRenderer(scaleData,chartData);
                }
                break;
            default:
                throw new RuntimeException("Unknown chart type");

        }
    }

    public DrawData getScaleData() {
        return scaleData;
    }
}
