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

    // todo: add logs

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

        List<Integer> numbers = dataUtils.getRotationInts();
        if (numbers.isEmpty()) {
            rotateItems();
        } else loadItems(numbers);
    }

    public CompletableFuture<Void> rotateItems() {

        CompletableFuture<Void> response = new CompletableFuture<>();

        CompletableFuture.runAsync(() -> {
            getNewNumbers().whenComplete((numbers, throwable) -> {
                dataUtils.setRotationInts(numbers).whenComplete((result, throwable1) -> {
                    // item loading
                    loadItems(numbers);
                    response.complete(null);
                });
            });
        });

        return response;
    }

    private CompletableFuture<List<Integer>> getNewNumbers() {
        return CompletableFuture.supplyAsync(() -> {

            // generate itemsAmount random numbers
            List<Integer> numbers = new ArrayList<>();

            int loopTimes = configUtils.getShopItemSlots().size();

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

            return numbers;
        });
    }

    private void loadItems(List<Integer> numbers) {

        items.clear();

        // set items
        for (int num : numbers) {
            items.put(num, dataUtils.getItem(num));
        }
    }

    public void reload() {
        initiateItems();
    }

}
