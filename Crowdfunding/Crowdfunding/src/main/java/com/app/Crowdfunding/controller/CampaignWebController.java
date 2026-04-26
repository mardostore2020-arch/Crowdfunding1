package com.app.crowdfunding.controller;

import com.app.crowdfunding.model.Campaign;
import com.app.crowdfunding.repository.CampaignRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

//@Controller
public class CampaignWebController {

    // Utilisation de @Autowired pour garantir que Spring injecte bien le repository
    @Autowired
    private CampaignRepository repository;

    // Affiche la liste des campagnes sur la page d'accueil
    @GetMapping("/")
    public String index(Model model) {
        List<Campaign> campaigns = repository.findAll();
        model.addAttribute("campaigns", campaigns);
        return "index"; 
    }

    // Affiche le formulaire de création
    @GetMapping("/create")
    public String showCreateForm(Model model) {
        // Indispensable pour que Thymeleaf puisse lier les champs du formulaire
        model.addAttribute("campaign", new Campaign());
        return "create-campaign";
    }

    // Enregistre la campagne dans la base MySQL
    @PostMapping("/save")
    public String saveCampaign(@ModelAttribute("campaign") Campaign campaign) {
        repository.save(campaign);
        return "redirect:/"; // Retour à l'accueil
    }
}