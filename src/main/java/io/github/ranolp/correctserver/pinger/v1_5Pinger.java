package io.github.ranolp.correctserver.pinger;

import io.github.ranolp.correctserver.PingResult;
import io.github.ranolp.correctserver.Pinger;
import io.github.ranolp.correctserver.minecraft.Motd;
import io.github.ranolp.correctserver.minecraft.PlayerList;
import io.github.ranolp.correctserver.minecraft.ServerStatus;
import io.github.ranolp.correctserver.minecraft.Version;
import io.github.ranolp.correctserver.util.MinecraftSocket;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;

public class v1_5Pinger implements Pinger {
    private v1_5Pinger() {
    }

    private interface SingletonHolder {
        v1_5Pinger INSTANCE = new v1_5Pinger();
    }

    public static v1_5Pinger getInstance() {
        return SingletonHolder.INSTANCE;
    }

    @Override
    public PingResult ping(String host, short port) {
        try (MinecraftSocket socket = new MinecraftSocket()) {
            socket.connect(host, port);

            // HandShake Packet
            socket.write(new byte[] {(byte) 0xFE, (byte) 0x01});
            try (InputStreamReader reader = new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_16BE)) {
                int id = socket.read();
                if (id != 0xFF) {
                    return PingResult.fail(ServerStatus.PACKET_INVALID);
                }
                int len = reader.read();
                if (len < 1) {
                    return PingResult.fail(ServerStatus.CANT_CONNECT);
                }
                char[] chars = new char[len];
                if (reader.read(chars, 0, len) != len) {
                    return PingResult.fail(ServerStatus.CANT_CONNECT);
                }
                String s = new String(chars);
                if (s.startsWith("§")) {
                    String[] array = s.substring(1).split("\0");
                    int pingVersion = Integer.parseInt(array[0]);
                    return PingResult.success(null, new Version(array[2], Integer.parseInt(array[1])),
                                              new PlayerList(Integer.parseInt(array[4]), Integer.parseInt(array[5])),
                                              new Motd(array[3]), "");
                }
            }
        } catch (ConnectException | SocketTimeoutException e) {
            return PingResult.fail(ServerStatus.TIMEOUT);
        } catch (UnknownHostException e) {
            return PingResult.fail(ServerStatus.UNKNOWN_HOST);
        } catch (SecurityException | IOException e) {
            e.printStackTrace();
        }
        return PingResult.fail(ServerStatus.OFFLINE);
    }
}
