package org.thanhmagics.guildwar2;

import java.io.Serializable;

public class PPlayer implements Serializable {

    public String uuid;

    public String inventory;

    public String guild=null,wait=null,invited=null;

    public boolean chat = false;

    public PPlayer(String uuid) {
        this.uuid = uuid;
    }
    public Guild getGuild() {
        for (String k : GuildWar2.get().dataStorage.guildStorage.keySet())
            if (k.equals(guild))
                return GuildWar2.get().dataStorage.guildStorage.get(k);
        return null;
    }
}
