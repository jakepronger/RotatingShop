package me.jakepronger.rotatingshop.utils;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;

import static me.jakepronger.rotatingshop.RotatingShop.plugin;

public class TimerUtils {

    // TODO: Add timer
    public static void startTimer() {

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {

        });

    }

    /*

     */
    @Deprecated
    public static LocalDateTime getTime() {
        return null;
    }

    @Deprecated
    private static LocalDate getCurrentDate() {
        return LocalDate.now();
    }

    public static CompletableFuture<Boolean> updateServerStartedTime() {
        //return CompletableFuture.supplyAsync(() -> {
            //FileConfiguration config = plugin.dataFile;
        //});
        return null;
    }

    public static void updateServerBackupTime() {

    }

    public static void updateServerStoppedTime() {

    }



}
