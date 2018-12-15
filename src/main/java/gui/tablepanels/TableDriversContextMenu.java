/*
 * 
 *  Somado (System Optymalizacji Małych Dostaw)
 *  Optymalizacja dostaw towarów, dane OSM, problem VRP
 * 
 *  Autor: Maciej Kawecki 2016 (praca inż. EE PW)
 * 
 */
package gui.tablepanels;


import datamodel.Driver;
import datamodel.glossaries.GlossDrivers;
import datamodel.tablemodels.DriversTableModel;
import gui.GUI;
import gui.TableContextMenu;
import gui.TablePanel;
import gui.dialogs.ConfirmDialog;
import gui.dialogs.tableforms.DriverEditModDialog;
import gui.dialogs.tableforms.DriverEditNewDialog;
import somado.Lang;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JSeparator;
import javax.swing.JTable;



/**
 * Szablon obiektu menu kontekstowego dla wierszy tabeli kierowców
 * 
 * @author Maciej Kawecki
 * @version 1.0
 * 
 */
@SuppressWarnings("serial")
public class TableDriversContextMenu extends TableContextMenu {
  
    
    
  /**
   * Konstruktor
   * @param frame Ref. do GUI 
   */  
  public TableDriversContextMenu(final GUI frame) {

      super(frame);
                
      ContextMenuItem item = new ContextMenuItem(Lang.get("Tables.Drivers.Edit"), "icons/form_edit.png");
      item.addActionListener(new ActionListener() {

          @Override
          public void actionPerformed(ActionEvent e) {
           
             new DriverEditModDialog(frame); 
              
          }
      
      });
      add(item);
      
      item = new ContextMenuItem(Lang.get("Tables.Drivers.Delete"), "icons/form_del.png");
      item.addActionListener(new ActionListener() {

          @Override
          public void actionPerformed(ActionEvent e) {
           
             JTable parentTable = ((TablePanel) frame.getActiveDataPanel()).getTable();
             DriversTableModel dModel = (DriversTableModel) (parentTable.getModel());     
             Driver dTmp = 
                new Driver(dModel.getElement(parentTable.convertRowIndexToModel(parentTable.getSelectedRow()))); 
              
             if ((new ConfirmDialog(frame, Lang.get("Tables.Drivers.Delete.AreYouSure", dTmp.toString()), 170)).isConfirmed()) {
                            
                if (new GlossDrivers(frame.getDatabase()).deleteItem(dTmp, frame.getUser())) {
                    ((TablePanel) frame.getActiveDataPanel()).refreshTable();
                    frame.getDataPanel(GUI.TAB_DRIVERS).setChanged(true);              
                }
   
                 
             }  
              
          }
      
      });
      add(item);      
      
      add(new JSeparator());
     
      item = new ContextMenuItem(Lang.get("Tables.Drivers.Add"), "icons/form_add.png");
      item.addActionListener(new ActionListener() {

          @Override
          public void actionPerformed(ActionEvent e) {
           
            new DriverEditNewDialog(frame); 
              
          }
      
      });
      
      add(item);
      
       
       
   }
  
  
   /**
    * (Niezaimplementowane) Zmiana pozycji gotowego menu przed pokazaniem
    */   
   @Override
   protected void updateMenuItems() {}
  
  
    
}
