package io.github.betterclient.fabric;

import io.github.betterclient.client.Application;

import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.lang.reflect.Field;

public class FabricErrorReporter {
    public String errorName;
    public Exception exception;
    public String modName;

    public static FabricErrorReporter noException(String modName) {
        return new FabricErrorReporter(modName, "No exception was thrown, but mod loading failed (??).", new IllegalStateException("?"));
    }

    public static FabricErrorReporter exception(String modName, Exception e) {
        return new FabricErrorReporter(modName, "Mod loading failed.", e);
    }

    public FabricErrorReporter(String modName, String errorName, Exception exception) {
        this.modName = modName;
        this.errorName = errorName;
        this.exception = exception;
    }

    private boolean saveToFile(StringBuilder error, File f) {
        String s = f.getAbsolutePath();

        try {
            if(!f.createNewFile()) {
                return false;
            }

            Writer w = new FileWriter(f);

            w.append(error.toString());

            w.close();
        } catch (Exception e) {
            return false;
        }

        error.append("\n Error also saved to: ").append(s);
        return true;
    }

    public void print() {
        System.err.println(this);
    }

    @Override
    public String toString() {
        StringBuilder error = new StringBuilder("Ascend client error report\n");
        error.append("Mod \"").append(this.modName).append("\" failed loading with error: \n");
        error.append(this.errorName).append("\n");
        error.append(this.exception.getLocalizedMessage()).append("\n");
        error.append("\nStacktrace: \n");

        for (StackTraceElement stackTraceElement : this.exception.getStackTrace()) {
            error.append("      ").append(stackTraceElement.toString()).append("\n");
        }

        if(exception.getCause() != null) {
            error.append("Cause: \n");

            error.append(exception.toString() + "\n\n");
        }

        File savedError = new File(Application.errorsFolder, "fabric-error-" + System.currentTimeMillis() + ".txt");
        if(!saveToFile(error, savedError)) {
            error.append("Failed to save error to file.");
        }

        return error.toString();
    }
}
