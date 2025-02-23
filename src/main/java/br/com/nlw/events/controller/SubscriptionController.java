package br.com.nlw.events.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import br.com.nlw.events.dto.ErrorMessage;
import br.com.nlw.events.dto.SubscriptionResponse;
import br.com.nlw.events.exceptions.EventNotFoundException;
import br.com.nlw.events.exceptions.SubscriptionConflictException;
import br.com.nlw.events.exceptions.UserIndicadorNotFoundException;
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
  public ResponseEntity<?> createSubscription(@PathVariable String prettyName, @RequestBody User subscriber, @PathVariable(required = false) Integer userId) {

    try {
      SubscriptionResponse result = subscriptionService.createNewSubscription(prettyName, subscriber, userId);

      if (result != null) {
        return ResponseEntity.ok(result);
      }
    } catch (EventNotFoundException e) {
      return ResponseEntity.status(404).body(new ErrorMessage(e.getMessage()));
    }
    catch(SubscriptionConflictException e) {
      return ResponseEntity.status(409).body(new ErrorMessage(e.getMessage()));
    }
    catch(UserIndicadorNotFoundException e) {
      return ResponseEntity.status(404).body(new ErrorMessage(e.getMessage()));
    }

    return ResponseEntity.badRequest().build();
  }
  
  @GetMapping("/subscription/{prettyName}/ranking")
  public ResponseEntity<?> generateRankingByEvent(@PathVariable String prettyName) {
    try {
      return ResponseEntity.ok(subscriptionService.getCompleteRanking(prettyName).subList(0, 3));
    } catch(EventNotFoundException e) {
      return ResponseEntity.status(404).body(new ErrorMessage((e.getMessage())));
    }
  }

  @GetMapping("/subscription/{prettyName}/ranking/{userId}")
  public ResponseEntity<?> generateRankingByEventAndUser(@PathVariable String prettyName, @PathVariable Integer userId) {
    try {
      return ResponseEntity.ok(subscriptionService.getRankingByUser(prettyName, userId));
    } catch (Exception e) {
      return ResponseEntity.status(404).body(new ErrorMessage(e.getMessage()));
    }
  }
  
}
