package io.github.betterclient.client.event.impl;

import com.google.common.util.concurrent.AtomicDouble;
import io.github.betterclient.client.event.Event;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

public class HitEntityEvent extends Event {
    public PlayerEntity damager;
    public Entity damagedEntity;
    public double distance;

    public HitEntityEvent(PlayerEntity damager, Entity damagedEntity) {
        this.damager = damager;
        this.damagedEntity = damagedEntity;
        this.distance = this.getAttackDistance(damager, damagedEntity);
    }

    public double getAttackDistance(Entity attacking, Entity receiving) {
        Vec3d camera = attacking.getCameraPosVec(1);
        Vec3d rotation = attacking.getRotationVec(1);

        Vec3d maxPos = receiving.getPos();
        AtomicDouble max = new AtomicDouble(0);

        maxPos = compareTo(camera, maxPos.add(0, 0, receiving.getBoundingBox().maxZ), max);
        maxPos = compareTo(camera, maxPos.add(0, 0, receiving.getBoundingBox().minZ), max);
        maxPos = compareTo(camera, maxPos.add(0, receiving.getBoundingBox().maxY, 0), max);
        maxPos = compareTo(camera, maxPos.add(0, receiving.getBoundingBox().minY, 0), max);
        maxPos = compareTo(camera, maxPos.add(receiving.getBoundingBox().maxX, 0, 0), max);
        maxPos = compareTo(camera, maxPos.add(receiving.getBoundingBox().minX, 0, 0), max);

        double d = max.get() + .5;
        Vec3d possibleHits = camera.add(rotation.x * d, rotation.y * d, rotation.z * d);
        Box box = attacking.getBoundingBox().stretch(rotation.multiply(d)).expand(1.0, 1.0, 1.0);

        EntityHitResult result = ProjectileUtil.raycast(attacking, camera, possibleHits, box,
                entity -> entity.getId() == receiving.getId(), d);
        if (result == null || result.getEntity() == null) {
            return -1;
        }
        return camera.distanceTo(result.getPos());
    }

    private Vec3d compareTo(Vec3d compare, Vec3d test, AtomicDouble max) {
        double dist = compare.distanceTo(test);
        if (dist > max.get()) {
            max.set(dist);
            return test;
        }
        return compare;
    }
}
