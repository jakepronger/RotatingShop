package me.jakepronger.rotatingshop.utils;

import me.jakepronger.rotatingshop.config.DataUtils;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.io.IOException;

import static me.jakepronger.rotatingshop.RotatingShop.plugin;

public class TimerUtils {

    private final DataUtils dataUtils;

    private final long START_TIME;
    private long UPTIME;
    private long TIMER_CHECK;

    private boolean useTimer;
    private int timerMinutes;

    private int taskId;

    public TimerUtils(DataUtils dataUtils) {

        this.dataUtils = dataUtils;

        initiateVars();

        START_TIME = System.currentTimeMillis();
    }

    private void initiateVars() {

        Logger.log("┎ &eLoading timer settings...");

        useTimer = dataUtils.getConfig().getBoolean("time.uptime-updater.use", true);

        Logger.log("┃ &fTimer: " + (useTimer ? "&aOn" : "&cOff"));

        timerMinutes = dataUtils.getConfig().getInt("time.uptime-updater.minutes", 5);
        if (timerMinutes < 1)
            timerMinutes = 5;

        Logger.log("┃ &fMinutes: &a" + timerMinutes);

        UPTIME = dataUtils.getConfig().getLong("time.uptime", 0L);

        Logger.log("&aLoaded timer uptime.");
    }

    public void startTimer() {

        if (!useTimer)
            return;

        taskId = Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () -> {
            updateUptime(true);
        }, timerMinutes*(20*60), timerMinutes*(20*60)).getTaskId();

        Logger.log("&aTimer has started.");
    }

    public void stopTimer() {

        if (taskId == 0)
            return;

        Bukkit.getScheduler().cancelTask(taskId);
        taskId = 0;

        Logger.log("&aTimer has stopped.");
    }

    public void reloadTimer() {
        stopTimer();
        initiateVars();
        startTimer();
    }

    private long getLastUpdated() {
        return TIMER_CHECK != 0 ? TIMER_CHECK : START_TIME;
    }

    public void updateUptime() {
        updateUptime(false);
    }

    /**
     *
     * @param timerUpdate true if this is a timer save, otherwise server is shutting down
     */
    private void updateUptime(boolean timerUpdate) {

        long newUptime;
        if (timerUpdate) {
            newUptime = UPTIME + timerMinutes;
            TIMER_CHECK = System.currentTimeMillis();
        } else {
            newUptime = UPTIME + ((System.currentTimeMillis() - getLastUpdated()) / 1000 / 60);
        }

        if (newUptime == UPTIME) {
            Logger.debug("Timer uptime has no difference; returning");
            return;
        }

        UPTIME = newUptime;

        // write new timer check and uptime in config
        FileConfiguration config = dataUtils.getConfig();
        config.set("time.uptime", UPTIME);

        // save config
        File dataFile = dataUtils.file;
        try {
            config.save(dataFile);
            Logger.debug("Updated uptime: " + UPTIME);
        } catch (IOException e) {
            Logger.error("Issue saving " + dataFile.getName() + ": " + e.getMessage());
        }

    }

}
