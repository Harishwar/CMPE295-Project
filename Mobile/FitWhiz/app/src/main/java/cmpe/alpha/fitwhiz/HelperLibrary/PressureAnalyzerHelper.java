package cmpe.alpha.fitwhiz.HelperLibrary;

import android.content.Context;

import cmpe.alpha.fitwhiz.lib.FitwhizApplication;

/**
 * Created by rajagopalan on 4/13/15.
 */
public class PressureAnalyzerHelper implements Runnable {
    private double p;
    private FitwhizApplication app;
    private Context context;

    public PressureAnalyzerHelper(double p,FitwhizApplication app, Context context){
        this.p=p;
        this.app=app;
        this.context=context;
    }

    public void run() {
    ReadingsAnalyzer ra = new ReadingsAnalyzer(app);
    ra.analyzePressure(p);
    }
}