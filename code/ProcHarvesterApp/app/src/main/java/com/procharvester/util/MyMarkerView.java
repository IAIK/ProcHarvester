package com.procharvester.util;


import android.content.Context;
import android.widget.TextView;

import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;
import com.github.mikephil.charting.utils.Utils;

import com.procharvester.R;

/**
 * Custom implementation of the MarkerView.
 * taken from https://github.com/PhilJay/MPAndroidChart
 * @author Philipp Jahoda
 */
public class MyMarkerView extends MarkerView {

    private TextView tvContent;


    public MyMarkerView(Context context, int layoutResource) {
        super(context, layoutResource);

        tvContent = (TextView) findViewById(R.id.tvContent);
    }

    Entry getPrevEntry(Entry e) {

        Chart chart = getChartView();
        if (chart == null) {
            return null;
        }

        LineDataSet lineDataSet = (LineDataSet)chart.getData().getDataSetByIndex(0);
        int index = lineDataSet.getEntryIndex(e);
        if (index == 0) {
            return null;
        }

        return lineDataSet.getEntryForIndex(index - 1);
    }

    // callbacks everytime the MarkerView is redrawn, can be used to update the
    // content (user-interface)
    @Override
    public void refreshContent(Entry e, Highlight highlight) {

        String popupText = "" + Utils.formatNumber(e.getY(), 0, true);

        Entry prev = getPrevEntry(e);
        if (prev != null) {
            int delta = (int)(e.getY() - prev.getY());
            popupText += "  ";
            if (delta >= 0) {
                popupText += "+";
            }
            popupText += Integer.toString(delta);
        }

        tvContent.setText(popupText);
        super.refreshContent(e, highlight);
    }

    @Override
    public MPPointF getOffset() {
        return new MPPointF(-(getWidth() / 2), -getHeight());
    }
}