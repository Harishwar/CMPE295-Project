package cmpe.alpha.fitwhiz.lib;

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
    private double result_yVal = 0.0;
    private double result_zVal = 0.0;
    private double result_hVal = 0.0;
    private double result_tVal = 0.0;
    private double result_xVal = 0.0;
    private String BloodType = "";
    private String PhoneNumber = "";
    private String Weight = "";
    private String FirstName = "";
    private String Gender = "";
    private String LastName = "";
    private String Address = "";
    private String Height = "";
    public double count;
    private double m_xVal;

    public double getM_xVal() {
        return m_xVal;
    }

    public void setM_xVal(double m_xVal) {
        this.m_xVal = m_xVal;
    }

    public double getM_yVal() {
        return m_yVal;
    }

    public void setM_yVal(double m_yVal) {
        this.m_yVal = m_yVal;
    }

    public double getM_zVal() {
        return m_zVal;
    }

    public void setM_zVal(double m_zVal) {
        this.m_zVal = m_zVal;
    }

    private double g_xVal;

    public double getG_xVal() {
        return g_xVal;
    }

    public void setG_xVal(double g_xVal) {
        this.g_xVal = g_xVal;
    }

    public double getG_yVal() {
        return g_yVal;
    }

    public void setG_yVal(double g_yVal) {
        this.g_yVal = g_yVal;
    }

    public double getG_zVal() {
        return g_zVal;
    }

    public void setG_zVal(double g_zVal) {
        this.g_zVal = g_zVal;
    }

    private double g_yVal;
    private double g_zVal;

    private double m_yVal;
    private double m_zVal;

    private double ambTemp;

    public double getBodyTemp() {
        return bodyTemp;
    }

    public void setBodyTemp(double bodyTemp) {
        this.bodyTemp = bodyTemp;
    }

    public double getAmbTemp() {
        return ambTemp;
    }

    public void setAmbTemp(double ambTemp) {
        this.ambTemp = ambTemp;
    }

    private double bodyTemp;

    public double getCount()
    {
        return this.count;
    }
    public void setCount(double d)
    {
        this.count=d;

    }

    public String getSensorId() {
        return SensorId;
    }

    public void setSensorId(String sensorId) {
        SensorId = sensorId;
    }

    protected String SensorId = "";
    public String getHeight() {
        return Height;
    }

    public void setHeight(String height) {
        Height = height;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public String getLastName() {
        return LastName;
    }

    public void setLastName(String lastName) {
        LastName = lastName;
    }

    public String getGender() {
        return Gender;
    }

    public void setGender(String gender) {
        Gender = gender;
    }

    public String getFirstName() {
        return FirstName;
    }

    public void setFirstName(String firstName) {
        FirstName = firstName;
    }

    public String getWeight() {
        return Weight;
    }

    public void setWeight(String weight) {
        Weight = weight;
    }

    public String getPhoneNumber() {
        return PhoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        PhoneNumber = phoneNumber;
    }

    public String getBloodType() {
        return BloodType;
    }

    public void setBloodType(String bloodType) {
        BloodType = bloodType;
    }

    public double getResult_xVal() {
        return result_xVal;
    }

    public void setResult_xVal(double result_xVal) {
        this.result_xVal = result_xVal;
    }


    public double getResult_yVal() {
        return result_yVal;
    }

    public void setResult_yVal(double result_yVal) {
        this.result_yVal = result_yVal;
    }

    public double getResult_zVal() {
        return result_zVal;
    }

    public void setResult_zVal(double result_zVal) {
        this.result_zVal = result_zVal;
    }

    public double getResult_hVal() {
        return result_hVal;
    }

    public void setResult_hVal(double result_hVal) {
        this.result_hVal = result_hVal;
    }

    public double getResult_tVal() {
        return result_tVal;
    }

    public void setResult_tVal(double result_tVal) {
        this.result_tVal = result_tVal;
    }

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
