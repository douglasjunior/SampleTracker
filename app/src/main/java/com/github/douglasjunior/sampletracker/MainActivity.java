package com.github.douglasjunior.sampletracker;

import android.app.Activity;
import android.graphics.PointF;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;


public class MainActivity extends Activity implements View.OnClickListener, SeekBar.OnSeekBarChangeListener {

    private PointF lastPoint = new PointF(50f, 50f);

    private Navigator navigator;
    private Button btnGo;
    private Button btnLeft;
    private Button btnRight;

    private SeekBar sbDirecao;
    private TextView tvDirecao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        navigator = (Navigator) findViewById(R.id.navigator);

        btnGo = (Button) findViewById(R.id.btnGo);
        btnLeft = (Button) findViewById(R.id.btnLeft);
        btnRight = (Button) findViewById(R.id.btnRight);

        btnGo.setOnClickListener(this);
        btnLeft.setOnClickListener(this);
        btnRight.setOnClickListener(this);

        tvDirecao = (TextView) findViewById(R.id.tvDirecao);

        sbDirecao = (SeekBar) findViewById(R.id.sbDirecao);
        sbDirecao.setOnSeekBarChangeListener(this);

    }

    @Override
    public void onClick(View v) {
        if (v == btnGo) {
            toGo();
        } else if (v == btnLeft) {
            toLeft();
        } else if (v == btnRight) {
            toRight();
        }
    }

    private void toGo() {
        PointF newPoint = new PointF(lastPoint.x + getDirecao(), lastPoint.y + 5 - Math.abs(getDirecao()));
        Log.d(getClass().getSimpleName(), "toGo: " + newPoint);
        navigator.addTrackerPoint(newPoint);
        lastPoint = newPoint;
        navigator.requestLayout();
        navigator.invalidate();
    }

    private void toLeft() {
        PointF newPoint = new PointF(lastPoint.x - 5, lastPoint.y + 3);
        Log.d(getClass().getSimpleName(), "toLeft: " + newPoint);
        navigator.addTrackerPoint(newPoint);
        lastPoint = newPoint;
        navigator.requestLayout();
        navigator.invalidate();
    }

    private void toRight() {

        PointF newPoint = new PointF(lastPoint.x + 5, lastPoint.y + 3);
        Log.d(getClass().getSimpleName(), "toRight: " + newPoint);
        navigator.addTrackerPoint(newPoint);
        lastPoint = newPoint;
        navigator.requestLayout();
        navigator.invalidate();
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        tvDirecao.setText(getDirecao() + "");
    }

    private int getDirecao() {
        return sbDirecao.getProgress() - 10;
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
