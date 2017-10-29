package io.github.ranolp.correctserver.pinger;

import io.github.ranolp.correctserver.PingResult;
import io.github.ranolp.correctserver.Pinger;
import io.github.ranolp.correctserver.minecraft.ServerStatus;

import java.time.Instant;
import java.util.*;

public final class CompoundPinger implements Pinger {
    private final Set<Pinger> pingers;

    public CompoundPinger(Collection<Pinger> pingers) {
        this.pingers = Collections.unmodifiableSet(new HashSet<>(pingers));
    }

    public CompoundPinger(Pinger... pingers) {
        this(Arrays.asList(pingers));
    }

    @Override
    public PingResult ping(String host, short port) {
        Instant start = Instant.now();
        for (Pinger pinger : pingers) {
            PingResult result = pinger.ping(host, port);
            if (result.isAlive()) {
                return PingResult.success(result.getSoftware(), result.getVersion(), result.getPlayerList(),
                                          result.getMotd(), result.getBase64Icon());
            }
        }
        return PingResult.fail(ServerStatus.OFFLINE);
    }

    public Set<Pinger> getPingers() {
        return pingers;
    }
}
