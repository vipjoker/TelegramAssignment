package vipjokerstudio.com.telegramchart;

import android.content.Context;
import android.widget.ArrayAdapter;

import vipjokerstudio.com.telegramchart.model.ChartData;

public class ChartListAdapter extends ArrayAdapter<ChartData> {

    public ChartListAdapter(Context context) {
        super(context,R.layout.item_chart_list,R.id.tvName);
    }

}
