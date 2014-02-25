package fr.irit.geotablet_interactions.layout_t12;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Set;

import org.osmdroid.util.BoundingBoxE6;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.MapView.Projection;

import android.content.Context;
import android.os.Environment;
import android.os.Vibrator;
import android.speech.tts.TextToSpeech;
import android.support.v4.view.MotionEventCompat;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import fr.irit.geotablet_interactions.common.MyMapView;
import fr.irit.geotablet_interactions.common.MyTTS;
import fr.irit.geotablet_interactions.common.OsmNode;
import fr.irit.layout_t12.R;

/**
 * Listener to guide user toward the last selected item when finger(s) on the
 * map view
 * 
 * @author helene jonin
 * @mail helene.jonin@gmail.com
 * 
 */
public class MapViewTouchListener implements OnTouchListener {
	private static final int BOUND_WIDTH = 24;
	private static final int TARGET_SIZE = 120; // Touch target size for on screen elements

	private static final int INVALID_POINTER_ID = -1;

	private Context context;
	private int activePointerId;
	private int lastArea;
	//private String currentPoint; //for old Mathieu modification code
	//private String lastPoint;

	//for logging
	private PrintWriter output;
	private Date myDate;
	private boolean firstTouch = true;
	private String logContact = "";
	private String logAnnounce = "";
	private String logArea = "";
	
