package net.shortninja.staffplus.core.domain.staff.tracing.config;

import be.garagepoort.mcioc.IocBean;
import be.garagepoort.mcioc.configuration.ConfigurationLoader;
import be.garagepoort.mcioc.configuration.yaml.configuration.file.FileConfiguration;
import net.shortninja.staffplus.core.application.config.AbstractConfigLoader;
import net.shortninja.staffplus.core.common.exceptions.ConfigurationException;
import net.shortninja.staffplus.core.domain.staff.tracing.TraceType;
import net.shortninja.staffplusplus.trace.TraceOutputChannel;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@IocBean
public class TraceModuleLoader extends AbstractConfigLoader<TraceConfiguration> {

    public TraceModuleLoader(ConfigurationLoader configurationLoader) {
        super(configurationLoader);
    }

    @Override
    protected TraceConfiguration load() {
        boolean enabled = defaultConfig.getBoolean("trace-module.enabled");
        if (!enabled) {
            return new TraceConfiguration(false, Collections.emptyList(), Collections.emptyList());
        }

        List<TraceType> traceTypes = getTraceTypes(defaultConfig);
        List<TraceOutputChannel> traceOutputChannels = getTraceOutputChannels(defaultConfig);
        return new TraceConfiguration(true, traceTypes, traceOutputChannels);
    }

    private List<TraceOutputChannel> getTraceOutputChannels(FileConfiguration config) {
        String outputChannelsString = config.getString("trace-module.output-channels");
        if (outputChannelsString == null || outputChannelsString.isEmpty()) {
            throw new ConfigurationException("Invalid configuration: no output channels registered for the tracing module");
        }
        return Arrays.stream(outputChannelsString.split(";"))
            .map(TraceOutputChannel::valueOf)
            .collect(Collectors.toList());
    }

    private List<TraceType> getTraceTypes(FileConfiguration config) {
        String traceEventsString = config.getString("trace-module.trace-events");
        if (traceEventsString == null || traceEventsString.isEmpty()) {
            throw new ConfigurationException("Invalid configuration: no trace events registered for the tracing module");
        }
        return Arrays.stream(traceEventsString.split(";"))
            .map(TraceType::valueOf)
            .collect(Collectors.toList());
    }
}
