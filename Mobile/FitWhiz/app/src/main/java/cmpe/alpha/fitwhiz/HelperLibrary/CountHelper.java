package cmpe.alpha.fitwhiz.HelperLibrary;

import android.content.Context;
import android.hardware.SensorManager;

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
    private float   mLimit = 10;
    private double   mLastValues[];
    private float   mScale[] = new float[2];
    private float   mYOffset;

    private float   mLastDirections[] = new float[3*2];
    private double   mLastExtremes[][] = { new double[3*2], new double[3*2] };
    private double   mLastDiff[] = new double[3*2];
    private int     mLastMatch = -1;
    public CountHelper(FitwhizApplication app, Context context)
    {
        this.fitwhizApplication = app;
        this.context = context;
        mLimit=fitwhizApplication.getmLimit();
        mLastValues=fitwhizApplication.getmLastValues();
        mLastDirections=fitwhizApplication.getmLastDirections();
        mLastExtremes = fitwhizApplication.getmLastExtremes();
        mLastDiff=fitwhizApplication.getmLastDiff();
        mLastMatch=fitwhizApplication.getmLastMatch();
        int h = 480; // TODO: remove this constant
        mYOffset = h * 0.5f;
        mScale[0] = - (h * 0.5f * (1.0f / (SensorManager.STANDARD_GRAVITY * 2)));
        mScale[1] = - (h * 0.5f * (1.0f / (SensorManager.MAGNETIC_FIELD_EARTH_MAX)));
    }
    /*public void AnalyzeValues(double x, double y, double z)
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
/*
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
    }*/
    public void AnalyzeValues(double x, double y, double z)
    {
        count = fitwhizApplication.getCount();
        System.out.println("Count is " + count);
        double vSum = 0;
        double[] values = {x,y,z};
        int j=1;
        for (int i=0 ; i<3 ; i++) {
            final double v = mYOffset + values[i] * mScale[j];
            vSum += v;
        }
        int k = 0;
        double v = vSum / 3;
        //System.out.println("Last Values "+mLastValues[k]);
        float direction = (v > mLastValues[k] ? 1 : (v < mLastValues[k] ? -1 : 0));
        //System.out.println("Direction "+direction+" mLastDirection "+mLastDirections[k]);
        if (direction == - mLastDirections[k]) {
            // Direction changed
            int extType = (direction > 0 ? 0 : 1); // minumum or maximum?
            mLastExtremes[extType][k] = mLastValues[k];
            double diff = Math.abs(mLastExtremes[extType][k] - mLastExtremes[1 - extType][k]);
            System.out.println("Diff "+diff+" mLimit"+mLimit);
            if (diff > mLimit) {

                boolean isAlmostAsLargeAsPrevious = diff > (mLastDiff[k]*2/3);
                boolean isPreviousLargeEnough = mLastDiff[k] > (diff/3);
                boolean isNotContra = (mLastMatch != 1 - extType);
                System.out.println("isAlmostAsLargeAsPrevious "+isAlmostAsLargeAsPrevious);
                System.out.println("isPreviousLargeEnough "+isPreviousLargeEnough);
                System.out.println("isNotContra "+isNotContra);
                if (isAlmostAsLargeAsPrevious && isPreviousLargeEnough && isNotContra) {
                    fitwhizApplication.setCount(count+1);
                    System.out.println(count+1);
                    fitwhizApplication.setmLastMatch(extType);
                }
                else {
                    fitwhizApplication.setmLastMatch(-1);
                }
            }
            mLastDiff[k] = diff;
            fitwhizApplication.setmLastDiff(mLastDiff);
        }
        mLastDirections[k] = direction;
        fitwhizApplication.setmLastDirections(mLastDirections);
        mLastValues[k] = v;
        fitwhizApplication.setmLastValues(mLastValues);
        fitwhizApplication.setmLastExtremes(mLastExtremes);
    }
}
