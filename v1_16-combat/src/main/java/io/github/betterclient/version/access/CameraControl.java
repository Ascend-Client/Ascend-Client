package io.github.betterclient.version.access;

public interface CameraControl {

    float getCameraPitch();

    float getCameraYaw();

    void setCameraPitch(float pitch);

    void setCameraYaw(float yaw);
}
