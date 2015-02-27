package cmpe.alpha.fitwhiz.HelperLibrary;

/**
 * Created by rajagopalan on 2/22/15.
 */
public class MathHelper {
    public static double getResultantAcceleration(double x, double y, double z)
    {
        return Math.sqrt(((x*x)+(y+y)+(z*z)));
    }
}
