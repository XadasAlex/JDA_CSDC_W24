package utils;

public class GuildBaseInfo {
    private String guildName;
    private String totalMembers;
    private String onlineMembers;

    public GuildBaseInfo(String guildName, String totalMembers, String onlineMembers) {
        this.guildName = guildName;
        this.totalMembers = totalMembers;
        this.onlineMembers = onlineMembers;
    }

    public String getGuildName() {
        return guildName;
    }

    public String getTotalMembers() {
        return totalMembers;
    }

    public String getOnlineMembers() {
        return onlineMembers;
    }

    public void setOnlineMemberCount(String onlineCount) {
        this.onlineMembers = onlineCount;
    }
}
