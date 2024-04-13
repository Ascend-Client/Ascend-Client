package io.github.betterclient.client.event.impl;

import io.github.betterclient.client.bridge.IBridge;
import io.github.betterclient.client.event.Event;
import io.github.betterclient.client.bridge.IBridge.*;

import java.util.concurrent.atomic.AtomicReference;

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
        AtomicReference<Double> max = new AtomicReference<>(0D);

        maxPos = compareTo(camera, maxPos.add(0, 0, receiving.getBox().maxZ), max);
        maxPos = compareTo(camera, maxPos.add(0, 0, receiving.getBox().minZ), max);
        maxPos = compareTo(camera, maxPos.add(0, receiving.getBox().maxY, 0), max);
        maxPos = compareTo(camera, maxPos.add(0, receiving.getBox().minY, 0), max);
        maxPos = compareTo(camera, maxPos.add(receiving.getBox().maxX, 0, 0), max);
        compareTo(camera, maxPos.add(receiving.getBox().minX, 0, 0), max);

        double d = max.get() + .5;
        Vec3d possibleHits = camera.add(rotation.x * d, rotation.y * d, rotation.z * d);
        BoundingBox box = attacking.getBox().stretch(rotation.multiply(d)).expand(1.0, 1.0, 1.0);

        RaycastResult raycast = IBridge.getInstance().getClient().raycast(attacking, camera, possibleHits, box, receiving.getID(), d);
        if (raycast == null || raycast.entity == null) {
            return -1;
        }
        return camera.distanceTo(raycast.pos);
    }

    private Vec3d compareTo(Vec3d compare, Vec3d test, AtomicReference<Double> max) {
        double dist = compare.distanceTo(test);
        if (dist > max.get()) {
            max.set(dist);
            return test;
        }
        return compare;
    }
}
