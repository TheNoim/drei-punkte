package com.dreipunkte;

import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.Port;
import lejos.hardware.port.TachoMotorPort;


public class ExtendedEV3LargeRegulatedMotor extends EV3LargeRegulatedMotor {
	private MotorState currentMotorState = MotorState.STOP;
	private MotorState lastMotorState = MotorState.STOP;
	
	/**
	 * Original constructor
	 * @param port
	 */
	public ExtendedEV3LargeRegulatedMotor(TachoMotorPort port) {
		super(port);
	}

	/**
	 * Original constructor
	 * @param port
	 */
	public ExtendedEV3LargeRegulatedMotor(Port port) {
		super(port);
	}

	@Override
	public void flt() {
		this.setNewMotorState(MotorState.FLT);
		super.flt();
	}
	
	@Override
	public void flt(boolean immediateReturn) {
		this.setNewMotorState(MotorState.FLT);
		super.flt(immediateReturn);
	}
	
	@Override
    public void forward()
    {
		this.setNewMotorState(MotorState.FORWARD);
		super.forward();
    }

	@Override
    public void backward()
    {
		this.setNewMotorState(MotorState.BACKWARD);
		super.backward();
    }
	
	@Override
	public void stop() {
		this.setNewMotorState(MotorState.STOP);
		super.stop();
	}
	
	@Override
	public void stop(boolean immediateReturn) {
		this.setNewMotorState(MotorState.STOP);
		super.stop(immediateReturn);
	}
	
    public void forward(boolean immediateReturn)
    {
        reg.newMove(speed, acceleration, +NO_LIMIT, true, !immediateReturn);
    }

	public void backward(boolean immediateReturn)
    {
        reg.newMove(speed, acceleration, -NO_LIMIT, true, !immediateReturn);
    }
	
	public void doNewState(MotorState state, boolean immediateReturn) {
		switch (state) {
		case STOP:
			this.stop(immediateReturn);
			break;
		case FLT:
			this.flt(immediateReturn);
			break;
		case FORWARD:
			this.forward(immediateReturn);
			break;
		case BACKWARD:
			this.backward(immediateReturn);
			break;
		}
	}
	
	public void doNewState(MotorState state) {
		this.doNewState(state, false);
	}
	
	private void setNewMotorState(MotorState newState) {
		this.lastMotorState = this.currentMotorState;
		this.currentMotorState = newState;
	}
	
	public MotorState getCurrentState() {
		return this.currentMotorState;
	}
	
	public MotorState getLastState() {
		return this.lastMotorState;
	}
}
