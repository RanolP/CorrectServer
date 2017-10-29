package io.github.ranolp.correctserver.util;

import io.github.ranolp.correctserver.errors.VarIntError;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class MinecraftUtil {
    private MinecraftUtil(){
        throw new UnsupportedOperationException("You cannot instantiate MinecraftUtil");
    }
    public static int readVarInt(DataInputStream in) throws IOException {
        int i = 0, j = 0;
        while (true) {
            int k = in.readByte();
            i |= (k & 0x7F) << j++ * 7;
            if (j > 5) {
                throw new VarIntError("value is so big");
            }
            if ((k & 0x80) != 128) {
                break;
            }
        }
        return i;
    }

    public static void writeVarInt(DataOutputStream out, int data) throws IOException {
        while (true) {
            if ((data & 0xFFFFFF80) == 0) {
                out.writeByte(data);
                return;
            }
            out.writeByte(data & 0x7F | 0x80);
            data >>>= 7;
        }
    }
}
