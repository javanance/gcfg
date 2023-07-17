package com.gof.util;

import java.math.BigInteger;

public class MathUtil {

	public static void main(String[] args) throws Exception {		
		
        int aa = 49;
        int bb = 2;        
        
		Number nn = aa;
		nn = Long.valueOf(aa);
		nn = BigInteger.valueOf(aa);
		nn = Double.valueOf(aa);
		
		Number rr = bb;
		rr = Long.valueOf(bb);
		rr = BigInteger.valueOf(bb);
		rr = Double.valueOf(bb);		
		
		System.out.println(factorial(nn));
		System.out.println(nPr(nn, rr));
		System.out.println(nCr(nn, rr));
		
		for(int i=0; i<50; i++) System.out.println(i + "|" + nCr(nn,Double.valueOf(i)));		
	}

	
	public static Number factorial(Number n) {
		
		//System.out.println(n.getClass().getSimpleName());
		
		if(n instanceof Integer) return factorial(n.intValue());
		if(n instanceof Long) return factorial(n.longValue());
		if(n instanceof Double) return factorial(n.doubleValue());
		if(n instanceof BigInteger) return factorial(BigInteger.valueOf(n.longValue()));		
		
		return null;
	}
	
	
	private static Number substract(Number n, Number r) {		

		if(n instanceof Integer)    return n.intValue() - r.intValue();
		if(n instanceof Long)       return n.longValue() - r.longValue();
		if(n instanceof Double)     return n.doubleValue() - r.doubleValue();
		if(n instanceof BigInteger) return ((BigInteger) n).subtract(((BigInteger) r).equals(BigInteger.ZERO) ? (BigInteger.ONE):(BigInteger) r);		
		
		return null;		
	}
	
	
	private static Number divide(Number n, Number r) {		
		
		if(n instanceof Integer)    return n.intValue() / Math.max(r.intValue(), 1);
		if(n instanceof Long)       return n.longValue() / Math.max(r.longValue(), 1);
		if(n instanceof Double)     return n.doubleValue() / Math.max(r.doubleValue(), 1);
		if(n instanceof BigInteger) return ((BigInteger) n).divide((BigInteger) r);		
		
		return null;
	}
	
	
	public static Number nPr(Number n, Number r) {
		
		if(n.getClass().equals(r.getClass())) return divide(factorial(n), factorial(substract(n,r)));
		else return null;
    }
	
	
	public static Number permutation(Number n, Number r) {
		return nPr(n,r);
	}
	
	
	public static Number nCr(Number n, Number r) {
		
		if(n.getClass().equals(r.getClass())) return divide(divide(factorial(n), factorial(substract(n,r))), factorial(r));
		else return null;
    }


	public static Number combination(Number n, Number r) {
		return nCr(n,r);
	}
	
	
	public static BigInteger factorial(BigInteger n) {
		
		BigInteger fact = BigInteger.ONE;		
		
    	for(int i=1; i<=n.intValue(); i++) fact = fact.multiply(BigInteger.valueOf(i));
    	return fact;
	}	


	public static Double factorial(Double n) {		
		
		double fact = 1.0;    	
    	for(int i=1; i<=n; i++) fact *= i * 1.0;
    	return fact;
	}

	
	public static double factorial(double n) {		
		
		double fact = 1.0;    	
    	for(int i=1; i<=n; i++) fact *= (double) i * 1.0;
    	return fact;
	}

	
	public static double nCrD(double n, double r) {
		return factorial(n) / (factorial(n-r) * factorial(r));
	}
	

	public static double combinationD(double n, double r) {
	    return nCrD(n, r);
    }	
	
	
	public static Integer factorial(int n) {		
		
		int fact = 1;    	
    	for(int i=1; i<=n; i++) fact *= i;
    	return fact;
	}
	
	
	public static Long factorial(long n) {
		
		long fact = 1;    	
    	for(int i=1; i<=n; i++) fact *= i;
    	return fact;
	}	
	
	
	
//	public static int nPr(int n, int r) {
//		return factorial(n) / factorial(n-r);
//	}
//	
//	
//	public static int nCr(int n, int r) {
//		return factorial(n) / (factorial(n-r) * factorial(r));
//	}	
//
//	
//	public static long nPr(long n, long r) {
//		return factorial(n) / factorial(n-r);
//	}
//	
//	
//	public static long nCr(long n, long r) {
//		return factorial(n) / (factorial(n-r) * factorial(r));
//	}	
//
//
//	public static long permutation(long n, long r) {
//		return nPr(n, r);
//	}
//	
//	
//	public static long combination(long n, long r) {
//		return nCr(n, r);
//	}	
	
	
	public static  double[][] multi(double[][] leftMatrix,  double[][] rightMatrix) {
		int rowNum = leftMatrix.length;
		int colNum = rightMatrix[0].length;

		int effectCol = rightMatrix.length;

		if( leftMatrix[0].length >  rightMatrix.length) {
			//			 logger.info("LeftMatrix size is shortened to Matrix Multiply");
		}

		double[][] rstMatrix = new double[rowNum][colNum];
		double temp =0.0;

		for(int i= 0; i<rowNum; i++) {
			for(int j =0; j<colNum; j++) {
				//	    		logger.info("TM : {},{},{}", i,j, rightMatrix[i][j]);
				for(int k =0; k< effectCol; k++) {
					temp = temp + leftMatrix[i][k] * rightMatrix[k][j] ;
				}
				//	    		logger.info("Multi : {},{},{}", temp);
				rstMatrix[i][j] =temp;
				temp =0.0;
			}
		}

		return rstMatrix;
	}

	 
    public static double tempSum1(double... dataset) {    	

        double temp = 0;
		for(int i=0; i<dataset.length; i++) {
			temp = temp + dataset[i];
		}
		return temp;
    }


	public static double tempSum2(double[] dataset) {

        double temp = 0;
		for(int i=0; i<dataset.length; i++) {
		    temp = temp + dataset[i];
		}
		return temp;
	}

	
	public static double tempSum3(double[]... dataset) {
		
		double temp = 0;
		for(int i=0; i<dataset.length; i++) {
			for(int j=0; j<dataset[0].length; j++) {
				temp = temp + dataset[i][j];
			}
		}
		return temp;
	}


    public static int tempInt(int a, int[] arr) {
    	
    	int temp = a;
		for(int i=0; i<arr.length; i++) {
			temp = temp + arr[i];
		}
		return temp;
    }
    

    public static int tempInt2(int a, int... arr) {
    	
    	int temp = a;
		for(int i=0; i<arr.length; i++) {
			temp = temp + arr[i];
		}
		return temp;
    }	 

}
