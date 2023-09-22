package com.example.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class AttributeValueRequest {
	private Long id;
    private Long attributeId;
    private LocalDateTime timestamp;
    private double value;
}