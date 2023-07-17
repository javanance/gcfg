package com.gof.irmodel;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;

import com.gof.entity.IrmodelResult;
import com.gof.util.TimeUtil;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@Getter
@Setter
public class IrmodelSmithWilson extends IrmodelAbstract {
	
	private static final int NegativeDirection = -1;
	private static final int PositiveDirection = 1;
	
	private double          ltfr                = 0.045;	
	private double          ltfrCont            = 0.0;
	private int             ltfrPoint           = 60;
	private double          ltfrEpsilon         = 0.0001;
	                        
	private double          alphaMaxInit        = 1.000;
	private double          alphaMinInit        = 0.001;
	private int             alphaItrNum         = 20;	
	private int             lastLiquidPoint     = 20;	
	                        
	private boolean         initializeFlag      = false;	
	private int             dirApproach         = 0;		
	private double          alphaApplied        = 0.0;
	private double          kappaApplied        = 0.0;
	private int             dirAlphaApplied     = 0;
	private double          extendApplied       = 0.0;	
	private double          alphaMaxApplied     = 0.0;
	private double          alphaMinApplied     = 0.0;

	private List<Double>    alphaList           = new ArrayList<Double>();
	private List<Double>    kappaList           = new ArrayList<Double>();
	private List<Integer>   dirAlphaList        = new ArrayList<Integer>();	
	private List<Double>    extendList          = new ArrayList<Double>();	
	private List<Double>    alphaMaxList        = new ArrayList<Double>();
	private List<Double>    alphaMinList        = new ArrayList<Double>();

	private RealMatrix      zetaColumn;

	
	public IrmodelSmithWilson(LocalDate baseDate, Map<Double, Double> termStructure) {
		super(baseDate, termStructure);		
	}
	
