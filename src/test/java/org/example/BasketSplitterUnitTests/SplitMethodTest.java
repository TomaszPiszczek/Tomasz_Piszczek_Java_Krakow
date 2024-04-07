package org.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class BasketSplitterTest {
    private BasketSplitter basketSplitter;
    private Map<String, List<String>> mockProductDeliveryMap = Map.of(
            "Carrots (1kg)", Arrays.asList("Express Delivery", "Click&Collect"),
            "Soda (24x330ml)", List.of("Express Delivery"),
            "Steak (300g)", Arrays.asList("Express Delivery", "Click&Collect"),
            "AA Battery (4 Pcs.)", Arrays.asList("Express Delivery", "Courier"),
            "Espresso Machine", Arrays.asList("Courier", "Click&Collect"),
            "Garden Chair", List.of("Courier")
    );

    @BeforeEach
    void setUp() throws NoSuchFieldException, IllegalAccessException {
        basketSplitter = new BasketSplitter(""); // Passing an empty string since we will mock the map

        Field productDeliveryMapField = BasketSplitter.class.getDeclaredField("productDeliveryMap");
        productDeliveryMapField.setAccessible(true);
        productDeliveryMapField.set(basketSplitter, mockProductDeliveryMap);
    }

    @Test
    void splitMethodShouldReturnOptimalDistributionOfSuppliers() {
        List<String> testItems = Arrays.asList(
                "Steak (300g)",
                "Carrots (1kg)",
                "Soda (24x330ml)",
                "AA Battery (4 Pcs.)",
                "Espresso Machine",
                "Garden Chair"
        );

        Map<String, List<String>> expected = Map.of(
                "Courier", Arrays.asList("Espresso Machine", "Garden Chair"),
                "Express Delivery", Arrays.asList("Steak (300g)", "Carrots (1kg)","Soda (24x330ml)", "AA Battery (4 Pcs.)")
        );

        Map<String, List<String>> result = basketSplitter.split(testItems);


        Map<String, Set<String>> expectedResultSet = expected.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> new HashSet<>(e.getValue())));
        Map<String, Set<String>> resultSet = result.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> new HashSet<>(e.getValue())));

        assertEquals(expectedResultSet, resultSet);
    }

    @Test
    void splitShouldReturnWileItemNotExists() {
        List<String> testItems = Arrays.asList(
                "NotExisitingItem"
        );

        Map<String, List<String>> result = basketSplitter.split(testItems);
        assertEquals(result.size(), 0);
    }




    
}