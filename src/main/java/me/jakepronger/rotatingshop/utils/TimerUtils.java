package me.jakepronger.rotatingshop.utils;

import me.jakepronger.rotatingshop.config.DataUtils;

import org.bukkit.Bukkit;

import java.util.concurrent.CompletableFuture;

import static me.jakepronger.rotatingshop.RotatingShop.plugin;

public class TimerUtils {

    private final DataUtils dataUtils;

    private final long START_TIME;

    private final boolean useUptimeUpdater;
    private final int uptimeUpdaterMinutes;

    private int taskId;

    public TimerUtils(DataUtils dataUtils) {

        this.dataUtils = dataUtils;

        useUptimeUpdater = dataUtils.getConfig().getBoolean("time.uptime-updater.use", true);
        uptimeUpdaterMinutes = dataUtils.getConfig().getInt("time.uptime-updater.time", 5);

        START_TIME = System.currentTimeMillis();
    }

    public void startTimer() {

        if (!useUptimeUpdater)
            return;

        taskId = Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () -> {

        }, uptimeUpdaterMinutes*(20*60), uptimeUpdaterMinutes*(20*60)).getTaskId();
    }

    public void stopTimer() {

        if (taskId == 0)
            return;

        Bukkit.getScheduler().cancelTask(taskId);

        taskId = 0;
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
