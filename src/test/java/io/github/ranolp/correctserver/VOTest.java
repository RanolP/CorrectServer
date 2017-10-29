package io.github.ranolp.correctserver;

import io.github.ranolp.correctserver.minecraft.Motd;
import io.github.ranolp.correctserver.minecraft.ServerStatus;
import org.junit.Test;
import static org.junit.Assert.*;

public class VOTest {
    @Test
    public void motd(){
        Motd formatted = new Motd("§aEquality§c§lTest");
        Motd jsonExtra = new Motd("{\"text\":\"Equality\",\"color\":\"green\",\"extra\":[{\"text\":\"Test\",\"color\":\"red\",\"bold\":true}]}");

        assertEquals(formatted, jsonExtra);
        assertEquals(jsonExtra, formatted);
    }

    @Test
    public void result() {
        PingResult result = PingResult.fail(ServerStatus.OFFLINE);
        PingResult result2 = PingResult.fail(ServerStatus.OFFLINE);

        assertTrue(result == result2);
    }
}
