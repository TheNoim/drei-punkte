package dreipunkte;

import ev3dev.sensors.ev3.EV3ColorSensor;
import lejos.hardware.port.SensorPort;
import lejos.robotics.Color;
import lejos.robotics.SampleProvider;
import lejos.utility.Delay;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FollowLine implements RunLoop {
    private EV3ColorSensor colorLeft = new EV3ColorSensor(SensorPort.S1);
    private EV3ColorSensor colorRight = new EV3ColorSensor(SensorPort.S2);

    final Logger logger = LoggerFactory.getLogger(FollowLine.class);

    private SampleProvider spLeft;
    private SampleProvider spRight;
    private float[] sampleLeft;
    private float[] sampleRight;

    private int lineColorId = Color.BLACK;
    private int nothingId = Color.MAGENTA;
    private int ballMarkerColorId = Color.BLUE;

    private LastDetected lastDetected = LastDetected.NONE;

    private int turnDegree = 10;

    @Override
    public void setup() {
        spLeft = colorLeft.getColorIDMode();
        spRight = colorRight.getColorIDMode();

        sampleLeft = new float[spLeft.sampleSize()];
        sampleRight = new float[spRight.sampleSize()];

        colorLeft.fetchSample(sampleLeft, 0);
        colorRight.fetchSample(sampleRight, 0);
    }

    @Override
    public void runLoop() {
        logger.info("runLoop");
        if (SteeringControl.shared.isSteering) {
            return;
        }
        while (true && !ObstacleControl.shared.foundObstacle) {
            synchronized (ThreadLock.shared.mutex) {
                colorLeft.fetchSample(sampleLeft, 0);
                colorRight.fetchSample(sampleRight, 0);
            }

            int colorIdLeft = (int) sampleLeft[0];
            int colorIdRight = (int) sampleRight[0];

            logger.info("colorIdLeft={} colorIdRight={}", colorIdLeft, colorIdRight);

            // continue last turn and default prefer right
            if (lastDetected == LastDetected.NONE || lastDetected == LastDetected.RIGHT) {
                if (colorIdRight == lineColorId) {
                    synchronized (ThreadLock.shared.mutex) {
                        logger.info("Go right");
                        SteeringControl.shared.turnRight(turnDegree);
                        lastDetected = LastDetected.RIGHT;
                    }
                } else if (colorIdLeft == lineColorId) {
                    synchronized (ThreadLock.shared.mutex) {
                        logger.info("Go left");
                        SteeringControl.shared.turnLeft(turnDegree);
                        lastDetected = LastDetected.LEFT;
                    }
                } else {
                    synchronized (ThreadLock.shared.mutex) {
                        lastDetected = LastDetected.NONE;
                        if (!ObstacleControl.shared.foundObstacle) SteeringControl.shared.goForward();
                    }
                    break;
                }
            } else {
                if (colorIdLeft == lineColorId) {
                    synchronized (ThreadLock.shared.mutex) {
                        logger.info("Go left");
                        SteeringControl.shared.turnLeft(turnDegree);
                        lastDetected = LastDetected.LEFT;
                    }
                } else if (colorIdRight == lineColorId) {
                    synchronized (ThreadLock.shared.mutex) {
                        logger.info("Go right");
                        SteeringControl.shared.turnRight(turnDegree);
                        lastDetected = LastDetected.RIGHT;
                    }
                } else {
                    synchronized (ThreadLock.shared.mutex) {
                        lastDetected = LastDetected.NONE;
                        if (!ObstacleControl.shared.foundObstacle) SteeringControl.shared.goForward();
                    }
                    break;
                }
            }
            Delay.msDelay(50);
        }
    }

    @Override
    public void shutdown() {

    }
}
