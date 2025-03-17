package utils;

import launcher.Bot;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;

import org.jsoup.*;
import org.jsoup.nodes.*;
import org.jsoup.select.*;

import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
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

    public static String getResourcePath() {
        return getProjectPath().concat("/src/main/resources/");
    }

    public static String getGuildSettingsPath(String guildId) {
        return getResourcePath().concat(String.format("/guilds/%s/settings.json", guildId));

    }

    public static void jarRunner(String path, boolean terminal) {
        try {
            ProcessBuilder processBuilder;

            if (terminal) {
                processBuilder = new ProcessBuilder("cmd.exe", "/c", "start", "cmd.exe", "/k", String.format("java -jar %s", path));
            } else {
                processBuilder = new ProcessBuilder("java", "-jar", path);
            }

            processBuilder.redirectErrorStream(true);
            processBuilder.inheritIO();
            processBuilder.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
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

    public static String formatSecondsHHMMSS(long totalSeconds) {
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

    public static String formatMillisMMSS(long millis) {
        long minutes = TimeUnit.MILLISECONDS.toMinutes(millis);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(millis) % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }


    public static String formatTimestamp(OffsetDateTime offsetDateTime) {
        // Konvertiere OffsetDateTime zu LocalDateTime
        LocalDateTime localDateTime = offsetDateTime.toLocalDateTime();

        // Definiere das gewünschte Format
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss dd.MM.yyyy");

        // Formatiere den Timestamp
        return localDateTime.format(formatter);
    }

    private static void setEnvironmentVariable(String key, String value) {
        try {
            // Get the environment variables map
            Map<String, String> env = System.getenv();

            // Use reflection to get the field of the map
            Field field = env.getClass().getDeclaredField("m");
            field.setAccessible(true);

            // Modify the map to set the new variable
            @SuppressWarnings("unchecked")
            Map<String, String> modifiableEnv = (Map<String, String>) field.get(env);
            modifiableEnv.put(key, value);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set environment variable", e);
        }
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

    public static void deleteAfter5(InteractionHook message) {
        deleteAfter(message, 5);
    }

    public static void deleteAfter10(InteractionHook message) {
        deleteAfter(message, 10);
    }

    public static void deleteAfter15(InteractionHook message) {
        deleteAfter(message, 15);
    }

    public static void deleteAfter30(InteractionHook message) {
        Message msg = (Message) message;
        deleteAfter(message, 30);
    }

    public static String getUptime() {
        Instant startTime = Bot.getInstance().getStartTime();

        if (startTime == null) return "00:00:00";
        Instant now = Instant.now();
        Duration uptime = Duration.between(startTime, now);

        long hours = uptime.toHours();
        long minutes = uptime.toMinutes() % 60;
        long seconds = uptime.getSeconds() % 60;

        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    public static List<String> loadSwearWordsFromFile() throws IOException {
        String pathString = Helper.getResourcePath().concat("/swearwords/de.txt");
        Path path = Paths.get(pathString);
        return Files.readAllLines(path, StandardCharsets.UTF_8);
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

    public static void deleteAfter(Message message, int seconds) {
        Executors.newSingleThreadScheduledExecutor().schedule(() -> {
            message.delete().queue();
        }, seconds, TimeUnit.SECONDS);
    }

    public static List<String[]> fetchOrdisQuoteLinks() {
        List<String[]> links = new ArrayList<>();
        try {
            Document doc = Jsoup.connect("https://warframe.fandom.com/wiki/Ordis/Quotes").get();

            Elements audioButtons = doc.select(".audio-button audio");

            for (Element audioButton : audioButtons) {
                String link = audioButton.attr("src");

                Element listItem = audioButton.closest("li");
                String description = (listItem != null) ? listItem.select("i").text() : "No description found";

                links.add(new String[]{link, description});
            }

            return links;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static VoiceChannel handleVoiceChannelJoin(SlashCommandInteractionEvent event) {
        Member member = event.getMember();
        if (member == null || member.getVoiceState() == null || member.getVoiceState().getChannel() == null) {
            event.getHook().sendMessage("❌ You must be in a voice channel!").queue(msg -> Helper.deleteAfter(msg, 5));
            return null;
        }

        VoiceChannel channel = member.getVoiceState().getChannel().asVoiceChannel();
        event.getGuild().getAudioManager().openAudioConnection(channel);

        event.getHook().sendMessage("Joined voice channel: " + channel.getName()).queue(msg -> Helper.deleteAfter(msg, 5));

        return channel;
    }

    public static void startLavaLink() {
        getPIDListenerByPort(Helper.getLavaLinkPort());

        String path = getResourcePath() + "lavalink/";

        ProcessBuilder processBuilder = new ProcessBuilder("java", "-jar", "Lavalink.jar");
        processBuilder.directory(new File(path));
        processBuilder.redirectErrorStream(true); // Fehler- und Normal-Output zusammenführen

        try {
            Process process = processBuilder.start();
            Bot.setLavaLinkProcess(process);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getLavaLinkPort() {
        String path = getResourcePath().concat("lavalink/application.yml");

        var yaml = new Yaml();

        try {
            File file = new File(path);
            FileInputStream fis = new FileInputStream(file);

            Map<String, Object> data = yaml.load(fis);
            Map<String, Object> server = (Map<String, Object>) data.get("server");

            return Integer.toString((int) server.get("port"));

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static String getLavaLinkAddress() {
        String path = getResourcePath().concat("lavalink/application.yml");

        var yaml = new Yaml();

        try {
            File file = new File(path);
            FileInputStream fis = new FileInputStream(file);

            Map<String, Object> data = yaml.load(fis);
            Map<String, Object> server = (Map<String, Object>) data.get("server");

            String address = (String) server.get("address");

            return address;

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static List<String> getPIDListenerByPort(String port) {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder("cmd.exe", "/c", "netstat -ano");
            Process process = processBuilder.start();

            Pattern pattern = Pattern.compile("\\s*(\\S+)\\s+(\\S+)\\s+(\\S+)\\s+(\\S+)\\s+(\\d+)");

            List<String> pids = new ArrayList<>();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    Matcher matcher = pattern.matcher(line);
                    if (matcher.matches()) {
                        String localAddress = matcher.group(2); // xxx.xxx.xxx.xxx:port
                        String pid = matcher.group(5);
                        String state = matcher.group(4); // e.g. listening

                        String[] parts = localAddress.split(":");
                        String ipPort = parts.length > 0 ? parts[1] : null;

                        if (ipPort == null) continue;

                        if (ipPort.equals(port) &&
                                (state.equals("ABH?REN") || state.equals("LISTENING") || state.equals("モニター"))
                        ) pids.add(pid);
                    }
                }

                return pids;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static void killProcessByPID(String pid) {
        try {
            new ProcessBuilder("cmd.exe", "/c", String.format("taskkill /PID %s /F", pid)).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
