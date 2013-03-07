
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class FrameLogueo extends JPanel {
		 
       private static final long serialVersionUID = 1L;
 
       public JLabel etiquetaNombre;
       public JTextField campoNombre;
       public JButton botonAceptar;   	
       public JFrame frame;
       public static String nombre = "";   	  
   	   
       public FrameLogueo(){
		     		      
	      frame = new JFrame("Introduce tu nombre");
		      
              etiquetaNombre = new JLabel("Nombre: ");
              add(etiquetaNombre);
              
              campoNombre = new JTextField(20);
              add(campoNombre);
              
              botonAceptar = new JButton("Aceptar");
              botonAceptar.addActionListener(new ActionListener() {				
	  	                   @Override
				   public void actionPerformed(ActionEvent e) {
 				          nombre = campoNombre.getText().trim();
				          if( nombre.equals("") || nombre == null ){
				              JOptionPane.showMessageDialog(FrameLogueo.this,"El nombre no puede estar vacio",
 	        		                                            "Nombre vacio",JOptionPane.WARNING_MESSAGE);
				    	  }else{
				    	   	frame.dispose();				    	    	   
				    	        crearMostrarGui(nombre);
				    	  }     
					
				   }
			   }
              );
              
              add(botonAceptar);
              
              frame.add(this);
              frame.setTitle("Introduce tu nombre");              
              frame.setResizable(false);				     
              frame.setLocation(300, 300);	
              frame.pack();
              frame.setVisible(true);
              frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		                     		      
       }
	   
       public static void crearMostrarGui(String nombre){
           		   
              try{ UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
              catch (Exception ex) { ex.printStackTrace(); }                          
           
              JFrame frame = new JFrame("Comparar Registros contra imagenes");                                                                  
                                 
              JTabbedPane tabPane = new JTabbedPane();
              tabPane.addTab("Comparar archivos e imagenes", new InterfazImagenes(nombre));
              tabPane.addTab("Generar reporte", new GenerarReportes(nombre));
           
              frame.add(tabPane);
           
              frame.setVisible(true);
              frame.setResizable(true);
              frame.setSize(650,700);
              frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
              frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);                                        
                                     
       }
	   
       public static void main(String args[]){
           
              SwingUtilities.invokeLater(
                             new Runnable() {
                                 @Override
                                 public void run(){ new FrameLogueo(); }                       
                             }                      
              );             
           
       }   

}
