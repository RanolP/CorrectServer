package io.github.ranolp.correctserver;

import com.google.gson.JsonObject;
import io.github.ranolp.correctserver.minecraft.Motd;
import io.github.ranolp.correctserver.errors.MotdError;
import org.junit.Test;

import static org.junit.Assert.*;

public class MotdTest {
    @Test
    public void plainText() {
        Motd motd = new Motd("Plain text");
        assertEquals(motd.asPlainText(), "Plain text");
        assertEquals(motd.asFormattedText(), "Plain text");
        assertEquals(motd.asJsonText().getAsJsonObject().get("text").getAsString(), "Plain text");
    }

    @Test
    public void formattedText() {
        Motd motd = new Motd("§cRed§9Blue");
        assertEquals(motd.asPlainText(), "RedBlue");
        assertEquals(motd.asFormattedText(), "§cRed§9Blue");
        JsonObject red = motd.asJsonText();
        JsonObject blue = red.get("extra").getAsJsonArray().get(0).getAsJsonObject();
        assertEquals(red.get("text").getAsString(), "Red");
        assertEquals(red.get("color").getAsString(), "red");
        assertEquals(blue.get("text").getAsString(), "Blue");
        assertEquals(blue.get("color").getAsString(), "blue");

         motd = new Motd("§c§lHello,§aWorld!");
        assertEquals(motd.asPlainText(), "Hello,World!");
        assertEquals(motd.asFormattedText(), "§c§lHello,§aWorld!");
        JsonObject boldRed = motd.asJsonText();
        JsonObject green = boldRed.get("extra").getAsJsonArray().get(0).getAsJsonObject();
        assertEquals(boldRed.get("text").getAsString(), "Hello,");
        assertEquals(boldRed.get("color").getAsString(), "red");
        assertTrue(boldRed.get("bold").getAsBoolean());
        assertEquals(green.get("text").getAsString(), "World!");
        assertEquals(green.get("color").getAsString(), "green");
        assertNull(green.get("bold"));
    }

    @Test
    public void jsonObjectText() {
        Motd motd = new Motd("{\"text\":\"Hello\",\"color\":\"dark_green\"}");
        assertEquals(motd.asPlainText(), "Hello");
        assertEquals(motd.asFormattedText(), "§2Hello");
        JsonObject object = motd.asJsonText();
        assertEquals(object.get("text").getAsString(), "Hello");
        assertEquals(object.get("color").getAsString(), "dark_green");
    }

    @Test
    public void jsonFormatFail() {
        try {
            Motd motd = new Motd("{\"error\":true}");
            fail();
        } catch (MotdError error) {
            // ignore
        }
    }
}
