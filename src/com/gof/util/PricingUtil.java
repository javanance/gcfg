package com.gof.util;

import com.gof.interfaces.Constant;
import com.gof.interfaces.Instrument;

import lombok.extern.slf4j.Slf4j;


@Slf4j
public class PricingUtil implements Constant {  
    
	public static void main(String[] args) throws Exception {
		
//		double s = 23.75;
//		double x = 15.00;
//		double r = 0.01;
//		double sigma = 0.35;
//		double t = 0.5;
//		double days = t*365;
		
//		8.827124531389066
//		8.923746442513103
		
//		double s = 30.14;
//		double x = 15.00;
//		double r = 0.01;
//		double sigma = 0.332;
//		double t = 0.25;
//		double days = t*365;
		
//		15.177453164443504
//		15.199648900096312
		
//		double s = 276.04;  //271.35(11/30) + 2.81 -> (11/29) PRICE		
//		double x = 272.5;
//		double r = 0.01;
//		double sigma = 0.1882;
//		double t = 11/365.;
//		double days = t*365.;		
//		s = s * 0.65;

//		double s = 266.22;
//		s = s * 0.65;
//		double x = 255.0;
//		double r = 0.014881695; //0.0333 (20120418)  //0.014881695
//		double sigma = 0.1867;  //0.2189  //0.1867
//		double t = 22/365.;
//		double days = t*365;		
//	
		//71.73733870579346  for 0.014881695
		//81.72837293858959
		
		//71.46574796124659  for 0.0333
		//81.44569685751657
		
		
		//FOR KRA5233018B0
//		double s = 262.95;		
//		double x = 264.01;				
//		double r = 0.017;    //for Stability of finding ImpliedSigma... It may set r to zero....
//		r = 0.01;
//		//r = 0.001;
//		double sigma = 0.02139;
//		sigma = 0.04612513656220108;
//		
//		double t = 368./365.;		
//		double days = t*365;		
//		double val = 4.356164989;		
//		
//		s = 261.98; 	
//		s = s * 0.65;
//		sigma = 0.016837487846802106; 
//		sigma = 0.00737238486458955;
//		sigma += 0.19;
//		t = 308./365.;
//		days = t*365;
//		val = 0.80576;		
		
		
		double s = 262.95;		
		double x = 264.01;				
		double r = 0.01;    //for Stability of finding ImpliedSigma... It may set r to zero....
//		r = 0.01;
		//r = 0.001;
		double sigma = 0.02139;
		sigma = 0.038539273262853384;
		
		double t = 368./365.;		
		double days = t*365;		
		double val = .80576;		
		
		s = 261.98;		
		x = 264.01;
		//s = s * 0.65;
		
		r = 0.01;
		t = 308./365.;
		val = 0.80576;
		sigma = 0.00737238486458955;
		sigma = 0.009344593980218634;
		//sigma += 0.19;
		//sigma = 0.001;
		

		//days = t*365;
		//val = 0.000;		
		
		//System.out.println("s=" + sigma);
		System.out.println(getImpliedSigma(true, "BSE", s, x, r, t, val));	
		System.out.println(getImpliedSigma(false, "BSE", s, x, r, t, val));
		System.out.println();
		
		System.out.println(callPriceBSE(s, x, r, sigma, t));
		System.out.println(callPriceKRX(s, x, r, sigma, days));
		System.out.println(callPriceMCS(s, x, r, sigma, t));
		System.out.println();
		
		System.out.println(putPriceBSE(s, x, r, sigma, t));
		System.out.println(putPriceKRX(s, x, r, sigma, days));
		System.out.println(putPriceMCS(s, x, r, sigma, t));

	}	
	
	
	public static double optionPrice(boolean isCall, double s, double x, double r, double sigma, double t) throws Exception {		
		return optionPrice(isCall, Instrument.OPTION_PRICING_BSE, s, x, r, sigma, t);		
	}
	
	
	public static double optionPrice(boolean isCall, String pricingMeth, double s, double x, double r, double sigma, double t) throws Exception {
		
		if(isCall) {
			switch(pricingMeth) {
			    case(Instrument.OPTION_PRICING_BSE): return callPriceBSE(s, x, r, sigma, t);
			    case(Instrument.OPTION_PRICING_KRX): return callPriceKRX(s, x, r, sigma, 365.0*t);
			    case(Instrument.OPTION_PRICING_MCS): return callPriceMCS(s, x, r, sigma, t);
			    default                            : return callPriceBSE(s, x, r, sigma, t);
			}
		}
		else {
			switch(pricingMeth) {
		        case(Instrument.OPTION_PRICING_BSE): return putPriceBSE(s, x, r, sigma, t);
		        case(Instrument.OPTION_PRICING_KRX): return putPriceKRX(s, x, r, sigma, 365.0*t);
		        case(Instrument.OPTION_PRICING_MCS): return putPriceMCS(s, x, r, sigma, t);
		        default                            : return putPriceBSE(s, x, r, sigma, t);
			}
		}		
	}
	

