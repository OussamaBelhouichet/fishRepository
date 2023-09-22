package com.example.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.dto.AttributeValueRequest;
import com.example.entity.AttributeValue;
import com.example.entity.Tank;
import com.example.service.AttributeValueService;
import com.example.service.TankService;


@RestController
@RequestMapping("/tanks")
@CrossOrigin(origins = "*") // Allow requests from any origin
public class TankController {
    
	@Autowired
    private TankService tankService;
	
	@Autowired
	private AttributeValueService attributeValueService;

    @GetMapping
    public List<Tank> getAllTanks() {
        return tankService.getAllTanks();
    }

    @GetMapping("/{id}")
    public Tank getTank(@PathVariable Long id) {
        return tankService.getTankById(id);
    }

    @PostMapping
    public Tank createTank(@RequestBody Tank tank) {
        return tankService.saveTank(tank);
    }

    @DeleteMapping("/{id}")
    public void deleteTank(@PathVariable Long id) {
        tankService.deleteTank(id);
    }
    
    @PostMapping("/{tankId}/attributeValues")
    public Tank saveAttributeValuesToTank(
            @PathVariable Long tankId,
            @RequestBody List<AttributeValueRequest> attributeValueRequests,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate specificDate) { 
    	
    	return tankService.saveAttributeValuesToTankWithDate(tankId, attributeValueRequests, specificDate);
    }
    
    @GetMapping("/{id}/attribute-values/{date}")
    public List<AttributeValue> getTankAttributeValuesByDate(
        @PathVariable Long id,
        @PathVariable String date
    ) {
        Tank tank = tankService.getTankById(id);
        LocalDate localDate = LocalDate.parse(date);
        LocalDateTime startDateTime = localDate.atStartOfDay(); // Convert LocalDate to LocalDateTime
        LocalDateTime endDateTime = localDate.plusDays(1).atStartOfDay(); // Next day at start time

        return attributeValueService.getByTankAndDateRange(tank, startDateTime, endDateTime);
    }

}
