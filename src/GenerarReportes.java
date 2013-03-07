
//@author Daniel.Meza
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import com.lowagie.text.Document;
import com.lowagie.text.Font;
import com.lowagie.text.HeaderFooter;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
 
public class GenerarReportes extends JPanel implements ActionListener {
    
       private static final long serialVersionUID = 1L;
   	   
       private JLabel etiquetaAplicacion,etiquetaSubAplicacion,etiquetaAño,etiquetaMes;                           
       private JButton botonGenerarReporte,botonImprimirReporte;        
       private JScrollPane panelTabla;
       private JPanel panelCampos;
       private JTable tabla;                          
       private GridBagConstraints gbc;
       private JComboBox<String> comboTExamen,comboSTExamen,comboAños,comboMes;
       private ArrayList<Object[]> datosReporte;     
       
       
       private final String[] tExamen =        {"AC286","ACREDITA","ACREL","ACRETSU","BULATS","DELEGACIONES","DGEP","DGESPE","ECCYPEC","ECELE","EGEL","EGETSU",
                                                "EGREB","EGREMS","ENAMS","ENLACE","EPROM","EUC","EUCCA","EXANI","EXIL","EXTRA-ES","GESE","IEE-CEF","IFE","ISE",
                                                "MINNESOTA","PPD","TKT","UPN"};
       
       private final String[][] stExamen =     {{"CSF"},{"ACREDITA_BACH","ACREDITA_SEC"},{"ACREL_DII","ACREL_EIN","ACREL_EPRE","ACREL_EPRIM","ACREL_MODA"},
                                                {"ACRETSU_CI","ACRETSU_PFP","ACRETSU_PI"},{""},{"IZTACALCO","TLAHUAC"},{""},{"EGC","EXI"},{""},{""},{""},{""},{""},{""},
                                                {""},{""},{""},{"EUC_ENFER","EUC_EO","EUC_ODON","EUC_PSI","EUC_QUICLI","EUC_TENFER"},{"EUCCA_ACCIDEN","EUCCA_AUD_ACCIDEN",
                                                 "EUCCA_AUD_DANOS","EUCCA_AUD_FIANZAS","EUCCA_AUDIT","EUCCA_AUD_RENTAS","EUCCA_AUD_VIDA","EUCCA_DANOS","EUCCA_FIANZAS",
                                                 "EUCCA_RENTAS","EUCCA_VIDA"},{"E2E","EXANI_I","EXANI_II","EXANI_III","PREEXANI_I","PREEXANI_II"},{""},{"EXTRA_ES_BAS",
                                                 "EXTRA_ES_EXP","EXTRA_ES_MET","EXTRA_ES_MUES"},{""},{""},{""},{""},{""},{""},{""},{"UPN_LE","UPN_LEPEPMI"}};

       private final String[] nombresColumnas = {"Numero de aplicacion","Tipo Aplicacion","Fecha Apicacion","Fecha de Procesamiento","Imagenes","Registrados",
    		                                     "Registrados bpm","Registrados mcontrol","Aplicados","Aplicados bpm","Aplicados mcontrol","Estado",
    		                                     "Institucion"};
       
       
       private final String[] años =  {"2000","2001","2002","2003","2004","2005","2006","2007","2008","2009","2010","2011","2012","2013","2014","2015"}; 
       private final String[] meses = {"Enero","Febrero","Marzo","Abril","Mayo","Junio","Julio","Agosto","Septiembre","Octubre","Noviembre","Diciembre"};
       
       private String name;
              
