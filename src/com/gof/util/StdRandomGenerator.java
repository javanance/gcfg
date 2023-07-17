package com.gof.util;

import java.util.Random;

public class StdRandomGenerator {
	
    private static Random random;
    
    private static long seed;    
    
    static {
    	seed = System.currentTimeMillis();
    	random = new Random(seed);
    }
    
    private StdRandomGenerator() {}
    
    public static double uniform() {
    	return random.nextDouble();
    }
    
    
    public static int uniform(int n) throws Exception {
    	if(n<=0) throw new Exception("n must be positive");
    	return random.nextInt(n);
    }
    
    
    public static double uniform(double a, double b) throws Exception {
    	
    	if(!(a<b)) {
    		throw new Exception("invalid range");    		
    	}
    	return a + uniform() * (b-a);
    }
    
	
    public static double gaussian() throws Exception {
    	
    	double r, x, y;
    	
    	do {
    		x = uniform(-1.0, 1.0);
    		y = uniform(-1.0, 1.0);
    		r = x*x + y*y;
    	} while (r>=1 || r==0);
    	
    	return x * Math.sqrt(-2 *  Math.log(r) / r);
    }
    
    
    public static double gaussian(double mu, double sigma) throws Exception {
    	return mu + sigma * gaussian();
    }
    
    
    
    
	public static void main(String[] args) throws Exception {

//		double z = 820;
//		double mu = 1019;
//		double sigma = 209;

//		double z = 1500;
//		double mu = 1019;
//		double sigma = 209;
		
//		double z = 1500;
//		double mu = 1025;
//		double sigma = 231;
//
//		System.out.println(cdf(z, mu, sigma));
//		double y = cdf(z);
//		System.out.println(inverseCDF(y));
	}
    

    
}


  
