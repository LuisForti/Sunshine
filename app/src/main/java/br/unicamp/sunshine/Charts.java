package br.unicamp.sunshine;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

public class Charts extends AppCompatActivity {

    Button btnHit;
    public static Button btnAbrir;
    public static TextView txtJson;
    public static TextView txtCoord;
    EditText dataInicial;
    EditText dataFinal;
    RadioButton horario;
    RadioButton diario;
    RadioButton semanal;
    RadioButton mensal;
    RadioButton anual;
    ImageView imgLux;
    WebView webView;
    double latitude, longitude;
    String ret[][];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_charts);

        Intent intent = getIntent();
        latitude = intent.getDoubleExtra("latitudeSerializable", 0);
        longitude = intent.getDoubleExtra("longitudeSerializable", 0);

        btnHit = (Button) findViewById(R.id.btnHit);
        btnAbrir = (Button) findViewById(R.id.btnAbrir);
        txtJson = (TextView) findViewById(R.id.tvJsonItem);
        txtCoord = (TextView) findViewById(R.id.tvCoord);
        dataInicial = (EditText) findViewById(R.id.edtInicial);
        dataFinal = (EditText) findViewById(R.id.edtFinal);
        horario = (RadioButton) findViewById(R.id.rdbHorario);
        diario = (RadioButton) findViewById(R.id.rdbDiario);
        semanal = (RadioButton) findViewById(R.id.rdbSemana);
        mensal = (RadioButton) findViewById(R.id.rdbMensal);
        anual = (RadioButton) findViewById(R.id.rdbAnual);
        imgLux = (ImageView) findViewById(R.id.imgLux);
        webView = (WebView) findViewById(R.id.webGrafico);

        imgLux.setImageResource(R.drawable.lux);

        btnHit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String inicio = dataInicial.getText().toString();
                String fim = dataFinal.getText().toString();
                try {
                    if(inicio.length() != 8)
                    {
                        dataInicial.setText("Type a valid date! (YYYYMMDD)");
                        dataFinal.setText("Type a valid date! (YYYYMMDD)");
                        throw new Exception("Invalid date!");
                    }
                    if(fim.length() != 8)
                    {
                        dataInicial.setText("Type a valid date! (YYYYMMDD)");
                        dataFinal.setText("Type a valid date! (YYYYMMDD)");
                        throw new Exception("Invalid date!");
                    }
                    if(Integer.parseInt(inicio) > Integer.parseInt(fim))
                    {
                        dataInicial.setText("Inicial date further than final date!");
                        throw new Exception("Invalid date!");
                    }
                    FetchData process = new FetchData();
                    if(horario.isChecked()) {
                        process.execute("https://power.larc.nasa.gov/api/temporal/hourly/point?latitude=" + latitude + "&longitude=" + longitude + "&community=sb&parameters=DIRECT_ILLUMINANCE&start=" + inicio + "&end=" + fim, "h");
                    }
                    else if (diario.isChecked())
                    {
                        process.execute("https://power.larc.nasa.gov/api/temporal/daily/point?latitude=" + latitude + "&longitude=" + longitude + "&community=sb&parameters=DIRECT_ILLUMINANCE&start=" + inicio + "&end=" + fim, "d");
                    }
                    else if (semanal.isChecked())
                    {
                        if(Integer.parseInt(fim) - Integer.parseInt(inicio) < 7)
                        {
                            dataInicial.setText("Less then 1 week between the two dates!");
                            throw new Exception("Invalid date!");
                        }
                        process.execute("https://power.larc.nasa.gov/api/temporal/daily/point?latitude=" + latitude + "&longitude=" + longitude + "&community=sb&parameters=DIRECT_ILLUMINANCE&start=" + inicio + "&end=" + fim, "w");
                    }
                    else if (mensal.isChecked())
                    {
                        if(Integer.parseInt(fim) - Integer.parseInt(inicio) < 30)
                        {
                            dataInicial.setText("Less then 30 days between the two dates!");
                            throw new Exception("Invalid date!");
                        }
                        process.execute("https://power.larc.nasa.gov/api/temporal/daily/point?latitude=" + latitude + "&longitude=" + longitude + "&community=sb&parameters=DIRECT_ILLUMINANCE&start=" + inicio + "&end=" + fim, "m");
                    }
                    else if (anual.isChecked())
                    {
                        if(Integer.parseInt(fim) - Integer.parseInt(inicio) < 365)
                        {
                            dataInicial.setText("Less then 365 days between the two dates!");
                            throw new Exception("Invalid date!");
                        }
                        process.execute("https://power.larc.nasa.gov/api/temporal/daily/point?latitude=" + latitude + "&longitude=" + longitude + "&community=sb&parameters=DIRECT_ILLUMINANCE&start=" + inicio + "&end=" + fim, "a");
                    }
                }
                catch (Exception err)
                {}
            }
        });

        btnAbrir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (txtJson.getText().toString().length() != 0) {
                    String jSon = txtJson.getText().toString();
                    int quantosDados = 0;
                    while (!jSon.equals("")) {
                        quantosDados++;
                        jSon = jSon.substring(jSon.indexOf('\n') + 1);
                    }

                    ret = new String[quantosDados][2];
                    jSon = txtJson.getText().toString();
                    int posicaoAtual = 0;

                    while (!jSon.equals("")) {
                        String data = jSon.substring(0, jSon.indexOf(":"));
                        String luminosidade = jSon.substring(jSon.indexOf(":") + 1, jSon.indexOf('\n'));
                        jSon = jSon.substring(jSon.indexOf('\n') + 1);
                        ret[posicaoAtual][0] = data;
                        ret[posicaoAtual][1] = luminosidade;
                        posicaoAtual++;
                    }

                    webView.addJavascriptInterface(new WebAppInterface(), "Android");

                    webView.getSettings().setJavaScriptEnabled(true);
                    webView.loadUrl("file:///android_asset/chart.html");

                    txtJson.setText("");
                }
            }
        });
    }
    public class WebAppInterface {

        @JavascriptInterface
        public String getCoord() {
            return txtCoord.getText().toString();
        }

        @JavascriptInterface
        public int getQuantidade() {
            return ret.length;
        }

        @JavascriptInterface
        public String getDados(int numeroString, int numeroValor) {
            return ret[numeroString][numeroValor];
        }
    }
}