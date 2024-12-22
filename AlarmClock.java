
package alarmclock;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.*;

public class AlarmClock {

    private static ArrayList<Alarm> alarmList = new ArrayList<>(); // List to store alarms
    private static String snoozeTime = null; // Store the snoozed time
    private static Color currentBackgroundColor = Color.BLACK;
    private static Color currentButtonColor = Color.WHITE;
    private static Color currentLabelColor = Color.WHITE;
    private static Color currentTextColor = Color.BLACK;
    private static TrayIcon trayIcon;
    private static JTextField t; // Declare JTextField
    private static JLabel l; // Declare JLabel
    private static int fontSize = 30;

    public static void main(String[] args) {
        if (!SystemTray.isSupported()) {
            System.out.println("System tray is not supported!");
            return;
        }

        JFrame frame = new JFrame("ALARM CLOCK");
        frame.setLayout(null);
        frame.getContentPane().setBackground(currentBackgroundColor);

        // Initialize JTextField
        t = new JTextField();
        t.setBounds(600, 200, 300, 50);
        t.setFont(new Font("Arial", Font.BOLD, fontSize));
        t.setHorizontalAlignment(JTextField.CENTER);
        t.setForeground(currentTextColor);
        frame.add(t);

        String[] amPmOptions = {"AM", "PM"};
        JComboBox<String> amPmComboBox = new JComboBox<>(amPmOptions);
        amPmComboBox.setFont(new Font("Arial", Font.BOLD, 20));
        amPmComboBox.setBackground(currentButtonColor);
        amPmComboBox.setForeground(currentTextColor);
        amPmComboBox.setSelectedIndex(0);
        amPmComboBox.setBounds(910, 200, 100, 50);
        frame.add(amPmComboBox);

        JButton b = new JButton("Set the Alarm");
        b.setBounds(730, 300, 150, 50);
        b.setBackground(currentButtonColor);
        b.setForeground(currentTextColor);
        frame.add(b);

        JButton showAlarmsButton = new JButton("Show Alarms");
        showAlarmsButton.setBounds(730, 440, 150, 50);
        showAlarmsButton.setBackground(currentButtonColor);
        showAlarmsButton.setForeground(currentTextColor);
        frame.add(showAlarmsButton);

        JButton repeatButton = new JButton("Repeat");
        repeatButton.setBounds(730, 370, 150, 50);
        repeatButton.setBackground(currentButtonColor);
        repeatButton.setForeground(currentTextColor);
        frame.add(repeatButton);

        JButton deleteButton = new JButton("Delete Alarm");
        deleteButton.setBounds(730, 510, 150, 50);
        deleteButton.setBackground(currentButtonColor);
        deleteButton.setForeground(currentTextColor);
        frame.add(deleteButton);

        JButton cancelAllButton = new JButton("Cancel All");
        cancelAllButton.setBounds(730, 580, 150, 50);
        cancelAllButton.setBackground(currentButtonColor);
        cancelAllButton.setForeground(currentTextColor);
        frame.add(cancelAllButton);

        // Add a snooze button to the frame
        JButton snoozeButton = new JButton("Snooze");
        snoozeButton.setBounds(730, 650, 150, 50);
        snoozeButton.setBackground(currentButtonColor);
        snoozeButton.setForeground(currentTextColor);
        frame.add(snoozeButton);



        // Initialize JLabel
        l = new JLabel();
        l.setFont(new Font("Arial", Font.BOLD, fontSize + 20)); // Larger font for the label
        l.setBounds(600, 100, 400, 50);
        l.setForeground(currentLabelColor);
        frame.add(l);


        JButton themeButton = new JButton("Change Theme");
        themeButton.setBounds(20, 20, 150, 50);
        themeButton.setBackground(currentButtonColor);
        themeButton.setForeground(currentTextColor);
        frame.add(themeButton);


        // Font size buttons
        JButton increaseFontButton = new JButton("Increase Font Size");
        increaseFontButton.setBounds(200, 20, 150, 50);
        increaseFontButton.setBackground(currentButtonColor);
        increaseFontButton.setForeground(currentTextColor);
        frame.add(increaseFontButton);

        JButton decreaseFontButton = new JButton("Decrease Font Size");
        decreaseFontButton.setBounds(360, 20, 150, 50);
        decreaseFontButton.setBackground(currentButtonColor);
        decreaseFontButton.setForeground(currentTextColor);
        frame.add(decreaseFontButton);


        // System Tray Setup
        setupSystemTray(frame);

        Timer timer = new Timer();
        // Timer task to check if the current time matches any alarm time
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                String currentTime24 = new SimpleDateFormat("HH:mm:ss").format(new Date());
                String currentDay = new SimpleDateFormat("EEEE").format(new Date()); // Get current day
                String currentTime12 = convertTo12HourFormat(currentTime24);
                l.setText(currentTime12);

                // Check if the current time matches any alarm
                for (Alarm alarm : alarmList) {
                    if (alarm.time.equals(currentTime24) && !alarm.hasRung) {

                        // Send a system tray notification
                        showAlarmNotification("Alarm: " + convertTo12HourFormat(alarm.time) + " (" + alarm.label + ")");

                        // Display a pop-up message when alarm time matches the current time
                        JOptionPane.showMessageDialog(frame, "Alarm: " + convertTo12HourFormat(alarm.time) + " (" + alarm.label + ")",
                                "Alarm Triggered", JOptionPane.INFORMATION_MESSAGE);
                        // Set the hasRung flag to true when the alarm rings
                        alarm.hasRung = true;
                    }
                }
            }
        }, 0, 1000);


        // Modify the action listener for the "Set the Alarm" button
        b.addActionListener(e -> {
            String alarmTime = t.getText().trim();

            if (alarmTime.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Please set an alarm.", "No Alarm", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String amPm = (String) amPmComboBox.getSelectedItem();
            String formattedAlarmTime;

            // Validate the input format and convert it to 24-hour time using AM/PM
            if (alarmTime.matches("^\\d{1,2}$")) { // Only hour provided
                int hour = Integer.parseInt(alarmTime);
                if (hour < 1 || hour > 12) {
                    JOptionPane.showMessageDialog(frame, "Hour must be between 1 and 12.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                formattedAlarmTime = convertTo24HourFormat(hour + ":00:00", amPm);
            } else if (alarmTime.matches("^\\d{1,2}:\\d{1,2}$")) { // Hour and minute provided
                String[] parts = alarmTime.split(":");
                int hour = Integer.parseInt(parts[0]);
                int minute = Integer.parseInt(parts[1]);

                if (hour < 1 || hour > 12) {
                    JOptionPane.showMessageDialog(frame, "Hour must be between 1 and 12.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (minute < 0 || minute > 59) {
                    JOptionPane.showMessageDialog(frame, "Minute must be between 0 and 59.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                formattedAlarmTime = convertTo24HourFormat(String.format("%02d:%02d:00", hour, minute), amPm);
            } else if (alarmTime.matches("^\\d{1,2}:\\d{1,2}:\\d{1,2}$")) { // Full time provided
                String[] parts = alarmTime.split(":");
                int hour = Integer.parseInt(parts[0]);
                int minute = Integer.parseInt(parts[1]);
                int second = Integer.parseInt(parts[2]);

                if (hour < 1 || hour > 12) {
                    JOptionPane.showMessageDialog(frame, "Hour must be between 1 and 12.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (minute < 0 || minute > 59) {
                    JOptionPane.showMessageDialog(frame, "Minute must be between 0 and 59.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (second < 0 || second > 59) {
                    JOptionPane.showMessageDialog(frame, "Second must be between 0 and 59.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                formattedAlarmTime = convertTo24HourFormat(String.format("%02d:%02d:%02d", hour, minute, second), amPm);
            } else { // Invalid format
                JOptionPane.showMessageDialog(frame, "Invalid time format! Use HH, HH:mm, or HH:mm:ss.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }



            int labelResponse = JOptionPane.showConfirmDialog(frame, "Do you want to add a label to the alarm?", "Add Label", JOptionPane.YES_NO_OPTION);
            String label = "No Label";
            if (labelResponse == JOptionPane.YES_OPTION) {
                label = JOptionPane.showInputDialog(frame, "Enter a label for the alarm:", "Alarm Label", JOptionPane.PLAIN_MESSAGE);
                if (label == null || label.trim().isEmpty()) {
                    label = "No Label";
                }
            }



            // Add the formatted alarm time to the alarm list
            Alarm newAlarm = new Alarm(formattedAlarmTime, new ArrayList<>(), label);
            alarmList.add(newAlarm);

            JOptionPane.showMessageDialog(frame, "Alarm set for " + convertTo12HourFormat(formattedAlarmTime) +
                    " with label: " + label + "!", "Alarm Set", JOptionPane.INFORMATION_MESSAGE);
        });




        repeatButton.addActionListener(e -> {
            if (alarmList.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "No alarms set to repeat.", "Repeat Alarm", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            // Filter out alarms that have already rung
            String[] alarmOptions = alarmList.stream()
                    .filter(alarm -> !alarm.hasRung) // Only include alarms that have not rung
                    .map(alarm -> convertTo12HourFormat(alarm.time) + " (Label: " + alarm.label + ")")
                    .toArray(String[]::new);

            if (alarmOptions.length == 0) {
                JOptionPane.showMessageDialog(frame, "No alarms available to repeat.", "Repeat Alarm", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            String selectedAlarm = (String) JOptionPane.showInputDialog(frame,
                    "Select an alarm to set repeat days:",
                    "Select Alarm",
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    alarmOptions,
                    alarmOptions[0]);

            if (selectedAlarm == null) {
                return; // User closed the dialog or selected nothing
            }

            // Create a panel for day selection
            JPanel dayPanel = new JPanel();
            dayPanel.setLayout(new GridLayout(0, 1));
            JCheckBox[] dayCheckBoxes = new JCheckBox[7];
            String[] days = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};

            for (int i = 0; i < days.length; i++) {
                dayCheckBoxes[i] = new JCheckBox(days[i]);
                dayPanel.add(dayCheckBoxes[i]);
            }

            int response = JOptionPane.showConfirmDialog(frame, dayPanel, "Select Repeat Days", JOptionPane.OK_CANCEL_OPTION);
            if (response == JOptionPane.OK_OPTION) {
                ArrayList<String> selectedDays = new ArrayList<>();
                for (JCheckBox checkBox : dayCheckBoxes) {
                    if (checkBox.isSelected()) {
                        selectedDays.add(checkBox.getText());
                    }
                }

                // Find the selected alarm in the list
                for (Alarm alarm : alarmList) {
                    String alarmDisplayText = convertTo12HourFormat(alarm.time) + " (Label: " + alarm.label + ")";
                    if (alarmDisplayText.equals(selectedAlarm)) {
                        alarm.repeatDays = selectedDays; // Set the repeat days for the selected alarm
                        JOptionPane.showMessageDialog(frame, "Repeat days set for the alarm!", "Repeat Set", JOptionPane.INFORMATION_MESSAGE);
                        break;
                    }
                }
            }
        });




        showAlarmsButton.addActionListener(e -> {
            if (alarmList.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "No alarms set.", "Alarm List", JOptionPane.INFORMATION_MESSAGE);
            } else {
                StringBuilder alarmDisplay = new StringBuilder(" Set Alarms:\n");

                // Filter out alarms that have already rung
                for (Alarm alarm : alarmList) {

                    if (!alarm.hasRung) { // Only show alarms that have not rung yet
                        alarmDisplay.append(convertTo12HourFormat(alarm.time))
                                .append(" (Label: ")
                                .append(alarm.label)
                                .append(", Repeats: ")
                                .append(alarm.repeatDays.isEmpty() ? "None" : String.join(", ", alarm.repeatDays))
                                .append(")\n");
                    }
                }

                // Show the filtered alarm list
                if (alarmDisplay.length() == 0) {
                    JOptionPane.showMessageDialog(frame, "No alarms left to show.", "Alarm List", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(frame, alarmDisplay.toString(), "Alarm List", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });


        deleteButton.addActionListener(e -> {
            if (alarmList.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "No alarms to delete.", "Delete Alarm", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Create a dialog to select an alarm to delete
            // Filter out alarms that have already rung
            String[] alarmOptions = alarmList.stream()
                    .filter(alarm -> !alarm.hasRung) // Only show alarms that haven't rung yet
                    .map(alarm -> convertTo12HourFormat(alarm.time) + " (Label: " + alarm.label + ")")
                    .toArray(String[]::new);

            if (alarmOptions.length == 0) {
                JOptionPane.showMessageDialog(frame, "All alarms have already rung. No alarms left to delete.", "Delete Alarm", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            String alarmToDelete = (String) JOptionPane.showInputDialog(frame, "Select an alarm to delete:",
                    "Delete Alarm", JOptionPane.QUESTION_MESSAGE, null, alarmOptions, null);

            if (alarmToDelete != null) {
                // Extract the time and label from the selected alarm
                String[] parts = alarmToDelete.split(" \\(Label: ");
                String selectedTime = parts[0].trim(); // Get the time part
                String selectedLabel = parts[1].replace(")", "").trim(); // Get the label part

                // Remove the alarm from the list
                boolean removed = alarmList.removeIf(alarm ->
                        convertTo12HourFormat(alarm.time).equals(selectedTime) && alarm.label.equals(selectedLabel)
                );

                if (removed) {
                    JOptionPane.showMessageDialog(frame, "Alarm " + alarmToDelete + " deleted successfully.", "Alarm Deleted", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(frame, "Alarm not found.", "Delete Alarm", JOptionPane.WARNING_MESSAGE);
                }
            }
        });



// Cancel all alarms action listener
        cancelAllButton.addActionListener(e -> {
            if (alarmList.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "There are no alarms set to cancel.", "Cancel All", JOptionPane.INFORMATION_MESSAGE);
            } else {
                alarmList.clear();
                JOptionPane.showMessageDialog(frame, "All alarms have been canceled.", "Cancel All", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        // Snooze button action listener
        // Snooze button action listener
        snoozeButton.addActionListener(e -> {
            if (alarmList.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "No alarms set to snooze.", "Snooze Alarm", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            // Find the triggered alarm
            Alarm alarmToSnooze = null;
            for (Alarm alarm : alarmList) {
                if (alarm.hasRung) {
                    alarmToSnooze = alarm;
                    break;
                }
            }

            if (alarmToSnooze == null) {
                JOptionPane.showMessageDialog(frame, "No alarms have rung yet.", "Snooze Alarm", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            // Prompt user for snooze time
            String[] options = {"2 minutes", "3 minutes", "5 minutes"};
            int choice = JOptionPane.showOptionDialog(frame, "Choose snooze time:", "Snooze Time",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
            if (choice == JOptionPane.CLOSED_OPTION) {
                return; // User closed the dialog without choosing
            }

            // Map the user's choice to the corresponding snooze time in milliseconds
            int snoozeMinutes = switch (choice) {
                case 0 -> 2;
                case 1 -> 3;
                case 2 -> 5;
                default -> 0;
            };

            try {
                // Determine the format of the alarm time
                SimpleDateFormat timeFormat;
                boolean isFullFormat = alarmToSnooze.time.contains(":") && (alarmToSnooze.time.split(":").length == 3);

                if (isFullFormat) {
                    timeFormat = new SimpleDateFormat("HH:mm:ss");
                } else {
                    timeFormat = new SimpleDateFormat("HH:mm");
                }

                // Parse the alarm time
                Date alarmTime = timeFormat.parse(alarmToSnooze.time);

                // Add the selected snooze time
                long snoozeTimeMillis = alarmTime.getTime() + snoozeMinutes * 60 * 1000;
                Date newAlarmTime = new Date(snoozeTimeMillis);

                // Format the new alarm time according to the original format
                String newAlarmTimeFormatted = isFullFormat ? new SimpleDateFormat("HH:mm:ss").format(newAlarmTime)
                        : new SimpleDateFormat("HH:mm").format(newAlarmTime);

                // Update the alarm time with the new snoozed time
                alarmToSnooze.time = newAlarmTimeFormatted; // Store in the correct format
                alarmToSnooze.hasRung = false; // Reset hasRung flag

                // Notify the user about the snooze
                JOptionPane.showMessageDialog(frame, "Alarm snoozed! New time: " + newAlarmTimeFormatted,
                        "Snooze Alarm", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Error snoozing the alarm.", "Snooze Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        themeButton.addActionListener(e -> {
            String[] themes = {"Dusty Rose", "Sage Green", "Charcoal", "Navy Blue"};
            String selectedTheme = (String) JOptionPane.showInputDialog(frame, "Select a theme:", "Change Theme", JOptionPane.PLAIN_MESSAGE, null, themes, themes[0]);

            if (selectedTheme != null) {
                switch (selectedTheme) {
                    case "Dusty Rose":
                        currentBackgroundColor = new Color(205, 150, 158);
                        currentButtonColor = new Color(255, 228, 229);
                        currentLabelColor = new Color(255, 100, 130);
                        currentTextColor = Color.BLACK;
                        break;
                    case "Sage Green":
                        currentBackgroundColor = new Color(173, 191, 149);
                        currentButtonColor = new Color(191, 214, 171);
                        currentLabelColor = new Color(100, 130, 91);
                        currentTextColor = Color.BLACK;
                        break;
                    case "Charcoal":
                        currentBackgroundColor = new Color(54, 69, 79);
                        currentButtonColor = new Color(80, 100, 115);
                        currentLabelColor = new Color(200, 200, 200);
                        currentTextColor = Color.WHITE;
                        break;
                    case "Navy Blue":
                        currentBackgroundColor = new Color(35, 41, 122);
                        currentButtonColor = new Color(56, 65, 180);
                        currentLabelColor = new Color(255, 255, 255);
                        currentTextColor = Color.WHITE;
                        break;
                }

                frame.getContentPane().setBackground(currentBackgroundColor);
                updateComponentColors(frame);
            }
        });


        updateFontSize(fontSize);

        // Increase font size action listener
        increaseFontButton.addActionListener(e -> {
            if(fontSize<40) {
                fontSize += 2; // Increase by 2
            }
            updateFontSize(fontSize);
        });

        // Decrease font size action listener
        decreaseFontButton.addActionListener(e -> {
            if (fontSize > 10) { // Prevent font size from going too small
                fontSize -= 2; // Decrease by 2
                updateFontSize(fontSize);
            }
        });





        frame.setSize(1200, 800);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }


    // Method to update the font size of text components
    private static void updateFontSize(int newSize) {
        t.setFont(new Font("Arial", Font.BOLD, newSize));
        l.setFont(new Font("Arial", Font.BOLD, newSize + 20)); // Larger label font
    }








    private static void setupSystemTray(JFrame frame) {
        if (!SystemTray.isSupported()) {
            System.out.println("System tray is not supported!");
            return;
        }

        Image image = Toolkit.getDefaultToolkit().getImage("icon.png"); // Use a valid image path
        trayIcon = new TrayIcon(image, "Alarm Clock");
        trayIcon.setImageAutoSize(true);

        SystemTray tray = SystemTray.getSystemTray();

        MenuItem openItem = new MenuItem("Open");
        openItem.addActionListener(e -> SwingUtilities.invokeLater(() -> frame.setVisible(true)));

        MenuItem exitItem = new MenuItem("Exit");
        exitItem.addActionListener(e -> {
            tray.remove(trayIcon);
            System.exit(0);
        });

        PopupMenu popup = new PopupMenu();
        popup.add(openItem);
        popup.add(exitItem);

        trayIcon.setPopupMenu(popup);
        trayIcon.addActionListener(e -> SwingUtilities.invokeLater(() -> frame.setVisible(true)));

        try {
            tray.add(trayIcon);
        } catch (AWTException e) {
            System.err.println("TrayIcon could not be added.");
        }

        frame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowIconified(java.awt.event.WindowEvent e) {
                frame.setVisible(false);
            }
        });
    }

    private static void showAlarmNotification(String message) {
        trayIcon.displayMessage("Alarm Notification", message, TrayIcon.MessageType.INFO);
    }

    private static String convertTo24HourFormat(String time, String amPm) {
        try {
            int hour = Integer.parseInt(time.substring(0, 2));
            if (amPm.equals("AM")) {
                if (hour == 12) {
                    hour = 0;
                }
            } else if (amPm.equals("PM")) {
                if (hour < 12) {
                    hour += 12;
                }
            }
            String minuteSecond = time.length() > 5 ? time.substring(2) : ":00";
            return String.format("%02d%s", hour, minuteSecond);
        } catch (Exception e) {
            return time;
        }
    }

    private static String convertTo12HourFormat(String time24) {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("HH:mm:ss");
            SimpleDateFormat outputFormat = new SimpleDateFormat("hh:mm:ss a");
            Date date = inputFormat.parse(time24);
            return outputFormat.format(date);
        } catch (Exception e) {
            return time24;
        }
    }

    private static void updateComponentColors(JFrame frame) {
        for (Component comp : frame.getContentPane().getComponents()) {
            if (comp instanceof JButton) {
                comp.setBackground(currentButtonColor);
                comp.setForeground(currentTextColor);
            } else if (comp instanceof JLabel) {
                comp.setForeground(currentLabelColor);
            } else if (comp instanceof JTextField) {
                comp.setForeground(currentTextColor);
                comp.setBackground(currentButtonColor);
            } else if (comp instanceof JComboBox) {
                comp.setBackground(currentButtonColor);
                comp.setForeground(currentTextColor);
            }
        }
    }

    public static class Alarm {
        String time; // Store time in HH:mm format
        ArrayList<String> repeatDays;
        String label;
        boolean hasRung;

        public Alarm(String time, ArrayList<String> repeatDays, String label) {
            this.time = time; // Accept time in HH:mm
            this.repeatDays = repeatDays;
            this.label = label;
            this.hasRung = false;
        }
    }

}