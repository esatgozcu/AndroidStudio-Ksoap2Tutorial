package com.example.esatgozcu.androidstudioksoap2tutorial;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    EditText tcText;
    EditText nameText;
    EditText surnameText;
    EditText yearText;

    private static final String NAMESPACE = "http://tckimlik.nvi.gov.tr/WS";
    private static final String URL = "https://tckimlik.nvi.gov.tr/Service/KPSPublic.asmx";
    private static final String SOAP_ACTION = "http://tckimlik.nvi.gov.tr/WS/TCKimlikNoDogrula";
    private static final String METHOD_NAME = "TCKimlikNoDogrula";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tcText = (EditText)findViewById(R.id.tcText);
        nameText=(EditText)findViewById(R.id.nameText);
        surnameText = (EditText)findViewById(R.id.surnameText);
        yearText = (EditText)findViewById(R.id.yearText);

    }
    public void button (View view)
    {
        // Bir metin içerisindeki boşluklardan kurtulmak için trim metodu kullanılır.
        // trim() metodu, metnin sonunda ve başında yer alan boşlukları yok ederken
        // kelime aralarındaki boşluklara dokunmaz.
        String tcString = tcText.getText().toString().trim();
        // Bir büyük harflere çevirmek için toUpperCase() methodu kullanılır.
        // Uygulamada verilerin isim ve soyisimin bütün harfleri büyük olması gerekiyor.
        String nameString = nameText.getText().toString().trim().toUpperCase(new Locale("tr_TR"));
        String surnameString = surnameText.getText().toString().trim().toUpperCase(new Locale("tr_TR"));
        String yearString = yearText.getText().toString().trim();

        new asynTask().execute(tcString,nameString,surnameString,yearString);
    }

    private class asynTask extends AsyncTask<String,Void,Void>{

        String resultText;
        Boolean result;
        ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        AlertDialog alert;

        // Doğrula butonuna basıldığında ilk yapılacaklar..
        @Override
        protected void onPreExecute() {

            // ProgressDialog oluşturuyoruz.
            progressDialog.setMessage("Kontrol ediliyor...");
            progressDialog.show();
        }
        // Doğrula butonuna basıldığında arka planda yapılacaklar..
        @Override
        protected Void doInBackground(String... strings) {

            // Sorgumuzu oluşturuyoruz..
            SoapObject Request = new SoapObject(NAMESPACE, METHOD_NAME);
            Request.addProperty("TCKimlikNo",strings[0]);
            Request.addProperty("Ad", strings[1]);
            Request.addProperty("Soyad", strings[2]);
            Request.addProperty("DogumYili", strings[3]);

            // SoapEnvelope oluşturduk ve Soap 1.1 kullanacağımız belirttik.
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.dotNet = true;
            // Envelope ile requesti birbiri ile bağladık.
            envelope.setOutputSoapObject(Request);
            HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);

            try
            {
                // Web servisi çağırdık
                androidHttpTransport.call(SOAP_ACTION, envelope);
                // Gelen verileri değerlendirmek için objemizi oluşturuyoruz.
                SoapObject response = (SoapObject) envelope.bodyIn;
                result = Boolean.parseBoolean(response.getProperty(0).toString());
                // Gelen sonucu değerlendiriyoruz.
                if(result) {
                    resultText ="Sisteme Kayıtlı Kullanıcı Bulundu !";
                }else{
                    resultText = "Sisteme Kayıtlı Kullanıcı Bulunamadı !";
                }
            }
            catch(Exception e){
                e.printStackTrace();
            }
            return null;
        }
        // Arka plan işlemleri bittikten sonra yapılacaklar..
        @Override
        protected void onPostExecute(Void aVoid) {

            // ProgressDialog kapatılıyor.
            progressDialog.dismiss();
            // AlertDialog'u gösteriyoruz.
            alert = builder.setMessage(resultText)
                    .setTitle("Sonuç")
                    .setCancelable(true)
                    .setPositiveButton("Tamam", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                        }
                    }).create();
            alert.show();
        }
    }
}
