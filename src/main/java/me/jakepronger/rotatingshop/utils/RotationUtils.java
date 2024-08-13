package me.jakepronger.rotatingshop.utils;

import me.jakepronger.rotatingshop.config.ConfigUtils;
import me.jakepronger.rotatingshop.config.DataUtils;

import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class RotationUtils {

    private final HashMap<Integer, Map.Entry<ItemStack, Double>> items;

    private final DataUtils dataUtils;
    private final ConfigUtils configUtils;

    public RotationUtils(DataUtils dataUtils, ConfigUtils configUtils) {
        this.dataUtils = dataUtils;
        this.configUtils = configUtils;

        items = new HashMap<>();

        initiateItems();
    }

    private void initiateItems() {

    }

    public CompletableFuture<Void> rotateItems() {

        CompletableFuture<Void> response = new CompletableFuture<>();

        CompletableFuture.runAsync(() -> {

            // generate itemsAmount random numbers
            List<Integer> numbers = new ArrayList<>();

            int loopTimes = configUtils.getItemSlots().size();

            // if total amount of available items is less than the provided slots to show items
            // use total amount of available items instead
            if (dataUtils.getItemsAmount() < loopTimes) {
                loopTimes = dataUtils.getItemsAmount();
            }

            for (int i = 1; i <= loopTimes; i++) {

                int randomNumber = (int) (Math.random() * dataUtils.getItemsAmount()) + 1;

                if (numbers.contains(randomNumber)) {
                    i--;
                    continue;
                }

                numbers.add(randomNumber);
            }

            items.clear();

            List<CompletableFuture<Map.Entry<ItemStack, Double>>> futures = new ArrayList<>();

            // set items
            for (int num : numbers) {
                futures.add(dataUtils.getItem(num).whenComplete((entry, throwable) ->
                        items.put(num, entry)));
            }

            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).whenComplete((aVoid, throwable) -> {
                response.complete(null);
            });
        });

        return response;
    }

    public void reload() {

    }

}
