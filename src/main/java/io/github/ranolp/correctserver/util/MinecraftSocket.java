package io.github.ranolp.correctserver.util;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class MinecraftSocket implements AutoCloseable {
    private DataOutputStream dataOutputStream;
    private DataInputStream dataInputStream;
    private InputStream inputStream;
    private Socket socket;

    public MinecraftSocket() {
    }

    public void connect(String host, short port) throws IOException {
        socket = new Socket();
        socket.connect(new InetSocketAddress(host, port));
        dataOutputStream = new DataOutputStream(socket.getOutputStream());
        dataInputStream = new DataInputStream(socket.getInputStream());
        this.inputStream = socket.getInputStream();
    }

    public void write(byte[] b) throws IOException {
        dataOutputStream.write(b);
    }

    public void write(int b) throws IOException {
        dataOutputStream.write(b);
    }

    public void writeByte(int b) throws IOException {
        dataOutputStream.writeByte(b);
    }


    public void writeShort(int v) throws IOException {
        dataOutputStream.writeShort(v);
    }

    public void writeInt(int v) throws IOException {
        dataOutputStream.writeInt(v);
    }

    public void writeLegacyString(String str) throws IOException {
        dataOutputStream.writeShort(str.length());
        dataOutputStream.write(str.getBytes(StandardCharsets.UTF_16BE));
    }

    public DataOutputStream getOutputStream() {
        return dataOutputStream;
    }

    public void flush() throws IOException {
        dataOutputStream.flush();
    }

    public int read() throws IOException {
        return dataInputStream.read();
    }

    public int readVarInt() throws IOException {
        return MinecraftUtil.readVarInt(dataInputStream);
    }

    public byte[] readFully(int len) throws IOException {
        byte[] result = new byte[len];
        dataInputStream.readFully(result);
        return result;
    }

    public int readUnsignedByte() throws IOException {
        return dataInputStream.readUnsignedByte();
    }

    public String readLegacyString() throws IOException {
        int len = dataInputStream.readShort();
        if (len <= 0) { throw new IOException("Invalid legacy string length"); }
        return new String(readFully(len * 2), StandardCharsets.UTF_16BE);
    }

    public DataInputStream getDataInputStream() {
        return dataInputStream;
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    @Override
    public void close() throws IOException {
        socket.close();
        dataOutputStream.close();
        dataInputStream.close();
        inputStream.close();
    }
}
