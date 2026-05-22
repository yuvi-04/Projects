package com.uv.quizservice.controller;

import com.uv.quizservice.model.QuestionWrapper;
import com.uv.quizservice.model.QuizDto;
import com.uv.quizservice.model.Response;
import com.uv.quizservice.service.QuizService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("quiz")
@Slf4j
public class QuizController {

    @Autowired
    QuizService quizService;

    @PostMapping("create")
    public ResponseEntity<String> createQuiz(@RequestBody QuizDto quizDto){
        log.info("Creating quiz title={} category={} questions={}", quizDto.getTitle(), quizDto.getCategoryName(), quizDto.getNumQuestions());
        return quizService.createQuiz(quizDto.getCategoryName(), quizDto.getNumQuestions(), quizDto.getTitle());
    }

    @PostMapping("get/{id}")
    public ResponseEntity<List<QuestionWrapper>> getQuizQuestions(@PathVariable Integer id){
        log.info("Fetching quiz questions for quizId={}", id);
        return quizService.getQuizQuestions(id);
    }

    @PostMapping("submit/{id}")
    public ResponseEntity<Integer> submitQuiz(@PathVariable Integer id, @RequestBody List<Response> responses){
        log.info("Submitting quizId={} with {} responses", id, responses.size());
        return quizService.calculateResult(id, responses);
    }


}
