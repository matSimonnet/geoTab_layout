package fr.irit.geotablet_interactions.layout_t12;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;

import fr.irit.layout_t12.R;

import fr.irit.geotablet_interactions.common.MyMapView;
import fr.irit.geotablet_interactions.common.MyTTS;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		MyMapView mapView = (MyMapView) findViewById(R.id.map_view);

		// Set listener to the map view
		mapView.setOnTouchListener(new MapViewTouchListener(this));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	protected void onDestroy() {
		MyTTS.release();
		super.onDestroy();
	}

}
