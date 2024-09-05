package com.example.loactionapplication

import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.common.api.Status
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener

class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    private var mGoogleMap:GoogleMap? = null
    private lateinit var autocompleteFragment: AutocompleteSupportFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        Places.initialize(applicationContext,getString(R.string.Google_map_api_key))
        autocompleteFragment = supportFragmentManager.findFragmentById(R.id.autoComplete_Search)
                as AutocompleteSupportFragment
        autocompleteFragment.setPlaceFields(listOf(Place.Field.ID, Place.Field.ADDRESS, Place.Field.LAT_LNG))
        autocompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onError(p0: Status) {
                Toast.makeText(this@MainActivity,"Some error an search",Toast.LENGTH_SHORT).show()
            }
            override fun onPlaceSelected(place: Place) {
                val add = place.address
                val id = place.id
                val latlng = place.latLng!!
                val marker = addMarker(latlng)
                marker.title = add
                marker.snippet = id
                zoomOnMap(latlng)
            }
        })
        val mapFragment = supportFragmentManager.findFragmentById(R.id.mapFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)
        val mapOptionButton : ImageButton = findViewById(R.id.mapOptionMenu)
        val popupMenu = PopupMenu(this, mapOptionButton)
        popupMenu.menuInflater.inflate(R.menu.map_option, popupMenu.menu)
        popupMenu.setOnMenuItemClickListener{ menuItem ->
            changeMap(menuItem.itemId)
            true
        }
        mapOptionButton.setOnClickListener{
            Toast.makeText(applicationContext,"Button clicked",Toast.LENGTH_SHORT).show()
            popupMenu.show()
        }
    }
    private fun zoomOnMap(latLng: LatLng) {
        Log.d("ZoomIn camera","ZoomOnMapCamera")
        val newlatlngZoom = CameraUpdateFactory.newLatLngZoom(latLng,12f) //----->12f is used for ZoomIn (or) focus in the map
        mGoogleMap?.animateCamera(newlatlngZoom)
    }

    //map type or map mode
    private fun changeMap(itemId: Int) {
        Log.d("ChangeMap","MapType")
        when(itemId) {
            R.id.normal_map -> mGoogleMap?.mapType = GoogleMap.MAP_TYPE_NORMAL
            R.id.hybrid_map -> mGoogleMap?.mapType = GoogleMap.MAP_TYPE_HYBRID
            R.id.satellite_map -> mGoogleMap?.mapType = GoogleMap.MAP_TYPE_SATELLITE
            R.id.terrain_map -> mGoogleMap?.mapType = GoogleMap.MAP_TYPE_TERRAIN
        }
    }
    override fun onMapReady(googleMap: GoogleMap)
    {
        mGoogleMap = googleMap

        //normal maker position or location
        addMarker(LatLng(13.0429707, 80.2740429)) //Current location latlng

        //draggable maker position or location
        draggableMarker(LatLng(20.5937, 78.9629)) //Random location latlng

        mGoogleMap?.setOnMapClickListener {
            mGoogleMap?.clear()
            addMarker(it)
        }

        mGoogleMap?.setOnMarkerClickListener {
            it.remove()
            false
        }
    }
    private fun addMarker(position:LatLng):Marker
    {
        Log.d("Marker","addMarker")
        val marker = mGoogleMap?.addMarker(MarkerOptions()
            .position(position)
            .title("Normal marker")
        )
        return marker!!
    }
    private fun draggableMarker(position: LatLng)
    {
        mGoogleMap?.addMarker(MarkerOptions()
            .position(position)
            .title("Draggable marker")
            .draggable(true))
    }
}