	public IrmodelSmithWilson(LocalDate baseDate, Map<Double, Double> termStructure, char cmpdType, char timeUnit, int dayCountBasis) {
		super(baseDate, termStructure, cmpdType, timeUnit, dayCountBasis);		
	}
	
	
	public Map<String, Map<String, IrmodelResult>> getIrmodelResult() {
		
		if(!resultMap.isEmpty() || resultMap.containsKey(TimeUtil.dateToString(this.baseDate))) return resultMap;		
				
		double alphaOpt = 0.0;				
		ltfrCont = (this.cmpdType == CMPD_MTD_DISC) ? toContCmpd(this.ltfr) : this.ltfr;

		if(this.termStructure == null || this.termStructure.isEmpty()) {		
			log.error("Term Structure Data is Empty in {}", this.modelType);
		}
		else if(this.baseDate == null) {
			log.error("Set BaseDate First");
		}
		else {
			setIrmodelAttributes();
			setProjectionTenor();			
			alphaOpt = smithWilsonAlphaFinding();			
			//alphaOpt = 0.121529;		
			
			resultMap.put(TimeUtil.dateToString(this.baseDate), smithWilsonProjection(alphaOpt));			
		}
		return resultMap;
	}
	
	
	private double smithWilsonAlphaFinding() {				
		
		for(int i=0; i<this.alphaItrNum; i++) {
			
			//Initialization Process (i=0)
			if(i==0) {
				this.alphaMaxApplied = this.alphaMaxInit;
				this.alphaMinApplied = this.alphaMinInit;
			}
			
			//Alpha Finding Process (i=1 to ItrNum)
			else {				
				if(this.dirAlphaApplied == NegativeDirection) {
					this.alphaMaxApplied = this.alphaMaxApplied - this.extendApplied;			
				}
				else  {
					this.alphaMinApplied = this.alphaMinApplied + this.extendApplied;				
					if(this.dirAlphaApplied != PositiveDirection) log.warn("Check the Direction in Smith-Wilson Alpha Finding Process");
				}
			}
			
			this.alphaApplied  = round(0.5 * (alphaMaxApplied + alphaMinApplied), this.decimalDigit);
			this.extendApplied = round(0.5 * (alphaMaxApplied - alphaMinApplied), this.decimalDigit);
			smithWilsonZeta(this.alphaApplied);		
		
			alphaList.add(this.alphaApplied);
			kappaList.add(round(this.kappaApplied));
			dirAlphaList.add(this.dirAlphaApplied);
			extendList.add(this.extendApplied);
			alphaMaxList.add(round(this.alphaMaxApplied));
			alphaMinList.add(round(this.alphaMinApplied));
		}
		log.info("alphaOpt: {}", round(0.5 * (alphaMaxApplied + alphaMinApplied),  this.decimalDigit));		
		return round(0.5 * (alphaMaxApplied + alphaMinApplied), 6);		//Return the average value of alpha max/min of last iteration  
	}	

	
	private void smithWilsonZeta(double alpha) {		
		
		RealMatrix tenorCol  = MatrixUtils.createColumnRealMatrix(tenorYearFrac);
		RealMatrix weight    = MatrixUtils.createRealMatrix(smithWilsonWeight(tenorYearFrac, tenorYearFrac, alpha, ltfrCont));		
		//RealMatrix trsWeight = weight.transpose();
		//matrixLogger(weight.getData());
		RealMatrix invWeight = MatrixUtils.inverse(weight);
		
		double[] pVal = new double[tenorYearFrac.length];
		double[] mean = new double[tenorYearFrac.length];
		double[] loss = new double[tenorYearFrac.length];
		double[] sinh = new double[tenorYearFrac.length];
		
		for(int i=0; i<loss.length; i++) {
			pVal[i] = zeroBondUnitPrice(iRate[i], tenorYearFrac[i]);
			mean[i] = zeroBondUnitPrice(ltfrCont, tenorYearFrac[i]);
			loss[i] = smithWilsonLoss(iRate[i], tenorYearFrac[i], ltfrCont);
			sinh[i] = Math.sinh(alpha * tenorYearFrac[i]);
		}				
		
		RealMatrix lossCol  = MatrixUtils.createColumnRealMatrix(loss);
		RealMatrix zetaCol  = invWeight.multiply(lossCol);
		
		RealMatrix sinhCol  = MatrixUtils.createColumnRealMatrix(sinh);
		RealMatrix qMatDiag = MatrixUtils.createRealDiagonalMatrix(mean);
		
//		matrixLogger(sinhCol.getData());
//		matrixLogger(qMatDiag.getData());

		double kappaNum   = tenorCol.transpose().multiply(qMatDiag).multiply(zetaCol).scalarMultiply(alpha).scalarAdd(1.0).getEntry(0, 0);
		double kappaDenom = sinhCol.transpose().multiply(qMatDiag).multiply(zetaCol).getEntry(0, 0);
		this.kappaApplied = kappaNum / (Math.abs(kappaDenom) < ZERO_DOUBLE ? 1.0 : kappaDenom);		        
		
        if(!this.initializeFlag) {
        	this.initializeFlag = true;
        	this.dirApproach = (int) Math.signum(alpha / (1 - this.kappaApplied * Math.exp(alpha*lastLiquidPoint) ) );        	
        }        
        
        this.dirAlphaApplied = (int) Math.signum(this.dirApproach * alpha / (1 - this.kappaApplied * Math.exp(alpha*ltfrPoint) ) - ltfrEpsilon);
        this.zetaColumn = zetaCol;
        
        //log.info("{}, {}, {}, {}, {}", kappaNum, kappaDenom, this.kappaApplied, this.dirApproach, this.dirAlphaApplied);
		
//		matrixLogger(weight.getData());
//		matrixLogger(lossCol.getData());
//		matrixLogger(zetaCol.getData());		
	}		
	
	 
	protected Map<String, IrmodelResult> smithWilsonProjection(double alpha) {
		
		Map<String, IrmodelResult> swModelMap = new TreeMap<String, IrmodelResult>();	
		smithWilsonZeta(alpha);
		
		double[] df = new double[prjYearFrac.length];		
		for(int i=0; i<df.length; i++) df[i] = zeroBondUnitPrice(ltfrCont, prjYearFrac[i]);
		
		RealMatrix weightPrjTenor = MatrixUtils.createRealMatrix(smithWilsonWeight(prjYearFrac, tenorYearFrac, alpha, ltfrCont));
		
		RealMatrix dfCol    = MatrixUtils.createColumnRealMatrix(df);		
		RealMatrix sigmaCol = weightPrjTenor.multiply(this.zetaColumn);		
		RealMatrix priceCol = dfCol.add(sigmaCol);		

		//matrixLogger(priceCol.getData());
		
		double[] spotCont = new double[prjYearFrac.length];				
		double[] fwdCont  = new double[prjYearFrac.length];		
		
		for(int i=0; i<this.prjYearFrac.length; i++) {			
			spotCont[i] =  -1.0 / prjYearFrac[i] * Math.log(priceCol.getEntry(i, 0));
			fwdCont[i]  = (i > 0) ? (spotCont[i]*prjYearFrac[i] - spotCont[i-1]*prjYearFrac[i-1]) / (prjYearFrac[i] - prjYearFrac[i-1]) : spotCont[i];			
		
			IrmodelResult irResult = new IrmodelResult();
			
			irResult.setMatTerm(round(prjYearFrac[i]));
			irResult.setMatDate(prjDate[i]);
			irResult.setSpotCont(round(spotCont[i]));
			irResult.setFwdCont(round(fwdCont[i]));
			irResult.setSpotDisc(round(toDiscCmpd(spotCont[i])));
			irResult.setFwdDisc(round(toDiscCmpd(fwdCont[i])));
			
			swModelMap.put(String.format("%s%04d", this.prjTimeUnit, i+1), irResult);			
		}		
		return swModelMap;
	}
	
	
	private double[][] smithWilsonWeight(double[] prjYearFrac, double[] tenorYearFrac, double alpha, double ltfrCont) {
	
		double[][] weight = new double[prjYearFrac.length][tenorYearFrac.length];
		double min, max;
		
		for(int i=0; i<prjYearFrac.length; i++) {			
			for(int j=0; j<tenorYearFrac.length; j++) {		
				
				min = Math.min(prjYearFrac[i], tenorYearFrac[j]);
				max = Math.max(prjYearFrac[i], tenorYearFrac[j]);				
//				weight[i][j] = Math.exp(-ltfrCont * (prjYearFrac[i] + tenorYearFrac[j])) * (alpha * min - 0.5 * Math.exp(-alpha*max) * (Math.exp(alpha*min) - Math.exp(-alpha*min)));
				weight[i][j] = Math.exp(-ltfrCont * (prjYearFrac[i] + tenorYearFrac[j])) * (alpha * min - Math.exp(-alpha*max) * Math.sinh(alpha*min));
			}
		}		
		return weight;
	}
	
	
	private double zeroBondUnitPrice(double rateCont, double mat) {
		return Math.exp(-rateCont*mat);		
	}	
	
	
	private double smithWilsonLoss(double rateCont, double mat, double ltfrCont) {
		return zeroBondUnitPrice(rateCont, mat) - zeroBondUnitPrice(ltfrCont, mat);
	}

	
	public void paramToString() {
		log.info("BaseDate:{}, ItrNum:{}, Alpha:{}, TimeUnit:{}, CONT/DISC:{}, DCB:{}, RateReal:{}, PrjYear: {}, LTFR:{}, LTFR Term:{}"
				             , this.baseDate
				             , this.alphaItrNum
				             , this.alphaApplied
				             , this.timeUnit
				             , this.cmpdType
				             , this.dayCountBasis
				             , this.isRealNumber
				             , this.prjYear
				             , this.ltfr
				             , this.ltfrPoint
				             );
	}	
	
}