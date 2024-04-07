package org.example;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class BasketSplitter {
    private static final Logger logger = LoggerFactory.getLogger(BasketSplitter.class);
    private final Map<String, List<String>> productDeliveryMap;

    public BasketSplitter(String absolutePathToConfigFile) {
        this.productDeliveryMap = ConfigMapper.readJsonFromFile(absolutePathToConfigFile,new TypeReference<>(){});
    }



    /**
     * @return A map of suppliers with optimal distribution across different items
     */
    public Map<String, List<String>> split(List<String> items) {
        Map<String, List<String>> deliveryMap = new HashMap<>();
        Map<String, Integer> supplierExclusivityScore = calculateSupplierExclusivityScores(items);
        Set<String> assignedItems = new HashSet<>();

        assignExclusiveItems(items, deliveryMap, assignedItems);
        assignNonExclusiveItems(items, deliveryMap, supplierExclusivityScore, assignedItems);

        return deliveryMap;
    }


    private Map<String, Integer> calculateSupplierExclusivityScores(final List<String> items) {
        final Map<String, Integer> scores = new HashMap<>();
        items.forEach(item -> Optional.ofNullable(productDeliveryMap.get(item)).ifPresent(suppliers -> suppliers.forEach(supplier -> scores.merge(supplier, 1, Integer::sum))));
        return scores;
    }

    private void assignExclusiveItems(final List<String> items, final Map<String, List<String>> deliveryMap, final Set<String> assignedItems) {
        items.stream().filter(item -> Optional.ofNullable(productDeliveryMap.get(item)).map(List::size).orElse(0) == 1).forEach(item -> {
            final String supplier = productDeliveryMap.get(item).get(0);
            deliveryMap.computeIfAbsent(supplier, k -> new ArrayList<>()).add(item);
            assignedItems.add(item);
        });
    }

    private void assignNonExclusiveItems(final List<String> items, final Map<String, List<String>> deliveryMap, final Map<String, Integer> supplierExclusivityScore, final Set<String> assignedItems) {
        items.stream().filter(item -> !assignedItems.contains(item) && productDeliveryMap.containsKey(item)).forEach(item -> {
            final List<String> suppliers = new ArrayList<>(productDeliveryMap.get(item));
            suppliers.sort(Comparator.comparingInt((String s) -> deliveryMap.getOrDefault(s, Collections.emptyList()).size()).thenComparingInt(s -> supplierExclusivityScore.getOrDefault(s, 0)).reversed());
            final String selectedSupplier = suppliers.get(0);
            deliveryMap.computeIfAbsent(selectedSupplier, k -> new ArrayList<>()).add(item);
            assignedItems.add(item);
        });
    }

    public Map<String, List<String>> getProductDeliveryMap() {
        return productDeliveryMap;
    }
}
