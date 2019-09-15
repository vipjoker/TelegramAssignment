package vipjokerstudio.com.telegramchart;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.List;

import vipjokerstudio.com.telegramchart.model.ChartData;

public class ChartListFragment extends Fragment {
    private ListView lvCharts;
    private ChartListAdapter adapter;

    public static final String TAG = ChartListFragment.class.getName();
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_list,container,false);

        findViews(root);
        setupListView();
        return root;
    }

    private void setupListView() {
        adapter = new ChartListAdapter(getActivity());
        final List<ChartData> chartList = ChartDataRepository.getInstance().getChartList();
        adapter.addAll(chartList);
        lvCharts.setAdapter(adapter);
        lvCharts.setOnItemClickListener(this::onItemClicked);
    }

    private void onItemClicked(AdapterView<?> adapterView, View view, int position, long l) {
        if(getActivity()!= null) {






            TextureViewActivity.startChartActvity(getActivity(),position);
//            ((MainActivity) getActivity()).replaceFragment(ChartFragment.getInstance(position), ChartFragment.TAG);
        }

    }

    private void findViews(View root) {
        lvCharts = root.findViewById(R.id.lvCharts);
    }
}
