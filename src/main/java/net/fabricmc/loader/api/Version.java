package net.fabricmc.loader.api;

public interface Version extends Comparable<Version> {
	default String getFriendlyString() {
		return "1.0";
	}

	static Version parse(String str) {
		return new CoolVersion(str);
	}
}