package eiu.example.tuann.bus;

import android.graphics.Color;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tuann on 8/19/2017.
 */

public class DrawingRote {

    public void drawingBrown() {
        check();
        if (MainActivity.brown == null) {
            List<LatLng> allPoints = new ArrayList<LatLng>();
            allPoints.add(new LatLng(11.053410, 106.666873));
            allPoints.add(new LatLng(11.053366, 106.666719));
            allPoints.add(new LatLng(11.053364, 106.666667));
            allPoints.add(new LatLng(11.053410, 106.666625));
            allPoints.add(new LatLng(11.053465, 106.666569));
            allPoints.add(new LatLng(11.053513, 106.666504));
            allPoints.add(new LatLng(11.053557, 106.666453));
            allPoints.add(new LatLng(11.053585, 106.666383));
            allPoints.add(new LatLng(11.053620, 106.666300));
            allPoints.add(new LatLng(11.053640, 106.666228));
            allPoints.add(new LatLng(11.053650, 106.666157));
            allPoints.add(new LatLng(11.053651, 106.666086));
            allPoints.add(new LatLng(11.053646, 106.666023));
            allPoints.add(new LatLng(11.053626, 106.665916));
            allPoints.add(new LatLng(11.053716, 106.665888));
            allPoints.add(new LatLng(11.054614, 106.665703));
            allPoints.add(new LatLng(11.054849, 106.665642));
            allPoints.add(new LatLng(11.055277, 106.665555));
            allPoints.add(new LatLng(11.055753, 106.667658));
            allPoints.add(new LatLng(11.055902, 106.668339));
            MainActivity.brown = MainActivity.mMap.addPolyline(new PolylineOptions().addAll(allPoints).width(7).color(Color.parseColor("#795548")));
        } else {
            MainActivity.brown.setVisible(true);
        }
    }

    public void drawingBlue() {
        check();
        if (MainActivity.blue == null) {
            List<LatLng> allPoints = new ArrayList<LatLng>();
            allPoints.add(new LatLng(11.053447, 106.667164));
            allPoints.add(new LatLng(11.053465, 106.667367));
            allPoints.add(new LatLng(11.053453, 106.667408));
            allPoints.add(new LatLng(11.053430, 106.667436));
            allPoints.add(new LatLng(11.053394, 106.667455));
            allPoints.add(new LatLng(11.053338, 106.667464));
            allPoints.add(new LatLng(11.053298, 106.667472));
            allPoints.add(new LatLng(11.053341, 106.667995));
            allPoints.add(new LatLng(11.053351, 106.668070));
            allPoints.add(new LatLng(11.053383, 106.668346));
            allPoints.add(new LatLng(11.053410, 106.668617));
            MainActivity.blue = MainActivity.mMap.addPolyline(new PolylineOptions().addAll(allPoints).width(7).color(Color.parseColor("#2196F3")));
        } else {
            MainActivity.blue.setVisible(true);
        }
    }

    public void drawingRed() {
        check();
        if (MainActivity.red == null) {
            List<LatLng> allPoints = new ArrayList<LatLng>();
            allPoints.add(new LatLng(11.053238, 106.668476));
            allPoints.add(new LatLng(11.053177, 106.668051));
            allPoints.add(new LatLng(11.053125, 106.667503));
            allPoints.add(new LatLng(11.053419, 106.667418));
            allPoints.add(new LatLng(11.053356, 106.666667));
            allPoints.add(new LatLng(11.053630, 106.665916));
            allPoints.add(new LatLng(11.055267, 106.665586));
            allPoints.add(new LatLng(11.055896, 106.668332));
            MainActivity.red = MainActivity.mMap.addPolyline(new PolylineOptions().addAll(allPoints).width(7).color(Color.parseColor("#F44336")));
        } else {
            MainActivity.red.setVisible(true);
        }
    }

    public void drawingGreen() {
        check();
        if (MainActivity.green == null) {
            List<LatLng> allPoints = new ArrayList<LatLng>();
            allPoints.add(new LatLng(11.053238, 106.668476));
            allPoints.add(new LatLng(11.053177, 106.668051));
            allPoints.add(new LatLng(11.053125, 106.667503));
            allPoints.add(new LatLng(11.053419, 106.667418));
            allPoints.add(new LatLng(11.053356, 106.666667));
            allPoints.add(new LatLng(11.053630, 106.665916));
            allPoints.add(new LatLng(11.055267, 106.665586));
            allPoints.add(new LatLng(11.055896, 106.668332));
            MainActivity.green = MainActivity.mMap.addPolyline(new PolylineOptions().addAll(allPoints).width(7).color(Color.parseColor("#4CAF50")));
        } else {
            MainActivity.green.setVisible(true);
        }
    }

    public void drawingYello() {
        check();
        if (MainActivity.yello == null) {
            List<LatLng> allPoints = new ArrayList<LatLng>();
            allPoints.add(new LatLng(11.053238, 106.668476));
            allPoints.add(new LatLng(11.053177, 106.668051));
            allPoints.add(new LatLng(11.053125, 106.667503));
            allPoints.add(new LatLng(11.053419, 106.667418));
            allPoints.add(new LatLng(11.053356, 106.666667));
            allPoints.add(new LatLng(11.053630, 106.665916));
            allPoints.add(new LatLng(11.055267, 106.665586));
            allPoints.add(new LatLng(11.055896, 106.668332));
            MainActivity.yello = MainActivity.mMap.addPolyline(new PolylineOptions().addAll(allPoints).width(7).color(Color.parseColor("#FFEB3B")));
        } else {
            MainActivity.yello.setVisible(true);
        }
    }

    public void drawingPink() {
        check();
        if (MainActivity.pink == null) {
            List<LatLng> allPoints = new ArrayList<LatLng>();
            allPoints.add(new LatLng(11.053238, 106.668476));
            allPoints.add(new LatLng(11.053177, 106.668051));
            allPoints.add(new LatLng(11.053125, 106.667503));
            allPoints.add(new LatLng(11.053419, 106.667418));
            allPoints.add(new LatLng(11.053356, 106.666667));
            allPoints.add(new LatLng(11.053630, 106.665916));
            allPoints.add(new LatLng(11.055267, 106.665586));
            allPoints.add(new LatLng(11.055896, 106.668332));
            MainActivity.pink = MainActivity.mMap.addPolyline(new PolylineOptions().addAll(allPoints).width(7).color(Color.parseColor("#E91E63")));
        } else {
            MainActivity.pink.setVisible(true);
        }
    }
    public void drawingAll() {
        drawingBrown();
        drawingBlue();
        drawingRed();
        drawingGreen();
        drawingYello();
        drawingPink();
    }

    public void drawingRemove() {
        check();
    }

    private void check() {
        if (MainActivity.brown != null) {
            MainActivity.brown.setVisible(false);
        }
        if (MainActivity.blue != null) {
            MainActivity.blue.setVisible(false);
        }
        if (MainActivity.red != null) {
            MainActivity.red.setVisible(false);
        }
        if (MainActivity.green != null) {
            MainActivity.green.setVisible(false);
        }
        if (MainActivity.yello != null) {
            MainActivity.yello.setVisible(false);
        }
        if (MainActivity.pink != null) {
            MainActivity.pink.setVisible(false);
        }
    }
}
