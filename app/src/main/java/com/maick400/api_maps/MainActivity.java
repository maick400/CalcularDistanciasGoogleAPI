package com.maick400.api_maps;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import WebService.Asynchtask;
import WebService.WebService;


public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMapClickListener, Asynchtask {
    GoogleMap map;
    EditText txtMensaje;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtMensaje =  (EditText) FindElementById(R.id.txtMensaje);
        SupportMapFragment mapFragment = (SupportMapFragment)
                getSupportFragmentManager()
                        .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        map = googleMap;

        map.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        map.getUiSettings().setZoomControlsEnabled(true);

        //LatLng madrid = new LatLng(40.417325, -3.683081);
        //CameraPosition camPos = new CameraPosition.Builder()
        //        .target(madrid)
        //        .zoom(19)
        //        .bearing(60) //noreste arriba
        //        .tilt(50) //punto de vista de la cámara 70 grados
        //        .build();
        //CameraUpdate camUpd3 =
        //        CameraUpdateFactory.newCameraPosition(camPos);
        //map.animateCamera(camUpd3);
        map.setOnMapClickListener((this));


        CameraUpdate camUpd1 =
                CameraUpdateFactory
                        .newLatLngZoom(new LatLng(40.689338858667234, -74.04453258929587), 18);
        map.moveCamera(camUpd1);
    }

    PolylineOptions lineas = new PolylineOptions();
    ArrayList<MarkerOptions> markers = new ArrayList();
    ArrayList<LatLng> puntos  = new ArrayList<LatLng>();

    float sum = 0;

    @Override
    public void onMapClick(@NonNull LatLng latLng) {
        LatLng punto = new LatLng(latLng.latitude, latLng.longitude);
        puntos.add(punto);
        MarkerOptions marcador = new MarkerOptions().position(punto).title("Marker in Sydney");
        map.addMarker(marcador);

        lineas.width(8);
        lineas.color(Color.RED);

        markers.add(marcador);
        lineas.add(punto);

        if (markers.size() == 5) {
            puntos.add(puntos.get(0));
            lineas.add(markers.get(0).getPosition());
            map.addPolyline(lineas);
            //for (int i=0 ; i< puntos.size();  i ++){
            //    if (i > 0 ){
            //        medirDistancia( puntos.get(i-1) ,puntos.get(i));
            //    }
            //}
            txtMensaje.setText("La distancia de los puntos selccionados es "+ sum + " Km");
            Toast.makeText(this.getApplicationContext(), sum+"" , Toast.LENGTH_LONG).show();
        }

    }

    public void medirDistancia(LatLng origen, LatLng destino) {
        //Toast.makeText(this.getApplicationContext(), origen.latitude, Toast.LENGTH_LONG).show();

        Map<String, String> datos = new HashMap<String, String>();
        WebService ws = new WebService(

                "https://maps.googleapis.com/maps/api/distancematrix/json?" +
                        "destinations=" + origen.latitude + ","+origen.longitude +
                        "&origins=" +destino.latitude + ","+destino.longitude +
                        "&units=" + "imperial" +
                        "&key=AIzaSyDMmRXHBYOjJyXZruXemR11tl7uiJ2T_Q8",
                datos, MainActivity.this, MainActivity.this);
        ws.execute("GET");

    }

    @Override
    public void processFinish(String result) throws JSONException {
        String distancia = "";
        JSONObject jsonOj = new JSONObject(result);
        JSONArray jsonRow = jsonOj.getJSONArray("rows");
        JSONObject jsonOjElementos = jsonRow.getJSONObject(0);
        JSONArray jsonElement = jsonOjElementos.getJSONArray("elements");

        for (int i = 0; i < jsonElement.length(); i++) {
            JSONObject Jdistancia = jsonElement.getJSONObject(i);
            JSONObject jsonOjValue = Jdistancia.getJSONObject("distance");
            distancia =  jsonOjValue.getString("value");
        }
        sum +=   Float.parseFloat(distancia);
        Toast.makeText(this.getApplicationContext(), distancia, Toast.LENGTH_LONG).show();
    }
}