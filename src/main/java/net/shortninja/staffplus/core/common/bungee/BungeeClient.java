package net.shortninja.staffplus.core.common.bungee;

import be.garagepoort.mcioc.IocBean;
import be.garagepoort.mcioc.load.InjectTubingPlugin;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.shortninja.staffplus.core.StaffPlus;
import net.shortninja.staffplus.core.domain.synchronization.ServerSyncConfig;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static com.google.common.io.ByteStreams.newDataOutput;
import static net.shortninja.staffplus.core.common.Constants.BUNGEE_CORD_CHANNEL;

@IocBean
public class BungeeClient {

    private final GsonParser gsonParser;
    private final StaffPlus staffPlus;

    public BungeeClient(GsonParser gsonParser, @InjectTubingPlugin StaffPlus staffPlus) {
        this.gsonParser = gsonParser;
        this.staffPlus = staffPlus;
        Bukkit.getServer().getMessenger().registerOutgoingPluginChannel(staffPlus, BUNGEE_CORD_CHANNEL);
    }

    public void sendAll(CommandSender sender, BungeeAction action, BungeeContext context, String message) {
        send(sender, action, "ALL", context, message);
    }

    public void send(CommandSender sender, BungeeAction action, String server, BungeeContext context, String message) {
        Player player = null;
        if (sender instanceof Player) {
            player = (Player) sender;
        } else {
            Collection<? extends Player> onlinePlayers = Bukkit.getOnlinePlayers();
            if (onlinePlayers.iterator().hasNext()) {
                player = onlinePlayers.iterator().next();
            }
        }
        if (player != null) {
            try {
                ByteArrayDataOutput out = newDataOutput();
                out.writeUTF(action.getActionString());
                out.writeUTF(server);
                out.writeUTF(context.getContextString());
                ByteArrayOutputStream msgbytes = new ByteArrayOutputStream();
                DataOutputStream msgout = new DataOutputStream(msgbytes);
                msgout.writeUTF(message);

                out.writeShort(msgbytes.toByteArray().length);
                out.write(msgbytes.toByteArray());

                player.sendPluginMessage(staffPlus, BUNGEE_CORD_CHANNEL, out.toByteArray());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void sendMessage(Player player, String channel, Object event, List<String> servers) {
        servers.forEach(server -> sendToServer(player, channel, event, server));
    }

    public void sendMessage(Player player, String channel, Object event, ServerSyncConfig serverSyncConfig) {
        if(serverSyncConfig.isMatchesAll()) {
            sendMessage(player, channel, event);
        }else{
            serverSyncConfig.getServers().forEach(server -> sendToServer(player, channel, event, server));
        }
    }

    public void sendMessage(Player player, String channel, Object event) {
        sendToServer(player, channel, event, "ALL");
    }

    private void sendToServer(Player player, String channel, Object event, String server) {
        if (player == null) {
            return;
        }
        try {
            ByteArrayDataOutput out = ByteStreams.newDataOutput();
            out.writeUTF("Forward");
            out.writeUTF(server);
            out.writeUTF(channel);
            ByteArrayOutputStream msgbytes = new ByteArrayOutputStream();
            DataOutputStream msgout = new DataOutputStream(msgbytes);
            msgout.writeUTF(gsonParser.toJson(event));

            out.writeShort(msgbytes.toByteArray().length);
            out.write(msgbytes.toByteArray());

            player.sendPluginMessage(staffPlus, BUNGEE_CORD_CHANNEL, out.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public <T> Optional<T> handleReceived(String channel, String subChannel, byte[] message, Class<? extends BungeeMessage> classOf) {
        if (!channel.equals(BUNGEE_CORD_CHANNEL)) {
            return Optional.empty();
        }
        try {
            ByteArrayDataInput in = ByteStreams.newDataInput(message);
            String subchannel = in.readUTF();
            if (subchannel.equals(subChannel)) {
                short len = in.readShort();
                byte[] msgbytes = new byte[len];
                in.readFully(msgbytes);

                DataInputStream msgin = new DataInputStream(new ByteArrayInputStream(msgbytes));
                String data = msgin.readUTF();
                BungeeMessage bungeeMessage = gsonParser.fromJson(data, classOf);
                if (getDuration(bungeeMessage) > 10) {
                    return Optional.empty();
                }
                return (Optional<T>) Optional.of(bungeeMessage);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    private long getDuration(BungeeMessage bungeeMessage) {
        long now = System.currentTimeMillis();
        return (now - bungeeMessage.getTimestamp()) / 1000;
    }
}
