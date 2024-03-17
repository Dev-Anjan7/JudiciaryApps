/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.naturewise_yearwise_gen;

/**
 *
 * @author Anjan
 */
import java.util.ArrayList;

public class CaseType {
	
	private String CaseNature;
        //private String revisedNature;
	private ArrayList<YearFrequency> YearFreq;
	private int Total;
	
	public CaseType()
	{
		CaseNature = "";
		YearFreq = new ArrayList<>();
		Total = 0;
                //revisedNature = "";
	}
        
	
	public CaseType(String caseNature)
	{
		CaseNature = caseNature;
		YearFreq = new ArrayList<>();
                //revisedNature = revisedCaseNature;
                //setRevisedCaseNature();
	}
	
        /**
	private void setRevisedCaseNature()
        {
            if (CaseNature.equalsIgnoreCase("Misc Cases")
                        || CaseNature.equalsIgnoreCase("Criminal Misc")) {
                    this.revisedNature = "Criminal  Misc Cases";
                }
         else
              this.revisedNature = CaseNature;
        }
        
        public String getRevisedCaseNature()
        {
            return this.revisedNature;
        }
	**/
	@Override
	public boolean equals(Object caseNature)
	{
		if(caseNature instanceof CaseType)
		{
			
			CaseType ct = (CaseType)caseNature;
			if(this.CaseNature.equalsIgnoreCase(ct.getCaseNature()))
				return true;
		}
		
		return false;
	}
	
	@Override
	public int hashCode() {
	    return this.CaseNature.hashCode();

	}
	
	public int getTotal()
	{
		return this.Total;
	}
        public void setTotal(int total)
	{
		this.Total = total;
	}
	
	public ArrayList<YearFrequency> getAllYearFrequency()
	{
		return YearFreq;
	}
        
        public void setYearFrequencyArrayList(ArrayList<YearFrequency> yf)
        {
            this.YearFreq = yf;
        }

	public void addYear(int year)
	{
		//int count = YearFreq.size();
		int indx = YearFreq.indexOf(new YearFrequency(year));
		if(indx > -1)
		{
			YearFrequency YF = YearFreq.get(indx);
			YF.incrementFrequency();
		}
		else
		{
			YearFreq.add(new YearFrequency(year));
		}
		
		this.Total++;
	}
        
        public void addYearWithFreq(int year, int freq)
        {
            int indx = YearFreq.indexOf(new YearFrequency(year));
            if(indx > -1)
            {
                YearFrequency y = YearFreq.get(indx);
                y.addFrequency(freq);
                this.YearFreq.set(indx, y);
                this.Total += freq;
            }
            else
            {
                YearFreq.add(new YearFrequency(year,freq));
                this.Total += freq;
            }
            
        }
	
	public void setCaseNature(String nature)
	{
		this.CaseNature = nature;
	}
	
	public String getCaseNature()
	{
		return this.CaseNature;
	}
	
	
	public String toString()
	{
		String result = this.CaseNature + ": Total = " + this.Total + " ";
		for(YearFrequency yr:YearFreq)
		{
			result += yr.getYear() + " = " + yr.getFrequency() + "  ";
		}
		return result;
	}
	
}
