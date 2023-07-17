package com.gof.util;

public class GaussianDistribution {  
    
	public static void main(String[] args) {
		double z = 820;
		double mu = 1019;
		double sigma = 209;

//		double z = 1500;
//		double mu = 1019;
//		double sigma = 209;
		
//		double z = 1500;
//		double mu = 1025;
//		double sigma = 231;

		System.out.println(cdf(z, mu, sigma));
		double y = cdf(z);
		System.out.println(inverseCDF(y));
	}
    
    
    public static double pdf(double x) {
    	return Math.exp(-x*x / 2) / Math.sqrt(2*Math.PI);
    }
    
    
    public static double pdf(double x, double mu, double sigma) {
    	return pdf((x-mu)/sigma) / sigma;    	
    }
    
   
    public static double cdf(double z) {
    	if(z < -8.0) return 0.0;
    	if(z > 8.0)  return 1.0;
    	double sum = 0.0;
    	double term = z;
    	
    	for (int i=3; sum+term!=sum; i+=2) {
    		sum = sum + term;
    		term = term * z*z/i;
    	}    	
    	return 0.5 + sum*pdf(z);
    }
    
    	
    public static double cdf(double z, double mu, double sigma) {
    	return cdf((z-mu) / sigma);
    }
    
    
    public static double inverseCDF(double y) {
    	return inverseCDF(y, 0.00000001, -8, 8);    	
    }
    
    
    private static double inverseCDF(double y, double delta, double lo, double hi) {
    	double mid = lo + (hi-lo)/2;
    	if(hi-lo < delta) return mid;
    	if(cdf(mid) > y) return inverseCDF(y, delta, lo, mid);
    	else return inverseCDF(y, delta, mid, hi);
    }
    
    
    public static double phi(double x) {
    	return pdf(x);
    }
    
    
    public static double phi(double x, double mu, double sigma) {
    	return pdf(x, mu, sigma);
    }
    
    
    public static double Phi(double z) {
    	return cdf(z);
    }
    
    
    public static double Phi(double z, double mu, double sigma) {
    	return cdf(z, mu, sigma);
    }
    
    
    public static double PhiInverse(double y) {
    	return inverseCDF(y);
    }    
        
}
  
