package net.shortninja.staffplus.core.domain.staff.staffchat.config;

import be.garagepoort.mcioc.configuration.IConfigTransformer;
import net.shortninja.staffplus.core.common.Sounds;
import net.shortninja.staffplus.core.domain.staff.staffchat.StaffChatChannelConfiguration;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

public class StaffChatChannelConfigMapper implements IConfigTransformer<List<StaffChatChannelConfiguration>, List<LinkedHashMap<String, Object>>> {

    @Override
    public List<StaffChatChannelConfiguration> mapConfig(List<LinkedHashMap<String, Object>> list) {
        return list.stream().map(map -> {
            String name = (String) map.get("name");
            String command = (String) map.get("command");
            String permission = (String) map.get("permission");
            String prefix = (String) map.get("prefix");
            String handle = (String) map.get("handle");
            String messageFormat = (String) map.get("message-format");
            Sounds sound = null;
            if (map.containsKey("sound")) {
                sound = new Sounds((String) map.get("sound"));
            }
            return new StaffChatChannelConfiguration(name, command, permission, handle, prefix, messageFormat, sound);
        }).collect(Collectors.toList());
    }
}
