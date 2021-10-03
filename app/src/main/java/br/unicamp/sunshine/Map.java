package br.unicamp.sunshine;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.Serializable;

import br.unicamp.sunshine.databinding.ActivityMapBinding;

public class Map extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMapBinding binding;
    private Button btnCharts;
    LatLng coordenadas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        btnCharts = (Button) findViewById(R.id.btnCharts);
        mapFragment.getMapAsync(this);
        btnCharts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                double latitude = coordenadas.latitude;
                double longitude = coordenadas.longitude;
                Intent intent=  new Intent(Map.this, Charts.class);
                intent.putExtra("latitudeSerializable", (Serializable) latitude);
                intent.putExtra("longitudeSerializable", (Serializable) longitude);
                startActivity(intent);
            }
        });
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        // Add a marker in Sydney and move the camera
        LatLng campinas = new LatLng(-22.906842, -47.056663);
        mMap.addMarker(new MarkerOptions().position(campinas).title("Marker in Campinas"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(campinas));
        MarkerOptions mo = new MarkerOptions();
        coordenadas = campinas;
        mMap .setOnMapClickListener( new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick (LatLng latLng) {
                //função clear
                mMap.clear();
                MarkerOptions options = new MarkerOptions() ;
                options.position( latLng ) ;
                mMap .addMarker( options ) ;
                mo.position(options.getPosition());
                Toast.makeText(getBaseContext(), ""+mo.getPosition(), Toast.LENGTH_SHORT).show();
                //mMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)).title("Marker"));
                coordenadas = mo.getPosition();
            }
        });

    }


}