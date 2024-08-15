package me.jakepronger.rotatingshop.utils;

import me.jakepronger.rotatingshop.config.DataUtils;

import org.bukkit.Bukkit;

import java.util.concurrent.CompletableFuture;

import static me.jakepronger.rotatingshop.RotatingShop.plugin;

public class TimerUtils {

    private final DataUtils dataUtils;

    private final long START_TIME;
    private long UPTIME;
    private long TIMER_CHECK;

    private boolean useTimer;
    private int timerMinutes;
    private int rotateMinutes;

    private int uptimeTimerId;
    private int rotateTimerId;

    public TimerUtils(DataUtils dataUtils) {

        this.dataUtils = dataUtils;

        START_TIME = System.currentTimeMillis();

        initiateVars();
    }

    private void initiateVars() {

        UPTIME = dataUtils.getUptime();
        useTimer = dataUtils.isTimerEnabled();

        // if timer minutes is less than one use default value
        int timerMinutes = dataUtils.getTimerMinutes();

        if (dataUtils.getTimerMinutes() < 1)
            this.timerMinutes = 5;
        else this.timerMinutes = timerMinutes;

        Logger.log("┎ &eLoaded timer settings");
        Logger.log("┃ &fTimer: " + (this.useTimer ? "&aOn" : "&cOff"));
        Logger.log("┃ &fUptime Timer: " + (this.useTimer ? "&a" : "&c") + this.timerMinutes + " minutes");

        int rotateMinutes = plugin.getConfigUtils().getItemRotateMinutes();

        // if timer minutes is less than one use default value
        if (rotateMinutes < 1) {
            this.rotateMinutes = 360;
        } else this.rotateMinutes = rotateMinutes;

        Logger.log("┎ &eRotation Info");
        Logger.log("┃ &fRemaining: &a" + getMinutesLeft() + " minutes");
        Logger.log("┃ &fRotate every: &a" + rotateMinutes + " minutes");
        Logger.debug("Uptime: " + UPTIME);
    }

    private long getMinutesLeft() {
        return rotateMinutes - UPTIME;
    }

    public void startRotateTimer() {

        if (rotateTimerId != 0) {
            Logger.error("&cRotate timer has already started. Aborting.");
            return;
        }

        if (UPTIME >= rotateMinutes) {

            UPTIME = UPTIME - rotateMinutes;

            // update uptime in config
            dataUtils.setUptime(UPTIME).whenComplete((result, throwable) -> {
                Logger.debug("Uptime reset to offset in data.yml: " + UPTIME);
            });

            // todo: rotate items + logs
        }

        Logger.log("&aStarted rotation timer.");

        rotateTimerId = Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, () -> {

            // todo: rotate items + logs

        }, getMinutesLeft()*(20*60)).getTaskId();
    }

    public void stopRotateTimer() {

        if (rotateTimerId == 0)
            return;

        Bukkit.getScheduler().cancelTask(rotateTimerId);
        rotateTimerId = 0;

        Logger.log("&aStopped rotation timer.");
    }

    public void startTimer() {

        if (!useTimer)
            return;

        if (uptimeTimerId != 0) {
            Logger.error("&cUptime timer has already started. Aborting.");
            return;
        }

        uptimeTimerId = Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () -> {
            updateUptime(true);
        }, timerMinutes*(20*60), timerMinutes*(20*60)).getTaskId();

        Logger.log("&aUptime timer has started.");
    }

    public void stopTimer() {

        if (uptimeTimerId == 0)
            return;

        Bukkit.getScheduler().cancelTask(uptimeTimerId);
        uptimeTimerId = 0;

        Logger.log("&aUptime timer has stopped.");
    }

    public void reloadTimer() {
        stopRotateTimer();
        stopTimer();
        initiateVars();
        startRotateTimer();
        startTimer();
    }

    private long getLastUpdated() {
        return TIMER_CHECK != 0 ? TIMER_CHECK : START_TIME;
    }

    public CompletableFuture<Void> updateUptime() {
        return updateUptime(false);
    }

    /**
     *
     * @param timerUpdate true if this is a timer save, otherwise server is shutting down
     */
    private CompletableFuture<Void> updateUptime(boolean timerUpdate) {

        CompletableFuture<Void> response = new CompletableFuture<>();

        long newUptime;
        if (timerUpdate) {
            newUptime = UPTIME + timerMinutes;
            TIMER_CHECK = System.currentTimeMillis();
        } else {
            newUptime = UPTIME + ((System.currentTimeMillis() - getLastUpdated()) / 1000 / 60);
        }

        boolean rotateRequired;
        if (newUptime >= rotateMinutes) {
            newUptime = newUptime - rotateMinutes;
            rotateRequired = true;
        } else {
            rotateRequired = false;
        }

        if (UPTIME == newUptime && !rotateRequired) {
            Logger.debug("Uptime has no significant time difference; returning");
            response.complete(null);
            return response;
        }

        UPTIME = newUptime;

        // update new uptime in config
        dataUtils.setUptime(UPTIME).whenComplete((result, throwable) -> {

            Logger.debug("Updated uptime in data.yml: " + UPTIME);

            if (rotateRequired) {
                // todo: rotate items + logs
            }

            response.complete(null);
        });

        return response;
    }

}
