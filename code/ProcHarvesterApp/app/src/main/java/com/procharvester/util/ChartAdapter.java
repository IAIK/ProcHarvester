package com.procharvester.util;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;

import com.procharvester.Logging.Event;
import com.procharvester.R;

public class ChartAdapter extends ArrayAdapter<Event> {


    private final Context cx_;
    private final View.OnLongClickListener clickListener_;
    private ArrayList<Event> events_;

    public ChartAdapter(ArrayList<Event> events, Context cx, View.OnLongClickListener clickListener) {
        super(cx, R.layout.chart_item, events);
        clickListener_ = clickListener;
        events_ = events;
        cx_ = cx;
    }

    private static class ViewHolder {
        LineChart chart;
    }

    public void changeEventData(ArrayList<Event> events) {
        this.events_.clear();
        this.events_.addAll(events);
        notifyDataSetChanged();
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Event event = getItem(position);

        ViewHolder viewHolder;
        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.chart_item, parent, false);
            viewHolder.chart = (LineChart) convertView.findViewById(R.id.item_line_chart);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        initChart(viewHolder.chart, event);
        return convertView;
    }

    final float LINE_WIDTH = 2f;
    final float LABEL_TEXT_SIZE = 5f;

    void initChart(LineChart chart, Event event) {

        ArrayList<Entry> entries = event.getChartEntries();
        chart.setTag(event);
        chart.setOnLongClickListener(clickListener_);
        if (entries.isEmpty()) {
            return;
        }

        // Enable highlighting of values
        chart.setPinchZoom(false);
        chart.setScaleXEnabled(true);
        chart.setScaleYEnabled(false);
        chart.setDragEnabled(false);
        chart.setDoubleTapToZoomEnabled(false);
        chart.setHighlightPerTapEnabled(true);
        chart.setHighlightPerDragEnabled(true);
        MyMarkerView mv = new MyMarkerView(cx_, R.layout.custom_marker_view);
        mv.setChartView(chart); // For bounds control
        chart.setMarker(mv); // Set the marker to the chart

        // Set up description
        chart.getLegend().setEnabled(false);
        Description description = new Description();
        description.setText(event.toString());
        chart.setDescription(description);

        // Configure axises
        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setEnabled(true);
        xAxis.setAvoidFirstLastClipping(true);
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                int seconds = (int)(value / 1000);
                int remainingMilliSeconds = (int)(value % 1000);
                return seconds + "." + String.format("%03d", remainingMilliSeconds);
            }
        });

        YAxis rightAxis = chart.getAxisRight();
        rightAxis.setEnabled(true);
        rightAxis.setDrawLabels(false);
        rightAxis.setDrawGridLines(false);

        // Add data and format data
        if (entries.size() >= 2) {
            final float xMinimum = entries.get(0).getX();
            final float xMaximum = entries.get(entries.size() - 1).getX();
            xAxis.setAxisMinimum(xMinimum);
            xAxis.setAxisMaximum(xMaximum);
            chart.setVisibleXRange(xMinimum, xMaximum);
        }

        LineDataSet dataSet = new LineDataSet(entries, "set1");
        dataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        dataSet.setColor(Color.RED);
        dataSet.setCircleColor(Color.BLUE);
        dataSet.setLineWidth(LINE_WIDTH);
        dataSet.setFillAlpha(65);
        dataSet.setFillColor(ColorTemplate.getHoloBlue());
        dataSet.setDrawCircleHole(false);
        dataSet.setHighLightColor(Color.TRANSPARENT);
        dataSet.enableDashedHighlightLine(10f, 5f, 0f);
        dataSet.setHighLightColor(Color.BLACK);

        // Indicate the exact time when the event happened with a limit line
        xAxis.removeAllLimitLines();
        LimitLine ll = new LimitLine(event.getRelativeTime(), "Event Start");
        ll.setLineColor(Color.CYAN);
        ll.setLineWidth(LINE_WIDTH);
        ll.setTextColor(Color.BLACK);
        ll.setTextSize(LABEL_TEXT_SIZE);
        xAxis.addLimitLine(ll);

        // Show chart
        LineData lineData = new LineData(dataSet);
        chart.setData(lineData);
        chart.invalidate();
    }
}
