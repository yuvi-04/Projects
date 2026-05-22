package com.uv.questionservice.controller;


import com.uv.questionservice.model.Question;
import com.uv.questionservice.model.QuestionWrapper;
import com.uv.questionservice.model.Response;
import com.uv.questionservice.service.QuestionService;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("question")
@Slf4j
public class QuestionController {

    @Autowired
    QuestionService questionService;

    @Autowired
    Environment environment;

    @GetMapping("allQuestions")
    public ResponseEntity<List<Question>> getAllQuestions(){
        log.info("Fetching all questions");
        return questionService.getAllQuestions();
    }

    @GetMapping("category/{category}")
    public ResponseEntity<List<Question>> getQuestionsByCategory(@PathVariable String category){
        log.info("Fetching questions for category={}", category);
        return questionService.getQuestionsByCategory(category);
    }

    @PostMapping("add")
    public ResponseEntity<String> addQuestion(@RequestBody Question question){
        log.info("Adding question for category={}", question.getCategory());
        return  questionService.addQuestion(question);
    }

    @GetMapping("generate")
    public ResponseEntity<List<Integer>> getQuestionsForQuiz
            (@RequestParam String categoryName, @RequestParam Integer numQuestions ){
        log.info("Generating {} questions for category={}", numQuestions, categoryName);
        return questionService.getQuestionsForQuiz(categoryName, numQuestions);
    }

    @PostMapping("getQuestions")
    public ResponseEntity<List<QuestionWrapper>> getQuestionsFromId(@RequestBody List<Integer> questionIds){
        log.info("Fetching {} questions from ids on port={}", questionIds.size(), environment.getProperty("local.server.port"));
        return questionService.getQuestionsFromId(questionIds);
    }

    @PostMapping("getScore")
    public ResponseEntity<Integer> getScore(@RequestBody List<Response> responses)
    {
        log.info("Calculating score for {} responses", responses.size());
        return questionService.getScore(responses);
    }


    // generate
    // getQuestions (questionid)
    // getScore

}
