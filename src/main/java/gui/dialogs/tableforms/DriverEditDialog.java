/*
 * 
 *  Somado (System Optymalizacji Małych Dostaw)
 *  Optymalizacja dostaw towarów, dane OSM, problem VRP
 * 
 *  Autor: Maciej Kawecki 2016 (praca inż. EE PW)
 * 
 */
package gui.dialogs.tableforms; 


import datamodel.Driver;
import gui.GUI;
import gui.SimpleDialog;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import datamodel.Vehicle;
import datamodel.glossaries.GlossVehicles;
import datamodel.tablemodels.DriversTableModel;
import gui.formfields.FormRowPad;
import gui.formfields.FormTabbedPane;
import gui.TableFilters;
import gui.TablePanel;
import gui.dialogs.ErrorDialog;
import gui.tablepanels.TableDriversPanel;
import java.awt.FlowLayout;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JTabbedPane;
import somado.IConf;
 

/**
 *
 * Szablon okienka dodawania/modyfikacji kierowcy
 * 
 * @author Maciej Kawecki
 * @version 1.0
 * 
 */
@SuppressWarnings("serial")
public abstract class DriverEditDialog extends SimpleDialog implements IDialogForm {
    
   /** Model tabeli z lista kierowców */ 
   private final DriversTableModel tableModel;  
   /** Obiekt edytowanego/dodawanego kierowcy */
   protected final Driver driver;
   /** Nadrzedny komponent tabeli */
   private final JTable parentTable;
   /** Nadrzedny panel z tabela */
   protected final TablePanel parentTablePanel;
   /** Filtry nadrzednej tabeli */
   protected final TableFilters parentTableFilters;
   /** Indeks zaznaczonego wiersza nadrzednej tabeli */
   private final int selectedTableRow;
   /** True jezeli konieczne jest odswiezenie powiazanej tabeli */
   private boolean needRefresh = false;
   /** Ostatni blad (DB lub logika) */
   protected String lastError = "";

    
   
   /**
    * Konstruktor przy otwieraniu okna z tabeli
    * @param frame Referencja do GUI
    * @param title Tytul okna
    * @param edit True jezeli to edycja 
    */
   public DriverEditDialog(GUI frame, String title, boolean edit) {
        
     super(frame, IConf.APP_NAME + " - " + title);
     
     this.parentTablePanel = (TablePanel) frame.getActiveDataPanel();
     this.parentTable = parentTablePanel.getTable();
     this.parentTableFilters = parentTablePanel.getTableFilters();
     
     tableModel = (parentTable != null) ? (DriversTableModel)(parentTable.getModel()) : null;
     
     selectedTableRow = edit ? parentTable.convertRowIndexToModel(parentTable.getSelectedRow()) : -1;   
     
     this.driver = edit ? new Driver(tableModel.getElement(selectedTableRow)) : new Driver();

      
     super.showDialog(645, 350);
         
   }
   
   

  
   
   @Override
   protected void getContent() {
   
     JPanel p = new JPanel(new GridLayout(1,1));
     p.setOpaque(false);
     p.setBorder(new EmptyBorder(5, 5, 5, 5));
       
     p.add(new FormPanel(this));
     add(p);
   
   }
   
  
  
  
  /**
   * Metoda zapisujaca do BD
   * @param item Dane do zapisania
   * @return true jezeli OK
   */
  protected abstract boolean saveItem(Driver item);
  
  
  /**
   * Metoda odswieza liste po zapisie do BD
   * @param item Zmienione dane
   */
  protected void refreshItemsList(Driver item) {              
      
     // wyczyszczenie filtrow i nowy model 
     parentTableFilters.clearFields();
     DriversTableModel dModel = new DriversTableModel(frame.getDatabase());
     parentTablePanel.getTable().setModel(dModel);
       
     // domyslne sortowanie
     ((TableDriversPanel)parentTablePanel).afterModelChange();
     parentTablePanel.getTable().getRowSorter().toggleSortOrder(dModel.getDefaultSortOrder());
     if (dModel.getDefaultSortOrderAsc())
       parentTablePanel.getTable().getRowSorter().toggleSortOrder(dModel.getDefaultSortOrder()); 
       
     // ustawienie zaznaczenia
     try {
       int rowIndex = parentTablePanel.getTable().getRowSorter().convertRowIndexToView(dModel.getIndex(driver));
       parentTablePanel.getTable().setRowSelectionInterval(rowIndex, rowIndex);
       parentTablePanel.getTable().scrollRectToVisible(parentTablePanel.getTable().getCellRect(rowIndex, 0, true));
     }
     // brak zaznaczenia (-1)
     catch (ArrayIndexOutOfBoundsException e) {}
       
  }
  
 
  
