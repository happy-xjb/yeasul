package com.yealink.controller;

import com.yealink.service.CatalogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/v1/catalog")
public class CatalogController {
    @Autowired
    private CatalogService catalogService;

    @GetMapping("/services")
    public Map<String, List<String>> getCatalogServices(){
        return catalogService.getCatalogServices();
    }

}
