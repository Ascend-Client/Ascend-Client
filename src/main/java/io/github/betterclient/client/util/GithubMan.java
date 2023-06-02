package io.github.betterclient.client.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * github man
 */
public class GithubMan {
    public String commitId = "";
    public String branch = "";

    public GithubMan() {
        try {
            List<String> text = Files.readAllLines(toPath(GithubMan.class.getResourceAsStream("/ballsack/github/github.txt")));

            for(String line : text) {
                if(line.startsWith("git.branch=")) {
                    branch = line.replace("git.branch=", "");
                }

                if(line.startsWith("git.commit.id.abbrev=")) {
                    commitId = line.replace("git.commit.id.abbrev=", "");
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Path toPath(InputStream is) {
        try {
            File gitFile = File.createTempFile("git", ".txt");
            FileOutputStream fos = new FileOutputStream(gitFile);

            fos.write(is.readAllBytes());

            fos.close();
            is.close();

            return gitFile.toPath();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
