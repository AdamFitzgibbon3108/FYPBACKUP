package com.example.service;

import com.example.model.CveItem;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Service
public class ThreatIntelService {

    private static final String API_URL = "https://cve.circl.lu/api/last";
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public List<CveItem> fetchRecentCves() {
        List<CveItem> cveItems = new ArrayList<>();

        try {
            JsonNode root = objectMapper.readTree(restTemplate.getForObject(API_URL, String.class));
            if (root.isArray()) {
                for (JsonNode node : root) {
                    String id = node.path("id").asText("");
                    String summary = node.path("summary").asText("");
                    String published = node.path("published").asText("");
                    String modified = node.path("modified").asText("");
                    String cvss = node.path("cvss").asText("");
                    String cwe = node.path("cwe").asText("");

                    // Fallback: Try CVE Metadata
                    if (id.isBlank()) {
                        id = node.path("cveMetadata").path("cveId").asText("");
                    }

                    if (summary.isBlank()) {
                        JsonNode descriptions = node.path("containers").path("cna").path("descriptions");
                        if (descriptions.isArray() && descriptions.size() > 0) {
                            summary = descriptions.get(0).path("value").asText("");
                        }
                    }

                    if (published.isBlank()) {
                        published = node.path("cveMetadata").path("datePublished").asText("");
                    }

                    if (modified.isBlank()) {
                        modified = node.path("cveMetadata").path("dateUpdated").asText("");
                    }

                    // Nested CVSS extraction: just version label (e.g., 3.1)
                    if (cvss.isBlank()) {
                        JsonNode metricsArray = node.path("containers").path("cna").path("metrics");
                        if (metricsArray.isArray() && metricsArray.size() > 0) {
                            JsonNode metrics = metricsArray.get(0).path("cvssV3_1");
                            if (!metrics.isMissingNode()) {
                                String vector = metrics.path("vectorString").asText("");
                                if (vector.startsWith("CVSS:3.1")) {
                                    cvss = "3.1";
                                } else if (vector.startsWith("CVSS:3.0")) {
                                    cvss = "3.0";
                                } else if (!vector.isEmpty()) {
                                    cvss = vector;
                                }
                            }
                        }
                    }

                    // CWE fallback
                    if (cwe.isBlank()) {
                        JsonNode problemTypes = node.path("containers").path("cna").path("problemTypes");
                        if (problemTypes.isArray() && problemTypes.size() > 0) {
                            JsonNode descriptions = problemTypes.get(0).path("descriptions");
                            if (descriptions.isArray() && descriptions.size() > 0) {
                                cwe = descriptions.get(0).path("description").asText("");
                            }
                        }
                    }

                    
                    if (id.isBlank() || summary.isBlank() || published.isBlank()) {
                        System.out.println("[DEBUG] Skipping due to missing critical fields: " + id);
                        continue;
                    }

                    if (cvss.isBlank()) cvss = "n/a";
                    if (cwe.isBlank()) cwe = "n/a";
                    if (modified.isBlank()) modified = "n/a";

                    CveItem item = new CveItem(id, summary, published, modified, cvss, cwe);
                    cveItems.add(item);
                    System.out.println("[DEBUG] Parsed CVE: " + item);
                }

                System.out.println("[DEBUG] Total valid CVEs loaded: " + cveItems.size());
            }
        } catch (Exception e) {
            System.err.println("[ERROR] Failed to fetch CVEs: " + e.getMessage());
        }

        return cveItems;
    }
}
