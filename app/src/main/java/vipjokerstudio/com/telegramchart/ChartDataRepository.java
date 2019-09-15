package vipjokerstudio.com.telegramchart;

import android.content.res.AssetManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import vipjokerstudio.com.telegramchart.model.ChartData;

public class ChartDataRepository {
    private static ChartDataRepository INSTANCE = new ChartDataRepository();

    public static ChartDataRepository getInstance() {
        return INSTANCE;
    }

    private final List<ChartData> chartList = new ArrayList<>();

    private ChartDataRepository() {

    }

    public void loadData(AssetManager assetManager) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(assetManager.open("chart_data.json")))) {
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }

            chartList.addAll(parseJson(stringBuilder.toString()));


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadData2(AssetManager assetManager) {


        String pathPattern = "contest/%d/overview.json";

        for (int i = 1; i <= 5; i++) {
            final String path = String.format(pathPattern, i);
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(assetManager.open(path)))) {
                StringBuilder stringBuilder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line);
                }

                chartList.add(parseJson2(stringBuilder.toString()));


            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public List<ChartData> getChartList() {
        return chartList;
    }

    public ChartData getChartDataByIndex(int index) {
        return chartList.get(index);
    }

    private List<ChartData> parseJson(String json) {
        List<ChartData> list = new ArrayList<>();
        try {
            JSONArray array = new JSONArray(json);

            for (int i = 0; i < array.length(); i++) {
                final JSONObject jsonObject = array.getJSONObject(i);
                final JSONArray columns = jsonObject.getJSONArray("columns");
                final JSONObject types = jsonObject.getJSONObject("types");
                final JSONObject names = jsonObject.getJSONObject("names");
                final JSONObject colors = jsonObject.getJSONObject("colors");
                LineData[] lineData = new LineData[columns.length() - 1];
                ChartData chartData = null;
                for (int j = 0; j < columns.length(); j++) {
                    final JSONArray jsonArray = columns.getJSONArray(j);
                    final String key = jsonArray.getString(0);
                    if (j == 0) {
                        long[] x = new long[jsonArray.length() - 1];
                        chartData = new ChartData(x, lineData);
                        for (int index = 1; index < jsonArray.length(); index++) {
                            x[index - 1] = jsonArray.getLong(index);
                        }
                    } else {
                        String name = names.getString(key);
                        String color = colors.getString(key);
                        String type = types.getString(key);
                        lineData[j - 1] = new LineData(jsonArray.length() - 1, name, color, type);
                        for (int index = 1; index < jsonArray.length(); index++) {
                            lineData[j - 1].getData()[index - 1] = jsonArray.getInt(index);
                        }
                    }
                }
                chartData.setName("Chart " + i + 1);
                list.add(chartData);
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }

    private ChartData parseJson2(String json) {

        try {


            final JSONObject jsonObject = new JSONObject(json);
            final JSONArray columns = jsonObject.getJSONArray("columns");
            final JSONObject types = jsonObject.getJSONObject("types");
            final JSONObject names = jsonObject.getJSONObject("names");
            final JSONObject colors = jsonObject.getJSONObject("colors");



            LineData[] lineData = new LineData[columns.length() - 1];
            ChartData chartData = null;
            for (int j = 0; j < columns.length(); j++) {
                final JSONArray jsonArray = columns.getJSONArray(j);
                final String key = jsonArray.getString(0);
                if (j == 0) {
                    long[] x = new long[jsonArray.length() - 1];
                    chartData = new ChartData(x, lineData);
                    for (int index = 1; index < jsonArray.length(); index++) {
                        x[index - 1] = jsonArray.getLong(index);
                    }
                } else {
                    String name = names.getString(key);
                    String color = colors.getString(key);
                    String type = types.getString(key);
                    lineData[j - 1] = new LineData(jsonArray.length() - 1, name, color, type);
                    for (int index = 1; index < jsonArray.length(); index++) {
                        lineData[j - 1].getData()[index - 1] = jsonArray.getInt(index);
                    }
                }
            }
            StringBuilder builder = new StringBuilder();
            final Iterator<String> keys = names.keys();
            while (keys.hasNext()){
                final String n = names.getString(keys.next());
                builder.append(n);
                builder.append(" ");
            }

            if(chartData == null)return null;

            if(jsonObject.has("y_scaled")){
                final boolean yScaled = jsonObject.getBoolean("y_scaled");
                chartData.setyScaled(yScaled);
            }

            if(jsonObject.has("stacked")){
                final boolean stacked = jsonObject.getBoolean("stacked");
                chartData.setStacked(stacked);
            }


                chartData.setName(builder.toString());
            return chartData;


        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
