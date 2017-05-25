package com.innovagenesis.aplicaciones.android.examentrece;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.AxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements OnChartValueSelectedListener {

    private BarChart mChart;
    private int contador = 1;
    private ArrayList<BarEntry> yVals1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mChart = (BarChart) findViewById(R.id.grafico);
        final EditText valores = (EditText) findViewById(R.id.valores);

        yVals1 = new ArrayList<>();

        /**
         * Creacion del gráfico
         * */

        mInicializarGrafico();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                float data = Float.valueOf(valores.getText().toString());
                mCargarDatos(contador, data);
                contador++;
            }
        });
        mCargarDatos(contador, 0f);

    }

    private void mInicializarGrafico() {

        mChart.setDrawBarShadow(false); // completa el grafico con sombra
        mChart.setDrawValueAboveBar(true); // posicion de la etiqueta de datos
        mChart.setDescription(""); //Descripcion para el grafico (Leyenda)
        mChart.setMaxVisibleValueCount(7); // Maximas etiquetas
        mChart.setPinchZoom(true); // Zomm
        mChart.setDrawGridBackground(false); //Fondo

        AxisValueFormatter xAxisFormatter = new Formater(mChart, this);

        XAxis xAxis = mChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM); //posicion de las etiquetas
        xAxis.setTypeface(Typeface.SERIF); //Tipo de letra
        xAxis.setDrawGridLines(true); //Cuadricula
        xAxis.setGranularity(1f); // only intervals of 1 day
        xAxis.setLabelCount(7); // cantidad de etiquetas mostradas
        xAxis.setValueFormatter(xAxisFormatter);
        Legend l = mChart.getLegend();
        l.setPosition(Legend.LegendPosition.BELOW_CHART_LEFT); // Posicion de la legenda de datos
        l.setForm(Legend.LegendForm.SQUARE); //Forma de la leyenda
        l.setFormSize(9f);
        l.setTextSize(11f);
        l.setXEntrySpace(4f);
    }

    private void mCargarDatos(int contador, float valor) {

        float start = 0f;
        BarDataSet set1;

        mChart.getXAxis().setAxisMinValue(start); //Inicio del eje x
        mChart.getXAxis().setAxisMaxValue(start + contador + 1); //cantidad elementos eje x

        //yVals1.add(new BarEntry(0, 0));
        yVals1.add(new BarEntry(contador, valor));
        set1 = new BarDataSet(yVals1, "Nombres");
        set1.setColors(ColorTemplate.MATERIAL_COLORS);

        ArrayList<IBarDataSet> dataSets = new ArrayList<IBarDataSet>();
        dataSets.add(set1);
        BarData data = new BarData(dataSets);
        data.setValueTextSize(10f);
        data.setValueTypeface(Typeface.SERIF);
        data.setBarWidth(0.9f);
        mChart.setData(data);
        mChart.invalidate();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onValueSelected(Entry e, Highlight h) {

    }

    @Override
    public void onNothingSelected() {

    }
}
