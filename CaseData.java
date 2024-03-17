/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.naturewise_yearwise_gen;

import static java.lang.Long.compare;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author Anjan
 * Class to store the case data and read the cases csv file based on different source of input (dashboard/Query Builder)
 */
public class CaseData implements Comparable<CaseData> {
    
    private String nature;
    private int CaseYear;
    private int regYear;
    private long caseNo;
    private String partyName;
    private String stage;
    private final String line;
    private String act;
    private String registrationDate;
    private String realNature;
    private String revisedNature;
    private String policeStation;
    
    public CaseData(String line)
    {
        this.line = line;       
    }
    
    public CaseData(CaseData other)
    {
        this.nature = other.nature;
        this.regYear = other.regYear;
        this.caseNo = other.caseNo;
        this.partyName = other.partyName;
        this.line = other.line;
        this.act = other.act;
        this.registrationDate = other.registrationDate;
        this.realNature = other.realNature;        
        this.policeStation = other.policeStation;
    }
    
        
    public boolean extractCaseDataDashboard()
    {
        String[] data = line.split("\",");
				// 1, 4
        String caseT = data[1].replace("\"", "");
        partyName = data[2].replace("\"", "");
        String regDate = data[3].replace("\"", "");
        
        //System.out.println("caseT: " + caseT + " regDate: " + regDate);
        
        
        SimpleDateFormat dateformat
            = new SimpleDateFormat("dd-MM-yyyy");
        
        this.stage = data[7].replace("\"", "");
        act = "";
        //System.out.println("ACT: " + data[7]);
        this.policeStation = "";
        
        

        String caseName[] = caseT.split("/");
        String regDateToken[] = regDate.split("-");
        
        registrationDate = regDate;       
        nature = caseName[0];
        
        
        try
        {
            //System.out.println("Case Year: " + caseName[2] + ", RegYear: " + regDateToken[2]);
            caseNo = Long.parseLong(caseName[1]);
            CaseYear = Integer.parseInt(caseName[2]);
            regYear = Integer.parseInt(regDateToken[2]);
            realNature = data[10].replace("\"", "");
        }
        catch(NumberFormatException e)
        {
                CaseYear = 0;
                regYear = 0;
        }
        catch(IndexOutOfBoundsException ex)
        {
            //System.out.println("CaseData: IndexOutOfBoundsException: " + ex.getMessage());
            realNature = "";
        }
        setRevisedNature();
        return true;
    }
    
    
    public boolean extractCaseDataQueryBuilder()
    {
        String[] data = line.split("\",");
				// 1, 4
        String caseT = data[1].replace("\"", "");
        partyName = data[2].replace("\"", "");
        String regDate = data[4].replace("\"", "");
        
        //System.out.println("caseT: " + caseT + " regDate: " + regDate);
        
        
       
        this.stage = data[6].replace("\"", "");
        this.act = data[7].replace("\"", "");
        //System.out.println("ACT: " + data[7]);
        this.policeStation = data[8].replace("\"", "");
        
        

        String caseName[] = caseT.split("/");
        String regDateToken[] = regDate.split("-");
        
        registrationDate = regDate;       

        nature = caseName[0];
        
        
        try
        {
            //System.out.println("Case Year: " + caseName[2] + ", RegYear: " + regDateToken[2]);
            caseNo = Long.parseLong(caseName[1]);
            CaseYear = Integer.parseInt(caseName[2]);
            regYear = Integer.parseInt(regDateToken[2]);
            
            realNature = data[data.length-1].replace("\"", "");
        }
        catch(NumberFormatException e)
        {
                CaseYear = 0;
                regYear = 0;
        }
        catch(IndexOutOfBoundsException ex)
        {
            //System.out.println("CaseData: IndexOutOfBoundsException: " + ex.getMessage());
            realNature = "";
        }
        setRevisedNature();
        return true;
    }
    
    
    public String getCaseNature()
    {
        return this.nature;
    }
    
    public void setCaseNature(String nature)
    {
        this.nature = nature;
    }
    
    public int getCaseYear()
    {
        return this.CaseYear;
    }
    
    public int getRegistrationYear()
    {
        return this.regYear;
    }
    
    public int getRevisedCaseYear()
    {
        return ( regYear < CaseYear && regYear >= 1951 ) ? 
                regYear : ((CaseYear >= 1951) ? CaseYear : regYear);
        
        /**
        if( regYear < CaseYear && regYear >= 1951 )
                    return this.regYear;
         else
             if(CaseYear >= 1951)
                return this.CaseYear;
            else
                return this.regYear;
         ***/
    }
    
