
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableModel;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

public class Worker extends SwingWorker<Void,Void> {
	
       Map<Object,Object> alCantidadImagenes = new HashMap<>();
       Map<Object,Object> alAplicacionesSinDatif = new HashMap<>();
       Map<Object,Object> alAplicacionesPosicionesDesfazadas = new HashMap<>();
       Map<Object,Object> alAplicacionesSinDats = new HashMap<>();
       ArrayList<Object>  alAplicacionesDatsErraticos = new ArrayList<>();       
       ArrayList<Object>  alAplicacionesNoEmpataMcontrol = new ArrayList<>();     
    
       Map<Object,Object> alPosicionesRegistro = new HashMap<>();
       Map<Object,Object> alPosicionesRespuesta = new HashMap<>();
       Map<Object,Object> alPosicionesRegistroMcontrol = new HashMap<>();
       Map<Object,Object> alPosicionesRespuestaMcontrol = new HashMap<>();
       Map<Object,Object> alPosicionesRegistroBPM = new HashMap<>();
       Map<Object,Object> alPosicionesRespuestaBPM = new HashMap<>();
       Map<Object,Object> alAplicacionYaProcesada = new HashMap<>();
        	     
       Map<Object,Object> alAplicacionesSinImagenes = new HashMap<>();             
       Map<Object,Object> alAplicacionesNoEncontradas = new HashMap<>();
       ArrayList<Object> aplicaciones = new ArrayList<>();
       ArrayList<Object> fechas = new ArrayList<>();
       ArrayList<Object> estados = new ArrayList<>();
       ArrayList<Object[]> alResultados = new ArrayList<>();
    
       private int posicionesExcel = 0,ndr = -1,nds = -1;
       private int numeroPosiciones = 0;       
       private JLabel eProcesando;
       private JTable tResultados;
       private JScrollPane sResultados;
       private JPanel panel;
       private boolean rbRuta,rbMes;       
       private String sRuta;
    
       int s = -1;
       int tamaAño = 0;
       int progress = 0;
    
       double salto = 0.0; 
       double intermedio = 0.0;
    
       String se = "", te = "";
    
       public Worker(ArrayList<Object> al,ArrayList<Object> alf,String cte,String cste,JLabel ep,JTable tr,JPanel pa,JScrollPane sr,ArrayList<Object> estado,
                     boolean rRuta,boolean rMes,String ruta){
    	     	      
    	      aplicaciones = al;
    	      fechas = alf;
    	      se = cste;
    	      te = cte;
    	      eProcesando = ep;
    	      tResultados = tr;
    	      panel = pa;    	          	   
    	      sResultados = sr;
    	      estados = estado;
              rbMes = rMes;
              rbRuta = rRuta;
              sRuta = ruta;
    	      
    	      tamaAño = aplicaciones.size() - 1;    	      
    	      salto = 100 / tamaAño;                               
    	
    	      System.out.println("En worker " + se + " " + cste + " " + te + " " + cte + " rbMes " + rbMes + " rbRuta " + rbRuta + " " + sRuta);
    	      
       }
           
