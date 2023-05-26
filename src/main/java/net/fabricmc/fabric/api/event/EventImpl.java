package net.fabricmc.fabric.api.event;

public class EventImpl<T> extends Event<T> {
    @Override
    public void register(T listener) {
        this.registrars.add(listener);
    }
}
