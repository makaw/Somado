/*
 * 
 *  Somado (System Optymalizacji Małych Dostaw)
 *  Optymalizacja dostaw towarów, dane OSM, problem VRP
 * 
 *  Autor: Maciej Kawecki 2016 (praca inż. EE PW)
 * 
 */
package gui.dialogs.tableforms;

import datamodel.Order;
import datamodel.glossaries.GlossOrders;
import gui.GUI;
import java.awt.Color;
import javax.swing.JPanel;


/**
 *
 * Szablon obiektu okienka dodawania nowego zamówienia
 * 
 * @author Maciej Kawecki
 * @version 1.0
 * 
 */
@SuppressWarnings("serial")
public class OrderEditNewDialog extends OrderEditDialog {
    
   /**
    * Konstruktor
    * @param frame Ref. do GUI
    */
   public OrderEditNewDialog(GUI frame) {
       
     super(frame, "Nowe zam\u00f3wienie", false);  
       
   }    
   

    @Override
    protected boolean saveItem(Order order) {

       GlossOrders glossOrders =  new GlossOrders(frame.getDatabaseShared());                             
       boolean tmp = glossOrders.addItem(order, frame.getUser());
       lastError = glossOrders.getLastError();
       return tmp;       
    
    }
    
    
    
   /**
    * (Niezaimplementowana) Metoda zwraca panel z lista historii zmian
    * @param bgColor Kolor tla
    * @return Panel z lista szkolen
    */    
    @Override
    protected JPanel getAuditPanel(Color bgColor) {   
        
      return null;
        
    }     
    
    
}
