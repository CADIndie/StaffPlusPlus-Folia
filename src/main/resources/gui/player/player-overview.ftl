<#import "player-commons.ftl" as playercommons/>
<#import "/gui/commons/commons.ftl" as commons/>
<#assign URLEncoder=statics['java.net.URLEncoder']>
<TubingGUi size="54">
    <title>${title}</title>

    <#list players as p>
        <@playercommons.playerhead slot="${p?index}" sppPlayer=p onLeftClick="players/view/detail?targetPlayerName=${p.username}&backAction=${URLEncoder.encode(currentAction)}" actions=['&7Left click to &6view players details'] />
    </#list>

    <@commons.pageFooter currentAction="${currentAction}" backAction="${backAction}" page=page />
</TubingGUi>