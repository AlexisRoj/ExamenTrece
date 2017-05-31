package com.innovagenesis.aplicaciones.android.examentrece;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
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
        implements OnChartValueSelectedListener {

    private BarChart mChart;
    private int contadorGlobal = 1;
    private ArrayList<BarEntry> yVals1;

    AxisValueFormatter xAxisFormatter = null;

    private int tipoIngresoDatos = 2;
    /**
     * Entorno de variables
     */
    private InterstitialAd interstitialAd;
    private CountDownTimer countDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mChart = (BarChart) findViewById(R.id.grafico);
        yVals1 = new ArrayList<>();

        final EditText valores = (EditText) findViewById(R.id.valores);
        final TextInputEditText txtValores = (TextInputEditText) findViewById(R.id.valores2);
        final TextInputLayout inputData = (TextInputLayout) findViewById(R.id.textInput);

        final AlertDialog alertDialog =
                getAlertDialog(inputData, txtValores); //Pregunta el tipo de llenado
        alertDialog.setCancelable(false); //Evita cerrar el dialogo
        alertDialog.show();

        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                float data;
                int indice = 0;
                String errorUsuario = "";
                if (TextUtils.isEmpty(txtValores.getText())) {
                    errorUsuario = getString(R.string.error_campo_vacio);
                } else {
                    SelecionarTipoEntrada selecionarTipoEntrada =
                            new SelecionarTipoEntrada(indice, txtValores).invoke();
                    indice = selecionarTipoEntrada.getIndice();
                    data = selecionarTipoEntrada.getData();
                    //Carga el grafico con los valores atrapados
                    mCargarDatos(contadorGlobal, indice, data, tipoIngresoDatos);
                    contadorGlobal++;
                    txtValores.setText(null);
                }
                mValidarTextInput(inputData, errorUsuario);
            }
        });
        /*
          Seccion de publicidad
          */
        interstitialAd = new InterstitialAd(this);
        MobileAds.initialize(this, getString(R.string.id_admob));
        interstitialAd.setAdUnitId(getString(R.string.id_admob));

        countDownTimer = new CountDownTimer(3000, 50) {
            @SuppressLint("SetTextI18n")
            @Override
            public void onTick(long millisUntilFinished) {
                valores.setText(getString(R.string.sRestantes)
                        + ((millisUntilFinished / 1000) + 1));
            }
            @Override
            public void onFinish() {
                valores.setText(R.string.listo);
            }
        };
    }

    /**
     * Encargado de selecionar el tipo de llenado
     * Cambia mensajes de ayuda e inicializa el grafico
     * además identifica el tipo de ingreso que va haber
     *
     * @param inputLayout
     * @param inputEditText
     */
    private AlertDialog getAlertDialog(final TextInputLayout inputLayout,
                                       final TextInputEditText inputEditText) {
        return new AlertDialog.Builder(this)
                .setTitle(R.string.encabezadoDialogo)
                .setMessage(R.string.mensajeDialogo)
                .setPositiveButton(R.string.botonSecuencial,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //Selecion secuencial
                                tipoIngresoDatos = 1;
                                inputEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
                                inputLayout.setHint(getString(R.string.digite_un_valor_secuencial));

                                mInicializarGrafico(tipoIngresoDatos);
                                mCargarDatos(contadorGlobal, 0, 0f, tipoIngresoDatos);

                                dialog.dismiss();
                            }
                        })
                .setNegativeButton(R.string.botonAleatorio,
                        new DialogInterface.OnClickListener() {
                            //Seleccion Aleatoria
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                tipoIngresoDatos = 2;
                                inputEditText.setInputType(InputType.TYPE_CLASS_DATETIME);
                                inputLayout.setHint(getString(R.string.digite_un_valor_aleatorio));
                                contadorGlobal = 7; //Envia a llenar las etiquetas

                                mInicializarGrafico(tipoIngresoDatos);
                                mCargarDatos(contadorGlobal, 0, 0f, tipoIngresoDatos);

                                dialog.dismiss();
                            }
                        }).create();
    }

    /**
     * Inicializa el grafico antes de ingresar datos
     *
     * @param tipoIngresoDatos
     */
    private void mInicializarGrafico(int tipoIngresoDatos) {

        mChart.setDrawBarShadow(false); // completa el grafico con sombra
        mChart.setDrawValueAboveBar(true); // posicion de la etiqueta de datos
        mChart.setDescription(""); //Descripcion para el grafico (Leyenda)
        mChart.setMaxVisibleValueCount(7); // Maximas etiquetas
        mChart.setPinchZoom(true); // Zomm
        mChart.setAutoScaleMinMaxEnabled(true);
        mChart.setDrawGridBackground(false); //Fondo
        mChart.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AlertDialog alertLimpiarGrafico = getAlertDialogLimpiar();
                alertLimpiarGrafico.setCancelable(false);
                alertLimpiarGrafico.show();
                return true;
            }
        });
        /* * * * * * * * * * * *
         * Crea las etiquetas  *
         * * * * * * * * * * * */
        if (tipoIngresoDatos == 1) {
            xAxisFormatter = new Formater(mChart, this);
        } else if (tipoIngresoDatos == 2) {
            for (int i = 0; i < 7; i++) {
                xAxisFormatter = new Formater(mChart, this);
            }
        }

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
     * Encargado de limpiar el grafico
     * */
    private AlertDialog getAlertDialogLimpiar() {
        return new AlertDialog.Builder(MainActivity.this)
                            .setTitle(R.string.limpiar)
                            .setMessage(R.string.desea_limpiar)
                            .setPositiveButton(R.string.si, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    finish();
                                    startActivity(getIntent());
                                    Toast.makeText(MainActivity.this,
                                            R.string.limpiando_grafico, Toast.LENGTH_SHORT).show();

                                }
                            })
                            .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }).create();
    }

    /**
     * Carga los datos del grafico y valida el tipo de llenado
     */
    private void mCargarDatos(int contador, int indice, float dato, int tipoLlenado) {
        if (indice <= 7) {
            float start = 0f;
            mChart.getXAxis().setAxisMinValue(start); //Inicio del eje x
            mChart.getXAxis().setAxisMaxValue(start + contador + 1); //cantidad elementos eje x

            if (mChart.getData() != null
                    && mChart.getData().getDataSetCount() > 0
                    && tipoLlenado == 2) {
                mInsertarDatos(indice, dato);
            } else {
                mInsertarDatos(contador, dato);
            }
        } else {
            Toast.makeText(this, R.string.validacionIndices,
                    Toast.LENGTH_SHORT).show();
        }
    }
    /**
     * Metodo encargado de rellenar los datos del
     * grafico
     */
    private void mInsertarDatos(int indice, float dato) {
        BarDataSet set1;
        yVals1.add(new BarEntry(indice, dato));
        set1 = new BarDataSet(yVals1, "Nombres");
        set1.setColors(ColorTemplate.MATERIAL_COLORS);

        ArrayList<IBarDataSet> dataSets = new ArrayList<>();
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
     * Encargado de iniciar la publicidad
     */
    public void mIniciarPublicidad() {
        AdRequest adRequest = new AdRequest.Builder().build();
        interstitialAd.loadAd(adRequest);
        countDownTimer.start();
    }

    /**
     * Método que permite cargar la publicidad
     * */
    private void verPublicidad() {
        if (interstitialAd != null && interstitialAd.isLoaded()) {
            interstitialAd.show();
        } else {
            Toast.makeText(this, R.string.errorAds, Toast.LENGTH_SHORT).show();
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
     * Encargado de valirdar que el textinput no este vacio
     */
    private void mValidarTextInput(TextInputLayout textInput, String mensajeError) {
        textInput.setError(mensajeError);
        if (mensajeError == null) {
            textInput.setErrorEnabled(false);
        } else
            textInput.setErrorEnabled(true);
    }
    /**
     * Clase encargada de interpretar el tipo de entrada que va
     * a tener el gráfico respectivo, separa los valores dependiendo
     */
    private class SelecionarTipoEntrada {
        private int indice;
        private TextInputEditText txtValores;
        private float data;

        /** Constructor */
        SelecionarTipoEntrada(int indice, TextInputEditText txtValores) {
            this.indice = indice;
            this.txtValores = txtValores;
        }

        public float getData() {
            return data;
        }

        int getIndice() {
            return indice;
        }

        SelecionarTipoEntrada invoke() {
            if (tipoIngresoDatos == 1) {
                data = Float.valueOf(txtValores.getText().toString());
            } else {
                String dividirCadena = txtValores.getText().toString();
                String[] cadena = dividirCadena.split("-");
                indice = Integer.parseInt(cadena[0]);
                data = Float.valueOf(cadena[1]);
                contadorGlobal = 7;
            }
            return this;
        }
    }
}

