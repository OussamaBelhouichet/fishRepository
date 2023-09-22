package com.example.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.entity.Attribute;
import com.example.repository.AttributeRepository;

@Service
public class AttributeService {
	
    @Autowired
    private AttributeRepository attributeRepository;

    public List<Attribute> getAllAttributes() {
        return attributeRepository.findAll();
    }

    public Attribute getAttributeById(Long id) {
        return attributeRepository.findById(id).orElse(null);
    }

    public Attribute saveAttribute(Attribute attribute) {
        return attributeRepository.save(attribute);
    }

    public void deleteAttribute(Long id) {
        attributeRepository.deleteById(id);
    }
}