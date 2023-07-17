
package com.gof.util;

import java.util.DoubleSummaryStatistics;
import java.util.stream.Collector;

public class DoubleStaticstics extends DoubleSummaryStatistics{
	private double sumOfSquare =0.0;
	private double sumOfSquareCompensation =0.0;
	private double simpleSumOfSquare;
	
	@Override
	public void accept(double value) {
	
		super.accept(value);
		double squareValue = value * value;
		simpleSumOfSquare += squareValue;
		sumOfSquareWithCompensation(squareValue);
		
		
	}
	private void sumOfSquareWithCompensation(double value) {
		double temp = value - sumOfSquareCompensation;
		double velvel = sumOfSquare + temp;
		sumOfSquareCompensation =(velvel - sumOfSquare) -temp;
		sumOfSquare = velvel;
		
	}
	public double getSumOfSquare() {
		double temp = sumOfSquare+sumOfSquareCompensation;
		if(Double.isNaN(temp) &&Double.isInfinite(simpleSumOfSquare)) {
			return simpleSumOfSquare;
		}
		return temp;
	}
	
	public DoubleStaticstics combine(DoubleStaticstics other) {

		super.combine(other);
		simpleSumOfSquare += other.simpleSumOfSquare;
		sumOfSquareWithCompensation(other.sumOfSquare);
		sumOfSquareWithCompensation(other.sumOfSquareCompensation);
		return this;
	}

	public final double getStd() {
		long count = getCount();
		double sumOfSquare = getSumOfSquare();
		double avg = getAverage();
		if(count <=1) {
			return 0.0;
		}
		return count>0? Math.sqrt(( sumOfSquare - count * Math.pow(avg,2)) / (count -1)) : 0.0;
	}
	
	public static Collector<Double, ?, DoubleStaticstics> collector(){
		return Collector.of(DoubleStaticstics::new, DoubleStaticstics::accept, DoubleStaticstics::combine);
	}
	
	

}