  /**
   * Klasa wewnetrzna - szablon calego formularza
   */
  private class FormPanel extends JPanel {
      
     /** Kolor tla aktywnej zakladki */
     private final Color bgColor = new Color(0xddecfa);  
     

     FormPanel(JDialog dialog) {
         
        super();
       
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        
        final JPanel buttonsPanel = new JPanel();
        
        JTabbedPane tabPane = new FormTabbedPane(bgColor);        
        JPanel tabPane1 = (JPanel)tabPane.add("Dane kierowcy", new JPanel());           
        
        tabPane1.add(new JLabel(" "));
        
        GlossVehicles glossVehicles = new GlossVehicles(frame.getDatabase());
        final JComboBox<Vehicle> vehicleField = new JComboBox<>(glossVehicles.getListModel());
        vehicleField.setSelectedIndex(glossVehicles.getItemsIndex(driver.getVehicle().getId()));
        vehicleField.setFont(GUI.BASE_FONT);
        tabPane1.add(new FormRowPad("Pojazd:", vehicleField));
        
        final JTextField firstnameField = new JTextField(12);
        firstnameField.setText(driver.getFirstname());
        tabPane1.add(new FormRowPad("Imi\u0119", firstnameField));
        
        final JTextField surnameField = new JTextField(12);
        surnameField.setText(driver.getSurname());
        tabPane1.add(new FormRowPad("Nazwisko", surnameField));
        
        final JTextArea commentField = new JTextArea(driver.getComment());
        commentField.setLineWrap(true);
        commentField.setWrapStyleWord(true);
        commentField.setRows(3);
      
        JScrollPane sp = new JScrollPane(commentField);
        sp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        sp.setMaximumSize(new Dimension(600, 60));
        sp.setPreferredSize(new Dimension(600, 60));
    
        tabPane1.add(new FormRowPad("Uwagi:                      ", sp));
        
        final JCheckBox availableField = new JCheckBox("Kierowca dost\u0119pny");
        availableField.setSelected(driver.isAvailable());
        availableField.setOpaque(false);
        availableField.setFocusPainted(false);
        availableField.setFont(GUI.BASE_FONT);
        tabPane1.add(new FormRowPad("Dost\u0119pno\u015b\u0107: ", availableField));
        
        if (driver.getId()!=0) refreshAuditPanel();
       
        add(tabPane);
                        
        
        buttonsPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        buttonsPanel.setOpaque(false);
        buttonsPanel.setBorder(new EmptyBorder(12, 0, 2, 0));

        JButton saveButton = new JButton(" Zapisz ");
        saveButton.setFocusPainted(false);
        
        saveButton.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(final ActionEvent e) {
            
         
           Driver driverUpd = new Driver(driver.getId(), firstnameField.getText(), surnameField.getText(),  
                   (Vehicle)vehicleField.getSelectedItem(), commentField.getText(), availableField.isSelected());      
                   
           if (!saveItem(driverUpd)) {
               
              new ErrorDialog(frame, lastError);  
   
           }
           
           else {
               
             needRefresh = true;  
             refreshItemsList(driverUpd);
             frame.getDataPanel(GUI.TAB_DRIVERS).setChanged(true);
             frame.getDataPanel(GUI.TAB_VEHICLES).setChanged(true);
             dispose();
               
           }
           
          }
        });            
                
        buttonsPanel.add(saveButton);
        buttonsPanel.add(new JLabel(" "));
        buttonsPanel.add(new JLabel(" "));
        buttonsPanel.add(new CloseButton(" Anuluj "));        
        add(buttonsPanel);      
        
         
     }
     
     
     private void refreshAuditPanel()  {}
          
      
  }
      
  
  
  /**
   * Metoda okresla czy konieczne jest odswiezenie tabeli-rodzica
   * @return True jezeli konieczne jest odswiezenie
   */
  @Override
  public boolean isNeedRefresh() {
      
    return needRefresh;  
      
  }
  
 /** 
   * (Niezaimplementowana) Metoda zwraca indeks BD ostatnio dodanego elementu
   * @return Indeks BD ostatnio dodanego elementu
   */
  @Override
  public int getAddedIndex() {
      
     return -1;  
      
  }
  

}
