package br.com.nlw.events.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.nlw.events.dto.SubscriptionRankingByUser;
import br.com.nlw.events.dto.SubscriptionRankingItem;
import br.com.nlw.events.dto.SubscriptionResponse;
import br.com.nlw.events.exceptions.EventNotFoundException;
import br.com.nlw.events.exceptions.SubscriptionConflictException;
import br.com.nlw.events.exceptions.UserIndicadorNotFoundException;
import br.com.nlw.events.model.Event;
import br.com.nlw.events.model.Subscription;
import br.com.nlw.events.model.User;
import br.com.nlw.events.repository.EventRepository;
import br.com.nlw.events.repository.SubscriptionRepository;
import br.com.nlw.events.repository.UserRepository;

@Service
public class SubscriptionService {

  @Autowired
  private EventRepository eventRepository;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private SubscriptionRepository subscriptionRepository;

  private Event findEventByPrettyName(String eventName) {
    return Optional.ofNullable(eventRepository.findByPrettyName(eventName))
      .orElseThrow(() -> new EventNotFoundException("Evento " + eventName + " não existe"));
  }

  private User findOrCreateUser(User user) {
    return Optional.ofNullable(userRepository.findByEmail(user.getEmail()))
      .orElseGet(() -> userRepository.save(user));
  }

  private Optional<User> findUserById(Integer userId) {
    return Optional.ofNullable(userId)
      .flatMap(id -> userRepository.findById(id));
  }

  private void checkSubscriptionConflict(Event event, User subscriber) {
    if (subscriptionRepository.findByEventAndSubscriber(event, subscriber) != null) {
      throw new SubscriptionConflictException("Já existe inscrição para o usuário " + subscriber.getEmail() + " no evento " + event.getPrettyName());
    }
  }

  private Subscription createSubscription(Event event, User subscriber, User indicator) {
    Subscription subscription = new Subscription();

    subscription.setEvent(event);
    subscription.setSubscriber(subscriber);
    subscription.setIndication(indicator);

    return subscription;
  }

  public SubscriptionResponse createNewSubscription(String eventName, User user, Integer userId) {
    Event event = findEventByPrettyName(eventName);
    User subscriber = findOrCreateUser(user);
    Optional<User> indicatorOpt = findUserById(userId);

    checkSubscriptionConflict(event, subscriber);

    Subscription subscription = createSubscription(event, subscriber, indicatorOpt.orElse(null));
    Subscription savedSubscription = subscriptionRepository.save(subscription);

    return new SubscriptionResponse(savedSubscription.getSubscriptionNumber(), 
    "http://codecraft.com/subscription/" + savedSubscription.getEvent().getPrettyName() + "/" + savedSubscription.getSubscriber().getId());
}

  public List<SubscriptionRankingItem> getCompleteRanking(String prettyName) {
    Event event = Optional.ofNullable(eventRepository.findByPrettyName(prettyName))
        .orElseThrow(() -> new EventNotFoundException("Ranking do evento " + prettyName + " não existe"));

    return subscriptionRepository.generateRanking(event.getEventId());
  }

  public SubscriptionRankingByUser getRankingByUser(String prettyName, Integer userId) {
    List<SubscriptionRankingItem> ranking = getCompleteRanking(prettyName);

    SubscriptionRankingItem item = ranking.stream()
      .filter(i -> i.userId().equals(userId))
      .findFirst()
      .orElse(null);

    Integer position = IntStream.range(0, ranking.size())
      .filter(pos -> ranking.get(pos).userId().equals(userId))
      .findFirst()
      .orElseThrow(() -> new UserIndicadorNotFoundException("Não há inscrições para este usuário " + userId));

    return new SubscriptionRankingByUser(item, position + 1);
  }
}
