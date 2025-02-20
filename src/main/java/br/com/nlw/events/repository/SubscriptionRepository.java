package br.com.nlw.events.repository;

import org.springframework.data.repository.CrudRepository;

import br.com.nlw.events.model.Event;
import br.com.nlw.events.model.Subscription;
import br.com.nlw.events.model.User;

public interface SubscriptionRepository extends CrudRepository<Subscription, Integer> {
  public Subscription findByEventAndSubscriber(Event evt, User user);
}
