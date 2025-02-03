package utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GuildSettings {

    private boolean allowSpam;
    private boolean allowSwear;
    private boolean memberWelcomeMessage;
    private boolean memberLeaveMessage;
    private boolean dedicatedBotChannels;
    private boolean allowMusic;
    private final String guildId;
    private List<String> chatRestrictedMembers = new ArrayList<>();

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public GuildSettings(boolean allowSpam,
                         boolean allowSwear,
                         boolean memberWelcomeMessage,
                         boolean memberLeaveMessage,
                         boolean dedicatedBotChannels,
                         boolean allowMusic,
                         List<String> chatRestrictedMembers,
                         String guildId) {
        this.allowSpam = allowSpam;
        this.allowSwear = allowSwear;
        this.memberWelcomeMessage = memberWelcomeMessage;
        this.memberLeaveMessage = memberLeaveMessage;
        this.dedicatedBotChannels = dedicatedBotChannels;
        this.allowMusic = allowMusic;
        this.guildId = guildId;
        this.chatRestrictedMembers = chatRestrictedMembers != null ? chatRestrictedMembers : new ArrayList<>();
    }

    public static HashMap<String, GuildSettings> loadMultiple(List<String> guildIds) {
        HashMap<String, GuildSettings> guildSettingsList = new HashMap<>();

        for (String guildId : guildIds) {
            GuildSettings settings = load(guildId);
            if (settings != null) {
                guildSettingsList.put(guildId, settings);
            } else {
                System.err.println("Failed to load or initialize settings for guild: " + guildId);
            }
        }

        return guildSettingsList;
    }

    public static GuildSettings load(String guildId) {
        String guildSettingsPath = Helper.getGuildSettingsPath(guildId);
        File guildSettingsFile = new File(guildSettingsPath);

        if (!guildSettingsFile.exists()) {
            System.out.println("Settings file not found for guild: " + guildId + ". Initializing new settings.");
            return initGuildSettings(guildId);
        }

        try (FileReader reader = new FileReader(guildSettingsFile)) {
            return GSON.fromJson(reader, GuildSettings.class);
        } catch (Exception e) {
            System.err.println("Failed to load settings for guild: " + guildId + ". Reinitializing...");
            e.printStackTrace();
            return initGuildSettings(guildId);
        }
    }

    private static GuildSettings initGuildSettings(String guildId) {
        GuildSettings init = new GuildSettings(
                true,  // Default allowSpam
                true,  // Default allowSwear
                false, // Default memberWelcomeMessage
                false, // Default memberLeaveMessage
                false, // Default dedicatedBotChannels
                false, //Default allowMusic
                new ArrayList<>(), // Empty list for restricted members
                guildId
        );
        writeSettingsForGuild(init, guildId);
        return init;
    }

    private static void writeSettingsForGuild(GuildSettings settings, String guildId) {
        String guildSettingsPath = Helper.getGuildSettingsPath(guildId);

        try {
            File file = new File(guildSettingsPath);
            file.getParentFile().mkdirs(); // Create directories if they do not exist

            try (FileWriter writer = new FileWriter(file)) {
                GSON.toJson(settings, writer);
            }
        } catch (Exception e) {
            System.err.println("Failed to write settings for guild: " + guildId);
            e.printStackTrace();
        }
    }

    public void reset() {
        System.out.println("Resetting settings for guild: " + guildId);
        this.allowSpam = true;
        this.allowSwear = true;
        this.memberWelcomeMessage = false;
        this.memberLeaveMessage = false;
        this.dedicatedBotChannels = false;
        this.chatRestrictedMembers = new ArrayList<>();
        writeSettingsForGuild();
    }

    private void writeSettingsForGuild() {
        writeSettingsForGuild(this, this.guildId);
    }

    public boolean isMemberChatRestricted(String memberId) {
        return chatRestrictedMembers.contains(memberId);
    }

    public void addMemberChatRestriction(String memberId) {
        if (!this.chatRestrictedMembers.contains(memberId)) {
            this.chatRestrictedMembers.add(memberId);
            writeSettingsForGuild();
        }
    }

    public void removeMemberChatRestriction(String memberId) {
        if (this.chatRestrictedMembers.contains(memberId)) {
            this.chatRestrictedMembers.remove(memberId);
            writeSettingsForGuild();
        }
    }

    public void update() {
        writeSettingsForGuild();
    }

    // Getters and setters for all fields (optional, if needed elsewhere)
    public boolean isAllowSpam() {
        return allowSpam;
    }

    public void setAllowSpam(boolean allowSpam) {
        this.allowSpam = allowSpam;
        writeSettingsForGuild();
    }

    public boolean isAllowSwear() {
        return allowSwear;
    }

    public void setAllowSwear(boolean allowSwear) {
        this.allowSwear = allowSwear;
        writeSettingsForGuild();
    }

    public boolean isMemberWelcomeMessage() {
        return memberWelcomeMessage;
    }

    public void setMemberWelcomeMessage(boolean memberWelcomeMessage) {
        this.memberWelcomeMessage = memberWelcomeMessage;
        writeSettingsForGuild();
    }

    public boolean isMemberLeaveMessage() {
        return memberLeaveMessage;
    }

    public void setMemberLeaveMessage(boolean memberLeaveMessage) {
        this.memberLeaveMessage = memberLeaveMessage;
        writeSettingsForGuild();
    }

    public boolean isDedicatedBotChannels() {
        return dedicatedBotChannels;
    }

    public void setDedicatedBotChannels(boolean dedicatedBotChannels) {
        this.dedicatedBotChannels = dedicatedBotChannels;
        writeSettingsForGuild();
    }

    public String getGuildId() {
        return guildId;
    }

    public List<String> getChatRestrictedMembers() {
        return chatRestrictedMembers;
    }

    public boolean isAllowMusic() {
        return allowMusic;
    }

    public void setAllowMusic(boolean allowMusic) {
        this.allowMusic = allowMusic;
        writeSettingsForGuild();
    }
}
