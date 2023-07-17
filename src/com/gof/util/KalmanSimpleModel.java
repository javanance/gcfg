package com.gof.util;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class KalmanSimpleModel {
	
	
	private static double A;    // for x_(k+1) = x_k + w_k(=0)
	private static double H;    // for z_k = x_k + v_k
	private static double Q;    // for w_k = 0
	private static double R;    // for y ~ N(0,2) 
	private static double K;    // Kalman Gain	
	
	private static double X;    // for initial input X
	private static double P;    // for initial error co-variance
	
	
	/**
	 * Source: https://blog.naver.com/leeinje66/221983736260
	 * @param args
	 */

	public static void main(String[] args) throws Exception {
		
		//log.info("abc");
		
		//double bbb = 0;  // for check AVG value
		
		double[] Xsaved = new double[100];
		double[] Ysaved = new double[100];
		initSimpleKalman();
		
		for(int i=0; i<Xsaved.length; i++) {
			
			double aaa = getSignal(14, 0, 2);
			//bbb = bbb + aaa;
			
			Xsaved[i] = aaa;
			Ysaved[i] = loopSimpleKalman(aaa);
			
			log.info("{}, {}, {}, {}, {}, {}, {}, {}, {}", Xsaved[i], Ysaved[i], K, A, H, Q, R, X, P);
		}
		//log.info("{}", bbb/100);
	}
	
	
	protected static void initSimpleKalman() {
		
		A  =  1;   // for x_(k+1) = x_k + w_k(=0)
		H  =  1;   // for z_k = x_k + v_k
		Q  =  0;   // for w_k = 0
		R  =  4;   // for y ~ N(0,2) 
		
		X = 14;   // for initial input X
		P =  1;   // for initial error co-variance  (in example, P is set to 6)
	}
	
	
	protected static double loopSimpleKalman(double input) {
		
		double Xp = A * X;                 // 추정값예측
		double Pp = A * P * A  + Q;        // 오차공분산 예측, 2nd A is actually transpose A
		
		K = Pp * H / (H * Pp * H + R);     //Calculating Kalman Gain, 1st and 3rd H is transpose H
		
		X = Xp + K * (input - H * Xp);     // 추정값계산
		P = Pp - K * H * Pp;
		
		return X;
	}
	
	
	protected static double getSignal(double level) throws Exception{		
		
		return getSignal(level, 0, 1);
	}
	
	
	protected static double getSignal(double level, double mu, double sigma) throws Exception {
		return level + StdRandomGenerator.gaussian(mu, sigma);
	}	

}
