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

    private List<Integer> itemSlots;
    private HashMap<Integer, Map.Entry<ItemStack, Double>> items;

    private final DataUtils dataUtils;
    private final ConfigUtils configUtils;

    public RotationUtils(DataUtils dataUtils, ConfigUtils configUtils) {
        this.dataUtils = dataUtils;
        this.configUtils = configUtils;
        itemSlots = new ArrayList<>();
        items = new HashMap<>();
        initiateVars();
    }

    private void initiateVars() {
        itemSlots = configUtils.getItemSlots();
    }

    public CompletableFuture<Void> rotateItems() {

        CompletableFuture<Void> response = new CompletableFuture<>();

        // get maximum amount of items
        int totalItemsAmount = dataUtils.getItemsAmount();

        // generate itemsAmount random numbers
        List<Integer> numbers = new ArrayList<>();

        for (int i = 0; i < itemSlots.size(); i++) {

            int randomNumber = (int) (Math.random() * totalItemsAmount) + 1;

            while (numbers.contains(randomNumber)) {
                randomNumber = (int) (Math.random() * totalItemsAmount) + 1;
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

        return response;
    }

    public void reload() {

    }

}
