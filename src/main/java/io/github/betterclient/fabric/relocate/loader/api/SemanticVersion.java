package io.github.betterclient.fabric.relocate.loader.api;

import io.github.betterclient.fabric.api.CoolVersion;

import java.util.Optional;

public interface SemanticVersion extends Version {
	int COMPONENT_WILDCARD = Integer.MIN_VALUE;
	int getVersionComponentCount();
	int getVersionComponent(int pos);
	Optional<String> getPrereleaseKey();
	Optional<String> getBuildKey();
	boolean hasWildcard();

	@Deprecated
	default int compareTo(SemanticVersion o) {
		return compareTo((Version) o);
	}

	static SemanticVersion parse(String s) throws VersionParsingException {
		return new CoolVersion(s);
	}
}