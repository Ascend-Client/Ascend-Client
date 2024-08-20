package io.github.betterclient.version.access;

import net.minecraft.util.math.Vec3d;

/**
 * Code "borrowed" from: <a href="https://github.com/Draylar/better-dropped-items/blob/1.16.2/src/main/java/bdi/util/ItemEntityRotator.java">...</a>
 */
public interface ItemEntityRotator {
    Vec3d getRotation();
    void setRotation(Vec3d rotation);
    void addRotation(Vec3d rotation);
    void addRotation(double x, double y, double z);
}