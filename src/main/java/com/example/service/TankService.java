package com.example.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.dto.AttributeValueRequest;
import com.example.entity.Attribute;
import com.example.entity.AttributeValue;
import com.example.entity.Tank;
import com.example.repository.AttributeValueRepository;
import com.example.repository.TankRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class TankService {

	@Autowired
    private TankRepository tankRepository;
	
	@Autowired
	private AttributeService attributeService;
	
	@Autowired
	private AttributeValueRepository attributeValueRepository;

    public List<Tank> getAllTanks() {
        return tankRepository.findAll();
    }

    public Tank getTankById(Long id) {
        return tankRepository.findById(id).orElse(null);
    }

    public Tank saveTank(Tank tank) {
        return tankRepository.save(tank);
    }

    public void deleteTank(Long id) {
        tankRepository.deleteById(id);
    }
    

//    /**
//     * Save or update attribute values for a tank.
//     *
//     * @param tankId                  The ID of the tank to which attribute values belong.
//     * @param attributeValueRequests  A list of attribute value requests to save or update.
//     * @return                        The tank entity after saving or updating attribute values.
//     * @throws EntityNotFoundException If the tank with the given ID is not found.
//     */
//    public Tank saveAttributeValuesToTank(Long tankId, List<AttributeValueRequest> attributeValueRequests) {
//        // Find the tank by its ID or return null if not found
//        Tank tank = tankRepository.findById(tankId).orElse(null);
//
//        // Throw an exception if the tank is not found
//        if (tank == null) {
//            throw new EntityNotFoundException("Tank not found with ID: " + tankId);
//        }
//
//        // Lists to store attribute values to be added and updated
//        List<AttributeValue> attributeValuesToAdd = new ArrayList<>();
//        List<AttributeValue> attributeValuesToUpdate = new ArrayList<>();
//
//        // Iterate through the attribute value requests
//        for (AttributeValueRequest request : attributeValueRequests) {
//            // Attribute ID validation
//            Attribute attribute = attributeService.getAttributeById(request.getAttributeId());
//
//            // Check if the request has an ID (update) or not (add)
//            if (request.getId() == null) {
//                // Request does not have an ID, so it's a new attribute value
//                if (attribute == null) {
//                    // Invalid attribute ID, throw an exception
//                    throw new EntityNotFoundException("Invalid attribute ID: " + request.getAttributeId());
//                }
//
//                // Create a new attribute value and add it to the list of values to be added
//                AttributeValue attributeValue = new AttributeValue();
//                attributeValue.setTank(tank);
//                attributeValue.setAttribute(attribute);
//                attributeValue.setTimestamp(request.getTimestamp());
//                attributeValue.setValue(request.getValue());
//
//                attributeValuesToAdd.add(attributeValue);
//            } else {
//                // Request has an ID, indicating an update to an existing attribute value
//                // Find the existing attribute value to update
//                AttributeValue result = tank.getAttributeValues().stream()
//                        .filter(atr -> atr.getId().equals(request.getId()))
//                        .findFirst()
//                        .orElse(null);
//
//                if (result != null) {
//                    // Update existing values with the new data
//                    result.setTank(tank);
//                    result.setAttribute(attribute);
//                    result.setTimestamp(request.getTimestamp());
//                    result.setValue(request.getValue());
//                    attributeValuesToUpdate.add(result);
//                }
//            }
//        }
//
//        // Add the updated and new attribute values to the tank
//        tank.getAttributeValues().addAll(attributeValuesToUpdate);
//        tank.getAttributeValues().addAll(attributeValuesToAdd);
//
//        // Save the tank with the updated attribute values and return it
//        return tankRepository.save(tank);
//    }
    
    /**
     * Save or update attribute values for a tank and remove values not present in the JSON request for a specific date.
     *
     * @param tankId                  The ID of the tank to which attribute values belong.
     * @param attributeValueRequests  A list of attribute value requests to save or update.
     * @param specificDate            The specific date for which to remove attribute values not in the request.
     * @return                        The tank entity after saving or updating attribute values.
     * @throws EntityNotFoundException If the tank with the given ID is not found.
     */
    @Transactional
    public Tank saveAttributeValuesToTankWithDate(Long tankId, List<AttributeValueRequest> attributeValueRequests, LocalDate specificDate) {
        // Find the tank by its ID or return null if not found
        Tank tank = tankRepository.findById(tankId).orElse(null);

        // Throw an exception if the tank is not found
        if (tank == null) {
            throw new EntityNotFoundException("Tank not found with ID: " + tankId);
        }

        // Lists to store attribute values to be added, updated, and removed
        List<AttributeValue> attributeValuesToAdd = new ArrayList<>();

        // Iterate through the attribute value requests
        for (AttributeValueRequest request : attributeValueRequests) {
            // Attribute ID validation
            Attribute attribute = attributeService.getAttributeById(request.getAttributeId());


                // Create a new attribute value and add it to the list of values to be added
                AttributeValue attributeValue = new AttributeValue();
                attributeValue.setTank(tank);
                attributeValue.setAttribute(attribute);
                attributeValue.setTimestamp(request.getTimestamp());
                attributeValue.setValue(request.getValue());

                attributeValuesToAdd.add(attributeValue);
            } 

       

        // Remove attribute values not present in the JSON request for the specific date
        attributeValueRepository.deleteAttributeValuesByTankAndDate(tank, specificDate);

        // Add attribute values to the tank
        tank.getAttributeValues().addAll(attributeValuesToAdd);

        // Save the tank with the updated attribute values and return it
        return tankRepository.save(tank);
    }


}
