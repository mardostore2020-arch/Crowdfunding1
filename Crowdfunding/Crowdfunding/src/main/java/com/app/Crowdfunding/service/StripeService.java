package com.app.Crowdfunding.service;

import com.app.crowdfunding.model.Campaign;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class StripeService {

    @Value("${stripe.api.key}")
    private String stripeSecretKey;

    public String createCheckoutSession(Campaign campaign, Long amount) throws StripeException {
        Stripe.apiKey = stripeSecretKey;

        SessionCreateParams params = SessionCreateParams.builder()
            .addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
            .setMode(SessionCreateParams.Mode.PAYMENT)
            .setSuccessUrl("http://localhost:8084/success")
            .setCancelUrl("http://localhost:8084/campaign/" + campaign.getId())
            .addLineItem(SessionCreateParams.LineItem.builder()
                .setQuantity(1L)
                .setPriceData(SessionCreateParams.LineItem.PriceData.builder()
                    .setCurrency("usd") // Stripe utilise souvent USD
                    .setUnitAmount(amount * 100) // Montant en cents (ex: 10$ = 1000)
                    .setProductData(SessionCreateParams.LineItem.PriceData.ProductData.builder()
                        .setName("Don pour : " + campaign.getTitle())
                        .build())
                    .build())
                .build())
            .build();

        Session session = Session.create(params);
        return session.getUrl();
    }
}