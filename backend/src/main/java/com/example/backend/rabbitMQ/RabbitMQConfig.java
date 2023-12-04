package com.example.backend.rabbitMQ;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// STATUS: Not tested
@Configuration
public class RabbitMQConfig {

  @Value("${rabbitmq.queue.name}")
  private String queue;

  @Value("${rabbitmq.exchange.name}")
  private String exchange;

  @Value("${rabbitmq.routing.key}")
  private String routingKey;

  @Bean("rabbitExchangeName")
  public String rabbitExchangeName() {
    return exchange;
  }

  @Bean("rabbitRoutingKey")
  public String rabbitRoutingKey() {
    return routingKey;
  }

  @Bean
  Queue queue() {
    return new Queue(queue, false);
  }

  @Bean
  DirectExchange exchange(){
    return new DirectExchange(exchange);
  }

  @Bean
  Binding binding(){
    return BindingBuilder
        .bind(queue())
        .to(exchange())
        .with(routingKey);
  }

}
