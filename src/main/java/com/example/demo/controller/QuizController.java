package com.example.demo.controller;

import com.example.demo.model.Question;
import com.example.demo.model.Result;
import com.example.demo.service.QuizService;
import com.example.demo.model.ResultEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

@RestController
// ⭐ Updated CORS to explicitly support cookie-based session handshakes across origins
@CrossOrigin(origins = {"http://localhost:8080", "https://onrender.com"}, allowCredentials = "true")
public class QuizController {

    @Autowired
    private QuizService service;

    // ================= QUIZ PORTAL SYSTEM =================

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

    // ================= ADMIN CREDENTIAL VALIDATION =================

    @PostMapping("/admin/login")
    public ResponseEntity<String> adminLogin(@RequestBody Map<String, String> data, HttpSession session) {
        String user = data.get("username");
        String pass = data.get("password");

        if ("admin".equals(user) && "admin123".equals(pass)) {
            session.setAttribute("adminLoggedIn", true); // Create secure native session flag
            return ResponseEntity.ok("success");
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("fail");
    }

    @PostMapping("/admin/logout")
    public ResponseEntity<String> adminLogout(HttpSession session) {
        session.removeAttribute("adminLoggedIn");
        session.invalidate();
        return ResponseEntity.ok("logged_out");
    }

    // Helper method to protect endpoints without using spring security
    private boolean isNotAdmin(HttpSession session) {
        return session == null || session.getAttribute("adminLoggedIn") == null;
    }

    // ================= PROTECTED ADMIN ACTIONS =================

    @GetMapping("/admin/questions")
    public ResponseEntity<?> allQuestions(HttpSession session) {
        if (isNotAdmin(session)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }
        return ResponseEntity.ok(service.getAllQuestions());
    }

    @PostMapping("/admin/add")
    public ResponseEntity<String> add(@RequestBody Question question, HttpSession session) {
        if (isNotAdmin(session)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }
        service.addQuestion(question);
        return ResponseEntity.ok("Question added successfully");
    }

    @DeleteMapping("/admin/delete/{id}")
    public ResponseEntity<String> delete(@PathVariable Integer id, HttpSession session) {
        if (isNotAdmin(session)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }
        service.deleteQuestion(id);
        return ResponseEntity.ok("Deleted successfully");
    }
    
    @GetMapping("/admin/results")
    public ResponseEntity<String> allResults(HttpSession session) {
        // Blocks anyone guessing the endpoint URL from spying on student data
        if (isNotAdmin(session)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("<h1>Access Denied</h1><p>Authentication required to view system data metrics.</p>");
        }

        List<ResultEntity> results = service.getAllResults();
        StringBuilder sb = new StringBuilder();

        for (ResultEntity r : results) {
            sb.append("{\"id\":").append(r.getId())
              .append(", \"userName\":\"").append(r.getUserName()).append("\"")
              .append(", \"score\":").append(r.getScore())
              .append(", \"totalQuestions\":").append(r.getTotal())
              .append(", \"status\":\"").append(r.getStatus()).append("\"")
              .append(", \"attemptTime\":\"").append(r.getAttemptTime()).append("\"")
              .append("}\n\n"); // Your exact requested output format
        }

        return ResponseEntity.ok("<pre>" + sb.toString() + "</pre>");
    }
    
    @GetMapping("/admin/clear-results")
    public ResponseEntity<String> clearResults(HttpSession session) {
        if (isNotAdmin(session)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }
        service.deleteAllResults();
        return ResponseEntity.ok("All results deleted");
    }
}
