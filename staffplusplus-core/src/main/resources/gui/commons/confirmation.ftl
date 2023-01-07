<#include "/gui/commons/translate.ftl"/>
<#assign JavaUtils=statics['net.shortninja.staffplus.core.common.JavaUtils']>
<TubingGui size="27" id="confirmation">
    <title class="gui-title">${title}</title>

    <GuiItem id="confirmation-info"
             slot="4"
             material="BOOK">
        <name class="item-name" color="&6"><@translate key="gui.confirmation.confirm"/></name>
        <Lore>
            <#if confirmationMessage??>
                <#list JavaUtils.formatLines(confirmationMessage, 30) as confirmationLine>
                    <LoreLine>
                        <t class="confirmation-lore" color="&7">${confirmationLine}</t>
                    </LoreLine>
                </#list>
            </#if>
            <#if confirmationMessageLines??>
                <#list confirmationMessageLines as confirmationMessageLine>
                    <LoreLine>
                        <t id="confirmation-lore">${confirmationMessageLine}</t>
                    </LoreLine>
                </#list>
            </#if>
        </Lore>
    </GuiItem>
    <#list [9,10,18,19] as slot>
        <GuiItem id="cancel-${slot?index}"
                 slot="${slot}"
                 material="RED_STAINED_GLASS_PANE"
                 onLeftClick="${cancelAction}">
            <name class="item-name" color="&C"><@translate key="gui.confirmation.cancel"/></name>
        </GuiItem>
    </#list>
    <#list [16,17,25,26] as slot>
        <GuiItem id="confirm-${slot?index}"
                 slot="${slot}"
                 material="GREEN_STAINED_GLASS_PANE"
                 onLeftClick="${confirmAction}">
            <name class="item-name" color="&2"><@translate key="gui.confirmation.confirm"/></name>
        </GuiItem>
    </#list>

</TubingGui>