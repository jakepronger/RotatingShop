package me.jakepronger.rotatingshop.hooks;

import org.black_ixx.playerpoints.PlayerPoints;
import org.black_ixx.playerpoints.PlayerPointsAPI;

import org.bukkit.Bukkit;

import static me.jakepronger.rotatingshop.RotatingShop.plugin;

public class PlayerPointsHook {

    public PlayerPointsHook() {
        hook();
    }

    private PlayerPointsAPI ppAPI;
    public PlayerPointsAPI getPlayerPoints() {
        return ppAPI;
    }

    public void hook() {

        if (plugin.getServer().getPluginManager().getPlugin("PlayerPoints") == null) {
            return;
        }

        if (Bukkit.getPluginManager().isPluginEnabled("PlayerPoints")) {
            try {
                ppAPI = PlayerPoints.getInstance().getAPI();
            } catch (Exception e) {

            }
        }

    }

}
