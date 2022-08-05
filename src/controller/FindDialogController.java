/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import view.FindDialogGUI;
import view.MTEGui;

/**
 *
 * @author ADMIN
 */
public class FindDialogController {

    public FindDialogController(MTEGui parentGui) {
        this.parentGui = parentGui;
        initComponents();
    }

    public void showDialog() {
        findDialog.setVisible(true);
        findDialog.pack();
    }

    private void initComponents() {
        findDialog = new FindDialogGUI(parentGui, false);
        textArea = parentGui.getTextArea();
        this.findButton = findDialog.getFindButton();
        this.findPatternField = findDialog.getFindPatternField();
        this.findDownDirectionBtn = findDialog.getFindDownDirectionBtn();
        this.findMatchCaseCheckBox = findDialog.getFindMatchCaseCheckBox();
        this.findWrapAroundCheckBox = findDialog.getFindWrapAroundCheckBox();
        findButton.setEnabled(false);
        findDialog.getFindCancelButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                findDialog.setVisible(false);
            }
        });
        findPatternField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                findPatternFieldDocumentChangeEvent();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                findPatternFieldDocumentChangeEvent();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                findPatternFieldDocumentChangeEvent();
            }
        });
        findDialog.getFindButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String searchPattern = findPatternField.getText();
                boolean isWrapAround = findWrapAroundCheckBox.isSelected();
                boolean isMatchCase = findMatchCaseCheckBox.isSelected();
                boolean isDownDirection = findDownDirectionBtn.isSelected();
                //If cannot find the search pattern with user choices in the text area
                if (!selectSearchPattern(searchPattern, isMatchCase, isDownDirection)) {
                    JOptionPane.showMessageDialog(null, "Cannot find pattern \"" + searchPattern + "\"");
                }
            }
        });
    }

    /**
     * Event when findPatternField document text be changed
     */
    private void findPatternFieldDocumentChangeEvent() {
        //Check if there was any text in the findPatternField
        if (findPatternField.getText().isEmpty()) {
            findButton.setEnabled(false);
        } else {
            findButton.setEnabled(true);
        }
    }

    /**
     * Find the searchPattern in the text area then select it
     *
     * @param searchPattern - the pattern which need need to be selected
     * @param isWrapAround - the choice to comeback to the other side of
     * document when the pointer is at the end of one side
     * @param isMatchCase - true if not ignore case sensitive, false if ignore
     * @param isDownDirection - true if the search direction is from top to
     * bottom of document, otherwise false
     * @return true - find searchPattern in the textArea, false - cannot find
     * the pattern
     */
    private boolean selectSearchPattern(String searchPattern, boolean isMatchCase, boolean isDownDirection) {
        String content = textArea.getText();
        int startPos, pointerPosition;
        //Check if user choose to find with or without case sensitive
        if (!isMatchCase) {
            content = content.toLowerCase();
            searchPattern = searchPattern.toLowerCase();
        }
        //Check if find direction is down or up
        if (!isDownDirection) {
            pointerPosition = textArea.getSelectionStart();
            System.out.println(""+pointerPosition);
            startPos = content.lastIndexOf(searchPattern, pointerPosition - 1);
        } else {
            pointerPosition = textArea.getSelectionEnd();
            System.out.println(""+pointerPosition);
            startPos = content.indexOf(searchPattern, pointerPosition);
        }
        //If can't find position of search pattern but wrap around option is choosen
//        if (isWrapAround && startPos == -1) {
//            //Check the find direction is down or up
//            if (!isDownDirection) {
//                startPos = content.lastIndexOf(searchPattern);
//            } else {
//                startPos = content.indexOf(searchPattern);
//            }
//        }
        //If find the search pattern
        if (startPos != -1) {
            int endPos = startPos + searchPattern.length();
            textArea.setSelectionStart(startPos);
            textArea.setSelectionEnd(endPos);
            return true;
        } else {
            return false;
        }
    }

    private MTEGui parentGui;
    private FindDialogGUI findDialog;
    private JTextArea textArea;
    private javax.swing.JButton findButton;
    private javax.swing.JButton findCancelButton;
    private javax.swing.JRadioButton findDownDirectionBtn;
    private javax.swing.JCheckBox findMatchCaseCheckBox;
    private javax.swing.JTextField findPatternField;
    private javax.swing.JRadioButton findUpDirectionBtn;
    private javax.swing.JCheckBox findWrapAroundCheckBox;
}
