/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.awt.Font;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.DefaultEditorKit;
import javax.swing.undo.UndoManager;
import view.MTEGui;

/**
 *
 * @author ADMIN
 */
public class MTEGuiController {

    public MTEGuiController() {
        initComponents();
    }

    private void initComponents() {
        userInterface = new MTEGui();
        userInterface.setVisible(true);
        userInterface.setLocationRelativeTo(null);
        userInterface.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        textArea = userInterface.getTextArea();
        textArea.setWrapStyleWord(true);
        textArea.setLineWrap(true);
        undoManager = new UndoManager();
        initThread();
        initFileMenuRelatedComponents();
        initEditMenuRelatedComponents();
        createNewDocument();
        generateTextAreaEvent();
        userInterface.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                windowClosingEvent();
            }
        });

    }

    private void initFileMenuRelatedComponents() {
        fileChooser = new JFileChooser();
        fileChooser.setAcceptAllFileFilterUsed(false);
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Text Document", "txt");
        fileChooser.setFileFilter(filter);
        this.findMenuItem = userInterface.getFindMenuItem();
        this.replaceMenuItem = userInterface.getReplaceMenuItem();
        findMenuItem.setEnabled(false);
        replaceMenuItem.setEnabled(false);
        userInterface.getNewDocumentMenuItem().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                newDocumentMenuItemEvent();
            }
        });
        userInterface.getOpenDocumentMenuItem().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openDocumentMenuItemEvent();
            }
        });
        userInterface.getSaveDocumentMenuItem().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //Check if the document is saved or not
                if (isSaved) {
                    return;
                }
                //Check if the document is the default document or not
                if (isDefaultDocument) {
                    saveAsNewDocument();
                } else {
                    saveCurrentDocument();
                }
            }
        });
        userInterface.getSaveDocumentAsMenuItem().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveAsNewDocument();
            }
        });
        userInterface.getExitMenuItem().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                windowClosingEvent();
            }
        });
    }

    private void initEditMenuRelatedComponents() {
        findDialogController = new FindDialogController(userInterface);
        replaceDialogController = new ReplaceDialogController(userInterface);
        fontChooserController = new FontChooserController(userInterface);
        this.selectAllMenuItem = userInterface.getSelectAllMenuItem();
        this.copyMenuItem = userInterface.getCopyMenuItem();
        this.cutMenuItem = userInterface.getCutMenuItem();
        this.findMenuItem = userInterface.getFindMenuItem();
        this.replaceMenuItem = userInterface.getReplaceMenuItem();
        this.undoMenuItem = userInterface.getUndoMenuItem();
        this.redoMenuItem = userInterface.getRedoMenuItem();
        initUndoRedoAction();
        cutMenuItem.setEnabled(false);
        copyMenuItem.setEnabled(false);
        cutMenuItem.addActionListener(new DefaultEditorKit.CutAction());
        copyMenuItem.addActionListener(new DefaultEditorKit.CopyAction());
        userInterface.getPasteMenuItem().addActionListener(new DefaultEditorKit.PasteAction());

        userInterface.getSelectAllMenuItem().addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                textArea.selectAll();
            }
        });
        userInterface.getChangeFontMenuItem().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Font currentFont = textArea.getFont();
                fontChooserController.setSelectedFont(currentFont.getFamily(), currentFont.getStyle(), currentFont.getSize());
                fontChooserController.showDialog();
            }
        });
        findMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                findDialogController.showDialog();
            }
        });
        replaceMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                replaceDialogController.showDialog();
            }
        });
    }

    /**
     * Generate document listener and caret listener for text area
     */
    private boolean getTransferData() {
        try {
            t = clipboard.getContents(this);
            if (t != null && t.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                return true;
            }
        } catch (Exception e) {

        }
        return false;
    }

    private void initThread() {
        thread = new Thread(() -> {
        while (true) {
                if (getTransferData() == false) {
                    userInterface.getPasteMenuItem().setEnabled(false);
                } else {
                    userInterface.getPasteMenuItem().setEnabled(true);
                }
            }
        });
        thread.start();
    }

    private void generateTextAreaEvent() {
        //Check if there was any text in the text area
        if (textArea.getText().isEmpty()) {
            selectAllMenuItem.setEnabled(false);
        }
        textArea.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                textAreaKeyTypeEvent();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                textAreaKeyTypeEvent();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                textAreaKeyTypeEvent();
            }
        });
        textArea.addCaretListener(new CaretListener() {
            @Override
            public void caretUpdate(CaretEvent e) {
                //Check if there is any text in the textarea has been selected
                if (textArea.getSelectionStart() != textArea.getSelectionEnd()) {
                    cutMenuItem.setEnabled(true);
                    copyMenuItem.setEnabled(true);
                } else {
                    cutMenuItem.setEnabled(false);
                    copyMenuItem.setEnabled(false);
                }
            }
        });
    }

    private void textAreaKeyTypeEvent() {
        isSaved = false;
        //Check if the document can be redo or not
        if (!undoManager.canRedo()) {
            redoMenuItem.setEnabled(false);
        }
        //Check if there was any text in the text area
        if (textArea.getText().isEmpty()) {
            findMenuItem.setEnabled(false);
            replaceMenuItem.setEnabled(false);
            selectAllMenuItem.setEnabled(false);
        } else {
            findMenuItem.setEnabled(true);
            replaceMenuItem.setEnabled(true);
            selectAllMenuItem.setEnabled(true);
        }
        undoMenuItem.setEnabled(true);
    }

    private void windowClosingEvent() {
        //Check if the document hadn't been changed yet
        if (!isChanged()) {
            System.exit(0);
        }
        int userChoice = JOptionPane.showOptionDialog(null, "Do you want to save changes to " + fileName, "MTE",
                JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null, SAVE_DIALOG_BUTTONS, SAVE_DIALOG_BUTTONS[0]);
        //Check if user choose to don't save changes
        if (userChoice == 1) {
            System.exit(0);
        } else if (userChoice == 0) { //Check if user choose to save changes
            //Check whether the document is the default document or not
            if (isDefaultDocument) {
                saveAsNewDocument();
            } else {
                saveCurrentDocument();
            }

        }
    }

    private void newDocumentMenuItemEvent() {
        //Check if the document had been changed yet
        if (!isChanged()) {
            createNewDocument();
            return;
        }
        int userChoice = JOptionPane.showOptionDialog(null, "Do you want to save changes to " + fileName, "MTE",
                JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null, SAVE_DIALOG_BUTTONS, SAVE_DIALOG_BUTTONS[0]);
        //Check if user choose to don't save changes
        if (userChoice == 1) {
            createNewDocument();
            return;
        } else if (userChoice == 0) { //Check if user choose to save changes
            //Check whether the document is the default document or not
            if (isDefaultDocument) {
                saveAsNewDocument();
            } else {
                saveCurrentDocument();
            }
            createNewDocument();
        }
    }

    private void openDocumentMenuItemEvent() {
        if (!isChanged()) {
            openADocument();
            return;
        }
        int userChoice = JOptionPane.showOptionDialog(null, "Do you want to save changes to " + fileName, "MTE",
                JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null, SAVE_DIALOG_BUTTONS, SAVE_DIALOG_BUTTONS[0]);
        //Check if user choose to don't save changes
        if (userChoice == 1) {
            openADocument();
            return;
        } else if (userChoice == 0) { //Check if user choose to save changes
            //Check whether the document is the default document or not
            if (isDefaultDocument) {
                saveAsNewDocument();
            } else {
                saveCurrentDocument();
            }
            openADocument();
        }
    }

    /**
     * let user choose a document file from computer then open it
     */
    private void openADocument() {
        File fileToOpen;
        int returnValue = fileChooser.showOpenDialog(userInterface);
        //Check if user choose to open a file or cancel
        if (returnValue != JFileChooser.APPROVE_OPTION) {
            return;
        }
        while (returnValue == JFileChooser.APPROVE_OPTION) {
            fileToOpen = fileChooser.getSelectedFile();
            //Check if the selected file is not existed in the computer anymore
            if (!fileToOpen.exists()) {
                JOptionPane.showMessageDialog(null, "Selected find not found");
                returnValue = fileChooser.showOpenDialog(userInterface);
                continue;
            }
            //fileName = fileToOpen.getAbsolutePath();
            userInterface.setTitle(fileToOpen.getName() + " - MTE");
            try {
                //Check if the bufferReader had been opened for a fileReader before or not
                if (bufferReader != null) {
                    bufferReader.close();
                    fileReader.close();
                }
                fileReader = new FileReader(fileToOpen);
                bufferReader = new BufferedReader(fileReader);
                textArea.read(fileReader, null);
                textArea.requestFocusInWindow();
            } catch (IOException e) {
                e.printStackTrace();
            }
            isDefaultDocument = false;
            isSaved = true;
            generateTextAreaEvent();
            initUndoRedoAction();
            //Check if the opened document is empty or not
            if (!textArea.getText().isEmpty()) {
                findMenuItem.setEnabled(true);
                replaceMenuItem.setEnabled(true);
            }
            break;
        }
    }

    /**
     * Create a new blank default document
     */
    private void createNewDocument() {
        isSaved = false;
        isDefaultDocument = true;
        textArea.setText("");
        textArea.getDocument().removeUndoableEditListener(undoManager);
        fileName = DEFAULT_FILE_NAME;
        userInterface.setTitle(fileName + " - MTE");
        try {
            //Check if the bufferReader had been opened before
            if (bufferReader != null) {
                bufferReader.close();
                fileReader.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
       generateTextAreaEvent();
      initUndoRedoAction();
    }

    /**
     * Save the current document to the corresponding file
     */
    private void saveCurrentDocument() {
        File saveFile = new File(fileName);
        //Check if the file extension is .txt or not
        if (!saveFile.getName().toLowerCase().endsWith(".txt")) {
            saveFile = new File(saveFile.getParentFile(), saveFile.getName() + ".txt");
        }
        System.out.println(saveFile.getAbsoluteFile());
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(saveFile);
            OutputStreamWriter streamWriter = new OutputStreamWriter(fileOutputStream);
            textArea.write(streamWriter);
            streamWriter.close();
            fileOutputStream.close();
            isDefaultDocument = false;
            isSaved = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Save the current document as a new document file
     *
     * @return
     */
    private boolean saveAsNewDocument() {
        //Check if the document is the default document or not
        if (!isDefaultDocument) {
            fileChooser.setSelectedFile(new File(fileName));
        } else {
            fileChooser.setSelectedFile(new File("*.txt"));
        }
        int returnValue = fileChooser.showSaveDialog(userInterface);
        //Run the infinite loop if user choose save
        while (returnValue == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            //Check whether the file user want to save is existed or not
            if (fileToSave.exists()) {
                int userChoice = JOptionPane.showConfirmDialog(
                        fileChooser,
                        fileToSave.getName() + " already exists\nDo you want to replace it?",
                        "Confirm Save As",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE);
                //Check if user want to overwrite the existed file or not
                if (userChoice == JOptionPane.NO_OPTION) {
                    returnValue = fileChooser.showSaveDialog(userInterface);
                    continue;
                }
            }
            //Check if the file extension is .txt or not
            if (!fileToSave.getName().toLowerCase().endsWith(".txt")) {
                fileToSave = new File(fileToSave.getParentFile(), fileToSave.getName() + ".txt");
            }
            try {
                textArea.write(new OutputStreamWriter(new FileOutputStream(fileToSave),
                        "utf-8"));
                isDefaultDocument = false;
            } catch (Exception e) {
                e.printStackTrace();
            }
         
            userInterface.setTitle(fileToSave.getName() + " - MTE");
            isDefaultDocument = false;
            isSaved = true;
            break; //Break out of the loop if save successfully
        }
        return (returnValue == 0);
    }

    private void initUndoRedoAction() {
        undoMenuItem.setEnabled(false);
        redoMenuItem.setEnabled(false);
        undoManager.end();
        undoManager = new UndoManager();
        textArea.getDocument().addUndoableEditListener(undoManager);
        undoMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                redoMenuItem.setEnabled(true);
                //Check whether can still undo or not
                if (!undoManager.canUndo()) {
                    undoMenuItem.setEnabled(false);
                } else {
                    undoManager.undo();
                }
            }
        });
        redoMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //Check whether can still redo or not
                if (!undoManager.canRedo()) {
                    redoMenuItem.setEnabled(false);
                } else {
                    undoManager.redo();
                }
            }
        });
    }

    /**
     * Check if the document had been changed
     *
     * @return true - the document had been changed, false - the document was
     * not changed
     */
    private boolean isChanged() {
        //Check if the document is the default document and the text area is empty
        if (isDefaultDocument && textArea.getText().isEmpty()) {
            return false;
        }
        //Check if the document is not the default document but had been saved
        if (!isDefaultDocument && isSaved) {
            return false;
        }
        return true;
    }

    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
    Transferable t;
    Thread thread;
    private FileReader fileReader = null;
    private BufferedReader bufferReader = null;
    private UndoManager undoManager;
    private MTEGui userInterface;
    private String fileName;
    private boolean isSaved;
    private boolean isDefaultDocument;
    private final String DEFAULT_FILE_NAME = "Untitled";
    private final String[] SAVE_DIALOG_BUTTONS = {"Save", "Don't save", "Cancel"};
    private FontChooserController fontChooserController;
    private JTextArea textArea;
    private JFileChooser fileChooser;
    private FindDialogController findDialogController;
    private ReplaceDialogController replaceDialogController;
    private javax.swing.JMenuItem selectAllMenuItem;
    private javax.swing.JMenuItem copyMenuItem;
    private javax.swing.JMenuItem cutMenuItem;
    private javax.swing.JMenuItem findMenuItem;
    private javax.swing.JMenuItem undoMenuItem;
    private javax.swing.JMenuItem redoMenuItem;
    private javax.swing.JMenuItem replaceMenuItem;

}
