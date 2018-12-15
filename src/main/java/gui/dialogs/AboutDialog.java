/*
 * 
 *  Somado (System Optymalizacji Małych Dostaw)
 *  Optymalizacja dostaw towarów, dane OSM, problem VRP
 * 
 *  Autor: Maciej Kawecki 2016 (praca inż. EE PW)
 * 
 */
package gui.dialogs;

import gui.BgPanel;
import gui.SimpleDialog;
import gui.GUI;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.font.TextAttribute;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;

import edu.stanford.ejalbert.BrowserLauncher;
import edu.stanford.ejalbert.exception.BrowserLaunchingInitializingException;
import edu.stanford.ejalbert.exception.UnsupportedOperatingSystemException;
import somado.IConf;
import somado.Lang;


/**
 *
 * Okienko "o programie"
 * 
 * @author Maciej Kawecki
 * @version 1.0
 * 
 */
@SuppressWarnings("serial")
public class AboutDialog extends SimpleDialog {

   
   /**
    * Konstruktor, wywołanie konstruktora klasy nadrzędnej i wyświetlenie okienka
    * @param frame Referencja do GUI
    */ 
   public AboutDialog(GUI frame) {
        
     super(frame);
     
     setTitle(IConf.APP_NAME +" - " + Lang.get("About.About"));
     super.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
     
     super.showDialog(360, 420); 
            
   } 
   
   
    
   /**
    * Metoda wyświetlająca zawartość okienka
    */
   @Override
   protected void getContent()  {   

      JPanel mainPanel = new BgPanel("splash_about.png"); 
      mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
     
      JLabel tmp = new JLabel();
      tmp.setBorder(new EmptyBorder(325, 0, 0, 0));
      //mainPanel.add(tmp);
      
      // pole do umieszczenia tekstu
      JTextPane tx = new JTextPane();
      tx.setEditable(false);
      tx.setOpaque(false);
      tx.setBorder(new EmptyBorder(250, 0, 0, 0));
      tx.setBackground(new Color(0, 0, 0, 0));
      
      StyledDocument doc =  tx.getStyledDocument();  

      Style style = StyleContext.getDefaultStyleContext().getStyle(
                    StyleContext.DEFAULT_STYLE);
      
      SimpleAttributeSet center = new SimpleAttributeSet();
      StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER);
      doc.setParagraphAttributes(0, doc.getLength(), center, false);
      
      StyleConstants.setFontSize(style, 13);
      StyleConstants.setForeground(style, Color.DARK_GRAY);
      StyleConstants.setBackground(style, tx.getBackground());
       
      Style style2 = doc.addStyle("bold", style);
      StyleConstants.setBold(style2, true);
      StyleConstants.setForeground(style2, Color.BLACK);
     
      String sep = System.getProperty("line.separator");
      
      // umieszczenie tekstu
      try {
        doc.insertString(doc.getLength(), IConf.APP_NAME + " " + Lang.get("About.Version") + " " + IConf.APP_VERSION + sep, style2);
        doc.insertString(doc.getLength(), Lang.get("About.Credits.Row1") +  sep, style);
        doc.insertString(doc.getLength(), Lang.get("About.Credits.Row2") + sep, style);
        doc.insertString(doc.getLength(), Lang.get("About.Author") + ": Maciej Kawecki 2016", style);
      }
      catch(BadLocationException e) {
        System.err.println(e.getMessage());
      }     
 
      mainPanel.add(tx);
      
      JLabel lab = new JLabel("https://github.com/makaw/somado");
      lab.setCursor(new Cursor(Cursor.HAND_CURSOR));
      Map<TextAttribute, Integer> fontAttr = new HashMap<>();
      fontAttr.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
      lab.setFont(GUI.BASE_FONT.deriveFont(fontAttr));
      lab.setForeground(new Color(0x330066));
      mainPanel.add(lab);
                
      lab.addMouseListener(new MouseAdapter() {
        @Override
        public void mouseClicked(MouseEvent e) {
          try {
             new BrowserLauncher().openURLinBrowser("https://github.com/makaw/somado");
          }  catch (BrowserLaunchingInitializingException | UnsupportedOperatingSystemException ex) {
             System.err.println(ex);
          }
        }
      });          
      
                   
      JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER));
      p.setBorder(new EmptyBorder(15, 0, 10, 0));
      p.setOpaque(false);
      p.add(new CloseButton(" OK "));
      
      
      mainPanel.add(p);
      
      add(mainPanel);
      
      
   }

   
}

