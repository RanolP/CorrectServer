package io.github.ranolp.correctserver.minecraft;

import java.util.Objects;

public final class Version {
    private final String name;
    private final int protocolVersion;

    public Version(String name, int protocolVersion) {
        this.name = name;
        this.protocolVersion = protocolVersion;
    }

    public String getName() {
        return name;
    }

    public int getProtocolVersion() {
        return protocolVersion;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (!(o instanceof Version)) { return false; }
        Version version = (Version) o;
        return protocolVersion == version.protocolVersion && Objects.equals(name, version.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, protocolVersion);
    }

    @Override
    public String toString() {
        return "Version{" + "name='" + name + '\'' + ", protocolVersion=" + protocolVersion + '}';
    }
}
