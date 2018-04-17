package dev.local.gtm.api.web.exception;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.context.request.NativeWebRequest;
import org.zalando.problem.DefaultProblem;
import org.zalando.problem.Problem;
import org.zalando.problem.ProblemBuilder;
import org.zalando.problem.spring.web.advice.ProblemHandling;
import org.zalando.problem.spring.web.advice.validation.ConstraintViolationProblem;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
@RequiredArgsConstructor
public class ExceptionTranslator implements ProblemHandling {

    private final HttpServletRequest request;

    @Override
    public ResponseEntity<Problem> process(ResponseEntity<Problem> entity) {
        if (entity.getBody() == null) {
            return entity;
        }
        Problem problem = entity.getBody();
        if (!(problem instanceof ConstraintViolationProblem || problem instanceof DefaultProblem)) {
            return entity;
        }
        ProblemBuilder builder = Problem.builder()
                .withType(Problem.DEFAULT_TYPE.equals(problem.getType()) ? ErrorConstants.DEFAULT_TYPE : problem.getType())
                .withStatus(problem.getStatus())
                .withTitle(problem.getTitle())
                .with("path", request.getRequestURI());

        if (problem instanceof ConstraintViolationProblem) {
            builder
                    .with("violations", ((ConstraintViolationProblem) problem).getViolations())
                    .with("message", ErrorConstants.ERR_VALIDATION);
            return new ResponseEntity<>(builder.build(), entity.getHeaders(), entity.getStatusCode());
        } else {
            builder
                    .withCause(((DefaultProblem) problem).getCause())
                    .withDetail(problem.getDetail())
                    .withInstance(problem.getInstance());
            problem.getParameters().forEach(builder::with);
            if (!problem.getParameters().containsKey("message") && problem.getStatus() != null) {
                builder.with("message", "error.http." + problem.getStatus().getStatusCode());
            }
            return new ResponseEntity<>(builder.build(), entity.getHeaders(), entity.getStatusCode());
        }
    }

    @Override
    public ResponseEntity<Problem> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, NativeWebRequest req) {
        BindingResult result = ex.getBindingResult();
        List<FieldErrorVM> fieldErrors = result.getFieldErrors().stream()
                .map(f -> new FieldErrorVM(f.getObjectName(), f.getField(), f.getCode()))
                .collect(Collectors.toList());

        Problem problem = Problem.builder()
                .withType(ErrorConstants.CONSTRAINT_VIOLATION_TYPE)
                .withTitle("方法参数不正确")
                .withStatus(defaultConstraintViolationStatus())
                .with("message", ErrorConstants.ERR_VALIDATION)
                .with("fieldErrors", fieldErrors)
                .build();
        return create(ex, problem, req);
    }
}