       @SuppressWarnings("LeakingThisInConstructor")
       public GenerarReportes(String nombre){
             
    	      name = nombre;
    	      
    	      setLayout(new GridBagLayout());
    	      
    	      datosReporte = new ArrayList<>();
    	      
              botonGenerarReporte = new JButton("Generar Reporte");
              botonGenerarReporte.addActionListener(this);
              
              botonImprimirReporte = new JButton("Imprimir Reporte");
              botonImprimirReporte.addActionListener(this);
                            
              panelCampos = new JPanel(new GridBagLayout());              
              panelCampos.setSize(500, 200);
              
              etiquetaAplicacion = new JLabel("Tipo Aplicacion");
              gbc = new GridBagConstraints();
              gbc.gridx = 0;
              gbc.gridy = 0;  
              gbc.weightx = 0.1;
              gbc.weighty = 0.1;
              gbc.insets = new Insets(0,0,0,5);
              panelCampos.add(etiquetaAplicacion,gbc);                                     
              
              comboTExamen = new JComboBox<>(tExamen);
              comboTExamen.addActionListener(
                      new ActionListener() {
                          @Override
                          public void actionPerformed(ActionEvent e) {                                                         
                          
                       	         System.out.println("El tipo de aplicacion es " + comboTExamen.getSelectedItem());
                  	      
                                 panelCampos.remove(comboSTExamen);
                            
                                 int itExamen = comboTExamen.getSelectedIndex();                                           
                            
                                 gbc = new GridBagConstraints();              
                                 gbc.gridx = 3;
                                 gbc.gridy = 0;
                                 gbc.anchor = GridBagConstraints.WEST;                                 
                                                                  
                                 if( itExamen == 0  || itExamen == 4  || itExamen == 7  || itExamen == 8  || itExamen == 9  || 
                                     itExamen == 10 || itExamen == 11 || itExamen == 12 || itExamen == 22 || itExamen == 23 ||
                                     itExamen == 24 || itExamen == 25 || itExamen == 26 || itExamen == 27 || itExamen == 28  ){                                                 
                                     comboSTExamen = new JComboBox<>();
                                     panelCampos.add(comboSTExamen,gbc);
                                     panelCampos.revalidate();
                                     panelCampos.repaint();                                                                                
                                }else{                                                                                                                                                                            
                                      comboSTExamen = new JComboBox<>(stExamen[itExamen]);                                      
                                      panelCampos.add(comboSTExamen,gbc);
                                      panelCampos.revalidate();
                                      panelCampos.repaint();                                                                                  
                               }
                            
                           }
                     
                      }
            
              );
              
              gbc = new GridBagConstraints();
              gbc.gridx = 1;
              gbc.gridy = 0;  
              gbc.weightx = 0.1;
              gbc.weighty = 0.1;
              gbc.insets = new Insets(0,0,0,5);
              panelCampos.add(comboTExamen,gbc);
              
              etiquetaSubAplicacion = new JLabel("SubTipo Aplicacion");
              gbc = new GridBagConstraints();
              gbc.gridx = 2;
              gbc.gridy = 0;  
              gbc.weightx = 0.1;
              gbc.weighty = 0.1;
              gbc.insets = new Insets(0,0,0,5);
              panelCampos.add(etiquetaSubAplicacion,gbc); 
              
              comboSTExamen = new JComboBox<>();
              gbc = new GridBagConstraints();
              gbc.gridx = 3;
              gbc.gridy = 0;  
              gbc.weightx = 0.1;
              gbc.weighty = 0.1;
              gbc.insets = new Insets(0,0,0,5);
              panelCampos.add(comboSTExamen,gbc);
                            
              etiquetaAño = new JLabel("Año");              
              gbc = new GridBagConstraints();
              gbc.gridx = 4;
              gbc.gridy = 0;  
              gbc.weightx = 0.1;
              gbc.weighty = 0.1;
              gbc.insets = new Insets(0,0,0,5);
              panelCampos.add(etiquetaAño,gbc);
              
              comboAños = new JComboBox<>(años);
              gbc = new GridBagConstraints();
              gbc.gridx = 5;
              gbc.gridy = 0;                            
              gbc.weightx = 0.1;
              gbc.weighty = 0.1;
              gbc.insets = new Insets(0,0,0,5);
              panelCampos.add(comboAños,gbc);
              
              etiquetaMes = new JLabel("Mes");
              gbc = new GridBagConstraints();
              gbc.gridx = 6;
              gbc.gridy = 0;                            
              gbc.weightx = 0.1;
              gbc.weighty = 0.1;
              gbc.insets = new Insets(0,0,0,5);
              panelCampos.add(etiquetaMes,gbc);
              
              comboMes = new JComboBox<>(meses);
              gbc = new GridBagConstraints();
              gbc.gridx = 7;
              gbc.gridy = 0;    
              gbc.weightx = 0.1;
              gbc.weighty = 0.1;
              gbc.insets = new Insets(0,0,0,5);
              panelCampos.add(comboMes,gbc);
                                                                
              gbc = new GridBagConstraints();
              gbc.gridx = 8;
              gbc.gridy = 0;    
              gbc.weightx = 0.1;
              gbc.weighty = 0.1;
              gbc.insets = new Insets(0,0,0,5);
              panelCampos.add(botonGenerarReporte,gbc);   
              
              gbc = new GridBagConstraints();
              gbc.gridx = 9;
              gbc.gridy = 0;    
              gbc.weightx = 0.1;
              gbc.weighty = 0.1;
              gbc.insets = new Insets(0,0,0,5);
              panelCampos.add(botonImprimirReporte,gbc);   
              
              tabla = new JTable();
             
              panelTabla = new JScrollPane(tabla);              
              panelTabla.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
              panelTabla.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
              
              gbc = new GridBagConstraints();
              gbc.gridx = 0;
              gbc.gridy = 0;    
              gbc.weightx = 0.1;
              gbc.weighty = 0.1;              
              add(panelCampos,gbc);
              
              gbc = new GridBagConstraints();
              gbc.gridx = 0;
              gbc.gridy = 1;    
              gbc.weightx = 0.1;
              gbc.weighty = 0.1;    
              gbc.gridwidth = 9;
              gbc.insets = new Insets(0, 10, 0, 10);
              gbc.fill = GridBagConstraints.HORIZONTAL;
              gbc.anchor = GridBagConstraints.NORTH;
              add(panelTabla,gbc);                               
                                        
       }                         

