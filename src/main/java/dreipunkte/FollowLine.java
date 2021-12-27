package dreipunkte;

import ev3dev.sensors.ev3.EV3ColorSensor;
import lejos.hardware.port.SensorPort;
import lejos.robotics.Color;
import lejos.robotics.SampleProvider;
import lejos.utility.Delay;

public class FollowLine implements RunLoop {
    private EV3ColorSensor colorLeft = new EV3ColorSensor(SensorPort.S1);
    private EV3ColorSensor colorRight = new EV3ColorSensor(SensorPort.S2);

    private SampleProvider spLeft;
    private SampleProvider spRight;
    private float[] sampleLeft;
    private float[] sampleRight;

    private int lineColorId = Color.BLACK;
    private int nothingId = Color.MAGENTA;
    private int ballMarkerColorId = Color.BLUE;

    @Override
    public void setup() {
        spLeft = colorLeft.getColorIDMode();
        spRight = colorRight.getColorIDMode();

        sampleLeft = new float[spLeft.sampleSize()];
        sampleRight = new float[spRight.sampleSize()];
    }

    @Override
    public void runLoop() {
        if (SteeringControl.shared.isSteering) {
            return;
        }
        colorLeft.fetchSample(sampleLeft, 0);
        colorRight.fetchSample(sampleRight, 0);

        int colorIdLeft = (int) sampleLeft[0];
        int colorIdRight = (int) sampleRight[0];

        System.out.format("colorIdLeft=%d colorIdRight=%d \n", colorIdLeft, colorIdRight);

        if (colorIdRight == lineColorId) {
            System.out.println("Go right");
            SteeringControl.shared.turnRight(10);
        } else if (colorIdLeft == lineColorId) {
            System.out.println("Go left");
            SteeringControl.shared.turnLeft(10);
        } else {
            Delay.msDelay(100);
        }
    }

    @Override
    public void shutdown() {

    }
}
