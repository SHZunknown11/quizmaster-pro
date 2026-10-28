package com.quizmaster;

import com.quizmaster.view.MainWindow;

import javax.swing.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Application Entry Point for QuizMaster Pro.
 * Configures font anti-aliasing, locates the data directory,
 * and launches MainWindow on the Swing Event Dispatch Thread (EDT).
 */
public class Main {

    public static void main(String[] args) {
        // High quality LCD font anti-aliasing properties
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");

        // Try to set FlatLaf if on classpath, or System LookAndFeel as fallback
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
            // Standard fallback
        }

        // Locate data directory
        Path dataPath = Paths.get("data");
        if (!Files.exists(dataPath)) {
            // Check parent directory
            Path parentData = Paths.get("..", "data");
            if (Files.exists(parentData)) {
                dataPath = parentData;
            }
        }

        final Path finalDataPath = dataPath;

        SwingUtilities.invokeLater(() -> {
            try {
                MainWindow window = new MainWindow(finalDataPath);
                window.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null,
                        "Failed to launch QuizMaster Pro:\n" + e.getMessage(),
                        "Launch Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}
