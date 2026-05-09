package com.example.demo.service;

import com.example.demo.model.Question;
import com.example.demo.model.QuestionReview;
import com.example.demo.model.Result;
import com.example.demo.repository.QuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.demo.model.ResultEntity;
import com.example.demo.repository.ResultRepository;
import java.util.*;

@Service
public class QuizService {

    @Autowired
    private QuestionRepository repo;
    
    @Autowired
    private ResultRepository resultRepo;

    private Map<String, List<Question>> userQuestions = new HashMap<>();
    private Map<String, Result> results = new HashMap<>();

    // START QUIZ
    public List<Question> startQuiz(String userName) {

        List<Question> copy = repo.findAll();
        Collections.shuffle(copy);

        userQuestions.put(userName, copy);

        return copy;
    }

    // SUBMIT QUIZ
    public Result submitQuiz(String userName, List<String> answers) {

        List<Question> userQ = userQuestions.get(userName);

        if (userQ == null) {
        	Result r = new Result();
        	r.setUserName(userName);
        	r.setScore(0);
        	r.setTotal(0);
        	r.setStatus("Quiz Not Started");
        	r.setCorrectCount(0);
        	r.setIncorrectCount(0);
        	r.setCorrectList(new ArrayList<>());
        	r.setIncorrectList(new ArrayList<>());
        	return r;
        }

        int score = 0;

        List<QuestionReview> correctList = new ArrayList<>();
        List<QuestionReview> incorrectList = new ArrayList<>();

        for (int i = 0; i < userQ.size(); i++) {

            Question q = userQ.get(i);

            String userAns = (i < answers.size()) ? answers.get(i) : "";

            if (q.getCorrectAnswer().equals(userAns)) {

                score++;

                correctList.add(new QuestionReview(
                        q.getQuestion(),
                        userAns,
                        q.getCorrectAnswer()
                ));

            } else {

                incorrectList.add(new QuestionReview(
                        q.getQuestion(),
                        userAns,
                        q.getCorrectAnswer()
                ));
            }
        }

        Result result = new Result();

        result.setUserName(userName);
        result.setScore(score);
        result.setTotal(userQ.size());
        result.setStatus(score >= (userQ.size() * 0.6) ? "Pass" : "Fail");

        result.setCorrectCount(correctList.size());
        result.setIncorrectCount(incorrectList.size());
        result.setCorrectList(correctList);
        result.setIncorrectList(incorrectList);
        
    
        results.put(userName, result);

        ResultEntity savedResult = new ResultEntity();
        savedResult.setUserName(userName);
        savedResult.setScore(score);
        savedResult.setTotal(userQ.size());
        savedResult.setStatus(result.getStatus());
        savedResult.setAttemptTime(
                java.time.LocalDateTime.now()
                .format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        );

        resultRepo.save(savedResult);

        return result;
    }

    // GET RESULT
    public Result getResult(String userName) {
    	return results.getOrDefault(
    	        userName,
    	        new Result(
    	                userName,
    	                0,
    	                0,
    	                "No Result Found",
    	                0,
    	                0,
    	                new ArrayList<>(),
    	                new ArrayList<>()
    	        )
    	);
    }

    // ADMIN
    public List<Question> getAllQuestions() {
        return repo.findAll();
    }

    public void addQuestion(Question question) {
        repo.save(question);
    }

    public void deleteQuestion(Integer id) {
        repo.deleteById(id);
    }
    
    public List<ResultEntity> getAllResults() {
        return resultRepo.findAllByOrderByIdDesc();
    }

    public void deleteAllResults() {
        resultRepo.deleteAll();
    }
}