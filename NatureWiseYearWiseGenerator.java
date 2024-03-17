/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.mycompany.naturewise_yearwise_gen;

import javax.swing.JTable;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.JOptionPane;

import java.awt.Component;
import java.awt.Cursor;
import javax.swing.JFileChooser;

import java.io.File;
import java.io.FileWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.swing.ButtonGroup;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

/**
 *
 * @author Anjan
 */
public class NatureWiseYearWiseGenerator extends javax.swing.JFrame {

    // Class variables
    private List<CaseType> allCases;
    private List<CaseType> allCases_merged;
    private File f;
    private DefaultTableModel model;
    private boolean buttonBrowseClicked;
    private final int mode;
    private String browsedFilePath;
    private File directory;
    private List<CaseData> casedata;
    private List<CaseData> casedata2;
    private boolean btnGenBreakupClicked;
    private String exportButtonOutFileName;
    private List<String> filter;
    private FilterForm filterform;
    public List<String> permanentFilterValues;

    /**
     * Creates new form NatureWiseYearWiseGenerator
     * @param mode
     */
    public NatureWiseYearWiseGenerator(int mode) {
        initComponents();
        buttonBrowseClicked = false;
        addJTableColumns();

        casedata = new ArrayList<>();
        allCases = new ArrayList<>();
        allCases_merged = new ArrayList<>();
        setButtonGrouptoCheckboxes();
        exportButtonFileChooser = new JFileChooser();
        btnGenBreakupClicked = false;
        this.mode = mode;
        setEnableJMenu();
        this.filterform = null;
        permanentFilterValues = new ArrayList();
        filter = new ArrayList();
    }
    
    /**
     * Method to decide which option was selected for opening the app
     */
    private void setEnableJMenu()
    {
        if(mode == 1)
            this.jMenu_tools.setEnabled(false);
        else
            this.jMenu_tools.setEnabled(true);
        
    }

    /**
     * Private method set the button group checkboxes
     */
    private void setButtonGrouptoCheckboxes() {
        ButtonGroup bg1 = new ButtonGroup();
        bg1.add(checkBox_queryBuilder);
        bg1.add(checkBox_dashboard);

    }

    /**
     * Method to set the form element text and browse button text as default
     * @param title
     * @param browseBtn 
     */
    public void setFormElementText(String title, String browseBtn) {
        this.setTitle(title);
        this.btnBrowse.setText(browseBtn);
    }

