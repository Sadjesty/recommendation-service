package com.xm.recommendationservice.interceptor;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Bucket4j;
import io.github.bucket4j.ConsumptionProbe;
import io.github.bucket4j.Refill;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.Duration;

@Component
public class RateLimitInterceptor implements HandlerInterceptor {

    private final Bucket bucket;

    public RateLimitInterceptor() {
        Bandwidth limit = Bandwidth.classic(4, Refill.greedy(4, Duration.ofMinutes(1)));
        this.bucket = Bucket4j.builder()
                .addLimit(limit)
                .build();
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);
        if (probe.isConsumed()) {
            return true;
        } else {
            response.sendError(HttpStatus.TOO_MANY_REQUESTS.value(),
                    "You have exhausted your API Request Quota");
            return false;
        }
    }
}
