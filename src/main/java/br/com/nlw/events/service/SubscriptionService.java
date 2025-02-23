package br.com.nlw.events.service;

import java.util.List;
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

  public SubscriptionResponse createNewSubscription(String eventName, User user, Integer userId) {
    Event evt = eventRepository.findByPrettyName(eventName);

    if (evt == null) {
      throw new EventNotFoundException("Evento " + eventName + " não existe");
    }
    
    User userResponse = userRepository.findByEmail(user.getEmail());

    if (userResponse == null) {
      userResponse = userRepository.save(user);
    }

    User indicator = null;

    if (userId != null) {
      indicator = userRepository.findById(userId).orElse(null);

      if (indicator == null) {
        throw new UserIndicadorNotFoundException("Usuario indicador não encontrado");
      }
    }
 
    Subscription subs = new Subscription(); 

    subs.setEvent(evt);
    subs.setSubscriber(userResponse);
    subs.setIndication(indicator);

    Subscription tmpSubscription = subscriptionRepository.findByEventAndSubscriber(evt, userResponse);

    if (tmpSubscription != null) {
      throw new SubscriptionConflictException(" ja existe inscrição para o usuario " + userResponse.getEmail() + " no evento " + evt.getPrettyName());
    }

    Subscription result = subscriptionRepository.save(subs);

    return new SubscriptionResponse(result.getSubscriptionNumber(), "http://codecraft.com/subscription/" + result.getEvent().getPrettyName() + "/" + result.getSubscriber().getId());
  }

  public List<SubscriptionRankingItem> getCompleteRanking(String prettyName) {
    Event event = eventRepository.findByPrettyName((prettyName));

    if (event == null) {
      throw new EventNotFoundException("Ranking do evento " + prettyName + "nao existe");
    }

    return subscriptionRepository.generateRanking(event.getEventId());
  }
  
  public SubscriptionRankingByUser getRankingByUser(String prettyName, Integer userId) {
    List<SubscriptionRankingItem> ranking = getCompleteRanking(prettyName);

    SubscriptionRankingItem item = ranking.stream()
      .filter(i -> i.userId().equals(userId))
      .findFirst()
      .orElse(null);

    if (item == null) {
      throw new UserIndicadorNotFoundException("Nao ha inscricoes para desde usiario " + userId);
    }

    Integer posicao = IntStream.range(0, ranking.size())
      .filter(pos -> ranking.get(pos).userId().equals(userId))
      .findFirst().getAsInt();

    return new SubscriptionRankingByUser(item, posicao + 1);
  }
}
