package com.app.crowdfunding.controller;

import com.app.Crowdfunding.service.StripeService;
import com.app.crowdfunding.model.Campaign;
import com.app.crowdfunding.repository.CampaignRepository;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.BindingResult;

import java.util.List;

@Controller
public class CampaignController {

    private final CampaignRepository repository;
    private final StripeService stripeService; // Utilisation de StripeService

    // Constructeur mis à jour pour Java 17
    public CampaignController(CampaignRepository repository, StripeService stripeService) {
        this.repository = repository;
        this.stripeService = stripeService;
    }

    // --- ACCUEIL ---
    @GetMapping("/")
    public String index(Model model) {
        List<Campaign> campaigns = repository.findAll();
        model.addAttribute("campaigns", campaigns);
        return "index";
    }

    // --- CRÉATION / ÉDITION ---
    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("campaign", new Campaign());
        return "create";
    }

    @PostMapping("/create")
    public String saveCampaign(@Valid @ModelAttribute("campaign") Campaign campaign, 
                               BindingResult result) {
        if (result.hasErrors()) {
            return "create";
        }
        repository.save(campaign);
        return "redirect:/";
    }

    // --- DÉTAILS DE LA CAMPAGNE ---
    @GetMapping("/campaign/{id}")
    public String showCampaign(@PathVariable Long id, Model model) {
        Campaign campaign = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Campagne invalide : " + id));
        model.addAttribute("campaign", campaign);
        return "details";
    }

    // --- SYSTÈME DE DON (API Stripe) ---
    @PostMapping("/campaign/donate")
    public String donate(@RequestParam Long campaignId, @RequestParam Long amount) {
        try {
            // Correction : On utilise 'repository' au lieu de 'campaignService'
            Campaign campaign = repository.findById(campaignId)
                    .orElseThrow(() -> new IllegalArgumentException("ID invalide"));
            
            // Génération de l'URL Stripe
            String checkoutUrl = stripeService.createCheckoutSession(campaign, amount);
            
            return "redirect:" + checkoutUrl; 
        } catch (Exception e) {
            System.err.println("Erreur Stripe : " + e.getMessage());
            return "redirect:/campaign/" + campaignId + "?error=stripe_failed";
        }
    }

    // --- PAGES DE RETOUR (Après Stripe) ---

    @GetMapping("/success")
    public String showSuccessPage() {
        // Stripe redirigera ici après un paiement réussi
        return "success_page"; 
    }

    @GetMapping("/cancel")
    public String showCancelPage() {
        // Optionnel : Une page si l'utilisateur annule le paiement
        return "redirect:/";
    }

    // --- ADMINISTRATION ---
    @GetMapping("/admin")
    public String adminDashboard(Model model) {
        model.addAttribute("campaigns", repository.findAll());
        return "admin";
    }

    @GetMapping("/admin/delete/{id}")
    public String deleteCampaign(@PathVariable Long id) {
        repository.deleteById(id);
        return "redirect:/admin?deleted=success";
    }

    @GetMapping("/admin/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Campaign campaign = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("ID invalide"));
        model.addAttribute("campaign", campaign);
        return "create";
    }

    @PostMapping("/admin/edit")
    public String updateCampaign(@Valid @ModelAttribute("campaign") Campaign campaign, 
                                 BindingResult result) {
        if (result.hasErrors()) {
            return "create";
        }
        repository.save(campaign);
        return "redirect:/admin?updated=success";
    }
}