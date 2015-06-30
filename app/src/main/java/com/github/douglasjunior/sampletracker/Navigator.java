package com.github.douglasjunior.sampletracker;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

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
    private final List<Vector> trackerHistory = new ArrayList<>();

    private float trackerWidth; // largura do pulverizador/plantadeira
    private float centralDistance; // distância do pulverizador/plantadeira para receptor GPS
    private Vector lastPoint;
    private Vector preLastPoint;
    private volatile boolean running = false;
    private volatile boolean needsDraw;


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
        needsDraw = true;
    }

    public void addTrackerPoint(Vector newPoint) {
        trackerHistory.add(newPoint);
        needsDraw = true;
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
        needsDraw = true;
    }

    @Override
    public void run() {
        while (running && !renderThread.isInterrupted()) {
            if (holder.getSurface().isValid() && needsDraw) {

                Canvas canvas = holder.lockCanvas();

                canvas.drawColor(Color.GREEN);

                /**
                 * Calcula posição e dimenções do tracker
                 */
                RectF trackerLayout = new RectF();

                trackerLayout.left = (getWidth() / 2f) - (meterToDpi(BASE_TRACKER_WIDTH) / 2f);
                trackerLayout.right = trackerLayout.left + meterToDpi(BASE_TRACKER_WIDTH);
                //trackerLayout.bottom = getHeight() - TRACKER_BOTTOM_MARGIN;
                trackerLayout.bottom = getHeight() / 2f;
                trackerLayout.top = trackerLayout.bottom - meterToDpi(BASE_TRACKER_HEIGHT);


                tracker.layout((int) trackerLayout.left, (int) trackerLayout.top, (int) trackerLayout.right, (int) trackerLayout.bottom);

                /**
                 * Calcula posição e dimenções do terreno
                 */
                // calcula o centro do terreno com base na posição do tracker
                Vector terrainCenter = new Vector(trackerLayout.centerX(), trackerLayout.bottom);

                terrain.setTrackerWidth(trackerWidth);
                terrain.setLastPoints(lastPoint, preLastPoint);
                terrain.setCenter(terrainCenter);
                terrain.setTrackerHistory(trackerHistory);

                terrain.layout(0, 0, getWidth(), getHeight());

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
