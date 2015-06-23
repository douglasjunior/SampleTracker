package com.github.douglasjunior.sampletracker;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * Created by douglas on 19/06/15.
 */
public class Tracker extends View {

    private Paint paint;
    private Point point1_draw;
    private Point point2_draw;
    private Point point3_draw;

    public Tracker(Context context) {
        super(context);
        init();
    }

    public Tracker(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public Tracker(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStrokeWidth(2);
        paint.setColor(android.graphics.Color.RED);
        paint.setStyle(Paint.Style.STROKE);
        paint.setAntiAlias(true);

        point1_draw = new Point();
        point2_draw = new Point();
        point3_draw = new Point();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Log.d(getClass().getSimpleName(), "onDraw");

        point1_draw.set(getWidth() / 2, 0);
        point2_draw.set(getWidth(), getHeight());
        point3_draw.set(0, getHeight());

        Path path = new Path();
        path.setFillType(Path.FillType.EVEN_ODD);
        path.moveTo(point1_draw.x, point1_draw.y);
        path.lineTo(point2_draw.x, point2_draw.y);
        path.lineTo(point3_draw.x, point3_draw.y);
        path.lineTo(point1_draw.x, point1_draw.y);
        path.lineTo(point1_draw.x, point2_draw.y);
        path.close();

        canvas.drawPath(path, paint);
    }
}
