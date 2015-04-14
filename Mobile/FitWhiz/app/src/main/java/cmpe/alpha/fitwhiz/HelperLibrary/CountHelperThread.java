package cmpe.alpha.fitwhiz.HelperLibrary;

import android.content.Context;

import cmpe.alpha.fitwhiz.lib.FitwhizApplication;

/**
 * Created by rajagopalan on 4/13/15.
 */
public class CountHelperThread implements Runnable{
    private double x, y, z;
    private FitwhizApplication app;
    private Context context;
    public CountHelperThread(double x, double y, double z, FitwhizApplication app, Context context)
    {
        this.app=app;
        this.context = context;
        this.x=x;
        this.y=y;
        this.z=z;
    }

    public void run()
    {
        CountHelper ch = new CountHelper(app,context);
        ch.AnalyzeValues(x,y,z);
    }
}