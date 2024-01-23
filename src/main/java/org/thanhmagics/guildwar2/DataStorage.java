package org.thanhmagics.guildwar2;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class DataStorage implements Serializable {

    public Map<String,PPlayer> playerStorage = new HashMap<>();

    public Map<String,Guild> guildStorage = new HashMap<>();

}
