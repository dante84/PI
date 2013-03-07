
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;
import javax.swing.ButtonGroup;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

//@author Daniel.Meza
 
public class InterfazImagenes extends JPanel implements PropertyChangeListener {
           
       private static final long serialVersionUID = 1L;
       private JLabel eMes,eTExamen,eSTExamen,eSSTExamen,eProcesando,etiquetaSaludo,eExaminar;
       private static JComboBox<String> comboMes,comboTExamen,comboSTExamen,comboSSTExamen,comboMesesRuta;
       private JButton bProcesarAplicaciones,bSalvarDatos,bExaminar;     
       private ArrayList<Object> aplicaciones,fechas,instituciones,estado;       
       private String cte,cste;
       private JProgressBar progressBar;             
       private Worker worker;
       private static JPanel panelCombos,panelRadios,panelRuta,panelProcesar;       
       private JTable tResultados;      
       private JScrollPane sResultados;              
       private JTextField cExaminar;
       private JFileChooser fileChooser;
       private ButtonGroup bg;
       private JRadioButton rbCompleto,rbMes;
       private boolean brbRuta,brbMes;  
        
       private final String[] meses =       {"Enero","Febrero","Marzo","Abril","Mayo","Junio","Julio","Agosto","Septiembre","Octubre","Noviembre","Diciembre"};
       
       private final String[] tExamen =     {"AC286","ACREDITA","ACREL","ACRETSU","BULATS","DELEGACIONES","DGEP","DGESPE","ECCYPEC","ECELE","EGEL","EGETSU",
                                             "EGREB","EGREMS","ENAMS","ENLACE","EPROM","EUC","EUCCA","EXANI","EXIL","EXTRA-ES","GESE","IEE-CEF","IFE","ISE",
                                             "MINNESOTA","PPD","TKT","UPN"};
       
       private final String[][] stExamen =  {{"CSF"},{"ACREDITA_BACH","ACREDITA_SEC"},{"ACREL_DII","ACREL_EIN","ACREL_EPRE","ACREL_EPRIM","ACREL_MODA"},
                                             {"ACRETSU_CI","ACRETSU_PFP","ACRETSU_PI"},{""},{"IZTACALCO","TLAHUAC"},{""},{"EGC","EXI"},{""},{""},{""},{""},{""},{""},
                                             {""},{""},{""},{"EUC_ENFER","EUC_EO","EUC_ODON","EUC_PSI","EUC_QUICLI","EUC_TENFER"},{"EUCCA_ACCIDEN","EUCCA_AUD_ACCIDEN",
                                              "EUCCA_AUD_DANOS","EUCCA_AUD_FIANZAS","EUCCA_AUDIT","EUCCA_AUD_RENTAS","EUCCA_AUD_VIDA","EUCCA_DANOS","EUCCA_FIANZAS",
                                              "EUCCA_RENTAS","EUCCA_VIDA"},{"E2E","EXANI_I","EXANI_II","EXANI_III","PREEXANI_I","PREEXANI_II"},{""},{"EXTRA_ES_BAS",
                                              "EXTRA_ES_EXP","EXTRA_ES_MET","EXTRA_ES_MUES"},{""},{""},{""},{""},{""},{""},{""},{"UPN_LE","UPN_LEPEPMI"}};
       
       private final String[][][] sstExamen = {{{}},{{"cs","ct","eg","m","sf"}},{{}},{{}},{{}},{{}},{{}},{{}},{{}},{{}},{{}},{{}},{{}},{{}},{{}},{{}},{{}},{{}},{{}},
                                               {{}},{{}},{{}},{{}},{{}},{{}},{{}},{{}},{{}},{{}},{{}}};
              
       private final int[] ciaRaiz =               {0,0,0,2,0,0,0,0,0,2,0,12,0,6,10,0,0,0,10,0,0,4,0,0,2,0,0,0,0,0};
       
       private final int[][] ciaSubRaiz =          {{1},{2,0,0,0,0,0},{10,2,2,2,0},{0,0,2},{1,0},{0},{2,2},{},{2,1,2,1},{12},{},{6},{10},{},{},{},{4,0,6,0,0,4},
                                                    {0,0,0,0,0,0,0,0,0,0,0},{0,9,9,10,9,10},{},{0,0,0,0},{},{},{},{},{},{},{},{0,6}};
       
