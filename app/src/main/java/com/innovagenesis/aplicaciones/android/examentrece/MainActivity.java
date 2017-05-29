package com.innovagenesis.aplicaciones.android.examentrece;

import android.graphics.Typeface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

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
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements OnChartValueSelectedListener, View.OnLongClickListener {

    private BarChart mChart;
    private int contador = 1;
    private ArrayList<BarEntry> yVals1;

    /** Entorno de variables */
    private InterstitialAd interstitialAd;
    private CountDownTimer countDownTimer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mChart = (BarChart) findViewById(R.id.grafico);
        mChart.setOnLongClickListener(this);
        final EditText valores = (EditText) findViewById(R.id.valores);
        final TextInputEditText txtValores = (TextInputEditText)findViewById(R.id.valores2);
        final TextInputLayout inputData =(TextInputLayout)findViewById(R.id.textInput) ;


        /**
         * Creacion del gráfico
         * */

        mInicializarGrafico();

        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String errorUsuario = "";
                if (TextUtils.isEmpty(txtValores.getText())) {
                    errorUsuario = "No puede ser vacio";
                } else{
                    float data = Float.valueOf(txtValores.getText().toString());
                    mCargarDatos(contador, data);
                    contador++;
                    txtValores.setText(null);
                }
                mValidarTextInput(inputData, errorUsuario);
            }
        });
        mCargarDatos(contador, 0f);


        /**
         * Seccion de publicidad
         * */

        interstitialAd = new InterstitialAd(this);
        MobileAds.initialize(this,getString(R.string.id_admob));
        interstitialAd.setAdUnitId(getString(R.string.id_admob));

        countDownTimer = new CountDownTimer(3000, 50) {
            @Override
            public void onTick(long millisUntilFinished) {
                valores.setText(getString(R.string.sRestantes)+ ((millisUntilFinished / 1000) + 1));
            }

            @Override
            public void onFinish() {
                valores.setText("Listo!");
            }
        };
    }

    /**
     * Inicializa el grafico antes de ingresar datos
     * */
    private void mInicializarGrafico() {

        yVals1 = new ArrayList<>();

        mChart.setDrawBarShadow(false); // completa el grafico con sombra
        mChart.setDrawValueAboveBar(true); // posicion de la etiqueta de datos
        mChart.setDescription(""); //Descripcion para el grafico (Leyenda)
        mChart.setMaxVisibleValueCount(7); // Maximas etiquetas
        mChart.setPinchZoom(true); // Zomm
        mChart.setAutoScaleMinMaxEnabled(true);
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

    /**
    * Carga los datos del grafico
    * */
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
            verPublicidad();
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onValueSelected(Entry e, Highlight h) {

    }

    @Override
    public void onNothingSelected() {

    }

    /**
     * Encargado de iniciar la publicidad*/
    public void mIniciarPublicidad(){
        AdRequest adRequest = new AdRequest.Builder().build();
        interstitialAd.loadAd(adRequest);
        countDownTimer.start();
    }

    //metodo que permite cargar la publicidad
    private void verPublicidad() {
        if(interstitialAd != null && interstitialAd.isLoaded()){
            interstitialAd.show();
        }else {
            Toast.makeText(this, "No se cargo la Publicidad", Toast.LENGTH_SHORT).show();
            mIniciarPublicidad();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mIniciarPublicidad();
    }

    @Override
    protected void onPause() {
        countDownTimer.cancel();
        super.onPause();
    }

    /**
     * Limpia el grafico
     * */
    @Override
    public boolean onLongClick(View v) {
        Toast.makeText(this, "Precionado", Toast.LENGTH_SHORT).show();
        mChart.clear();
        mInicializarGrafico();
        contador = 0;
        mCargarDatos(contador, 0f);
        return false;
    }

    /**
     * Encargado de valirdar que el textinput no este vacio
     * */
    private void mValidarTextInput(TextInputLayout textInput, String mensajeError) {
        textInput.setError(mensajeError);
        if (mensajeError == null) {
            textInput.setErrorEnabled(false);
        } else
            textInput.setErrorEnabled(true);
    }
}

