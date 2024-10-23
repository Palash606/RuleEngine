package com.zeotap.ruleengineapp.controller;

import com.zeotap.ruleengineapp.model.Node;
import com.zeotap.ruleengineapp.service.RuleService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/rules")
public class RuleController {

    private final RuleService ruleService;

    public RuleController(RuleService ruleService) {
        this.ruleService = ruleService;
    }

    @PostMapping("/create")
    public ResponseEntity<Node> createRule(@RequestBody String ruleString) {
        Node rootNode = ruleService.createRule(ruleString);
        return ResponseEntity.ok(rootNode);
    }

    @PostMapping("/combine")
    public ResponseEntity<Node> combineRules(@RequestBody List<String> ruleStrings) {
        List<Node> nodes = ruleStrings.stream()
                .map(ruleService::createRule)
                .collect(Collectors.toList());
        Node combinedNode = ruleService.combineRules(nodes);
        return ResponseEntity.ok(combinedNode);
    }

    @SuppressWarnings("unchecked")
    @PostMapping("/evaluate")
    public ResponseEntity<Boolean> evaluateRule(@RequestBody Map<String, Object> data) {
        Node rootNode = ruleService.createRule(data.get("ruleString").toString());
        boolean result = ruleService.evaluateRule(rootNode, (Map<String, Object>) data.get("attributes"));
        return ResponseEntity.ok(result);
    }
}
