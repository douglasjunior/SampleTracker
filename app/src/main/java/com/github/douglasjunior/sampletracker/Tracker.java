package com.github.douglasjunior.sampletracker;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;

/**
 * Created by douglas on 19/06/15.
 */
public class Tracker {

    private Paint paint;
    private Navigator navigator;
    private Rect layout = new Rect();

    public Tracker(Navigator navigator) {
        this.navigator = navigator;
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStrokeWidth(2);
        paint.setColor(android.graphics.Color.RED);
        paint.setStyle(Paint.Style.STROKE);
        paint.setAntiAlias(true);
    }

    public void draw(Canvas canvas) {
        Log.d(getClass().getSimpleName(), "draw");

//        Path path = new Path();
//        path.setFillType(Path.FillType.EVEN_ODD);
//        path.moveTo(layout.left, layout.bottom);
//        path.lineTo(layout.left + layout.width() / 2, layout.top);
//        path.lineTo(layout.right, layout.bottom);
//        path.close();

//        canvas.drawPath(path, paint);

        Bitmap bitmap = BitmapFactory.decodeResource(navigator.getContext().getResources(), R.drawable.ic_car);
        canvas.drawBitmap(bitmap, (layout.left + layout.width() / 2) - (bitmap.getWidth() / 2), layout.bottom - bitmap.getHeight(), paint);
        bitmap.recycle();
    }

    public void layout(int left, int top, int right, int bottom) {
        layout.left = left;
        layout.top = top;
        layout.right = right;
        layout.bottom = bottom;
    }

    public void layout(RectF trackerLayout) {
        layout.left = (int) trackerLayout.left;
        layout.top = (int) trackerLayout.top;
        layout.right = (int) trackerLayout.right;
        layout.bottom = (int) trackerLayout.bottom;
    }

    public Rect getLayout() {
        return layout;
    }
}