       private final int[][][] ciaCentralSubRaiz = {{{}},{{2,2,3,2,2}},{{}},{{}},{{}},{{}},{{}},{{}},{{}},{{}},{{}},{{}},{{}},{{}},{{}},{{}},{{}},{{}},{{}},
                                                    {{}},{{}},{{}},{{}},{{}},{{}},{{}},{{}},{{}},{{}},{{}}};
                     
       private GridBagConstraints gbc;
       
       public InterfazImagenes(String nombre){    	           	     
    	      
    	      etiquetaSaludo = new JLabel("Hola " + nombre);
    	      
              setLayout(new GridBagLayout());
              
              gbc = new GridBagConstraints();
              gbc.gridx = 0;
              gbc.gridy = 0;
              gbc.insets = new Insets(5,5,5,5);
              //add(etiquetaSaludo,gbc);
              
              rbCompleto = new JRadioButton("Por ruta");
              //rbCompleto.setSelected(true);              
              
              rbCompleto.addActionListener(
                            new ActionListener() {
                                @Override
                                public void actionPerformed(ActionEvent e) {
                                    
                                       comboMes.setEnabled(false);
                                       comboSSTExamen.setEnabled(false);
                                       comboSTExamen.setEnabled(false);
                                       comboTExamen.setEnabled(false);
                                       
                                       bExaminar.setEnabled(true);
                                       cExaminar.setEnabled(true);
                                       
                                }
                                
                            }
                      
              );  
              
              rbMes = new JRadioButton("Por mes");
              rbMes.addActionListener(
                       new ActionListener() {
                           @Override
                           public void actionPerformed(ActionEvent e) {
                                  
                                  cExaminar.setEnabled(false);
                                  bExaminar.setEnabled(false);
                                  
                                  comboMes.setEnabled(true);
                                  comboSSTExamen.setEnabled(true);
                                  comboSTExamen.setEnabled(true);
                                  comboTExamen.setEnabled(true);
                               
                           }
                       }
              ); 
              
              bg = new ButtonGroup();
              bg.add(rbCompleto);
              bg.add(rbMes);
              
              panelRadios = new JPanel(new GridBagLayout());
              
              gbc = new GridBagConstraints();      
              gbc.insets = new Insets(5, 0, 0, 0);
              panelRadios.add(rbCompleto,gbc);
              
              gbc = new GridBagConstraints();              
              gbc.gridy = 2;              
              gbc.insets = new Insets(17, 0, 0, 0);
              gbc.weighty = 0.1;              
              panelRadios.add(rbMes,gbc);
              
              gbc = new GridBagConstraints();
              gbc.gridx = 0;
              gbc.gridy = 0;
              gbc.gridheight = 2;
              gbc.anchor = GridBagConstraints.NORTH;                               
              gbc.fill = GridBagConstraints.VERTICAL;
              gbc.insets = new Insets(5,5,5,5);
              
              add(panelRadios,gbc);
              
              tResultados = new JTable(new DefaultTableModel());
              
              panelCombos = new JPanel(new GridBagLayout());
              
              progressBar = new JProgressBar(0, 100);
              progressBar.setValue(0);
              progressBar.setStringPainted(true);                                                                                                     
              
              eExaminar = new JLabel("Ruta : ");
              cExaminar = new JTextField(40);
              cExaminar.setEditable(false);
              
              bExaminar = new JButton("Examinar");
              bExaminar.addActionListener(
                         new ActionListener() {
                             @Override
                             public void actionPerformed(ActionEvent e) {
                                 
                                    fileChooser = new JFileChooser();
                                    fileChooser.setMultiSelectionEnabled(false);
                                    
                                    fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                                    
                                    int valor = fileChooser.showOpenDialog(null);
                                    
                                    if( valor == JFileChooser.APPROVE_OPTION ){
                                        File f = fileChooser.getSelectedFile();
                                        cExaminar.setText(f.getAbsolutePath());
                                    }
                                    
                             }
                             
                       }
                      
              );
              
              comboMesesRuta = new JComboBox<>(meses);
              
              panelRuta = new JPanel();
                            
              panelRuta.add(eExaminar);
              panelRuta.add(cExaminar);
              panelRuta.add(bExaminar);
              panelRuta.add(comboMesesRuta);
              
              gbc = new GridBagConstraints();
              gbc.gridx = 1;
              gbc.gridy = 0;              
              //gbc.weightx = 0.1;
              gbc.anchor = GridBagConstraints.EAST;
              gbc.insets = new Insets(5,5,5,5);
              
              add(panelRuta,gbc);
              
              eMes = new JLabel("Mes : ");              
              comboMes = new JComboBox<>(meses);
              comboMes.addActionListener(
                      new ActionListener() {
                          @Override
                          public void actionPerformed(ActionEvent e) {  
                                
                                 gbc = new GridBagConstraints();
                                 gbc.gridx = 3;
                                 gbc.gridy = 1;
                                 gbc.anchor = GridBagConstraints.WEST;  
                                 gbc.insets = new Insets(5,0,0,0);
                                 panelCombos.add(comboTExamen,gbc);
                                 panelCombos.revalidate();
                                 panelCombos.repaint();                                    
                                 
                          }
                          
                      }
                   
              );
              
              eTExamen = new JLabel("Examen : ");
              comboTExamen = new JComboBox<>(tExamen);
              comboTExamen.addActionListener(
                           new ActionListener() {
                               @Override
                               public void actionPerformed(ActionEvent e) {                                                                                                                    	     
                       	      
                                      panelCombos.remove(comboSTExamen);
                                 
                                      int itExamen = comboTExamen.getSelectedIndex();                                           
                                 
                                      gbc = new GridBagConstraints();              
                                      gbc.gridx = 5;
                                      gbc.gridy = 1;
                                      gbc.anchor = GridBagConstraints.WEST;
                                      gbc.insets = new Insets(5,5,5,5);
                                                                       
                                      if( itExamen == 0  || itExamen == 4  || itExamen == 7  || itExamen == 8  || itExamen == 9  || 
                                          itExamen == 10 || itExamen == 11 || itExamen == 12 || itExamen == 22 || itExamen == 23 ||
                                          itExamen == 24 || itExamen == 25 || itExamen == 26 || itExamen == 27 || itExamen == 28  ){                                                 
                                          comboSTExamen = new JComboBox<>();
                                          panelCombos.add(comboSTExamen,gbc);
                                          panelCombos.revalidate();
                                          panelCombos.repaint();                                                                                
                                     }else{                                                                                                                                                                            
                                           comboSTExamen = new JComboBox<>(stExamen[itExamen]);                                      
                                           panelCombos.add(comboSTExamen,gbc);
                                           panelCombos.revalidate();
                                           panelCombos.repaint();                                                                                  
                                    }
                                 
                                }
                          
                           }
                 
              );
              
              eSTExamen = new JLabel("Subtipo : ");
              
              comboSTExamen = new JComboBox<>();              
              
              comboSSTExamen = new JComboBox<>();                                                                                                                         
              
              bProcesarAplicaciones = new JButton("Procesar Aplicaciones");
              bProcesarAplicaciones.addActionListener(
                            new ActionListener() {
                                @Override
                                public void actionPerformed(ActionEvent e) {                                                                                                                      	                                   	  
                                	    
                                       brbRuta = rbCompleto.isSelected();
                                       brbMes  = rbMes.isSelected();    
                                       
                                       String cRuta = cExaminar.getText().trim();
                                       
                                       if( brbRuta && cRuta.equals("") ){
                                           JOptionPane.showMessageDialog(
                                                        InterfazImagenes.this,
                                                        "La ruta es invalida, verifica.",
                                                        "Ruta invalida",
                                                        JOptionPane.WARNING_MESSAGE);
                                           return;
                                       }
                                                                                 
                                       aplicaciones = new ArrayList<>();
                                       fechas = new ArrayList<>();
                                       instituciones = new ArrayList<>();
                                       estado = new ArrayList<>();  
                                       
                                       if( brbMes ){
                                           
                                           cte = (String)comboTExamen.getSelectedItem();
                                           cste = (String)comboSTExamen.getSelectedItem();                                                                                
                                       
                                           int cmi = comboMes.getSelectedIndex();
                                                                                                                                                                  
                                           try{
                                           
                                       	       Workbook wb = WorkbookFactory.create(new File("C:\\copiamcontrl.xlsx"));
                                               Sheet hoja = wb.getSheetAt(0);                  
                                               Iterator<Row> rowIt = hoja.rowIterator();                                    
                                               rowIt.next();
                                       
                                               DateFormat df = new SimpleDateFormat("dd-MMM-yy",Locale.ENGLISH);                    
                                       
                                               if(cste == null){                                                                                                                                                                                                                                                                                                                                                                 
                  
                                                  for( Iterator<Row> it = rowIt; it.hasNext(); ){
                      
                                                       Row r = it.next();
                      
                                                       Cell cFechaInicio = r.getCell(1); 
                                                       Cell cTipoAplicacion = r.getCell(11);
                                                       Cell cSTipoAplcacion = r.getCell(13);
                                                       Cell cInstitucion = r.getCell(32);
                                                     
                                                       String scTipoAplicacion = cTipoAplicacion.getStringCellValue().trim();                                                  
                                                       String scSTipoAplicacion = cSTipoAplcacion.getStringCellValue().trim();
                                                       String scInstitucion = cInstitucion.getStringCellValue().trim();
                                                  
                                                       String valor = cFechaInicio.getStringCellValue().trim();                      
                      
                                                       if( valor.length() < 7 ){ continue; }
                      
                                                       Date fechaExcel = df.parse(valor);
                                                       Calendar c = Calendar.getInstance();
                                                       c.setTime(fechaExcel);
                                                       int fem = c.get(Calendar.MONTH);                                                                                                                    
                                                  
                                                       int itExamen = comboTExamen.getSelectedIndex();                                           
                                                                                                                                                        
                                                       if( (fem == cmi) && (cte.equals(scTipoAplicacion)) && (!scSTipoAplicacion.startsWith("[EUC")) && 
                                                           (itExamen == 0  || itExamen == 4  || itExamen == 7  || itExamen == 8  || itExamen == 9  || 
                                                            itExamen == 10 || itExamen == 11 || itExamen == 12 || itExamen == 22 || itExamen == 23 ||
                                                            itExamen == 24 || itExamen == 25 || itExamen == 26 || itExamen == 27 || itExamen == 28) ){
                                                       
                                                            Cell cApp = r.getCell(0);
                                                            Object oapp = cApp.getStringCellValue();                                                     
                                                                                                            
                                                           if( oapp != null ){                                                                                                                   
                                                               aplicaciones.add(oapp);   
                                                               fechas.add(fechaExcel);
                                                               instituciones.add(scInstitucion);
                                                           }
                                                     
                                                       }
                                                   
                                                 }                                                                                            
                                                                                                                                              
                                                 worker = new Worker(InterfazImagenes.this.aplicaciones,InterfazImagenes.this.fechas,InterfazImagenes.this.cte,
                                         	   	             InterfazImagenes.this.cste,InterfazImagenes.this.eProcesando,InterfazImagenes.this.tResultados,
                                            		             InterfazImagenes.this,InterfazImagenes.this.sResultados,InterfazImagenes.this.estado,
                                                                     InterfazImagenes.this.brbRuta,InterfazImagenes.this.brbMes,cRuta);
                                                 worker.addPropertyChangeListener(InterfazImagenes.this);
                                                 worker.execute();                                                
                                                                                    
                                               }else{                                                                                        
                                                                                                                                                                                                    
                                                     for(Iterator<Row> it = rowIt; it.hasNext();){
                          
                                                         Row r = it.next();
                      
                                                         Cell cFechaInicio = r.getCell(1); 
                                                         Cell cTipoAplicacion = r.getCell(11);
                                                         Cell cSTipoAplcacion = r.getCell(13);
                                                         Cell cInstitucion = r.getCell(32);
                                                     
                                                         String scTipoAplicacion = cTipoAplicacion.getStringCellValue().trim();                                                  
                                                         String scSTipoAplicacion = cSTipoAplcacion.getStringCellValue().trim();
                                                         String scInstitucion = cInstitucion.getStringCellValue().trim();
                                                  
                                                         String valor = cFechaInicio.getStringCellValue().trim();                      
                      
                                                         if( valor.length() < 7 ){ continue; }
                      
                                                         Date fechaExcel = df.parse(valor);
                                                         Calendar c = Calendar.getInstance();
                                                         c.setTime(fechaExcel);
                                                         int fem = c.get(Calendar.MONTH);                                                                                                                    
                                                  
                                                         if( (fem == cmi) && (cte.equals(scTipoAplicacion)) && (scSTipoAplicacion.equals(cste)) ){
                                                      
                                                             Cell cApp = r.getCell(0);
                                                             Object oapp = cApp.getStringCellValue();                                                     
                                                     
                                                             if(oapp != null){                                                                  
                                                                aplicaciones.add(oapp);
                                                                fechas.add(fechaExcel);
                                                                instituciones.add(scInstitucion);
                                                             }
                                                     
                                                         }
                                                     
                                                     }                                                                                            
                                                                                           
                                                     worker = new Worker(InterfazImagenes.this.aplicaciones,InterfazImagenes.this.fechas,InterfazImagenes.this.cte,
                                		                         InterfazImagenes.this.cste,InterfazImagenes.this.eProcesando,InterfazImagenes.this.tResultados,
                                		                         InterfazImagenes.this,InterfazImagenes.this.sResultados,InterfazImagenes.this.estado,
                                                                         InterfazImagenes.this.brbRuta,InterfazImagenes.this.brbMes,cRuta);
                                                     worker.addPropertyChangeListener(InterfazImagenes.this);
                                                     worker.execute();                                                                                                                                            
                                                                                                                                           
                                               }                                                                                     
                                           
                                           }catch(Exception e1){ e1.printStackTrace(); }                                                                                                                     																			                                                                                                                            
                                       
                                       }
                                       
                                       if( brbRuta ){
                                                                                    
                                           String subExamen = "";
                                           char[] caracteres = cRuta.toCharArray();
                                           char[] acFinal = new char[20];                                           
                                           int cmi = comboMesesRuta.getSelectedIndex();
                                           
                                           int k = 0;
                                           for( int i = caracteres.length - 1; i > 0 ; i-- ){
                                                if( caracteres[i] == '\\' ){
                                                    break;         
                                                }
                                                
                                                acFinal[k] = caracteres[i];
                                                k++;
                                                
                                           }
                                           
                                           for( int l = acFinal.length - 1; l >= 0 ; l-- ){
                                               
                                                if( acFinal[l] != ' ' ){
                                                    subExamen += acFinal[l];
                                                }
                                               
                                           }
                                           
                                           String subExamenTrimeada = subExamen.trim();
                                           
                                           if( subExamenTrimeada.equals("EGEL") ){
                                                  
                                               try{
                                                   
                                                   Workbook wb = WorkbookFactory.create(new File("C:\\copiamcontrl.xlsx"));
                                                   Sheet hoja = wb.getSheetAt(0);                  
                                                   Iterator<Row> rowIt = hoja.rowIterator();                                    
                                                   rowIt.next();
                                       
                                                   DateFormat df = new SimpleDateFormat("dd-MMM-yy",Locale.ENGLISH);                                                                                                             
                                                                   
                                                   for( Iterator<Row> it = rowIt; it.hasNext(); ){
                      
                                                        Row r = it.next();
                      
                                                        Cell cFechaInicio = r.getCell(1); 
                                                        Cell cTipoAplicacion = r.getCell(11);
                                                        Cell cSTipoAplcacion = r.getCell(13);
                                                        Cell cInstitucion = r.getCell(32);
                                                     
                                                        String scTipoAplicacion = cTipoAplicacion.getStringCellValue().trim();                                                  
                                                        String scInstitucion = cInstitucion.getStringCellValue().trim();
                                                                                                                                                                                                       
                                                        if( scTipoAplicacion.equals(subExamenTrimeada) ){                                                                                                 
                                                            
                                                            String valor = cFechaInicio.getStringCellValue().trim();                      
                      
                                                            if( valor.length() < 7 ){ continue; }
                      
                                                            Date fechaExcel = df.parse(valor);
                                                            Calendar c = Calendar.getInstance();
                                                            c.setTime(fechaExcel);                                                                                                                                                                                                                                                                             
                                                            int fem = c.get(Calendar.MONTH);                                                                                                                                                                                
                                                            
                                                            Cell cApp = r.getCell(0);
                                                            Object oapp = cApp.getStringCellValue();                                                     
                                                                                                            
                                                            if( (fem == cmi) && (oapp != null) ){                                              
                                                                aplicaciones.add(oapp);   
                                                                fechas.add(fechaExcel);
                                                                instituciones.add(scInstitucion);
                                                            }                                                                                                            
                                                   
                                                        }                                                                                            
                                                                                                                                                                                                                                                   
                                                   }
                                                   
                                                   worker = new Worker(InterfazImagenes.this.aplicaciones,InterfazImagenes.this.fechas,InterfazImagenes.this.cte,
                                		                       InterfazImagenes.this.cste,InterfazImagenes.this.eProcesando,InterfazImagenes.this.tResultados,
                                		                       InterfazImagenes.this,InterfazImagenes.this.sResultados,InterfazImagenes.this.estado,
                                                                       InterfazImagenes.this.brbRuta,InterfazImagenes.this.brbMes,cRuta);
                                                   worker.addPropertyChangeListener(InterfazImagenes.this);
                                                   worker.execute(); 
                                           
                                               }catch( Exception e2 ){ e2.printStackTrace(); }
                                           
                                           }else{    
                                               
                                                 String cr = subExamen.replace("-","_").trim();                                             
                                                                                                       
                                                 try{
                                                     
                                                     Workbook wb = WorkbookFactory.create(new File("C:\\copiamcontrl.xlsx"));
                                                     Sheet hoja = wb.getSheetAt(0);                  
                                                     Iterator<Row> rowIt = hoja.rowIterator();                                    
                                                     rowIt.next();
                                        
                                                     DateFormat df = new SimpleDateFormat("dd-MMM-yy",Locale.ENGLISH);                    
                                                                                                              
                                                     for( Iterator<Row> it = rowIt; it.hasNext(); ){
                      
                                                          Row r = it.next();
                      
                                                          Cell cFechaInicio = r.getCell(1); 
                                                          Cell cTipoAplicacion = r.getCell(11);
                                                          Cell cSTipoAplcacion = r.getCell(13);
                                                          Cell cInstitucion = r.getCell(32);
                                                     
                                                          String scTipoAplicacion = cTipoAplicacion.getStringCellValue().trim();                                                  
                                                          String scSTipoAplicacion = cSTipoAplcacion.getStringCellValue().trim();
                                                          String scInstitucion = cInstitucion.getStringCellValue().trim();
                                                     
                                                          String valor = cFechaInicio.getStringCellValue().trim();                      
                      
                                                          if( valor.length() < 7 ){ continue; }
                      
                                                          Date fechaExcel = df.parse(valor);
                                                          Calendar c = Calendar.getInstance();
                                                          c.setTime(fechaExcel);                                                          
                                                          int fem = c.get(Calendar.MONTH);                                                                                                                                                                                
                                                                                                                                                        
                                                          Cell cApp = r.getCell(0);
                                                          Object oapp = cApp.getStringCellValue();                                                                                                                       
                                                          
                                                          if( (fem == cmi) && (oapp != null) && (cr.equals(scSTipoAplicacion)) ){                                                                                                                   
                                                              aplicaciones.add(oapp);   
                                                              fechas.add(fechaExcel);
                                                              instituciones.add(scInstitucion);
                                                          }                                                                                                            
                                                   
                                                     }          
                                                     
                                                     worker = new Worker(InterfazImagenes.this.aplicaciones,InterfazImagenes.this.fechas,InterfazImagenes.this.cte,
                                		                         InterfazImagenes.this.cste,InterfazImagenes.this.eProcesando,InterfazImagenes.this.tResultados,
                                  		                         InterfazImagenes.this,InterfazImagenes.this.sResultados,InterfazImagenes.this.estado,
                                                                         InterfazImagenes.this.brbRuta,InterfazImagenes.this.brbMes,cRuta);
                                                     worker.addPropertyChangeListener(InterfazImagenes.this);
                                                     worker.execute(); 
                                           
                                               }catch( Exception e2 ){ e2.printStackTrace(); }
                                           }
                                       
                                       }
                                
                                }
                                
                         }
                      
              );
                                                        
              bSalvarDatos = new JButton("Guardar Registros");
              bSalvarDatos.addActionListener(
            		      new ActionListener() {				
   				  @Override
				  public void actionPerformed(ActionEvent e) {
   				            	  
   				         if( tResultados != null ){
   				            	    	  
   				             try{
   				            	            
      				                 Class.forName("com.mysql.jdbc.Driver");                   
      				                 Connection c = DriverManager.getConnection("jdbc:mysql://172.16.37.32:3306/Ceneval","root","ceneval");
      				            	                   
      				                 int k = 0;
      				                 for( Object[] ao : worker.alResultados ){
      				            	              
      				                      if( k > (instituciones.size() - 1) || k > (estado.size() - 1) ){ break; }
      				            	        	  
      				            	       	  String insert = "";
      				            	        	  
      				            	      	  Object[] aos = new Object[ao.length - 1];
      				            	        	  
      				            	       	  for( int i = 0; i < (ao.length - 1); i++ ){
      				            	               if( ao[i] != null ){ aos[i] = ao[i]; }
      				            	               else{ aos[i] = "0"; } 
      				            	          }
      				            	        	  
      				            	          DateFormat df = new SimpleDateFormat("dd/MM/yyyy");      				            	        	  
      				            	          Date fecha_alta = new Date();
      				            	          String faf = df.format(fecha_alta);
      				            	       	  Date fecha_registro = (Date)fechas.get(k);
      				            	       	  String frf = df.format(fecha_registro);
      				            	        	        				            	        	        				            	        	  
      				            	       	  insert += "INSERT INTO viimagenes(no_aplicacion,tipo_aplicacion,subtipo,fecha_alta,fecha_registro,imagenes,pregistro" +   
      				            	     	            ",pregistrobpm,pregistromcontrol,prespuesta,prespuestabpm,prespuestamcontrol,institucion,estado) VALUES(" + 
      				            	         	    aos[0] + ",'" + (String)comboTExamen.getSelectedItem() + "','" + (String)comboSTExamen.getSelectedItem() +      				            	         			    
      				            	         	    "',str_to_date('" + faf + "','%d/%m/%Y'),str_to_date('" + frf + "','%d/%m/%Y')," + aos[3] + "," + 
      				            	         	    aos[4] + "," + aos[5] + "," +  aos[6] + "," + aos[7] + "," + aos[8] + "," + aos[9] + ",'" +
      				            	                    instituciones.get(k) + "','" + estado.get(k) + "')";      				            	         	        				            	         	        				            	         	        				         
      				            	         	  
      				            	          Statement s = c.createStatement();
      				            	          s.executeUpdate(insert);
       				            	             
            				            	  s.close();
            				            	      
      				            	          k++;
            				            	      
      				            	         }
      				            	                     				            	         
      				            	         c.close();
      				            	         
      				            	         JOptionPane.showMessageDialog(InterfazImagenes.this,"Datos actualizados correctamente",
      				            	        		                       "Exito",JOptionPane.PLAIN_MESSAGE);
      				            	                              
      				            	      }catch(Exception e1){ e1.printStackTrace(); }	  
   				            	    	     				            	    	  
   				            	    	 
   				            	     }else{
   				            	    	   JOptionPane.showMessageDialog(InterfazImagenes.this,"Necesitas hacer una consulta primero",
   				            	    			                         "Advertencia",JOptionPane.OK_OPTION); 
   				            	     }   				            	        				            	     
   				            	        
   				               }					
  				                 				              
			             }
            		      
              );
              
              eSSTExamen = new JLabel("Sub-Subtipo : ");
              
              eProcesando = new JLabel();
              
              gbc = new GridBagConstraints();
              gbc.gridx = 0;
              gbc.gridy = 1;
              gbc.anchor = GridBagConstraints.WEST;
              gbc.insets = new Insets(5,5,5,5);
              panelCombos.add(eMes,gbc);
              
              gbc = new GridBagConstraints();
              gbc.gridx = 1;
              gbc.gridy = 1;
              gbc.anchor = GridBagConstraints.WEST;
              gbc.insets = new Insets(5,5,5,5);
              panelCombos.add(comboMes,gbc);
              
              gbc = new GridBagConstraints();
              gbc.gridx = 2;
              gbc.gridy = 1;
              gbc.anchor = GridBagConstraints.WEST;
              gbc.insets = new Insets(5,5,5,5);
              panelCombos.add(eTExamen,gbc);
              
              gbc = new GridBagConstraints();
              gbc.gridx = 3;
              gbc.gridy = 1;
              gbc.anchor = GridBagConstraints.WEST;
              gbc.insets = new Insets(5,5,5,5);
              panelCombos.add(comboTExamen,gbc);
              
              gbc = new GridBagConstraints();
              gbc.gridx = 4;
              gbc.gridy = 1;
              gbc.anchor = GridBagConstraints.WEST;
              gbc.insets = new Insets(5,5,5,5);
              panelCombos.add(eSTExamen,gbc);
              
              gbc = new GridBagConstraints();
              gbc.gridx = 5;
              gbc.gridy = 1;
              gbc.anchor = GridBagConstraints.WEST;
              gbc.insets = new Insets(5,5,5,5);
              panelCombos.add(comboSTExamen,gbc);
              
              gbc = new GridBagConstraints();
              gbc.gridx = 6;
              gbc.gridy = 1;
              gbc.anchor = GridBagConstraints.WEST;
              gbc.insets = new Insets(5,5,5,5);
              panelCombos.add(eSSTExamen,gbc);
              
              gbc = new GridBagConstraints();
              gbc.gridx = 7;
              gbc.gridy = 1;
              gbc.anchor = GridBagConstraints.WEST;
              gbc.insets = new Insets(5,5,5,5);
              panelCombos.add(comboSSTExamen,gbc);                                                                         	                                               
              
              gbc = new GridBagConstraints();
              gbc.gridx = 1;
              gbc.gridy = 1;
              gbc.anchor = GridBagConstraints.EAST;                 
              gbc.gridwidth = 4;              
              gbc.insets = new Insets(5,5,5,5);
              add(panelCombos,gbc);                                	          	     
              
              panelProcesar = new JPanel(new GridBagLayout());              
              
              gbc = new GridBagConstraints();
              gbc.gridx = 0;
              gbc.gridy = 0;
              gbc.anchor = GridBagConstraints.WEST;
              gbc.insets = new Insets(0,0,0,5);
              panelProcesar.add(bProcesarAplicaciones,gbc);                        
              
              gbc = new GridBagConstraints();
              gbc.gridx = 1;
              gbc.gridy = 0;                                         
              gbc.insets = new Insets(0,0,0,5);
              panelProcesar.add(bSalvarDatos,gbc);
              
              gbc = new GridBagConstraints();
              gbc.gridx = 0;
              gbc.gridy = 1;              
              gbc.insets = new Insets(20,0,0,0);
              panelProcesar.add(progressBar,gbc);
              
              gbc = new GridBagConstraints();
              gbc.gridx = 1;
              gbc.gridy = 1;
              gbc.anchor = GridBagConstraints.WEST;
              gbc.insets = new Insets(20,5,0,0);
              panelProcesar.add(eProcesando,gbc);                                                              
              
              gbc = new GridBagConstraints();
              gbc.gridx = 7;
              gbc.gridy = 0;
              gbc.anchor = GridBagConstraints.EAST;                 
              gbc.gridwidth = 2;              
              gbc.gridheight = 2;              
              gbc.insets = new Insets(5,5,5,5);                            
              add(panelProcesar,gbc);
                                                                   
       }                          
                  
