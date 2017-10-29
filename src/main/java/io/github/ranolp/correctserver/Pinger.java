package io.github.ranolp.correctserver;

import java.net.InetSocketAddress;

public interface Pinger {
    PingResult ping(String host, short port);

    default PingResult ping(InetSocketAddress address) {
        return ping(address.getHostName(), (short) address.getPort());
    }

    default PingResult ping(String host, int port) {
        return ping(host, (short) port);
    }

    default PingResult ping(String host) {
        return ping(host, 25565);
    }
}
