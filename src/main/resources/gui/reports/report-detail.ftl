<#import "report-commons.ftl" as reportcommon/>
<#import "/gui/commons/commons.ftl" as commons/>
<#import "/gui/evidence/evidence-commons.ftl" as evidenceCommons/>
<#include "/gui/commons/translate.ftl"/>
<#assign URLEncoder=statics['java.net.URLEncoder']>

<TubingGui size="54" id="report-detail">
    <title class="gui-title"><@translate key="gui.reports.detail.title"/>${report.reporterName}</title>

    <@reportcommon.reportitem  itemId="report-info" slot=13 report=report/>

    <#if report.reportStatus.name() == 'IN_PROGRESS'>
        <#if player.uniqueId == report.staffUuid && $permissions.has(player, $config.get("permissions:reports.manage.resolve"))>
            <#list [34,35,43,44] as slot>
                <GuiItem slot="${slot}"
                         id="resolve-${slot?index}"
                         class="report-resolve"
                         material="GREEN_STAINED_GLASS_PANE"
                         onLeftClick="manage-reports/resolve?reportId=${report.id}">
                    <name class="item-name"><@translate key="gui.reports.detail.resolve.title"/></name>
                    <Lore>
                        <LoreLine><@translate key="gui.reports.detail.resolve.lore"/></LoreLine>
                    </Lore>
                </GuiItem>
            </#list>
        </#if>

        <#if player.uniqueId == report.staffUuid || $permissions.has(player, $config.get("permissions:reports.manage.reopen-other"))>
            <#list [27,28,36,37] as slot>
                <GuiItem slot="${slot}"
                         id="unassign-${slot?index}"
                         class="report-unassign"
                         material="WHITE_STAINED_GLASS_PANE"
                         onLeftClick="manage-reports/reopen?reportId=${report.id}">
                    <name class="item-name"><@translate key="gui.reports.detail.unassign.title"/></name>
                    <Lore>
                        <LoreLine><@translate key="gui.reports.detail.unassign.lore"/></LoreLine>
                    </Lore>
                </GuiItem>
            </#list>
        </#if>

        <#if player.uniqueId == report.staffUuid && $permissions.has(player, $config.get("permissions:reports.manage.reject"))>
            <#list [30,31,32,39,40,41] as slot>
                <GuiItem slot="${slot}"
                         id="reject-${slot?index}"
                         material="RED_STAINED_GLASS_PANE"
                         class="report-reject"
                         onLeftClick="manage-reports/reject?reportId=${report.id}">
                    <name class="item-name"><@translate key="gui.reports.detail.reject.title"/></name>
                    <Lore>
                        <LoreLine><@translate key="gui.reports.detail.reject.lore"/></LoreLine>
                    </Lore>
                </GuiItem>
            </#list>
        </#if>
    </#if>

    <#if $permissions.has(player, $config.get("permissions:reports.manage.delete"))>
        <GuiItem slot="8"
                 id="delete"
                 class="report-delete"
                 material="REDSTONE_BLOCK"
                 onLeftClick="manage-reports/delete?reportId=${report.id}">
            <name class="item-name"><@translate key="gui.reports.detail.delete.title"/></name>
            <Lore>
                <LoreLine><@translate key="gui.reports.detail.delete.lore"/></LoreLine>
            </Lore>
        </GuiItem>
    </#if>

    <GuiItem slot="0"
             id="teleport"
             permission="config|permissions:reports.manage.teleport"
             material="ORANGE_STAINED_GLASS_PANE"
             class="report-teleport"
             onLeftClick="manage-reports/teleport?reportId=${report.id}">
        <name class="item-name"><@translate key="gui.reports.detail.teleport.title"/></name>
        <Lore>
            <LoreLine><@translate key="gui.reports.detail.teleport.lore"/></LoreLine>
        </Lore>
    </GuiItem>

    <#if $config.get("reports-module.chatchannels.enabled")>
        <#if channelPresent
            && !isMemberOfChannel
            && $permissions.has(player, $config.get("permissions:chatchannels.join") + ".report")>
            <GuiItem slot="1"
                     id="join-chatchannel-report"
                     material="WRITTEN_BOOK"
                     onLeftClick="manage-reports/join-chatchannel?reportId=${report.id}&backAction=${URLEncoder.encode(currentAction)}">
                <name class="item-name"><@translate key="gui.reports.detail.join-chatchannel.title"/></name>
                <Lore>
                    <LoreLine><@translate key="gui.reports.detail.join-chatchannel.lore"/></LoreLine>
                </Lore>
            </GuiItem>
        </#if>
        <#if channelPresent
            && isMemberOfChannel
            && $permissions.has(player, $config.get("permissions:chatchannels.leave") + ".report")>
            <GuiItem slot="1"
                     id="leave-chatchannel-report"
                     material="BOOK"
                     onLeftClick="manage-reports/leave-chatchannel?reportId=${report.id}&backAction=${URLEncoder.encode(currentAction)}">
                <name class="item-name"><@translate key="gui.reports.detail.leave-chatchannel.title"/></name>
                <Lore>
                    <LoreLine><@translate key="gui.reports.detail.leave-chatchannel.lore"/></LoreLine>
                </Lore>
            </GuiItem>
        </#if>
        <#if channelPresent
        && $permissions.has(player, $config.get("permissions:chatchannels.close") + ".report")>
            <GuiItem slot="2"
                     id="close-chatchannel-report"
                     material="RED_WOOL"
                     onLeftClick="manage-reports/close-chatchannel?reportId=${report.id}&backAction=${URLEncoder.encode(currentAction)}">
                <name class="item-name"><@translate key="gui.reports.detail.close-chatchannel.title"/></name>
                <Lore>
                    <LoreLine><@translate key="gui.reports.detail.close-chatchannel.lore"/></LoreLine>
                </Lore>
            </GuiItem>
        </#if>
        <#if !channelPresent
            && $permissions.has(player, $config.get("permissions:chatchannels.open") + ".report")>
            <GuiItem slot="1"
                     id="open-chatchannel-report"
                     material="KNOWLEDGE_BOOK"
                     onLeftClick="manage-reports/open-chatchannel?reportId=${report.id}&backAction=${URLEncoder.encode(currentAction)}">
                <name class="item-name"><@translate key="gui.reports.detail.open-chatchannel.title"/></name>
                <Lore>
                    <LoreLine><@translate key="gui.reports.detail.open-chatchannel.lore"/></LoreLine>
                </Lore>
            </GuiItem>
        </#if>
    </#if>
    <@evidenceCommons.evidenceButton slot=14 evidence=report backAction=currentAction />
    <@commons.backButton action=backAction/>
</TubingGui>