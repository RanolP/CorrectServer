package io.github.ranolp.correctserver;

import io.github.ranolp.correctserver.minecraft.ChatColor;
import org.junit.Test;

import static org.junit.Assert.*;

public class ChatColorTest {
    @Test
    public void stripColor() {
        assertEquals(ChatColor.stripColor("§00§11§22§33§44§55§66§77§88§99§aa§bb§cc§dd§ee§ff§ll§mm§nn§oo§kk"),
                     "0123456789abcdeflmnok");
        assertEquals(ChatColor.stripColor("§0Black§cRED"), "BlackRED");
        assertEquals(ChatColor.stripColor("§q§sr§§rz"), "§q§sr§z");
    }
}
