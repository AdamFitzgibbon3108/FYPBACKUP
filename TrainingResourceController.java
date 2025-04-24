package com.example.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.model.TrainingResource;
import com.example.service.TrainingResourceService;

@Controller
@RequestMapping("/training")
public class TrainingResourceController {

	private final TrainingResourceService trainingResourceService;

	@Autowired
	public TrainingResourceController(TrainingResourceService trainingResourceService) {
		this.trainingResourceService = trainingResourceService;
	}

	// Load training page with resources for a given category
	@GetMapping("/{category}")
	public String getTrainingByCategory(@PathVariable("category") String category, Model model) {
		List<TrainingResource> resources = trainingResourceService.getResourcesByCategory(category);
		model.addAttribute("category", category);
		model.addAttribute("resources", resources);
		return "training-resources";
	}

	// Endpoint for listing all resources across categories (if needed)
	@GetMapping("/all")
	public String getAllTrainingResources(Model model) {
		List<TrainingResource> allResources = trainingResourceService.getAllResources();
		model.addAttribute("category", "All Categories");
		model.addAttribute("resources", allResources);
		return "training-resources";
	}

}
