/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.naturewise_yearwise_gen;

import java.io.FileWriter;
import java.io.IOException;
import javax.swing.table.TableModel;

/**
 *
 * @author Anjan
 */
public class Writer {
    
    
    public static boolean writeToTextFile(String filename, TableModel model)
    {
        FileWriter writer = null;
        try {
            writer = new FileWriter(filename);
            for (int i = 0; i < model.getColumnCount(); i++) {
                writer.write("\"" + model.getColumnName(i) + "\"" + ",");
            }
            writer.write("\n");
            for (int i = 0; i < model.getRowCount(); i++) {
                for (int j = 0; j < model.getColumnCount(); j++) {
                    if(model.getValueAt(i, j) != null)
                        writer.write("\"" + model.getValueAt(i, j).toString() + "\"" + ",");
                    else
                        writer.write("\"\"" + ",");
                }
                writer.write("\n");
            }
            writer.close();
            
            }
            catch (IOException ex) {
               
               return false;
            }
                 
        return true;       
    }
    
    public static boolean appendToFile(FileWriter writer, String courtName, TableModel model)
    {
         try {
            writer.write("\"" + courtName + "\"");
            writer.write("\n");
            for (int i = 0; i < model.getColumnCount(); i++) {
                writer.write("\"" + model.getColumnName(i) + "\"" + ",");
            }
            writer.write("\n");
            for (int i = 0; i < model.getRowCount(); i++) {
                for (int j = 0; j < model.getColumnCount(); j++) {
                    if(model.getValueAt(i, j) != null)
                        writer.write("\"" + model.getValueAt(i, j).toString() + "\"" + ",");
                    else
                        writer.write("\"\"" + ",");
                }
                writer.write("\n");
            }
            
            }
            catch (IOException ex) {
               
               return false;
            }
                 
        return true;     
    }
    
    
    public static boolean writeToTextFileIgnoringEmptyColummns(String filename, TableModel model)
    {
        FileWriter writer = null;
        int rows = getActualRowCount(model);
        try {
            writer = new FileWriter(filename);
            // writing header to the text file
            for (int i = 0; i < model.getColumnCount(); i++) {
                
                if(model.getValueAt((rows-1), i) != null)
                    writer.write("\"" + model.getColumnName(i) + "\"" + ",");
            }
            writer.write("\n");
            
            for (int i = 0; i < model.getRowCount(); i++) {
                for (int j = 0; j < model.getColumnCount(); j++) {
                    if( (model.getValueAt(i, j) != null))
                        writer.write("\"" + model.getValueAt(i, j).toString() + "\"" + ",");
                     else
                    {
                        if((model.getValueAt((rows-1), j) != null))
                            writer.write("\"\"" + ",");

                        
                    }
                        
                   
                }
                writer.write("\n");
            }
            writer.close();
            
            }
            catch (IOException ex) {
               
               return false;
            }
                 
        return true;       
    }
    
    public static int getActualRowCount(TableModel model)
    {
       int rows = model.getRowCount();
       for(int i = 0; i < rows; i++)
       {
           if(model.getValueAt(i, 1).equals("GRAND TOTAL"))
               return (i+1);
       }
        return 1;        
    }
    
}