	public static double getImpliedSigma(boolean isCall, double s, double x, double r, double t, double mktPrice) throws Exception {
		return getImpliedSigma(isCall, Instrument.OPTION_PRICING_BSE, s, x, r, t, mktPrice, INITIAL_GUESS);		
	}

	
	public static double getImpliedSigma(boolean isCall, String pricingMeth, double s, double x, double r, double t, double mktPrice) throws Exception {
		return getImpliedSigma(isCall, pricingMeth, s, x, r, t, mktPrice, INITIAL_GUESS);		
	}
	
	
	public static double getImpliedSigma(boolean isCall, String pricingMeth, double s, double x, double r, double t, double mktPrice, double initGuess) throws Exception {
		
		double sigmaPos   = 0.0;
		double sigmaNeg   = 0.0;
		double sigmaNew   = 0.0;
		double fnSigmaPos = 0.0;
		double fnSigmaNeg = 0.0;
		double fnSigmaNew = 0.0;
		
    	if(Math.abs(initGuess) <= 0) initGuess = INITIAL_GUESS;    	
    	sigmaPos = Math.min( initGuess,  10.0);    	
    	sigmaNeg = 0.001;    	

    	fnSigmaPos = optionPrice(isCall, pricingMeth, s, x, r, sigmaPos, t) - mktPrice;		
    	fnSigmaNeg = optionPrice(isCall, pricingMeth, s, x, r, sigmaNeg, t) - mktPrice;
    	if(Double.isNaN(fnSigmaPos) || Double.isNaN(fnSigmaNeg)) return 10 * ONE_BP;
    	
//    	log.info("Initial Fn Value: {}, {}, {}, {}, {}, {}, {}", isCall, pricingMeth, s, x, r, sigmaNeg, t);
//    	log.info("Initial Fn Value: {}, {}", fnSigmaPos, fnSigmaNeg);
		
    	if(fnSigmaPos * fnSigmaNeg > ZERO_DOUBLE) {
		
			for(int i=0; i<Constant.MAX_INITIAL_GUESS; i++) {    		    			
				log.warn("Changing Initial Guess in Calculating Implied Volatility [SEQ:{}]", i+1);    			
				
				sigmaPos  *= 2.0;
				sigmaNeg  *= 2.0;    			
				fnSigmaPos = optionPrice(isCall, pricingMeth, s, x, r, sigmaPos, t) - mktPrice;		
				fnSigmaNeg = optionPrice(isCall, pricingMeth, s, x, r, sigmaNeg, t) - mktPrice;	        	
	
	        	if(fnSigmaPos * fnSigmaNeg < ZERO_DOUBLE) break;
	        	if(Double.isNaN(fnSigmaPos) || Double.isNaN(fnSigmaNeg)) return 10 * ONE_BP;
			}
    	}
    	if(fnSigmaPos * fnSigmaNeg > ZERO_DOUBLE) return 10 * ONE_BP;    	

    	sigmaNew = 0.5 * (sigmaPos + sigmaNeg);
    	
    	for(int i=0; i<MAX_ITERATION; i++) {    		

    		fnSigmaNew = optionPrice(isCall, pricingMeth, s, x, r, sigmaNew, t) - mktPrice;    		
    		
//    		log.info("Sigma: {}, {}, {}", sigmaNew, sigmaPos, sigmaNeg);

    		if(fnSigmaNeg * fnSigmaNew < 0) {
    			
    			fnSigmaPos = fnSigmaNew;
    			sigmaPos   = sigmaNew;
    			sigmaNew   = 0.5 * (sigmaPos + sigmaNeg);    			
    			if(Math.abs(sigmaNew - sigmaPos) < ZERO_DOUBLE) return sigmaNew;
    		}
        	else if(fnSigmaPos * fnSigmaNew < 0) {
        		
        		fnSigmaNeg = fnSigmaNew;
        		sigmaNeg   = sigmaNew;
        		sigmaNew   = 0.5 * (sigmaPos + sigmaNeg);        		
        		if(Math.abs(sigmaNew - sigmaNeg) < ZERO_DOUBLE) return sigmaNew;
        	}
        	else return 10 * ONE_BP;    		
    	}    	
    	log.warn("Calculating Implied Volatility is failed");    	
    	return 10 * ONE_BP;    	
    }   	
	
