/*
 * 
 *  Somado (System Optymalizacji Małych Dostaw)
 *  Optymalizacja dostaw towarów, dane OSM, problem VRP
 * 
 *  Autor: Maciej Kawecki 2016 (praca inż. EE PW)
 * 
 */
package gui.dialogs.gloss;


import datamodel.Product;
import datamodel.glossaries.GlossProducts;
import gui.GUI;
import gui.dialogs.GlossDialog;
import somado.Lang;

import java.awt.Color;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 *
 * Szablon obiektu okienka dialogowego ze słownikiem produktów
 * 
 * @author Maciej Kawecki 
 * @version 1.0
 * 
 */
@SuppressWarnings("serial")
public class GlossProductsDialog extends GlossDialog<Product> {
    
   /** Przycisk wyboru do zainicjowania w getButtonsPanel() */
   private JButton chooseButton;
   /** Przycisk edycji do zainicjowania w getButtonsPanel() */
   private JButton editButton;    
   /** Referencja do slownika produktów */
   private GlossProducts glossProducts;
   
    
    
    /**
     * Konstruktor, wywołuje konstruktor nadklasy z odpowiednim tytułem
     * @param frame Referencja do GUI
     */
    public GlossProductsDialog(GUI frame) {
        
        this(frame, Lang.get("Gloss.GlossItems"));
        
    }
    
    /**
     * Konstruktor, wywołuje konstruktor nadklasy
     * @param frame Referencja do GUI
     * @param title Tytuł słownika
     */
    public GlossProductsDialog(GUI frame, String title) {
        
        super(frame, title);
        
    }    
    
    
    /**
     * Konstruktor, wywołuje konstruktor nadklasy
     * @param frame Referencja do GUI
     * @param title Tytuł słownika
     * @param chooseable true jezeli mozna wybrac wartosc ze slownika
     */
    public GlossProductsDialog(GUI frame, String title, boolean chooseable) {
        
        super(frame, title, chooseable);
        
    }        
    
    
    public GlossProductsDialog(GUI frame, int productId) {
        
        super(frame, Lang.get("Gloss.GlossItems"), productId);
        
    }            
    
    
    /**
     * Zwraca model listy słownika 
     * @return Model listy  
     */
    @Override
    public DefaultListModel<Product> getListModel() {

      glossProducts = new GlossProducts(frame.getDatabase());
        
      return glossProducts.getListModel(); 

    }
    
    /**
     * Zwraca model listy słownika po filtrowaniu
     * @param params Mapa parametrów filtrowania
     * @return Model listy firm po filtrowaniu
     */
    @Override
    public DefaultListModel<Product> getListModel(Map<String, String> params) {

      glossProducts = new GlossProducts(frame.getDatabase()); 
        
      return glossProducts.getListModel(params);
        
    }    
    
    
    /**
    * Metoda zwraca referencje do obiektu odpowiedniego slownika
    * @return Ref. do obiektu slownika
    */
    @Override
    public GlossProducts getGlossary() {
        
       return glossProducts;
        
    }
  
    
    /**
     * Metoda zwraca panel z przyciskami dostępnych akcji 
     * @return Panel z przyciskami dostępnych akcji 
     */    
    @Override
    protected JPanel getButtonsPanel() {
        
      JPanel p = new JPanel(new GridLayout(1,4));
      
      chooseButton = new JButton(Lang.get("Choose"));
      chooseButton.setEnabled(false);
      chooseButton.setFocusPainted(false);
            
      chooseButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(final ActionEvent e) {
           
         selectedElement = glossProducts.getItem(getObjList().getSelectedIndex());  
         dispose();
          
       }
     });              
      
      p.add(chooseButton);
      
      editButton = new JButton(Lang.get("Edit"));
      editButton.setEnabled(false);
      editButton.setFocusPainted(false);
      
      editButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(final ActionEvent e) {
           
         new GlossProductEditModDialog(frame, GlossProductsDialog.this, getObjList().getSelectedIndex());
          
       }
     });               
      
      p.add(editButton);
      
      JButton addButton = new JButton(Lang.get("AddNew"));
      addButton.setFocusPainted(false);
      
      addButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(final ActionEvent e) {
           
         new GlossProductEditNewDialog(frame, GlossProductsDialog.this);
          
       }
     });         
      
      p.add(addButton);
      
      p.add(new CloseButton());        
        
      return p;    
        
        
        
    }
    
    

    /**
     * Metoda konstruuje zestaw pól do filtrowania na danym panelu
     * @param p Panel do umieszczenia pól
     */    
    @Override
    protected void setFiltersFormPanel(JPanel p) {
      
      p.add(new JLabel(Lang.get("Gloss.Name") + ":"));
      filters.addField("name", new JTextField(12));
        
    }
    
    
    @Override
    protected JButton getChooseButton() {
        
       return chooseButton;  
        
    }
    
    
   @Override
   protected JButton getEditButton() {
        
       return editButton; 
        
   }
   
   
   @Override
   protected DefaultListCellRenderer getCustomListRenderer() {
       
       
     return new DefaultListCellRenderer() {
       
       @Override
       public Component getListCellRendererComponent(JList<?> list, Object value,
         int index, boolean isSelected, boolean cellHasFocus) {
           
          Component comp = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
          if (value!=null && !((Product)value).isAvailable()) { comp.setForeground(Color.RED);}
          return comp;
          
       }         
                  
     };
       
       
   }
    
    
    
}
