package cmpe.alpha.fitwhiz.HelperLibrary;

import android.content.Context;

import cmpe.alpha.fitwhiz.lib.FitwhizApplication;

/**
 * Created by rajagopalan on 4/13/15.
 */
public class AmbientTemparatureAnalyzerHelper implements Runnable {
    private double t;
    private FitwhizApplication app;
    private Context context;

    public AmbientTemparatureAnalyzerHelper(double t,FitwhizApplication app, Context context){
        this.t=t;
        this.app=app;
        this.context=context;
    }

    public void run() {
        ReadingsAnalyzer ra = new ReadingsAnalyzer(app);
        ra.analyzeAmbientTemperature(t);
    }
}