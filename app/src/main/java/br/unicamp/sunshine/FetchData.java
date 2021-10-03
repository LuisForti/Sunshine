package br.unicamp.sunshine;


import android.os.AsyncTask;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;

public class FetchData extends AsyncTask<String, Void, Void> {

    String resultado;
    String textoFormatado = "";
    String coord = "";
    String quantidadeDeTempo;

    @Override
    protected Void doInBackground(String... params) {
        try {
            URL url = new URL(params[0]);

            quantidadeDeTempo = params[1];

            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

            InputStream inputStream = httpURLConnection.getInputStream();

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            String line = "";

            while (line != null)
            {
                line = bufferedReader.readLine();
                resultado += line;
            }
        } catch (Exception error) {
            System.err.println(error);
        }

        int posicaoInicial = resultado.indexOf("DIRECT_ILLUMINANCE") + 20;
        int posicaoFinal = resultado.indexOf("}}}") + 1;
        String textoSemiFormatado = resultado.substring(posicaoInicial, posicaoFinal);
        String tempo = params[0].toString();
        tempo = tempo.substring(41, 47);

        try {
            String json = textoSemiFormatado;
            JSONObject jObject = new JSONObject(json.trim());
            Iterator<?> keys = jObject.keys();

            String data = "";


            if(quantidadeDeTempo.equals("h"))
            {
                while (keys.hasNext()) {
                    String key = (String) keys.next();
                    Object objetoAtual = jObject.get(key);

                    if ((Double) objetoAtual == (-999)) {
                        objetoAtual = 0;
                    }

                    data = key.substring(4, 6);
                    data += "/";
                    data += key.substring(6, 8);
                    data += "/";
                    data += key.substring(0, 4);
                    data += " ";
                    data += key.substring(8, 10);
                    data += "h";


                    textoFormatado += data + ": " + objetoAtual + "\n";
                }
            }
            else if(quantidadeDeTempo.equals("d"))
            {
                while (keys.hasNext()) {
                    String key = (String) keys.next();
                    Object objetoAtual = jObject.get(key);

                    if ((Double) objetoAtual == (-999)) {
                        objetoAtual = 0;
                    }

                    data = key.substring(4, 6);
                    data += "/";
                    data += key.substring(6, 8);
                    data += "/";
                    data += key.substring(0, 4);
                    data += " ";

                    textoFormatado += data + ": " + objetoAtual + "\n";
                }
            }
            else if(quantidadeDeTempo.equals("w"))
            {
                double valorTotal = 0;
                int quantosDias = 0;

                while (keys.hasNext()) {
                    String key = (String) keys.next();
                    Object objetoAtual = jObject.get(key);

                    if ((Double) objetoAtual == (-999)) {
                        objetoAtual = 0;
                    }

                    data = key.substring(4, 6);
                    data += "/";
                    data += key.substring(6, 8);
                    data += "/";
                    data += key.substring(0, 4);
                    data += " ";

                    valorTotal += Double.parseDouble(objetoAtual.toString());
                    quantosDias++;

                    if(quantosDias == 7) {
                        textoFormatado += data + ": " + valorTotal/7 + "\n";
                        quantosDias = 0;
                        valorTotal = 0;
                    }
                }
            }
            else if(quantidadeDeTempo.equals("m"))
            {
                double valorTotal = 0;
                int quantosDias = 0;

                while (keys.hasNext()) {
                    String key = (String) keys.next();
                    Object objetoAtual = jObject.get(key);

                    if ((Double) objetoAtual == (-999)) {
                        objetoAtual = 0;
                    }

                    data = key.substring(4, 6);
                    data += "/";
                    data += key.substring(6, 8);
                    data += "/";
                    data += key.substring(0, 4);
                    data += " ";

                    valorTotal += Double.parseDouble(objetoAtual.toString());
                    quantosDias++;

                    if(quantosDias == 30) {
                        textoFormatado += data + ": " + valorTotal/30 + "\n";
                        quantosDias = 0;
                        valorTotal = 0;
                    }
                }
            }
            else
            {
                double valorTotal = 0;
                int quantosDias = 0;

                while (keys.hasNext()) {
                    String key = (String) keys.next();
                    Object objetoAtual = jObject.get(key);

                    if ((Double) objetoAtual == (-999)) {
                        objetoAtual = 0;
                    }

                    data = key.substring(4, 6);
                    data += "/";
                    data += key.substring(6, 8);
                    data += "/";
                    data += key.substring(0, 4);
                    data += " ";

                    valorTotal += Double.parseDouble(objetoAtual.toString());
                    quantosDias++;

                    if(quantosDias == 365) {
                        textoFormatado += data + ": " + valorTotal/365 + "\n";
                        quantosDias = 0;
                        valorTotal = 0;
                    }
                }
            }
        }
        catch (Exception err)
        {
            System.out.println(err);
        }

        posicaoInicial = 64;
        posicaoFinal = resultado.indexOf("]}");
        resultado = resultado.substring(posicaoInicial, posicaoFinal);
        coord = "Longitude " + resultado.substring(0, resultado.indexOf(','));
        resultado = resultado.substring(resultado.indexOf(',')+1);
        coord += "   Latitude " + resultado.substring(0, resultado.indexOf(','));

        return null;
    }

    @Override
    protected void onPostExecute(Void unused) {
        super.onPostExecute(unused);
        Charts.txtJson.setText(this.textoFormatado);
        Charts.txtCoord.setText(this.coord);
        Charts.btnAbrir.performClick();
    }
}
