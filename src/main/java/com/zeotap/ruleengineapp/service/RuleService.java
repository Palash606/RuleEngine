package com.zeotap.ruleengineapp.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zeotap.ruleengineapp.entity.Rule;
import com.zeotap.ruleengineapp.model.Node;
import com.zeotap.ruleengineapp.repository.RuleRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Stack;

@Service
public class RuleService {
    private final RuleRepository ruleRepository;

    private final ObjectMapper objectMapper;

    public RuleService(RuleRepository ruleRepository, ObjectMapper objectMapper) {
        this.ruleRepository = ruleRepository;
        this.objectMapper = objectMapper;
    }

    public Node createRule(String ruleString) {
        if (ruleString == null || ruleString.trim().isEmpty()) {
            throw new IllegalArgumentException("Rule String cannot be empty");
        }
        return parseRuleStringToAST(ruleString);
    }

    private Node parseRuleStringToAST(String ruleString) {
        Stack<Node> stack = new Stack<>();
        String[] tokens = ruleString.split(" ");
        for (String token : tokens) {
            if (token.equalsIgnoreCase("AND") || token.equalsIgnoreCase("OR")) {
                Node operatorNode = new Node();
                operatorNode.setType("operator");
                operatorNode.setValue(token);

                if (stack.size() < 2) {
                    throw new IllegalArgumentException("Invalid rule syntax: missing operands for " + token);
                }
                operatorNode.setRight(stack.pop());
                operatorNode.setLeft(stack.pop());

                stack.push(operatorNode);
            } else {
                Node operandNode = new Node();
                operandNode.setType("operand");
                operandNode.setValue(token);
                stack.push(operandNode);
            }
        }
        if (stack.size() != 1) {
            throw new IllegalArgumentException("Invalid rule syntax: unbalanced operators and operands");
        }
        return stack.pop();
    }

    public Node combineRules(List<Node> ruleNodes) {
        if (ruleNodes == null || ruleNodes.isEmpty()) {
            throw new IllegalArgumentException("No rules provided for combination");
        }

        Node combinedRoot = ruleNodes.get(0);

        for (int i = 1; i < ruleNodes.size(); i++) {
            Node combinedNode = new Node();
            combinedNode.setType("operator");
            combinedNode.setValue("AND");
            combinedNode.setLeft(combinedRoot);
            combinedNode.setRight(ruleNodes.get(0));

            combinedRoot = combinedNode;
        }
        return combinedRoot;
    }

    public boolean evaluateRule(Node root, Map<String, Object> data) {
        if (root == null) {
            throw new IllegalArgumentException("Rule AST cannot be null.");
        }
        if (data == null) {
            throw new IllegalArgumentException("Data for evaluation cannot be null.");
        }

        return evaluateNode(root, data);
    }

    private boolean evaluateNode(Node node, Map<String, Object> data) {
        if (node.getType().equals("operand")) {
            return evaluateCondition(node.getValue(), data);
        } else if (node.getType().equals("operator")) {
            boolean leftResult = evaluateNode(node.getLeft(), data);
            boolean rightResult = evaluateNode(node.getRight(), data);

            if ("AND".equalsIgnoreCase(node.getValue())) {
                return leftResult && rightResult;
            } else if ("OR".equalsIgnoreCase(node.getValue())) {
                return leftResult || rightResult;
            } else {
                throw new IllegalArgumentException("Unsupported operator: " + node.getValue());
            }
        }
        return false;
    }

    private boolean evaluateCondition(String condition, Map<String, Object> data) {
        String[] parts = condition.split(" ");

        if (parts.length != 3) {
            throw new IllegalArgumentException("Invalid condition: " + condition);
        }

        String attribute = parts[0];
        String operator = parts[1];
        String value = parts[2];

        Object attributeValue = data.get(attribute);

        if (attributeValue == null) {
            throw new IllegalArgumentException("Attribute " + attribute + " not found in data.");
        }

        try {
            double dataValue = Double.parseDouble(attributeValue.toString());
            double conditionValue = Double.parseDouble(value);

            return switch (operator) {
                case ">" -> dataValue > conditionValue;
                case "<" -> dataValue < conditionValue;
                case ">=" -> dataValue >= conditionValue;
                case "<=" -> dataValue <= conditionValue;
                case "==" -> dataValue == conditionValue;
                case "!=" -> dataValue != conditionValue;
                default -> throw new IllegalArgumentException("Unsupported operator: " + operator);
            };
        } catch (NumberFormatException e) {
            return attributeValue.toString().equalsIgnoreCase(value);
        }
    }

    public Rule saveRule(String ruleString, Node ast) throws JsonProcessingException {
        Rule rule = new Rule();
        rule.setRuleString(ruleString);
        rule.setAstJson(objectMapper.writeValueAsString(ast));
        return ruleRepository.save(rule);
    }

    public List<Rule> getAllRules() {
        return ruleRepository.findAll();
    }

    public Node parseAstFromJson(String astJson) throws JsonProcessingException {
        return objectMapper.readValue(astJson, Node.class);
    }

}
