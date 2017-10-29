package io.github.ranolp.correctserver;

import io.github.ranolp.correctserver.minecraft.*;

import java.util.EnumMap;
import java.util.Objects;

public final class PingResult {
    private final boolean alive;
    private final ServerStatus status;
    private final ServerSoftware software;
    private final Version version;
    private final PlayerList playerList;
    private final Motd motd;
    private final String base64Icon;
    private static final EnumMap<ServerStatus, PingResult> failPool = new EnumMap<>(ServerStatus.class);

    private PingResult(boolean alive, ServerStatus status, ServerSoftware software, Version version,
                       PlayerList playerList, Motd motd, String base64Icon) {
        this.alive = alive;
        this.status = status;
        this.software = software;
        this.version = version;
        this.playerList = playerList;
        this.motd = motd;
        this.base64Icon = base64Icon;
    }

    public static PingResult success(ServerSoftware software, Version version, PlayerList playerList, Motd motd,
                                     String base64Icon) {
        return new PingResult(true, ServerStatus.ONLINE, software, version, playerList, motd, base64Icon);
    }

    public static PingResult fail(ServerStatus status) {
        return failPool.computeIfAbsent(status,
                                        s -> new PingResult(false, s, null, null, PlayerList.EMPTY, Motd.NOTHING, ""));
    }

    /**
     * @return Whether the server is alive
     */
    public boolean isAlive() {
        return alive;
    }

    public ServerStatus getStatus() {
        return status;
    }

    public ServerSoftware getSoftware() {
        return software;
    }

    public Version getVersion() {
        return version;
    }

    public PlayerList getPlayerList() {
        return playerList;
    }

    public Motd getMotd() {
        return motd;
    }

    public String getBase64Icon() {
        return base64Icon;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (!(o instanceof PingResult)) { return false; }
        PingResult that = (PingResult) o;
        return alive == that.alive &&
               status == that.status &&
               Objects.equals(software, that.software) &&
               Objects.equals(version, that.version) &&
               Objects.equals(playerList, that.playerList) &&
               Objects.equals(motd, that.motd) &&
               Objects.equals(base64Icon, that.base64Icon);
    }

    @Override
    public int hashCode() {
        return Objects.hash(alive, status, software, version, playerList, motd, base64Icon);
    }

    @Override
    public String toString() {
        return "PingResult{" +
               "alive=" +
               alive +
               ", status=" +
               status +
               ", software=" +
               software +
               ", version=" +
               version +
               ", playerList=" +
               playerList +
               ", motd=" +
               motd +
               ", base64Icon='" +
               base64Icon +
               '\'' +
               '}';
    }
}
