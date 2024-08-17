package io.github.betterclient.fabric.relocate.loader.impl.gui;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
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

	public static final class FabricStatusNode {

		public FabricStatusNode(DataInputStream inputStream, String name) {

		}

		public FabricStatusNode(String dataInputStream, DataInputStream is) {

		}

		public void writeTo(DataOutputStream os) {
			System.out.println(os);
		}

		public FabricStatusNode addChild(String name) {
			System.out.println("added child" + name);
			return new FabricStatusNode(name, null);
		}
	}

	public static final class FabricStatusTab {
		public final FabricStatusNode node;

		/** The minimum warning level to display for this tab. */
		public FabricTreeWarningLevel filterLevel = FabricTreeWarningLevel.NONE;

		public FabricStatusTab(String name) {
			this.node = new FabricStatusNode(null, name);
		}

		public FabricStatusTab(DataInputStream is) throws IOException {
			node = new FabricStatusNode(null, is);
			filterLevel = FabricTreeWarningLevel.valueOf(is.readUTF());
		}

		public void writeTo(DataOutputStream os) throws IOException {
			node.writeTo(os);
			os.writeUTF(filterLevel.name());
		}

		public FabricStatusNode addChild(String name) {
			return node.addChild(name);
		}
	}
}