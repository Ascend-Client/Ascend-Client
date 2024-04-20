package io.github.betterclient.client.autoupdater;

import com.google.gson.*;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class GitHubArtifactDownloader {
    public static File download(File to) {
        try {
            String latestArtifactUrl = getLatestArtifactUrl();
            if (latestArtifactUrl != null) {
                downloadArtifact(latestArtifactUrl, to);
            } else {
                System.out.println("Failed to get the latest artifact URL.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        to.deleteOnExit();
        return to;
    }

    private static String getLatestArtifactUrl() throws IOException {
        String apiUrl = "https://api.github.com/repos/betterclient/Minecraft-Client/actions/workflows/commit.yml/runs";
        URL url = new URL(apiUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Accept", "application/vnd.github.v3+json");

        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        reader.close();

        JsonObject jsonResponse = JsonParser.parseString(response.toString()).getAsJsonObject();
        JsonArray workflowRuns = jsonResponse.getAsJsonArray("workflow_runs");
        if (workflowRuns.size() > 0) {
            JsonObject latestRun = workflowRuns.get(0).getAsJsonObject();
            String artifactsUrl = latestRun.get("artifacts_url").getAsString();
            return getArtifactDownloadUrl(artifactsUrl);
        }
        return null;
    }

    private static String getArtifactDownloadUrl(String artifactsUrl) throws IOException {
        URL url = new URL(artifactsUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Accept", "application/vnd.github.v3+json");

        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        reader.close();
        JsonObject jsonResponse = JsonParser.parseString(response.toString()).getAsJsonObject();

        JsonArray artifacts = jsonResponse.getAsJsonArray("artifacts");
        if (artifacts.size() > 0) {
            JsonObject latestArtifact = artifacts.get(0).getAsJsonObject();
            return latestArtifact.get("archive_download_url").getAsString();
        }
        return null;
    }

    private static void downloadArtifact(String downloadUrl, File to) throws IOException {
        URL url = new URL(downloadUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        //use dummy account token
        connection.setRequestProperty("Authorization", "Bearer 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");

        InputStream inputStream = connection.getInputStream();
        FileOutputStream outputStream = new FileOutputStream(to);
        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, bytesRead);
        }
        outputStream.close();
        inputStream.close();
        connection.disconnect();
    }
}