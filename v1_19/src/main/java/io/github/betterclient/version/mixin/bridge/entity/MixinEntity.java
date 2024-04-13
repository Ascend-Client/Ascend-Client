package io.github.betterclient.version.mixin.bridge.entity;

import io.github.betterclient.client.bridge.IBridge;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Entity.class)
public abstract class MixinEntity implements IBridge.Entity {
    @Shadow public abstract Vec3d getCameraPosVec(float tickDelta);

    @Shadow public abstract Vec3d getRotationVec(float tickDelta);

    @Shadow private Vec3d pos;

    @Shadow private Box boundingBox;

    @Shadow private int id;

    private IBridge.Vec3d translate(Vec3d vec) {
        return new IBridge.Vec3d(vec.x, vec.y, vec.z);
    }

    @Override
    public IBridge.Vec3d getCameraPosVec(int number) {
        return translate(this.getCameraPosVec(Float.valueOf(number)));
    }

    @Override
    public IBridge.Vec3d getRotationVec(int number) {
        return translate(this.getRotationVec(Float.valueOf(number)));
    }

    @Override
    public IBridge.Vec3d getPos() {
        return translate(this.pos);
    }

    @Override
    public IBridge.BoundingBox getBox() {
        return new IBridge.BoundingBox(
                this.boundingBox.minX,
                this.boundingBox.minY,
                this.boundingBox.minZ,
                this.boundingBox.maxX,
                this.boundingBox.maxY,
                this.boundingBox.maxZ
        );
    }

    @Override
    public int getID() {
        return this.id;
    }
}
