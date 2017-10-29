package io.github.ranolp.correctserver.minecraft;

import java.util.UUID;

public class Player {
    private final String nickname;
    private final UUID uuid;

    public Player(String nickname, String uuid) {
        this.nickname = nickname;
        this.uuid = UUID.fromString(uuid);
    }
}