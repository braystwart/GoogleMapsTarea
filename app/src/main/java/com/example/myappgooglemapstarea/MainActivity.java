package com.example.googlemapstarea;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FetchPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMapClickListener {

    private GoogleMap mMap;
    private PlacesClient placesClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inicializa la API de Google Places
        Places.initialize(getApplicationContext(), "AQUÍ_PEGA_TU_CLAVE_DE_API");

        // Crea una instancia de PlacesClient
        placesClient = Places.createClient(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.getUiSettings().setZoomControlsEnabled(true);

        // Mover el mapa a una ubicación inicial (por ejemplo, Quevedo)
        LatLng quevedo = new LatLng(-1.0113558476088707, -79.46938086135764);
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(quevedo, 15);
        mMap.moveCamera(cameraUpdate);

        mMap.setOnMapClickListener(this);

        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                // Crear una vista personalizada para el cuadro de información del marcador
                View infoView = getLayoutInflater().inflate(R.layout.custom_info_window, null);

                // Obtén el ID del lugar desde el marcador (si lo tienes)
                String placeId = marker.getSnippet();

                if (placeId != null) {
                    // Configura la solicitud para obtener detalles del lugar
                    List<Place.Field> placeFields = Arrays.asList(Place.Field.NAME, Place.Field.ADDRESS);
                    FetchPlaceRequest request = FetchPlaceRequest.newInstance(placeId, placeFields);

                    // Realiza la solicitud a la API de Google Places
                    placesClient.fetchPlace(request).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            FetchPlaceResponse response = task.getResult();
                            Place place = response.getPlace();

                            // Actualiza la vista del cuadro de información con los datos del lugar
                            TextView titleTextView = infoView.findViewById(R.id.text_title);
                            TextView descriptionTextView = infoView.findViewById(R.id.text_description);

                            titleTextView.setText(place.getName());
                            descriptionTextView.setText(place.getAddress());
                        }
                    });
                }

                return infoView;
            }
        });

        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                // Manejar el evento de clic en el cuadro de información del marcador
                // Por ejemplo, abrir una actividad de detalle del lugar
            }
        });
    }

    @Override
    public void onMapClick(@NonNull LatLng latLng) {
        // Agregar un marcador al lugar donde se hizo clic
        Marker marker = mMap.addMarker(new MarkerOptions()
                .position(latLng)
                .title("Título del Lugar")
                .snippet("AQUÍ_PUEDES_PONER_EL_PLACE_ID_DEL_LUGAR"));

        // Mostrar el cuadro de información del marcador
        marker.showInfoWindow();
    }
}