package net.shortninja.staffplus.core.domain.blacklist;

import be.garagepoort.mcioc.IocBean;
import net.shortninja.staffplus.core.application.config.AbstractConfigLoader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.util.stream.Collectors.toList;

@IocBean
public class BlackListConfigurationLoader extends AbstractConfigLoader<BlackListConfiguration> {

    @Override
    protected BlackListConfiguration load() {
        String commas4 = defaultConfig.getString("blacklist-module.words");
        if (commas4 == null) {
            throw new IllegalArgumentException("Commas may not be null.");
        }

        List<String> censoredWords = new ArrayList<>(Arrays.asList(commas4.split("\\s*,\\s*")));
        String commas3 = defaultConfig.getString("blacklist-module.characters");
        if (commas3 == null) {
            throw new IllegalArgumentException("Commas may not be null.");
        }

        List<String> censoredCharacters = new ArrayList<>(Arrays.asList(commas3.split("\\s*,\\s*")));
        String commas2 = defaultConfig.getString("blacklist-module.domains");
        if (commas2 == null) {
            throw new IllegalArgumentException("Commas may not be null.");
        }

        List<String> censoredDomains = new ArrayList<>(Arrays.asList(commas2.split("\\s*,\\s*")));
        String commas1 = defaultConfig.getString("blacklist-module.periods");
        if (commas1 == null) {
            throw new IllegalArgumentException("Commas may not be null.");
        }

        List<String> periods = new ArrayList<>(Arrays.asList(commas1.split("\\s*,\\s*")));
        String commas = defaultConfig.getString("blacklist-module.allowed");
        if (commas == null) {
            throw new IllegalArgumentException("Commas may not be null.");
        }

        List<String> allowed = new ArrayList<>(Arrays.asList(commas.split("\\s*,\\s*")));

        censoredWords = censoredWords.stream().sorted().map(String::toLowerCase).collect(toList());
        censoredDomains = periods.stream().map(String::toLowerCase).collect(toList());
        periods = censoredDomains.stream().map(String::toLowerCase).collect(toList());

        return new BlackListConfiguration(censoredWords, censoredCharacters, censoredDomains, periods, allowed);

    }

}
