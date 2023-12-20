package com.example.backend.rabbitMQ;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for setting up RabbitMQ components like queues, exchanges, and bindings.
 */
@Configuration
public class RabbitMQConfig {

  @Value("${rabbitmq.queue.name}")
  private String queue;

  @Value("${rabbitmq.exchange.name}")
  private String exchange;

  @Value("${rabbitmq.routing.key}")
  private String routingKey;

  /**
   * Bean for the name of the RabbitMQ exchange.
   *
   * @return The exchange name as a String.
   */
  @Bean("rabbitExchangeName")
  public String rabbitExchangeName() {
    return exchange;
  }

  /**
   * Bean for the RabbitMQ routing key.
   *
   * @return The routing key as a String.
   */
  @Bean("rabbitRoutingKey")
  public String rabbitRoutingKey() {
    return routingKey;
  }

  /**
   * Bean for the RabbitMQ queue.
   *
   * @return The Queue object.
   */
  @Bean
  Queue queue() {
    return new Queue(queue, false);
  }

  /**
   * Bean for the RabbitMQ DirectExchange.
   *
   * @return The DirectExchange object.
   */
  @Bean
  DirectExchange exchange(){
    return new DirectExchange(exchange);
  }

  /**
   * Bean for binding between the queue and the exchange with a routing key.
   *
   * @return The Binding object.
   */
  @Bean
  Binding binding(){
    return BindingBuilder
        .bind(queue())
        .to(exchange())
        .with(routingKey);
  }

}
