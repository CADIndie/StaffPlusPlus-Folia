package net.shortninja.staffplus.core.domain.staff.mute.gui;

import net.shortninja.staffplus.core.application.config.messages.Messages;
import net.shortninja.staffplus.core.application.session.OnlineSessionsManager;
import net.shortninja.staffplus.core.common.gui.AbstractTubingGuiTemplateTest;
import net.shortninja.staffplus.core.common.gui.GuiUtils;
import net.shortninja.staffplus.core.common.utils.BukkitUtils;
import net.shortninja.staffplus.core.domain.player.PlayerManager;
import net.shortninja.staffplus.core.domain.staff.appeals.Appeal;
import net.shortninja.staffplus.core.domain.staff.appeals.AppealService;
import net.shortninja.staffplus.core.domain.staff.mute.Mute;
import net.shortninja.staffplus.core.domain.staff.mute.MuteService;
import net.shortninja.staffplus.core.domain.staff.mute.appeals.MuteAppealConfiguration;
import net.shortninja.staffplus.core.domain.staff.mute.appeals.MuteAppealGuiController;
import net.shortninja.staffplusplus.appeals.AppealStatus;
import net.shortninja.staffplusplus.appeals.AppealableType;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Collections;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class MuteAppealGuiControllerTest extends AbstractTubingGuiTemplateTest {

    private static final long CREATION_DATE = 1630537429182L;
    private static final String TIMESTAMP_FORMAT = "dd/MM/yyyy-HH:mm:ss";

    @Mock
    private MuteService muteService;
    @Mock
    private Messages messages;
    @Mock
    private PlayerManager playerManager;
    @Mock
    private OnlineSessionsManager onlineSessionsManager;
    @Mock
    private AppealService appealService;
    @Mock
    private BukkitUtils bukkitUtils;

    @Captor
    private ArgumentCaptor<String> xmlCaptor;
    private static MockedStatic<GuiUtils> guiUtilsMockedStatic;
    private MuteAppealConfiguration muteAppealConfiguration;

    @BeforeAll
    public static void beforeAll() {
        guiUtilsMockedStatic = mockStatic(GuiUtils.class);
        guiUtilsMockedStatic.when(() -> GuiUtils.parseTimestamp(CREATION_DATE, TIMESTAMP_FORMAT)).thenReturn("01/09/2021-01:11:15");
        guiUtilsMockedStatic.when(() -> GuiUtils.parseTimestampSeconds(CREATION_DATE, TIMESTAMP_FORMAT)).thenReturn("01/09/2021-01:11:15");
        guiUtilsMockedStatic.when(() -> GuiUtils.getNextPage(anyString(), anyInt())).thenReturn("goNext");
        guiUtilsMockedStatic.when(() -> GuiUtils.getPreviousPage(anyString(), anyInt())).thenReturn("goPrevious");
    }

    @AfterAll
    public static void close() {
        guiUtilsMockedStatic.close();
    }

    @BeforeEach
    public void setUp() {
        super.setUp();
        when(permissionHandler.has(eq(player), anyString())).thenReturn(true);
        doReturn(true).when(templateConfigResolverSpy).get("server-sync-module.ban-sync");
    }

    @Override
    public Object getGuiController() {
        muteAppealConfiguration = new MuteAppealConfiguration();
        return new MuteAppealGuiController(
            appealService,
            muteService,
            messages,
            onlineSessionsManager,
            bukkitUtils,
            muteAppealConfiguration,
            permissionHandler,
            playerManager);
    }

    @Test
    public void appealDetail() throws URISyntaxException, IOException {
        when(appealService.getAppeal(1)).thenReturn(buildAppeal());

        guiActionService.executeAction(player, "manage-mute-appeals/view/detail?appealId=1");

        verify(tubingGuiXmlParser).toTubingGui(eq(player), xmlCaptor.capture());
        validateMaterials(xmlCaptor.getValue());
        validateXml(xmlCaptor.getValue(), "/guitemplates/mutes/appeal-detail.xml");
    }

    @Test
    public void appealReasonSelect() throws URISyntaxException, IOException {
        muteAppealConfiguration.appealReasons = Arrays.asList("Reason 1", "Reason 2", "Reason 3");

        guiActionService.executeAction(player, "manage-mute-appeals/view/create/reason-select?muteId=12");

        verify(tubingGuiXmlParser).toTubingGui(eq(player), xmlCaptor.capture());
        validateMaterials(xmlCaptor.getValue());
        validateXml(xmlCaptor.getValue(), "/guitemplates/mutes/appeal-reason-select.xml");
    }

    @Test
    public void appealedMutesOverview() throws URISyntaxException, IOException {
        when(muteService.getAppealedMutes(0, 45)).thenReturn(Collections.singletonList(buildMute()));

        guiActionService.executeAction(player, "manage-mutes/view/appealed-mutes");

        verify(tubingGuiXmlParser).toTubingGui(eq(player), xmlCaptor.capture());
        validateMaterials(xmlCaptor.getValue());
        validateXml(xmlCaptor.getValue(), "/guitemplates/mutes/appealed-mutes.xml");
    }

    @NotNull
    private Mute buildMute() {
        return new Mute(12,
            "Mute reason",
            CREATION_DATE,
            null,
            "player",
            UUID.fromString("d38f72ea-551a-4a65-8401-d83465a7f596"),
            "issuer",
            UUID.fromString("8fc39a71-63ba-4a4b-99e8-66f5791dd377"),
            null,
            null,
            null,
            "ServerName",
            false,
            null
        );
    }

    private Appeal buildAppeal() {
        return new Appeal(
            1,
            12,
            UUID.fromString("d38f72ea-551a-4a65-8401-d83465a7f596"),
            "appealer",
            UUID.fromString("8fc39a71-63ba-4a4b-99e8-66f5791dd377"),
            "resolver",
            "reason",
            null,
            AppealStatus.OPEN,
            CREATION_DATE,
            AppealableType.BAN
        );
    }
}