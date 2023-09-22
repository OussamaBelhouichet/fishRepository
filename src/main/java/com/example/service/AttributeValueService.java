package com.example.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.entity.Attribute;
import com.example.entity.AttributeValue;
import com.example.entity.Department;
import com.example.entity.Room;
import com.example.entity.Tank;
import com.example.repository.AttributeValueRepository;

@Service
public class AttributeValueService {
    @Autowired
    private AttributeValueRepository attributeValueRepository;
    
    @Autowired
    private AttributeService attributeService;
    
    public Optional<AttributeValue> getById(Long id) {
        return attributeValueRepository.findById(id);
    }
    
    public AttributeValue saveAttributeValue(AttributeValue attributeValue) {
        return attributeValueRepository.save(attributeValue);
    }
    
    public List<AttributeValue> saveAttributeValues(List<AttributeValue> attributeValues) {
        return attributeValueRepository.saveAll(attributeValues);
    }
    
    public List<AttributeValue> getByTankAndDateRange(Tank tank, LocalDateTime startDate, LocalDateTime endDate) {
        return attributeValueRepository.findByTankAndTimestampBetween(tank, startDate, endDate);
    }

    
    /**
     * Calculate daily average values from a list of attribute values.
     *
     * @param attributeValues A list of attribute values.
     * @return A map containing daily average values.
     */
    private Map<LocalDate, Double> calculateDailyAverages(List<AttributeValue> attributeValues) {
        // Initialize maps to store daily sums and counts
        Map<LocalDate, Double> dailySums = new HashMap<>();
        Map<LocalDate, Integer> dailyCounts = new HashMap<>();

        // Iterate through the attribute values
        for (AttributeValue attrValue : attributeValues) {
            LocalDate date = attrValue.getTimestamp().toLocalDate();
            double value = attrValue.getValue();

            // Accumulate the sum of attribute values for each day
            dailySums.merge(date, value, Double::sum);

            // Increment the count of attribute values for each day
            dailyCounts.merge(date, 1, Integer::sum);
        }

        // Calculate daily averages based on sums and counts
        Map<LocalDate, Double> dailyAverages = new HashMap<>();
        for (Map.Entry<LocalDate, Double> entry : dailySums.entrySet()) {
            LocalDate date = entry.getKey();
            double sum = entry.getValue();
            int count = dailyCounts.get(date);

            // Calculate and store the daily average
            dailyAverages.put(date, sum / count);
        }

        return dailyAverages;
    }

    
    /**
     * Get daily average attribute values for a specific attribute in tank between two dates.
     *
     * @param tank The tank for which to retrieve attribute values.
     * @param attribute The attribute for which to retrieve values.
     * @param startDate The start date of the time range.
     * @param endDate The end date of the time range.
     * @return A map containing daily average attribute values for the tank.
     */
    public Map<LocalDate, Double> getDailyAverageAttributeValuesInTankBetweenDates(Tank tank, Attribute attribute, LocalDate startDate, LocalDate endDate) {
    	LocalDateTime startOfDay = startDate.atStartOfDay();
        LocalDateTime endOfDay = endDate.atTime(LocalTime.MAX);
        
        List<AttributeValue> attributeValues = attributeValueRepository.findAttributeValuesInTankBetweenDates(tank, attribute, startOfDay, endOfDay);
        
        return calculateDailyAverages(attributeValues);
    } 
    
    
    /**
     * Calculates the daily average values of all attributes inside a tank between two dates.
     *
     * @param tank      The tank for which to calculate daily averages.
     * @param startDate The start date for the calculation period.
     * @param endDate   The end date for the calculation period.
     * @return A map containing daily average values for each attribute.
     */
    public Map<String, Map<LocalDate, Double>> getDailyAverageAllAttributesInTankBetweenDates(
            Tank tank,
            LocalDate startDate,
            LocalDate endDate) {
        // Calculate the start and end timestamps for the specified dates.
        LocalDateTime startOfDay = startDate.atStartOfDay();
        LocalDateTime endOfDay = endDate.atTime(LocalTime.MAX);

        // Initialize a map to store daily averages for each attribute.
        Map<String, Map<LocalDate, Double>> dailyAveragesByAttribute = new HashMap<>();

        // Retrieve a list of all attributes.
        List<Attribute> attributes = attributeService.getAllAttributes(); // You should have a method to get all attributes

        // Iterate through each attribute and calculate daily averages.
        for (Attribute attribute : attributes) {
            // Retrieve attribute values for the specified tank and date range.
            List<AttributeValue> attributeValues = attributeValueRepository.findAttributeValuesInTankBetweenDates(tank, attribute, startOfDay, endOfDay);

            // Calculate daily averages for the current attribute.
            Map<LocalDate, Double> dailyAverages = calculateDailyAverages(attributeValues);

            // Store the daily averages in the map using the attribute name as the key.
            dailyAveragesByAttribute.put(attribute.getName(), dailyAverages);
        }

        return dailyAveragesByAttribute;
    }

    
    /**
     * Get daily average attribute values for a specific attribute in room between two dates.
     *
     * @param room The room for which to retrieve attribute values.
     * @param attribute The attribute for which to retrieve values.
     * @param startDate The start date of the time range.
     * @param endDate The end date of the time range.
     * @return A map containing daily average attribute values for the room.
     */
    public Map<LocalDate, Double> getDailyAverageAttributeValuesInRoomBetweenDates(Room room, Attribute attribute, LocalDate startDate, LocalDate endDate) {
        LocalDateTime startOfDay = startDate.atStartOfDay();
        LocalDateTime endOfDay = endDate.atTime(LocalTime.MAX);
        
        List<AttributeValue> attributeValues = attributeValueRepository.findAttributeValuesInRoomBetweenDates(room, attribute, startOfDay, endOfDay);
        
        return calculateDailyAverages(attributeValues);
    }

    
    /**
     * Calculates the daily average values of all attributes inside a room between two specified dates.
     *
     * @param room      The room for which to calculate daily averages.
     * @param startDate The start date of the calculation period.
     * @param endDate   The end date of the calculation period.
     * @return A map containing daily average values for each attribute, where attribute names are keys.
     */
    public Map<String, Map<LocalDate, Double>> getDailyAverageAllAttributesInRoomBetweenDates(
            Room room,
            LocalDate startDate,
            LocalDate endDate) {
        // Calculate the start and end timestamps for the specified dates.
        LocalDateTime startOfDay = startDate.atStartOfDay();
        LocalDateTime endOfDay = endDate.atTime(LocalTime.MAX);

        // Initialize a map to store daily averages for each attribute.
        Map<String, Map<LocalDate, Double>> dailyAveragesByAttribute = new HashMap<>();

        // Retrieve a list of all attributes.
        List<Attribute> attributes = attributeService.getAllAttributes(); // You should have a method to get all attributes

        // Iterate through each attribute and calculate daily averages.
        for (Attribute attribute : attributes) {
            // Retrieve attribute values for the specified room and date range.
            List<AttributeValue> attributeValues = attributeValueRepository.findAttributeValuesInRoomBetweenDates(room, attribute, startOfDay, endOfDay);
            
            // Calculate daily averages for the current attribute.
            Map<LocalDate, Double> dailyAverages = calculateDailyAverages(attributeValues);
            
            // Store the daily averages in the map using the attribute name as the key.
            dailyAveragesByAttribute.put(attribute.getName(), dailyAverages);
        }

        return dailyAveragesByAttribute;
    }

    
    /**
     * Get daily average attribute values for a specific department between two dates.
     *
     * @param department The department for which to retrieve attribute values.
     * @param attribute The attribute for which to retrieve values.
     * @param startDate The start date of the time range.
     * @param endDate The end date of the time range.
     * @return A map containing daily average attribute values for the department.
     */
    public Map<LocalDate, Double> getDailyAverageAttributeValuesInDepartmentBetweenDates(Department department, Attribute attribute, LocalDate startDate, LocalDate endDate) {
        LocalDateTime startOfDay = startDate.atStartOfDay();
        LocalDateTime endOfDay = endDate.atTime(LocalTime.MAX);
        
        List<AttributeValue> attributeValues = attributeValueRepository.findAttributeValuesInDepartmentBetweenDates(department, attribute, startOfDay, endOfDay);
        
        return calculateDailyAverages(attributeValues);
    }
    
    
    /**
     * Calculates the daily average values of all attributes inside a department between two specified dates.
     *
     * @param department The department for which to calculate daily averages.
     * @param startDate  The start date of the calculation period.
     * @param endDate    The end date of the calculation period.
     * @return A map containing daily average values for each attribute, where attribute names are keys.
     */
    public Map<String, Map<LocalDate, Double>> getDailyAverageAllAttributesInDepartmentBetweenDates(
            Department department,
            LocalDate startDate,
            LocalDate endDate) {
        // Calculate the start and end timestamps for the specified dates.
        LocalDateTime startOfDay = startDate.atStartOfDay();
        LocalDateTime endOfDay = endDate.atTime(LocalTime.MAX);

        // Initialize a map to store daily averages for each attribute.
        Map<String, Map<LocalDate, Double>> dailyAveragesByAttribute = new HashMap<>();

        // Retrieve a list of all attributes.
        List<Attribute> attributes = attributeService.getAllAttributes(); // You should have a method to get all attributes

        // Iterate through each attribute and calculate daily averages.
        for (Attribute attribute : attributes) {
            // Retrieve attribute values for the specified department and date range.
            List<AttributeValue> attributeValues = attributeValueRepository.findAttributeValuesInDepartmentBetweenDates(department, attribute, startOfDay, endOfDay);
            
            // Calculate daily averages for the current attribute.
            Map<LocalDate, Double> dailyAverages = calculateDailyAverages(attributeValues);
            
            // Store the daily averages in the map using the attribute name as the key.
            dailyAveragesByAttribute.put(attribute.getName(), dailyAverages);
        }

        return dailyAveragesByAttribute;
    }

    
    
    
}
