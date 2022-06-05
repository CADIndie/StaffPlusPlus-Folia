package net.shortninja.staffplus.core.domain.staff.playernotes.gui;

import be.garagepoort.mcioc.configuration.ConfigProperty;
import be.garagepoort.mcioc.tubinggui.AsyncGui;
import be.garagepoort.mcioc.tubinggui.GuiAction;
import be.garagepoort.mcioc.tubinggui.GuiActionReturnType;
import be.garagepoort.mcioc.tubinggui.GuiController;
import be.garagepoort.mcioc.tubinggui.GuiParam;
import be.garagepoort.mcioc.tubinggui.GuiParams;
import be.garagepoort.mcioc.tubinggui.templates.GuiTemplate;
import net.shortninja.staffplus.core.application.config.messages.Messages;
import net.shortninja.staffplus.core.application.session.OnlinePlayerSession;
import net.shortninja.staffplus.core.application.session.OnlineSessionsManager;
import net.shortninja.staffplus.core.common.exceptions.PlayerNotFoundException;
import net.shortninja.staffplus.core.common.permissions.PermissionHandler;
import net.shortninja.staffplus.core.common.utils.BukkitUtils;
import net.shortninja.staffplus.core.domain.player.PlayerManager;
import net.shortninja.staffplus.core.domain.staff.playernotes.PlayerNote;
import net.shortninja.staffplus.core.domain.staff.playernotes.PlayerNoteService;
import net.shortninja.staffplus.core.domain.staff.playernotes.gui.cmd.PlayerNoteFiltersMapper;
import net.shortninja.staffplusplus.playernotes.PlayerNoteFilters;
import net.shortninja.staffplusplus.session.SppPlayer;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static be.garagepoort.mcioc.tubinggui.AsyncGui.async;
import static be.garagepoort.mcioc.tubinggui.GuiActionReturnType.BACK;
import static be.garagepoort.mcioc.tubinggui.templates.GuiTemplate.template;

@GuiController
public class PlayerNotesGuiController {

    @ConfigProperty("permissions:player-notes.create")
    private String permissionCreateNote;
    @ConfigProperty("permissions:player-notes.create-private")
    private String permissionCreatePrivateNote;
    @ConfigProperty("permissions:player-notes.delete")
    private String permissionDelete;
    @ConfigProperty("permissions:player-notes.delete-other")
    private String permissionDeleteOther;

    private static final int PAGE_SIZE = 45;

    private final PlayerManager playerManager;
    private final PlayerNoteService playerNoteService;
    private final Messages messages;
    private final OnlineSessionsManager sessionManager;
    private final BukkitUtils bukkitUtils;
    private final PlayerNoteFiltersMapper playerNoteFiltersMapper;
    private final PermissionHandler permissionHandler;

    public PlayerNotesGuiController(PlayerManager playerManager,
                                    PlayerNoteService playerNoteService,
                                    Messages messages,
                                    OnlineSessionsManager sessionManager,
                                    BukkitUtils bukkitUtils,
                                    PlayerNoteFiltersMapper playerNoteFiltersMapper,
                                    PermissionHandler permissionHandler) {
        this.playerManager = playerManager;
        this.playerNoteService = playerNoteService;
        this.messages = messages;
        this.sessionManager = sessionManager;
        this.bukkitUtils = bukkitUtils;
        this.playerNoteFiltersMapper = playerNoteFiltersMapper;
        this.permissionHandler = permissionHandler;
    }

    @GuiAction("player-notes/view/overview")
    public AsyncGui<GuiTemplate> getNoteOverview(Player player,
                                                 @GuiParam(value = "page", defaultValue = "0") int page,
                                                 @GuiParams Map<String, String> allParams) {
        return async(() -> {
            PlayerNoteFilters.PlayerNoteFiltersBuilder playerNoteFiltersBuilder = new PlayerNoteFilters.PlayerNoteFiltersBuilder();
            allParams.forEach((k, v) -> playerNoteFiltersMapper.map(k, v, playerNoteFiltersBuilder));

            List<PlayerNote> allPlayerNotes = playerNoteService.findPlayerNotes(player, playerNoteFiltersBuilder.build(), PAGE_SIZE * page, PAGE_SIZE);

            Map<String, Object> params = new HashMap<>();
            params.put("title", "&bNote overview");
            params.put("notes", allPlayerNotes);
            return template("gui/player-notes/note-overview.ftl", params);
        });
    }

    @GuiAction("player-notes/create")
    public void createNote(Player staff, @GuiParam("targetPlayerName") String targetPlayerName) {
        SppPlayer sppStaff = playerManager.getOnlinePlayer(staff.getUniqueId()).orElseThrow(() -> new PlayerNotFoundException(targetPlayerName));
        SppPlayer targetPlayer = playerManager.getOnOrOfflinePlayer(targetPlayerName).orElseThrow(() -> new PlayerNotFoundException(targetPlayerName));

        OnlinePlayerSession playerSession = sessionManager.get(staff);
        messages.send(staff, messages.typeInput, messages.prefixGeneral);

        playerSession.setChatAction((player, input) ->
            bukkitUtils.runTaskAsync(player, () -> {
                playerNoteService.createNote(sppStaff, input, targetPlayer, false);
                messages.send(player, messages.inputAccepted, messages.prefixGeneral);
            }));
    }

    @GuiAction("player-notes/delete")
    public AsyncGui<GuiActionReturnType> createNote(Player staff, @GuiParam("noteId") int noteId) {
        return async(() -> {
            SppPlayer sppPlayer = playerManager.getOnlinePlayer(staff.getUniqueId()).orElseThrow(() -> new PlayerNotFoundException(staff.getName()));
            PlayerNote note = playerNoteService.getNote(noteId);

            permissionHandler.validate(staff, permissionDelete);
            if (!note.getNotedByUuid().equals(staff.getUniqueId())) {
                permissionHandler.validate(staff, permissionDeleteOther);
            }

            playerNoteService.deleteNote(sppPlayer, noteId);
            return BACK;
        });
    }
}
