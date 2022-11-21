package com.alok.gatewayserver.filters;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@Order(1)
@Slf4j
public class TraceRequestFilter implements GlobalFilter {

    @Autowired
    FilterUtility filterUtility;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        HttpHeaders headers = exchange.getRequest().getHeaders();
        if(isCorrelationIdPresent(headers)){
            log.debug("EazyBank-correlation-id found in tracing filter: {}. ", filterUtility.getCorrelationId(headers));
        }
        else {
            String correlationID = generateCorrelationId();
            exchange = filterUtility.setCorrelationId(exchange, correlationID);
            log.debug("EazyBank-correlation-id generated in tracing filter: {}.", correlationID);
        }
        return chain.filter(exchange);
    }

    private boolean isCorrelationIdPresent(HttpHeaders headers) {
        if(filterUtility.getCorrelationId(headers) != null){
            log.info("CORRELATION_ID : {}", filterUtility.getCorrelationId(headers));
            return true;}
        return false;
    }

    private String generateCorrelationId() {
        return java.util.UUID.randomUUID().toString();
    }
}
