package com.github.douglasjunior.sampletracker;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.util.List;


/**
 * Created by douglas on 19/06/15.
 */
public class Terrain extends View {

    private PointF center; // centro de rotação (trazeira do trator)
    private List<PointF> trackerHistory; // array com histórico de pontos
    private Paint paint;
    private PointF lastTrackerPoint; // ultima posicao
    private PointF preLastTrackerPoint; // penultima posição

    public Terrain(Context context) {
        super(context);
        init();
    }

    public Terrain(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public Terrain(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        setBackgroundColor(Color.GREEN);

        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStrokeWidth(5);
        paint.setColor(Color.BLUE);
        paint.setStyle(Paint.Style.STROKE);
        paint.setAntiAlias(true);
    }

    /**
     * Desenha o terreno e o caminho percorrido
     *
     * @param canvas
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Log.d(getClass().getSimpleName(), "onDraw");

        canvas.translate(center.x, center.y);

        if (trackerHistory.size() > 1) {
            Path path = new Path();
            /*
            Percorre o histórico de posições e desenha o caminho
             */
            for (int i = 1; i < trackerHistory.size() - 1; i++) {
                PointF pointAtual = trackerHistory.get(i);
                PointF pointPrev = trackerHistory.get(i - 1);

                double[] posTratorAtual = convertToTerrainCoordinates(pointAtual);
                double[] posTratorPrev = convertToTerrainCoordinates(pointPrev);

                path.moveTo((float) posTratorPrev[0], (float) posTratorPrev[1]);
                path.lineTo((float) posTratorAtual[0], (float) posTratorAtual[1]);
            }

            Vector dirFixed = new Vector(new double[]{0d, 1d});

            /*
            Após desenhar o caminho, pega somente o vetor da última posição e tenta encontrar o ângulo de rotação.
             */
            Vector posTratorAtual = new Vector(convertToTerrainCoordinates(lastTrackerPoint));
            Vector posTratorPrev = new Vector(convertToTerrainCoordinates(preLastTrackerPoint));
            Log.d(getClass().getSimpleName(), "posTratorAtual: " + posTratorAtual);
            Log.d(getClass().getSimpleName(), "posTratorPrev: " + posTratorPrev);

            Vector dirTrator = posTratorAtual.minus(posTratorPrev).direction();
            Log.d(getClass().getSimpleName(), "dirTrator: " + dirTrator);

            double deslocamento = posTratorAtual.distanceTo(posTratorPrev);
            Log.d(getClass().getSimpleName(), "deslocamento: " + deslocamento);

            dirTrator.normalize(deslocamento);
            Log.d(getClass().getSimpleName(), "dirTrator normalizado: " + dirTrator);

            double senAngulo = posTratorPrev.cartesian(0) / deslocamento;
            Log.d(getClass().getSimpleName(), "senAngulo: " + senAngulo);

            //double angulo = radToDeg(dirTrator.dot(dirFixed));
            double angulo = Math.toDegrees(Math.asin(senAngulo));
            Log.d(getClass().getSimpleName(), "angulo: " + angulo);

            if (posTratorPrev.cartesian(1) < 0) {
                if (posTratorPrev.cartesian(0) < 0) {
                    senAngulo = posTratorPrev.cartesian(1) / deslocamento;
                    Log.d(getClass().getSimpleName(), "senAngulo: " + senAngulo);

                    angulo = Math.toDegrees(Math.asin(senAngulo)) - 90d;
                    Log.d(getClass().getSimpleName(), "angulo: " + angulo);
                } else {
                    senAngulo = posTratorPrev.cartesian(1) / deslocamento;
                    Log.d(getClass().getSimpleName(), "senAngulo: " + senAngulo);

                    angulo = Math.abs(Math.toDegrees(Math.asin(senAngulo))) + 90d;
                    Log.d(getClass().getSimpleName(), "angulo: " + angulo);
                }
            }

            canvas.rotate((float) angulo);
            canvas.drawPath(path, paint);
        }
    }

    private double radToDeg(double rad) {
        return rad * (180d / Math.PI);
    }

    /**
     * Faz uma conversão nas posições para que sempre a última seja mais próxima de [0, 0]
     *
     * @param point
     * @return
     */
    private double[] convertToTerrainCoordinates(PointF point) {
        double[] coordinate = new double[]{lastTrackerPoint.x - point.x, lastTrackerPoint.y - point.y};
        return coordinate;
    }

    /**
     * Recebe a posição central (do trator)
     *
     * @param center
     */
    public void setCenter(PointF center) {
        Log.d(getClass().getSimpleName(), "Center: " + center);
        this.center = center;
    }

    /**
     * Recebe o histórico de posições
     *
     * @param trackerHistory
     */
    public void setTrackerHistory(List<PointF> trackerHistory) {
        if (trackerHistory != null && trackerHistory.size() > 1) {
            lastTrackerPoint = trackerHistory.get(trackerHistory.size() - 1);
            preLastTrackerPoint = trackerHistory.get(trackerHistory.size() - 2);
        }
        this.trackerHistory = trackerHistory;
    }
}
