package dreipunkte;

import ev3dev.sensors.ev3.EV3ColorSensor;
import lejos.hardware.port.SensorPort;

public class Main {

    private static EV3ColorSensor color1 = new EV3ColorSensor(SensorPort.S1);

    public static void main(String[] args) {
        // java is stupid
        final boolean[] runLoop = {true};

        RunLoop[] loopInstances = { new FollowLine(), new SteeringControl() };

        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            public void run() {
                runLoop[0] = false;
            }
        }));

        for (int i = 0; i < loopInstances.length; i++) {
            System.out.format("Setup %s", loopInstances[i].getClass().getName());
            loopInstances[i].setup();
        }

        while (runLoop[0]) {
            for (int i = 0; i < loopInstances.length; i++) {
                loopInstances[i].runLoop();
            }
        }

        for (int i = 0; i < loopInstances.length; i++) {
            System.out.format("Shutdown %s", loopInstances[i].getClass().getName());
            loopInstances[i].shutdown();
        }
    }

}
