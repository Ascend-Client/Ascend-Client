package net.fabricmc.fabric.api.event;

import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

public abstract class Event<T> {
    public List<T> registrars = new ArrayList<>();

    public abstract void register(T listener);

    public void register(Identifier phase, T listener) {
        register(listener);
    }

    public void addPhaseOrdering(Identifier firstPhase, Identifier secondPhase) {}
}
