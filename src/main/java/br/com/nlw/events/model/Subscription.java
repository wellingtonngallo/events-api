package br.com.nlw.events.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "tbl_subscription")
@Data
public class Subscription {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "subscription_number")
  private Integer subscriptionNumber;

  @ManyToOne
  @JoinColumn(name = "event_id")
  private Event event;

  @ManyToOne
  @JoinColumn(name = "subscribed_user_id")
  private User subscriber;

  @ManyToOne
  @JoinColumn(name = "indication_user_id", nullable = true)
  private User indication;
}