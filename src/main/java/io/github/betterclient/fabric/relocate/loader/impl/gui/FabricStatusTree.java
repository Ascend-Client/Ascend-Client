package io.github.betterclient.fabric.relocate.loader.impl.gui;

import java.util.Locale;

public final class FabricStatusTree {
	public enum FabricTreeWarningLevel {
		ERROR,
		WARN,
		INFO,
		NONE;

		public final String lowerCaseName = name().toLowerCase(Locale.ROOT);

		public boolean isHigherThan(FabricTreeWarningLevel other) {
			return ordinal() < other.ordinal();
		}

		public boolean isAtLeast(FabricTreeWarningLevel other) {
			return ordinal() <= other.ordinal();
		}

		public static FabricTreeWarningLevel getHighest(FabricTreeWarningLevel a, FabricTreeWarningLevel b) {
			return a.isHigherThan(b) ? a : b;
		}
	}
}