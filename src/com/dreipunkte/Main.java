package com.dreipunkte;

import lejos.utility.Delay;

public class Main {

	public static void main(String[] args) {
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
			public void run() {
				SteeringControl.stop();
			}
		}));

		SteeringControl.goForward();

		Delay.msDelay(2000);

		SteeringControl.turnLeft(45);

		Delay.msDelay(2000);

		SteeringControl.stop();
	}

}
