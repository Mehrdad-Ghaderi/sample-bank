package com.mehrdad.sample.bank.api.error;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Component
public class ProblemDetailsFactory {

    private static final String PROBLEM_TYPE_BASE = "https://api.sample-bank.local/problems/";

    public ProblemDetail create(
            HttpStatus status,
            String errorCode,
            String title,
            String detail,
            HttpServletRequest request
    ) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(status, detail);
        problem.setType(problemType(errorCode));
        problem.setTitle(title);
        problem.setInstance(URI.create(request.getRequestURI()));
        problem.setProperty("errorCode", errorCode);
        problem.setProperty("timestamp", OffsetDateTime.now(ZoneOffset.UTC));
        return problem;
    }

    private URI problemType(String errorCode) {
        return URI.create(PROBLEM_TYPE_BASE + errorCode.toLowerCase().replace('_', '-'));
    }
}
