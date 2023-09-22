package com.example.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.entity.Attribute;
import com.example.entity.AttributeValue;
import com.example.entity.Department;
import com.example.entity.Room;
import com.example.entity.Tank;

@Repository
public interface AttributeValueRepository extends JpaRepository<AttributeValue, Long> {

    /**
     * Find attribute values for a specific tank between two dates.
     *
     * @param tank The tank for which to retrieve attribute values.
     * @param attribute The attribute for which to retrieve values.
     * @param start The start date of the time range.
     * @param end The end date of the time range.
     * @return A list of attribute values for the tank and attribute between the specified dates.
     */
    @Query("SELECT av FROM AttributeValue av " +
            "WHERE av.tank = :tank " +
            "AND av.attribute = :attribute " +
            "AND av.timestamp BETWEEN :start AND :end " +
            "ORDER BY av.timestamp ASC")
    List<AttributeValue> findAttributeValuesInTankBetweenDates(
            @Param("tank") Tank tank,
            @Param("attribute") Attribute attribute,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);
    
    @Query("SELECT av FROM AttributeValue av " +
            "WHERE av.tank = :tank " +
            "AND av.timestamp BETWEEN :startDate AND :endDate " +
            "ORDER BY av.timestamp ASC")
     List<AttributeValue> findAttributeValuesInTankBetweenDates(
             @Param("tank") Tank tank,
             @Param("startDate") LocalDateTime startDate,
             @Param("endDate") LocalDateTime endDate);

    /**
     * Find attribute values for a specific room between two dates.
     *
     * @param room The room for which to retrieve attribute values.
     * @param attribute The attribute for which to retrieve values.
     * @param start The start date of the time range.
     * @param end The end date of the time range.
     * @return A list of attribute values for the room and attribute between the specified dates.
     */
    @Query("SELECT av FROM AttributeValue av " +
            "WHERE av.tank.room = :room " +
            "AND av.attribute = :attribute " +
            "AND av.timestamp BETWEEN :start AND :end " +
            "ORDER BY av.timestamp ASC")
    List<AttributeValue> findAttributeValuesInRoomBetweenDates(
            @Param("room") Room room,
            @Param("attribute") Attribute attribute,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);

    /**
     * Find attribute values for a specific department between two dates.
     *
     * @param department The department for which to retrieve attribute values.
     * @param attribute The attribute for which to retrieve values.
     * @param start The start date of the time range.
     * @param end The end date of the time range.
     * @return A list of attribute values for the department and attribute between the specified dates.
     */
    @Query("SELECT av FROM AttributeValue av " +
            "WHERE av.tank.room.department = :department " +
            "AND av.attribute = :attribute " +
            "AND av.timestamp BETWEEN :start AND :end " +
            "ORDER BY av.timestamp ASC")
    List<AttributeValue> findAttributeValuesInDepartmentBetweenDates(
            @Param("department") Department department,
            @Param("attribute") Attribute attribute,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);
    
    /**
     * Retrieves a list of AttributeValues for a specific Tank within a specified timestamp range.
     *
     * @param tank      The Tank entity for which to retrieve AttributeValues.
     * @param startDate The start of the timestamp range.
     * @param endDate   The end of the timestamp range.
     * @return A list of AttributeValues that match the criteria, ordered by Attribute ID.
     */
    @Query("SELECT av FROM AttributeValue av " +
    		"WHERE av.tank = :tank " +
    		"AND av.timestamp BETWEEN :startDate " +
    		"AND :endDate " +
    		"ORDER BY av.attribute.id ASC")
    List<AttributeValue> findByTankAndTimestampBetween(
            @Param("tank") Tank tank,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );
    
    
    /**
     * Deletes all attribute values associated with a specific tank and date.
     *
     * @param tank The tank entity for which attribute values should be deleted.
     * @param date The specific date (yyyy-MM-dd) on which attribute values should be deleted.
     */
    @Modifying
	@Query("DELETE FROM AttributeValue " +
			"av WHERE av.tank = :tank " +
			"AND DATE(av.timestamp) = :date")
	void deleteAttributeValuesByTankAndDate(@Param("tank") Tank tank, @Param("date") LocalDate date);

}