    /**
     * Method to generate year-wise breakup by reading the input csv file line by line
     */
    public void generateYearWiseBreakup() {
        
        if (f == null) {
            JOptionPane.showMessageDialog(null, "Please choose a CSV File", "Choose File",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        Scanner scan;

        allCases = new ArrayList<>();
        allCases_merged = new ArrayList<>();
        casedata = new ArrayList<>();

        String line = "";

        try {

            scan = new Scanner(f);
            scan.nextLine();

            while (scan.hasNextLine()) {
                String nature;
                int CaseYear;
                line = scan.nextLine();

                CaseData data = new CaseData(line);

                if (checkBox_queryBuilder.isSelected()) {
                    data.extractCaseDataQueryBuilder();
                } 
                else {
                    data.extractCaseDataDashboard();
                }

                casedata.add(data);
                CaseYear = data.getRevisedCaseYear();
                
                nature = data.getCaseNature();
                addCase(nature, CaseYear, allCases);               

                nature = data.getRevisedNature();
                addCase(nature, CaseYear, allCases_merged);
                
                addCase("GRAND TOTAL", CaseYear, allCases);
                addCase("GRAND TOTAL", CaseYear, allCases_merged);
            }
            // sort the case list arraylist based on institution year and then case number
            customSortCaseTypeArrayList(allCases, false);
            customSortCaseTypeArrayList(allCases_merged, true);
            // finally assign the casedata2 to casedata
            casedata2 = casedata;
            
        }
        
        catch(Exception e){
            JOptionPane.showMessageDialog(null, (e.getMessage() + "\n\n" + line), 
                    "Error reading file",JOptionPane.ERROR_MESSAGE);
        }

    }
    
    /**
     * Method to filter the cases based on their nature
     * @param selectedNature 
     */
    public void filterCases(List<String> selectedNature)
    {
        casedata = casedata2;
        this.filter = selectedNature;
        int length = filter.size();
        if(length > 0)
        {
            
           // List<CaseType> allCasesTemp_merged = new ArrayList();
           ArrayList <CaseData> casedata_t = new ArrayList<>();
           ArrayList <CaseType> allCases_t = new ArrayList<>();
           boolean flag = false;
           boolean finalFlag = false;
           
           // loop for each casedata in list
            for(int i = 0; i < casedata.size(); i++)
            {
                CaseData d = casedata.get(i);
                flag = false;
                boolean grand_total_flag = false;
                for(int k = 0; k < length; k++)
                {
                    
                    if(d.getCaseNature().equals(filter.get(k)))
                    {
                        flag = true;
                        finalFlag = true;
                    }
                    if(filter.get(k).equals("GRAND TOTAL"))
                    {
                        grand_total_flag = true;
                    }
                    
                }
                if(flag)
                {
                    casedata_t.add(d);
                    addCase(d.getCaseNature(), d.getRevisedCaseYear(), allCases_t);
                    if(length > 1 && !grand_total_flag)
                        addCase("GRAND TOTAL", d.getRevisedCaseYear(), allCases_t);
                }
                if(grand_total_flag)
                {
                    casedata_t.add(d);
                    addCase("GRAND TOTAL", d.getRevisedCaseYear(), allCases_t);
                }
            
            }
            casedata = casedata_t;
            
            customSortCaseTypeArrayList(allCases_t, false);
            clearTable();
            this.addJTableColumns();
            addTableRows(allCases_t);
            
            this.tf_output.setText("OUTPUT -> " + f.getName() + " Selected_Nature_Breakup");
            table.setDefaultEditor(Object.class, null);
            
        }
        else
        {
            this.btnGenBreakupActionPerformed(null);
        }
    }
    
     public void PermanentfilterCases(List<String> permanentFilterNature)
     {
         if(!permanentFilterNature.isEmpty())
            filterCases(permanentFilterNature);
     }

    public void addCase(String c, int year, List<CaseType> AllCases) {
        CaseType CT = new CaseType(c);
        int indx = AllCases.indexOf(new CaseType(c));

        //System.out.println("CT: " + CT.getCaseNature() + " and indx: " + indx);
        if (indx != -1) {
            CaseType temp = AllCases.get(indx);
            temp.addYear(year);
            AllCases.set(indx, temp);
        } else {
            CT.addYear(year);
            AllCases.add(CT);
        }
    }

    private void addJTableColumns() {
        model = new DefaultTableModel();
        table.setAutoscrolls(true);
        model.addColumn("Sr. No.");
        model.addColumn("Case Type");
        model.addColumn("TOTAL");
        
        int currYr = Calendar.getInstance().get(Calendar.YEAR);

        for (int i = currYr; i >= 1961; i--) 
        {
           model.addColumn(i);
        }

        table.setModel(model);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        TableColumnModel tca = table.getColumnModel();
        tca.getColumn(0).setPreferredWidth(50);
        tca.getColumn(1).setPreferredWidth(200);

        //tca.getColumn(2).setPreferredWidth(50);
        tca.getColumn(4).setPreferredWidth(100);
        tca.getColumn(5).setPreferredWidth(100);
        tca.getColumn(6).setPreferredWidth(130);

        table.setRowHeight(25);
    }

    public void addTableRows(List<CaseType> allCaseTypes) {
        
        int srno = 1;
        // declaring variables of age wise breakup

        //SimpleDateFormat dateformat = new SimpleDateFormat("dd-MM-yyyy");
        int currYr = Calendar.getInstance().get(Calendar.YEAR);

        model.setRowCount(allCaseTypes.size() + 1);

        for (CaseType ct : allCaseTypes) {
            
            if(ct.getCaseNature().equals("GRAND TOTAL") == false)
                model.setValueAt(srno, srno - 1, 0);
            
            model.setValueAt(ct.getCaseNature(), srno - 1, 1);
            model.setValueAt(ct.getTotal(), srno - 1, 2);

            for (int i = 0; i < ct.getAllYearFrequency().size(); i++) {

                int year = ct.getAllYearFrequency().get(i).getYear();
                int freq = ct.getAllYearFrequency().get(i).getFrequency();
                //System.out.println("Year: "+year+", Freq: "+freq+", Details: " + ct.getCaseNature());
                model.setValueAt(freq, srno - 1, (currYr - year) + 3);

            }
            srno++; // to increment the row count
        }
       
    }
    
    /**
     * Method to sort the All Cases Breakup in a pre-defined method
     * @param allCases
     * @param merged 
     */
    private void customSortCaseTypeArrayList(List<CaseType> allCases, boolean merged)
    {
        ArrayList<String> refrenceAllCases = new ArrayList<>();

        refrenceAllCases.add("Cri Case");
        refrenceAllCases.add("Complaint Cases");
        refrenceAllCases.add("Warrant or Summons Criminal Case");
        refrenceAllCases.add("Misc Cases");
        refrenceAllCases.add("Criminal Misc");
        refrenceAllCases.add("Criminal  Misc Cases");
        refrenceAllCases.add("Final Report");
        

        ArrayList<String> refrenceAllCasesMerged = new ArrayList<>();
        refrenceAllCasesMerged.add("Session Trial Cases(SC ST Act)");
        refrenceAllCasesMerged.add("Session Trial Cases(POCSO Act)");
        refrenceAllCasesMerged.add("Session Trial Cases(NDPS Act)");
        refrenceAllCasesMerged.add("Session Trial Cases(Gangster Act)");
        refrenceAllCasesMerged.add("Electricity Act Cases");
        refrenceAllCasesMerged.add("Sessions Case");
        
        refrenceAllCasesMerged.add("Criminal Appeal");
        refrenceAllCasesMerged.add("Criminal Revision");
        
        refrenceAllCasesMerged.add("Cri Case");
        refrenceAllCasesMerged.add("Complaint Cases");
        refrenceAllCasesMerged.add("Warrant or Summons Criminal Case");
         refrenceAllCasesMerged.add("MV Act Cases");
          refrenceAllCasesMerged.add("DV Act Cases");
        refrenceAllCasesMerged.add("Final Report");
        refrenceAllCasesMerged.add("Applications under Section 156(3) CrPC");
        refrenceAllCasesMerged.add("Criminal Misc Cases");
        
        
       refrenceAllCasesMerged.add("Original Suit");
       refrenceAllCasesMerged.add("SCC Suit");
       refrenceAllCasesMerged.add("Suits of Prescribed Authority");
       refrenceAllCasesMerged.add("Guardian and Wards Cases");
       refrenceAllCasesMerged.add("Civil Revision");
       refrenceAllCasesMerged.add("SCC Revision");
       refrenceAllCasesMerged.add("Civil Appeal");
       
       refrenceAllCasesMerged.add("Execution");
       refrenceAllCasesMerged.add("Succession");
       refrenceAllCasesMerged.add("Misc Civil Cases");
               
        
        
        
        if(!merged)
        {
            for(int i = 0; i < refrenceAllCases.size(); i++)
            {
                String referenceCaseNature = refrenceAllCases.get(i);
                int index = allCases.indexOf(new CaseType(referenceCaseNature));
                if(index != -1)
                {
                    CaseType ct1 = allCases.get(i);
                    CaseType ct2 = allCases.get(index);
                    allCases.set(i, ct2);
                    allCases.set(index, ct1);
                }
                else
                {
                    refrenceAllCases.remove(i);
                    i--;
                }
            }
           
        }
        else
        {
             for(int i = 0; i < refrenceAllCasesMerged.size(); i++)
            {
                String referenceCaseNature = refrenceAllCasesMerged.get(i);
                int index = allCases.indexOf(new CaseType(referenceCaseNature));
                if(index != -1)
                {
                    CaseType ct1 = allCases.get(i);
                    CaseType ct2 = allCases.get(index);
                    allCases.set(i, ct2);
                    allCases.set(index, ct1);
                }
                else
                {
                    refrenceAllCasesMerged.remove(i);
                    i--;
                }
            }
             
        }
         int index = allCases.indexOf(new CaseType("GRAND TOTAL"));
            if(index != allCases.size()-1 && index != -1)
            {
                CaseType ct1 = allCases.get(index);
                CaseType ct2 = allCases.get(allCases.size()-1);
                allCases.set(index, ct2);
                allCases.set(allCases.size()-1, ct1);
            }

    }

    private void addAgeWiseBreakup(List<CaseType> allCaseTypes) {
        long zero2fiveYears = 0;
        long five2tenYears = 0;
        long ten2fifteenYears = 0;
        long fifteen2twentyYears = 0;
        long morethan20Years = 0;
        int currYr = Calendar.getInstance().get(Calendar.YEAR);

        for (CaseType ct : allCaseTypes) {
            for (int i = 0; i < ct.getAllYearFrequency().size(); i++) {

                int year = ct.getAllYearFrequency().get(i).getYear();
                int freq = ct.getAllYearFrequency().get(i).getFrequency();

                int diff = (currYr - year);

                if (diff < 7) {
                    zero2fiveYears += freq;
                } else if (diff >= 7 && diff < 12) {
                    five2tenYears += freq;
                } else if (diff >= 12 && diff < 17) {
                    ten2fifteenYears += freq;
                } else if (diff >= 17 && diff < 22) {
                    fifteen2twentyYears += freq;
                } else if (diff >= 22) {
                    morethan20Years += freq;
                }
            }

        }

        model.addRow(new Object[]{"", "Age-Wise Breakup of Pendency", "0 - 5 years", "5 - 10 years",
            "10 - 15 years", "15 - 20 years", "More than 20 years"});

        model.addRow(new Object[]{"", "", zero2fiveYears, five2tenYears, ten2fifteenYears,
            fifteen2twentyYears, morethan20Years});

    }

    public static void setThisFileFilter(JFileChooser chooser) {
        chooser.setFileFilter(new FileFilter() {
            @Override
            public String getDescription() {
                return "CSV Files (*.csv)";
            }

            @Override
            public boolean accept(File myfile) {
                if (myfile.isDirectory()) {
                    return true;
                } else {
                    String filename = myfile.getName().toLowerCase();
                    return filename.endsWith(".csv");
                }
            }
        });
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">                          
    private void initComponents() {

        jDesktopPane1 = new javax.swing.JDesktopPane();
        tf_FilePath = new javax.swing.JTextField();
        btnBrowse = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        table = new javax.swing.JTable(){//Implement table cell tool tips.
            public String getToolTipText(java.awt.event.MouseEvent e) {
                String tipToDisplay = null;
                java.awt.Point p = e.getPoint();
                int row = rowAtPoint(p);
                int col = columnAtPoint(p);

                try {
                    //comment row, exclude heading
                    if(row != 0){
                        tipToDisplay = getValueAt(row, col).toString();
                    }
                } catch (RuntimeException e1) {
                    //catch null pointer exception if mouse is over an empty line
                }

                return tipToDisplay;
            }
        };
        btnGenBreakup = new javax.swing.JButton();
        btnExport = new javax.swing.JButton();
        btnClearAll = new javax.swing.JButton();
        combobox_selectFile = new javax.swing.JComboBox<>();
        combobox_folder = new javax.swing.JComboBox<>();
        lbl_selctFile = new java.awt.Label();
        lbl_selectFolder = new java.awt.Label();
        panel_dataSource1 = new javax.swing.JPanel();
        checkBox_dashboard = new javax.swing.JCheckBox();
        checkBox_queryBuilder = new javax.swing.JCheckBox();
        panel_exportStats = new javax.swing.JPanel();
        checkBox_ignoreAgeWise = new javax.swing.JCheckBox();
        checkBox_ignoreEmptyCols = new javax.swing.JCheckBox();
        jPanel1 = new javax.swing.JPanel();
        lbl_home = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        checkBox_mergeSimilarCases = new javax.swing.JCheckBox();
        checkBox_hideAgewise = new javax.swing.JCheckBox();
        checkBox_hideEmptyCols = new javax.swing.JCheckBox();
        tf_output = new javax.swing.JTextField();
        menuBar_mainBar = new javax.swing.JMenuBar();
        jMenu_File = new javax.swing.JMenu();
        menuItem_save = new javax.swing.JMenuItem();
        menu_report = new javax.swing.JMenu();
        jMenu2 = new javax.swing.JMenu();
        menuItem_5oldest = new javax.swing.JMenuItem();
        menuItem_10oldest = new javax.swing.JMenuItem();
        menuItem_20oldest = new javax.swing.JMenuItem();
        menuItem_30oldest = new javax.swing.JMenuItem();
        menuItem_GenListUptoYear = new javax.swing.JMenuItem();
        jMenu_tools = new javax.swing.JMenu();
        jMenuItem_genFilewiseBreakup = new javax.swing.JMenuItem();
        jMenuItem_mergeGenBreakup = new javax.swing.JMenuItem();
        jMenuItem_fileWise_to_singlefile = new javax.swing.JMenuItem();
        jMenu_filter = new javax.swing.JMenu();
        jMenuItem_byNature = new javax.swing.JMenuItem();
        jMenuItem_ByYear = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jDesktopPane1.setBackground(new java.awt.Color(0, 102, 102));

        tf_FilePath.setBackground(javax.swing.UIManager.getDefaults().getColor("Button.default.focusedBackground"));
        tf_FilePath.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        tf_FilePath.setToolTipText("This area will display the path of the file/folder selected.");
        tf_FilePath.setEnabled(false);
        tf_FilePath.setPreferredSize(new java.awt.Dimension(96, 41));

        btnBrowse.setBackground(new java.awt.Color(255, 102, 102));
        btnBrowse.setFont(new java.awt.Font("Segoe UI Black", 0, 18)); // NOI18N
        btnBrowse.setToolTipText("Use button to browse for a file/folder");
        btnBrowse.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED, null, new java.awt.Color(102, 0, 153), null, null));
        btnBrowse.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnBrowse.setLabel("Browse File");
        btnBrowse.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBrowseActionPerformed(evt);
            }
        });

        table.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        table.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        table.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_ALL_COLUMNS);
        table.setColumnSelectionAllowed(true);
        table.setEditingColumn(0);
        table.setEditingRow(0);
        table.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        table.setShowHorizontalLines(true);
        table.setShowVerticalLines(true);
        table.getTableHeader().setResizingAllowed(false);
        table.getTableHeader().setReorderingAllowed(false);
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tableMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                tableMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                tableMouseExited(evt);
            }
        });
        jScrollPane1.setViewportView(table);

        btnGenBreakup.setBackground(new java.awt.Color(102, 102, 255));
        btnGenBreakup.setText("Generate Breakup");
        btnGenBreakup.setToolTipText("Click this button to Generate Nature-wise Year-wise breakup from a csv file containg the case information");
        btnGenBreakup.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGenBreakupActionPerformed(evt);
            }
        });

        btnExport.setBackground(new java.awt.Color(102, 204, 0));
        btnExport.setText("Export the table to a CSV File");
        btnExport.setToolTipText("Click this button to save the above table into a csv file");
        btnExport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExportActionPerformed(evt);
            }
        });

        btnClearAll.setBackground(java.awt.Color.red);
        btnClearAll.setText("Clear All");
        btnClearAll.setToolTipText("Click this button to Reset the form.");
        btnClearAll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnClearAllActionPerformed(evt);
            }
        });

        combobox_selectFile.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                combobox_selectFileItemStateChanged(evt);
            }
        });

        combobox_folder.setFont(new java.awt.Font("Segoe UI", 0, 15)); // NOI18N
        combobox_folder.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                combobox_folderItemStateChanged(evt);
            }
        });

        lbl_selctFile.setAlignment(java.awt.Label.RIGHT);
        lbl_selctFile.setBackground(java.awt.Color.lightGray);
        lbl_selctFile.setFont(new java.awt.Font("Dialog", 0, 15)); // NOI18N
        lbl_selctFile.setText("Select File:");

        lbl_selectFolder.setAlignment(java.awt.Label.RIGHT);
        lbl_selectFolder.setBackground(java.awt.Color.lightGray);
        lbl_selectFolder.setFont(new java.awt.Font("Dialog", 0, 15)); // NOI18N
        lbl_selectFolder.setText("Select Folder:");

        panel_dataSource1.setBackground(new java.awt.Color(255, 102, 102));
        panel_dataSource1.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED), "Select Data Source"));

        checkBox_dashboard.setToolTipText("Select this Checkbox if the csv file has been generated from the Establishment/Court's Dashboard.");
        checkBox_dashboard.setInheritsPopupMenu(true);
        checkBox_dashboard.setLabel("CIS Dashboard");

        checkBox_queryBuilder.setSelected(true);
        checkBox_queryBuilder.setText("CIS Query Builder");
        checkBox_queryBuilder.setToolTipText("Select this Checkbox if the csv file has been generated from the CIS Query Builder");

        javax.swing.GroupLayout panel_dataSource1Layout = new javax.swing.GroupLayout(panel_dataSource1);
        panel_dataSource1.setLayout(panel_dataSource1Layout);
        panel_dataSource1Layout.setHorizontalGroup(
            panel_dataSource1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel_dataSource1Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addGroup(panel_dataSource1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(checkBox_queryBuilder)
                    .addComponent(checkBox_dashboard))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panel_dataSource1Layout.setVerticalGroup(
            panel_dataSource1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panel_dataSource1Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(checkBox_queryBuilder)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 23, Short.MAX_VALUE)
                .addComponent(checkBox_dashboard)
                .addGap(21, 21, 21))
        );

        panel_exportStats.setBackground(new java.awt.Color(102, 204, 0));
        panel_exportStats.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.MatteBorder(null), "Export Stats"));

        checkBox_ignoreAgeWise.setSelected(true);
        checkBox_ignoreAgeWise.setText("Ignore Age Wise Breakup");
        checkBox_ignoreAgeWise.setToolTipText("Select this checkbox if you want to export the table as is");
        checkBox_ignoreAgeWise.setEnabled(false);

        checkBox_ignoreEmptyCols.setText("Ignore Empty Columns");
        checkBox_ignoreEmptyCols.setToolTipText("Select this option if you want to export the breakup without the empty columns ");
        checkBox_ignoreEmptyCols.setEnabled(false);
        checkBox_ignoreEmptyCols.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                checkBox_ignoreEmptyColsItemStateChanged(evt);
            }
        });

        javax.swing.GroupLayout panel_exportStatsLayout = new javax.swing.GroupLayout(panel_exportStats);
        panel_exportStats.setLayout(panel_exportStatsLayout);
        panel_exportStatsLayout.setHorizontalGroup(
            panel_exportStatsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel_exportStatsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panel_exportStatsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(checkBox_ignoreEmptyCols)
                    .addComponent(checkBox_ignoreAgeWise))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panel_exportStatsLayout.setVerticalGroup(
            panel_exportStatsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panel_exportStatsLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(checkBox_ignoreAgeWise)
                .addGap(18, 18, 18)
                .addComponent(checkBox_ignoreEmptyCols)
                .addGap(31, 31, 31))
        );

        jPanel1.setBackground(new java.awt.Color(204, 204, 204));

        lbl_home.setBackground(new java.awt.Color(255, 255, 255));
        lbl_home.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        lbl_home.setForeground(new java.awt.Color(51, 51, 0));
        lbl_home.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbl_home.setText("Home");
        lbl_home.setToolTipText("Go to the Application Selection Form");
        lbl_home.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 3));
        lbl_home.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        lbl_home.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lbl_homeMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lbl_home, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lbl_home, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 2), "Select Display Stats"));

        checkBox_mergeSimilarCases.setSelected(true);
        checkBox_mergeSimilarCases.setText("Merge Similar Cases");
        checkBox_mergeSimilarCases.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                checkBox_mergeSimilarCasesItemStateChanged(evt);
            }
        });

        checkBox_hideAgewise.setSelected(true);
        checkBox_hideAgewise.setText("Hide Age Wise Breakup");
        checkBox_hideAgewise.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                checkBox_hideAgewiseItemStateChanged(evt);
            }
        });

        checkBox_hideEmptyCols.setText("Hide Empty Columns");
        checkBox_hideEmptyCols.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                checkBox_hideEmptyColsItemStateChanged(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(checkBox_mergeSimilarCases)
                    .addComponent(checkBox_hideAgewise)
                    .addComponent(checkBox_hideEmptyCols))
                .addContainerGap(27, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(checkBox_mergeSimilarCases)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(checkBox_hideAgewise)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(checkBox_hideEmptyCols)
                .addContainerGap(22, Short.MAX_VALUE))
        );

        tf_output.setEditable(false);
        tf_output.setFont(new java.awt.Font("Serif", 1, 14)); // NOI18N
        tf_output.setForeground(new java.awt.Color(0, 204, 0));
        tf_output.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        tf_output.setText("Name of the Breakup Table");

        jDesktopPane1.setLayer(tf_FilePath, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jDesktopPane1.setLayer(btnBrowse, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jDesktopPane1.setLayer(jScrollPane1, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jDesktopPane1.setLayer(btnGenBreakup, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jDesktopPane1.setLayer(btnExport, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jDesktopPane1.setLayer(btnClearAll, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jDesktopPane1.setLayer(combobox_selectFile, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jDesktopPane1.setLayer(combobox_folder, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jDesktopPane1.setLayer(lbl_selctFile, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jDesktopPane1.setLayer(lbl_selectFolder, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jDesktopPane1.setLayer(panel_dataSource1, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jDesktopPane1.setLayer(panel_exportStats, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jDesktopPane1.setLayer(jPanel1, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jDesktopPane1.setLayer(jPanel2, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jDesktopPane1.setLayer(tf_output, javax.swing.JLayeredPane.DEFAULT_LAYER);

        javax.swing.GroupLayout jDesktopPane1Layout = new javax.swing.GroupLayout(jDesktopPane1);
        jDesktopPane1.setLayout(jDesktopPane1Layout);
        jDesktopPane1Layout.setHorizontalGroup(
            jDesktopPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jDesktopPane1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jDesktopPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(lbl_selectFolder, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(panel_dataSource1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnBrowse, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(panel_exportStats, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(33, 33, 33)
                .addGroup(jDesktopPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jDesktopPane1Layout.createSequentialGroup()
                        .addComponent(btnExport)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnClearAll, javax.swing.GroupLayout.PREFERRED_SIZE, 152, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())
                    .addGroup(jDesktopPane1Layout.createSequentialGroup()
                        .addGroup(jDesktopPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(tf_FilePath, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(jDesktopPane1Layout.createSequentialGroup()
                                .addComponent(combobox_folder, javax.swing.GroupLayout.PREFERRED_SIZE, 223, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(39, 39, 39)
                                .addComponent(tf_output, javax.swing.GroupLayout.DEFAULT_SIZE, 469, Short.MAX_VALUE)
                                .addGap(43, 43, 43)
                                .addComponent(lbl_selctFile, javax.swing.GroupLayout.PREFERRED_SIZE, 136, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(44, 44, 44)
                        .addGroup(jDesktopPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnGenBreakup, javax.swing.GroupLayout.PREFERRED_SIZE, 255, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(combobox_selectFile, javax.swing.GroupLayout.PREFERRED_SIZE, 255, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jScrollPane1)))
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jDesktopPane1Layout.setVerticalGroup(
            jDesktopPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jDesktopPane1Layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jDesktopPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(tf_FilePath, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jDesktopPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btnBrowse, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnGenBreakup, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addGroup(jDesktopPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(combobox_selectFile, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jDesktopPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(lbl_selctFile, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(lbl_selectFolder, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(tf_output, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 43, Short.MAX_VALUE)
                        .addComponent(combobox_folder)))
                .addGap(25, 25, 25)
                .addGroup(jDesktopPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jDesktopPane1Layout.createSequentialGroup()
                        .addComponent(jScrollPane1)
                        .addGap(18, 18, 18)
                        .addGroup(jDesktopPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnClearAll, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnExport, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jDesktopPane1Layout.createSequentialGroup()
                        .addComponent(panel_dataSource1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(panel_exportStats, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(9, 9, 9))
        );

        jMenu_File.setText("File");

        menuItem_save.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        menuItem_save.setText("Save");
        menuItem_save.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItem_saveActionPerformed(evt);
            }
        });
        jMenu_File.add(menuItem_save);

        menuBar_mainBar.add(jMenu_File);

        menu_report.setText("Report");

        jMenu2.setText("Generate Oldest case list Nature-wise");

        menuItem_5oldest.setText("Generate 5 Oldest case list Nature-wise");
        menuItem_5oldest.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItem_5oldestActionPerformed(evt);
            }
        });
        jMenu2.add(menuItem_5oldest);

        menuItem_10oldest.setText("Generate 10 Oldest case list Nature-wise");
        menuItem_10oldest.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItem_10oldestActionPerformed(evt);
            }
        });
        jMenu2.add(menuItem_10oldest);

        menuItem_20oldest.setText("Generate 20 Oldest case list Nature-wise");
        menuItem_20oldest.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItem_20oldestActionPerformed(evt);
            }
        });
        jMenu2.add(menuItem_20oldest);

        menuItem_30oldest.setText("Generate 30 Oldest case list Nature-wise");
        menuItem_30oldest.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItem_30oldestActionPerformed(evt);
            }
        });
        jMenu2.add(menuItem_30oldest);

        menu_report.add(jMenu2);

        menuItem_GenListUptoYear.setText("Generate Case list upto year");
        menuItem_GenListUptoYear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItem_GenListUptoYearActionPerformed(evt);
            }
        });
        menu_report.add(menuItem_GenListUptoYear);

        menuBar_mainBar.add(menu_report);

        jMenu_tools.setText("Tools");

        jMenuItem_genFilewiseBreakup.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F, java.awt.event.InputEvent.ALT_DOWN_MASK | java.awt.event.InputEvent.CTRL_DOWN_MASK));
        jMenuItem_genFilewiseBreakup.setText("Generate File-wise breakup");
        jMenuItem_genFilewiseBreakup.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem_genFilewiseBreakupActionPerformed(evt);
            }
        });
        jMenu_tools.add(jMenuItem_genFilewiseBreakup);

        jMenuItem_mergeGenBreakup.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_M, java.awt.event.InputEvent.ALT_DOWN_MASK | java.awt.event.InputEvent.CTRL_DOWN_MASK));
        jMenuItem_mergeGenBreakup.setText("Generate merged breakup");
        jMenuItem_mergeGenBreakup.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem_mergeGenBreakupActionPerformed(evt);
            }
        });
        jMenu_tools.add(jMenuItem_mergeGenBreakup);

        jMenuItem_fileWise_to_singlefile.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.ALT_DOWN_MASK | java.awt.event.InputEvent.CTRL_DOWN_MASK));
        jMenuItem_fileWise_to_singlefile.setText("Output File-wise breakup to a single file");
        jMenuItem_fileWise_to_singlefile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem_fileWise_to_singlefileActionPerformed(evt);
            }
        });
        jMenu_tools.add(jMenuItem_fileWise_to_singlefile);

        menuBar_mainBar.add(jMenu_tools);

        jMenu_filter.setText("Filter");

        jMenuItem_byNature.setText("By Case Nature");
        jMenuItem_byNature.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem_byNatureActionPerformed(evt);
            }
        });
        jMenu_filter.add(jMenuItem_byNature);

        jMenuItem_ByYear.setText("By Year");
        jMenu_filter.add(jMenuItem_ByYear);

        menuBar_mainBar.add(jMenu_filter);

        setJMenuBar(menuBar_mainBar);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jDesktopPane1)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addComponent(jDesktopPane1))
        );

        pack();
    }// </editor-fold>                        

    private void btnBrowseActionPerformed(java.awt.event.ActionEvent evt) {                                          
        if (mode == 1) {
            JFileChooser chooser = new JFileChooser();
            String directoryToBrowse = tf_FilePath.getText();
            File file = null;
            try {
                file = new File(directoryToBrowse);
            } catch (Exception e) {

            }
            if (file == null || file.toString().equals("")) {
                chooser.setCurrentDirectory(new File(System.getProperty("user.home") + 
                        System.getProperty("file.separator") + "Desktop"));

            } else {
                //System.out.println("F is not null: " + file.toString());
                chooser.setCurrentDirectory(file);

            }
            setThisFileFilter(chooser);
            int retVal = chooser.showOpenDialog((Component) evt.getSource());

            if (retVal == chooser.APPROVE_OPTION) {
                f = chooser.getSelectedFile();
                this.browsedFilePath = f.getPath();
                tf_FilePath.setText(f.getPath());
                buttonBrowseClicked = true;
            }
        } else if (mode == 2) {
            JFileChooser chooser = new JFileChooser();
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            String directoryToBrowse = tf_FilePath.getText();
            File file = null;
            try {
                file = new File(directoryToBrowse);
            } catch (Exception e) {

            }
            if (file == null || file.toString().equals("")) {
                chooser.setCurrentDirectory(new File(System.getProperty("user.home")
                        + System.getProperty("file.separator") + "Desktop"));

            } else {
                chooser.setCurrentDirectory(file);
            }

            //setThisFileFilter(chooser);
            int retVal = chooser.showOpenDialog((Component) evt.getSource());

            if (retVal == chooser.APPROVE_OPTION) {
                f = chooser.getSelectedFile();
                this.browsedFilePath = f.getPath();
                tf_FilePath.setText(f.getPath());
                buttonBrowseClicked = true;
                File fArray[] = {f};
                populatefolderItems(fArray);
            }
        }
        setFilePath();
    }                                         

    private void populatefolderItems(File[] files) {
        this.combobox_folder.removeAllItems();
        this.combobox_selectFile.removeAllItems();
        for (File fileT : files[0].listFiles()) {
            if (fileT.isDirectory()) {
                this.combobox_folder.addItem(fileT.getName());
            } // populateCheckBox(fileT.listFiles());
            else {
                this.combobox_selectFile.addItem(fileT.getName());
            }
        }

    }

    private void setFilePath() {
        if (mode == 2) {
            f = new File(browsedFilePath + "/" + this.combobox_folder.getSelectedItem() + "/"
                    + this.combobox_selectFile.getSelectedItem());
        }        
    }

    private void btnGenBreakupActionPerformed(java.awt.event.ActionEvent evt) {                                              

        if (buttonBrowseClicked) {
            clearTable();
            generateYearWiseBreakup();
            this.addJTableColumns();

            addTableRows(this.allCases);
            this.tf_output.setText("OUTPUT -> " + f.getName() + " Breakup");
            table.setDefaultEditor(Object.class, null);
            btnGenBreakupClicked = true;
            
            
            // check for checkbox conditions
            this.checkBox_hideAgewiseItemStateChanged(null);
            this.checkBox_mergeSimilarCasesItemStateChanged(null);
            this.checkBox_hideEmptyColsItemStateChanged(null);
            //filterCases();
            this.exportButtonOutFileName = f.getName().substring(0, (f.getName().length() - 4));
            sortCaseData();
            this.populateFilterFormData();
            PermanentfilterCases(this.permanentFilterValues);
        } else {
            JOptionPane.showMessageDialog(rootPane, "Select a file first!",
                    "No CSV File", JOptionPane.ERROR_MESSAGE);
        }
    }                                             

    private void sortCaseData() {
        // Create an ExecutorService with a single thread
        ExecutorService executorService = Executors.newSingleThreadExecutor();

        // Create a Runnable to sort the list
        Runnable sortTask = () -> {
            Collections.sort(casedata);

        };

        // Submit the sort task to the executor service
        executorService.submit(sortTask);

        // Shutdown the executor service
        executorService.shutdown();
    }

    private JFileChooser exportButtonFileChooser;

    private void btnExportActionPerformed(java.awt.event.ActionEvent evt) {                                          

        if (btnGenBreakupClicked == false) {
            JOptionPane.showMessageDialog(rootPane, "Please generate a breakup table first!",
                    "No Table Generated", JOptionPane.ERROR_MESSAGE);
        } else {
            if (exportButtonFileChooser.getSelectedFile() == null) {
                exportButtonFileChooser.setCurrentDirectory(new File(System.getProperty("user.home") + System.getProperty("file.separator") + "Desktop"));
            } else {
                exportButtonFileChooser.setCurrentDirectory(exportButtonFileChooser.getSelectedFile());
            }
            setThisFileFilter(exportButtonFileChooser);

            exportButtonFileChooser.setSelectedFile(new File(exportButtonOutFileName + "_Breakup"));
            int retVal = exportButtonFileChooser.showSaveDialog((Component) evt.getSource());

            if (retVal == exportButtonFileChooser.APPROVE_OPTION) {

                String filename = exportButtonFileChooser.getSelectedFile().toString();

                if (!filename.substring((filename.length() - 4), (filename.length() - 1)).toLowerCase().equals(".csv")) {
                    filename = filename + ".csv";
                }

                boolean success = false;

                if (checkBox_ignoreEmptyCols.isSelected()) {
                        success = Writer.writeToTextFileIgnoringEmptyColummns(filename, 
                                this.model);
                 
                } else {
                    success = Writer.writeToTextFile(filename, this.model);
                }

                if (success) {
                    JOptionPane.showMessageDialog(rootPane, "File " + filename + " Created successfully!",
                            "Write Success!",
                            JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(rootPane, "Error in creating the file!", 
                            "Write Failure", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }                                         

    public void clearTable() {
        DefaultTableModel dtm = (DefaultTableModel) table.getModel();
        dtm.setRowCount(0);
        dtm.getDataVector().removeAllElements();
    }


    private void btnClearAllActionPerformed(java.awt.event.ActionEvent evt) {                                            
        // TODO add your handling code here:
        clearTable();
        tf_FilePath.setText("");
        buttonBrowseClicked = false;
        combobox_folder.removeAllItems();
        combobox_selectFile.removeAllItems();
        exportButtonFileChooser = new JFileChooser();
        checkBox_queryBuilder.setSelected(true);
        this.casedata.clear();
        this.allCases.clear();
        this.permanentFilterValues.clear();
        this.allCases_merged.clear();
        this.btnGenBreakupClicked = false;
        this.tf_output.setText("");
    }                                           

    private void combobox_folderItemStateChanged(java.awt.event.ItemEvent evt) {                                                 
        this.combobox_selectFile.removeAllItems();
        // TODO add your handling code here:
        if (this.combobox_folder.getItemCount() != 0) {
            String folder = this.combobox_folder.getSelectedItem().toString();
            directory = new File(this.browsedFilePath + "/" + folder);
            File allFiles[] = directory.listFiles();
            for (File f : allFiles) {
                this.combobox_selectFile.addItem(f.getName());

                // setting the file path on each change of the folder items    
                this.setFilePath();
            }
        }

    }                                                


    private void tableMouseClicked(java.awt.event.MouseEvent evt) {                                   
        
        int row = table.getSelectedRow();
        int col = table.getSelectedColumn();
        if(table.getValueAt(row, col) == null)
            return;
        

        //System.out.println("Row: " + row + ", Column: " + col);
        if (table.getValueAt(row, col) != null) {
            // col && row < table.getRowCount() - 2
            String caseNature = table.getValueAt(row, 1).toString();
            //System.out.println("Case Nature is: " + caseNature);
            //System.out.println("Case type: "); 
            //System.out.println("Its Grand Total Column");
            DisplayCaseInfoTable caseinfotable = new DisplayCaseInfoTable();

            DefaultTableModel model1 = (DefaultTableModel) (caseinfotable.tableCaseInfo.getModel());
            int srno = 1;

            boolean dataAdded = false;
            String lblText = "";
            for (int i = 0; i < this.casedata.size(); i++) {

                CaseData data = this.casedata.get(i);
                String caseNatureToCompare;

                if (this.checkBox_mergeSimilarCases.isSelected()) {
                    caseNatureToCompare = data.getRevisedNature();
                } else {
                    caseNatureToCompare = data.getCaseNature();
                }

                if (col == 0) {
                    // do nothing
                } // if column is 1 or 2 then display all data related to that nature
                else if (col <= 2 && caseNature.equalsIgnoreCase(caseNatureToCompare)) {
                    // System.out.println("Total of " + caseNature + " will be shown");
                    dataAdded = addCaseToCaseInfoTable(data, srno++, model1);
                    lblText = "List of all cases of type " + caseNature;

                } // add all data to display table if column name is Grand Total and count is 1 or 2
                else if (col <= 2 && col > 0 && caseNature.equalsIgnoreCase("GRAND TOTAL")) {
                    dataAdded = addCaseToCaseInfoTable(data, srno++, model1);
                    lblText = "List of all cases";
                } // add all case info to the display table of that year
                else if (caseNature.equalsIgnoreCase("GRAND TOTAL") && col > 2
                        && table.getColumnName(col).equals(casedata.get(i).getRevisedCaseYear() + "")) {
                    dataAdded = addCaseToCaseInfoTable(data, srno++, model1);
                    lblText = "List of all cases of the selected year "
                            + table.getColumnName(col);
                } // add only the cases of the particular year and nature to the case table
                else if (caseNature.equalsIgnoreCase(caseNatureToCompare)
                        && this.table.getColumnName(col).equals(casedata.get(i).getRevisedCaseYear() + "")) {
                    dataAdded = addCaseToCaseInfoTable(data, srno++, model1);
                    lblText = "List of all cases of type " + caseNature + " of year " + table.getColumnName(col);
                }

            }
            if (dataAdded) {
                caseinfotable.lbl_heading.setText(lblText);
                caseinfotable.setVisible(true);
            }
            
        }
    }                                  

    // helper method to add data to the case info JTable 
    private boolean addCaseToCaseInfoTable(CaseData data, int srno, DefaultTableModel model) {
        String caseNo = data.getCaseNature() + "/" + data.getCaseNo() + "/" + data.getCaseYear();
        model.addRow(new Object[]{(srno++), caseNo, data.getPartyName(),
            data.getRegistrationYear(), data.getCasePurpose(), data.getRealNature(), data.getCaseActSection(), data.getPoliceStation()});
        return true;
    }


    private void tableMouseEntered(java.awt.event.MouseEvent evt) {                                   
        table.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }                                  

    private void tableMouseExited(java.awt.event.MouseEvent evt) {                                  
        table.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }                                 

    private void combobox_selectFileItemStateChanged(java.awt.event.ItemEvent evt) {                                                     
        DefaultTableModel dtm = (DefaultTableModel) table.getModel();
        dtm.setRowCount(0);
        this.tf_output.setText("");
        if (this.combobox_selectFile.getItemCount() != 0)
            this.setFilePath();
    }                                                    

    private void lbl_homeMouseClicked(java.awt.event.MouseEvent evt) {                                      
        int retVal = JOptionPane.showConfirmDialog(rootPane,
                "Are you sure you want to go home page?",
                "Are you sure?", JOptionPane.YES_NO_OPTION);
        if (JOptionPane.YES_OPTION == retVal) {
            SelectAppForm home = new SelectAppForm();
            home.setVisible(true);
            this.setVisible(false);
        }
    }                                     

    private void menuItem_5oldestActionPerformed(java.awt.event.ActionEvent evt) {                                                 
        if (btnGenBreakupClicked)
            GenerateOldestCases(5);
        else
            JOptionPane.showMessageDialog(rootPane, "Please generate a breakup table first!",
                    "No Table Generated", JOptionPane.ERROR_MESSAGE);
    }                                                

    private void menuItem_10oldestActionPerformed(java.awt.event.ActionEvent evt) {                                                  
        if (btnGenBreakupClicked)
            GenerateOldestCases(10);
        else
            JOptionPane.showMessageDialog(rootPane, "Please generate a breakup table first!",
                    "No Table Generated", JOptionPane.ERROR_MESSAGE);
    }                                                 

    private void menuItem_20oldestActionPerformed(java.awt.event.ActionEvent evt) {                                                  
        if (btnGenBreakupClicked)
            GenerateOldestCases(20);
        else
            JOptionPane.showMessageDialog(rootPane, "Please generate a breakup table first!",
                    "No Table Generated", JOptionPane.ERROR_MESSAGE);
    }                                                 

    private void menuItem_30oldestActionPerformed(java.awt.event.ActionEvent evt) {                                                  
        if (btnGenBreakupClicked)
            GenerateOldestCases(30);
        else
            JOptionPane.showMessageDialog(rootPane, "Please generate a breakup table first!",
                    "No Table Generated", JOptionPane.ERROR_MESSAGE);
    }                                                 

    private void menuItem_saveActionPerformed(java.awt.event.ActionEvent evt) {                                              
        this.btnExport.doClick();
    }                                             

    private void checkBox_mergeSimilarCasesItemStateChanged(java.awt.event.ItemEvent evt) {                                                            

        if (this.btnGenBreakupClicked) {

            clearTable();

            if (this.checkBox_mergeSimilarCases.isSelected()) {
                addTableRows(allCases_merged);
                table.setDefaultEditor(Object.class, null);
                btnGenBreakupClicked = true;
                if (!this.checkBox_hideAgewise.isSelected()) {
                    addAgeWiseBreakup(allCases_merged);}
            } else {
                addTableRows(allCases);
                table.setDefaultEditor(Object.class, null);
                btnGenBreakupClicked = true;
                if (!this.checkBox_hideAgewise.isSelected()) {
                    addAgeWiseBreakup(allCases);
                }
            }
        }
    }                                                           

    private void checkBox_hideAgewiseItemStateChanged(java.awt.event.ItemEvent evt) {                                                      

        if (this.btnGenBreakupClicked) {
            if (this.checkBox_hideAgewise.isSelected()) {
                model.removeRow(model.getRowCount() - 2);
                model.removeRow(model.getRowCount() - 1);
                this.checkBox_ignoreAgeWise.setSelected(true);
            } else {
                addAgeWiseBreakup(allCases);
                this.checkBox_ignoreAgeWise.setSelected(false);
            }
        } else {
            if (this.checkBox_hideAgewise.isSelected()) {

                this.checkBox_ignoreAgeWise.setSelected(true);
            } else {

                this.checkBox_ignoreAgeWise.setSelected(false);
            }
        }

    }                                                     

    private void checkBox_ignoreEmptyColsItemStateChanged(java.awt.event.ItemEvent evt) {                                                          
        // TODO add your handling code here:
    }                                                         

    private void checkBox_hideEmptyColsItemStateChanged(java.awt.event.ItemEvent evt) {                                                        
        // TODO add your handling code here:

        if (this.checkBox_hideEmptyCols.isSelected()) {
            this.checkBox_ignoreEmptyCols.setSelected(true);
        } else {
            this.checkBox_ignoreEmptyCols.setSelected(false);
        }

        if (this.btnGenBreakupClicked) {

            if (this.checkBox_hideEmptyCols.isSelected()) {
                int grandTotalRow = getActualRowCount(model) - 1;

                //System.out.println("Grand Total Row: " + grandTotalRow);
                int cols = model.getColumnCount();
                int currYr = Calendar.getInstance().get(Calendar.YEAR);

                for (int i = 3; i < cols; i++) {
                    if (model.getValueAt(grandTotalRow, i) == null) {
                        TableColumn colToremove = table.getColumn(model.getColumnName(i));
                        table.removeColumn(colToremove);
                    }
                }
            } else {
                clearTable();
                this.addJTableColumns();
                if (this.checkBox_mergeSimilarCases.isSelected()) {
                    this.addTableRows(this.allCases_merged);
                    // check for hide age wise checkbox
                    if(!this.checkBox_hideAgewise.isSelected())
                        this.addAgeWiseBreakup(allCases_merged);
                        
                } else {
                    this.addTableRows(allCases);
                    // check for hide age wise checkbox
                    if(!this.checkBox_hideAgewise.isSelected())
                        this.addAgeWiseBreakup(allCases);
                }
                
            }

        }

    }                                                       

    private void jMenuItem_genFilewiseBreakupActionPerformed(java.awt.event.ActionEvent evt) {                                                             
        // TODO add your handling code here:
        if(!this.buttonBrowseClicked)
        {
            JOptionPane.showMessageDialog(rootPane, "Browse a directory first!",
                    "No folder selected", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        File[] allFile = (new File(browsedFilePath + "/" + this.combobox_folder.getSelectedItem())).listFiles();
        int numFiles = allFile.length;
        for (int i = 0; i < numFiles; i++) {

            File thisFile = allFile[i];
            this.f = thisFile;
            this.btnGenBreakupActionPerformed(null);
            this.btnExportActionPerformed(null);
            clearTable();
        }
    }                                                            

    private void jMenuItem_mergeGenBreakupActionPerformed(java.awt.event.ActionEvent evt) {                                                          
        // TODO add your handling code here:
        if(!this.buttonBrowseClicked)
        {
            JOptionPane.showMessageDialog(rootPane, "Browse a directory first!",
                    "No folder selected", JOptionPane.ERROR_MESSAGE);
            return;
        }
            
        
        File[] allFile = (new File(browsedFilePath + "/" + this.combobox_folder.getSelectedItem())).listFiles();
        int numFiles = allFile.length;

        allCases = new ArrayList<>();
        allCases_merged = new ArrayList<>();
        casedata = new ArrayList<>();

        clearTable();
        Scanner scan;
        String line = "";

        for (int i = 0; i < numFiles; i++) {

            File thisFile = allFile[i];

            try {

                scan = new Scanner(thisFile);
                scan.nextLine();

                while (scan.hasNextLine()) {
                    String nature;
                    int CaseYear;
                    line = scan.nextLine();

                    CaseData data = new CaseData(line);

                    if (checkBox_queryBuilder.isSelected()) {
                        data.extractCaseDataQueryBuilder();
                    } else {
                        data.extractCaseDataDashboard();
                    }

                    casedata.add(data);
                    CaseYear = data.getRevisedCaseYear();

                    nature = data.getCaseNature();
                    addCase(nature, CaseYear, allCases);

                    nature = data.getRevisedNature();
                    addCase(nature, CaseYear, allCases_merged);
                    
                    addCase("GRAND TOTAL", CaseYear, allCases);
                    addCase("GRAND TOTAL", CaseYear, allCases_merged);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, e.getMessage() + "\n\n" + line, "Error reading file",
                        JOptionPane.ERROR_MESSAGE);
            }
            
        }
        this.customSortCaseTypeArrayList(allCases, false);
        this.customSortCaseTypeArrayList(allCases_merged, true);
        this.addJTableColumns();
        addTableRows(this.allCases);
        this.tf_output.setText("OUTPUT -> " + this.combobox_folder.getSelectedItem() + " Merged Breakup");
        table.setDefaultEditor(Object.class, null);
        btnGenBreakupClicked = true;
        PermanentfilterCases(this.permanentFilterValues);

        // check for checkbox conditions
        this.checkBox_hideAgewiseItemStateChanged(null);
        this.checkBox_mergeSimilarCasesItemStateChanged(null);
        this.checkBox_hideEmptyColsItemStateChanged(null);
        exportButtonOutFileName = "mergedFiles";
        sortCaseData();
        this.populateFilterFormData();
    }                                                         

    private void jMenuItem_fileWise_to_singlefileActionPerformed(java.awt.event.ActionEvent evt) {                                                                 
        // TODO add your handling code here:
         if(!this.buttonBrowseClicked)
        {
            JOptionPane.showMessageDialog(rootPane, "Browse a directory first!",
                    "No folder selected", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        File[] allFile = (new File(browsedFilePath + "/" + this.combobox_folder.getSelectedItem())).listFiles();
        int numFiles = allFile.length;
        
        
         JFileChooser chooser = new JFileChooser();
                chooser.setCurrentDirectory(new File(System.getProperty("user.home") + 
                        System.getProperty("file.separator") + "Desktop"));


        
        chooser.setSelectedFile(new File("CourtWise_YearWise_Breakup"));
        setThisFileFilter(chooser);
        int retVal = chooser.showSaveDialog((Component) evt.getSource());
        String filename = "";

        if (retVal == chooser.APPROVE_OPTION) {
            filename = chooser.getSelectedFile().toString();

            if (!filename.substring((filename.length() - 5),
                    (filename.length() - 1)).toLowerCase().equals(".csv")) {
                filename = filename + ".csv";
            }
            try {
                FileWriter writer = new FileWriter(filename);
                writer.write("\""+"Court-wise, Nature-wise, Year-wise breakup of Cases" + "\"");
                writer.write("\n\n");
                for (int i = 0; i < numFiles; i++) {

                    File thisFile = allFile[i];
                    this.f = thisFile;
                    // show input dialog and pre-fill the court name
                    String courtName = JOptionPane.
                            showInputDialog("Please enter the Court Name for the file: " + f.getName(), 
                            (f.getName().substring(0, (f.getName().length() - 4))+" Breakup"));
                    if( !(courtName == null || courtName.isEmpty()) )
                    {
                    this.btnGenBreakupActionPerformed(null);
                    Writer.appendToFile(writer, courtName,  model);
                    clearTable();
                    }                   
                }
                JOptionPane.showMessageDialog(null, "Job completed!");
                writer.close();
            } catch (java.io.IOException e) {
                JOptionPane.showMessageDialog(null, "Some error occured while writing to file!", 
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }                                                                

    private void jMenuItem_byNatureActionPerformed(java.awt.event.ActionEvent evt) {                                                   
        // TODO add your handling code here:
         if(!this.btnGenBreakupClicked)
        {
            JOptionPane.showMessageDialog(null, "Please generate a breakup table first!", 
                    "No Table Generated", JOptionPane.ERROR_MESSAGE);
            return;
        }
      
       filterform.setVisible(true);
 

    }                                                  

    private void populateFilterFormData(){
       String allCaseType[] = new String[this.allCases.size()+1];

       for(int i = 0; i < allCaseType.length-1; i++)
            allCaseType[i] = allCases.get(i).getCaseNature();
       
       this.filterform = new FilterForm(allCaseType, this.permanentFilterValues, this);
    }
    
    private void menuItem_GenListUptoYearActionPerformed(java.awt.event.ActionEvent evt) {                                                         
        // TODO add your handling code here:
        if(!this.btnGenBreakupClicked)
        {
            JOptionPane.showMessageDialog(null, "Please generate a breakup table first!", 
                    "No Table Generated", JOptionPane.ERROR_MESSAGE);
            return;
        }
        String yearStr;
        int year;
        yearStr = JOptionPane.showInputDialog(null, "Enter the year upto which you want to generate the case list", "Enter the year", JOptionPane.OK_CANCEL_OPTION);
        if( !(yearStr==null || yearStr.equals("")) )
        {
            try{
                year = Integer.parseInt(yearStr);
                 DisplayCaseInfoTable infotable = new DisplayCaseInfoTable();
                 DefaultTableModel model = (DefaultTableModel) (infotable.tableCaseInfo.getModel());
               
                int count = 0;
                for(CaseData data : casedata)
                {
                    if(data.getRevisedCaseYear() <= year)
                    {
                      String casenum = data.getCaseNature() + "/" + data.getCaseNo() + "/" + data.getCaseYear();
                       
                            //System.out.println(casenum);
                            count++;
                            model.addRow(new Object[]{count, casenum, data.getPartyName(), 
                                data.getRegistrationYear(),data.getCasePurpose(), 
                                data.getRealNature(), data.getCaseActSection(), data.getPoliceStation()});
                    }   
                }
                if(count > 0)
                {
                    infotable.lbl_heading.setText("List of all cases upto year " + year);
                    infotable.tableCaseInfo.setModel(model);
                    infotable.pack();
                    infotable.setLocationRelativeTo(null);
                    infotable.setVisible(true);
                }
                else
                {
                    JOptionPane.showMessageDialog(null, "No case found whose year is less than or equal to " + year, "No record", JOptionPane.INFORMATION_MESSAGE);
                }
                
            }
            catch(NumberFormatException e)
            {
                JOptionPane.showMessageDialog(null, "Input must be an year in integer form", "Invalid Input", JOptionPane.ERROR_MESSAGE);
            }
        }
        
    }                                                        

    private int getActualRowCount(DefaultTableModel model) {
        int rows = model.getRowCount();
        for (int i = 0; i < rows; i++) {
            if (model.getValueAt(i, 1).equals("GRAND TOTAL")) {
                return (i + 1);
            }
        }
        return 1;
    }

    private void GenerateOldestCases(int numCases) {
        DisplayCaseInfoTable infotable = new DisplayCaseInfoTable();
        DefaultTableModel model = (DefaultTableModel) (infotable.tableCaseInfo.getModel());
        SimpleDateFormat dateformat = new SimpleDateFormat("dd-MM-yyyy");
        
        int stopCond = allCases.size();
        
        if(this.checkBox_mergeSimilarCases.isSelected())
            stopCond = allCases_merged.size();
        
      
        for (int i = 0; i < stopCond; i++) {
            String type = allCases_merged.get(i).getCaseNature();                    

            int count = 1;
            
            model.addRow(new Object[]{type.toUpperCase(), " ", " ", " ", " ", " ", " "});
            for (int y = 0; y < casedata.size(); y++) {
                CaseData data = casedata.get(y);
                String caseType = data.getCaseNature();
                
                if(this.checkBox_mergeSimilarCases.isSelected())
                    caseType = data.getRevisedNature();
                
                if (type.toLowerCase().equals(caseType.toLowerCase())) {
                    if (count <= numCases) {
                        String casenum = data.getCaseNature() + "/" + data.getCaseNo() + "/" + data.getCaseYear();
                        try {
                            //System.out.println(casenum);
                            model.addRow(new Object[]{count++, casenum, data.getPartyName(), 
                                dateformat.format(data.getRegistrationDate()),
                                data.getCasePurpose(), data.getRealNature(), data.getCaseActSection(), 
                                data.getPoliceStation()});
                            
                        } catch (ParseException ex) {
                            JOptionPane.showMessageDialog(null, 
                                    "Some error occured while generating report!",
                                    "Error", JOptionPane.ERROR_MESSAGE);
                        }

                    } else {
                        model.addRow(new Object[]{" ", " ", " ", " ", " ", " "});
                        break;
                    }

                }

            }
            model.addRow(new Object[]{"", "", "", "", "", ""});
        }
        infotable.lbl_heading.setText("Nature-wise " + numCases + " Oldest cases");
        infotable.tableCaseInfo.setModel(model);
        infotable.pack();
        infotable.setLocationRelativeTo(null);
        infotable.setVisible(true);
    }

   
    // Variables declaration - do not modify                     
    private javax.swing.JButton btnBrowse;
    private javax.swing.JButton btnClearAll;
    private javax.swing.JButton btnExport;
    private javax.swing.JButton btnGenBreakup;
    private javax.swing.JCheckBox checkBox_dashboard;
    private javax.swing.JCheckBox checkBox_hideAgewise;
    private javax.swing.JCheckBox checkBox_hideEmptyCols;
    private javax.swing.JCheckBox checkBox_ignoreAgeWise;
    private javax.swing.JCheckBox checkBox_ignoreEmptyCols;
    private javax.swing.JCheckBox checkBox_mergeSimilarCases;
    private javax.swing.JCheckBox checkBox_queryBuilder;
    protected javax.swing.JComboBox<String> combobox_folder;
    protected javax.swing.JComboBox<String> combobox_selectFile;
    private javax.swing.JDesktopPane jDesktopPane1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenuItem jMenuItem_ByYear;
    private javax.swing.JMenuItem jMenuItem_byNature;
    private javax.swing.JMenuItem jMenuItem_fileWise_to_singlefile;
    private javax.swing.JMenuItem jMenuItem_genFilewiseBreakup;
    private javax.swing.JMenuItem jMenuItem_mergeGenBreakup;
    private javax.swing.JMenu jMenu_File;
    private javax.swing.JMenu jMenu_filter;
    private javax.swing.JMenu jMenu_tools;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lbl_home;
    protected java.awt.Label lbl_selctFile;
    protected java.awt.Label lbl_selectFolder;
    private javax.swing.JMenuBar menuBar_mainBar;
    private javax.swing.JMenuItem menuItem_10oldest;
    private javax.swing.JMenuItem menuItem_20oldest;
    private javax.swing.JMenuItem menuItem_30oldest;
    private javax.swing.JMenuItem menuItem_5oldest;
    private javax.swing.JMenuItem menuItem_GenListUptoYear;
    private javax.swing.JMenuItem menuItem_save;
    private javax.swing.JMenu menu_report;
    private javax.swing.JPanel panel_dataSource1;
    private javax.swing.JPanel panel_exportStats;
    private javax.swing.JTable table;
    private javax.swing.JTextField tf_FilePath;
    public javax.swing.JTextField tf_output;
    // End of variables declaration                   
}

