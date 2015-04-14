package cmpe.alpha.fitwhiz.HelperLibrary;

import android.content.Context;

import cmpe.alpha.fitwhiz.lib.FitwhizApplication;

/**
 * Created by rajagopalan on 4/13/15.
 */
public class HumidityAnalyzerHelper implements Runnable {
    private double h;
    private FitwhizApplication app;
    private Context context;

    public HumidityAnalyzerHelper(double h,FitwhizApplication app, Context context){
        this.h=h;
        this.app=app;
        this.context=context;
    }

    public void run() {
        ReadingsAnalyzer ra = new ReadingsAnalyzer(app);
        ra.analyzeHumidity(h);
    }
}