package com.example.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import com.example.entity.Department;
import com.example.entity.Room;
import com.example.entity.Tank;
import com.example.repository.DepartmentRepository;

@Service
public class DepartmentService {
	
	@Autowired
    private DepartmentRepository departmentRepository;
	
	@Autowired 
	private TankService tankService;
	
	@Autowired
	private RoomService roomService;

    public List<Department> getAllDepartments() {
        return departmentRepository.findAll();
    }

    public Department getDepartmentById(Long id) {
        return departmentRepository.findById(id).orElse(null);
    }

    public Department saveDepartment(Department department) {
        return departmentRepository.save(department);
    }
    
    public List<Department> saveDepartments(List<Department> departments) {
        return departmentRepository.saveAll(departments);
    }

    public void deleteDepartment(Long id) {
        departmentRepository.deleteById(id);
    }
    
    /**
     * Creates or updates a list of departments based on the provided JSON payload.
     * Departments, rooms, and tanks that are not present in the payload will be deleted.
     * @param departments A list of departments to create or update.
     * @return A list of saved departments after processing.
     */
    
    
    public List<Department> createOrUpdateDepartments(@RequestBody List<Department> departments) {
        List<Department> savedDepartments = new ArrayList<>();
        List<Long> departmentIdsToKeep = new ArrayList<>();
        List<Long> roomIdsToKeep = new ArrayList<>();
        List<Long> tankIdsToKeep = new ArrayList<>();

        // Get a list of all existing department IDs before the update
        List<Long> existingDepartmentIds = getAllDepartments()
                .stream()
                .map(Department::getId)
                .collect(Collectors.toList());

        for (Department department : departments) {       

            // Iterate through rooms and update/create them
            List<Room> updatedRooms = new ArrayList<>();
            for (Room room : department.getRooms()) {
            	
            	// Iterate through tanks and update/create them
            	List<Tank> updatedTanks = new ArrayList<>();
                for (Tank tank : room.getTanks()) {
                    if (tank.getId() == null) {
                        // Create a new tank
                        tank.setRoom(room); // Set the room
                    } else {
                        // Check if the tank with the same ID exists
                        Tank existingTank = tankService.getTankById(tank.getId());
                        if (existingTank != null) {
                            // Update existing tank fields
                            existingTank.setName(tank.getName());
                            tank = existingTank;
                        }
                    }

                    // Add tank IDs to the list of IDs to keep
                    tankIdsToKeep.add(tank.getId());

                    updatedTanks.add(tank);
                }
            	
                if (room.getId() == null) {
                    // Create a new room
                    room.setDepartment(department); // Set the department
                } else {
                    // Check if the room with the same ID exists
                    Room existingRoom = roomService.getRoomById(room.getId());
                    if (existingRoom != null) {
                        // Update existing room fields
                        existingRoom.setName(room.getName());
                        room = existingRoom;
                    }
                }

                // Add room IDs to the list of IDs to keep
                roomIdsToKeep.add(room.getId());

                // Check if the department with the same ID exists
                if (department.getId() != null) {
                    Department existingDepartment = getDepartmentById(department.getId());
                    if (existingDepartment != null) {
                        // Update existing department fields
                        existingDepartment.setName(department.getName());
                        department = existingDepartment;
                    }
                }
                // Set the updated list of tanks to the room
                room.getTanks().clear();
                room.getTanks().addAll(updatedTanks);

                // Add the room to the list of updated rooms
                updatedRooms.add(room);
            }
            // Set the updated list of rooms to the department
            department.getRooms().clear();
            department.getRooms().addAll(updatedRooms);

            // Save or update the department and its related entities
            savedDepartments.add(saveDepartment(department));

            // Add department ID to the list of IDs to keep
            departmentIdsToKeep.add(department.getId());
        }

        // Delete departments that were not in the JSON payload
        existingDepartmentIds.removeAll(departmentIdsToKeep);
        for (Long departmentId : existingDepartmentIds) {
            deleteDepartment(departmentId);
        }

        // Retrieve all existing room IDs
        List<Long> existingRoomIds = roomService.getAllRooms()
                .stream()
                .map(Room::getId)
                .collect(Collectors.toList());

        System.out.println(roomIdsToKeep);
        // Delete rooms that were not in the JSON payload
        existingRoomIds.removeAll(roomIdsToKeep);
        for (Long roomId : existingRoomIds) {
            roomService.deleteRoom(roomId);
        }

        // Delete tanks that were not in the JSON payload
        List<Long> existingTankIds = tankService.getAllTanks()
                .stream()
                .map(Tank::getId)
                .collect(Collectors.toList());
        existingTankIds.removeAll(tankIdsToKeep);
        for (Long tankId : existingTankIds) {
            tankService.deleteTank(tankId);
        }

        return savedDepartments;
    }
    
    
    
//    public List<Department> createOrUpdateDepartments(@RequestBody List<Department> departments) {
//        List<Department> savedDepartments = new ArrayList<>();
//        List<Long> departmentIdsToKeep = new ArrayList<>();
//        List<Long> roomIdsToKeep = new ArrayList<>();
//        List<Long> tankIdsToKeep = new ArrayList<>();
//
//        List<Long> existingDepartmentIds = getAllExistingDepartmentIds();
//
//        for (Department department : departments) {
//            processDepartment(department, departmentIdsToKeep, roomIdsToKeep, tankIdsToKeep);
//            savedDepartments.add(saveDepartment(department));
//        }
//
//        deleteDepartmentsNotInPayload(existingDepartmentIds, departmentIdsToKeep);
//        deleteRoomsNotInPayload(roomIdsToKeep);
//        deleteTanksNotInPayload(tankIdsToKeep);
//
//        return savedDepartments;
//    }
//
//    /**
//     * Get a list of all existing department IDs.
//     *
//     * @return A list of existing department IDs.
//     */
//    private List<Long> getAllExistingDepartmentIds() {
//        return getAllDepartments()
//                .stream()
//                .map(Department::getId)
//                .collect(Collectors.toList());
//    }
//
//    /**
//     * Process a department, including rooms and tanks within it.
//     *
//     * @param department         The department to process.
//     * @param departmentIdsToKeep List of department IDs to keep track of.
//     * @param roomIdsToKeep      List of room IDs to keep track of.
//     * @param tankIdsToKeep      List of tank IDs to keep track of.
//     */
//    private void processDepartment(Department department, List<Long> departmentIdsToKeep,
//                                   List<Long> roomIdsToKeep, List<Long> tankIdsToKeep) {
//    	
//    	processRoomsInDepartment(department, roomIdsToKeep, tankIdsToKeep);
//    	
//    	Long departmentId = department.getId();
//
//        if (departmentId != null) {
//            Department existingDepartment = getDepartmentById(departmentId);
//            if (existingDepartment != null) {
//                existingDepartment.setName(department.getName());
//                department = existingDepartment;
//            }
//        }
//
//        departmentIdsToKeep.add(departmentId);
//    }
//
//    /**
//     * Process rooms within a department, including tanks within each room.
//     *
//     * @param department   The department containing the rooms.
//     * @param roomIdsToKeep List of room IDs to keep track of.
//     * @param tankIdsToKeep List of tank IDs to keep track of.
//     */
//    private void processRoomsInDepartment(Department department, List<Long> roomIdsToKeep, List<Long> tankIdsToKeep) {
//        List<Room> updatedRooms = new ArrayList<>();
//
//        for (Room room : department.getRooms()) {
//        	processTanksInRoom(room, tankIdsToKeep);
//        	
//            Long roomId = room.getId();
//            if (roomId == null) {
//                room.setDepartment(department);
//            } else {
//                Room existingRoom = roomService.getRoomById(roomId);
//                if (existingRoom != null) {
//                    existingRoom.setName(room.getName());
//                    room = existingRoom;
//                }
//            }
//
//            roomIdsToKeep.add(roomId);
//
//            updatedRooms.add(room);
//        }
//
//        department.getRooms().clear();
//        department.getRooms().addAll(updatedRooms);
//    }
//
//    /**
//     * Process tanks within a room.
//     *
//     * @param room         The room containing the tanks.
//     * @param tankIdsToKeep List of tank IDs to keep track of.
//     */
//    private void processTanksInRoom(Room room, List<Long> tankIdsToKeep) {
//        List<Tank> updatedTanks = new ArrayList<>();
//
//        for (Tank tank : room.getTanks()) {
//            Long tankId = tank.getId();
//            if (tankId == null) {
//                tank.setRoom(room);
//            } else {
//                Tank existingTank = tankService.getTankById(tankId);
//                if (existingTank != null) {
//                    existingTank.setName(tank.getName());
//                    tank = existingTank;
//                }
//            }
//
//            tankIdsToKeep.add(tankId);
//
//            updatedTanks.add(tank);
//        }
//
//        room.getTanks().clear();
//        room.getTanks().addAll(updatedTanks);
//    }
//
//    /**
//     * Delete departments that were not present in the JSON payload.
//     *
//     * @param existingDepartmentIds List of existing department IDs.
//     * @param departmentIdsToKeep   List of department IDs to keep.
//     */
//    private void deleteDepartmentsNotInPayload(List<Long> existingDepartmentIds, List<Long> departmentIdsToKeep) {
//        existingDepartmentIds.removeAll(departmentIdsToKeep);
//        for (Long departmentId : existingDepartmentIds) {
//            deleteDepartment(departmentId);
//        }
//    }
//
//    /**
//     * Delete rooms that were not present in the JSON payload.
//     *
//     * @param roomIdsToKeep List of room IDs to keep.
//     */
//    private void deleteRoomsNotInPayload(List<Long> roomIdsToKeep) {
//        List<Long> existingRoomIds = roomService.getAllRooms()
//                .stream()
//                .map(Room::getId)
//                .collect(Collectors.toList());
//
//        existingRoomIds.removeAll(roomIdsToKeep);
//        for (Long roomId : existingRoomIds) {
//            roomService.deleteRoom(roomId);
//        }
//    }
//
//    /**
//     * Delete tanks that were not present in the JSON payload.
//     *
//     * @param tankIdsToKeep List of tank IDs to keep.
//     */
//    private void deleteTanksNotInPayload(List<Long> tankIdsToKeep) {
//        List<Long> existingTankIds = tankService.getAllTanks()
//                .stream()
//                .map(Tank::getId)
//                .collect(Collectors.toList());
//
//        existingTankIds.removeAll(tankIdsToKeep);
//        for (Long tankId : existingTankIds) {
//            tankService.deleteTank(tankId);
//        }
//    }

    

}