       @Override
       protected Void doInBackground() throws Exception {   	      	        
    	         
   	         int conteoImagenes = 0;                                                        
      
                 setProgress(progress);
       
                 int k = 1;
                 for( Object o: aplicaciones ){
                	                     	  
                      eProcesando.setText("Procesando Imagenes");
                      String numeroAplicacion = (String)o;                                                                                      
                      
                      File dir = null;
               
                      if( rbMes ){
                          if( se == null || se.equals("") ){ dir = new File("\\\\172.16.50.1\\2012\\" + te + "\\" + numeroAplicacion + "\\");                                
                          }else{ 
                                String nse = se.replace('_', '-');
                                dir = new File("\\\\172.16.50.1\\2012\\" + te + "\\" + nse + "\\" + numeroAplicacion + "\\"); 
                          } 
                      }
                      
                      if( rbRuta ){                       
                          dir = new File(sRuta + "\\" + numeroAplicacion + "\\");
                      }
                      
                      boolean existe = dir.exists();                                                                                        
                      
                      if( existe ){
                    	  boolean esDir = dir.isDirectory();                       	  
                          if( esDir ){                                                                        
                              File[] archivos = dir.listFiles();                                                                
                              for(File f : archivos){                                        
                                  String nombreArchivo = f.getName();                                  
                                  if( nombreArchivo.endsWith(".tif") ){ 
                                	  conteoImagenes++;                                	  
                                  }                                                                                                                                                                                        
                              }      
                                                                                          
                              if( conteoImagenes == 0 ){ alAplicacionesSinImagenes.put(o,o); }
                              else{ alCantidadImagenes.put(o,conteoImagenes); }                                                                                                                 
                          }                              
                      }else{                             
                            alAplicacionesNoEncontradas.put(o,o);                                 
                      }                                                                 
                      
                      conteoImagenes = 0;                                                                                                                
                 
                      intermedio = salto * k;                           
                      progress = (int)intermedio;                                                                                     
                      setProgress(progress);                                                                                    
             
                      k++;        
             
                      if( k - 1 >= tamaAño ){ setProgress(100); }                                                                                                                                           
                                                                                        
                 }

                 //Conteo de posiciones en archivos dat
       	         setProgress(0);
   	             
       	         k = 1;
   	         int registro = 0;
                 int respuesta = 0;
              
                 String noAplicacion;                                  
                 
                 for( Object o: aplicaciones ){
           	       
                      if( alAplicacionesNoEncontradas.containsKey(o) ){ continue; }
                     
           	      eProcesando.setText("Procesando Dats");
           	   
                      noAplicacion = (String)o;                                      
                      String folderAplicacion = "\\\\172.16.50.1\\2012\\";
                 
                      if( rbMes ){
                          if( se == null || se.equals("") ){ folderAplicacion += te + "\\" + noAplicacion + "\\"; }
                              else{ 
                    	           String nse = se.replace('_', '-');
                    	           folderAplicacion += te + "\\" + nse + "\\" + o + "\\"; 
                              }
                      }                  
                            
                      if( rbRuta ){                          
                          folderAplicacion = sRuta + "\\" + noAplicacion + "\\";
                      }
                     
                      File datif = new File( folderAplicacion + "\\DATIF" );                                        
                      boolean existeDatif = datif.exists();   

                      Map<String,Map<Object,Object>> valoresMControl = new HashMap<>();
                      Map<Object,ArrayList<Boolean>> mapExistenR = new HashMap<>();
                      Map<Object,ArrayList<Boolean>> mapExistenS = new HashMap<>();
                      ArrayList<Boolean> alExistenR = new ArrayList<>();
                      ArrayList<Boolean> alExistenS = new ArrayList<>();
                      Map<Object,Boolean> valoresIntermediosR = new HashMap<>();
                      Map<Object,Object> valoresIntermediosS = new HashMap<>();
                      Map<Object,Object> alValoresMControlR = new HashMap<>();
                      Map<Object,Object> alValoresMControlS = new HashMap<>();
                      
                      if( existeDatif ){
             
                          String rutaDatif = datif.getAbsolutePath();                     
                          File[] archivos = datif.listFiles(                            		   
                         	 	    new FileFilter() {
                     			        @Override
                                                public boolean accept(File pathname) {                                     
                                                       if( pathname.getName().endsWith(".dat") ){ return true; }                                          
                                                       return false;                                        
                                                }
                   
                                            }
                          );
                         
                          int r = -1;
                          int S = -1;
                          int la = (archivos.length - 1);                          
                          
                          for( int m = 0; m <= la; m++ ){
                              
                               String nombreArchivo = archivos[m].getName();                                                         
                               String subNombreArchivo = "";
                               
                               for( int i = 0; i <= nombreArchivo.length() - 5; i++ ){
                                    subNombreArchivo += nombreArchivo.charAt(i);
                               }
                                                              
                               char ci = nombreArchivo.charAt(0);
                               
                               if( subNombreArchivo.matches("[Rr]\\d{9}[Xx][_\\d]") || subNombreArchivo.matches("[Ss]\\d{9}[Xx][_\\d]") ){ 
                                   
                                   String c = "";
                                   c += ci;
                                   
                                   if( c.matches("[RrSs]") ){                                                       
                                       if( "r".equals(c) || "R".equals(c) ){
                                           alExistenR.add(true);              
                                           r++;                                 
                                       }   
                                       if( "s".equals(c) || "S".equals(c) ){
                                           alExistenS.add(true);
                                           S++;
                                       }
                                   }
                                   
                                   if( la == m ){
                                       System.out.println(o + " " + "   sizeR = " + (alExistenR.size() - 1) + "   sizeS = " +  (alExistenS.size() - 1) );
                                       mapExistenR.put(o, alExistenR);
                                       mapExistenS.put(o, alExistenS);
                                       alExistenR = new ArrayList<>();
                                       alExistenS = new ArrayList<>();
                                   }
                                   
                               }else{ 
                                     s++;
                                     alAplicacionesDatsErraticos.add(o);
                                     continue;
                               }                                                              
                               
                          }
                                                                                       
                          if( la == -1 ){ alAplicacionesSinDatif.put(o,o); }                     
                          else{     
                               int i = 0;
                               while( i <=  r ){                               
                                      String nombreArchivo = archivos[i].getName();                                                                                                       
                                      alValoresMControlR.put(o,leeArchivo(nombreArchivo,rutaDatif,(String)o,i,r,"R"));
                                      i++;
                               }                                                            
                              
                               while( i <= la ){                               
                                      String nombreArchivo = archivos[i].getName();                                                                                                                                                                                    
                                      alValoresMControlS.put(o,leeArchivo(nombreArchivo,rutaDatif,(String)o,i,la,"S"));
                                      i++;
                               }
                              
                          }                                                    
                                                             
                          Set setMER = mapExistenR.keySet();
                          Set setMES = mapExistenS.keySet();                                
                          
                          for( Object key : setMER ){                               
                               boolean tamaño = mapExistenR.get(key).size() - 1 == -1;
                               System.out.println("RKey = " + key + " tamaño = " + tamaño + " size - 1 " + (mapExistenR.get(key).size() - 1) );                               
                               if( tamaño ){
                                   alAplicacionesNoEmpataMcontrol.add(key);
                                   //continue;
                               }                               
//                               for( Boolean existe : mapExistenR.get(key) ){              
//                                    if( alValoresMControlR.get(key) == -1 ){
//                                        System.out.println("Entro en r = " + key);
//                                        alAplicacionesNoEmpataMcontrol.add(key);
//                                    }
//                               }
                          }
                          
                          for( Object key : setMES ){
                               boolean tamaño = mapExistenS.get(key).size() - 1 == -1;
                               System.out.println("SKey = " + key + " tamaño = " + tamaño + " size - 1 " + (mapExistenR.get(key).size() - 1) );                               
                               if( tamaño ){
                                   alAplicacionesNoEmpataMcontrol.add(key);
                                   //continue;
                               }
//                               for( Boolean existe : mapExistenS.get(key) ){
//                                    if( alValoresMControlS.get(key) == -1 ){
//                                        System.out.println("Entro en s = " + key);
//                                        alAplicacionesNoEmpataMcontrol.add(key);
//                                    }
//                               }
                          }
                                                  
                     }else{ alAplicacionesSinDatif.put(o,o); }
                     
                     intermedio = salto * k;                           
                     progress = (int)intermedio;                                                                                     
                     setProgress(progress);                                                                                    
            
                     k++;        
            
                     if( k - 1 >= tamaAño ){
                         setProgress(100);
                     }                                                               	
                     
               }                      
              
               return null;
         
       }
     
