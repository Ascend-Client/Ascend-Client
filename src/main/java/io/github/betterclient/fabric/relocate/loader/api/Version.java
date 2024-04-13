package io.github.betterclient.fabric.relocate.loader.api;

import io.github.betterclient.fabric.api.CoolVersion;

public interface Version extends Comparable<Version> {
	default String getFriendlyString() {
		return "1.0";
	}

	static Version parse(String str) {
		return new CoolVersion(str);
	}
}