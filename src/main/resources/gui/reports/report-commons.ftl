<#assign GuiUtils=statics['net.shortninja.staffplus.core.common.gui.GuiUtils']>
<#macro reportitem slot report onRightClick="$NOOP" onLeftClick="$NOOP" onMiddleClick="$NOOP" actions=[]>
    <#assign DateTimeFormatter=statics['java.time.format.DateTimeFormatter']>
    <#assign JavaUtils=statics['net.shortninja.staffplus.core.common.JavaUtils']>
    <GuiItem slot="${slot}" onLeftClick="${onLeftClick}" onRightClick="${onRightClick}" onMiddleClick="${onMiddleClick}"
             material="PAPER" name="&5Report">
        <Lore>
            <LoreLine>&bId: &7${report.id}</LoreLine>

            <LoreLine>&bStatus: &7${report.reportStatus.name()}</LoreLine>

            <#if .data_model["server-sync-module.report-sync"] == true >
                <LoreLine>&bServer: &7${report.serverName}</LoreLine>
            </#if>

            <#if report.reportType.isPresent() == true >
                <LoreLine>&bType: &7${report.reportType.get()}</LoreLine>
            </#if>
            <#if report.reportStatus.name() != "OPEN" >
                <LoreLine>&bAssignee: &7${report.staffName}</LoreLine>
            </#if>

            <#if report.culpritName?? >
                <LoreLine>&bCulprit: &7${report.culpritName}</LoreLine>
            <#else >
                <LoreLine>&bCulprit: &7Unknown</LoreLine>
            </#if>

            <LoreLine>&bTimestamp:
                &7${report.creationDate.format(DateTimeFormatter.ofPattern(.data_model["timestamp-format"]))}</LoreLine>

            <#if .data_model["reports-module.show-reporter"] == true >
                <LoreLine>&bReporter: &7${report.reporterName}</LoreLine>
            </#if>

            <LoreLine>&bReason:</LoreLine>
            <#list JavaUtils.formatLines(report.reason, 30) as reasonLine>
                <LoreLine>${"&7   " + reasonLine}</LoreLine>
            </#list>

            <LoreLine></LoreLine>

            <#if report.closeReason?has_content >
                <LoreLine>&bClose Reason:</LoreLine>
                <#list JavaUtils.formatLines(report.closeReason, 30) as reasonLine>
                    <LoreLine>${"&7   " + reasonLine}</LoreLine>
                </#list>
            </#if>

            <LoreLine></LoreLine>
            <#if report.sppLocation.isPresent() == true >
                <LoreLine>&bLocation: &7${report.sppLocation.get().worldName} &8 |
                    &7${JavaUtils.serializeLocation(report.sppLocation.get())}</LoreLine>
            <#else >
                <LoreLine>&bLocation: &7Unknown</LoreLine>
            </#if>

            <#if actions?has_content >
                <LoreLine></LoreLine>
                <#list actions as actionLine>
                    <LoreLine>${actionLine}</LoreLine>
                </#list>
            </#if>
        </Lore>
    </GuiItem>
</#macro>