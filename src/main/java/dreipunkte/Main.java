package dreipunkte;

import ev3dev.sensors.ev3.EV3ColorSensor;
import lejos.hardware.port.SensorPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class Main {

    final static Logger logger = LoggerFactory.getLogger(Main.class);

    private static EV3ColorSensor color1 = new EV3ColorSensor(SensorPort.S1);

    public static void main(String[] args) {
        // java is stupid
        final boolean[] runLoop = {true};

        RunLoop[] loopInstances = { /* new GrabberControl(), */new ThreadLock(), new FollowLine(), new ObstacleControl(), new SteeringControl() };

        Runtime.getRuntime().addShutdownHook(new Thread(() -> runLoop[0] = false));

        for (int i = 0; i < loopInstances.length; i++) {
            logger.info("Setup {}", loopInstances[i].getClass().getName());
            loopInstances[i].setup();
        }

        while (runLoop[0]) {
            List<Thread> threads = new ArrayList<>();

            for (int i = 0; i < loopInstances.length; i++) {
                int finalI = i;
                threads.add(new Thread(() -> {
                    loopInstances[finalI].runLoop();
                }));
                threads.get(i).start();
            }

            for (int i = 0; i < threads.size(); i++) {
                while (threads.get(i).isAlive()) { }
            }
        }

        for (int i = 0; i < loopInstances.length; i++) {
            logger.info("Shutdown {}", loopInstances[i].getClass().getName());
            loopInstances[i].shutdown();
        }
    }

}