       @SuppressWarnings("resource")
       public int leeArchivo(String nombreArchivo,String rutaDatif,Object f,int i,int noArchivos,String tipo){               
        
              String linea = "";                                                 
              int temp;                                                      
              
              try{         
                                                      
                  File f1 = new File(rutaDatif + "\\" + nombreArchivo);                  
                  FileInputStream fis = new FileInputStream(f1);                       
                     
                  while(true){
                   
                        temp = fis.read();
                     
                        if( temp == -1 ){                              
                            ndr++;
                            break;
                        }
                     
                        int digitoSub;
                        linea += (char)temp;                                                                                                          
                     
                        if( temp == '\n' ){ 
                                                        
                            String sub = linea.substring(3,9);                            
                            digitoSub = Integer.parseInt(sub);                               
                         
                            if( digitoSub == 0 ){ numeroPosiciones--; }
                         
                                numeroPosiciones++;                               
                                                         
                               if( digitoSub != numeroPosiciones ){ 
                            	   alAplicacionesPosicionesDesfazadas.put(f,f);
                               }
                                                                                         
                               linea = "";
                         
                           }                                                        
                     
                  }                                            
                     
                  posicionesExcel = mcExcelPosiciones((String)f, "2012",tipo);                                      
                                      
                  if( i == noArchivos ){ 
                   
                      int posiciones = revisaBpmPosiciones((String)f, "2012",tipo);
                                                                                                                                                                           
                      if( tipo.equals("R") ){                                   	 
                          alPosicionesRegistro.put(f,numeroPosiciones);
                          alPosicionesRegistroBPM.put(f,posiciones);
                          alPosicionesRegistroMcontrol.put(f,posicionesExcel);
                      }else{
                            alPosicionesRespuesta.put(f, numeroPosiciones);
                            alPosicionesRespuestaBPM.put(f, posiciones);
                            alPosicionesRespuestaMcontrol.put(f, posicionesExcel);
                      }  
                         
                      numeroPosiciones = 0;
                                                    
                  }                                   
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                          
              }catch(IOException | NumberFormatException e){ e.printStackTrace(); }                              
              
              System.out.println(posicionesExcel);
              return posicionesExcel;
              
       }
    
