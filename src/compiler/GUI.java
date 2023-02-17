package compiler;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.BorderLayout;
import java.awt.Cursor;
//import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class GUI extends JFrame {

    private final JPanel contentPane;
    private final JTextArea javaInput;
    private final LineNumberTextArea inputLineNumberArea;
    private final JTextArea mipsOutput;
    private final LineNumberTextArea resultLineNumberArea;

    private boolean isValid = false;

    public GUI() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/code.png")));
        setTitle("Simple Compiler Project");
        setBounds(100, 50, 1000, 600);
        contentPane = new JPanel(new GridLayout(1, 2));
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);

// Create the input area and its line number area
        javaInput = new JTextArea();
        javaInput.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14));
        javaInput.setBackground(new Color(40, 40, 40));
        javaInput.setForeground(Color.WHITE);
        javaInput.setCaretColor(Color.WHITE);
        JScrollPane inputScrollPane = new JScrollPane(javaInput);
        inputLineNumberArea = new LineNumberTextArea(javaInput);
        JScrollPane inputLineNumberScrollPane = new JScrollPane(inputLineNumberArea);
        inputScrollPane.setRowHeaderView(inputLineNumberScrollPane);

// Add a titled border to the input area
        JPanel javaInputPanel = new JPanel(new BorderLayout());
        javaInputPanel.setBackground(new Color(40, 40, 40));
        javaInputPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 2),
                "Java Code",
                TitledBorder.CENTER,
                TitledBorder.TOP,
                new Font(Font.SANS_SERIF, Font.BOLD, 16),
                Color.WHITE
        ));
        javaInputPanel.add(inputScrollPane, BorderLayout.CENTER);

// Add the input button to the top right of the input area
        JButton compileButton = new JButton("Compile");
        compileButton.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
        compileButton.setBackground(new Color(50, 150, 255));
        compileButton.setForeground(Color.WHITE);
        compileButton.setFocusPainted(false);
        compileButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        JPanel compileButtonPanel = new JPanel(new BorderLayout());
        compileButtonPanel.add(compileButton, BorderLayout.EAST);
        javaInputPanel.add(compileButtonPanel, BorderLayout.NORTH);

        contentPane.add(javaInputPanel);

// Create the result area and its line number area
        mipsOutput = new JTextArea();
        mipsOutput.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14));
        mipsOutput.setBackground(new Color(40, 40, 40));
        mipsOutput.setForeground(Color.WHITE);
        mipsOutput.setCaretColor(Color.WHITE);
        JScrollPane resultScrollPane = new JScrollPane(mipsOutput);
        resultLineNumberArea = new LineNumberTextArea(mipsOutput);
        JScrollPane resultLineNumberScrollPane = new JScrollPane(resultLineNumberArea);
        resultScrollPane.setRowHeaderView(resultLineNumberScrollPane);

// Add a titled border to the result area
        JPanel mipsOutputPanel = new JPanel(new BorderLayout());
        mipsOutputPanel.setBackground(new Color(40, 40, 40));
        mipsOutputPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 2),
                "Assembly Code",
                TitledBorder.CENTER,
                TitledBorder.TOP,
                new Font(Font.SANS_SERIF, Font.BOLD, 16),
                Color.WHITE
        ));
        mipsOutputPanel.add(resultScrollPane, BorderLayout.CENTER);
                            mipsOutput.setEditable(false);


        // Add the result button to the top right of the result area
        final JButton runButton = new JButton("Run");
        runButton.setBackground(Color.lightGray);
        JPanel runButtonPanel = new JPanel(new BorderLayout());
        runButtonPanel.add(runButton, BorderLayout.EAST);
        runButtonPanel.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
        mipsOutputPanel.add(runButtonPanel, BorderLayout.NORTH);
        runButton.setFocusPainted(false);

        contentPane.add(mipsOutputPanel);

        TextAreaListener listener = new TextAreaListener();
        javaInput.getDocument().addDocumentListener(listener);
        mipsOutput.getDocument().addDocumentListener(listener);

        // listener to the buttons
        compileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Compile button clicked, do something here
                String assembledCode = Assemble.assemble(unwrap.unwrapCode(javaInput.getText()));
                if (assembledCode.contains("error")) {
                    mipsOutput.setText(assembledCode);
                    mipsOutput.setForeground(Color.red);
                    runButton.setBackground(Color.lightGray);
                    isValid = false;
                } else {
                    mipsOutput.setText(assembledCode);
                    mipsOutput.setForeground(Color.WHITE);
                    runButton.setBackground(Color.GREEN);
                    isValid = true;
                }

            }
        });

        runButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (isValid) {
                    executer.execute(mipsOutput.getText());
                }
            }
        });
    }

    private class TextAreaListener implements DocumentListener {

        @Override
        public void insertUpdate(DocumentEvent e) {
            inputLineNumberArea.updateLineNumberArea();
            resultLineNumberArea.updateLineNumberArea();
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            inputLineNumberArea.updateLineNumberArea();
            resultLineNumberArea.updateLineNumberArea();
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
            inputLineNumberArea.updateLineNumberArea();
            resultLineNumberArea.updateLineNumberArea();

        }
    }

    public void run() {
        try {
            GUI frame = new GUI();
            frame.setVisible(true);
        } catch (Exception e) {

        }
    }

    /**
     * This class provides line numbers for a JTextArea.
     */
    private final class LineNumberTextArea extends JTextArea {

        private final JTextArea textArea;

        public LineNumberTextArea(JTextArea textArea) {
            this.textArea = textArea;
            setEditable(false);
            setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
            setForeground(Color.GRAY);
            setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Color.GRAY));
            updateLineNumberArea();
        }

        public void updateLineNumberArea() {
            String lineNumbers = "";
            int totalLines = textArea.getLineCount();
            for (int i = 1; i <= totalLines; i++) {
                lineNumbers += i + "\n";
            }
            setText(lineNumbers);
        }
    }
}
