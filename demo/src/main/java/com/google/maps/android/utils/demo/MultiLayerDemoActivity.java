/*
 * Copyright 2020 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.maps.android.utils.demo;

import com.huawei.hms.maps.CameraUpdateFactory;
import com.huawei.hms.maps.GoogleMap;
import com.huawei.hms.maps.model.BitmapDescriptorFactory;
import com.huawei.hms.maps.model.LatLng;
import com.huawei.hms.maps.model.Marker;
import com.huawei.hms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.collections.GroundOverlayManager;
import com.google.maps.android.collections.MarkerManager;
import com.google.maps.android.collections.PolygonManager;
import com.google.maps.android.collections.PolylineManager;
import com.google.maps.android.data.Feature;
import com.google.maps.android.data.geojson.GeoJsonFeature;
import com.google.maps.android.data.geojson.GeoJsonLayer;
import com.google.maps.android.data.geojson.GeoJsonLineStringStyle;
import com.google.maps.android.data.geojson.GeoJsonPolygonStyle;
import com.google.maps.android.data.kml.KmlLayer;
import com.google.maps.android.utils.demo.model.MyItem;

import org.json.JSONException;
import org.xmlpull.v1.XmlPullParserException;

import android.graphics.Color;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Activity that adds multiple layers on the same map. This helps ensure that layers don't
 * interfere with one another.
 */
public class MultiLayerDemoActivity extends BaseDemoActivity {
    public final static String TAG = "MultiDemo";

    @Override
    protected void startDemo(boolean isRestore) {
        if (!isRestore) {
            getMap().moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(51.403186, -0.126446), 10));
        }

        // Shared object managers - used to support multiple layer types on the map simultaneously
        // [START maps_multilayer_demo_init1]
        MarkerManager markerManager = new MarkerManager(getMap());
        GroundOverlayManager groundOverlayManager = new GroundOverlayManager(getMap());
        PolygonManager polygonManager = new PolygonManager(getMap());
        PolylineManager polylineManager = new PolylineManager(getMap());
        // [END maps_multilayer_demo_init1]

        // Add clustering
        // [START maps_multilayer_demo_init2]
        ClusterManager<MyItem> clusterManager = new ClusterManager<>(this, getMap(), markerManager);
        // [END maps_multilayer_demo_init2]
        getMap().setOnCameraIdleListener(clusterManager);
        addClusterItems(clusterManager);

        // Add GeoJSON from resource
        try {
            // GeoJSON polyline
            // [START maps_multilayer_demo_init3]
            GeoJsonLayer geoJsonLineLayer = new GeoJsonLayer(getMap(), R.raw.south_london_line_geojson, this, markerManager, polygonManager, polylineManager, groundOverlayManager);
            // [END maps_multilayer_demo_init3]
            // Make the line red
            GeoJsonLineStringStyle geoJsonLineStringStyle = new GeoJsonLineStringStyle();
            geoJsonLineStringStyle.setColor(Color.RED);
            for (GeoJsonFeature f : geoJsonLineLayer.getFeatures()) {
                f.setLineStringStyle(geoJsonLineStringStyle);
            }
            geoJsonLineLayer.addLayerToMap();
            geoJsonLineLayer.setOnFeatureClickListener(new GeoJsonLayer.GeoJsonOnFeatureClickListener() {
                @Override
                public void onFeatureClick(Feature feature) {
                    Toast.makeText(MultiLayerDemoActivity.this,
                            "GeoJSON polyline clicked: " + feature.getProperty("title"),
                            Toast.LENGTH_SHORT).show();
                }

            });

            // GeoJSON polygon
            GeoJsonLayer geoJsonPolygonLayer = new GeoJsonLayer(getMap(), R.raw.south_london_square_geojson, this, markerManager, polygonManager, polylineManager, groundOverlayManager);
            // Fill it with red
            GeoJsonPolygonStyle geoJsonPolygonStyle = new GeoJsonPolygonStyle();
            geoJsonPolygonStyle.setFillColor(Color.RED);
            for (GeoJsonFeature f : geoJsonPolygonLayer.getFeatures()) {
                f.setPolygonStyle(geoJsonPolygonStyle);
            }
            geoJsonPolygonLayer.addLayerToMap();
            geoJsonPolygonLayer.setOnFeatureClickListener(new GeoJsonLayer.GeoJsonOnFeatureClickListener() {
                @Override
                public void onFeatureClick(Feature feature) {
                    Toast.makeText(MultiLayerDemoActivity.this,
                            "GeoJSON polygon clicked: " + feature.getProperty("title"),
                            Toast.LENGTH_SHORT).show();
                }

            });
        } catch (IOException e) {
            Log.e(TAG, "GeoJSON file could not be read");
        } catch (JSONException e) {
            Log.e(TAG, "GeoJSON file could not be converted to a JSONObject");
        }

        // Add KMLs from resources
        try {
            // KML Polyline
            // [START maps_multilayer_demo_init4]
            KmlLayer kmlPolylineLayer = new KmlLayer(getMap(), R.raw.south_london_line_kml, this, markerManager, polygonManager, polylineManager, groundOverlayManager, null);
            // [END maps_multilayer_demo_init4]
            // [START maps_multilayer_demo_init6]
            kmlPolylineLayer.addLayerToMap();
            kmlPolylineLayer.setOnFeatureClickListener(new KmlLayer.OnFeatureClickListener() {
                @Override
                public void onFeatureClick(Feature feature) {
                    Toast.makeText(MultiLayerDemoActivity.this,
                            "KML polyline clicked: " + feature.getProperty("name"),
                            Toast.LENGTH_SHORT).show();
                }
            });
            // [END maps_multilayer_demo_init6]

            // KML Polygon
            KmlLayer kmlPolygonLayer = new KmlLayer(getMap(), R.raw.south_london_square_kml, this, markerManager, polygonManager, polylineManager, groundOverlayManager, null);
            kmlPolygonLayer.addLayerToMap();
            kmlPolygonLayer.setOnFeatureClickListener(new KmlLayer.OnFeatureClickListener() {
                @Override
                public void onFeatureClick(Feature feature) {
                    Toast.makeText(MultiLayerDemoActivity.this,
                            "KML polygon clicked: " + feature.getProperty("name"),
                            Toast.LENGTH_SHORT).show();
                }
            });
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Unclustered marker - instead of adding to the map directly, use the MarkerManager
        // [START maps_multilayer_demo_init5]
        MarkerManager.Collection markerCollection = markerManager.newCollection();
        markerCollection.addMarker(new MarkerOptions()
                .position(new LatLng( 51.150000, -0.150032))
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                .title("Unclustered marker"));
        // [END maps_multilayer_demo_init5]
        // [START maps_multilayer_demo_init7]
        markerCollection.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Toast.makeText(MultiLayerDemoActivity.this,
                        "Marker clicked: " + marker.getTitle(),
                        Toast.LENGTH_SHORT).show();
                return false;
            }
        });
        // [END maps_multilayer_demo_init7]
    }

    private void addClusterItems(ClusterManager clusterManager) {
        InputStream inputStream = getResources().openRawResource(R.raw.radar_search);
        List<MyItem> items;
        try {
            items = new MyItemReader().read(inputStream);
            clusterManager.addItems(items);
        } catch (JSONException e) {
            Toast.makeText(this, "Problem reading list of markers.", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }
}
