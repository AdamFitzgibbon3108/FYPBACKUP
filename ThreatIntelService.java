package com.example.service;

import com.example.model.CveItem;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class ThreatIntelService {

    private static final String API_URL = "https://cve.circl.lu/api/last";
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public List<CveItem> fetchRecentCves() {
        List<CveItem> cveItems = new ArrayList<>();

        try {
            String response = restTemplate.getForObject(API_URL, String.class);
            JsonNode root = objectMapper.readTree(response);

            int fallbackCounter = 1;

            if (root.isArray()) {
                for (JsonNode node : root) {
                    String id = extractCveId(node, fallbackCounter++);
                    String summary = extractSummary(node);
                    String published = node.path("cveMetadata").path("datePublished").asText("n/a");
                    String modified = node.path("cveMetadata").path("dateUpdated").asText("n/a");
                    String baseScore = extractBaseScore(node);
                    String cwe = extractCwe(node);

                    CveItem item = new CveItem(id, summary, published, modified, baseScore, cwe);
                    cveItems.add(item);
                }
            }
        } catch (Exception e) {
            System.err.println("[ERROR] Failed to fetch CVEs: " + e.getMessage());
        }

        return cveItems;
    }

    private String extractCveId(JsonNode node, int fallbackCounter) {
        JsonNode aliases = node.path("aliases");
        if (aliases.isArray() && aliases.size() > 0) return aliases.get(0).asText();
        String id = node.path("cveMetadata").path("cveId").asText();
        return id.isBlank() ? "TEMP-ID-" + fallbackCounter : id;
    }

    private String extractSummary(JsonNode node) {
        JsonNode descriptions = node.path("containers").path("cna").path("descriptions");
        if (descriptions.isArray() && descriptions.size() > 0)
            return descriptions.get(0).path("value").asText("n/a");

        String[] fallbacks = {
            node.path("containers").path("cna").path("title").asText(),
            node.path("details").asText(),
            node.path("title").asText(),
            node.path("summary").asText()
        };
        for (String s : fallbacks) if (!s.isBlank()) return s;
        return "n/a";
    }

    private String extractBaseScore(JsonNode node) {
        JsonNode metricsArray = node.path("containers").path("cna").path("metrics");
        if (metricsArray.isArray() && metricsArray.size() > 0) {
            JsonNode metrics = metricsArray.get(0);
            if (metrics.has("cvssV3_1"))
                return metrics.path("cvssV3_1").path("baseScore").asText("n/a");
            if (metrics.has("cvssV3_0"))
                return metrics.path("cvssV3_0").path("baseScore").asText("n/a");
        }

        // GitHub-style fallback
        JsonNode severityArray = node.path("severity");
        if (severityArray.isArray()) {
            for (JsonNode sev : severityArray) {
                String scoreStr = sev.path("score").asText("");
                if (scoreStr.startsWith("CVSS:3.")) {
                    Matcher matcher = Pattern.compile(".*?(\\d+\\.\\d+)$").matcher(scoreStr);
                    if (matcher.find()) return matcher.group(1);
                    return extractScoreFromVector(scoreStr);
                }
            }
        }

        return "n/a";
    }

    private String extractCwe(JsonNode node) {
        JsonNode cweArray = node.path("database_specific").path("cwe_ids");
        if (cweArray.isArray() && cweArray.size() > 0)
            return cweArray.get(0).asText("n/a");

        JsonNode problemTypes = node.path("containers").path("cna").path("problemTypes");
        if (problemTypes.isArray() && problemTypes.size() > 0) {
            JsonNode descs = problemTypes.get(0).path("descriptions");
            if (descs.isArray() && descs.size() > 0) {
                String fallback = descs.get(0).path("description").asText("n/a");
                Matcher matcher = Pattern.compile("CWE-\\d+").matcher(fallback);
                return matcher.find() ? matcher.group() : fallback;
            }
        }
        return "n/a";
    }

    private String extractScoreFromVector(String vector) {
        if (vector.contains("AV:N") && vector.contains("AC:L")) return "7.5";
        if (vector.contains("AV:L")) return "4.0";
        return "n/a";
    }
}
