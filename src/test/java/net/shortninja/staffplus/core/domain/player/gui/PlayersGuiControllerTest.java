package net.shortninja.staffplus.core.domain.player.gui;

import net.shortninja.staffplus.core.common.gui.AbstractTubingGuiTemplateTest;
import net.shortninja.staffplus.core.common.gui.GuiUtils;
import net.shortninja.staffplus.core.domain.player.PlayerManager;
import net.shortninja.staffplus.core.domain.player.ip.database.PlayerIpRepository;
import net.shortninja.staffplus.core.domain.staff.ban.ipbans.IpBanConfiguration;
import net.shortninja.staffplus.core.domain.staff.ban.ipbans.IpBanService;
import net.shortninja.staffplus.core.domain.staff.ban.playerbans.BanService;
import net.shortninja.staffplus.core.domain.staff.ban.playerbans.config.BanConfiguration;
import net.shortninja.staffplus.core.domain.staff.mute.MuteService;
import net.shortninja.staffplus.core.domain.staff.mute.config.MuteConfiguration;
import net.shortninja.staffplus.core.domain.staff.reporting.ReportService;
import net.shortninja.staffplus.core.domain.staff.warn.warnings.config.WarningConfiguration;
import net.shortninja.staffplusplus.session.SppPlayer;
import net.shortninja.staffplusplus.warnings.WarningService;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PlayersGuiControllerTest extends AbstractTubingGuiTemplateTest {

    private static final UUID UUID_PLAYER_1 = java.util.UUID.fromString("a8525382-49ed-4c9c-9c50-c00a0e570159");
    private static final UUID UUID_PLAYER_2 = java.util.UUID.fromString("98095f6e-f1f8-4884-93e9-6cc0c7d9cad1");

    @Mock
    private PlayerManager playerManager;
    @Mock
    private BanService banService;
    @Mock
    private IpBanService ipBanService;
    @Mock
    private BanConfiguration banConfiguration;
    @Mock
    private IpBanConfiguration ipBanConfiguration;
    @Mock
    private MuteService muteService;
    @Mock
    private MuteConfiguration muteConfiguration;
    @Mock
    private PlayerIpRepository playerIpRepository;
    @Mock
    private ReportService reportService;
    @Mock
    private WarningService warningService;
    @Mock
    private WarningConfiguration warningConfiguration;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private SppPlayer sppPlayer1;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private SppPlayer sppPlayer2;


    @Captor
    private ArgumentCaptor<String> xmlCaptor;
    private static MockedStatic<GuiUtils> guiUtilsMockedStatic;

    @BeforeAll
    public static void beforeAll() {
        guiUtilsMockedStatic = mockStatic(GuiUtils.class);
        guiUtilsMockedStatic.when(() -> GuiUtils.getNextPage(anyString(), anyInt())).thenReturn("goNext");
        guiUtilsMockedStatic.when(() -> GuiUtils.getPreviousPage(anyString(), anyInt())).thenReturn("goPrevious");
    }

    @AfterAll
    public static void close() {
        guiUtilsMockedStatic.close();
    }

    @Override
    public Object getGuiController() {
        return new PlayersGuiController(playerManager,
            banService,
            ipBanService,
            banConfiguration,
            ipBanConfiguration,
            muteService,
            muteConfiguration,
            playerIpRepository,
            reportService,
            warningService,
            warningConfiguration);
    }

    @Test
    public void selectOverviewType() {
        guiActionService.executeAction(player, "players/view/select-overview-type");

        verify(tubingGuiXmlParser).toTubingGui(eq(player), xmlCaptor.capture());
        validateMaterials(xmlCaptor.getValue());
    }

    @Test
    public void onlineOverview() throws URISyntaxException, IOException {
        when(playerManager.getOnlineSppPlayers()).thenReturn(Arrays.asList(sppPlayer1, sppPlayer2));
        when(sppPlayer1.getUsername()).thenReturn("player1");
        when(sppPlayer2.getUsername()).thenReturn("player2");
        when(sppPlayer1.getId()).thenReturn(UUID_PLAYER_1);
        when(sppPlayer2.getId()).thenReturn(UUID_PLAYER_2);
        guiUtilsMockedStatic.when(() -> GuiUtils.getSession(sppPlayer1)).thenReturn(Optional.empty());
        guiUtilsMockedStatic.when(() -> GuiUtils.getSession(sppPlayer2)).thenReturn(Optional.empty());

        guiActionService.executeAction(player, "players/view/overview/online");

        verify(tubingGuiXmlParser).toTubingGui(eq(player), xmlCaptor.capture());
        validateMaterials(xmlCaptor.getValue());
        validateXml(xmlCaptor.getValue(), "/guitemplates/players/onlineplayeroverview.xml");
    }

    @Test
    public void offlineOverview() throws URISyntaxException, IOException {
        when(playerManager.getOfflinePlayers()).thenReturn(Arrays.asList(sppPlayer1, sppPlayer2));
        when(sppPlayer1.getUsername()).thenReturn("player1");
        when(sppPlayer2.getUsername()).thenReturn("player2");
        when(sppPlayer1.getId()).thenReturn(UUID_PLAYER_1);
        when(sppPlayer2.getId()).thenReturn(UUID_PLAYER_2);
        guiUtilsMockedStatic.when(() -> GuiUtils.getSession(sppPlayer1)).thenReturn(Optional.empty());
        guiUtilsMockedStatic.when(() -> GuiUtils.getSession(sppPlayer2)).thenReturn(Optional.empty());

        guiActionService.executeAction(player, "players/view/overview/offline");

        verify(tubingGuiXmlParser).toTubingGui(eq(player), xmlCaptor.capture());
        validateMaterials(xmlCaptor.getValue());
        validateXml(xmlCaptor.getValue(), "/guitemplates/players/offlineplayeroverview.xml");
    }

    @Test
    public void playerDetail() {
        when(playerManager.getOnOrOfflinePlayer("garagepoort")).thenReturn(Optional.of(sppPlayer1));
        when(sppPlayer1.getUsername()).thenReturn("garagepoort");
        guiUtilsMockedStatic.when(() -> GuiUtils.getSession(sppPlayer1)).thenReturn(Optional.empty());

        guiActionService.executeAction(player, "players/view/detail?targetPlayerName=garagepoort");

        verify(tubingGuiXmlParser).toTubingGui(eq(player), xmlCaptor.capture());
        validateMaterials(xmlCaptor.getValue());
    }
}
