package io.github.ranolp.correctserver.minecraft;

import java.util.Collections;
import java.util.List;

public final class PlayerList {
    public static final PlayerList EMPTY = new PlayerList(Collections.emptyList(), 0, 0);
    private final List<Player> players;
    private final int current;
    private final int max;

    public PlayerList(List<Player> players, int current, int max) {
        this.players = Collections.unmodifiableList(players);
        this.current = current;
        this.max = max;
    }

    public PlayerList(int current, int max) {
        this(Collections.emptyList(), current, max);
    }

    public List<Player> getPlayers() {
        return players;
    }

    public int getPlayerCount() {
        return current;
    }

    public int getMaxPlayers() {
        return max;
    }
}
