package io.github.betterclient.client.autoupdater;

import javax.swing.*;
import java.awt.*;

public class StatusFrame extends JFrame {
    public StatusFrame(String version) {
        super("Downloading");
        JLabel downloading = new JLabel("Downloading");
        JLabel versionLabel = new JLabel("Version: " + version);
        add(downloading);
        add(versionLabel);
        pack();
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setVisible(true);
        setAlwaysOnTop(true);
        setLayout(new FlowLayout());
        setSize(175, 100);
    }
}
