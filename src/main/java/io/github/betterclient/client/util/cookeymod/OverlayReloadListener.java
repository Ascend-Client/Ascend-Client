package io.github.betterclient.client.util.cookeymod;

import java.util.ArrayList;
import java.util.List;

public interface OverlayReloadListener {
    List<OverlayReloadListener> LISTENERS = new ArrayList<>();

    void onOverlayReload();

    static void register(OverlayReloadListener listener) {
        LISTENERS.add(listener);
    }

    static void callEvent() {
        for (OverlayReloadListener listener : LISTENERS) {
            listener.onOverlayReload();
        }
    }
}