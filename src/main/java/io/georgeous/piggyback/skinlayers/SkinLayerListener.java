package io.georgeous.piggyback.skinlayers;

import com.destroystokyo.paper.event.player.PlayerClientOptionsChangeEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class SkinLayerListener implements Listener {

    @EventHandler
    public void onPlayerClientOptionsChange(PlayerClientOptionsChangeEvent event) {
        if (event.hasSkinPartsChanged()) {
            SkinLayerUtil.setSkinLayers(event.getPlayer(), SkinLayer.HAT, SkinLayer.JACKET);
        }
    }
}
