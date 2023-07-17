package com.gof.test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.stat.regression.OLSMultipleLinearRegression;
import org.apache.commons.math3.stat.regression.SimpleRegression;
import org.ejml.simple.SimpleMatrix;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gof.enums.EBoolean;

public class Test1 {
	
	public static final Logger logger = LoggerFactory.getLogger("JUST_TEST");

	
	public static void main(String[] args) {		
		
		logger.info("{}", EBoolean.valueOf("Y").isTrueFalse());
		
		logger.info("{}", LocalDateTime.now());
		
		logger.info("{}", LocalDateTime.now().format(DateTimeFormatter.ofPattern("YYYY-MM-dd HH:mm:ss")));
		
		
		int a = 3, b=4;		
		logger.info("{}, {}, {}", 1.0 * a / b, (double) (1.0 * (double)(a /b) ), (double) (a/b));		
		
		String format = "%0" + a + "d";
		String attr = "4";
		
		System.out.println(String.format(format, Integer.valueOf(attr)));		
		
		String isinCd = "KR4167M60002";		
		logger.info("test: {}", isinCd.substring(3, 6));
		//[12:27:20][INFO ] test: 167
		
		
		
		//testMatrix1();
//		logger.info("{}", add(3, 4));
//		testSimple();
//
//        String aaa = "ABCD234";
//        logger.info("{}", aaa.substring(0));
		
		//reg();
//		
//		int a = 2;
//		
//		switch(a) {
//		case 1: System.out.println("1"); break;
//		case 2: System.out.println("2"); break;
//		case 3: System.out.println("3"); break;
//		default: break;
//		}
		
		LocalDate date1 = LocalDate.of(2017, 12, 21);
		LocalDate date2 = LocalDate.of(2017, 12, 20);
//	
		if(date1.isBefore(date2)) logger.info("111");
		if(date1.isAfter(date2)) logger.info("222");
		//else logger.info("000");
		
	}
	
	
	public static void reg() {
		
		SimpleRegression sr111 = new SimpleRegression(true);
		sr111.addData(1, 2);
		//sr111.addData(2, 3);
		logger.info("{}, {}, {}", sr111.getSlope(), sr111.getIntercept());
		
		
		SimpleRegression simpleRegression = new SimpleRegression(true);	
		simpleRegression.addData(new double[][] { {1, 2}, {2, 3}, {3, 4}, {4, 5}, {5, 6} });
		
		
		
		logger.info("{}, {}, {}", simpleRegression.getSlope(), simpleRegression.getIntercept(), simpleRegression.predict(1.5));	
			
			
		SimpleRegression sr = new SimpleRegression(true);	
			
	    double[][] x = {{1,0},{0,0},{1,0},{2.0,1},{0,1},{0,0},{1,0},{0,0},{1,0},{0,0}};			
	    double[]y = { -0.48812477, 0.33458213, -0.52754476, -0.79863471, -0.68544309, -0.12970239, 0.02355622, -0.31890850, 0.34725819, 0.08108851};          			
				
	    sr.addObservations(x, y);                  			
	    logger.info("{}, {}, {}", sr.getSlope(), sr.getIntercept());			
	    			
	    			
		SimpleRegression sr1 = new SimpleRegression(true);	
			
		double[][] data = new double[2][x.length];	
		for(int i=0; i < x.length; i++) {	
			data[0][i] = x[i][1];
			data[1][i] = y[i];
		}	
			
	    sr1.addData(data);                  			
	    logger.info("{}, {}, {}", sr1.getSlope(), sr1.getIntercept());			

			
	    double [][] xb = {{1,0,0}, {0,0,0}, {1,0,0}, {2,1,2}, {0,1,0}, {0,0,0}, {1,0,0}, {0,0,0}, {1,0,0}, {0,0,0}}; 			
	    			
	    OLSMultipleLinearRegression regression = new OLSMultipleLinearRegression(); 			
	    regression.newSampleData(y, x); 			
	    double[] beta = regression.estimateRegressionParameters();    			
	     			
	    System.out.printf("First model: y = int + genoA + genoB\n"); 			
	    System.out.printf("Intercept: %.8f\t", beta[0]); 			
	    System.out.printf("beta1: %.8f\t", beta[1]); 			
	    System.out.printf("beta2: %.8f\n\n", beta[2]); 			
			
			
	    regression.newSampleData(y, xb); 			
	    double[] betab = regression.estimateRegressionParameters();    			
	     			
	    System.out.printf("Second model: y = int + genoA + genoB + genoA:genoB\n"); 			
	    System.out.printf("Intercept: %.8f\t", betab[0]); 			
	    System.out.printf("beta1: %.8f\t", betab[1]); 			
	    System.out.printf("beta2: %.8f\t", betab[2]); 			
	    System.out.printf("beta12: %.8f\n", betab[3]);
	    
//      R Code:			
//    	test_trait <- c( -0.48812477 , 0.33458213, -0.52754476, -0.79863471, -0.68544309, -0.12970239,  0.02355622, -0.31890850, 0.34725819 , 0.08108851)		
//    	geno_A <- c(1, 0, 1, 2, 0, 0, 1, 0, 1, 0)		
//    	geno_B <- c(0, 0, 0, 1, 1, 0, 0, 0, 0, 0) 		
//    	fit <- lm(test_trait ~ geno_A*geno_B)		
//    	fit		
//		
//		
//    	Call:		
//    	lm(formula = test_trait ~ geno_A * geno_B)		
//		
//    	Coefficients:		
//    	  (Intercept)         geno_A         geno_B  geno_A:geno_B  		
//    	    -0.008235      -0.152979      -0.677208       0.096383 		
		
		
	}	
	
	public static double add(double a, double b) {
		
		if (a>b) a = a+b;
		return a+b;
	}
	
	public static void testSimple() {
		
		double aaa = 4.5;
		double bbb = 0.0000000000000001;
		
		logger.info("{}, {}, {}", aaa*bbb, Math.signum(aaa*bbb), (int)Math.signum(aaa*bbb));
	}
	
	public static void testMatrix1() {

		double[] termIdx = new double[] {0.25, 0.50, 0.75, 1.00, 1.50, 2.00, 2.50, 3.00, 4.00, 5.00, 7.00, 10.00, 20.00};
		
		double[][] testMat = new double[][] {{0.8, 0}, {0.2, 0.5}};
		logger.info("{}, {}", testMat[1][1], testMat[1][0]);
		
		SimpleMatrix mat = new SimpleMatrix(13, 1, true, termIdx);
		
		mat = new SimpleMatrix(testMat);
		
		logger.info("{}, {}, {}", mat.diag(), mat.determinant(), mat.pseudoInverse());		
		
		RealMatrix mat2 = MatrixUtils.createRealMatrix(testMat);		
		RealMatrix p = MatrixUtils.inverse(mat2);		
		logger.info("{}, {}, {}", p.getData(), MatrixUtils.isSymmetric(p, 0.01), null);		
	}
	
	

}
