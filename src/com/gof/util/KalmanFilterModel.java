package com.gof.util;

import org.apache.commons.math3.filter.DefaultMeasurementModel;
import org.apache.commons.math3.filter.DefaultProcessModel;
import org.apache.commons.math3.filter.KalmanFilter;
import org.apache.commons.math3.filter.MeasurementModel;
import org.apache.commons.math3.filter.ProcessModel;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.random.JDKRandomGenerator;
import org.apache.commons.math3.random.RandomGenerator;

public class KalmanFilterModel {
	
	/**
	 * TODO:
	 * KalmanFilter provides a discrete-time filter to estimate a stochastic linear process.
     * A Kalman filter is initialized with a ProcessModel and a MeasurementModel, which contain the corresponding transformation and noise covariance matrices. The parameter names used in the respective models correspond to the following names commonly used in the mathematical literature:
     * A - state transition matrix
     * B - control input matrix
     * H - measurement matrix
     * Q - process noise covariance matrix
     * R - measurement noise covariance matrix
     * P - error covariance matrix  
	 */
	
	private static ProcessModel pm;
	private static MeasurementModel mm;
	private static KalmanFilter filter;
	

	public static void main(String[] args) {
		

	}
	
	
	public static void initialize() {
		
		// A = [ 1 ]
		RealMatrix A = new Array2DRowRealMatrix(new double[] { 1d });
		// no control input
		RealMatrix B = null;
		// H = [ 1 ]
		RealMatrix H = new Array2DRowRealMatrix(new double[] { 1d });
		// Q = [ 0 ]
		RealMatrix Q = new Array2DRowRealMatrix(new double[] { 0 });
		// R = [ 0 ]
		RealMatrix R = new Array2DRowRealMatrix(new double[] { 0 });

		pm = new DefaultProcessModel(A, B, Q, new ArrayRealVector(new double[] { 0 }), null);
		mm = new DefaultMeasurementModel(H, R);
		filter = new KalmanFilter(pm, mm);		
	}
	
	
	public static void iteration_pseudo() {
		
		for (;;) {
			// predict the state estimate one time-step ahead
			// optionally provide some control input
			filter.predict();

			// obtain measurement vector z
			//RealVector z = getMeasurement();
			RealVector z = null;

			// correct the state estimate with the latest measurement
			filter.correct(z);
			   
			double[] stateEstimate = filter.getStateEstimation();
			// do something with it
		}
		
	}
	
	
	//The following example creates a Kalman filter for a static process: 
	//a system with a constant voltage as internal state. 
	// We observe this process with an artificially imposed measurement noise of 0.1V and assume an internal process noise of 1e-5V.
	public static void constVoltExam() {
		
		double constantVoltage = 10d;
		double measurementNoise = 0.1d;
		double processNoise = 1e-5d;

		// A = [ 1 ]
		RealMatrix A = new Array2DRowRealMatrix(new double[] { 1d });
		// B = null
		RealMatrix B = null;
		// H = [ 1 ]
		RealMatrix H = new Array2DRowRealMatrix(new double[] { 1d });
		// x = [ 10 ]
		RealVector x = new ArrayRealVector(new double[] { constantVoltage });
		// Q = [ 1e-5 ]
		RealMatrix Q = new Array2DRowRealMatrix(new double[] { processNoise });
		// P = [ 1 ]
		RealMatrix P0 = new Array2DRowRealMatrix(new double[] { 1d });
		// R = [ 0.1 ]
		RealMatrix R = new Array2DRowRealMatrix(new double[] { measurementNoise });

		pm = new DefaultProcessModel(A, B, Q, x, P0);
		mm = new DefaultMeasurementModel(H, R);
		filter = new KalmanFilter(pm, mm);  

		// process and measurement noise vectors
		RealVector pNoise = new ArrayRealVector(1);
		RealVector mNoise = new ArrayRealVector(1);

		RandomGenerator rand = new JDKRandomGenerator();
		// iterate 60 steps
		for (int i = 0; i < 60; i++) {
		    filter.predict();

		    // simulate the process
		    pNoise.setEntry(0, processNoise * rand.nextGaussian());

		    // x = A * x + p_noise
		    x = A.operate(x).add(pNoise);

		    // simulate the measurement
		    mNoise.setEntry(0, measurementNoise * rand.nextGaussian());

		    // z = H * x + m_noise
		    RealVector z = H.operate(x).add(mNoise);

		    filter.correct(z);

		    double voltage = filter.getStateEstimation()[0];
		}
	}
	
	
	//The following example creates a Kalman filter for a simple linear process: 
	//a vehicle driving along a street with a velocity increasing at a constant rate. 
	//The process state is modeled as (position, velocity) and we only observe the position.
	//A measurement noise of 10m is imposed on the simulated measurement.
	public static void speedVehicleExam() {
		
		// discrete time interval
		double dt = 0.1d;
		// position measurement noise (meter)
		double measurementNoise = 10d;
		// acceleration noise (meter/sec^2)
		double accelNoise = 0.2d;

		// A = [ 1 dt ]
//		     [ 0  1 ]
		RealMatrix A = new Array2DRowRealMatrix(new double[][] { { 1, dt }, { 0, 1 } });
		// B = [ dt^2/2 ]
//		     [ dt     ]
		RealMatrix B = new Array2DRowRealMatrix(new double[][] { { Math.pow(dt, 2d) / 2d }, { dt } });
		// H = [ 1 0 ]
		RealMatrix H = new Array2DRowRealMatrix(new double[][] { { 1d, 0d } });
		// x = [ 0 0 ]
		RealVector x = new ArrayRealVector(new double[] { 0, 0 });

		RealMatrix tmp = new Array2DRowRealMatrix(new double[][] {
		    { Math.pow(dt, 4d) / 4d, Math.pow(dt, 3d) / 2d },
		    { Math.pow(dt, 3d) / 2d, Math.pow(dt, 2d) } });
		// Q = [ dt^4/4 dt^3/2 ]
//		     [ dt^3/2 dt^2   ]
		RealMatrix Q = tmp.scalarMultiply(Math.pow(accelNoise, 2));
		// P0 = [ 1 1 ]
//		      [ 1 1 ]
		RealMatrix P0 = new Array2DRowRealMatrix(new double[][] { { 1, 1 }, { 1, 1 } });
		// R = [ measurementNoise^2 ]
		RealMatrix R = new Array2DRowRealMatrix(new double[] { Math.pow(measurementNoise, 2) });

		// constant control input, increase velocity by 0.1 m/s per cycle
		RealVector u = new ArrayRealVector(new double[] { 0.1d });

		pm = new DefaultProcessModel(A, B, Q, x, P0);
		mm = new DefaultMeasurementModel(H, R);
		filter = new KalmanFilter(pm, mm);

		RandomGenerator rand = new JDKRandomGenerator();

		RealVector tmpPNoise = new ArrayRealVector(new double[] { Math.pow(dt, 2d) / 2d, dt });
		RealVector mNoise = new ArrayRealVector(1);

		// iterate 60 steps
		for (int i = 0; i < 60; i++) {
		    filter.predict(u);

		    // simulate the process
		    RealVector pNoise = tmpPNoise.mapMultiply(accelNoise * rand.nextGaussian());

		    // x = A * x + B * u + pNoise
		    x = A.operate(x).add(B.operate(u)).add(pNoise);

		    // simulate the measurement
		    mNoise.setEntry(0, measurementNoise * rand.nextGaussian());

		    // z = H * x + m_noise
		    RealVector z = H.operate(x).add(mNoise);

		    filter.correct(z);

		    double position = filter.getStateEstimation()[0];
		    double velocity = filter.getStateEstimation()[1];
		}
	}		

}
