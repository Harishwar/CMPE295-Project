package cmpe.alpha.fitwhiz.HelperLibrary;

import android.content.Context;

import cmpe.alpha.fitwhiz.lib.FitwhizApplication;

/**
 * Created by rajagopalan on 4/5/15.
 */
public class CountHelper {
    private FitwhizApplication fitwhizApplication;
    long now,prev=0;
    double count=0;
    Context context;
    final double alpha = 0.8;
    private final float NOISE = (float) 0.01;
    double lastX,lastY,lastZ;
    long time_between_steps = 300;
    public CountHelper(FitwhizApplication app, Context context)
    {
        this.fitwhizApplication = app;
        this.context = context;
    }
    public void AnalyzeValues(double x, double y, double z)
    {
        count = fitwhizApplication.getCount();
        double[] gravity = {0,0,0};
        gravity[0] = alpha * gravity[0] + (1 - alpha) * x;
        gravity[1] = alpha * gravity[1] + (1 - alpha) * y;
        gravity[2] = alpha * gravity[2] + (1 - alpha) * z;
        double dX = Math.abs(lastX-x);
        double dY = Math.abs(lastY-y);
        double dZ = Math.abs(lastZ-z);
        /*if (dX < NOISE)
            dX = (float) 0.0;
        if (dY < NOISE)
            dY = (float) 0.0;
        if (dZ < NOISE)
            dZ = (float) 0.0;
        lastX = x;
        lastY = y;
        lastZ = z;
        if (dX > dY) {
            // Horizontal shake
            // do something here if you like

        } else if (dY > dX) {
            // Vertical shake
            // do something here if you like

        } else if ((dZ > dX) && (dZ > dY)) {
            // Z shake
            count = count + 1;
            if (count > 0) {
                fitwhizApplication.setCount(count);
            }
        }*/
        //System.out.println(count+":::"+y);

        now=System.currentTimeMillis();
        float time=(float)(now-prev);
        //finding the time in seconds
        float time_s=(time/1000);
        //System.out.println(dX+":"+dY+":"+dZ);
        //finding the acceleration
        double res=Math.abs(Math.sqrt(Math.pow(dX,2)+Math.pow(dY,2)+Math.pow(dZ,2)));
        //System.out.println("res"+res);
        //finding the distance
        double dist=(res*time_s*time_s*1000);
        //System.out.println("dist"+dist);
        System.out.println(" -dist "+dist);
        if (dist>=38)
        {
            count=count+0.5;
            fitwhizApplication.setCount(count);
        }
        prev=now;
        //System.out.println("-time:"+time);
        System.out.println(" -count "+count);
    }
}
