package com.uv.quizservice.service;

import com.uv.quizservice.feign.QuizInterface;
import com.uv.quizservice.model.QuestionWrapper;
import com.uv.quizservice.model.Response;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class QuizClientService {

    private final QuizInterface quizInterface;

    public QuizClientService(QuizInterface quizInterface) {
        this.quizInterface = quizInterface;
    }

    @CircuitBreaker(name = "questionService", fallbackMethod = "getQuestionsForQuizFallback")
    public ResponseEntity<List<Integer>> getQuestionsForQuiz(String categoryName, Integer numQuestions) {
        return quizInterface.getQuestionsForQuiz(categoryName, numQuestions);
    }

    public ResponseEntity<List<Integer>> getQuestionsForQuizFallback(String categoryName, Integer numQuestions, Throwable t) {
        return ResponseEntity.status(503).body(Collections.emptyList());
    }

    @CircuitBreaker(name = "questionService", fallbackMethod = "getQuestionsFromIdFallback")
    public ResponseEntity<List<QuestionWrapper>> getQuestionsFromId(List<Integer> questionIds) {
        return quizInterface.getQuestionsFromId(questionIds);
    }

    public ResponseEntity<List<QuestionWrapper>> getQuestionsFromIdFallback(List<Integer> questionIds, Throwable t) {
        return ResponseEntity.status(503).body(Collections.emptyList());
    }

    @CircuitBreaker(name = "questionService", fallbackMethod = "getScoreFallback")
    public ResponseEntity<Integer> getScore(List<Response> responses) {
        return quizInterface.getScore(responses);
    }

    public ResponseEntity<Integer> getScoreFallback(List<Response> responses, Throwable t) {
        return ResponseEntity.status(503).body(0);
    }

}