	/**
	 * KRX Methodology
	 */
	public static double callPriceKRX(double s, double x, double r, double sigma, double days) {
		
		int N = 49;		
		
		double a = Math.exp( sigma*Math.sqrt(days/365/N));		
		double b = Math.exp(-sigma*Math.sqrt(days/365/N));
		double u = (Math.exp(r*days/365/N) - b) / (a-b);
		double d = 1-u;
		
		double value = 0.0;
		for(int k=0; k<=N; k++) {
			value += MathUtil.combination(Double.valueOf(N),Double.valueOf(k)).doubleValue() * Math.pow(u,k) * Math.pow(d, N-k) * Math.max((s * Math.pow(a,k) * Math.pow(b, N-k) - x), 0);			
			//value += MathUtil.combinationD(N, k) * Math.pow(u,k) * Math.pow(d,N-k) * Math.max((s * Math.pow(a,k) * Math.pow(b,N-k) - x), 0);			
		}		
		return Math.exp(-r*days/365) * value;		
	}	
	

	public static double putPriceKRX(double s, double x, double r, double sigma, double days) {
		
		int N = 49;
		
		double a = Math.exp( sigma*Math.sqrt(days/365/N));
		double b = Math.exp(-sigma*Math.sqrt(days/365/N));
		
		double u = (Math.exp(r*days/365/N) - b) / (a-b);
		double d = 1-u;
		
		double value = 0.0;
		for(int k=0; k<=N; k++) {
			value += MathUtil.combination(Double.valueOf(N),Double.valueOf(k)).doubleValue() * Math.pow(u,k) * Math.pow(d, N-k) * Math.max(x - (s * Math.pow(a,k) * Math.pow(b, N-k)), 0);
			//value += MathUtil.combinationD(N, k) * Math.pow(u,k) * Math.pow(d,N-k) * Math.max(x - (s * Math.pow(a,k) * Math.pow(b,N-k)), 0);
		}		
		return Math.exp(-r*days/365) * value;		
	}
	
    //Black-Scholes	
    public static double callPriceBSE(double s, double x, double r, double sigma, double t) {
    	
    	double d1 = (Math.log(s/x) + (r + sigma*sigma/2)*t) / (sigma * Math.sqrt(t));
    	double d2 = d1 - sigma * Math.sqrt(t);
    	
    	return s * GaussianDistribution.cdf(d1) - x * Math.exp(-r*t) * GaussianDistribution.cdf(d2);
    }
    
    //Black-Scholes	
    public static double putPriceBSE(double s, double x, double r, double sigma, double t) {
    	
    	double d1 = (Math.log(s/x) + (r + sigma*sigma/2)*t) / (sigma * Math.sqrt(t));
    	double d2 = d1 - sigma * Math.sqrt(t);
    	
    	return x * Math.exp(-r*t) * GaussianDistribution.cdf(-d2) - s * GaussianDistribution.cdf(-d1);
    }    
    
    //Monte-Carlo Simulation 
    public static double callPriceMCS(double s, double x, double r, double sigma, double t) throws Exception {
    	
    	int n = 10000;
    	double sum = 0.0;
    	
    	for(int i=0; i<n; i++) {
    		
    		double eps = StdRandomGenerator.gaussian();
    		double price = s * Math.exp(r*t - 0.5*sigma*sigma*t + sigma*eps*Math.sqrt(t));
    		double value = Math.max(price-x, 0);
    		sum += value;    		
    	}
    	double mean = sum/n;
    	
    	return Math.exp(-r*t) * mean;
    }        
    
    //Monte-Carlo Simulation 
    public static double putPriceMCS(double s, double x, double r, double sigma, double t) throws Exception {
    	
    	int n = 10000;
    	double sum = 0.0;
    	
    	for(int i=0; i<n; i++) {
    		
    		double eps = StdRandomGenerator.gaussian();
    		double price = s * Math.exp(r*t - 0.5*sigma*sigma*t + sigma*eps*Math.sqrt(t));
    		double value = Math.max(x-price, 0);
    		sum += value;    		
    	}
    	double mean = sum/n;
    	
    	return Math.exp(-r*t) * mean;
    }    
    
    //Monte-Carlo Simulation
    public static double callPriceMCS2(double s, double x, double r, double sigma, double t) throws Exception {
    	
    	int n = 10000;
    	double sum = 0.0;    	
    	
    	for(int i=0; i<n; i++) {
    		
    		double price = s;
    		double dt = t / 10000.0;
    		
    		for(double time=0; time <=t;  time += dt) {
    			price += r*price*dt + sigma*price*Math.sqrt(dt)*StdRandomGenerator.gaussian();    			
    		}    		
    		double value = Math.max(price-x, 0);
    		sum += value;    		
    	}    	
    	double mean = sum / n;
    	
    	return Math.exp(-r*t) * mean;
    }    
        
}

  
    
      