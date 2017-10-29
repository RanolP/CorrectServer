package io.github.ranolp.correctserver.pinger;

import io.github.ranolp.correctserver.PingResult;
import io.github.ranolp.correctserver.Pinger;
import io.github.ranolp.correctserver.minecraft.*;
import io.github.ranolp.correctserver.util.MinecraftSocket;
import io.github.ranolp.correctserver.util.StringUtil;

import java.io.EOFException;
import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.Arrays;

public class v1_6Pinger implements Pinger {
    private v1_6Pinger() {
    }

    private interface SingletonHolder {
        v1_6Pinger INSTANCE = new v1_6Pinger();
    }

    public static v1_6Pinger getInstance() {
        return SingletonHolder.INSTANCE;
    }

    @Override
    public PingResult ping(String host, short port) {
        try (MinecraftSocket socket = new MinecraftSocket()) {
            socket.connect(host, port);

            // HandShake Packet.
            socket.write(0xFE);
            socket.write(0x01);
            socket.write(0xFA);
            socket.writeLegacyString("MC|PingHost");
            socket.writeShort(host.length() * 2 + 7);
            socket.write(74);
            socket.writeLegacyString(host);
            socket.writeInt(port);
            socket.flush();

            int packet = socket.readUnsignedByte();
            if (packet != 0xFF) {
                return PingResult.fail(ServerStatus.PACKET_INVALID);
            }
            String s = socket.readLegacyString();
            if (s.charAt(0) == '§') {
                String[] array = s.substring(1).split("\0");
                if (array.length > 3) {
                    String tempPlayers = array[array.length - 2];
                    String tempMaxPlayers = array[array.length - 1];
                    if (StringUtil.isInteger(tempPlayers, true) && StringUtil.isInteger(tempMaxPlayers, true)) {
                        PlayerList playerList = new PlayerList(Integer.parseInt(tempPlayers),
                                                               Integer.parseInt(tempMaxPlayers));
                        if (array.length >= 6) {
                            String tempPingVersion = array[0];
                            String tempProtocolVersion = array[1];
                            if (StringUtil.isInteger(tempPingVersion, true) &&
                                StringUtil.isInteger(tempProtocolVersion, true)) {
                                ServerSoftware software = null;
                                return PingResult.success(software,
                                                          new Version(array[2], Integer.parseInt(tempProtocolVersion)),
                                                          playerList, new Motd(
                                                String.join("§", Arrays.copyOfRange(array, 3, array.length - 2))), "");
                            }
                        }
                        return PingResult.success(null, null, playerList,
                                                  new Motd(String.join("§", Arrays.copyOf(array, array.length - 2))),
                                                  "");
                    }
                }
            }
        } catch (UnknownHostException e) {
            return PingResult.fail(ServerStatus.UNKNOWN_HOST);
        } catch (ConnectException | SocketTimeoutException e) {
            return PingResult.fail(ServerStatus.TIMEOUT);
        } catch (EOFException e) {
            return PingResult.fail(ServerStatus.CANT_CONNECT);
        } catch (SecurityException | IOException e) {
            e.printStackTrace();
        }

        return PingResult.fail(ServerStatus.OFFLINE);
    }
}
