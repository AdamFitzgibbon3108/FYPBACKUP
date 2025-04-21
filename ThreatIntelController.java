package com.example.controller;

import com.example.model.CveItem;
import com.example.service.ThreatIntelService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class ThreatIntelController {

    private final ThreatIntelService threatIntelService;

    public ThreatIntelController(ThreatIntelService threatIntelService) {
        this.threatIntelService = threatIntelService;
    }

    @GetMapping("/threat-intel")
    public String showThreatIntel(Model model) {
        List<CveItem> cveItems = threatIntelService.fetchRecentCves();
        model.addAttribute("cves", cveItems);
        return "threat-intel";
    }

    @GetMapping("/threat-alerts")
    public String showThreatAlertsDashboard() {
        return "threat-alerts";
    }
}
