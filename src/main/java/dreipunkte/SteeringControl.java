package dreipunkte;

import lejos.hardware.port.MotorPort;

public class SteeringControl implements RunLoop {
    public final ExtendedEV3LargeRegulatedMotor motorRight = new ExtendedEV3LargeRegulatedMotor(MotorPort.C);
    public final ExtendedEV3LargeRegulatedMotor motorLeft = new ExtendedEV3LargeRegulatedMotor(MotorPort.B);

    public boolean isSteering = false;

    public static SteeringControl shared;

    private int radius = 14; // 13cm
    private double maxMotorArac = 5.969; // 5.969 for 360 degree motor spinn
    private double motorRadius = 0.95; // 0.95 cm
    private int speed = 250;

    private double realMaxDegree = 56.0;

    public void goForward() {
        motorRight.setSpeed(speed);
        motorLeft.setSpeed(speed);

        motorRight.forward();
        motorLeft.forward();
    }

    public void stop() {
        motorRight.stop();
        motorLeft.stop();
    }

    public void turnLeft(int angle) {
        turn(motorRight, motorLeft, angle);
    }

    public void turnRight(int angle) {
        turn(motorLeft, motorRight, angle);
    }

    private void turn(ExtendedEV3LargeRegulatedMotor activeMotor, ExtendedEV3LargeRegulatedMotor passiveMotor,
                             int angle) {
        isSteering = true;
        passiveMotor.stop(true);
        activeMotor.stop(true);

        MotorState lastActive = activeMotor.getLastState();
        MotorState lastPassive = passiveMotor.getLastState();

        // b = ((PI * r) / 180 degree) * alpha

        // Small degree correction
        int correctedAngle = correctedAngle(angle);

        double b = (double) (((Math.PI * radius) / 180) * correctedAngle);

        double neededRotationFactor = b / maxMotorArac;

        double neededRotation = neededRotationFactor * maxMotorArac;

        // alpha = b / ( (PI * r) / 180 degree );

        int motorAngleToTurn = (int) Math.round(neededRotation / ((Math.PI * motorRadius) / 180));

        passiveMotor.flt(true);

        activeMotor.rotate(motorAngleToTurn);

        activeMotor.doNewState(lastActive, true);

        passiveMotor.doNewState(lastPassive, true);

        isSteering = false;
    }

    private int correctedAngle(int angle) {
        return (int) Math.round((realMaxDegree / 90.0) * angle);
    }

    @Override
    public void setup() {
        SteeringControl.shared = this;
        this.goForward();
    }

    @Override
    public void runLoop() {

    }

    @Override
    public void shutdown() {
        this.stop();
    }
}
