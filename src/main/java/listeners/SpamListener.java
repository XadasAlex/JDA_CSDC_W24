package listeners;

import launcher.Bot;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import utils.GuildSettings;
import utils.Helper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SpamListener extends ListenerAdapter {
    private static final HashMap<String, List<HashMap<String, Long>>> cooldownHashMap = new HashMap<>();
    private static final int cooldownDuration = 2;

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (!event.isFromGuild()) return;
        // Ignore bot or webhook messages
        if (event.isWebhookMessage() || event.getAuthor().isBot()) return;

        GuildSettings gs = GuildSettings.load(event.getGuild().getId());

        if (gs == null || gs.isAllowSpam()) return;

        Member member = event.getMember();
        if (member == null) return;

        cleanupExpiredCooldowns(member.getGuild().getId());

        if (isMemberOnCooldown(member)) {
            event.getMessage().reply("You are on cooldown!").queue(msg -> Helper.deleteAfter(msg, 2));
            event.getMessage().delete().queue();
            return;
        }

        addCooldown(member);
    }

    private static List<HashMap<String, Long>> getGuildCooldownList(String guildId) {
        return cooldownHashMap.get(guildId);
    }

    private static void addCooldown(Member member) {
        String guildId = member.getGuild().getId();
        cooldownHashMap.putIfAbsent(guildId, new ArrayList<>());
        List<HashMap<String, Long>> guildCooldownList = cooldownHashMap.get(guildId);

        // Remove old cooldown for this member
        guildCooldownList.removeIf(entry -> entry.containsKey(member.getId()));

        // Add new cooldown
        HashMap<String, Long> cooldown = new HashMap<>();
        cooldown.put(member.getId(), Helper.currentTimeSeconds() + cooldownDuration);
        guildCooldownList.add(cooldown);
    }

    private static boolean isMemberOnCooldown(Member member) {
        String guildId = member.getGuild().getId();
        String memberId = member.getId();

        List<HashMap<String, Long>> guildCooldownList = getGuildCooldownList(guildId);
        if (guildCooldownList == null) return false;

        for (HashMap<String, Long> cooldown : guildCooldownList) {
            if (cooldown.containsKey(memberId)) {
                return Helper.currentTimeSeconds() <= cooldown.get(memberId);
            }
        }

        return false;
    }

    private static void cleanupExpiredCooldowns(String guildId) {
        List<HashMap<String, Long>> guildCooldownList = getGuildCooldownList(guildId);
        if (guildCooldownList == null) return;

        guildCooldownList.removeIf(entry -> {
            long cooldown = entry.values().iterator().next();
            return Helper.currentTimeSeconds() > cooldown;
        });

        if (guildCooldownList.isEmpty()) {
            cooldownHashMap.remove(guildId);
        }
    }
}
