package com.example.service;

import com.example.model.CveItem;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Service
public class ThreatIntelService {

    private static final Logger logger = LoggerFactory.getLogger(ThreatIntelService.class);

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public List<CveItem> fetchLatestCves() {
        logger.info("==> Entered fetchLatestCves()");
        List<CveItem> cveItems = new ArrayList<>();
        String url = "https://cve.circl.lu/api/last";

        try {
            logger.info("Sending GET request to {}", url);
            String response = restTemplate.getForObject(url, String.class);
            logger.debug("Received raw API response:\n{}", response.substring(0, Math.min(response.length(), 1000)));

            JsonNode root = objectMapper.readTree(response);

            if (root.isArray()) {
                logger.info("API response is an array. Parsing...");
                for (JsonNode node : root) {
                    String id = node.path("id").asText("n/a");

                    // Extract summary
                    String summary = "n/a";
                    JsonNode descriptions = node.path("descriptions");
                    if (descriptions.isArray() && descriptions.size() > 0) {
                        for (JsonNode desc : descriptions) {
                            if ("en".equals(desc.path("lang").asText())) {
                                summary = desc.path("value").asText("n/a");
                                break;
                            }
                        }
                    }

                    // Extract published and modified
                    String published = node.path("published").asText("n/a");
                    String modified = node.path("lastModified").asText("n/a");

                    // Extract CVSS score
                    String baseScore = "n/a";
                    JsonNode metrics = node.path("metrics").path("cvssMetricV31");
                    if (metrics.isArray() && metrics.size() > 0) {
                        JsonNode cvssData = metrics.get(0).path("cvssData");
                        if (!cvssData.isMissingNode()) {
                            baseScore = cvssData.path("baseScore").asText("n/a");
                        }
                    }

                    // Extract CWE
                    String cwe = "n/a";
                    JsonNode weaknesses = node.path("weaknesses");
                    if (weaknesses.isArray() && weaknesses.size() > 0) {
                        JsonNode descriptionsNode = weaknesses.get(0).path("description");
                        if (descriptionsNode.isArray() && descriptionsNode.size() > 0) {
                            cwe = descriptionsNode.get(0).path("value").asText("n/a");
                        }
                    }

                    logger.debug("→ CVE parsed | ID={} | CVSS={} | CWE={} | Published={} | Summary={}",
                            id, baseScore, cwe, published, summary.substring(0, Math.min(summary.length(), 60)));

                    cveItems.add(new CveItem(id, summary, published, modified, baseScore, cwe));
                }
                logger.info("✅ Parsed {} CVEs successfully", cveItems.size());
            } else {
                logger.warn("❌ API returned a non-array root JSON structure.");
            }

        } catch (Exception e) {
            logger.error("❌ Exception during CVE fetch: {}", e.getMessage(), e);
        }

        logger.info("<== Exiting fetchLatestCves()");
        return cveItems;
    }
}
