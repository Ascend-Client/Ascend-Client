package io.github.betterclient.client.event.impl;

import io.github.betterclient.client.event.Event;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;

public class HitEntityEvent extends Event {
    public PlayerEntity damager;
    public Entity damagedEntity;
    public double distance;

    public HitEntityEvent(PlayerEntity damager, Entity damagedEntity) {
        this.damager = damager;
        this.damagedEntity = damagedEntity;
        this.distance = damager.getPos().distanceTo(damagedEntity.getPos());
    }
}
