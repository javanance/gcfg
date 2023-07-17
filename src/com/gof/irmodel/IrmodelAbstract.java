package com.gof.irmodel;

import java.time.LocalDate;
import java.util.Map;
import java.util.TreeMap;

import com.gof.entity.IrmodelResult;
import com.gof.interfaces.Irmodel;
import com.gof.util.TimeUtil;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@Getter
@Setter
public abstract class IrmodelAbstract implements Irmodel {

	protected LocalDate     baseDate;
	protected int           modelType;
	protected char          cmpdType          = CMPD_MTD_DISC;	
	protected char          timeUnit          = TIME_UNIT_YEAR;
	protected int           dayCountBasis     = DCB_ACT_365;	
	protected int           decimalDigit      = 10;
	protected boolean       isRealNumber      = false;
	
	protected double[]      tenor;
	protected double[]      iRate;	
	protected LocalDate[]   tenorDate;
	protected double[]      tenorYearFrac;
	protected int           obsTsNum;	
	
	protected int           prjYear           = 100;
	protected int           prjInterval       = 1;
	protected char          prjTimeUnit       = TIME_UNIT_MONTH;	
	protected LocalDate[]   prjDate;
    protected double[]      prjYearFrac;    
    
	protected double[]      spotRateCont;
	protected double[]      spotRateDisc;
	protected double[]      fwdRateCont;
	protected double[]      fwdRateDisc;
	
	protected LocalDate[]   setDate;
	protected double[][]    tenorSet;
	protected double[][]    iRateSet;	
	protected LocalDate[][] tenorDateSet;
	protected double[][]    tenorYearFracSet;	
	
	protected Map<Double, Double>                     termStructure    = new TreeMap<Double, Double>();
	protected Map<String, Map<Double, Double>>        termStructureMap = new TreeMap<String, Map<Double, Double>>();
	protected Map<String, Map<String, IrmodelResult>> resultMap        = new TreeMap<String, Map<String, IrmodelResult>>();
	
	//private double[] testTenor = new double[] {   3,    6,    9,   12,   18,   24,   30,   36,   48,   60,   84,  120,  240};
	private double[] testTenor = new double[] {0.25, 0.50, 0.75, 1.00, 1.50, 2.00, 2.50, 3.00, 4.00, 5.00, 7.00, 10.0, 20.0};
	private double[] testRate  = new double[] {1.52, 1.66, 1.79, 1.87, 2.01, 2.09, 2.15, 2.15, 2.31, 2.38, 2.44, 2.46, 2.44}; //FY2017 RF
	//private double[] testRate  = new double[] {1.33, 1.47, 1.52, 1.56, 1.61, 1.63, 1.66, 1.65, 1.81, 1.86, 2.02, 2.10, 2.16}; //FY2016 RF	

	//private double[] testTenor = new double[] { 0.25,  0.50,  0.75,  1.00,  1.50,  2.00,  2.50,  3.00,  5.00,  7.00, 10.00, 15.00, 20.00}; // EXCEL MODEL
	//private double[] testRate  = new double[] {1.277, 1.325, 1.400, 1.462, 1.550, 1.635, 1.695, 1.700, 1.902, 2.105, 2.212, 2.277, 2.307}; //20170630 EXCEL MODEL
	
    // sw excel sample
	// { 0.25,  0.50,  0.75,  1.00,  1.50,  2.00,  2.50,  3.00,  5.00,  7.00, 10.00, 15.00, 20.00};	
    // {1.277, 1.325, 1.400, 1.462, 1.550, 1.635, 1.695, 1.700, 1.902, 2.105, 2.212, 2.277, 2.307};

	// sw techdoc sample
	// {1, 2, 3,   5  };	// {1, 2, 2.6, 3.4};	// {1, 2, 3, 4, 5 };	
	
	
	
