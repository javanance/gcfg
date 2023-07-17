package com.gof.irmodel;

import java.time.LocalDate;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.optim.MaxEval;
import org.apache.commons.math3.optim.nonlinear.scalar.GoalType;
import org.apache.commons.math3.optim.univariate.BrentOptimizer;
import org.apache.commons.math3.optim.univariate.SearchInterval;
import org.apache.commons.math3.optim.univariate.UnivariateObjectiveFunction;
import org.apache.commons.math3.optim.univariate.UnivariateOptimizer;
import org.apache.commons.math3.stat.regression.OLSMultipleLinearRegression;
import org.apache.commons.math3.stat.regression.SimpleRegression;

import com.gof.entity.IrmodelResult;
import com.gof.util.TimeUtil;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@Getter
@Setter
public class IrmodelNelsonSeigelD extends IrmodelAbstract {	
	
	private char            obsType             = TIME_UNIT_DAY;
	private int             obsNum              = 250;
	private int             obsMat              = 13;
	private int             lastLiquidPoint     = 20;
	private double          deltaT              = 1.0 / 252.0; // 1/52, 1/12 ...
	                        
	private double          volAdj              = 32 * ONE_BP;
	private double          lambdaMaxInit       = 2.000;
	private double          lambdaMinInit       = 0.05;
	private double          confInterval        = 0.995;    
    
    private double[]        coeffLt;
    private double[]        coeffSt;
    private double[]        coeffCt;
    
    private double          thetaL;
    private double          thetaS;
    private double          thetaC;
                            
    private double          kappaL;
    private double          kappaS;
    private double          kappaC;    


	public IrmodelNelsonSeigelD(LocalDate baseDate, Map<String, Map<Double, Double>> termStructureMap, int obsTsNum) {		
		super(baseDate, termStructureMap, obsTsNum);		
	}
	
	public IrmodelNelsonSeigelD(LocalDate baseDate, TreeMap<Double, Double> termStructure, char cmpdType, char timeUnit, int dayCountBasis) {
		super(baseDate, termStructure, cmpdType, timeUnit, dayCountBasis);		
	}	
	
    //TODO: At least 3 TS is required!!1 ( for Simple Regression )
	public Map<String, Map<String, IrmodelResult>> getIrmodelResult() {		
		
		if(!resultMap.isEmpty() || resultMap.containsKey(TimeUtil.dateToString(this.baseDate))) return resultMap;		
		
		double optLambda;
		
		if(this.termStructureMap == null || this.termStructureMap.isEmpty()) {		
			log.error("Term Structure Data is Empty in {}", this.modelType);
		}
		else if( this.termStructureMap.size() < 3) {
			log.error("At Least 3 Term Structures are Required in {} (currently just {} set).", this.modelType, this.termStructureMap.size());			
		}
		else {			
			setIrmodelAttributesMat();
			setProjectionTenorMat();		
			
			optLambda = optimizingLambda();
			//optLambda = 0.59355261333542;
			//optLambda = 0.5320308;

			log.info("optLambda: {}", optLambda);
			//log.info("{}", nelsonSiegelTermStructure(lambda, tenor, Lt, St, Ct));			
			//for(int i=0; i<coeffCt.length; i++) log.info("DT:{}, C:{}, S:{}, L:{}", this.setDate[i], coeffCt[i], coeffSt[i], coeffLt[i]);			
			
			SetDnsInitialParam();			
			resultMap.put(TimeUtil.dateToString(this.baseDate), null);			
		}
		return resultMap;
	}
	
    
    private void SetDnsInitialParam() {
    	
    	SimpleRegression linRegL = new SimpleRegression(true);
    	SimpleRegression linRegS = new SimpleRegression(true);
    	SimpleRegression linRegC = new SimpleRegression(true);    	
    	
    	for(int i=0; i<coeffLt.length-1; i++) {
    		
    		linRegL.addData(coeffLt[i], coeffLt[i+1]);
    		linRegS.addData(coeffSt[i], coeffSt[i+1]);
    		linRegC.addData(coeffCt[i], coeffCt[i+1]);
    	}
		//log.info("{}, {}, {}, {}, {}, {}", linRegC.getSlope(), linRegS.getSlope(), linRegL.getSlope(), linRegL.getIntercept());
		
		thetaL = linRegL.getIntercept() / ( 1.0 - linRegL.getSlope());		
		thetaS = linRegS.getIntercept() / ( 1.0 - linRegS.getSlope());
		thetaC = linRegC.getIntercept() / ( 1.0 - linRegC.getSlope());
		
		kappaL = -Math.log(linRegL.getSlope()) / deltaT;
		kappaS = -Math.log(linRegS.getSlope()) / deltaT;
		kappaC = -Math.log(linRegC.getSlope()) / deltaT;
		
		log.info("{}, {}, {}, {}, {}, {}, {}",  deltaT, thetaL, thetaS, thetaC, kappaL, kappaS, kappaC);    	
    }
    	

