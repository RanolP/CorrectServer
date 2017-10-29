package io.github.ranolp.correctserver.minecraft;

import com.google.gson.*;
import io.github.ranolp.correctserver.errors.MotdError;
import io.github.ranolp.correctserver.util.JsonUtil;

import java.util.Objects;

public final class Motd {

    public static final Motd NOTHING = new Motd();

    private static final JsonParser JSON_PARSER = new JsonParser();
    private String plainText;
    private String formattedText;
    private JsonObject jsonText;
    private final char colorChar;

    private Motd() {
        colorChar = ChatColor.COLOR_CHAR;
    }

    public Motd(String text, char colorChar) throws MotdError {
        this.colorChar = colorChar;
        boolean json = false;
        try {
            JsonElement parsed = JSON_PARSER.parse(text);
           if (parsed.isJsonObject()) {
                json = true;
                jsonText = parsed.getAsJsonObject();
                JsonArray array = new JsonArray();
                formattedText = jsonToFormattedText(jsonText);
                if (formattedText == null) {
                    throw new MotdError("Invalid json motd: " + jsonText.toString());
                }
                plainText = ChatColor.stripColor(formattedText);
            }
        } catch (JsonSyntaxException e) {
            // ignore error
        }
        if (!json) {
            if (text.indexOf(colorChar) != -1) {
                formattedText = text;
                plainText = ChatColor.stripColor(formattedText);
                JsonArray extra = new JsonArray();
                StringBuilder plainTextBuilder = new StringBuilder();
                StringBuilder builder = new StringBuilder();
                ChatColor color = null;
                boolean bold = false;
                boolean italic = false;
                boolean underlined = false;
                boolean strikethrough = false;
                boolean obfuscated = false;
                for (int i = 0; i < formattedText.length(); i++) {
                    char c = formattedText.charAt(i);
                    if (c == '§' && i + 1 < formattedText.length()) {
                        if (builder.length() > 0) {
                            plainTextBuilder.append(builder.toString());
                            JsonObject object = new JsonObject();
                            object.add("text", new JsonPrimitive(builder.toString()));
                            if (color != null) {
                                object.add("color", new JsonPrimitive(color.name().toLowerCase()));
                            }
                            if (bold) {
                                object.add("bold", JsonUtil.TRUE);
                            }
                            if (italic) {
                                object.add("italic", JsonUtil.TRUE);
                            }
                            if (underlined) {
                                object.add("underlined", JsonUtil.TRUE);
                            }
                            if (strikethrough) {
                                object.add("strikethrough", JsonUtil.TRUE);
                            }
                            if (obfuscated) {
                                object.add("obfuscated", JsonUtil.TRUE);
                            }
                            if(jsonText == null) {
                                jsonText = object;
                            } else {
                                extra.add(object);
                            }
                            builder.setLength(0);
                        }
                        char next = Character.toLowerCase(formattedText.charAt(i + 1));
                        if (ChatColor.hasCode(next)) {
                            if (ChatColor.hasColorCode(next)) {
                                color = ChatColor.byCode(next);
                                bold = false;
                                italic = false;
                                underlined = false;
                                strikethrough = false;
                                obfuscated = false;
                            } else if (next == 'l') {
                                bold = true;
                            } else if (next == 'o') {
                                italic = true;
                            } else if (next == 'n') {
                                underlined = true;
                            } else if (next == 'm') {
                                strikethrough = true;
                            } else if (next == 'k') {
                                obfuscated = true;
                            } else if (next == 'r') {
                                color = null;
                                bold = false;
                                italic = false;
                                underlined = false;
                                strikethrough = false;
                                obfuscated = false;
                            }
                            i++;
                            if (builder.length() == 0) {
                                continue;
                            }
                            continue;
                        }
                    }
                    builder.append(c);
                }
                if (builder.length() > 0) {
                    plainTextBuilder.append(builder.toString());
                    JsonObject object = new JsonObject();
                    object.add("text", new JsonPrimitive(builder.toString()));
                    if (color != null) {
                        object.add("color", new JsonPrimitive(color.name().toLowerCase()));
                    }
                    if (bold) {
                        object.add("bold", JsonUtil.TRUE);
                    }
                    if (italic) {
                        object.add("italic", JsonUtil.TRUE);
                    }
                    if (underlined) {
                        object.add("underlined", JsonUtil.TRUE);
                    }
                    if (strikethrough) {
                        object.add("strikethrough", JsonUtil.TRUE);
                    }
                    if (obfuscated) {
                        object.add("obfuscated", JsonUtil.TRUE);
                    }
                    extra.add(object);
                    builder.setLength(0);
                }
                if(extra.size() > 0) {
                    jsonText.add("extra", extra);
                }
            } else {
                plainText = formattedText = text;
                jsonText = new JsonObject();
                jsonText.add("text", new JsonPrimitive(plainText));
            }
        }
    }

    public Motd(String text) {
        this(text, ChatColor.COLOR_CHAR);
    }

    private String jsonToFormattedText(JsonArray array) {
        StringBuilder builder = new StringBuilder();
        for (JsonElement element : array) {
            if (element.isJsonObject()) {
                JsonObject object = element.getAsJsonObject();
                String temp = jsonToFormattedText(object);
                if (temp == null) {
                    throw new MotdError("Invalid json motd: " + object.toString());
                }
                builder.append(temp);
            } else {
                throw new MotdError("Invalid json motd: " + element.toString());
            }
        }
        return builder.toString();
    }

    private String jsonToFormattedText(JsonObject object) {
        if (!object.has("text") && !object.has("extra")) {
            return null;
        }
        String text = JsonUtil.getString(object, "text");
        String color = JsonUtil.getString(object, "color");
        StringBuilder colorBuilder = new StringBuilder();
        if (color != null) {
            ChatColor chatColor = ChatColor.valueOf(color.toUpperCase());
            colorBuilder.append(colorChar).append(chatColor.getCode());
        }
        if (JsonUtil.getBoolean(object, "bold")) {
            colorBuilder.append(colorChar).append('l');
        }
        if (JsonUtil.getBoolean(object, "italic")) {
            colorBuilder.append(colorChar).append('o');
        }
        if (JsonUtil.getBoolean(object, "underlined")) {
            colorBuilder.append(colorChar).append('n');
        }
        if (JsonUtil.getBoolean(object, "strikethrough")) {
            colorBuilder.append(colorChar).append('m');
        }
        if (JsonUtil.getBoolean(object, "obfuscated")) {
            colorBuilder.append(colorChar).append('k');
        }
        JsonElement extra = object.get("extra");
        return colorBuilder.toString() +
               text +
               (extra != null && extra.isJsonObject()
                       ? jsonToFormattedText(extra.getAsJsonObject())
                       : (extra != null && extra.isJsonArray()) ? jsonToFormattedText(extra.getAsJsonArray()) : "");
    }

    public String asPlainText() {
        return plainText;
    }

    public String asFormattedText() {
        return formattedText;
    }

    public JsonObject asJsonText() {
        return jsonText;
    }

    public char getColorChar() {
        return colorChar;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (!(o instanceof Motd)) { return false; }
        Motd motd = (Motd) o;
        return colorChar == motd.colorChar && Objects.equals(formattedText, motd.formattedText);
    }

    @Override
    public int hashCode() {
        return Objects.hash(formattedText, colorChar);
    }

    @Override
    public String toString() {
        return "Motd{formatted='" + formattedText + '\'' + ", json=" + jsonText + '}';
    }
}