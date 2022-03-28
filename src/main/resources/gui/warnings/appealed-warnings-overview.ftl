<#import "warning-commons.ftl" as warningCommons/>
<#import "/gui/commons/commons.ftl" as commons/>
<#assign URLEncoder=statics['java.net.URLEncoder']>
<TubingGui size="54" id="appealed-warnings-overview">
    <title class="gui-title">Appealed Warnings</title>

    <#list warnings as warning>
        <GuiItem id="warning-info-${warning?index}"
                 class="warning-info"
                 slot="${warning?index}"
                 onLeftClick="manage-warnings/view/detail?warningId=${warning.id}"
                 material="PLAYER_HEAD"
        >
            <name class="item-name" color="&3">Warning</name>
            <Lore>
                <@warningCommons.warninglorelines warning=warning />
            </Lore>
        </GuiItem>
    </#list>

    <@commons.pageFooter currentAction="${currentAction}"  page=page />
</TubingGui>