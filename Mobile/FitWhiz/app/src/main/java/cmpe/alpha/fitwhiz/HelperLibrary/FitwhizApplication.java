package cmpe.alpha.fitwhiz.HelperLibrary;

import android.app.Application;

/**
 * Created by rajagopalan on 2/22/15.
 */
public class FitwhizApplication extends Application{

    private double x_val = 0.0;
    private double y_val = 0.0;
    private double z_val = 0.0;
    private double h_val = 0.0;
    private double t_val = 0.0;
    public double getXVal()
    {
        return this.x_val;
    }
    public void setXVal(double val)
    {
        this.x_val=val;
    }
    public double getYVal()
    {
        return this.y_val;
    }
    public void setYVal(double val)
    {
        this.y_val=val;
    }
    public double getZVal()
    {
        return this.z_val;
    }
    public void setZVal(double val)
    {
        this.z_val=val;
    }
    public double getHVal()
    {
        return this.h_val;
    }
    public void setHVal(double val)
    {
        this.h_val=val;
    }
    public double getTVal()
    {
        return this.t_val;
    }
    public void setTVal(double val)
    {
        this.t_val=val;
    }
}