       public boolean revisaAplicacionProcesada(int noAplicacion,String año){
          
              try{
            
                  Class.forName("com.mysql.jdbc.Driver");                   
                  Connection c = DriverManager.getConnection("jdbc:mysql://172.16.37.32:3306/Ceneval","root","ceneval");
                  Statement s = c.createStatement();
                  ResultSet rs = s.executeQuery("select * from control_imagenes where aplicacion = " + noAplicacion + 
                                                " and YEAR(fecha_registro) = " +  año );
                                                                                                      
                  while( rs.next() ){ return true; }
            
                  rs.close();
                  s.close();
                  c.close();
                              
              }catch(ClassNotFoundException | SQLException e){ e.printStackTrace(); }
        
              return false;
      
       }
 
       public int revisaBpmPosiciones(String aplicacion,String año,String tipo){
     
              Connection c;
              Statement s;
              ResultSet rs;                                                        
     
              int posiciones = 0;
      
              try{
     
                  Class.forName("oracle.jdbc.OracleDriver");                   
                  c = DriverManager.getConnection("jdbc:oracle:thin:@10.10.50.10:1521:ceneval","dpoc","bpm_DPOC");
                        
                  s = c.createStatement();
                  
                  String select = "";
                  
                  if( tipo.equals("R") ){ 
                      select += "select \"Registrado_desglose\",\"Registrado\" from dpoc where NUM_APLIC = '" + 
                                aplicacion + "' and extract(year from \"fecha_de_inicio\") ='" + año + "'";
                  }else{
                	select += "select \"Aplicados_desglose\",\"Aplicados\" from dpoc where NUM_APLIC = '" + aplicacion + "' and " + 
                                  " to_char(\"fecha_de_inicio\",'YYYY') = '" + año + "'";
                  }
                  
                  rs = s.executeQuery(select);
                                     
                  int i = 0;                      
             
                  while( rs.next() ){
                         i++;
                         if( i > 1 ){                             
                             posiciones =  rs.getInt(1);                             
                             break;
                         }else{ posiciones = rs.getInt(2); }                             
                    
                  }
         
         
              }catch(ClassNotFoundException | SQLException e){ e.printStackTrace(); }
     
              return posiciones;
   
       }

