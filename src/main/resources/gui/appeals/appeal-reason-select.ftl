<#import "/gui/commons/commons.ftl" as commons/>
<#assign URLEncoder=statics['java.net.URLEncoder']>
<TubingGui size="54" id="appeal-reason-select">
    <title class="gui-title">Select appeal reason</title>

    <#list reasons as reason >
        <GuiItem id="appeal-reason-${reason?index}"
                 class="appeal-reason appeal-reason-${reason}"
                 slot="${reason?index}"
                 material="PAPER"
                 onLeftClick="${action}&reason=${reason}">
            <name class="item-name">${reason}</name>
        </GuiItem>
    </#list>

    <@commons.backButton/>
</TubingGui>