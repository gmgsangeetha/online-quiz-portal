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

    @PostMapping("/quiz/submit")
    public ResponseEntity<?> submit(@RequestParam String user,
                                    @RequestBody List<String> answers,
                                    HttpSession session) {
        
        if (session.getAttribute("status_" + user) != null) {
            Result existingResult = service.getResult(user);
            return ResponseEntity.ok(existingResult);
        }

        Result result = service.submitQuiz(user, answers);
        session.setAttribute("status_" + user, "submitted");
        return ResponseEntity.ok(result);
    }

    @GetMapping("/quiz/result")
    public Result result(@RequestParam String user) {
        return service.getResult(user);
    }

    @GetMapping("/quiz/past-attempts")
    public List<ResultEntity> pastAttempts(@RequestParam String user) {
        List<ResultEntity> allResults = service.getAllResults();
        List<ResultEntity> filteredResults = new ArrayList<>();
        
        for (ResultEntity r : allResults) {
            if (r.getUserName() != null && r.getUserName().equalsIgnoreCase(user)) {
                filteredResults.add(r);
            }
        }
        return filteredResults;
    }

    // ================= ADMIN CREDENTIAL VALIDATION =================

    @PostMapping("/admin/login")
    public ResponseEntity<String> adminLogin(@RequestBody Map<String, String> data, HttpSession session) {
        String user = data.get("username");
        String pass = data.get("password");

        if ("admin".equals(user) && "admin123".equals(pass)) {
            session.setAttribute("adminLoggedIn", true);
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

    // PLAIN TEXT ACCESS DENIED RESPONSE LAYOUT WITH BOLD BLACK HEADING
    private ResponseEntity<String> getAccessDeniedResponse() {
        String accessDeniedHtml = "<html>" +
            "<head><meta charset='UTF-8'><title>Access Denied</title></head>" +
            "<body>" +
            "  <h1 style='color:black; font-family:Arial; font-weight:bold;'>Access Denied</h1>" +
            "  <p style='font-family:Arial;'>Unauthorized access -  Login is required to view this page.</p>" +
            "</body>" +
            "</html>";
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(accessDeniedHtml);
    }

    // ================= PROTECTED ADMIN ACTIONS =================

    @GetMapping("/admin/questions")
    public ResponseEntity<?> allQuestions(HttpSession session) {
        if (isNotAdmin(session)) {
            return getAccessDeniedResponse(); // 🔒 Standardized Plain Access Denied
        }
        return ResponseEntity.ok(service.getAllQuestions());
    }

    @PostMapping("/admin/add")
    public ResponseEntity<String> add(@RequestBody Question question, HttpSession session) {
        if (isNotAdmin(session)) {
            return getAccessDeniedResponse(); // 🔒 Standardized Plain Access Denied
        }
        service.addQuestion(question);
        return ResponseEntity.ok("Question added successfully");
    }

    @DeleteMapping("/admin/delete/{id}")
    public ResponseEntity<String> delete(@PathVariable Integer id, HttpSession session) {
        if (isNotAdmin(session)) {
            return getAccessDeniedResponse(); // 🔒 Standardized Plain Access Denied
        }
        service.deleteQuestion(id);
        return ResponseEntity.ok("Deleted successfully");
    }
    
    @GetMapping("/admin/results")
    public ResponseEntity<String> allResults(HttpSession session) {
        if (isNotAdmin(session)) {
            return getAccessDeniedResponse(); // 🔒 Standardized Plain Access Denied
        }

        List<ResultEntity> results = service.getAllResults();
        StringBuilder sb = new StringBuilder();

        sb.append("<html><head><meta charset='UTF-8'><title>Admin - Quiz Results</title></head>")
          .append("<body style='font-family:Arial, sans-serif; background:#eef2f7; margin:0; padding:20px;'>")
          .append("<div style='max-width:95%; margin:auto; background:white; padding:20px; border-radius:8px; box-shadow:0 0 10px rgba(0,0,0,0.1);'>")
          .append("<h3 style='color:#0b3d91; margin-top:0;'>Student Quiz Results</h3>")
          
          //  SEARCH BAR WITH RIGHT-ALIGNED MAGNIFYING SYMBOL
          .append("<div style='position:relative; width:100%; margin-bottom:15px;'>")
          .append("  <input type='text' id='queryInput' placeholder='Type name, email, or status to filter JSON rows...' style='width:100%; padding:10px; padding-right:35px; border:1px solid #ccc; border-radius:5px; box-sizing:border-box; font-size:14px;'>")
          .append("  <span style='position:absolute; right:12px; top:50%; transform:translateY(-50%); color:#777; pointer-events:none;'>🔍</span>")
          .append("</div>")
          
          //  EMPTY STATE TEXT LOG PLACEHOLDER
          .append("<div id='emptySearchAlert' style='display:none; padding:10px 0 15px 0; color:#b71c1c; font-family:Arial; font-weight:bold; font-size:14px;'>No results found for this search</div>")
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

        // LIVE FILTER LOOP TRACKER WITH CONDITIONAL EMPTY SEARCH LOGIC
        sb.append("<script>")
          .append("document.getElementById('queryInput').onkeyup = function() {")
          .append("  let q = this.value.toLowerCase().trim();")
          .append("  let matchCount = 0;")
          .append("  let rows = document.querySelectorAll('.json-row');")
          
          .append("  rows.forEach(row => {")
          .append("    if (row.textContent.toLowerCase().includes(q)) {")
          .append("      row.style.display = '';")
          .append("      matchCount++;")
          .append("    } else {")
          .append("      row.style.display = 'none';")
          .append("    }")
          .append("  });")
          
          // Triggers your exact dynamic empty state phrase when match loops drop to zero matches
          .append("  document.getElementById('emptySearchAlert').style.display = (matchCount === 0 && rows.length > 0) ? 'block' : 'none';")
          .append("};")
          .append("</script></body></html>");

        return ResponseEntity.ok(sb.toString());
    }

    @GetMapping("/admin/clear-results")
    public ResponseEntity<String> clearResults(HttpSession session) {
        if (isNotAdmin(session)) {
            return getAccessDeniedResponse(); // 🔒 Standardized Plain Access Denied
        }
        service.deleteAllResults();
        return ResponseEntity.ok("All results deleted");
    }
}
