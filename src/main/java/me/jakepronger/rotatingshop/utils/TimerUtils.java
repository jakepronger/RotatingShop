package me.jakepronger.rotatingshop.utils;

import me.jakepronger.rotatingshop.RotatingShop;
import me.jakepronger.rotatingshop.config.ConfigUtils;

import me.jakepronger.rotatingshop.config.DataUtils;
import org.bukkit.Bukkit;

import java.util.concurrent.CompletableFuture;

import static me.jakepronger.rotatingshop.RotatingShop.plugin;

public class TimerUtils {

    private final ConfigUtils config;
    private final DataUtils data;

    private final long START_TIME;
    private long UPTIME;
    private long TIMER_CHECK;

    private boolean useTimer;
    private int timerMinutes;
    private int rotateMinutes;

    private int uptimeTimerId;
    private int rotateTimerId;

    public TimerUtils(RotatingShop plugin) {

        this.config = plugin.getConfigUtils();
        this.data = plugin.getDataUtils();

        START_TIME = System.currentTimeMillis();

        initiateVars();
    }

    private void initiateVars() {

        UPTIME = data.getUptime();
        useTimer = config.isTimerEnabled();

        // if timer minutes is less than one use default value
        timerMinutes = config.getTimerMinutes();

        if (timerMinutes < 1)
            timerMinutes = 5;

        rotateMinutes = plugin.getConfigUtils().getItemRotateMinutes();

        // if timer minutes is less than one use default value
        if (rotateMinutes < 1)
            rotateMinutes = 360;
    }

    // todo: might keep private just need something for debug %time-left% vars
    public long getMinutesLeft() {
        return rotateMinutes - UPTIME;
    }

    public void startRotateTimer() {

        if (rotateTimerId != 0) {
            Logger.error("&cRotate timer has already started. Aborting.");
            return;
        }

        if (UPTIME >= rotateMinutes) {

            while (UPTIME >= rotateMinutes)
                UPTIME = UPTIME - rotateMinutes;

            // update uptime in config
            data.setUptime(UPTIME).whenComplete((result, throwable) -> {
                Logger.debug("Uptime reset to offset in data.json: " + UPTIME);
            });

            // todo: rotate items + logs
            plugin.getRotationUtils().rotateItems().whenComplete((value, throwable) -> {
                Logger.log("&d&lDEBUG rotationCalled");
            });
        }

        long ticks = getMinutesLeft()*(20*60);

        rotateTimerId = Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () -> {

            // todo: rotate items + logs
            plugin.getRotationUtils().rotateItems().whenComplete((value, throwable) -> {
                Logger.log("&d&lDEBUG rotationCalled");
            });

        }, ticks, ticks).getTaskId();

        //Logger.log("&aStarted rotation timer.");
        Logger.log("┎ &eLoaded rotation timer");
        Logger.log("┃ &fTime left: &a" + getMinutesLeft() + " minutes");
        Logger.log("┃ &fRotate every: &a" + rotateMinutes + " minutes");
        Logger.debug("Uptime: " + UPTIME);
        //Logger.log("┃ &aTimer has started.");

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
            Logger.error("&cUptime timer is already running. Aborting.");
            return;
        }

        int ticks = timerMinutes*(20*60);

        uptimeTimerId = Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () -> {
            updateUptime(true);
        }, ticks, ticks).getTaskId();

        //Logger.log("&aStarted uptime timer.");

        Logger.log("┎ &eLoaded timer settings");
        Logger.log("┃ &fUptime Timer: " + (useTimer ? "&aOn" : "&cOff"));
        Logger.log("┃ &fDuration: " + (useTimer ? "&a" : "&c") + timerMinutes + " minutes");
    }

    public void stopTimer() {

        if (uptimeTimerId == 0)
            return;

        Bukkit.getScheduler().cancelTask(uptimeTimerId);
        uptimeTimerId = 0;

        Logger.log("&aStopped uptime timer.");
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
        data.setUptime(UPTIME).whenComplete((result, throwable) -> {

            Logger.debug("Updated uptime in data.json: " + UPTIME);

            if (rotateRequired) {
                // todo: rotate items + logs
            }

            response.complete(null);
        });

        return response;
    }

}