       @Override
       public void actionPerformed(ActionEvent ae){                            
              
              final int nombresCantidad = nombresColumnas.length - 1;
                  
              if( ae.getSource() == botonGenerarReporte ){                                    
                	  
                  SwingWorker<Void,Void> sw = new SwingWorker<Void,Void>(){
                		     
                	         Connection c = null;
                             Statement s = null;                  
                             ResultSet rsMysql = null;                                 
                              
                	         @Override
                	         protected Void doInBackground() throws Exception {
                		        	                 	                	        	                                                  
                                       try{
                                    	   
                                           Class.forName("com.mysql.jdbc.Driver");                   
                                           c = DriverManager.getConnection("jdbc:mysql://172.16.37.32:3306/Ceneval","root","ceneval");
                                           s = c.createStatement();
                                           
                                           String subtipo = "";
                                           String select = "";
                                           
                                           System.out.println(select);
                                           
                                           if( comboSTExamen.getSelectedItem() != null ){ 
                                        	   subtipo = " and subtipo = '" + (String)comboSTExamen.getSelectedItem()+ "'";
                                           }else{ subtipo = ""; }
                                           
                                           select = "select no_aplicacion,tipo_aplicacion,subtipo,fecha_registro,fecha_alta,imagenes,pregistro,pregistrobpm," +
                           		                    "pregistromcontrol,prespuesta,prespuestabpm,prespuestamcontrol,estado,institucion from viimagenes" +
                                        	        " where year(fecha_registro) = " + comboAños.getSelectedItem() + " and month(fecha_registro) = " + 
                           		                    (comboMes.getSelectedIndex() + 1) + " and tipo_aplicacion = '" + (String)comboTExamen.getSelectedItem() + 
                           		                    "'" + subtipo + " order by estado";
                                           
                                           System.out.println(select);
                                           
                                           rsMysql = s.executeQuery(select);
                                                                    
                                           DefaultTableModel dtm = new DefaultTableModel();
                                          
                                           TableCellRenderer renderer = new JComponentTableCellRenderer();                                                                                                                                                                                                                  
                                          
                                           for(int l = 0;l <= nombresCantidad;l++){ dtm.addColumn(""); }
                                          
                                               tabla.setModel(dtm);
                                               TableColumnModel columnModel = tabla.getColumnModel();
                                          
                                               for( int k = 0; k <= nombresCantidad; k++ ){
                                                    TableColumn tcTemp = columnModel.getColumn(k);                                                              
                                                    JLabel encabezado = new JLabel(nombresColumnas[k]);
                                                    tcTemp.setHeaderRenderer(renderer);
                                                    tcTemp.setHeaderValue(encabezado);
                                               }
                                                                                             
                                               while(rsMysql.next()){
                                            	     DecimalFormat df = new DecimalFormat();
                                            	     Object[] datos = new Object[]{String.format("%09d",rsMysql.getInt(1)),rsMysql.getString(3),rsMysql.getDate(4),
                     		                                                       rsMysql.getDate(5),rsMysql.getInt(6),rsMysql.getInt(7),rsMysql.getInt(8),
                     		                                                       rsMysql.getInt(9),rsMysql.getInt(10),rsMysql.getString(11),rsMysql.getString(12),
                     		                                                       rsMysql.getString(13),rsMysql.getString(14)};
                             
                                                     datosReporte.add(datos);
                             
                                                	 dtm.addRow(datos);                                                                  
                                               }
                                          
                                               s.close();
                                               c.close();
                                               rsMysql.close();
                                                                                                                                                                                                                                                                                     
                                               GenerarReportes.this.revalidate();
                                               GenerarReportes.this.repaint();                                                            
                                                                                   
                                       }catch(Exception e){ e.printStackTrace(); }
                                       finally{
                                               try{
                                                   rsMysql.close();                                                        
                                                   s.close();
                                                   c.close();
                                               }catch(Exception e){ e.printStackTrace(); }
                                       }                		        	        
                                   
                                       return null;
                                       
                		          }
                		                          		                          		                          		                          		  
                	  };
                	  
                	  sw.execute();                	                      
                      
                  }
              
                  if( ae.getSource() == botonImprimirReporte ){
                	                  	  
                	  /*PrinterJob pj = PrinterJob.getPrinterJob();                                                      
                      PrintRequestAttributeSet pras = new HashPrintRequestAttributeSet();
                      PageFormat pf = pj.pageDialog(pras);
                      pj.setPrintable(GenerarReportes.this, pf);
                      boolean ok = pj.printDialog(pras);
                     
                      if( ok ){
                          try{
                              pj.print(pras);                             
                          }catch(Exception e){ e.printStackTrace(); }
                      }
                      
                	  datosReporte.clear();*/
                	  
                	  try{ GeneraReportePdf(); }
                	  catch(Exception e){ e.printStackTrace(); }
                      
                  }
                                                                                                                     
       }
       
