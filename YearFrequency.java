/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.naturewise_yearwise_gen;

/**
 *
 * @author Anjan
 */
public class YearFrequency implements Comparable<YearFrequency> {
	
	private int cYear;
	private int frequency;
	
	public YearFrequency()
	{
		this.cYear = 0;
		this.frequency = 0;
	}
	
	public YearFrequency(int year)
	{
		this.cYear = year;
		this.frequency = 1;
	}
	
	public YearFrequency(int year, int freq)
	{
		this.cYear = year;
		this.frequency = freq;
	}

	public int getYear() {
		return cYear;
	}

	public int getFrequency() {
		return frequency;
	}

	public void setYear(int Year) {
		this.cYear = Year;
	}

	public void setFrequency(int frequency) {
		this.frequency = frequency;
	}
	
	public void incrementFrequency()
	{
		this.frequency++;
	}
        
        public void addFrequency(int freq)
        {
            this.frequency += freq;
        }
	
	@Override
	public boolean equals(Object yf)
	{
		if(yf instanceof YearFrequency)
		{
			YearFrequency YF = (YearFrequency)yf;
			if(this.cYear == YF.getYear())
				return true;
		}
		return false;
		
	}


	@Override
	public int compareTo(YearFrequency o2) {

		if(this.cYear > o2.cYear)
			return -1;
		else if(this.cYear < o2.cYear)
			return 1;
		else
			return 0;
	}

}

