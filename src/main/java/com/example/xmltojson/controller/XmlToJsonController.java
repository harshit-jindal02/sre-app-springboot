
package com.example.xmltojson.controller;

import org.json.JSONObject;
import org.json.XML;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.charset.StandardCharsets;

@RestController
public class XmlToJsonController {

    @Value("${json.storage.path:last.json}")
    private String jsonStoragePath;
    @PostMapping(value = "/tojson", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> xmlToJson(@RequestParam("file") MultipartFile file) {
        try {
            String xml = new String(file.getBytes(), StandardCharsets.UTF_8);
            JSONObject jsonObject = XML.toJSONObject(xml);
            String jsonString = jsonObject.toString(4);

            // Save to disk
            try (FileWriter writer = new FileWriter(jsonStoragePath)) {
                writer.write(jsonString);
            }

            return ResponseEntity.ok("XML converted to JSON and saved.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Failed to process XML: " + e.getMessage());
        }
    }

    // Handles raw XML in the request body
    @PostMapping(value = "/tojson-raw", consumes = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<String> xmlToJsonRaw(@RequestBody String xml) {
        try {
            JSONObject jsonObject = XML.toJSONObject(xml);
            String jsonString = jsonObject.toString(4);

            // Save to disk
            try (FileWriter writer = new FileWriter(jsonStoragePath)) {
                writer.write(jsonString);
            }

            return ResponseEntity.ok("XML converted to JSON and saved.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Failed to process XML: " + e.getMessage());
        }
    }

    @GetMapping(value = "/view", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> viewJson() {
        File file = new File(jsonStoragePath);
        if (!file.exists()) {
            return ResponseEntity.notFound().build();
        }
        try (FileInputStream fis = new FileInputStream(file)) {
            String json = StreamUtils.copyToString(fis, StandardCharsets.UTF_8);
            return ResponseEntity.ok(json);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Error reading stored JSON: " + e.getMessage());
        }
    }
}
