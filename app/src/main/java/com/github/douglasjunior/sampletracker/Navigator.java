package com.github.douglasjunior.sampletracker;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by douglas on 19/06/15.
 */
public class Navigator extends ViewGroup {


    private static final float TRACKER_WIDTH = 100f;
    private static final float TRACKER_HEIGHT = 100f;

    private static final float TRACKER_BOTTOM_MARGIN = 100f;

    // historico de pontos percorridos
    private final List<PointF> trackerHistory = new ArrayList<>();

    private Tracker tracker;
    private Terrain terrain;

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
        if (defStyle != Integer.MIN_VALUE) {
            tracker = new Tracker(context, attrs, defStyle);
            terrain = new Terrain(context, attrs, defStyle);
        } else if (attrs != null) {
            tracker = new Tracker(context, attrs);
            terrain = new Terrain(context, attrs);
        } else {
            tracker = new Tracker(context);
            terrain = new Terrain(context);
        }
        addView(terrain);
        addView(tracker);

        setBackgroundColor(Color.BLACK);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        Log.d(getClass().getSimpleName(), "onMeasure");
    }

    /**
     * Método chamado para organizar as posições dos objetos filhos.
     *
     * @param changed
     * @param left
     * @param top
     * @param right
     * @param bottom
     */
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        Log.d(getClass().getSimpleName(), "onLayout");
        /**
         * Calcula posição e dimenções do tracker
         */
        RectF trackerLayout = new RectF();

        trackerLayout.left = (getWidth() / 2f) - (TRACKER_WIDTH / 2f);
        trackerLayout.right = trackerLayout.left + TRACKER_WIDTH;
        //trackerLayout.bottom = getHeight() - TRACKER_BOTTOM_MARGIN;
        trackerLayout.bottom = getHeight() / 2f;
        trackerLayout.top = trackerLayout.bottom - TRACKER_HEIGHT;



        tracker.layout((int) trackerLayout.left, (int) trackerLayout.top, (int) trackerLayout.right, (int) trackerLayout.bottom);

        /**
         * Calcula posição e dimenções do terreno
         */

        // calcula o centro do terreno com base na posição do tracker
        PointF terrainCenter = new PointF(trackerLayout.centerX(), trackerLayout.bottom);

        terrain.setCenter(terrainCenter);

        terrain.setTrackerHistory(trackerHistory);

        RectF terrainLayout = new RectF();

        terrainLayout.left = 0;
        terrainLayout.top = 0;
        terrainLayout.right = getWidth();
        terrainLayout.bottom = getHeight();

        terrain.layout((int) terrainLayout.left, (int) terrainLayout.top, (int) terrainLayout.right, (int) terrainLayout.bottom);

        tracker.invalidate();
        terrain.invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Log.d(getClass().getSimpleName(), "onDraw");
    }

    public void addTrackerPoint(PointF newPoint) {
        trackerHistory.add(newPoint);
    }
}
