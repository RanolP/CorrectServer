package io.github.ranolp.correctserver.pinger;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.github.ranolp.correctserver.PingResult;
import io.github.ranolp.correctserver.Pinger;
import io.github.ranolp.correctserver.minecraft.*;
import io.github.ranolp.correctserver.util.JsonUtil;
import io.github.ranolp.correctserver.util.MinecraftSocket;
import io.github.ranolp.correctserver.util.MinecraftUtil;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class v1_7Pinger implements Pinger {
    private v1_7Pinger() {
    }

    private interface SingletonHolder {
        v1_7Pinger INSTANCE = new v1_7Pinger();
    }

    public static v1_7Pinger getInstance() {
        return SingletonHolder.INSTANCE;
    }

    @Override
    public PingResult ping(String host, short port) {
        Instant start = Instant.now();
        try (MinecraftSocket socket = new MinecraftSocket()) {
            socket.connect(host, port);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DataOutputStream handshake = new DataOutputStream(baos);
            handshake.writeByte(0x00);
            MinecraftUtil.writeVarInt(handshake, 4);
            MinecraftUtil.writeVarInt(handshake, host.length());
            handshake.write(host.getBytes(StandardCharsets.UTF_8));
            handshake.writeShort(port);
            MinecraftUtil.writeVarInt(handshake, 1);
            MinecraftUtil.writeVarInt(socket.getOutputStream(), baos.size());

            socket.write(baos.toByteArray());

            socket.writeByte(0x01);
            socket.writeByte(0x00);

            socket.readVarInt(); // Packet size
            int id = socket.readVarInt();
            if (id == -1) {
                return PingResult.fail(ServerStatus.CANT_CONNECT);
            }
            if (id != 0x00) {
                return PingResult.fail(ServerStatus.PACKET_INVALID);
            }
            int length = socket.readVarInt();
            if (length < 1) {
                return PingResult.fail(ServerStatus.CANT_CONNECT);
            }

            byte[] data = socket.readFully(length);

            String raw = new String(data, StandardCharsets.UTF_8);
            JsonObject json = new JsonParser().parse(raw).getAsJsonObject();
            JsonElement desc = json.get("description");
            Motd motd;
            if (desc.isJsonObject()) {
                motd = new Motd(desc.toString());
            } else if (desc.isJsonPrimitive()) {
                motd = new Motd(desc.getAsJsonPrimitive().getAsString());
            } else {
                // error
                motd = null;
            }
            PlayerList playerList;
            JsonElement jsonPlayers = json.get("players");
            if (jsonPlayers.isJsonObject()) {
                JsonObject obj = jsonPlayers.getAsJsonObject();
                int maxPlayers = JsonUtil.getInt(obj, "max");
                int player = JsonUtil.getInt(obj, "online");
                JsonElement sample = obj.get("sample");
                if (sample != null && sample.isJsonArray()) {
                    JsonArray array = sample.getAsJsonArray();
                    List<Player> players = new ArrayList<>();
                    for (JsonElement e : array) {
                        if (e.isJsonObject()) {
                            JsonObject tmp = e.getAsJsonObject();
                            players.add(new Player(JsonUtil.getString(tmp, "name"), JsonUtil.getString(tmp, "id")));
                        }
                    }
                    playerList = new PlayerList(players, player, maxPlayers);
                } else {
                    playerList = new PlayerList(Collections.emptyList(), player, maxPlayers);
                }
            } else {
                playerList = PlayerList.EMPTY;
            }
            String serverIcon = JsonUtil.getString(json, "favicon");
            ServerSoftware software = null;
            Version version = null;
            JsonElement jsonVersion = json.get("version");
            if (jsonVersion.isJsonObject()) {
                JsonObject obj = jsonVersion.getAsJsonObject();
                JsonElement name = obj.get("name");
                JsonElement protocol = obj.get("protocol");
                if (name.isJsonPrimitive() && protocol.isJsonPrimitive()) {
                    version = new Version(name.getAsString(), protocol.getAsInt());
                }
            }
            return PingResult.success(software, version, playerList, motd, serverIcon);
        } catch (ConnectException | SocketTimeoutException e) {
            return PingResult.fail(ServerStatus.TIMEOUT);
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            return PingResult.fail(ServerStatus.UNKNOWN_HOST);
        } catch (EOFException e) {
            return PingResult.fail(ServerStatus.CANT_CONNECT);
        } catch (SecurityException | IOException e) {
            e.printStackTrace();
        }
        return PingResult.fail(ServerStatus.OFFLINE);
    }
}
