package io.github.betterclient.fabric.relocate.loader.api;

import java.util.function.BiConsumer;

public interface ObjectShare {
	Object get(String key);
	void whenAvailable(String key, BiConsumer<String, Object> consumer);
	Object put(String key, Object value);
	Object putIfAbsent(String key, Object value);
	Object remove(String key);
}