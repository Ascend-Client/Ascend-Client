package io.github.betterclient.fabric.relocate.loader.api.metadata;

import io.github.betterclient.fabric.relocate.api.EnvType;

public enum ModEnvironment {
	CLIENT,
	SERVER,
	UNIVERSAL;

	public boolean matches(EnvType type) {
        return switch (this) {
            case CLIENT -> type == EnvType.CLIENT;
            case SERVER -> type == EnvType.SERVER;
            case UNIVERSAL -> true;
        };
	}
}