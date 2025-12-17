package com.invoicing.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    
    public static final String INVOICE_EXCHANGE = "invoice.exchange";
    public static final String INVOICE_CREATED_QUEUE = "invoice.created.queue";
    public static final String STOCK_UPDATE_QUEUE = "stock.update.queue";
    public static final String PDF_GENERATION_QUEUE = "pdf.generation.queue";
    public static final String INVOICE_CREATED_ROUTING_KEY = "invoice.created";
    
    @Bean
    public TopicExchange invoiceExchange() {
        return new TopicExchange(INVOICE_EXCHANGE);
    }
    
    @Bean
    public Queue invoiceCreatedQueue() {
        return QueueBuilder.durable(INVOICE_CREATED_QUEUE).build();
    }
    
    @Bean
    public Queue stockUpdateQueue() {
        return QueueBuilder.durable(STOCK_UPDATE_QUEUE).build();
    }
    
    @Bean
    public Queue pdfGenerationQueue() {
        return QueueBuilder.durable(PDF_GENERATION_QUEUE).build();
    }
    
    @Bean
    public Binding invoiceCreatedBinding() {
        return BindingBuilder
                .bind(invoiceCreatedQueue())
                .to(invoiceExchange())
                .with(INVOICE_CREATED_ROUTING_KEY);
    }
    
    @Bean
    public Binding stockUpdateBinding() {
        return BindingBuilder
                .bind(stockUpdateQueue())
                .to(invoiceExchange())
                .with(INVOICE_CREATED_ROUTING_KEY);
    }
    
    @Bean
    public Binding pdfGenerationBinding() {
        return BindingBuilder
                .bind(pdfGenerationQueue())
                .to(invoiceExchange())
                .with(INVOICE_CREATED_ROUTING_KEY);
    }
    
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
    
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter());
        return template;
    }
    
    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(jsonMessageConverter());
        return factory;
    }
}

