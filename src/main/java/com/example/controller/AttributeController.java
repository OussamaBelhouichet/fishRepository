package com.example.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.entity.Attribute;
import com.example.service.AttributeService;

@RestController
@RequestMapping("/attributes")
@CrossOrigin(origins = "*") // Allow requests from any origin
public class AttributeController {
    @Autowired
    private AttributeService attributeService;

    @GetMapping
    public List<Attribute> getAllAttributes() {
        return attributeService.getAllAttributes();
    }

    @GetMapping("/{id}")
    public Attribute getAttribute(@PathVariable Long id) {
        return attributeService.getAttributeById(id);
    }

    @PostMapping
    public Attribute createAttribute(@RequestBody Attribute attribute) {
        return attributeService.saveAttribute(attribute);
    }

    @DeleteMapping("/{id}")
    public void deleteAttribute(@PathVariable Long id) {
        attributeService.deleteAttribute(id);
    }
}