       public void insertaDatos(String datPath,String imagePath,String user,int application,int datRegCan,int imageCan){
           
              Connection c  = null;
              Statement s = null;            
                          
              String datPathTwoSlash = "";
              String imagePathTwoSlash = "";
              
              for( int i = 0; i <= datPath.length() - 1; i++ ){
                   if( datPath.charAt(i) == '\\' ){
                       datPathTwoSlash += "\\\\";
                       continue;
                   }
                   datPathTwoSlash += datPath.charAt(i);
              }
              
              for( int i = 0; i <= imagePath.length() - 1; i++ ){
                   if( imagePath.charAt(i) == '\\' ){
                       imagePathTwoSlash += "\\\\";
                       continue;
                   }
                   imagePathTwoSlash += imagePath.charAt(i);
              }                                          
              
              try{
                  
                  Class.forName("com.mysql.jdbc.Driver");                   
                  c = DriverManager.getConnection("jdbc:mysql://172.16.37.32:3306/Ceneval","root","slipknot");
                  
                  s = c.createStatement();
                  
                  String insert = "";
                		  
                		  //"insert into reporte_validacion_imagenes(ruta_dat,ruta_imagenes,usuario," + 
                                  //"tipo_aplicacion,cantidad_registros_dat,cantidad_imagenes,hora_fecha) "   +
                                  //"values('" + datPathTwoSlash + "','" + imagePathTwoSlash + "','" + user + 
                                  //"','" + selectedApplication + "','" + datRegCan + "','" + imageCan + "','" + 
                                  //fechaHora + "');";
                  
                   s.executeUpdate(insert);
                                   
              }catch(Exception e){ e.printStackTrace(); }
              
              finally{
                      try{
                          s.close();
                          c.close();
                      }catch(Exception e){ e.printStackTrace(); }
              }
            
       }              
       
       @Override
       public void propertyChange(PropertyChangeEvent evt) {
              if( "progress".equals(evt.getPropertyName()) ){
                  int progreso = (Integer)evt.getNewValue();
                  progressBar.setValue(progreso);
              }
       }                         
    
}
