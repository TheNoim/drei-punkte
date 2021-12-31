package dreipunkte;

import lejos.hardware.port.MotorPort;
import lejos.utility.Delay;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SteeringControl implements RunLoop {
    public final ExtendedEV3LargeRegulatedMotor motorRight = new ExtendedEV3LargeRegulatedMotor(MotorPort.B);
    public final ExtendedEV3LargeRegulatedMotor motorLeft = new ExtendedEV3LargeRegulatedMotor(MotorPort.C);

    final Logger logger = LoggerFactory.getLogger(SteeringControl.class);

    public boolean isSteering = false;

    public static SteeringControl shared;

    private int radius = 14; // 13cm
    private double maxMotorArac = 5.969; // 5.969 for 360 degree motor spinn
    private double motorRadius = 0.95; // 0.95 cm
    private int speed = 300;
    private int turnSpeed = 250;

    private double realMaxDegree = 56.0;

    private Thread emergencyStopThread;
    private boolean emergencyRun = true;

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

        activeMotor.setSpeed(turnSpeed);

        activeMotor.rotate(motorAngleToTurn);

        activeMotor.setSpeed(speed);

        isSteering = false;
    }

    private int correctedAngle(int angle) {
        return (int) Math.round((realMaxDegree / 90.0) * angle);
    }

    public void go(int distance, boolean backward) {
        // b / ((PI * r) / 180 degree) = alpha
        int alpha = (int) (distance / ((Math.PI * radius) / 180));
        if (backward) {
            alpha = alpha * -1;
        }
        motorLeft.rotate(alpha, true);
        motorRight.rotate(alpha);
    }

    public void brake() {
        motorRight.brake();
        motorLeft.brake();
    }

    @Override
    public void setup() {
        SteeringControl.shared = this;
        this.goForward();
        emergencyStopThread = new Thread(() -> {
            while (emergencyRun) {
                if (motorRight.isStalled()) {
                    logger.warn("motorRight is stalled. Emergency stop.");
                    motorRight.stop();
                }
                if (motorLeft.isStalled()) {
                    logger.warn("motorLeft is stalled. Emergency stop.");
                    motorRight.stop();
                }
                Delay.msDelay(100);
            }
        });

        emergencyStopThread.start();
    }

    @Override
    public void runLoop() {

    }

    @Override
    public void shutdown() {
        emergencyRun = false;
        this.stop();
    }
}
