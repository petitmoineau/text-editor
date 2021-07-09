import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.*;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.UndoManager;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

public class TextEditor extends JFrame implements ActionListener{

    JTextPane textPane;
    JScrollPane scrollPane;
    JSpinner fontSizeSpinner;
    JLabel fontLabel;
    JButton fontColorButton;
    JButton backgroundColorButton;
    JButton bold;
    JButton italic;
    JButton plain;
    JButton underline;
    JButton strikeThrough;
    JComboBox fontBox;
    String selected;
    Clipboard clpbrd;
    AttributeSet aset;

    UndoManager undoManager = new UndoManager();

    JMenuBar menuBar;
    JMenu fileMenu;
    JMenu editMenu;
    JMenuItem undo;
    JMenuItem redo;
    JMenuItem copy;
    JMenuItem paste;
    JMenuItem openItem;
    JMenuItem newItem;
    JMenuItem closeItem;
    JMenuItem saveItem;

    StyleContext sc = StyleContext.getDefaultStyleContext();

    int start;
    int end;

    TextEditor() {
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setTitle("Simple text editor");
        this.setSize(600, 600);
        this.setLayout(new FlowLayout());
        this.setLocationRelativeTo(null);

        textPane = new JTextPane();
        textPane.setPreferredSize(new Dimension(500, 500));
        textPane.setFont(new Font("Times New Roman", Font.PLAIN, 21));

        scrollPane = new JScrollPane(textPane);
        scrollPane.setPreferredSize(new Dimension(500, 500));
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        fontLabel = new JLabel("Font:  ");

        fontSizeSpinner = new JSpinner();
        fontSizeSpinner.setPreferredSize(new Dimension(50, 25));
        fontSizeSpinner.setValue(20);
        fontSizeSpinner.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                aset = sc.addAttribute(aset, StyleConstants.FontSize, fontSizeSpinner.getValue());
                Document doc = textPane.getStyledDocument();
                try {
                    doc.remove(start, end - start);
                    doc.insertString(start, selected, aset);
                } catch (BadLocationException badLocationException) {
                    badLocationException.printStackTrace();
                }
            }
        });

        bold = new JButton("Bold");
        bold.addActionListener(this);

        italic = new JButton("Italic");
        italic.addActionListener(this);

        plain = new JButton("Plain");
        plain.addActionListener(this);

        underline = new JButton("Underline");
        underline.addActionListener(this);

        strikeThrough = new JButton("Strike through");
        strikeThrough.addActionListener(this);

        textPane.addMouseListener(new MouseAdapter() {
            public void mouseReleased(MouseEvent e) {
                start = textPane.getSelectionStart();
                end = textPane.getSelectionEnd();
                try {
                    System.out.println("The Text : " + textPane.getDocument().getText(start, end - start));
                    selected = textPane.getDocument().getText(start, end - start);
                } catch (BadLocationException ef) {
                    System.err.println(ef);
                    ef.printStackTrace();
                }
            }
        });

        fontColorButton = new JButton("Font color");
        fontColorButton.addActionListener(this);
        
        backgroundColorButton = new JButton("Background color");
        backgroundColorButton.addActionListener(this);

        String[] fonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
        fontBox = new JComboBox(fonts);
        fontBox.addActionListener(this);
        fontBox.setSelectedItem("Times New Roman");

        menuBar = new JMenuBar();
        fileMenu = new JMenu("File");
        editMenu = new JMenu("Edit");
        undo = new JMenuItem("Undo");
        redo = new JMenuItem("Redo");
        copy = new JMenuItem("Copy");
        paste = new JMenuItem("Paste");
        newItem = new JMenuItem("New");
        openItem = new JMenuItem("Open");
        saveItem = new JMenuItem("Save");
        closeItem = new JMenuItem("Close");

        undo.addActionListener(this);
        redo.addActionListener(this);
        copy.addActionListener(this);
        paste.addActionListener(this);
        newItem.addActionListener(this);
        openItem.addActionListener(this);
        saveItem.addActionListener(this);
        closeItem.addActionListener(this);

        fileMenu.add(newItem);
        fileMenu.add(openItem);
        fileMenu.add(saveItem);
        fileMenu.add(closeItem);
        editMenu.add(undo);
        editMenu.add(redo);
        editMenu.add(copy);
        editMenu.add(paste);
        menuBar.add(fileMenu);
        menuBar.add(editMenu);

        this.setJMenuBar(menuBar);
        this.add(fontLabel);
        this.add(fontSizeSpinner);
        this.add(fontColorButton);
        this.add(backgroundColorButton);
        this.add(bold);
        this.add(italic);
        this.add(plain);
        this.add(underline);
        this.add(strikeThrough);
        this.add(fontBox);
        this.add(scrollPane);
        this.setVisible(true);

        Document doc = textPane.getStyledDocument();
        doc.addUndoableEditListener(new MyUndoableEditListener());
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == fontColorButton) {
            JColorChooser colorChooser = new JColorChooser();
            Color color = colorChooser.showDialog(null, "Choose a color", Color.BLACK);
            aset = sc.addAttribute(aset, StyleConstants.Foreground, color);
            Document doc = textPane.getStyledDocument();
            try {
                doc.remove(start, end - start);
                doc.insertString(start, selected, aset);
            } catch (BadLocationException badLocationException) {
                badLocationException.printStackTrace();
            }
        }

        if (e.getSource() == backgroundColorButton) {
            JColorChooser colorChooser = new JColorChooser();
            Color color = colorChooser.showDialog(null, "Choose a color", Color.BLACK);
            aset = sc.addAttribute(aset, StyleConstants.Background, color);
            Document doc = textPane.getStyledDocument();
            try {
                doc.remove(start, end - start);
                doc.insertString(start, selected, aset);
            } catch (BadLocationException badLocationException) {
                badLocationException.printStackTrace();
            }
        }

        if (e.getSource() == bold) {
            aset = sc.addAttribute(aset, StyleConstants.Bold, true);
            Document doc = textPane.getStyledDocument();
            try {
                doc.remove(start, end - start);
                doc.insertString(start, selected, aset);
            } catch (BadLocationException badLocationException) {
                badLocationException.printStackTrace();
            }
        }

        if (e.getSource() == italic) {
            aset = sc.addAttribute(aset, StyleConstants.Italic, true);
            Document doc = textPane.getStyledDocument();
            try {
                doc.remove(start, end - start);
                doc.insertString(start, selected, aset);
            } catch (BadLocationException badLocationException) {
                badLocationException.printStackTrace();
            }
        }

        if (e.getSource() == plain) {
            aset = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.FontSize, fontSizeSpinner.getValue());
            aset = sc.addAttribute(aset, Font.PLAIN, true);
            Document doc = textPane.getStyledDocument();
            try {
                doc.remove(start, end - start);
                doc.insertString(start, selected, aset);
            } catch (BadLocationException badLocationException) {
                badLocationException.printStackTrace();
            }
        }

        if (e.getSource() == underline) {
            aset = sc.addAttribute(aset, StyleConstants.Underline, true);
            Document doc = textPane.getStyledDocument();
            try {
                doc.remove(start, end - start);
                doc.insertString(start, selected, aset);
            } catch (BadLocationException badLocationException) {
                badLocationException.printStackTrace();
            }
        }

        if (e.getSource() == strikeThrough) {
            aset = sc.addAttribute(aset, StyleConstants.StrikeThrough, true);
            Document doc = textPane.getStyledDocument();
            try {
                doc.remove(start, end - start);
                doc.insertString(start, selected, aset);
            } catch (BadLocationException badLocationException) {
                badLocationException.printStackTrace();
            }
        }

        if (e.getSource() == fontBox) {
            aset = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.FontFamily, fontBox.getSelectedItem());
            Document doc = textPane.getStyledDocument();
            try {
                doc.remove(start, end - start);
                doc.insertString(start, selected, aset);
            } catch (BadLocationException badLocationException) {
                badLocationException.printStackTrace();
            }
        }

        if (e.getSource() == undo) {
            undo.setEnabled(undoManager.canUndo());
            if (undoManager.canUndo()) {
                undoManager.undo();
            }
        }

        if (e.getSource() == undo) {
            undoManager.undo();
        }

        if (e.getSource() == redo) {
            try {
                undoManager.redo();
            } catch (CannotRedoException ex) {
                System.out.println("Unable to redo: " + ex);
                ex.printStackTrace();
            }
        }

        if (e.getSource() == copy) {
            StringSelection stringSelection = new StringSelection(selected);
            clpbrd = Toolkit.getDefaultToolkit().getSystemClipboard();
            clpbrd.setContents(stringSelection, null);
        }

        if (e.getSource() == paste) {
            DataFlavor dataFlavor = clpbrd.getAvailableDataFlavors()[0];
            aset = sc.addAttribute(SimpleAttributeSet.EMPTY, Font.PLAIN, true);
            Document doc = textPane.getStyledDocument();
            try {
                doc.insertString(start, (String) clpbrd.getData(dataFlavor), aset);
            } catch (BadLocationException | UnsupportedFlavorException | IOException badLocationException) {
                badLocationException.printStackTrace();
            }
        }

        if (e.getSource() == newItem) {
            textPane.setText("");
        }
        if (e.getSource() == openItem) {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setCurrentDirectory(new File("."));
            FileNameExtensionFilter filter = new FileNameExtensionFilter("Text files", "txt");
            fileChooser.setFileFilter(filter);

            int response = fileChooser.showOpenDialog(null);
            if (response == JFileChooser.APPROVE_OPTION) {
                Document doc = textPane.getStyledDocument();
                AttributeSet aset = sc.addAttribute(SimpleAttributeSet.EMPTY, Font.PLAIN, true);
                File file = new File(fileChooser.getSelectedFile().getAbsolutePath());
                Scanner fileIn = null;
                try {
                    fileIn = new Scanner(file);
                    if (file.isFile()) {
                        while (fileIn.hasNextLine()) {
                            String line = fileIn.nextLine() + "\n";
                            doc.insertString(start, line, aset);
                        }
                    }
                } catch (FileNotFoundException | BadLocationException fileNotFoundException) {
                    fileNotFoundException.printStackTrace();
                } finally {
                    fileIn.close();
                }
            }
        }

        if (e.getSource() == saveItem) {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setCurrentDirectory(new File("."));

            int response = fileChooser.showSaveDialog(null);

            if (response == JFileChooser.APPROVE_OPTION) {
                File file;
                PrintWriter fileOut = null;
                file = new File(fileChooser.getSelectedFile().getAbsolutePath());
                try {
                    fileOut = new PrintWriter(file);
                    fileOut.println(textPane.getText());
                } catch (FileNotFoundException fileNotFoundException) {
                    fileNotFoundException.printStackTrace();
                } finally {
                    fileOut.close();
                }
            }
        }

        if (e.getSource() == closeItem) {
            System.exit(0);
        }
    }

    private class MyUndoableEditListener implements UndoableEditListener {
        public void undoableEditHappened(UndoableEditEvent e) {
            undoManager.addEdit(e.getEdit());
        }
    }
}