    protected double optimizingLambda() {
    	
    	double uniOptimum;
    	
        coeffSt = new double[this.tenorYearFracSet.length];
        coeffCt = new double[this.tenorYearFracSet.length];
        coeffLt = new double[this.tenorYearFracSet.length];
    	
    	UnivariateFunction fp = new UnivariateFunction() {			
			public double value(double lambda) {				
				return residualSumOfSquares(lambda);
			}
		};
		
		UnivariateOptimizer optimizer = new BrentOptimizer(1e-10, 1e-14);		
		uniOptimum = optimizer.optimize(new MaxEval(10000)
		  	                          , new UnivariateObjectiveFunction(fp)
				                      , GoalType.MINIMIZE
				                      , new SearchInterval(this.lambdaMinInit, this.lambdaMaxInit)).getPoint();
		
		return uniOptimum;
    }

    
	protected double residualSumOfSquares(double lambda) {
		
		double residualSum = 0.0;
		
		for(int i=0; i<this.tenorYearFracSet.length; i++) {

			double[][] xArray = inputValGeneration(lambda, this.tenorYearFracSet[i]);
			double[]   yArray = this.iRateSet[i];

			OLSMultipleLinearRegression reg = new OLSMultipleLinearRegression();
			reg.newSampleData(yArray,  xArray);
			
			//log.info("{}, {}", xArray);
			double[] rslt = reg.estimateRegressionParameters();
			coeffSt[i] = rslt[1];
			coeffCt[i] = rslt[2];
			coeffLt[i] = rslt[0];
			
			residualSum = residualSum + reg.calculateResidualSumOfSquares();			
		}
		//log.info("{}, {}, {}", this.tenorYearFracSet.length, this.tenorYearFracSet[0].length, this.tenorYearFracSet);		
		return residualSum;		
	}	
	
	/**
	 * for single date TS: same result as residualSumofSquare ...
	 * However some two dates TS: different from residualSumofSquare -->
	 * for single TS: each C, S, L coeff, for total TS : overall C, S, L solely...  
	 * DO NOT USE THIS METHOD!!!
	 */
	protected double residualSumOfSquares2(double lambda) {		
		
		double lamTau = 0.0;
		
		double[][] xArrayTotal = new double[this.tenorYearFracSet.length * this.tenorYearFracSet[0].length][2];
		double[]   yArrayTotal = new double[this.tenorYearFracSet.length * this.tenorYearFracSet[0].length];
		
		for(int i=0; i<this.tenorYearFracSet.length; i++) {			
			
			for(int j=0; j<this.tenorYearFracSet[i].length; j++) {
				
				lamTau = lambda * this.tenorYearFracSet[i][j];				
				
				xArrayTotal[this.tenorYearFracSet[i].length * i + j][0] = (1.0 - Math.exp(-lamTau)) / lamTau;
				xArrayTotal[this.tenorYearFracSet[i].length * i + j][1] = xArrayTotal[this.tenorYearFracSet[i].length * i + j][0] - Math.exp(-lamTau);
				yArrayTotal[this.tenorYearFracSet[i].length * i + j]    = this.iRateSet[i][j];
			}
		}
		//log.info("{}, {}", xArrayTotal);
		//log.info("{}, {}", this.iRateSet);
		
		OLSMultipleLinearRegression reg = new OLSMultipleLinearRegression();
		reg.newSampleData(yArrayTotal,  xArrayTotal);
		
		return reg.calculateResidualSumOfSquares();		
	}	
	
	
	private double[][] inputValGeneration(double lambda, double[] tenor) {
		
		final int COEF_S = 0;
		final int COEF_C = 1;
		
		double lamTau = 0.0;
		double[][] inputValArray = new double[tenor.length][2];		
		
		for(int i=0; i<inputValArray.length; i++) {			
			lamTau = lambda * tenor[i];
			
			for(int j=0; j<inputValArray[i].length; j++) {				
				switch(j) {				    
				    case COEF_S: inputValArray[i][j] = (1.0 - Math.exp(-lamTau)) / lamTau; break;				    
				    case COEF_C: inputValArray[i][j] = inputValArray[i][j-1] - Math.exp(-lamTau); break;
				    default: break;
				}
			}
		}		
		//log.info("{}, {}", inputValArray);
		return inputValArray;
	}		
	
