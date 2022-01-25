<#import "ban-commons.ftl" as banCommons/>
<#import "/gui/commons/commons.ftl" as commons/>
<#assign URLEncoder=statics['java.net.URLEncoder']>
<TubingGui size="54" id="bans-overview">
    <title class="gui-title">Bans</title>

    <#list bans as ban>
        <@banCommons.banitem itemId="ban-info-${ban?index}"
        slot="${ban?index}"
        ban=ban
        onLeftClick="manage-bans/view/detail?banId=${ban.id}&backAction=${URLEncoder.encode(currentAction)}"/>
    </#list>

    <#if backAction??>
        <@commons.pageFooter currentAction="${currentAction}" backAction="${backAction}" page=page />
    <#else>
        <@commons.pageFooter currentAction="${currentAction}" page=page />
    </#if>
</TubingGui>