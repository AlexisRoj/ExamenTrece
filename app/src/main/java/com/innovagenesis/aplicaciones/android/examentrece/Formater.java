package com.innovagenesis.aplicaciones.android.examentrece;

import android.app.Activity;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.AxisValueFormatter;

/**
 * Clase encargada de formatear los ejes del grafico
 * Created by alexi on 24/05/2017.
 */

class Formater implements AxisValueFormatter {

    private Activity activity;

    @Override
    public String getFormattedValue(float value, AxisBase axis) {

        String[] dias = activity.getResources().getStringArray(R.array.dias);
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

    Formater(Activity activity) {
        this.activity = activity;
    }
}
