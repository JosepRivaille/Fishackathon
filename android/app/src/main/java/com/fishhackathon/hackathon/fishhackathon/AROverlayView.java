package com.fishhackathon.hackathon.fishhackathon;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.location.Location;
import android.opengl.Matrix;
import android.view.View;

import com.fishhackathon.hackathon.fishhackathon.models.ARLines;
import com.fishhackathon.hackathon.fishhackathon.models.ARPoint;
import com.fishhackathon.hackathon.fishhackathon.utils.LocationHelper;

import java.util.ArrayList;
import java.util.List;

public class AROverlayView extends View {
    public static final String TAG = AROverlayView.class.getSimpleName();
    private float[] rotatedProjectionMatrix = new float[16];
    private Location currentLocation;
    private List<ARPoint> arPoints;
    private List<ARLines> arLines;

    public AROverlayView(Context context) {
        super(context);

        //TODO: Demo points
        arPoints = new ArrayList<ARPoint>() {{
            add(new ARPoint("Playa Rio San Pedro", 36.529317, -6.216425, 0));
            add(new ARPoint("Isla del Trocadero", 36.515823, -6.210870, 0));
        }};

        arLines = new ArrayList<ARLines>() {{
            add(new ARLines(
                    new ARPoint("Cadiz", 36.528213, -6.291334, 0),
                    new ARPoint("Rota", 36.623334, -6.361035, 0),
                    true, true));
        }};
    }

    public void updateRotatedProjectionMatrix(float[] rotatedProjectionMatrix) {
        this.rotatedProjectionMatrix = rotatedProjectionMatrix;
        this.invalidate();
    }

    public void updateCurrentLocation(Location currentLocation) {
        this.currentLocation = currentLocation;
        this.invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (currentLocation == null) {
            return;
        }

        final int circleRadius = 30;
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.GREEN);
        paint.setTypeface(Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL));
        paint.setTextSize(60);

        for (int i = 0; i < arPoints.size(); i++) {
            float[] currentLocationInECEF = LocationHelper.WSG84toECEF(currentLocation);
            float[] pointInECEF = LocationHelper.WSG84toECEF(arPoints.get(i).getLocation());
            float[] pointInENU = LocationHelper.ECEFtoENU(currentLocation, currentLocationInECEF, pointInECEF);

            float[] cameraCoordinateVector = new float[4];
            Matrix.multiplyMV(cameraCoordinateVector, 0, rotatedProjectionMatrix, 0, pointInENU, 0);

            // cameraCoordinateVector[2] is z, that always less than 0 to display on right position
            // if z > 0, the point will display on the opposite
            if (cameraCoordinateVector[2] < 0) {
                float x = (0.5f + cameraCoordinateVector[0] / cameraCoordinateVector[3]) * canvas.getWidth();
                float y = (0.5f - cameraCoordinateVector[1] / cameraCoordinateVector[3]) * canvas.getHeight();

                canvas.drawCircle(x, y, circleRadius, paint);
                canvas.drawText(arPoints.get(i).getName(), x - (30 * arPoints.get(i).getName().length() / 2), y - 80, paint);
            }
        }

        final int lineWidth = 15;
        paint.setColor(Color.RED);
        paint.setStrokeWidth(lineWidth);

        for (int i = 0; i < arLines.size(); i++) {
            float[] currentLocationInECEF = LocationHelper.WSG84toECEF(currentLocation);
            float[] pointAInECEF = LocationHelper.WSG84toECEF(arLines.get(i).getPointA().getLocation());
            float[] pointAInENU = LocationHelper.ECEFtoENU(currentLocation, currentLocationInECEF, pointAInECEF);

            float[] cameraCoordinateVectorA = new float[4];
            Matrix.multiplyMV(cameraCoordinateVectorA, 0, rotatedProjectionMatrix, 0, pointAInENU, 0);

            // cameraCoordinateVector[2] is z, that always less than 0 to display on right position
            // if z > 0, the point will display on the opposite
            float xA = 0, yA = 0;
            boolean drawA = false;
            if (cameraCoordinateVectorA[2] < 0) {
                xA = (0.5f + cameraCoordinateVectorA[0] / cameraCoordinateVectorA[3]) * canvas.getWidth();
                yA = (0.5f - cameraCoordinateVectorA[1] / cameraCoordinateVectorA[3]) * canvas.getHeight();

                if (arLines.get(i).isDrawPoints()) {
                    canvas.drawCircle(xA, yA, circleRadius, paint);
                }
                if (arLines.get(i).isDrawLabels()) {
                    canvas.drawText(arLines.get(i).getPointA().getName(), xA - (30 * arLines.get(i).getPointA().getName().length() / 2), yA - 80, paint);
                }
                drawA = true;
            }

            float[] pointBInECEF = LocationHelper.WSG84toECEF(arLines.get(i).getPointB().getLocation());
            float[] pointBInENU = LocationHelper.ECEFtoENU(currentLocation, currentLocationInECEF, pointBInECEF);

            float[] cameraCoordinateVectorB = new float[4];
            Matrix.multiplyMV(cameraCoordinateVectorB, 0, rotatedProjectionMatrix, 0, pointBInENU, 0);

            // cameraCoordinateVector[2] is z, that always less than 0 to display on right position
            // if z > 0, the point will display on the opposite
            float xB = 0, yB = 0;
            boolean drawB = false;
            if (cameraCoordinateVectorB[2] < 0) {
                xB = (0.5f + cameraCoordinateVectorB[0] / cameraCoordinateVectorB[3]) * canvas.getWidth();
                yB = (0.5f - cameraCoordinateVectorB[1] / cameraCoordinateVectorB[3]) * canvas.getHeight();

                if (arLines.get(i).isDrawPoints()) {
                    canvas.drawCircle(xB, yB, circleRadius, paint);
                }
                if (arLines.get(i).isDrawLabels()) {
                    canvas.drawText(arLines.get(i).getPointB().getName(), xB - (30 * arLines.get(i).getPointB().getName().length() / 2), yB - 80, paint);
                }
                drawB = true;
            }

            if (drawA && drawB) {
                canvas.drawLine(xA, yA, xB, yB, paint);
            }
        }
    }
}
