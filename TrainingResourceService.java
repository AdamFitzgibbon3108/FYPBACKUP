package com.example.service;

import java.util.List;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.model.TrainingResource;
import com.example.repository.TrainingResourceRepository;

@Service
public class TrainingResourceService {

	private final TrainingResourceRepository trainingResourceRepository;

	@Autowired
	public TrainingResourceService(TrainingResourceRepository trainingResourceRepository) {
		this.trainingResourceRepository = trainingResourceRepository;
	}

	// Display all training resources for all category's
	public List<TrainingResource> getAllResources() {
		return trainingResourceRepository.findAll();
	}

	// Fetch all training resources for a given category
	public List<TrainingResource> getResourcesByCategory(String categoryName) {
		return trainingResourceRepository.findByCategoryNameIgnoreCase(categoryName);
	}

	// Optionally, get a single random resource to display as recommendation
	public TrainingResource getRandomResourceByCategory(String categoryName) {
		List<TrainingResource> resources = getResourcesByCategory(categoryName);
		if (resources.isEmpty()) {
			return null;
		}
		return resources.get(new Random().nextInt(resources.size()));
	}
}
