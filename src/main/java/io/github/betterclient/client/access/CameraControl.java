package io.github.betterclient.client.access;

public interface CameraControl {
	float getCameraPitch();
	float getCameraYaw();

	void setCameraPitch(float pitch);
	void setCameraYaw(float yaw);
}