       public int revisaBpmAplicados(String aplicacion,String año){
  
              Connection c;
              Statement s;
              ResultSet rs;                            
     
              int aplicados = 0;
     
              try{
     
                  Class.forName("oracle.jdbc.OracleDriver");                   
                  c = DriverManager.getConnection("jdbc:oracle:thin:@10.10.50.10:1521:ceneval","dpoc","bpm_DPOC");
                  s = c.createStatement();                                    
                 
                  rs = s.executeQuery("select \"Aplicados_desglose\",\"Aplicados\" from dpoc where NUM_APLIC = '" + aplicacion + "' and " + 
                                      " to_char(\"fecha_de_inicio\",'YYYY') = '" + año + "'");              
     
                  int i = 0;                      
                  while( rs.next() ){                  
                         i++;
                         if( i > 1 ){                             
                             aplicados = rs.getInt(1);
                             break;
                         }else{ aplicados = rs.getInt(2); }    
                  }
         
              }catch(ClassNotFoundException | SQLException e){ e.printStackTrace(); }
     
              return aplicados;
   
       }             

       public int mcExcelPosiciones(String app,String año,String tipo){ 
   
              int posiciones = 0;                            
        
              try{
            	  
                  Workbook wb = WorkbookFactory.create(new File("C:\\copiamcontrl.xlsx"));
                  Sheet hoja = wb.getSheetAt(0);                  
                  Iterator<Row> rowIt = hoja.rowIterator();
         
                  rowIt.next();
                  for(Iterator<Row> it = rowIt; it.hasNext(); ){
                      Row r = it.next();
                      Cell cAplicacion = r.getCell(0);
                      String cvc = cAplicacion.getStringCellValue().trim();
                                   
                      if( cvc.matches("^[0-9]+$") ){
                          if( Integer.parseInt(cvc) == Integer.parseInt(app) ){
                              Cell cPosiciones;
                              if( tipo.equals("R") ){ cPosiciones = r.getCell(21); }
                              else{ cPosiciones = r.getCell(22);}
                              posiciones += cPosiciones.getNumericCellValue();                     
                          }
                      }
                  }
         
              }catch(IOException | InvalidFormatException | NumberFormatException e){ e.printStackTrace(); }
     
              return posiciones;
     
       }    
  