	public IrmodelAbstract(LocalDate baseDate) {
		super();
		this.baseDate = baseDate;
		this.tenor    = testTenor;
		this.iRate    = testRate;
	}	
	
	
	public IrmodelAbstract(LocalDate baseDate, char cmpdType) {		
		super();
		this.baseDate = baseDate;
		this.cmpdType = cmpdType;
		this.tenor    = testTenor;
		this.iRate    = testRate;		
	}
	
	
	public IrmodelAbstract(LocalDate baseDate, Map<Double, Double> termStructure) {		
		super();
		this.baseDate = baseDate;
		setTermStructure(termStructure);
	}
	
	
	public IrmodelAbstract(LocalDate baseDate, Map<Double, Double> termStructure, char cmpdType, char timeUnit, int dayCountBasis) {		
		super();
		this.baseDate      = baseDate;
		this.cmpdType      = cmpdType;
		this.timeUnit      = timeUnit;
		this.dayCountBasis = dayCountBasis;
		setTermStructure(termStructure);		
	}
	
	
	public IrmodelAbstract(LocalDate baseDate, Map<String, Map<Double, Double>> termStructureMap, int obsTsNum) {		
		super();
		this.baseDate = baseDate;
		this.obsTsNum = obsTsNum;
		setTermStructureMap(termStructureMap);
	}	
	
	
	public abstract Map<String, Map<String, IrmodelResult>> getIrmodelResult();		
	
	public abstract void paramToString();	
	

	public void setTermStructureMap(Map<String, Map<Double, Double>> termStructureMap) {		
		
		if(termStructureMap == null || termStructureMap.isEmpty()) {
			log.warn("Check the Term Structure Data");
		}
		else {			
			this.termStructureMap = termStructureMap;			
			int setNum = termStructureMap.keySet().size();		
			int tenorNum = ((TreeMap<String, Map<Double, Double>>) termStructureMap).firstEntry().getValue().size();
			
			setDate  = new LocalDate[setNum];
			tenorSet = new double[setNum][tenorNum];
			iRateSet = new double[setNum][tenorNum];
			
			int ii = 0;		
			for(Map.Entry<String, Map<Double, Double>> ts : termStructureMap.entrySet()) {				
				int jj = 0;
				this.setDate[ii] = TimeUtil.stringToDate(ts.getKey());				
				for(Map.Entry<Double, Double> pts : ts.getValue().entrySet()) {
					this.tenorSet[ii][jj] = pts.getKey();
					this.iRateSet[ii][jj] = pts.getValue();
					jj++;
				}				
				ii++;
			}
		}
		//log.info("{}, {}", this.tenorSet, this.iRateSet);			
	}
	
	
    protected void setIrmodelAttributesMat() {    	
		
    	tenorDateSet     = new LocalDate[this.tenorSet.length][this.tenorSet[0].length];
		tenorYearFracSet = new double   [this.tenorSet.length][this.tenorSet[0].length];

    	int yearToMonth = (this.timeUnit == TIME_UNIT_YEAR) ? 12 : 1;
    	double toRealScale = this.isRealNumber() ? 1 : 0.01;
    	
    	for(int i=0; i<this.tenorSet.length; i++) {
    		for(int j=0; j<this.tenorSet[i].length; j++) {

    			this.tenorDateSet[i][j]     = this.setDate[i].plusMonths((long) Math.round(this.tenorSet[i][j]*yearToMonth));
        		this.tenorYearFracSet[i][j] = TimeUtil.getTimeFactor(this.setDate[i], this.tenorDateSet[i][j], this.dayCountBasis);
        		this.iRateSet[i][j]         = (this.cmpdType == CMPD_MTD_DISC) ? toContCmpd(toRealScale*this.iRateSet[i][j]) : toRealScale*this.iRateSet[i][j];    			
    		}
    	}
    	//log.info("{}, {}", this.tenorYearFracSet, this.iRateSet);
    }

    
	protected void setProjectionTenorMat() {
		
		//so far, there is no change compared to no Mat method...
	
		int monthToYear = (this.prjTimeUnit == TIME_UNIT_MONTH) ? 12 : 1;
		int yearToMonth = (this.prjTimeUnit == TIME_UNIT_YEAR) ? 12 : 1;
		int prjNum = this.prjYear * monthToYear / ((this.prjInterval > 0) ? this.prjInterval : 1);

		prjDate      = new LocalDate[prjNum]; 
		prjYearFrac  = new double[prjNum];
		spotRateCont = new double[prjNum];
		spotRateDisc = new double[prjNum];
		fwdRateCont  = new double[prjNum];
		fwdRateDisc  = new double[prjNum];
		
    	for(int i=0; i<this.prjDate.length; i++) {	    	
    		
    		prjDate[i] = baseDate.plusMonths((long) Math.round((i+1) * this.prjInterval * yearToMonth));    		
    		prjYearFrac[i] = TimeUtil.getTimeFactor(baseDate, prjDate[i], this.dayCountBasis);
    		//log.info("prjYearFrac: {}", prjYearFrac[i]);
    	}		
	}		
	
	
	public Map<Double, Double> getTermStructure() {
		return this.termStructure;
	}
	

