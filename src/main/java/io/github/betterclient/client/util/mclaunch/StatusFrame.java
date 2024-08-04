package io.github.betterclient.client.util.mclaunch;

import io.github.betterclient.client.Application;
import io.github.betterclient.client.util.modremapper.utility.ModLoadingInformation;
import io.github.betterclient.fabric.FabricLoader;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class StatusFrame extends JFrame {

    public static StatusFrame instance = null;
    public JLabel stateLabel,
            modLabel,
            progressLabel,
            percentileLabel;
    public JProgressBar progressBar;
    public int progressCurrent = 1, progressTotal = 1;

    public StatusFrame() {
        setTitle("Mod remapping");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setPreferredSize(new Dimension(450, 150));

        JPanel panel = new JPanel();
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        panel.setLayout(new BorderLayout());

        stateLabel = new JLabel("State: " + Application.modLoadingInformation.state().getName());
        File currentMod = Application.modLoadingInformation.currentMod();
        modLabel = new JLabel("Current mod: " + (currentMod == null ? "..." : ModLoadingInformation.isBuiltin ? "Builtin mod" : currentMod.getName()));
        progressLabel = new JLabel("Progress: " + progressCurrent + "/" + progressTotal);
        percentileLabel = new JLabel("0% complete");

        progressBar = new JProgressBar(0, progressTotal);
        progressBar.setValue(progressCurrent);
        progressBar.setOrientation(JProgressBar.HORIZONTAL);
        progressBar.setPreferredSize(new Dimension(100, 20));

        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.add(stateLabel);
        infoPanel.add(modLabel);
        infoPanel.add(progressLabel);
        infoPanel.add(percentileLabel);

        panel.add(infoPanel, BorderLayout.NORTH);
        panel.add(progressBar, BorderLayout.SOUTH);

        add(panel, BorderLayout.CENTER);

        pack();
        setVisible(true);
        setAlwaysOnTop(true);
        instance = this;
        Application.statusFrame.set(this);
    }

    public void update(ModLoadingInformation mli) throws IOException {
        if(mli.currentMod() == null) {
            progressCurrent = 1;
            progressTotal = 1;
            updateLabels(mli);
            return;
        }

        if(mli.state().equals(ModLoadingInformation.State.LOADING_BUILTIN)) {
            if(!ModLoadingInformation.isBuiltin) {
                progressTotal = mli.nonCustomMods().size();
                progressCurrent = mli.nonCustomMods().indexOf(mli.currentMod()) + 1;
            }

        } else if(mli.state().equals(ModLoadingInformation.State.LOADING_CUSTOM)) {
            List<File> files = new ArrayList<>();

            for (File file : Objects.requireNonNullElse(Application.customJarsFolder.listFiles(), new File[0])) {
                if(file.getName().endsWith(".jar")) {
                    files.add(file);
                }
            }

            if(!ModLoadingInformation.isBuiltin) {
                progressTotal = files.size();
                progressCurrent = files.indexOf(mli.currentMod()) + 1;
            }

        }

        updateLabels(mli);
    }

    private void updateLabels(ModLoadingInformation mli) throws IOException {
        stateLabel.setText("State: " + mli.state().getName());
        File currentMod = mli.currentMod();
        modLabel.setText("Current mod: " + (currentMod == null ? "..." : FabricLoader.getInstance().getModName(currentMod)));
        progressLabel.setText("Progress: " + progressCurrent + "/" + progressTotal);
        percentileLabel.setText(mapTo100(progressCurrent, progressTotal) + "% complete");

        progressBar.setMaximum(progressTotal);
        progressBar.setValue(progressCurrent);
    }

    static int mapTo100(int val, int max) {
        return val * 100 / max;
    }
}