       public void GeneraReportePdf(){    	         	    
    	      
    	      try{
    	    	  
    	    	  Document pdf = new Document();
  		          pdf.setPageSize(PageSize.A4.rotate());
  		          PdfWriter.getInstance(pdf,new FileOutputStream("Test.pdf"));
  		             		           
  		          HeaderFooter encabezado = new HeaderFooter(new Phrase("Direccion de procesos opticos y calificacion.Validacion de posiciones e imagenes de "+
  		                                                                "lectura optica de " + comboTExamen.getSelectedItem() + "/" + 
  		        		                                                comboSTExamen.getSelectedItem() + " de " + comboMes.getSelectedItem() + "-" + 
  		                                                                comboAños.getSelectedItem(), new Font(Font.TIMES_ROMAN,12f,Font.BOLD)), false);
  		          
  		          java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy");
  		          String fCadena = sdf.format(new Date());
  		          HeaderFooter pie = new HeaderFooter(new Phrase("Usuario: " + name + " " + fCadena + "         ",
  		        		                                         new Font(Font.TIMES_ROMAN,10f,Font.BOLD)),true);
  		           
  		          pdf.setHeader(encabezado);
  		          pdf.setFooter(pie);
  		           
  		          pdf.open();
  		          Paragraph parrafo = new Paragraph();
  		            		            		          
  		          Font fuenteDatos = new Font(Font.TIMES_ROMAN,7f);
		          fuenteDatos.setStyle(Font.NORMAL);  		          
		          
		          float[] anchosCelda = {0.06f,0.05f,0.05f,0.05f,0.05f,0.05f,0.05f,0.05f,0.05f,0.05f,0.05f,0.05f,0.39f};
		          PdfPTable tabla = new PdfPTable(anchosCelda);		          
		          tabla.setWidthPercentage(100);
		          
		          String[] encabezados = {"Aplicacion","Tipo","Fecha registro","Fecha alta","Imagenes","Preg","Preg BPM","Preg Mcontrol","Pres",
  		                  "Pres BPM ","Pres Mcontrol","Estado","Institucion"};
    
                  Font fuenteEncabezados = new Font(Font.TIMES_ROMAN,8f);                  
                  fuenteEncabezados.setStyle(Font.BOLD);
                  
                  for( int i = 0; i <= (encabezados.length - 1); i++ ){
                	   Phrase fraseEncabezados = new Phrase();
                       fraseEncabezados.setFont(fuenteEncabezados);
          	    	   fraseEncabezados.add(encabezados[i]);
          	    	   PdfPCell celda = new PdfPCell(fraseEncabezados);
                       celda.setFixedHeight(20);  	    	      	            	    	
          	    	   tabla.addCell(celda);                	   
                  }
		          
  		          for( Object[] ao: datosReporte ){  		        	     		        	   
      	               for( Object dato: ao ){      	            	    
      	            	    Phrase frase = new Phrase();
      	            	    frase.setFont(fuenteDatos);
      	            	    if( dato instanceof String ){      	    
      	            	    	frase.add(dato);
      	            	    	PdfPCell celda = new PdfPCell(frase);
      	                        celda.setFixedHeight(20);  	    	      	            	    	
      	            	    	tabla.addCell(celda);
      	            	    }else{ 	      	  
      	            	    	  frase.add(String.valueOf(dato));
      	            	     	  PdfPCell celda = new PdfPCell(frase);
      	            	     	  celda.setFixedHeight(20);        	            	    
        	            	      tabla.addCell(celda);
        	            	}      	            	  
      	               }      	                     	               
      	               
                  } 
  		          
  		          pdf.add(tabla);
  		             		                       		           
  		          pdf.close();
  		           
    	      }catch(Exception e){ e.printStackTrace(); }    	    	      	             	    	  	       	    	       	              	    	 
    	    
       }              	  
                 
}

class JComponentTableCellRenderer implements TableCellRenderer {
    
      @Override
      public Component getTableCellRendererComponent(JTable table, Object value, 
             boolean isSelected, boolean hasFocus, int row, int column) {
             return (JComponent)value;
      }
      
}
