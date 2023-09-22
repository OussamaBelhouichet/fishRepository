package com.example.controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.entity.Attribute;
import com.example.entity.AttributeValue;
import com.example.entity.Department;
import com.example.entity.Room;
import com.example.entity.Tank;
import com.example.service.AttributeService;
import com.example.service.AttributeValueService;
import com.example.service.DepartmentService;
import com.example.service.RoomService;
import com.example.service.TankService;

@RestController
@RequestMapping("/attribute-values")
@CrossOrigin(origins = "*") // Allow requests from any origin
public class AttributeValueController {
    
	@Autowired
    private AttributeValueService attributeValueService;
	@Autowired
    private AttributeService attributeService;
	@Autowired
    private TankService tankService;
	@Autowired
	private RoomService roomService;
	@Autowired
	private DepartmentService departmentService;

	
	 /**
     * Create a new attribute value.
     *
     * @param attributeValue The attribute value to create.
     * @return The created attribute value.
     */
    @PostMapping
    public List<AttributeValue> createAttributeValue(@RequestBody List<AttributeValue> attributeValues) {
        return attributeValueService.saveAttributeValues(attributeValues);
    }
    
    
    /**
     * Get daily average attribute values for a specific tank between two dates.
     *
     * @param tankId The ID of the tank.
     * @param attributeId The ID of the attribute.
     * @param startDate The start date of the time range.
     * @param endDate The end date of the time range.
     * @return ResponseEntity containing the daily average attribute values in a specific format.
     */
    @GetMapping("/tank/{tankId}/attribute/{attributeId}/daily-average")
    public ResponseEntity<Map<String, Object>> getDailyAverageAttributeValuesInTankBetweenDates(
            @PathVariable Long tankId,
            @PathVariable Long attributeId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        Tank tank = tankService.getTankById(tankId);
        Attribute attribute = attributeService.getAttributeById(attributeId);

        Map<LocalDate, Double> dailyAverages = attributeValueService.getDailyAverageAttributeValuesInTankBetweenDates(tank, attribute, startDate, endDate);
        Map<String, Object> response = createResponse(dailyAverages, attribute.getName());
        
        return ResponseEntity.ok(response);
    } 
    
    
    /**
     * Retrieves the daily average values of all attributes inside a tank between two dates.
     *
     * @param tankId    The ID of the tank.
     * @param startDate The start date for the calculation period.
     * @param endDate   The end date for the calculation period.
     * @return A ResponseEntity containing the daily average values in the desired format.
     */
    @GetMapping("/tank/{tankId}/daily-average")
    public ResponseEntity<List<Map<String, Object>>> getDailyAverageAllAttributesInTankBetweenDates(
            @PathVariable Long tankId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
    	Tank tank = tankService.getTankById(tankId);

        Map<String, Map<LocalDate, Double>> dailyAveragesByAttribute = attributeValueService.getDailyAverageAllAttributesInTankBetweenDates(tank, startDate, endDate);

        List<Map<String, Object>> responseList = createResponse(dailyAveragesByAttribute);

        return ResponseEntity.ok(responseList);
    }  

    
    /**
     * Create a response containing daily average attribute values.
     *
     * @param dailyAverages A map of daily average attribute values.
     * @param attributeName The name of the attribute.
     * @return A response map containing data points and attribute name.
     */
    @GetMapping("/room/{roomId}/attribute/{attributeId}/daily-average")
    public ResponseEntity<Map<String, Object>> getDailyAverageAttributeValuesInRoomBetweenDates(
            @PathVariable Long roomId,
            @PathVariable Long attributeId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        Room room = roomService.getRoomById(roomId);
        Attribute attribute = attributeService.getAttributeById(attributeId);

        Map<LocalDate, Double> dailyAverages = attributeValueService.getDailyAverageAttributeValuesInRoomBetweenDates(room, attribute, startDate, endDate);
        Map<String, Object> response = createResponse(dailyAverages, attribute.getName());
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Retrieves the daily average values of all attributes inside a room between two specified dates.
     *
     * @param roomId     The ID of the room for which to calculate daily averages.
     * @param startDate  The start date of the calculation period.
     * @param endDate    The end date of the calculation period.
     * @return A ResponseEntity containing a list of daily average values for each attribute.
     */
    @GetMapping("/room/{roomId}/daily-average")
    public ResponseEntity<List<Map<String, Object>>> getDailyAverageAllAttributesInRoomBetweenDates(
            @PathVariable Long roomId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        Room room = roomService.getRoomById(roomId);

        Map<String, Map<LocalDate, Double>> dailyAveragesByAttribute = attributeValueService.getDailyAverageAllAttributesInRoomBetweenDates(room, startDate, endDate);

        List<Map<String, Object>> responseList = createResponse(dailyAveragesByAttribute);

        return ResponseEntity.ok(responseList);
    }

    
    /**
     * Get daily average attribute values for a specific department between two dates.
     *
     * @param departmentId The ID of the department.
     * @param attributeId The ID of the attribute.
     * @param startDate The start date of the time range.
     * @param endDate The end date of the time range.
     * @return ResponseEntity containing the daily average attribute values in a specific format.
     */
    @GetMapping("/department/{departmentId}/attribute/{attributeId}/daily-average")
    public ResponseEntity<Map<String, Object>> getDailyAverageAttributeValuesInDepartmentBetweenDates(
            @PathVariable Long departmentId,
            @PathVariable Long attributeId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        Department department = departmentService.getDepartmentById(departmentId);
        Attribute attribute = attributeService.getAttributeById(attributeId);

        Map<LocalDate, Double> dailyAverages = attributeValueService.getDailyAverageAttributeValuesInDepartmentBetweenDates(department, attribute, startDate, endDate);
        Map<String, Object> response = createResponse(dailyAverages, attribute.getName());
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Retrieves the daily average values of all attributes inside a department between two specified dates.
     *
     * @param departmentId The ID of the department for which to calculate daily averages.
     * @param startDate    The start date of the calculation period.
     * @param endDate      The end date of the calculation period.
     * @return A ResponseEntity containing a list of daily average values for each attribute.
     */
    @GetMapping("/department/{departmentId}/daily-average")
    public ResponseEntity<List<Map<String, Object>>> getDailyAverageAllAttributesInDepartmentBetweenDates(
            @PathVariable Long departmentId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        Department department = departmentService.getDepartmentById(departmentId);

        Map<String, Map<LocalDate, Double>> dailyAveragesByAttribute = attributeValueService.getDailyAverageAllAttributesInDepartmentBetweenDates(department, startDate, endDate);

        List<Map<String, Object>> responseList = createResponse(dailyAveragesByAttribute);

        return ResponseEntity.ok(responseList);
    }
    
    
    /**
     * Create a response containing daily average FOR ONLY ONE attribute values.
     *
     * @param dailyAverages A map of daily average attribute values.
     * @param attributeName The name of the attribute.
     * @return A response map containing data points and attribute name.
     */
    private Map<String, Object> createResponse(Map<LocalDate, Double> dailyAverages, String attributeName) {
        // Create a list to store data points for each day's average value
        List<Map<String, Object>> dataPoints = new ArrayList<>();
        
        // Iterate through the map of daily averages
        for (Map.Entry<LocalDate, Double> entry : dailyAverages.entrySet()) {
            LocalDate date = entry.getKey();          // Get the date (key)
            double averageValue = entry.getValue();   // Get the daily average value (value)
            
            // Create a data point map with timestamp and average value
            Map<String, Object> dataPoint = new HashMap<>();
            dataPoint.put("timestamp", date);
            dataPoint.put("value", averageValue);
            
            // Add the data point to the list
            dataPoints.add(dataPoint);
        }

        // Create the final response map
        Map<String, Object> response = new HashMap<>();
        response.put("dataPoints", dataPoints);      // Add the list of data points
        response.put("attribute", attributeName);    // Add the attribute name
            
        return response;
    }

    
    /**
     * Creates the desired response format by formatting the daily averages for all attributes.
     *
     * @param dailyAveragesByAttribute A map containing daily averages for each attribute.
     * @return A list of maps representing the desired response format.
     */
    private List<Map<String, Object>> createResponse(Map<String, Map<LocalDate, Double>> dailyAveragesByAttribute) {
        List<Map<String, Object>> responseList = new ArrayList<>();

        // Iterate through the daily averages for each attribute.
        for (Map.Entry<String, Map<LocalDate, Double>> entry : dailyAveragesByAttribute.entrySet()) {
            String attributeName = entry.getKey();
            Map<LocalDate, Double> dailyAverages = entry.getValue();

            List<Map<String, Object>> dataPointsList = new ArrayList<>();
            // Iterate through the daily averages for a specific attribute.
            for (Map.Entry<LocalDate, Double> dailyEntry : dailyAverages.entrySet()) {
                LocalDate date = dailyEntry.getKey();
                Double averageValue = dailyEntry.getValue();

                // Create a data point map for the daily average.
                Map<String, Object> dataPoint = new HashMap<>();
                dataPoint.put("timestamp", date.toString());
                dataPoint.put("value", averageValue);
                dataPointsList.add(dataPoint);
            }

            // Create an attribute data map with data points and attribute name.
            Map<String, Object> attributeData = new HashMap<>();
            attributeData.put("dataPoints", dataPointsList);
            attributeData.put("attribute", attributeName);

            // Add the attribute data to the response list.
            responseList.add(attributeData);
        }

        return responseList;
    }



}

