/*
 * 
 *  Somado (System Optymalizacji Małych Dostaw)
 *  Optymalizacja dostaw towarów, dane OSM, problem VRP
 * 
 *  Autor: Maciej Kawecki 2016 (praca inż. EE PW)
 * 
 */
package gui.dialogs.gloss;


import datamodel.Customer;
import gui.GUI;
import gui.TablePanel;
import gui.dialogs.GlossDialog;
import somado.Lang;


/**
 *
 * Szablon okienka do modyfikacji elementow slownika
 * 
 * @author Maciej Kawecki
 * @version 1.0
 * 
 */
@SuppressWarnings("serial")
public class GlossCustomerEditModDialog extends GlossCustomerEditDialog {
  
  /** Zmienione dane */
  private Customer updatedItem;  
    
  
  /**
   * Konstruktor: istniejacy element slownika
   * @param frame Referencja do GUI
   * @param parentDialog Obiekt nadrzednego okienka
   * @param cIndex Indeks slownikowy elementu
   */  
  public GlossCustomerEditModDialog(GUI frame, GlossDialog<Customer> parentDialog, int cIndex) {
        
    super(frame, parentDialog, Lang.get("Gloss.GlossReceivers") + " - " + Lang.get("Gloss.EditReceiver"), cIndex);
         
  }
  
  
  public GlossCustomerEditModDialog(GUI frame) {
      
    super(frame);          
      
  }
  
  
  /**
   * Metoda zapisujaca do BD
   * @param customer Dane do zapisania
   * @return true jezeli OK
   */  
  @Override
  protected boolean saveItem(Customer customer) {
      
     updatedItem = customer;
     boolean saved = glossCustomers.updateItem(customer, frame.getUser());
     
     if (saved) 
       try {
        ((TablePanel)(frame.getDataPanel(GUI.TAB_ORDERS))).refreshTable();
       }
       catch (ClassCastException e) {}
     
     return saved;
     
  }
  
  
  /**
   * Metoda odswieza liste po zapisie do BD
   */  
  @Override
  protected void refreshItemsList() {
      
    if (getParentDialog() != null) 
      getParentDialog().getFilters().doUpdate(updatedItem.getId());
      
  }
  
    
}
