/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.awt.Font;
import java.awt.GraphicsEnvironment;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import view.FontChooser;
import view.MTEGui;

/**
 *
 * @author ADMIN
 */
public class FontChooserController {

    public FontChooserController(MTEGui parentGui) {
        this.parentGui = parentGui;
        initComponents();
    }

    public void showDialog() {
        fontChooser.setVisible(true);
    }

    private void initComponents() {
        fontChooser = new FontChooser(parentGui, true);
        fontChooser.setTitle("Font Chooser");
        fontChooser.setResizable(false);
        fontChooser.setLocationRelativeTo(null);
        okBtn = fontChooser.getOkBtn();
        cancelBtn = fontChooser.getCancelBtn();
        fontFamilyField = fontChooser.getFontFamilyField();
        fontStyleField = fontChooser.getFontStyleField();
        fontSizeField = fontChooser.getFontSizeField();
        fontPreviewField = fontChooser.getFontPreviewField();
        fontFamilyJList = fontChooser.getFontFamilyJList();
        fontStyleJList = fontChooser.getFontStyleJList();
        fontSizeJList = fontChooser.getFontSizeJList();
        initFontFamilyRelatedComponents();
        initFontStyleRelatedComponents();
        initFontSizeRelatedComponents();
        initButton();
        previewFont();
    }

    private void initFontFamilyRelatedComponents() {
        GraphicsEnvironment enviroment = GraphicsEnvironment.getLocalGraphicsEnvironment();
        fontFamilyStrings = enviroment.getAvailableFontFamilyNames();;
        fontFamilyJList.setListData(fontFamilyStrings);
        fontFamilyJList.setSelectedIndex(0);
        fontFamilyField.setText(fontFamilyStrings[0]);
        fontFamilyJList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                fontFamilyField.setText(fontFamilyStrings[(fontFamilyJList.getSelectedIndex())]);
                previewFont();
            }
        });
    }

    private void initFontStyleRelatedComponents() {
        String[] fontStyleArray = {"Plain",
            "<html><b>Bold</b></html>",
            "<html><i>Italic</i></html>",
            "<html><b><i>Bold Italic</i></b></html>"};
        fontStyleJList.setListData(fontStyleArray);
        fontStyleJList.setSelectedIndex(0);
        fontStyleField.setText(FONT_STYLE_STRINGS[0]);
        fontStyleJList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                fontStyleField.setText(FONT_STYLE_STRINGS[fontStyleJList.getSelectedIndex()]);
                previewFont();
            }
        });
    }

    private void initFontSizeRelatedComponents() {
        fontSizeJList.setListData(DEFAULT_FONT_SIZE_STRINGS);
        fontSizeJList.setSelectedIndex(0);
        fontSizeField.setText(fontSizeJList.getSelectedValue());
        fontSizeJList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                fontSizeField.setText(fontSizeJList.getSelectedValue());
                previewFont();
            }
        });
        fontSizeField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                if(!fontSizeFieldKeyEvent()){
                    fontPreviewField.setText("");
                }else{
                    previewFont();
                }
            }

            public void keyReleased(java.awt.event.KeyEvent evt) {
                if(!fontSizeFieldKeyEvent()){
                    fontPreviewField.setText("");
                }else{
                    previewFont();
                }
            }
        });
    }

    private void initButton() {
        okBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fontChooser.setVisible(false);
                if(!fontSizeFieldKeyEvent()){
                    JOptionPane.showMessageDialog(fontChooser, "Invalid input size");
                    fontChooser.setVisible(true);
                    return;
                }else{
                    parentGui.getTextArea().setFont(selectedFont);
                } 
            }
        });
        cancelBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fontChooser.setVisible(false);
            }
        });
    }

    private void previewFont() {
        String fontFamily = fontFamilyField.getText();
        int fontStyle = fontStyleJList.getSelectedIndex();
        int fontSize = Integer.parseInt(fontSizeField.getText());
        setSelectedFont(fontFamily, fontStyle, fontSize);
        fontPreviewField.setFont(getSelectedFont());
    }

    /**
     * Key event for the fontSizeField text field
     */
    private boolean fontSizeFieldKeyEvent() {
        String inputSize = fontSizeField.getText();
        //Check if the fontSizeField text field is empty or not
        if (inputSize.trim().isEmpty()) {
            return false;
        }
        try {
            //Check if inputSize is a valid integer or not
            int size = Integer.parseInt(inputSize);
        } catch (Exception e) {
           return false;
        }
        //Traverse through the default font size array
        for (int i = 0; i < DEFAULT_FONT_SIZE_STRINGS.length; i++) {
            //Check if input size belong to one of the default font size
            if (DEFAULT_FONT_SIZE_STRINGS[i].equals(inputSize)) {
                fontSizeJList.setSelectedIndex(i);
                break;
            }
        }
        return true;
    }

    public Font getSelectedFont() {
        return selectedFont;
    }

    public void setSelectedFont(String fontFamily, int fontStyle, int fontSize) {
        this.selectedFont = new Font(fontFamily, fontStyle, fontSize);
        fontFamilyJList.setSelectedValue(fontFamily, true);
        fontFamilyJList.ensureIndexIsVisible(fontFamilyJList.getSelectedIndex());
        fontStyleJList.setSelectedValue(FONT_STYLE_STRINGS[fontStyle], true);
        fontSizeJList.setSelectedValue(String.valueOf(fontSize), true);
        fontSizeJList.ensureIndexIsVisible(fontSizeJList.getSelectedIndex());
    }

    private MTEGui parentGui;
    private Font selectedFont;
    private String[] fontFamilyStrings;
    private static final String[] FONT_STYLE_STRINGS = {"Plain", "Bold", "Italic", "Bold Italic"};
    private static final String[] DEFAULT_FONT_SIZE_STRINGS = {"8", "9", "10",
        "11", "12", "14", "16", "18", "20", "22", "24", "26", "28", "36",
        "48", "72",};
    private FontChooser fontChooser;
    private JButton cancelBtn;
    private JTextField fontFamilyField;
    private JList<String> fontFamilyJList;
    private JTextField fontPreviewField;
    private JTextField fontSizeField;
    private JList<String> fontSizeJList;
    private JTextField fontStyleField;
    private JList<String> fontStyleJList;
    private javax.swing.JButton okBtn;
}