	/**
	 * Constructor
	 * 
	 * @param context
	 *            The context
	 */
	public MapViewTouchListener(Context context) {
		super();
		this.context = context;
		this.lastArea = -1;
		//this.lastPoint = "nothing"; //for old Mathieu modification code
		// for logging
		myDate = new Date();
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd-HHmmss",Locale.getDefault()); 
		new File(Environment.getExternalStorageDirectory().getAbsolutePath()+ "/geoTablet/").mkdir();
		String logFilename = simpleDateFormat.format(new Date())+ "_layout" +".csv";
		File logFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+ "/geoTablet/" + logFilename);
		    try {
		      output = new PrintWriter(new FileWriter(logFile));
		    } catch (IOException e) {
		      e.printStackTrace();
		    }
	}

	@Override
	public boolean onTouch(View v, MotionEvent ev) {
		int action = MotionEventCompat.getActionMasked(ev);

		switch (action) {
		
		case MotionEvent.ACTION_DOWN: {
			// Save the ID of this pointer (for dragging)
			activePointerId = MotionEventCompat.getPointerId(ev, 0);
			break;
		}

		case MotionEvent.ACTION_MOVE: {
			// Find the index of the active pointer and fetch its position
			int pointerIndex = MotionEventCompat.findPointerIndex(ev, activePointerId);
			float y = MotionEventCompat.getY(ev, pointerIndex);
			float x = MotionEventCompat.getX(ev, pointerIndex);
			// Vibrate when crossing a bound
			if ((y <= BOUND_WIDTH) // Screen edges (bottom)
					|| (y >= v.getHeight() - BOUND_WIDTH) // Screen edges (top)
					|| (x <= BOUND_WIDTH) // Screen edges (left)
					|| (x >= v.getWidth() - BOUND_WIDTH) // Screen edges (right)
					|| ((y <= v.getHeight() / 4 + BOUND_WIDTH) && (y >= v.getHeight() / 4 - BOUND_WIDTH))
					|| ((y <= v.getHeight() / 2 + BOUND_WIDTH) && (y >= v.getHeight() / 2 - BOUND_WIDTH))
					|| ((y <= 3 * v.getHeight() / 4 + BOUND_WIDTH) && (y >= 3 * v.getHeight() / 4 - BOUND_WIDTH))
					|| ((x <= v.getWidth() / 3 + BOUND_WIDTH) && (x >= v.getWidth() / 3 - BOUND_WIDTH))
					|| ((x <= 2 * v.getWidth() / 3 + BOUND_WIDTH) && (x >= 2 * v.getWidth() / 3 - BOUND_WIDTH))) {
				((Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE)).vibrate(300);
			}
			// Look for the touched area
			Projection projection = ((MapView) v).getProjection();
			GeoPoint gpTopLeft = null;
			GeoPoint gpBottomRight = null;
			int area = -1;
			if ((y >= 0 + BOUND_WIDTH) && (y < v.getHeight() / 4 - BOUND_WIDTH)) {
				if ((x >= 0 + BOUND_WIDTH) && (x < v.getWidth() / 3 - BOUND_WIDTH)) { // Area 1
					gpTopLeft = (GeoPoint) projection.fromPixels(0, 0);
					gpBottomRight = (GeoPoint) projection.fromPixels(v.getWidth() / 3, v.getHeight() / 4);
					area = 1;
				}
				if ((x > v.getWidth() / 3 + BOUND_WIDTH) && (x < 2 * v.getWidth() / 3 - BOUND_WIDTH)) { // Area 2
					gpTopLeft = (GeoPoint) projection.fromPixels(v.getWidth() / 3, 0);
					gpBottomRight = (GeoPoint) projection.fromPixels(2 * v.getWidth() / 3, v.getHeight() / 4);
					area = 2;
				}
				if ((x > 2 * v.getWidth() / 3 + BOUND_WIDTH) && (x <= v.getWidth() - BOUND_WIDTH)) { // Area 3
					gpTopLeft = (GeoPoint) projection.fromPixels(2 * v.getWidth() / 3, 0);
					gpBottomRight = (GeoPoint) projection.fromPixels(v.getWidth(), v.getHeight() / 4);
					area = 3;
				}
			}
			if ((y > v.getHeight() / 4 + BOUND_WIDTH) && (y < v.getHeight() / 2 - BOUND_WIDTH)) {
				if ((x >= 0 + BOUND_WIDTH) && (x < v.getWidth() / 3 - BOUND_WIDTH)) { // Area 4
					gpTopLeft = (GeoPoint) projection.fromPixels(0, v.getHeight() / 4);
					gpBottomRight = (GeoPoint) projection.fromPixels(v.getWidth() / 3, v.getHeight() / 2);
					area = 4;
				}
				if ((x > v.getWidth() / 3 + BOUND_WIDTH) && (x < 2 * v.getWidth() / 3 - BOUND_WIDTH)) { // Area 5
					gpTopLeft = (GeoPoint) projection.fromPixels(v.getWidth() / 3, v.getHeight() / 4);
					gpBottomRight = (GeoPoint) projection.fromPixels(2 * v.getWidth() / 3, v.getHeight() / 2);
					area = 5;
				}
				if ((x > 2 * v.getWidth() / 3 + BOUND_WIDTH) && (x <= v.getWidth() - BOUND_WIDTH)) { // Area 6
					gpTopLeft = (GeoPoint) projection.fromPixels(2 * v.getWidth() / 3, v.getHeight() / 4);
					gpBottomRight = (GeoPoint) projection.fromPixels(v.getWidth(), v.getHeight() / 2);
					area = 6;
				}
			}
			if ((y > v.getHeight() / 2 + BOUND_WIDTH) && (y < 3 * v.getHeight() / 4 - BOUND_WIDTH)) {
				if ((x >= 0 + BOUND_WIDTH) && (x < v.getWidth() / 3 - BOUND_WIDTH)) { // Area 7
					gpTopLeft = (GeoPoint) projection.fromPixels(0, v.getHeight() / 2);
					gpBottomRight = (GeoPoint) projection.fromPixels(v.getWidth() / 3, 3 * v.getHeight() / 4);
					area = 7;
				}
				if ((x > v.getWidth() / 3 + BOUND_WIDTH) && (x < 2 * v.getWidth() / 3 - BOUND_WIDTH)) { // Area 8
					gpTopLeft = (GeoPoint) projection.fromPixels(v.getWidth() / 3, v.getHeight() / 2);
					gpBottomRight = (GeoPoint) projection.fromPixels(2 * v.getWidth() / 3, 3 * v.getHeight() / 4);
					area = 8;
				}
				if ((x > 2 * v.getWidth() / 3 + BOUND_WIDTH) && (x <= v.getWidth() - BOUND_WIDTH)) { // Area 9
					gpTopLeft = (GeoPoint) projection.fromPixels(2 * v.getWidth() / 3, v.getHeight() / 2);
					gpBottomRight = (GeoPoint) projection.fromPixels(v.getWidth(), 3 * v.getHeight() / 4);
					area = 9;
				}
			}
			if ((y > 3 * v.getHeight() / 4 + BOUND_WIDTH) && (y <= v.getHeight() - BOUND_WIDTH)) {
				if ((x >= 0 + BOUND_WIDTH) && (x < v.getWidth() / 3 - BOUND_WIDTH)) { // Area 10
					gpTopLeft = (GeoPoint) projection.fromPixels(0, 3 * v.getHeight() / 4);
					gpBottomRight = (GeoPoint) projection.fromPixels(v.getWidth() / 3, v.getHeight());
					area = 10;
				}
				if ((x > v.getWidth() / 3 + BOUND_WIDTH) && (x < 2 * v.getWidth() / 3 - BOUND_WIDTH)) { // Area 11
					gpTopLeft = (GeoPoint) projection.fromPixels(v.getWidth() / 3, 3 * v.getHeight() / 4);
					gpBottomRight = (GeoPoint) projection.fromPixels(2 * v.getWidth() / 3, v.getHeight());
					area = 11;
				}
				if ((x > 2 * v.getWidth() / 3 + BOUND_WIDTH) && (x <= v.getWidth() - BOUND_WIDTH)) { // Area 12
					gpTopLeft = (GeoPoint) projection.fromPixels(2 * v.getWidth() / 3, 3 * v.getHeight() / 4);
					gpBottomRight = (GeoPoint) projection.fromPixels(v.getWidth(), v.getHeight());
					area = 12;
				}
			}
			if ((area > 0) && (area <= 12)) { // If the area is valid
				// Get nodes in the touched area and speak
				BoundingBoxE6 bbox = new BoundingBoxE6(
						gpTopLeft.getLatitudeE6(),
						gpBottomRight.getLongitudeE6(),
						gpBottomRight.getLatitudeE6(),
						gpTopLeft.getLongitudeE6());
				Set<OsmNode> nodesInBbox = ((MyMapView) v).getNodesInBbox(bbox);			
				//Hélène's code to announce area and then the name of the touched point
				if (lastArea != area) {
					if (MyTTS.getInstance(context).speak(
							area + ": "
							+ nodesInBbox.size()
							+ context.getResources().getString(R.string.point_of_interest),
							TextToSpeech.QUEUE_FLUSH, 
							null) == TextToSpeech.SUCCESS) {
						lastArea = area;
						logArea = ""+ area;
					}
				} else {
					for (OsmNode n : nodesInBbox) {
						if ((n.toPoint((MapView) v).y <= y + TARGET_SIZE / 2)
								&& (n.toPoint((MapView) v).y >= y - TARGET_SIZE / 2)
								&& (n.toPoint((MapView) v).x <= x + TARGET_SIZE / 2)
								&& (n.toPoint((MapView) v).x >= x - TARGET_SIZE / 2)) {
							if (!MyTTS.getInstance(context).isSpeaking()) {
								// Vibrate when touching a node
								((Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE)).vibrate(150);
								MyTTS.getInstance(context).speak(n.getName(), TextToSpeech.QUEUE_FLUSH, null);
								logAnnounce = n.getName();								
							}
							logContact = n.getName();
						}
					}
				}
			}

			//for logging
			double lat = ((MainActivity) context).mapView.getProjection().fromPixels(x, y).getLatitudeE6();
			double lon = ((MainActivity) context).mapView.getProjection().fromPixels(x, y).getLongitudeE6();
			Datalogger(x,y,lat,lon,logContact,logAnnounce, logArea);
			logAnnounce = "";
			logContact = "";
			logArea = "";
			
			//Old Mathieu's modification
			/*Log.v(currentPoint,lastPoint);
			//if (lastPoint !=  currentPoint) {
				for (OsmNode n : nodesInBbox) {
					if ((n.toPoint((MapView) v).y <= y + TARGET_SIZE / 2)
							&& (n.toPoint((MapView) v).y >= y - TARGET_SIZE / 2)
							&& (n.toPoint((MapView) v).x <= x + TARGET_SIZE / 2)
							&& (n.toPoint((MapView) v).x >= x - TARGET_SIZE / 2) 
							//&& (lastPoint !=  currentPoint)
							){
							currentPoint = n.getName();
							// Vibrate when touching a node
							//if (currentPoint != lastPoint){
								MyTTS.getInstance(context).stop();
								((Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE)).vibrate(150);
								MyTTS.getInstance(context).speak(n.getName(), TextToSpeech.QUEUE_ADD, null);
								lastPoint = n.getName();
								logAnnounce = n.getName();
								Log.e("logAnnounce", logAnnounce);
							//}
						}
						//else lastPoint = "nothing";
					logContact = n.getName();
					Log.e("logContact", logContact);
					}
				//}
			//
			
			if (lastArea != area) {
				if (MyTTS.getInstance(context).speak(
						area + ": "
						+ nodesInBbox.size()
						+ context.getResources().getString(R.string.point_of_interest),
						TextToSpeech.QUEUE_FLUSH, 
						null) == TextToSpeech.SUCCESS) {
					lastArea = area;
				} 
			}*/
			
			break;
		}

		case MotionEvent.ACTION_UP: {
			activePointerId = INVALID_POINTER_ID;
			lastArea = -1; // Initialize area again so that when the user touches it again TTS is speaking again
			break;
		}

		case MotionEvent.ACTION_CANCEL: {
			activePointerId = INVALID_POINTER_ID;
			break;
		}

		case MotionEvent.ACTION_POINTER_UP: {
			int pointerIndex = MotionEventCompat.getActionIndex(ev);
			int pointerId = MotionEventCompat.getPointerId(ev, pointerIndex);
			if (pointerId == activePointerId) {
				// This was our active pointer going up. Choose a new
				// active pointer and adjust accordingly.
				int newPointerIndex = pointerIndex == 0 ? 1 : 0;
				activePointerId = MotionEventCompat.getPointerId(ev, newPointerIndex);
			}
			break;
		}
		}
		return true;
	}

	public void Datalogger (float x, float y, double lat, double lon, String logContact, String logAnnounce, String logArea){
	    if (firstTouch){
	    output.println("time(ms);x;y;lat;lon;contact;announce;area");
	    firstTouch = false;
	    }
	    Date touchDate = new Date();
	    String str = touchDate.getTime()-myDate.getTime() + ";" 
	    + (int)x + ";" + (int)y + ";" 
	    + lat/100000 + ";" + lon/100000 + ";"
	    + logContact + ";" + logAnnounce+ ";" + logArea;
	    output.println(str);
	    output.flush();
	  }
}
