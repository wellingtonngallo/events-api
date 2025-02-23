package br.com.nlw.events.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import br.com.nlw.events.dto.SubscriptionRankingByUser;
import br.com.nlw.events.dto.SubscriptionRankingItem;
import br.com.nlw.events.dto.SubscriptionResponse;
import br.com.nlw.events.model.User;
import br.com.nlw.events.service.SubscriptionService;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
public class SubscriptionController {

    @Autowired
    private SubscriptionService subscriptionService;
  
    @PostMapping({"/subscription/{prettyName}", "/subscription/{prettyName}/{userId}"})
    public ResponseEntity<SubscriptionResponse> createSubscription(@PathVariable String prettyName, @RequestBody User subscriber, @PathVariable(required = false) Integer userId) {
      SubscriptionResponse result = subscriptionService.createNewSubscription(prettyName, subscriber, userId);
  
      return ResponseEntity.ok(result);
    }
    
    @GetMapping("/subscription/{prettyName}/ranking")
    public ResponseEntity<List<SubscriptionRankingItem>> generateRankingByEvent(@PathVariable String prettyName) {
      List<SubscriptionRankingItem> result = subscriptionService.getCompleteRanking(prettyName).subList(0, 3);

      return ResponseEntity.ok(result);
    }

    @GetMapping("/subscription/{prettyName}/ranking/{userId}")
    public ResponseEntity<SubscriptionRankingByUser> generateRankingByEventAndUser(@PathVariable String prettyName, @PathVariable Integer userId) {
      SubscriptionRankingByUser result = subscriptionService.getRankingByUser(prettyName, userId);
      
      return ResponseEntity.ok(result);
    }
  
}
