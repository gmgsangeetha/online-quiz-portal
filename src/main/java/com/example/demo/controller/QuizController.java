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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = {"http://localhost:8080", "https://onrender.com"}, allowCredentials = "true")
public class QuizController {

    @Autowired
    private QuizService service;

    // ================= QUIZ PORTAL SYSTEM =================

    @GetMapping("/quiz/start")
    public List<Question> start(@RequestParam String user) {
        return service.startQuiz(user);
    }

    // ⭐ SINGLE TIME SUBMISSION: Handled safely using simple if-conditions and native HTTP Session
    @PostMapping("/quiz/submit")
    public ResponseEntity<?> submit(@RequestParam String user,
                                    @RequestBody List<String> answers,
                                    HttpSession session) {
        
        // Check if this student has already submitted in this session
        if (session.getAttribute("status_" + user) != null) {
            // If they already submitted, just return their existing result instead of saving again
            Result existingResult = service.getResult(user);
            return ResponseEntity.ok(existingResult);
        }

        // First time submitting? Process the quiz normally
        Result result = service.submitQuiz(user, answers);
        
        // Immediately save a flag in the session memory marking them as "submitted"
        session.setAttribute("status_" + user, "submitted");
        
        return ResponseEntity.ok(result);
    }

    @GetMapping("/quiz/result")
    public Result result(@RequestParam String user) {
        return service.getResult(user);
    }

    // ⭐ VIEW PAST ATTEMPTS: Uses a basic for-each loop and a simple list (No advanced streams)
    @GetMapping("/quiz/past-attempts")
    public List<ResultEntity> pastAttempts(@RequestParam String user) {
        // 1. Fetch all records from the database
        List<ResultEntity> allResults = service.getAllResults();
        
        // 2. Create a clean empty list to hold this specific student's results
        List<ResultEntity> filteredResults = new ArrayList<>();
        
        // 3. Simple for-each loop to find matching usernames
        for (ResultEntity r : allResults) {
            if (r.getUserName() != null && r.getUserName().equalsIgnoreCase(user)) {
                filteredResults.add(r); // Add matching attempt to our list
            }
        }
        
        // 4. Return the simple list back to the frontend
        return filteredResults;
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

        // 🌐 CONTAINER: Inject character encoding and create a clean box layout container frame
        sb.append("<html><head><meta charset='UTF-8'><title>Admin - Quiz Results</title></head>")
          .append("<body style='font-family:Arial, sans-serif; background:#eef2f7; margin:0; padding:20px;'>")
          .append("<div style='max-width:95%; margin:auto; background:white; padding:20px; border-radius:8px; box-shadow:0 0 10px rgba(0,0,0,0.1);'>")
          .append("<h3 style='color:#0b3d91; margin-top:0;'>Student Quiz Results</h3>")
          
          // 🔍 UNIVERSAL LIVE KEYWORD FILTER INPUT BAR
          .append("<input type='text' id='queryInput' placeholder='🔍 Type name, email, or status to filter JSON rows...' style='width:100%; padding:10px; margin-bottom:15px; border:1px solid #ccc; border-radius:5px; box-sizing:border-box; font-size:14px;'>")
          .append("<div id='logWrapper'>");

        for (ResultEntity r : results) {
            String rawUser = r.getUserName() != null ? r.getUserName() : "N/A";
            String parsedName = rawUser;
            String parsedEmail = "No email registered";

            int bracketIndex = rawUser.indexOf(" (");
            if (bracketIndex != -1) {
                parsedName = rawUser.substring(0, bracketIndex).trim();
                parsedEmail = rawUser.substring(bracketIndex + 2, rawUser.length() - 1).trim();
            }

            // 🔒 STRIP ID DISPLAY: The ID parameter chunk is completely skipped right here!
            // 🎨 SINGLE ROW DISPLAY FIX: white-space:nowrap keeps the JSON text locked entirely on a single straight line
            sb.append("<div class='json-row' style='margin-bottom:6px; background:#f4f6f8; padding:10px; border-radius:4px; font-family:monospace; font-size:13px; white-space:nowrap; overflow-x:auto;'>")
              .append("{\"userName\":\"").append(parsedName).append("\"")
              .append(", \"email\":\"").append(parsedEmail).append("\"") 
              .append(", \"score\":").append(r.getScore())
              .append(", \"totalQuestions\":").append(r.getTotal())
              .append(", \"status\":\"").append(r.getStatus()).append("\"")
              .append(", \"attemptTime\":\"").append(r.getAttemptTime()).append("\"")
              .append("}</div>"); 
        }

        sb.append("</div></div>");

        // 🎯 4-LINE LIVE SEARCH FILTER SCRIPT
        sb.append("<script>")
          .append("document.getElementById('queryInput').onkeyup = function() {")
          .append("  let q = this.value.toLowerCase().trim();")
          .append("  document.querySelectorAll('.json-row').forEach(row => {")
          .append("    row.style.display = row.textContent.toLowerCase().includes(q) ? '' : 'none';")
          .append("  });")
          .append("};")
          .append("</script></body></html>");

        return ResponseEntity.ok(sb.toString());
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
