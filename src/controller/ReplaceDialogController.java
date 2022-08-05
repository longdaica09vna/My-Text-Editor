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
import view.MTEGui;
import view.ReplaceDialogGUI;

/**
 *
 * @author ADMIN
 */
public class ReplaceDialogController {

    public ReplaceDialogController(MTEGui parentGui) {
        this.parentGui = parentGui;
        initComponents();
    }

    public void showDialog() {
        replaceDialog.setVisible(true);
        replaceDialog.pack();
    }

    private void initComponents() {
        replaceDialog = new ReplaceDialogGUI(parentGui, false);
        textArea = parentGui.getTextArea();
        this.replaceFindPatternField = replaceDialog.getReplaceFindPatternField();
        this.replaceToPatternField = replaceDialog.getReplaceToPatternField();
        this.replaceFindNextBtn = replaceDialog.getReplaceFindNextBtn();
        this.replaceBtn = replaceDialog.getReplaceBtn();
        this.replaceAllBtn = replaceDialog.getReplaceAllBtn();
        this.replaceMatchCaseCheckBox = replaceDialog.getReplaceMatchCaseCheckBox();
        this.replaceWrapAroundCheckBox = replaceDialog.getReplaceWrapAroundCheckBox();
        replaceFindNextBtn.setEnabled(false);
        replaceDialog.getReplaceCancelBtn().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                replaceDialog.setVisible(false);
            }
        });
        replaceFindPatternField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                replaceFindPatternFieldDocumentChangeEvent();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                replaceFindPatternFieldDocumentChangeEvent();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                replaceFindPatternFieldDocumentChangeEvent();
            }
        });
        replaceDialog.getReplaceFindNextBtn().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String searchPattern = replaceFindPatternField.getText();
                boolean isWrapAround = replaceWrapAroundCheckBox.isSelected();
                boolean isMatchCase = replaceMatchCaseCheckBox.isSelected();
                //If cannot find the search pattern with user choices in the text area
                if (!selectSearchPattern(searchPattern, isWrapAround, isMatchCase, true)) {
                    JOptionPane.showMessageDialog(null, "Cannot find pattern \"" + searchPattern + "\"");
                }
            }
        });
        replaceDialog.getReplaceAllBtn().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String searchPattern = replaceFindPatternField.getText();
                String replaceToPattern = replaceToPatternField.getText();
                //Check if user choose to ignore case sensitive
                if (!replaceMatchCaseCheckBox.isSelected()) {
                    searchPattern = searchPattern.toLowerCase();
                    replaceToPattern = replaceToPattern.toLowerCase();
                }
                String content = textArea.getText();
                content = content.replaceAll(searchPattern, replaceToPattern);
                textArea.setText(content);
            }
        });
        replaceDialog.getReplaceBtn().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String searchPattern = replaceFindPatternField.getText();
                String replaceToPattern = replaceToPatternField.getText();
                boolean isWrapAround = replaceWrapAroundCheckBox.isSelected();
                boolean isMatchCase = replaceMatchCaseCheckBox.isSelected();
                //Check if no text in text area had been selected
                if (textArea.getSelectionStart() == textArea.getSelectionEnd()) {
                    //Check if can find the search pattern (with user choice) in the text area
                    if (selectSearchPattern(searchPattern, isWrapAround, isMatchCase, true)) {
                        textArea.replaceSelection(replaceToPattern);
                        selectSearchPattern(searchPattern, isWrapAround, isMatchCase, true);
                    } else {
                        JOptionPane.showMessageDialog(null, "Cannot find pattern \"" + searchPattern + "\"");
                    }
                } else {
                    String selectedContent = textArea.getSelectedText();
                    //Check if user choose to ignore case sensitive
                    if (!isMatchCase) {
                        selectedContent = selectedContent.toLowerCase();
                        searchPattern = searchPattern.toLowerCase();
                    }
                    //Check if selected text is equal the text which need to be replace
                    if (selectedContent.equals(searchPattern)) {
                        textArea.replaceSelection(replaceToPattern);
                        selectSearchPattern(searchPattern, isWrapAround, isMatchCase, true);
                    } else {
                        //Check if can find the search pattern (with user choice) in the text area
                        if (selectSearchPattern(searchPattern, isWrapAround, isMatchCase, true)) {
                            textArea.replaceSelection(replaceToPattern);
                            selectSearchPattern(searchPattern, isWrapAround, isMatchCase, true);
                        } else {
                            JOptionPane.showMessageDialog(null, "Cannot find pattern \"" + searchPattern + "\"");
                        }
                    }
                }
            }
        });
    }

    /**
     * Event when document of replaceFindPatternField changes
     */
    private void replaceFindPatternFieldDocumentChangeEvent() {
        //Check if there was any text in the replaceFindPatternField
        if (replaceFindPatternField.getText().isEmpty()) {
            replaceFindNextBtn.setEnabled(false);
        } else {
            replaceFindNextBtn.setEnabled(true);
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
    private boolean selectSearchPattern(String searchPattern, boolean isWrapAround, boolean isMatchCase, boolean isDownDirection) {
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
            startPos = content.lastIndexOf(searchPattern, pointerPosition - 1);
        } else {
            pointerPosition = textArea.getSelectionEnd();
            startPos = content.indexOf(searchPattern, pointerPosition);
        }
        //If can't find position of search pattern but wrap around option is choosen
        if (isWrapAround && startPos == -1) {
            //Check the find direction is down or up
            if (!isDownDirection) {
                startPos = content.lastIndexOf(searchPattern);
            } else {
                startPos = content.indexOf(searchPattern);
            }
        }
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
    private ReplaceDialogGUI replaceDialog;
    private JTextArea textArea;
    private javax.swing.JButton replaceAllBtn;
    private javax.swing.JButton replaceBtn;
    private javax.swing.JButton replaceCancelBtn;
    private javax.swing.JButton replaceFindNextBtn;
    private javax.swing.JTextField replaceFindPatternField;
    private javax.swing.JCheckBox replaceMatchCaseCheckBox;
    private javax.swing.JTextField replaceToPatternField;
    private javax.swing.JCheckBox replaceWrapAroundCheckBox;
}
