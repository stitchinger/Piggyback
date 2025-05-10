package io.georgeous.piggyback.skinlayers;

import net.minecraft.network.protocol.game.ClientboundPlayerInfoRemovePacket;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket.Action;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.List;

public class SkinLayerUtil {

    public static void setSkinLayers(Player player, SkinLayer... layers) {
        byte bitmask = combineLayers(layers);
        updateSkinLayers(player, bitmask);
    }

    public static void hideSkinLayers(Player player, SkinLayer... layers) {
        ServerPlayer nmsPlayer = ((CraftPlayer) player).getHandle();
        byte currentLayers = nmsPlayer.getEntityData().get(ServerPlayer.DATA_PLAYER_MODE_CUSTOMISATION);
        byte newLayers = (byte) (currentLayers & ~combineLayers(layers));
        updateSkinLayers(player, newLayers);
    }

    public static void showSkinLayers(Player player, SkinLayer... layers) {
        ServerPlayer nmsPlayer = ((CraftPlayer) player).getHandle();
        byte currentLayers = nmsPlayer.getEntityData().get(ServerPlayer.DATA_PLAYER_MODE_CUSTOMISATION);
        byte newLayers = (byte) (currentLayers | combineLayers(layers));
        updateSkinLayers(player, newLayers);
    }

    private static void updateSkinLayers(Player player, byte bitmask) {
        ServerPlayer nmsPlayer = ((CraftPlayer) player).getHandle();
        nmsPlayer.getEntityData().set(ServerPlayer.DATA_PLAYER_MODE_CUSTOMISATION, bitmask);
        ClientboundPlayerInfoUpdatePacket packet = new ClientboundPlayerInfoUpdatePacket(Action.UPDATE_LISTED, nmsPlayer);

        for (Player online : player.getServer().getOnlinePlayers()) {
            ((CraftPlayer) online).getHandle().connection.send(packet);
        }

        updateTabList(player);
    }

    private static void updateTabList(Player player) {
        ServerPlayer nmsPlayer = ((CraftPlayer) player).getHandle();
        ClientboundPlayerInfoRemovePacket removePacket = new ClientboundPlayerInfoRemovePacket(List.of(nmsPlayer.getUUID()));
        ClientboundPlayerInfoUpdatePacket addPacket = new ClientboundPlayerInfoUpdatePacket(Action.ADD_PLAYER, nmsPlayer);

        for (Player online : player.getServer().getOnlinePlayers()) {
            ServerPlayer onlineNmsPlayer = ((CraftPlayer) online).getHandle();
            onlineNmsPlayer.connection.send(removePacket);
            onlineNmsPlayer.connection.send(addPacket);
        }

    }

    private static byte combineLayers(SkinLayer... layers) {
        byte result = 0;

        for (SkinLayer layer : layers) {
            result |= layer.getBit();
        }

        return result;
    }
}