    public String getCasePurpose()
    {
        return this.stage;
    }
    
    public void setCasePurpose(String purpose)
    {
        this.stage = purpose;
    }
    
    public void setPoliceStation(String ps)
    {
        this.policeStation = ps;
    }
    
    public String getCaseActSection()
    {
        return this.act;
    }
    
    public long getCaseNo()
    {
        return this.caseNo;
    }
    
    public String getPartyName()
    {
        return this.partyName;
    }
    
    public String getRealNature()
    {
        return  this.realNature;
    }
    
    public String getPoliceStation()
    {
        return this.policeStation;
    }
    
    public Date getRegistrationDate() throws ParseException
    {
         SimpleDateFormat dateformat = new SimpleDateFormat("dd-MM-yyyy");
         Date date = dateformat.parse("01-01-"+this.getRevisedCaseYear());
        try {
            date = dateformat.parse(this.registrationDate);
        } catch (ParseException ex) {
            
        }
         return date;
    }
    
    private void setCriminalCaseRevisedNature()
    {
       
    }
    
    /**
     * 
     */
    private void setCivilCaseRevisedNature()
    {         
        
    }
    
    private void setRevisedNature()
    {

          if (nature.equalsIgnoreCase("Misc Cases")
                        || nature.equalsIgnoreCase("Criminal Misc") 
                 || nature.equalsIgnoreCase("Criminal  Misc Cases")) {
             
         
                 if(this.act.contains("156(3)") ||
                     this.act.contains("Criminal Procedure Code--156(3)") ||
                             this.realNature.contains("156(3)") ||
                             this.realNature.contains("156"))
                   
                     
                     this.revisedNature = "Applications under Section 156(3) CrPC";
                   else
                    this.revisedNature = "Criminal Misc Cases";
          

        }
         /** else if(nature.equalsIgnoreCase("Warrant Case") ||
                 nature.equalsIgnoreCase("Cri Case") ||
                         nature.equalsIgnoreCase("Complaint Cases"))
                 this.revisedNature = "Warrant or Summons Criminal Case"; **/
         
         else if(nature.equalsIgnoreCase("Special Trial") 
                || nature.equalsIgnoreCase("Session Trial")
                || nature.equalsIgnoreCase("Sessions Case")) {
             
            if (this.act.contains("S.C/S.T Act")
                    || this.act.contains("S.C")
                    || this.act.contains("SC ST")) {
                this.revisedNature = "Session Trial Cases(SC ST Act)";
                
            } 
            
            else if (this.act.contains("Protection of Children From Sexual Offences") ||
                    this.act.contains("POCSO") ||
                    this.act.contains("P.O.C.S.O")) {
                this.revisedNature = "Session Trial Cases(POCSO Act)";                
            }
            
             else if (this.act.toLowerCase().contains("gangster") ||
                     this.act.toLowerCase().contains("gangester"))
                  this.revisedNature = "Session Trial Cases(Gangster Act)";
            
             else if (this.act.contains("N.D.P.S.Act")||
                     this.act.contains("narcotics"))
                  this.revisedNature = "Session Trial Cases(NDPS Act)";
            
            else {
                this.revisedNature = "Sessions Case";
            }
        }
         
         else if(this.nature.equalsIgnoreCase("Ndps"))
             this.revisedNature = "Session Trial Cases(NDPS Act)";
         
          else if(this.nature.equalsIgnoreCase("Criminal Appeal"))
             this.revisedNature = "Criminal Appeal";
         
         
         else if(this.nature.equalsIgnoreCase("Cri Case"))
         {
            if(this.act.toLowerCase().contains("electricity") || 
                 realNature.toLowerCase().contains("electricity")) 
                this.revisedNature = "Electricity Act Cases";
            
             else if(this.act.toLowerCase().contains("motor vehicle") || 
                 act.toLowerCase().contains("mv")) 
                this.revisedNature = "MV Act Cases";
             
              else if(this.act.toLowerCase().contains("domestic violence") ||
                        this.act.toLowerCase().contains("dv act")||
                      this.act.toLowerCase().contains("gharelu"))
                   this.revisedNature = "DV Act Cases";
              
            else
                this.revisedNature = nature;
         }
         else if(this.nature.equalsIgnoreCase("Complaint Case"))
         {

              if(this.act.toLowerCase().contains("electricity") || 
                 realNature.toLowerCase().contains("electricity")) 
                this.revisedNature = "Electricity Act Cases";
              
               else if(this.act.toLowerCase().contains("motor vehicle") || 
                 act.toLowerCase().contains("mv")) 
                this.revisedNature = "MV Act Cases";
               
               else if(this.act.toLowerCase().contains("domestic violence") ||
                        this.act.toLowerCase().contains("dv act")||
                      this.act.toLowerCase().contains("gharelu"))
                       
                   this.revisedNature = "DV Act Cases";
            else
                this.revisedNature = nature;
         }
          else if(this.nature.equalsIgnoreCase("Warrant or Summons Criminal Case"))
         {
             if(this.act.toLowerCase().contains("electricity") || 
                 realNature.toLowerCase().contains("electricity")) 
                this.revisedNature = "Electricity Act Cases";
             
             else if(this.act.toLowerCase().contains("motor vehicle") || 
                 act.toLowerCase().contains("mv")) 
                this.revisedNature = "MV Act Cases";
             
              else if(this.act.toLowerCase().contains("domestic violence") ||
                        this.act.toLowerCase().contains("dv act") ||
                      this.act.toLowerCase().contains("gharelu"))
                   this.revisedNature = "DV Act Cases";
              
            else
                this.revisedNature = nature;
         }
          else if(this.nature.equalsIgnoreCase("Warrant Case"))
              this.revisedNature = "Warrant or Summons Criminal Case";
         
          // civil cases nature
          else  if(nature.equalsIgnoreCase("Regular Civil Appeal"))
              if(this.act.toLowerCase().contains("rent"))
                   this.revisedNature = "Rent Control Appeal";
          else
             this.revisedNature = "Civil Appeal";
         
         else if(nature.equalsIgnoreCase("Misccivil") || 
                 nature.equalsIgnoreCase("Misc Regular")||
                         nature.equalsIgnoreCase("Misc Civil Cases"))
         {
             if(this.act.toLowerCase().contains("succession"))
                this.revisedNature = "Succession";
             else if(this.act.toLowerCase().contains("guardian"))
                 this.revisedNature = "Guardian and Wards Cases";
             else
                 this.revisedNature = "Misc Civil Cases";
         }
         else if( nature.equalsIgnoreCase("Oth"))
              this.revisedNature = "Misc Civil Cases";
             
         
         else if(nature.equalsIgnoreCase("Civil Reivision") ||
                 nature.equalsIgnoreCase("Civil Revision"))
             if(this.realNature.toLowerCase().contains("scc"))
                 this.revisedNature = "SCC Revision";
          else
             this.revisedNature = "Civil Revision";
         
         else if(nature.equalsIgnoreCase("Scc Revision"))
             this.revisedNature = "SCC Revision";
         
          else if(nature.equalsIgnoreCase("Civil Suit"))
          {
              if( this.act.toLowerCase().contains("scc") || this.realNature.toLowerCase().contains("scc"))
                this.revisedNature = "SCC Suit";
              else if(this.act.toLowerCase().contains("prescribed authority") || 
                      this.realNature.toLowerCase().contains("prescribed authority"))
                   this.revisedNature = "Suits of Prescribed Authority";
              else
                this.revisedNature = "Original Suit";
          }
          
          else if(nature.equalsIgnoreCase("Judge Small Cause Court Case") ||
                  nature.equalsIgnoreCase("Scc Suit") ||
                   nature.equalsIgnoreCase("Small Cause Court")) 
              this.revisedNature = "SCC Suit";
              
          else if(nature.equalsIgnoreCase("Pasuit"))
              this.revisedNature = "Suits of Prescribed Authority";
          
          else if (nature.equalsIgnoreCase("Gaurdian Wards Act"))
              this.revisedNature = "Guardian and Wards Cases";
          
         else
              this.revisedNature = nature;
                
    }
    
    public String getRevisedNature()
    {
        return this.revisedNature;
    }

    @Override
    public int compareTo(CaseData other)  {
        int val = -1;
        if(other == null)
            return -1;
        else {
            
            val = compare(this.getRevisedCaseYear(), other.getRevisedCaseYear());
            
            if(val == 0)
            {
                return compare(this.getCaseNo(), other.getCaseNo());
            }
                    
           return val;
                
         }
    }
    
}
