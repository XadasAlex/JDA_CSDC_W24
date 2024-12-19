package utils;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Helper {
    private static List<String> activePolls = new ArrayList<>();
    private static String[] numberEmojis = {
            "\u0031\uFE0F\u20E3", "\u0032\uFE0F\u20E3", "\u0033\uFE0F\u20E3", "\u0034\uFE0F\u20E3",
            "\u0035\uFE0F\u20E3", "\u0036\uFE0F\u20E3", "\u0037\uFE0F\u20E3", "\u0038\uFE0F\u20E3",
            "\u0039\uFE0F\u20E3", "\uD83D\uDD1F"
    };

    public static List<String> getPollEmojis() {
        return Arrays.stream(numberEmojis).toList();
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


    public static Member getTaggedMember(MessageReceivedEvent event) {
        return getTaggedMember(event.getGuild(), event.getMessage().getContentRaw());
    }

    public static List<Member> getTaggedMembers(MessageReceivedEvent event) {
        return getTaggedMembers(event.getGuild(), event.getMessage().getContentRaw());
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

    public static AudioChannel getAudioChannelFromString(String content, Guild guild, MessageChannel msgChannel) {

        long channelId = getIdFromString(content);
        Channel channel = guild.getGuildChannelById(channelId);

        if (channel == null) {
            ErrorSender.notFoundError(msgChannel, "Channel with ID " + channelId + " does not exist.");
            return null;
        }

        if (channel instanceof AudioChannel) {
            return (AudioChannel) channel;
        } else {
            ErrorSender.notFoundError(msgChannel, "Channel with ID " + channelId + " is not an AudioChannel.");
            return null;
        }
    }

    public static AudioChannel getAudioChannelFromMessage(MessageReceivedEvent event) {
        String content = event.getMessage().getContentRaw();
        Guild guild = event.getGuild();

        return getAudioChannelFromString(content, guild, event.getChannel());
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
}
