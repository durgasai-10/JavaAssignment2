import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

class FitnessEntry {
    private Date date;
    private String activity;
    private int steps;
    private double caloriesBurned;

    public FitnessEntry(Date date, String activity, int steps, double caloriesBurned) {
        this.date = date;
        this.activity = activity;
        this.steps = steps;
        this.caloriesBurned = caloriesBurned;
    }

    public Date getDate() {
        return date;
    }

    public String getActivity() {
        return activity;
    }

    public int getSteps() {
        return steps;
    }

    public double getCaloriesBurned() {
        return caloriesBurned;
    }

    @Override
    public String toString() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return "[" + activity + "] " + dateFormat.format(date) + " - Steps: " + steps + " - Calories Burned: " + caloriesBurned;
    }
}

class FitnessTrackingSystemApp extends JFrame {
    private List<FitnessEntry> entries;
    private DefaultListModel<FitnessEntry> listModel;
    private JList<FitnessEntry> entryList;
    private JTextField stepsField;
    private JTextField caloriesField;
    private JComboBox<String> activityComboBox;
    private JTextField dailyGoalField;
    private JLabel totalStepsLabel;

    private static final String DATA_FILE = "fitness_entries.dat";

    public FitnessTrackingSystemApp() {
        entries = new ArrayList<>();
        listModel = new DefaultListModel<>();
        entryList = new JList<>(listModel);
        stepsField = new JTextField(8);
        caloriesField = new JTextField(8);
        activityComboBox = new JComboBox<>(new String[]{"Walking", "Running", "Cycling", "Other"});
        dailyGoalField = new JTextField(8);
        totalStepsLabel = new JLabel("Total Steps: 0");

        JButton addButton = new JButton("Add Entry");
        addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addFitnessEntry();
            }
        });

        JButton saveButton = new JButton("Save Entries");
        saveButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                saveEntriesToFile();
            }
        });

        // Layout
        JPanel inputPanel = new JPanel(new GridLayout(3, 2));
        inputPanel.add(new JLabel("Activity:"));
        inputPanel.add(activityComboBox);
        inputPanel.add(new JLabel("Steps:"));
        inputPanel.add(stepsField);
        inputPanel.add(new JLabel("Calories Burned:"));
        inputPanel.add(caloriesField);

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(inputPanel, BorderLayout.NORTH);
        panel.add(addButton, BorderLayout.CENTER);

        JPanel goalPanel = new JPanel(new FlowLayout());
        goalPanel.add(new JLabel("Daily Goal (Steps):"));
        goalPanel.add(dailyGoalField);
        goalPanel.add(saveButton);

        JPanel statsPanel = new JPanel(new FlowLayout());
        statsPanel.add(totalStepsLabel);

        JPanel bottomPanel = new JPanel(new GridLayout(2, 1));
        bottomPanel.add(goalPanel);
        bottomPanel.add(statsPanel);

        setLayout(new BorderLayout());
        add(new JScrollPane(entryList), BorderLayout.CENTER);
        add(panel, BorderLayout.NORTH);
        add(bottomPanel, BorderLayout.SOUTH);

        loadEntriesFromFile();
        updateTotalStepsLabel();

        setTitle("Fitness Tracking System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void addFitnessEntry() {
        String activity = (String) activityComboBox.getSelectedItem();
        String stepsStr = stepsField.getText();
        String caloriesStr = caloriesField.getText();

        if (!activity.isEmpty() && !stepsStr.isEmpty() && !caloriesStr.isEmpty()) {
            try {
                int steps = Integer.parseInt(stepsStr);
                double caloriesBurned = Double.parseDouble(caloriesStr);
                FitnessEntry entry = new FitnessEntry(new Date(), activity, steps, caloriesBurned);
                entries.add(entry);
                listModel.addElement(entry);
                stepsField.setText("");
                caloriesField.setText("");
                updateTotalStepsLabel();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid input. Please enter valid numbers.");
            }
        }
    }

    private void saveEntriesToFile() {
        try (ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(DATA_FILE))) {
            outputStream.writeObject(entries);
            JOptionPane.showMessageDialog(this, "Entries saved successfully.", "Save Successful", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error saving entries to file.", "Save Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadEntriesFromFile() {
        try (ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(DATA_FILE))) {
            entries = (List<FitnessEntry>) inputStream.readObject();
            for (FitnessEntry entry : entries) {
                listModel.addElement(entry);
            }
        } catch (IOException | ClassNotFoundException ex) {
            // Ignore if the file doesn't exist or if there's a format issue.
        }
    }

    private void updateTotalStepsLabel() {
        int totalSteps = entries.stream().mapToInt(FitnessEntry::getSteps).sum();
        totalStepsLabel.setText("Total Steps: " + totalSteps);

        String goalStr = dailyGoalField.getText();
        if (!goalStr.isEmpty()) {
            try {
                int dailyGoal = Integer.parseInt(goalStr);
                if (totalSteps >= dailyGoal) {
                    JOptionPane.showMessageDialog(this, "Congratulations! You reached your daily goal!", "Goal Achieved", JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid goal. Please enter a valid number.", "Goal Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new FitnessTrackingSystemApp();
            }
        });
    }
}
