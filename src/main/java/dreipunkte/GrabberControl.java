package dreipunkte;

import lejos.hardware.port.MotorPort;
import lejos.utility.Delay;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GrabberControl implements RunLoop {
    public final ExtendedEV3LargeRegulatedMotor grabberMotor = new ExtendedEV3LargeRegulatedMotor(MotorPort.A);
    private float upPosition;
    private float downPosition;

    private Thread emergencyStopThread;
    private boolean emergencyRun = true;

    final Logger logger = LoggerFactory.getLogger(GrabberControl.class);

    public static GrabberControl shared;

    @Override
    public void setup() {
        // Find zero positions

        grabberMotor.resetTachoCount();
        grabberMotor.setSpeed(100);
        grabberMotor.forward();

        Thread forwardThread = new Thread(() -> {
            while (!grabberMotor.isStalled()) { }
            logger.info("grabberMotor.isStalled()={}", grabberMotor.isStalled());
            upPosition = grabberMotor.getPosition();
            grabberMotor.hold();
        });

        forwardThread.start();

        while(forwardThread.isAlive()) {}

        grabberMotor.backward();

        Thread backwardThread = new Thread(() -> {
            while (!grabberMotor.isStalled()) {  }
            downPosition = grabberMotor.getPosition();
            grabberMotor.hold();
        });

        backwardThread.start();

        while(backwardThread.isAlive()) {}

        logger.info("upPosition={} downPosition={}", upPosition, downPosition);

        emergencyStopThread = new Thread(() -> {
            while (emergencyRun) {
                if (grabberMotor.isStalled()) {
                    logger.warn("Grabber motor is stalled. Emergency stop.");
                    grabberMotor.stop();
                }
                Delay.msDelay(100);
            }
        });

        emergencyStopThread.start();

        GrabberControl.shared = this;

        grabberTo(50);
    }

    @Override
    public void runLoop() {
        logger.info("runLoop");
    }

    @Override
    public void shutdown() {
        emergencyRun = false;
        grabberMotor.stop();
    }

    public void grabberTo(float input) {
        logger.info("Grab input {}", input);
        if (input > 100) {
            input = 100;
        } else if (input < 0) {
            input = 0;
        }
        input = calculateTargetPosition(input);

        logger.info("Grab to {} with currentPosition={}", input, grabberMotor.getPosition());

        if (input > grabberMotor.getPosition()) {
            grabberMotor.forward();

            while (grabberMotor.getPosition() < input) {
                logger.debug("grabberMotor.getPosition() < input");
            }
        } else {
            grabberMotor.backward();
            while (grabberMotor.getPosition() > input) {
                logger.debug("grabberMotor.getPosition() > input");
            }
        }

        grabberMotor.hold();
    }

    public float calculateTargetPosition(float input) {
        return (((upPosition - downPosition) / 100) * input) + downPosition;
    }
}
