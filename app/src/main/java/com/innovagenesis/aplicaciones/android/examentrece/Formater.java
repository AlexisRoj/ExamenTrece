package com.innovagenesis.aplicaciones.android.examentrece;

import android.app.Activity;

import com.github.mikephil.charting.charts.BarLineChartBase;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.AxisValueFormatter;

/**
 *
 * Created by alexi on 24/05/2017.
 */

public class Formater implements AxisValueFormatter {

    private BarLineChartBase<?> chart;
    private Activity activity;

    @Override
    public String getFormattedValue(float value, AxisBase axis) {

        String[] dias = activity.getResources().getStringArray(R.array.dias);

        String dia = "Lunes"; //activity.getString(Integer.parseInt(dias[(int) value ]));

        return dia;
    }

    @Override
    public int getDecimalDigits() {
        return 0;
    }

    public Formater(BarLineChartBase<?> chart, Activity activity) {
        this.chart = chart;
        this.activity = activity;
    }
}
