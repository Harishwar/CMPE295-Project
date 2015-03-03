package cmpe.alpha.fitwhiz.HelperLibrary;

import cmpe.alpha.fitwhiz.lib.FitwhizApplication;

/**
 * Created by rajagopalan on 2/22/15.
 */
public class ReadingsAnalyzer {
    private FitwhizApplication application;
    public ReadingsAnalyzer(FitwhizApplication application)
    {
       this.application=application;
    }
    public void analyzeValues(double x, double y, double z, double h, double t)
    {
        analyzeAcceleration(MathHelper.getResultantAcceleration(x,y,z));
        analyzeTemperature(t);
        analyzeHumidity(h);
    }
    public void analyzeAcceleration(double acceleration)
    {
        double avg = MathHelper.getResultantAcceleration(application.getResult_xVal(),application.getResult_yVal(),application.getResult_zVal());
        //TODO Decide the threshold
        if(acceleration>avg+10)
        {
            //Give the details to the NotificationsHelper class
        }
    }
    public void analyzeHumidity(double currentValue)
    {
        double avg = application.getResult_hVal();
        //TODO Decide the threshold
        if(currentValue>avg+5)
        {
            //Give the details to the NotificationsHelper class
        }
    }
    public void analyzeTemperature(double currentValue)
    {
        double avg = application.getResult_tVal();
        //TODO Decide the threshold
        if(currentValue>avg+2)
        {
            //Give the details to the NotificationsHelper class
        }
    }
}
