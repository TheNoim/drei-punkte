package dreipunkte;

import ev3dev.sensors.ev3.EV3TouchSensor;
import ev3dev.sensors.ev3.EV3UltrasonicSensor;
import lejos.hardware.port.SensorPort;
import lejos.robotics.SampleProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ObstacleControl implements RunLoop {
    private EV3UltrasonicSensor right = new EV3UltrasonicSensor(SensorPort.S4);
    private EV3TouchSensor touchLeft = new EV3TouchSensor(SensorPort.S3);
    private SampleProvider spRight;
    private SampleProvider spLeft;
    public static ObstacleControl shared;
    float [] sampleRight;
    float [] sampleLeft;
    final Logger logger = LoggerFactory.getLogger(ObstacleControl.class);
    public boolean foundObstacle = false;

    @Override
    public void setup() {
        ObstacleControl.shared = this;
        spRight = right.getDistanceMode();
        spLeft = touchLeft.getTouchMode();
        sampleRight = new float[spRight.sampleSize()];
        sampleLeft = new float[spLeft.sampleSize()];
        spRight.fetchSample(sampleRight, 0);
        spLeft.fetchSample(sampleLeft, 0);
    }

    @Override
    public void runLoop() {
        logger.info("runLoop");
        synchronized (ThreadLock.shared.mutex) {
            spRight.fetchSample(sampleRight, 0);
            spLeft.fetchSample(sampleLeft, 0);
        }
        int distanceValueRight = (int) sampleRight[0];
        int distanceValueLeft = (int) sampleLeft[0];
        logger.info("distanceValueRight={} distanceValueLeft={}", distanceValueRight, distanceValueLeft);
        // Delay.msDelay(1000);
        if (distanceValueLeft > 0) {
            // System.exit(0);
            synchronized (ThreadLock.shared.mutex) {
                logger.info("Every time we touch!");
                foundObstacle = true;
                SteeringControl.shared.stop();
                SteeringControl.shared.go(70, true);
                SteeringControl.shared.turnRight(23);
                SteeringControl.shared.go(100, false);
                SteeringControl.shared.turnLeft(23);
                SteeringControl.shared.go(200, false);
                SteeringControl.shared.turnLeft(23);
                SteeringControl.shared.go(100, false);
                SteeringControl.shared.turnRight(23);
            }
        } else if (distanceValueRight < 15) {
            synchronized (ThreadLock.shared.mutex) {
                logger.info("Too close");
                // System.exit(0);
                foundObstacle = true;
                SteeringControl.shared.stop();
                SteeringControl.shared.go(70, true);
                SteeringControl.shared.turnLeft(23);
                SteeringControl.shared.go(100, false);
                SteeringControl.shared.turnRight(23);
                SteeringControl.shared.go(200, false);
                SteeringControl.shared.turnRight(23);
                SteeringControl.shared.go(100, false);
                SteeringControl.shared.turnLeft(23);
            }
        }
        foundObstacle = false;
    }

    @Override
    public void shutdown() {

    }
}