	public void setTermStructure(Map<Double, Double> termStructure) {
		
		if(termStructure == null || termStructure.isEmpty()) {
			log.warn("Check the Term Structure Data");
		}
		else {
			this.termStructure = termStructure;				
			this.tenor = this.termStructure.keySet().stream().mapToDouble(Double::doubleValue).toArray();		
			this.iRate = this.termStructure.values().stream().mapToDouble(Double::doubleValue).toArray();
		}
	}	
	
	
    protected void setIrmodelAttributes() {    	
    	
    	tenorDate          = new LocalDate[this.tenor.length];
    	tenorYearFrac      = new double   [this.tenor.length];    	
    	int yearToMonth    = (this.timeUnit == TIME_UNIT_YEAR) ? 12 : 1;
    	double toRealScale = this.isRealNumber() ? 1 : 0.01;
    	
    	for(int i=0; i<this.tenor.length; i++) {    		

    		this.tenorDate[i] = baseDate.plusMonths((long) Math.round(this.tenor[i]*yearToMonth));
    		this.tenorYearFrac[i] = TimeUtil.getTimeFactor(baseDate, tenorDate[i], this.dayCountBasis);
    		this.iRate[i] = (this.cmpdType == CMPD_MTD_DISC) ? toContCmpd(toRealScale*this.iRate[i]) : toRealScale*this.iRate[i];
    	}    	
    } 

    
	protected void setProjectionTenor() {		
		
		int monthToYear = (this.prjTimeUnit == TIME_UNIT_MONTH) ? 12 : 1;
		int yearToMonth = (this.prjTimeUnit == TIME_UNIT_YEAR) ? 12 : 1;
		int prjNum      = this.prjYear * monthToYear / ((this.prjInterval > 0) ? this.prjInterval : 1);

		prjDate         = new LocalDate[prjNum]; 
		prjYearFrac     = new double[prjNum];
		spotRateCont    = new double[prjNum];
		spotRateDisc    = new double[prjNum];
		fwdRateCont     = new double[prjNum];
		fwdRateDisc     = new double[prjNum];
		
    	for(int i=0; i<this.prjDate.length; i++) {	    	
    		
    		prjDate[i] = baseDate.plusMonths((long) Math.round((i+1) * this.prjInterval * yearToMonth));    		
    		prjYearFrac[i] = TimeUtil.getTimeFactor(baseDate, prjDate[i], this.dayCountBasis);
    		//log.info("prjYearFrac: {}", prjYearFrac[i]);
    	}		
	}


	protected double toContCmpd(double discCmpdRate) {    	
    	return Math.log(1 + discCmpdRate);
    }
	
    
	protected double toDiscCmpd(double contCmpdRate) {    	
    	return Math.exp(contCmpdRate) - 1.0;
    }
	

	protected void matrixLogger(double[][] mat, int rownum) {
		
		String delimiter;		
		for(int i=0; i<rownum; i++) {
			
			System.out.print(String.format("[%02d]:[", i));		
			
			for(int j=0; j<mat[i].length; j++) {
				delimiter = (j == mat[0].length-1) ? "" : ",";
				System.out.print( round(mat[i][j]) + delimiter);				
			}			
			System.out.print(String.format("]:[%02d]\n", mat[i].length-1));
		}		
	}	
	
	protected void matrixLogger2(double[][] mat, int skiprow) {
		
		String delimiter;		
		for(int i=0; i<mat.length; i=i+skiprow) {
			
			System.out.print(String.format("[%02d]:[", i));		
			
			for(int j=0; j<mat[i].length; j=j+skiprow) {
				delimiter = (j == mat[0].length-1) ? "" : ",";
				System.out.print( String.format("%.8f", mat[i][j]) + delimiter);				
			}			
			System.out.print(String.format("]:[%02d]\n", mat[i].length-1));
		}		
	}	
	
	
	public void matrixLogger(double[][] mat) {		
		matrixLogger(mat, mat.length);
	}
	
	
	public static double round(double number, int decimalDigit) {
		if(decimalDigit < 0) return Math.round(number);
		return Double.parseDouble(String.format("%." + decimalDigit + "f", number));
	}
	
	
	public double round(double number) {		
		return Double.parseDouble(String.format("%." + this.decimalDigit + "f", number));
	}
	
}