	//c, s, l 은 각 일자별로 나옴.... 사실 c * t1 + s * t2 + * 1 = iRate이다..   t1 t2 를 독립변수로 하고 만기별 금리값을 y로 하면 c와 s와 L이 나옴...
	//우선 일자별로 regression을 진행해야 함...
	// 이후에 L, S, C 배열은 전체 날짜 수대로 나오고 관리되어 야함...다만 람다를 하나로 정할 뿐...람다를 정하고나서...lsc배열!! 이 결정될 뿐이다..그것도 초기값 정도의 상황이다...
	// 다만 t1, t2가 람다의 함수이므로...고정적인 값이 아니다...즉 t1  t2는 람다와 타우에 의해서 자동 생성되어야 하는 녀석들인 것이다.	

//       lambda -> 그냥 아무렇게나...민 맥스 사이로...	
//          tau 0.25    0.5     0.75    1.00    1.50    2.00    2.50    3.00    4.00    5.00    7.00    10.0    20.0
//	20171226	1.52%	1.66%	1.80%	1.89%	2.01%	2.10%	2.15%	2.14%	2.30%	2.37%	2.45%	2.49%	2.43%
//	20171227	1.52%	1.66%	1.80%	1.89%	2.03%	2.11%	2.17%	2.16%	2.33%	2.40%	2.47%	2.50%	2.44%
	
	protected void multipleLinReg(double lambda, double[] tenor, double[] iRate) {
		
		double[][] xArray = inputValGeneration(lambda, tenor);
		double[]   yArray = iRate;		
		
		log.info("{}, {}", xArray, yArray);
		OLSMultipleLinearRegression reg = new OLSMultipleLinearRegression();
		reg.newSampleData(yArray,  xArray);

		double[] rslt = reg.estimateRegressionParameters();
		log.info("R: {}, C: {}, S: {}, L: {}", reg.calculateResidualSumOfSquares(), rslt[2], rslt[1], rslt[0]);		
	}	

	
	public double[] nelsonSiegelTermStructure(double lambda, double[] tenor, double[] Lt, double[] St, double[] Ct) {
		
		double[] iRate = new double[tenor.length];		
		for(int i=0; i<iRate.length; i++) iRate[i] = nelsonSiegelFn(lambda, tenor[i], Lt[i], St[i], Ct[i]);
		
		return iRate;
	}
	
	
	private double nelsonSiegelFn(double lambda, double tenor, double Lt, double St, double Ct) {		
		return nelsonSiegelFn(lambda, tenor, Lt, St, Ct, 0.0);
	}
	

	private double nelsonSiegelFn(double lambda, double tenor, double Lt, double St, double Ct, double epsilon) {
		
		double lamTau = lambda * tenor;
		return Lt * 1.0 + St * ((1 - Math.exp(-lamTau)) / lamTau) + Ct * ((1 - Math.exp(-lamTau)) / lamTau - Math.exp(-lamTau)) + epsilon;
	}	
	
	
	public double zeroBondUnitPrice(double rateCont, double mat) {
		return Math.exp(-rateCont*mat);		
	}
	
	
	public void paramToString() {
		log.info("BaseDate:{}, TimeUnit:{}, CONT/DISC:{}, DCB:{}, RateReal:{}, PrjYear: {}"
				             , this.baseDate				         
				             , this.timeUnit
				             , this.cmpdType
				             , this.dayCountBasis
				             , this.isRealNumber
				             , this.prjYear
				             );
	}

}
