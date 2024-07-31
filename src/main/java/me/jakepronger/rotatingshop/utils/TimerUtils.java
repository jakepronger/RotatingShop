package me.jakepronger.rotatingshop.utils;

import me.jakepronger.rotatingshop.config.DataUtils;
import org.bukkit.Bukkit;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;

import static me.jakepronger.rotatingshop.RotatingShop.plugin;

public class TimerUtils {

    private final DataUtils dataUtils;

    private final boolean useUptimeUpdater;
    private final int uptimeUpdaterMinutes;

    public TimerUtils(DataUtils dataUtils) {
        this.dataUtils = dataUtils;

        useUptimeUpdater = dataUtils.getConfig().getBoolean("time.uptime-updater.use", true);
        uptimeUpdaterMinutes = dataUtils.getConfig().getInt("time.uptime-updater.time", 5);
    }

    // TODO: Add timer
    public void startTimer() {
        Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () -> {

            dataUtils.

        }, 2*(20*60), 2*(20*60));
    }

    /*

     */
    @Deprecated
    public LocalDateTime getTime() {
        return null;
    }

    @Deprecated
    private LocalDate getCurrentDate() {
        return LocalDate.now();
    }

    public CompletableFuture<Boolean> updateServerStartedTime() {
        //return CompletableFuture.supplyAsync(() -> {
            //FileConfiguration config = plugin.dataFile;
        //});
        return null;
    }

    public void updateServerBackupTime() {

    }

    public void updateServerStoppedTime() {

    }



}
