import javax.swing.*;

public class TagExtractorGUIRunner {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new TagExtractorGUIFrame();
        });
    }
}