       @Override
       public void done(){ 
	    
	      tResultados = new JTable(new DefaultTableModel());
              DefaultTableModel dftm = new DefaultTableModel();
              dftm.addColumn("No aplicacion");
              dftm.addColumn("Existe");
              dftm.addColumn("Datif");
              dftm.addColumn("No Imagenes");
              dftm.addColumn("No PRegistro ");
              dftm.addColumn("No PRegistro BPM"); 
              dftm.addColumn("No PRegistro MControl");
              dftm.addColumn("No PRespuesta ");
              dftm.addColumn("No PRespuesta BPM"); 
              dftm.addColumn("No PRespuesta MControl");
              dftm.addColumn("Estado");
                                       
              for( Object o: aplicaciones ){
            	 
       	           ArrayList<Object> ao =  new ArrayList<>();       	                	         
       	           ao.add(o);       	         
       	        	                	               	         
        	   int tamañoNoEncontradas = alAplicacionesNoEncontradas.size() - 1;
       	           int tamañoSinDatif = alAplicacionesSinDatif.size() - 1;
       	           int tamañoSinDats = alAplicacionesSinDats.size() - 1;
       	           int tamañoCantidadImagenes = alCantidadImagenes.size() - 1;
       	           int tamañoPosicionesRegistro = alPosicionesRegistro.size() -1;
       	           int tamañoPosicionesRegistroBPM = alPosicionesRegistroBPM.size() -1;
       	           int tamañoPosicionesRegistroMControl = alPosicionesRegistroMcontrol.size() -1;
       	           int tamañoPosicionesRespuesta = alPosicionesRespuesta.size() - 1;
       	           int tamañoPosicionesRespuestaBPM = alPosicionesRespuesta.size() - 1;
       	           int tamañoPosicionesRespuestaMControl = alPosicionesRespuesta.size() - 1;
                   int tamañoAplicacionesDatsErraticos = alAplicacionesDatsErraticos.size() - 1;
       	                	                	                 
        	   ao.add(agregaDato(alAplicacionesNoEncontradas, tamañoNoEncontradas,o,false));        	 		
           	   ao.add(agregaDato(alAplicacionesSinDatif, tamañoSinDatif,o,false));           	  	       	  
           	   ao.add(agregaDato(alCantidadImagenes, tamañoCantidadImagenes,o,true));           	  
              	   ao.add(agregaDato(alPosicionesRegistro, tamañoPosicionesRegistro,o,true));                              	 
              	   ao.add(agregaDato(alPosicionesRegistroBPM, tamañoPosicionesRegistroBPM,o,true));         	  	                	 
                   ao.add(agregaDato(alPosicionesRegistroMcontrol, tamañoPosicionesRegistroMControl,o,true));                        	 
                   ao.add(agregaDato(alPosicionesRespuesta, tamañoPosicionesRespuesta, o, true));             	  		 
                   ao.add(agregaDato(alPosicionesRespuestaBPM, tamañoPosicionesRespuestaBPM, o, true));                                    	       	                	    	                    	       	                	         
                   ao.add(agregaDato(alPosicionesRespuestaMcontrol, tamañoPosicionesRespuestaMControl, o, true));
                  
                   Set<Object> setAne = alAplicacionesNoEncontradas.keySet();    
                   Set<Object> setAsd = alAplicacionesSinDatif.keySet();                  
                  
                   boolean s1 = false;
                   boolean s2 = false;   
                  
                   if( alPosicionesRegistro.containsKey(o) && alPosicionesRegistroMcontrol.containsKey(o) ){                                             
                       if( (int)alPosicionesRegistro.get(o) != (int)alPosicionesRegistroMcontrol.get(o) ){ 
                           s1 = true; 
              	       }
                      
                   }
                   	       	                    	     
                   if( alPosicionesRespuesta.containsKey(o) && alPosicionesRespuestaMcontrol.containsKey(o) ){
                       if( (int)alPosicionesRespuesta.get(o) != (int)alPosicionesRespuestaMcontrol.get(o) ){
                     	   s2 = true; 
                       }
                   }
                   	       	                    	     
                   System.out.println( s + " " + o + " " + setAne.contains(o) + " " + setAsd.contains(o) + " " + s1 + " " + s2 + " " + 
                                       alAplicacionesDatsErraticos.contains(o) + " " + alAplicacionesNoEmpataMcontrol.contains(o));
                   	       	                    	     
                   if( setAne.contains(o) || setAsd.contains(o) || s1 || s2 || alAplicacionesDatsErraticos.contains(o) || 
                       alAplicacionesNoEmpataMcontrol.contains(o) ){
                       
                       estados.add("Verificar"); 
                       ao.add("Verificar"); 
                       
                   }else{ 
                       
                         estados.add("Correcto");
                         ao.add("Correcto"); 
                         
                   }                     
        	  	 
        	   Object[] aResultados = ao.toArray();
        	   alResultados.add(aResultados);
        	   dftm.addRow(aResultados);        	  
        	     
              }    
                   
              tResultados.setModel(dftm);
 
              sResultados = new JScrollPane(tResultados);     
         
              GridBagConstraints gbc = new GridBagConstraints();         
              gbc.gridx = 0;
              gbc.gridy = 3;
              gbc.anchor = GridBagConstraints.WEST;
              gbc.fill = GridBagConstraints.HORIZONTAL;
              gbc.insets = new Insets(5,5,5,5);
              gbc.gridwidth = 9;
                                      
              panel.add(sResultados,gbc);
              tResultados.revalidate();
              tResultados.repaint();             
             
              eProcesando.setText("Terminado");                  
	   
       }    
  
       public Object agregaDato(Map<Object,Object> alObjetos,int tamaño,Object o,boolean real){	        	            	    
    	  
    	      Set<Object> set = alObjetos.keySet();    	         	        
    	      Iterator<Object> si = set.iterator();
    	     
    	      while( si.hasNext() ){
    	       	     if( si.next().equals(o) ){    	       	    	
    	       	     	 if( real ){ return alObjetos.get(o); }
    	       	         else{ return "No"; }    	       	    	
    	       	     }else{    	       	    	
     	       	           if( real ){ return alObjetos.get(o); }
     	       	           else{ return "Si"; }     	       	    	
    	       	     }
    	      }
    	         	    
    	      return "Si";
	      
       }

}
