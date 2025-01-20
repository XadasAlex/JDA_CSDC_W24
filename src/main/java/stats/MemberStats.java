package stats;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import utils.Helper;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class MemberStats {
    private int messagesSent;
    private int otherReactionCount;
    private int selfReactionCount;
    private int exp;
    private int botInteractions;
    private long totalTime;
    private long lastTimeJoined;
    private long lastTimeMessageSent;
    private long charactersSent;
    private boolean inVoice;
    private final String guildId;
    private final String memberId;
    private final String memberName;
    private final String guildName;

    public MemberStats(Guild guild, Member member, boolean inVoice) {
        assert guild != null;
        assert member != null;

        messagesSent = 0;
        otherReactionCount = 0;
        selfReactionCount = 0;
        totalTime = 0;
        charactersSent = 0;
        exp = 0;
        botInteractions = 0;
        lastTimeJoined = inVoice ? Helper.currentTimeSeconds() : 0;
        this.guildId = guild.getId();
        this.guildName = guild.getName();
        this.memberId = member.getId();
        this.memberName = member.getEffectiveName();
        this.inVoice = inVoice;
    }

    public static boolean allowedStats(String guildId, String memberId) {
        String path = Helper.getMemberStatPath(
                guildId,
                memberId
        );

        return (new File(path).exists());
    }

    public static MemberStats getMemberStats(String guildId, String memberId) {
        String path = Helper.getMemberStatPath(guildId, memberId);

        return getMemberStats(path);
    }

    public static MemberStats getMemberStats(String path) {
        File file = new File(path);

        if (!file.exists()) return null;

        Gson gson = new Gson();

        try (FileReader reader = new FileReader(path)) {
            return gson.fromJson(reader, MemberStats.class);

        } catch (JsonSyntaxException | IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void updateMemberStats(MemberStats stats, String guildId, String memberId) {
        String path = Helper.getMemberStatPath(guildId, memberId);

        Gson gson = new Gson();

        try (FileWriter writer = new FileWriter(path)) {
            gson.toJson(stats, writer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static List<MemberStats> topMembers(String guildId) {
        String path = Helper.getBaseStatPath(guildId);
        File guildFolder = new File(path);

        File[] listOfFiles = guildFolder.listFiles();

        if (listOfFiles == null || listOfFiles.length < 1) return null;

        List<MemberStats> memberStatsList = new ArrayList<>();

        for (File current : listOfFiles) {
            if (current.isFile()) {
                MemberStats stats = getMemberStats(current.getAbsolutePath());
                if (stats == null) continue;
                memberStatsList.add(stats);
            }
        }

        return memberStatsList;
    }

    public void updateSelf() {
        updateExp();
        updateMemberStats(this, guildId, memberId);
    }

    @Override
    public String toString() {
        return "MemberStat{" +
                "lastTimeJoined=" + lastTimeJoined +
                ", messagesSent=" + messagesSent +
                ", charactersSent=" + charactersSent +
                ", totalTime=" + totalTime +
                ", memberId='" + memberId + '\'' +
                '}';
    }

    public void incrementMessagesSent() {
        setMessagesSent(getMessagesSent() + 1);
    }

    public void incrementSelfReactionCount() {
        setSelfReactionCount(getSelfReactionCount() + 1);
    }

    public void incrementOtherReactionCount() {
        setOtherReactionCount(getOtherReactionCount() + 1);
    }

    public void updateExp() {
        int exp = getExp();
        int selfReactions = getSelfReactionCount();
        int otherReactions = getOtherReactionCount();
        int msgSent = getMessagesSent();
        long charsSent = getCharactersSent();
        long totalTime = getTotalTime();

        this.exp = exp +
                selfReactions * 10 +
                otherReactions * 100 +
                botInteractions * 2 +
                msgSent * 5 +
                (int) (charsSent / 20 * 10) +
                (int) (totalTime / 60);
    }

    public void setLastTimeMessageSent(long lastTimeMessageSent) {
        this.lastTimeMessageSent = lastTimeMessageSent;
    }

    public long getLastTimeMessageSent() {
        return lastTimeMessageSent;
    }

    public String getMemberName() {
        return memberName;
    }

    public String getGuildName() {
        return guildName;
    }

    public void leftVoice() {
        inVoice = false;
    }

    public void joinedVoice() {
        inVoice = true;
    }

    public boolean isInVoice() {
        return inVoice;
    }

    public long getLastTimeJoined() {
        return lastTimeJoined;
    }

    public String getLastTimeJoinedFormatted() {
        long lastTimeJoined = getLastTimeJoined();
        return Helper.getTimeAgo(lastTimeJoined);
    }

    public int getMessagesSent() {
        return messagesSent;
    }

    public long getCharactersSent() {
        return charactersSent;
    }

    public long getTotalTime() {
        return totalTime;
    }

    public String getMemberId() {
        return memberId;
    }

    public int getBotInteractions() {
        return botInteractions;
    }

    public String getGuildId() {
        return guildId;
    }

    public int getSelfReactionCount() {
        return selfReactionCount;
    }

    public int getOtherReactionCount() {
        return otherReactionCount;
    }

    public int getExp() {
        return exp;
    }

    public void setLastTimeJoined(long lastTimeJoined) {
        this.lastTimeJoined = lastTimeJoined;
    }

    public void setMessagesSent(int messagesSent) {
        this.messagesSent = messagesSent;
    }

    public void setCharactersSent(long charactersSent) {
        this.charactersSent = charactersSent;
    }

    public void setTotalTime(long totalTime) {
        this.totalTime = totalTime;
    }

    public void setSelfReactionCount(int selfReactionCount) {
        this.selfReactionCount = selfReactionCount;
    }

    public void setOtherReactionCount(int otherReactionCount) {
        this.otherReactionCount = otherReactionCount;
    }

    public void setExp(int exp) {
        this.exp = exp;
    }

    public void setBotInteractions(int botInteractions) {
        this.botInteractions = botInteractions;
    }

    public void incrementBotInteractions() {
        setBotInteractions(getBotInteractions() + 1);
    }

    public String getCurrentRank() {
        return "test-rank";
    }

    public String getNextRank() {
        return "test2-rank";
    }

    public int getExpUntilNextRank() {
        return 100;
    }
}
