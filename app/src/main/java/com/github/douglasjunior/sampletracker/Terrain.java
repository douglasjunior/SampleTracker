package com.github.douglasjunior.sampletracker;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;
import android.view.WindowManager;

import java.util.List;


/**
 * Created by douglas on 19/06/15.
 */
public class Terrain {

    private final WindowManager windowManager;
    private Vector center; // centro de rotação (trazeira do trator)

    private final Rect layout = new Rect();
    private Paint paint;
    private Paint paint2;
    private Vector lastPoint; // ultima posicao
    private Vector preLastPoint; // penultima posição
    private float trackerWidth;
    private Navigator navigator;
    private List<RectF> trackerHistory;

    public Terrain(Navigator navigator) {
        this.navigator = navigator;
        this.windowManager = (WindowManager) navigator.getContext().getSystemService(Context.WINDOW_SERVICE);
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStrokeWidth(2);
        paint.setColor(Color.BLUE);
        paint.setStyle(Paint.Style.STROKE);
        paint.setAntiAlias(true);

        paint2 = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint2.setStrokeWidth(1);
        paint2.setColor(Color.BLACK);
        paint2.setStyle(Paint.Style.STROKE);
        paint2.setAntiAlias(true);
    }


    public void draw(Canvas canvas) {
        Log.d(getClass().getSimpleName(), "draw");
        canvas.save();

        // translate canvas to vehicle positon
        canvas.translate((float) center.cartesian(0), (float) center.cartesian(1));

        float fieldRotation = 0;

        if (trackerHistory.size() > 1) {
             /*
            Before drawing the way, only takes the last position and finds the angle of rotation of the field.
             */
            Vector lastPosition = new Vector(convertToTerrainCoordinates(lastPoint));
            Vector preLastPosition = new Vector(convertToTerrainCoordinates(preLastPoint));
            float shift = (float) lastPosition.distanceTo(preLastPosition);

            /*
            Having the last coordinate as a triangle, 'preLastCoord' saves the values of the legs, while 'shift' is the hypotenuse
            */
            // If the Y offset is negative, then the opposite side is the Y displacement
            if (preLastPosition.cartesian(1) < 0) {
                // dividing the opposite side by hipetenusa, we have the sine of the angle that must be rotated.
                double sin = preLastPosition.cartesian(1) / shift;

                // when Y is negative, it is necessary to add or subtract 90 degrees depending on the value of X
                // The "Math.asin()" calculates the radian arc to the sine previously calculated.
                // And the "Math.toDegress()" converts degrees to radians from 0 to 360.
                if (preLastPosition.cartesian(0) < 0) {
                    fieldRotation = (float) (Math.toDegrees(Math.asin(sin)) - 90d);
                } else {
                    fieldRotation = (float) (Math.abs(Math.toDegrees(Math.asin(sin))) + 90d);
                }
            }
            // if not, the opposite side is the X offset
            else {
                // dividing the opposite side by hipetenusa have the sine of the angle that must be rotated.
                double senAngulo = preLastPosition.cartesian(0) / shift;

                // The "Math.asin()" calculates the radian arc to the sine previously calculated.
                // And the "Math.toDegress()" converts degrees to radians from 0 to 360.
                fieldRotation = (float) Math.toDegrees(Math.asin(senAngulo));
            }
        }

        final Path positionHistory = new Path(); // to draw the route
        final Path circle = new Path(); // to draw the positions

        /*
        Iterate the historical positions and draw the path
        */
        for (int i = 1; i < trackerHistory.size(); i++) {
            RectF lastRect = trackerHistory.get(i);
            RectF preLastRect = trackerHistory.get(i - 1);

            if (isInsideOfScreen(lastRect) || isInsideOfScreen(preLastRect)) {
                Path p = new Path();
                p.addRect(lastRect, Path.Direction.CW);
                p.addRect(preLastRect, Path.Direction.CW);
                p.close();
                positionHistory.addPath(p);
            }
        }

        // rotates the map to make the route down.
        canvas.rotate(fieldRotation);

        canvas.drawPath(positionHistory, paint);
        canvas.drawPath(circle, paint2);

        canvas.restore();
    }

    private boolean isInsideOfScreen(RectF rect) {
        return isInsideOfScreen(rect.left, rect.top) || isInsideOfScreen(rect.left, rect.bottom) ||
                isInsideOfScreen(rect.right, rect.top) || isInsideOfScreen(rect.right, rect.bottom);
    }

    private boolean isInsideOfScreen(double x, double y) {
        x += center.cartesian(0);
        y += center.cartesian(1);
        int size = Math.max(windowManager.getDefaultDisplay().getWidth(), windowManager.getDefaultDisplay().getHeight());
        return x <= size || y <= size;
    }

    /**
     * Faz uma conversão nas posições para que sempre a última seja mais próxima de [0, 0]
     *
     * @param point
     * @return
     */
    private double[] convertToTerrainCoordinates(Vector point) {
        double x = Navigator.meterToDpi(lastPoint.cartesian(0) - point.cartesian(0));
        double y = Navigator.meterToDpi(lastPoint.cartesian(1) - point.cartesian(1));
        return new double[]{x, y};
    }

    /**
     * Recebe a posição central (do trator)
     *
     * @param center
     */
    public void setCenter(Vector center) {
        Log.d(getClass().getSimpleName(), "Center: " + center);
        this.center = center;
    }

    /**
     * Recebe o histórico de posições
     *
     * @param trackerHistory
     */
    public void setTrackerHistory(List<RectF> trackerHistory) {
        this.trackerHistory = trackerHistory;
    }

    public void setLastPoints(Vector lastPoint, Vector preLastPoint) {
        this.lastPoint = lastPoint;
        this.preLastPoint = preLastPoint;
    }

    public void setTrackerWidth(float trackerWidth) {
        this.trackerWidth = trackerWidth;
    }


    public void layout(int left, int top, int right, int bottom) {
        layout.left = left;
        layout.top = top;
        layout.right = right;
        layout.bottom = bottom;
    }
}
