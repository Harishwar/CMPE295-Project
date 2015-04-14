package cmpe.alpha.fitwhiz.HelperLibrary;

import cmpe.alpha.fitwhiz.lib.FitwhizApplication;

/**
 * Created by rajagopalan on 2/22/15.
 */
public class ReadingsAnalyzer {
    private FitwhizApplication application;
    AlertRaiser alertRaiser;
    String url;
    public ReadingsAnalyzer(FitwhizApplication application)
    {
        this.application=application;
        alertRaiser = new AlertRaiser(application);
        url = new PropertiesReader(application.getApplicationContext()).getProperties("Fitwhiz.properties").getProperty("FileUploadUrl");
    }
    public void analyzeValues(double x, double y, double z, double h, double t)
    {
        //analyzeAcceleration(MathHelper.getResultantAcceleration(x,y,z));
        //analyzeTemperature(t);
        //analyzeHumidity(h);
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
            String msg = String.format("ABnormal humidity. H is %f",currentValue);
            alertRaiser.execute(url,msg);
            //Give the details to the NotificationsHelper class
        }
    }
    public void analyzeAmbientTemperature(double currentValue)
    {
        double avg = application.getResult_tVal();
        //TODO Decide the threshold
        if(currentValue>avg+2)
        {
            String msg = String.format("ABnormal Ambient temperature. Temp is %f",currentValue);
            alertRaiser.execute(url,msg);
            //Give the details to the NotificationsHelper class
        }
    }
    public void analyzeBodyTemperature(double currentValue)
    {
        double avg = application.getResult_tVal();
        //TODO Decide the threshold
        if(currentValue>avg+2)
        {
            String msg = String.format("ABnormal Body temperature. Temp is %f",currentValue);
            alertRaiser.execute(url,msg);
            //Give the details to the NotificationsHelper class
        }
    }
    public void analyzePressure(double currentValue)
    {
        double pMin = 0;
        double pMax = 1;
        //TODO Decide the threshold
        if(currentValue>pMax || currentValue<pMin)
        {
            String msg = String.format("ABnormal pressure. Pressure is %f",currentValue);
            alertRaiser.execute(url,msg);
            //Give the details to the NotificationsHelper class
        }
    }
}
