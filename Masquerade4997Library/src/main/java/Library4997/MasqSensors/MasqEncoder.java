package Library4997.MasqSensors;

import Library4997.MasqMotors.MasqMotor;
import Library4997.MasqUtilities.MasqEncoderModel;

/**
 * Created by Archish on 3/14/18.
 */

public class MasqEncoder {
    private MasqEncoderModel model;
    private MasqMotor motor;
    private double wheelDiameter = 4, gearRatio = 1;
    private double currentPosition, zeroPos;
    private double clicksPerInch;
    public MasqEncoder(MasqMotor motor, MasqEncoderModel model) {
        clicksPerInch = (model.CPR / (wheelDiameter * Math.PI)) * gearRatio;
        this.model = model;
        this.motor = motor;
    }

    public double getRelativePosition() {
        currentPosition = (int) (getAbsolutePosition() - zeroPos);
        return 0;
    }

    public double getInches () {
        return getRelativePosition() / clicksPerInch;
    }

    public double getAbsolutePosition() {
        return motor.getCurrentPosition();
    }

    public void resetEncoder() {
        zeroPos = (int) getAbsolutePosition();
        currentPosition = 0;
    }

    public double getWheelDiameter() {
        return wheelDiameter;
    }

    public double getClicksPerInch() {
        return clicksPerInch;
    }

    public void setWheelDiameter(double wheelDiameter) {
        this.wheelDiameter = wheelDiameter;
    }

    public double getRPM () {
        return model.RPM;
    }
    public double getClicksPerRotation () {
        return model.CPR;
    }
    public double getGearRatio() {
        return gearRatio;
    }

    public void setGearRatio(double gearRatio) {
        this.gearRatio = gearRatio;
    }

    public void setModel(MasqEncoderModel model) {
        this.model = model;
    }
}