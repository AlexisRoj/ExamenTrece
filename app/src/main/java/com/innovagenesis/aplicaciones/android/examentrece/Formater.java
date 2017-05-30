package com.innovagenesis.aplicaciones.android.examentrece;

import android.app.Activity;

import com.github.mikephil.charting.charts.BarLineChartBase;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.AxisValueFormatter;

/**
 * Created by alexi on 24/05/2017.
 */

public class Formater implements AxisValueFormatter {

    private BarLineChartBase<?> chart;
    private Activity activity;

    int contador;

    @Override
    public String getFormattedValue(float value, AxisBase axis) {

        String[] dias = activity.getResources().getStringArray(R.array.dias);

        contador++;
        int valor = (int) value;

        if (valor == 0)
            return "";
        else {
            try {

                 return dias[valor];

            } catch (Exception e) {
                e.printStackTrace();
                return "";
            }
        }
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
