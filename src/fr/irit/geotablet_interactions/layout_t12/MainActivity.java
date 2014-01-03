package fr.irit.geotablet_interactions.layout_t12;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import fr.irit.layout_t12.R;
import fr.irit.geotablet_interactions.common.MyMapView;
import fr.irit.geotablet_interactions.common.MyTTS;

public class MainActivity extends Activity {
	
	public MyMapView mapView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
    	//set Full screen landscape
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		
		//hide actionBar (up) -> does not work on galaxyTab 10.1
		//requestWindowFeature(Window.FEATURE_NO_TITLE);
				
		//hide menuBar (bottom)
		//this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
		getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);

		mapView = (MyMapView) findViewById(R.id.map_view);

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
