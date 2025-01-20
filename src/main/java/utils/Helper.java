package utils;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Helper {

    private static String[] numberEmojis = {
            "\u0031\uFE0F\u20E3", "\u0032\uFE0F\u20E3", "\u0033\uFE0F\u20E3", "\u0034\uFE0F\u20E3",
            "\u0035\uFE0F\u20E3", "\u0036\uFE0F\u20E3", "\u0037\uFE0F\u20E3", "\u0038\uFE0F\u20E3",
            "\u0039\uFE0F\u20E3", "\uD83D\uDD1F"
    };

    public static List<String> getPollEmojis() {
        return Arrays.stream(numberEmojis).toList();
    }

    public static long currentTimeSeconds() {
        return System.currentTimeMillis() / 1000;
    }

    public static String getBaseStatPath(String guildId) {
        return getProjectPath().concat(String.format("/src/main/resources/guilds/%s/stats", guildId));
    }

    public static String getMemberStatPath(String guildId, String memberId) {
        return getBaseStatPath(guildId).concat(String.format("/%s.json", memberId));
    }

    public static String getBaseDBPath() {
        return getProjectPath().concat("/src/main/resources/DB");
    }

    public static String createProgressBar(double percentage, int detail) {
        int filledSegments = (int) (percentage * detail / 100);
        int unfilledSegments = detail - filledSegments;

        String stringPercentage = "\uD83D\uDFE6".repeat(filledSegments);
        String stringTotal = "\u2B1C".repeat(unfilledSegments);

        return (stringPercentage + stringTotal);
    }

    public static Member getTaggedMember(Guild guild, String message) {
        long id = getIdFromString(message);

        if (id == 0) return null;

        return guild.getMemberById(id);
    }

    public static List<Member> getTaggedMembers(Guild guild, String message) {
        List<Long> memberIds = getIdsFromString(message);

        if (memberIds.isEmpty()) return null;

        return memberIds.stream().map(guild::getMemberById).toList();
    }



    public static long getIdFromString(String content) {
        Pattern pattern = Pattern.compile("<@(\\d+)>");
        Matcher matcher = pattern.matcher(content);

        if (!matcher.find()) return 0;

        return Long.parseLong(matcher.group(1));
    }

    public static String pythonRunner(String pythonScriptPath) {
        String pythonExecutable = "python";

        StringBuilder output = new StringBuilder();

        try {
            // Start the process
            ProcessBuilder processBuilder = new ProcessBuilder(pythonExecutable, pythonScriptPath);
            processBuilder.environment().put("PYTHONIOENCODING", "utf-8");
            Process process = processBuilder.start();

            // Capture the output of the Python script
            BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8));

            String line;
            while ((line = stdInput.readLine()) != null) {
                output.append(line).append("\n");
            }

            process.waitFor();

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        return output.toString();
    }

    public static List<Long> getIdsFromString(String content) {
        Pattern pattern = Pattern.compile("<@(\\d+)>");
        Matcher matcher = pattern.matcher(content);

        List<Long> ids = new ArrayList<>();

        while (matcher.find()) {
            String idString = matcher.group(1);
            ids.add(Long.parseLong(idString));
        }

        return ids;
    }

    public static String formatSeconds(long totalSeconds) {
        long seconds = totalSeconds % 60;
        int minutes = (int) ((totalSeconds / 60.0) % 60);
        int hours = (int) totalSeconds / 3600;

        return String.format("%02d:%02d:%02d", hours, minutes, (int) seconds);
    }

    public static long getDurationInSecondsFromString(String duration) {
        if (duration == null || duration.isBlank()) return 0L;

        Pattern pattern = Pattern.compile("(\\d+)(d|h)");
        Matcher matcher = pattern.matcher(duration);

        int days = 0, hours = 0;

        while (matcher.find()) {
            String match = matcher.group(); // Entire match like "5d", "19h", "12m"

            if (match.endsWith("d")) {
                days = Integer.parseInt(matcher.group(1)); // Group 1 is the number
            } else if (match.endsWith("h")) {
                hours = Integer.parseInt(matcher.group(1)); // Group 1 is the number
            }
        }

        Duration d = Duration.ofDays(days);
        Duration h = Duration.ofHours(hours);

        return (d.getSeconds() + h.getSeconds());
    }

    public static String getProjectPath() {
        return new File("").getAbsolutePath();
    }

    public static String sFormatting(int number) {
        return number > 1 ? "s" : "";
    }

    public static String sFormatting(long number) {
        return number > 1 ? "s" : "";
    }

    public static String getTimeAgo(long timeAgoSeconds) {
        Instant now = Instant.now();
        Instant then = Instant.ofEpochSecond(timeAgoSeconds);

        Duration duration = Duration.between(then, now);
        long seconds = duration.getSeconds();

        if (seconds < 60) {
            return String.format("%d second%s ago.", seconds, sFormatting(seconds));
        }

        if (seconds < 3600) {
            long minutes = duration.toMinutes();
            return String.format("%d minute%s ago.", minutes, sFormatting(minutes));
        }

        if (seconds < 86400) {
            long hours = duration.toHours();
            return String.format("%d hour%s ago.", hours, sFormatting(hours));
        }

        if (seconds < 2592000) {
            long days = duration.toDays();
            return String.format("%d day%s ago.", days, sFormatting(days));
        }

        if (seconds < 31536000) {
            long months = duration.toDays() / 30;
            return String.format("%d month%s ago.", months, sFormatting(months));
        }

        long years = duration.toDays() / 365;
        return String.format("%d year%s ago.", years, sFormatting(years));
    }

    public static void deleteAfter15(InteractionHook message) {
        deleteAfter(message, 15);
    }

    public static void deleteAfter30(InteractionHook message) {
        deleteAfter(message, 30);
    }

    public static void deleteAfter60(InteractionHook message) {
        deleteAfter(message, 60);
    }

    public static void deleteAfter300(InteractionHook message) {
        deleteAfter(message, 300);
    }

    public static void deleteAfter600(InteractionHook message) {
        deleteAfter(message, 600);
    }

    public static void deleteAfter(InteractionHook message, int seconds) {
        Executors.newSingleThreadScheduledExecutor().schedule(() -> {
            message.deleteOriginal().queue();
        }, seconds, TimeUnit.SECONDS);
    }
}
