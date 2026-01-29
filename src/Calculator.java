import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import javax.swing.*;

public class Calculator {
    int boardWidth = 360;
    int boardHeight = 540;

    JFrame frame = new JFrame("Calculator");
    JLabel displayLabel = new JLabel();
    JPanel textPanel = new JPanel();
    JPanel buttonPanel = new JPanel();

    JButton[] numberButtons = new JButton[10];
    ArrayList<JButton> functionButtons = new ArrayList<>();

    JButton addButton, subButton, mulButton, divButton;
    JButton decButton, equButton, delButton, clrButton, negButton;

    Font myFont = new Font("Arial", Font.BOLD, 30);

    double num1 = 0, num2 = 0, result = 0;
    char operator;

    Calculator() {
        frame.setVisible(true);
        frame.setSize(boardWidth, boardHeight);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        textPanel.setLayout(new BorderLayout());
        textPanel.setBounds(0, 0, boardWidth, 100);

        displayLabel.setBackground(Color.DARK_GRAY);
        displayLabel.setForeground(Color.WHITE);
        displayLabel.setFont(new Font("Arial", Font.BOLD, 50));
        displayLabel.setHorizontalAlignment(JLabel.RIGHT);
        displayLabel.setText("0");
        displayLabel.setOpaque(true);

        textPanel.add(displayLabel);
        frame.add(textPanel, BorderLayout.NORTH);

        buttonPanel.setLayout(new GridLayout(5, 4, 10, 10));
        buttonPanel.setBackground(Color.BLACK);
        frame.add(buttonPanel, BorderLayout.CENTER);

        addButton = new JButton("+");
        subButton = new JButton("-");
        mulButton = new JButton("×");
        divButton = new JButton("÷");
        decButton = new JButton(".");
        equButton = new JButton("=");
        delButton = new JButton("Del");
        clrButton = new JButton("C");
        negButton = new JButton("(-)");

        functionButtons.add(addButton);
        functionButtons.add(subButton);
        functionButtons.add(mulButton);
        functionButtons.add(divButton);
        functionButtons.add(decButton);
        functionButtons.add(equButton);
        functionButtons.add(delButton);
        functionButtons.add(clrButton);
        functionButtons.add(negButton);

        for (JButton btn : functionButtons) {
            btn.setFont(myFont);
            btn.setFocusable(false);
            btn.setBackground(Color.LIGHT_GRAY);
            btn.setForeground(Color.BLACK);
            btn.addActionListener(new ButtonClickListener());
        }

        for (int i = 0; i < 10; i++) {
            numberButtons[i] = new JButton(String.valueOf(i));
            numberButtons[i].setFont(myFont);
            numberButtons[i].setFocusable(false);
            numberButtons[i].setBackground(Color.WHITE);
            numberButtons[i].setForeground(Color.BLACK);
            numberButtons[i].addActionListener(new ButtonClickListener());
        }

        // Create percent button
        JButton percButton = new JButton("%");
        functionButtons.add(percButton);
        percButton.setFont(myFont);
        percButton.setFocusable(false);
        percButton.setBackground(Color.LIGHT_GRAY);
        percButton.setForeground(Color.BLACK);
        percButton.addActionListener(new ButtonClickListener());

        // Add buttons to panel in order (5 rows x 4 columns)
        // Row 1: C, Del, %, /
        buttonPanel.add(clrButton);
        buttonPanel.add(delButton);
        buttonPanel.add(percButton);
        buttonPanel.add(divButton);

        // Row 2: 7, 8, 9, *
        buttonPanel.add(numberButtons[7]);
        buttonPanel.add(numberButtons[8]);
        buttonPanel.add(numberButtons[9]);
        buttonPanel.add(mulButton);

        // Row 3: 4, 5, 6, -
        buttonPanel.add(numberButtons[4]);
        buttonPanel.add(numberButtons[5]);
        buttonPanel.add(numberButtons[6]);
        buttonPanel.add(subButton);

        // Row 4: 1, 2, 3, +
        buttonPanel.add(numberButtons[1]);
        buttonPanel.add(numberButtons[2]);
        buttonPanel.add(numberButtons[3]);
        buttonPanel.add(addButton);

        // Row 5: (-), 0, ., =
        buttonPanel.add(negButton);
        buttonPanel.add(numberButtons[0]);
        buttonPanel.add(decButton);
        buttonPanel.add(equButton);
    }

    private class ButtonClickListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            JButton source = (JButton) e.getSource();
            String command = source.getText();

            if ((command.charAt(0) >= '0' && command.charAt(0) <= '9')) {
                // If text is "0" replace it, otherwise append.
                if (displayLabel.getText().equals("0")) {
                    displayLabel.setText(command);
                } else {
                    displayLabel.setText(displayLabel.getText() + command);
                }
            } else if (command.equals(".")) {
                if (!displayLabel.getText().contains(".")) {
                    displayLabel.setText(displayLabel.getText() + ".");
                }
            } else if (command.equals("C")) {
                displayLabel.setText("0");
                num1 = 0;
                num2 = 0;
                operator = '\0';
            } else if (command.equals("Del")) {
                String text = displayLabel.getText();
                if (text.length() > 0) {
                    displayLabel.setText(text.substring(0, text.length() - 1));
                    if (displayLabel.getText().length() == 0) {
                        displayLabel.setText("0");
                    }
                }
            } else if (command.equals("(-)")) {
                // Negate current number
                try {
                    double val = Double.parseDouble(displayLabel.getText());
                    val *= -1;
                    displayLabel.setText(String.valueOf(val));
                    // Remove .0 if integer
                    if (val % 1 == 0) {
                        displayLabel.setText(String.valueOf((int) val));
                    }
                } catch (NumberFormatException ex) {
                    displayLabel.setText("Error");
                }
            } else if (command.equals("%")) {
                try {
                    double val = Double.parseDouble(displayLabel.getText());
                    val /= 100;
                    displayLabel.setText(String.valueOf(val));
                } catch (NumberFormatException ex) {
                    displayLabel.setText("Error");
                }
            } else if (command.equals("=")) {
                num2 = Double.parseDouble(displayLabel.getText());

                switch (operator) {
                    case '+':
                        result = num1 + num2;
                        break;
                    case '-':
                        result = num1 - num2;
                        break;
                    case '×':
                        result = num1 * num2;
                        break;
                    case '÷':
                        if (num2 != 0)
                            result = num1 / num2;
                        else {
                            displayLabel.setText("Error");
                            return;
                        }
                        break;
                    default:
                        result = num2; // No operator clicked
                        break;
                }
                displayLabel.setText(String.valueOf(result));
                // Remove .0 if integer
                if (result % 1 == 0 && !displayLabel.getText().equals("Error")) {
                    displayLabel.setText(String.valueOf((int) result));
                }
                num1 = result;
                // operator = '\0'; // Keep operator? No, clear it.
            } else {
                // Operator buttons
                try {
                    num1 = Double.parseDouble(displayLabel.getText());
                    operator = command.charAt(0);
                    displayLabel.setText("0");
                    // Usually calculators don't clear explicitly but value is stored.
                    // This is simple implementation: after operator, type new number.
                } catch (NumberFormatException ex) {
                    displayLabel.setText("Error");
                }
            }
        }
    }
}
