package com.example.demo.controller;

import com.example.demo.model.Question;
import com.example.demo.model.Result;
import com.example.demo.service.QuizService;
import com.example.demo.model.ResultEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
public class QuizController {

    @Autowired
    private QuizService service;

    // QUIZ
    @GetMapping("/quiz/start")
    public List<Question> start(@RequestParam String user) {
        return service.startQuiz(user);
    }

    @PostMapping("/quiz/submit")
    public Result submit(@RequestParam String user,
                         @RequestBody List<String> answers) {
        return service.submitQuiz(user, answers);
    }

    @GetMapping("/quiz/result")
    public Result result(@RequestParam String user) {
        return service.getResult(user);
    }

    // ADMIN
    @GetMapping("/admin/questions")
    public List<Question> allQuestions() {
        return service.getAllQuestions();
    }

    @PostMapping("/admin/add")
    public String add(@RequestBody Question question) {
        service.addQuestion(question);
        return "Question added successfully";
    }

    @DeleteMapping("/admin/delete/{id}")
    public String delete(@PathVariable Integer id) {
        service.deleteQuestion(id);
        return "Deleted successfully";
    }
       
    @GetMapping("/admin/results")
    public String allResults() {

        List<ResultEntity> results = service.getAllResults();

        StringBuilder sb = new StringBuilder();

        for (ResultEntity r : results) {

            sb.append("{\"id\":").append(r.getId())
              .append(", \"userName\":\"").append(r.getUserName()).append("\"")
              .append(", \"score\":").append(r.getScore())
              .append(", \"totalQuestions\":").append(r.getTotal())
              .append(", \"status\":\"").append(r.getStatus()).append("\"")
              .append(", \"attemptTime\":\"").append(r.getAttemptTime()).append("\"")
              .append("}\n\n"); 
        }

        return "<pre>" + sb.toString() + "</pre>";
    }
    
    @GetMapping("/admin/clear-results")
    public String clearResults() {
        service.deleteAllResults();
        return "All results deleted";
    }
    
}