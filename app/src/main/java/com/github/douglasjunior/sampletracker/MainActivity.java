package com.github.douglasjunior.sampletracker;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class MainActivity extends Activity implements View.OnClickListener, SeekBar.OnSeekBarChangeListener {

    private Vector preLastPoint = new Vector(0, 0);
    private Vector lastPoint = new Vector(0, 0);

    private Navigator navigator;
    private Button btnGo;
    private Button btnLeft;
    private Button btnRight;

    private SeekBar sbDirecao;
    private TextView tvDirecao;

    private final float larguraTrator = 10; // em metros
    private final float distanciaCentro = 3; // em metros

    private final List<Vector> cachePoints = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        navigator = (Navigator) findViewById(R.id.navigator);
        navigator.setTrackerWidth(larguraTrator);
        navigator.setCentralDistance(distanciaCentro);

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
    protected void onResume() {
        super.onResume();

        navigator.resume();
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
        Vector newPoint = new Vector(lastPoint.cartesian(0) + getDirecao(), lastPoint.cartesian(1) + 1.5f - Math.abs(getDirecao()));
        Log.d(getClass().getSimpleName(), "toGo: " + newPoint);
        cachePoints.add(newPoint);
        for (Iterator<Vector> iterator = cachePoints.iterator(); iterator.hasNext(); ) {
            Vector point = iterator.next();
            // calcula se o ponto está distante do centro do trator, conforme configurado
            double distance = point.distanceTo(newPoint);
            if (distance >= distanciaCentro) {
                navigator.addTrackerPoint(point);
                iterator.remove();
            }
        }
        preLastPoint = lastPoint;
        lastPoint = newPoint;
        navigator.setLastPoints(lastPoint, preLastPoint);
    }

    private void toLeft() {
        sbDirecao.setProgress(0);
        toGo();
    }

    private void toRight() {
        sbDirecao.setProgress(sbDirecao.getMax());
        toGo();
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        tvDirecao.setText(getDirecao() + "");
    }

    /**
     * Retorna a direção em metros
     *
     * @return
     */
    private float getDirecao() {
        float direcao = ((sbDirecao.getProgress() - 50f) / 50f);
        Log.w(getClass().getSimpleName(), "direcao: " + direcao);
        return direcao;
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
