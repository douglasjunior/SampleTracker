package com.github.douglasjunior.sampletracker;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by douglas on 19/06/15.
 */
public class Navigator extends SurfaceView implements Runnable {

    public static final float DPI_PER_METER = 20; // 20 dpi por metro

    private static final float BASE_TRACKER_WIDTH = 3f; // em metros
    private static final float BASE_TRACKER_HEIGHT = 5f; // em metros

    private static final float TRACKER_BOTTOM_MARGIN = 100f;

    private Thread renderThread;
    private SurfaceHolder holder;

    private Tracker tracker;
    private Terrain terrain;

    // historico de pontos percorridos
    private final List<Vector> pointHistory = new ArrayList<>();
    private final List<RectF> trackerHistory = new ArrayList<>();

    private float trackerWidth; // largura do pulverizador/plantadeira
    private float centralDistance; // distância do pulverizador/plantadeira para receptor GPS
    private Vector firstPoint;
    private Vector lastPoint;
    private Vector preLastPoint;
    private volatile boolean running = false;
    private volatile boolean needsDraw; // define se precisa repintar
    private Vector terrainCenter; // define o centro do terreno
    private boolean layout; // define se o layout foi calculado
    private WindowManager windowManager;

    public Navigator(Context context) {
        super(context);
        init(context, null, Integer.MIN_VALUE);
    }

    public Navigator(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, Integer.MIN_VALUE);
    }

    public Navigator(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs, defStyle);
    }

    private void init(Context context, AttributeSet attrs, int defStyle) {
        this.holder = getHolder();
        tracker = new Tracker(this);
        terrain = new Terrain(this);
        windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        needsDraw = true;
    }


    public void addTrackerPoint(Vector newPoint) {
        if (!pointHistory.isEmpty()) {
            Vector currentPosition = new Vector(convertToTerrainCoordinates(newPoint)); // vector with X and Y position
            Vector lastPosition = new Vector(convertToTerrainCoordinates(pointHistory.get(pointHistory.size() - 1))); // vector with X and Y position

            float dpiTrackerWidth = meterToDpi(trackerWidth);

            /*
            Calcule degree by triangle sides
             */
            float shift = (float) currentPosition.distanceTo(lastPosition);
            Vector dif = lastPosition.minus(currentPosition);
            float sin = (float) (dif.cartesian(0) / shift);

            float degress = (float) Math.toDegrees(Math.asin(sin));

            /*
            Create a Rect to draw displacement between two coordinates
             */
            RectF rect = new RectF();
            rect.left = (float) (currentPosition.cartesian(0) - (dpiTrackerWidth / 2));
            rect.right = rect.left + dpiTrackerWidth;
            rect.top = (float) currentPosition.cartesian(1);
            rect.bottom = rect.top - shift;

            Path p = new Path();
            Matrix m = new Matrix();
            p.addRect(rect, Path.Direction.CCW);
            m.postRotate(-degress, (float) currentPosition.cartesian(0), (float) currentPosition.cartesian(1));
            p.transform(m);

            p.computeBounds(rect, true);

            trackerHistory.add(rect);

        }
        pointHistory.add(newPoint);
        needsDraw = true;
    }

    /**
     * Faz uma conversão nas posições para que sempre a última seja mais próxima de [0, 0]
     *
     * @param point
     * @return
     */
    private double[] convertToTerrainCoordinates(Vector point) {
        double x = Navigator.meterToDpi(firstPoint.cartesian(0) - point.cartesian(0));
        double y = Navigator.meterToDpi(firstPoint.cartesian(1) - point.cartesian(1));
        return new double[]{x, y};
    }


    public void setTrackerWidth(float trackerWidth) {
        this.trackerWidth = trackerWidth;
        needsDraw = true;
    }

    public void setCentralDistance(float centralDistance) {
        this.centralDistance = centralDistance;
        needsDraw = true;
    }

    public void setLastPoints(Vector lastPoint, Vector preLastPoint) {
        if (this.firstPoint == null)
            this.firstPoint = lastPoint;
        this.lastPoint = lastPoint;
        this.preLastPoint = preLastPoint;
        needsDraw = true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        needsDraw = true;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        /**
         * Calcula posição e dimenções do tracker
         */
        RectF trackerLayout = new RectF();

        trackerLayout.left = (getWidth() / 2f) - (meterToDpi(BASE_TRACKER_WIDTH) / 2f);
        trackerLayout.right = trackerLayout.left + meterToDpi(BASE_TRACKER_WIDTH);
        //trackerLayout.bottom = getHeight() - TRACKER_BOTTOM_MARGIN;
        trackerLayout.bottom = getHeight() / 2f;
        trackerLayout.top = trackerLayout.bottom - meterToDpi(BASE_TRACKER_HEIGHT);

        tracker.layout(trackerLayout);

        /**
         * Calcula posição e dimenções do terreno
         */
        // calcula o centro do terreno com base na posição do tracker
        terrainCenter = new Vector(trackerLayout.centerX(), trackerLayout.bottom);

        terrain.layout(0, 0, getWidth(), getHeight());

        needsDraw = true;
        layout = true;
    }

    @Override
    public void run() {
        while (running && !renderThread.isInterrupted()) {
            if (holder.getSurface().isValid() && needsDraw && layout) {

                Canvas canvas = holder.lockCanvas();

                canvas.drawColor(Color.GREEN);

                terrain.setTrackerWidth(trackerWidth);
                terrain.setLastPoints(lastPoint, preLastPoint);
                terrain.setCenter(terrainCenter);
                terrain.setTrackerHistory(trackerHistory);

                terrain.draw(canvas);
                tracker.draw(canvas);

                holder.unlockCanvasAndPost(canvas);

                needsDraw = false;
            }

            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
                break;
            }
        }
    }

    public void resume() {
        needsDraw = true;
        running = true;
        renderThread = new Thread(this);
        renderThread.start();
    }

    public void pause() {
        running = false;
        if (renderThread != null && renderThread.isAlive() && !renderThread.isInterrupted()) {
            renderThread.interrupt();
        }
    }


    public static double dpiToMeter(double dpi) {
        return dpi / DPI_PER_METER;
    }

    public static double meterToDpi(double meter) {
        return meter * DPI_PER_METER;
    }

    public static float dpiToMeter(float dpi) {
        return (float) dpiToMeter((double) dpi);
    }

    public static float meterToDpi(float meter) {
        return (float) meterToDpi((double) meter);
    }
}
