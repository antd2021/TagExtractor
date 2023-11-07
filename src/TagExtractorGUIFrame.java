import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.Map;


public class TagExtractorGUIFrame extends JFrame {

    private JTextArea resultsTextArea;
    private File selectedFile;
    private Set<String> stopWords;
    private Map<String, Integer> tagFrequencyMap;

    public TagExtractorGUIFrame() {
        setTitle("Tag Extractor");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        createGUI();
        setVisible(true);
    }

    private void createGUI() {
        // Title
        JLabel titleLabel = new JLabel("Tag Extractor");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(titleLabel, BorderLayout.NORTH);

        // Results Panel
        JPanel resultsPanel = new JPanel();
        resultsPanel.setBorder(BorderFactory.createTitledBorder("Tag Extraction Results"));

        resultsTextArea = new JTextArea(20, 50);
        resultsTextArea.setEditable(false);
        resultsPanel.add(new JScrollPane(resultsTextArea));
        add(resultsPanel, BorderLayout.CENTER);

        // Buttons Panel
        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setBorder(BorderFactory.createTitledBorder("Actions"));
        buttonsPanel.setLayout(new GridLayout(1, 5));

        JButton openFileButton = new JButton("Open Text File");
        JButton openStopWordsButton = new JButton("Open Stop Words File");
        JButton extractTagsButton = new JButton("Extract Tags");
        JButton saveTagsButton = new JButton("Save Tags to File");
        JButton quit = new JButton("Quit");

        buttonsPanel.add(openFileButton);
        buttonsPanel.add(openStopWordsButton);
        buttonsPanel.add(extractTagsButton);
        buttonsPanel.add(saveTagsButton);
        buttonsPanel.add(quit);

        add(buttonsPanel, BorderLayout.SOUTH);


// Action Listeners for Buttons

        quit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int response = JOptionPane.showConfirmDialog(TagExtractorGUIFrame.this, "Are you sure you want to quit?", "Confirm", JOptionPane.YES_NO_OPTION);
                if (response == JOptionPane.YES_OPTION) {
                    System.exit(0);
                }
            }
        });

        openFileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setCurrentDirectory(new File(System.getProperty("user.dir")));

                int returnValue = fileChooser.showOpenDialog(null);

                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    selectedFile = fileChooser.getSelectedFile();
                    resultsTextArea.setText("Selected file: " + selectedFile.getAbsolutePath() + "\n");
                }
            }
        });

        openStopWordsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setCurrentDirectory(new File(System.getProperty("user.dir")));

                int returnValue = fileChooser.showOpenDialog(null);

                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    File stopWordsFile = fileChooser.getSelectedFile();
                    try {
                        stopWords = new HashSet<>();
                        BufferedReader reader = new BufferedReader(new FileReader(stopWordsFile));
                        String line;
                        while ((line = reader.readLine()) != null) {
                            stopWords.add(line.toLowerCase());
                        }
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });

        extractTagsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (selectedFile != null && stopWords != null) {
                    tagFrequencyMap = new HashMap<>();
                    try {
                        BufferedReader reader = new BufferedReader(new FileReader(selectedFile));
                        String line;
                        while ((line = reader.readLine()) != null) {
                            String[] words = line.split("\\s+");
                            for (String word : words) {
                                word = word.replaceAll("[^a-zA-Z]", "").toLowerCase();
                                if (!stopWords.contains(word)) {
                                    tagFrequencyMap.put(word, tagFrequencyMap.getOrDefault(word, 0) + 1);
                                }
                            }
                        }
                        resultsTextArea.setText("Tag Extraction complete." + "\n");
                        for (Map.Entry<String, Integer> entry : tagFrequencyMap.entrySet()) {
                            resultsTextArea.append(entry.getKey() + ": " + entry.getValue() + "\n");
                        }
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                } else {
                    resultsTextArea.setText("Please select a text file and stop words file first.");
                }
            }
        });

        saveTagsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (tagFrequencyMap != null && !tagFrequencyMap.isEmpty()) {
                    JFileChooser fileChooser = new JFileChooser();
                    fileChooser.setCurrentDirectory(new File(System.getProperty("user.dir")));

                    int returnValue = fileChooser.showSaveDialog(null);

                    if (returnValue == JFileChooser.APPROVE_OPTION) {
                        File outputFile = fileChooser.getSelectedFile();
                        try (PrintWriter writer = new PrintWriter(outputFile)) {
                            for (Map.Entry<String, Integer> entry : tagFrequencyMap.entrySet()) {
                                writer.println(entry.getKey() + ": " + entry.getValue());
                            }
                            resultsTextArea.setText("Tags saved to file: " + outputFile.getAbsolutePath());
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    }
                } else {
                    resultsTextArea.setText("No tags to save. Please perform tag extraction first.");
                }
            }
        });

    }

    // Method to open and load the stop words file
    private void openStopWordsFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File(System.getProperty("user.dir")));

        int returnValue = fileChooser.showOpenDialog(null);

        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File stopWordsFile = fileChooser.getSelectedFile();
            try {
                Set<String> stopWordsSet = new HashSet<>();
                BufferedReader reader = new BufferedReader(new FileReader(stopWordsFile));
                String line;
                while ((line = reader.readLine()) != null) {
                    stopWordsSet.add(line.toLowerCase());
                }
                stopWords = stopWordsSet;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void extractTags() {
        if (selectedFile != null && stopWords != null) {
            try {
                tagFrequencyMap = new HashMap<>();
                BufferedReader reader = new BufferedReader(new FileReader(selectedFile));
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] words = line.split("\\s+");
                    // Remove non-letter characters and force lowercase
                    for (String word : words) {
                        word = word.replaceAll("[^a-zA-Z]", "").toLowerCase();
                        if (!stopWords.contains(word)) {
                            tagFrequencyMap.put(word, tagFrequencyMap.getOrDefault(word, 0) + 1);
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            resultsTextArea.setText("Please select a text file and stop words file first.");
        }
    }

    private void saveTagsToFile() {
        if (tagFrequencyMap != null && !tagFrequencyMap.isEmpty()) {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setCurrentDirectory(new File(System.getProperty("user.dir")));

            int returnValue = fileChooser.showSaveDialog(null);

            if (returnValue == JFileChooser.APPROVE_OPTION) {
                File outputFile = fileChooser.getSelectedFile();
                try (PrintWriter writer = new PrintWriter(outputFile)) {
                    for (Map.Entry<String, Integer> entry : tagFrequencyMap.entrySet()) {
                        writer.println(entry.getKey() + ": " + entry.getValue());
                    }
                    resultsTextArea.setText("Tags saved to file: " + outputFile.getAbsolutePath());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else {
            resultsTextArea.setText("No tags to save. Please perform tag extraction first.");
        }
    }
}
