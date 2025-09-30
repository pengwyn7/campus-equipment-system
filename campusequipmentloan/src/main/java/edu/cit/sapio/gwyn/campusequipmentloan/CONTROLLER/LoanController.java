package edu.cit.sapio.gwyn.campusequipmentloan.CONTROLLER;

import edu.cit.sapio.gwyn.campusequipmentloan.DOMAIN.LoanEntity;
import edu.cit.sapio.gwyn.campusequipmentloan.DOMAIN.LoanRequest;
import edu.cit.sapio.gwyn.campusequipmentloan.SERVICE.LoanService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;

@RestController
@RequestMapping("/api/loans")
public class LoanController {

    private final LoanService loanService;

    public LoanController(LoanService loanService) {
        this.loanService = loanService;
    }

    // Create loan
    @PostMapping
    public ResponseEntity<Map<String, Object>> createLoan(@RequestBody LoanRequest request) {
        LoanEntity loan = loanService.borrowEquipment(request.getStudentId(), request.getEquipmentId());
        Map<String, Object> loanMap = createLoanMap(loan);
        return ResponseEntity.ok(loanMap);
    }

    // Return loan
    @PostMapping("/{id}/return")
    public ResponseEntity<Map<String, Object>> returnLoan(@PathVariable Long id) {
        LoanEntity loan = loanService.returnEquipment(id);
        Map<String, Object> loanMap = createLoanMap(loan);
        return ResponseEntity.ok(loanMap);
    }

    // Get all loans with penalty
    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getAllLoans() {
        List<LoanEntity> loans = loanService.getAllLoans();
        List<Map<String, Object>> loansWithPenalty = loans.stream()
            .map(loan -> {
                Map<String, Object> loanMap = new LinkedHashMap<>();
                loanMap.put("id", loan.getId());
                loanMap.put("equipment", loan.getEquipment());
                loanMap.put("student", loan.getStudent());
                loanMap.put("startDate", loan.getStartDate());
                loanMap.put("dueDate", loan.getDueDate());
                loanMap.put("returnDate", loan.getReturnDateForJson());
                loanMap.put("status", loan.getStatus());
                loanMap.put("daysLate", loanService.calculateDaysLate(loan));
                loanMap.put("penalty", loanService.calculatePenalty(loan));
                return loanMap;
            })
            .toList();
        return ResponseEntity.ok(loansWithPenalty);
    }

    // Get loan by ID with penalty
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getLoanById(@PathVariable Long id) {
        LoanEntity loan = loanService.getLoanById(id);
        Map<String, Object> loanMap = new LinkedHashMap<>();
        loanMap.put("id", loan.getId());
        loanMap.put("equipment", loan.getEquipment());
        loanMap.put("student", loan.getStudent());
        loanMap.put("startDate", loan.getStartDate());
        loanMap.put("dueDate", loan.getDueDate());
        loanMap.put("returnDate", loan.getReturnDateForJson());
        loanMap.put("status", loan.getStatus());
        loanMap.put("daysLate", loanService.calculateDaysLate(loan));
        loanMap.put("penalty", loanService.calculatePenalty(loan));
        return ResponseEntity.ok(loanMap);
    }

    // Manual trigger for updating overdue loans (for testing)
    @PostMapping("/update-overdue")
    public ResponseEntity<String> updateOverdueLoans() {
        loanService.updateOverdueLoans();
        return ResponseEntity.ok("Overdue loans updated successfully");
    }

    // Update loan (allows updating dates - startDate, dueDate, returnDate)
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateLoan(@PathVariable Long id, @RequestBody LoanEntity loanUpdate) {
        LoanEntity loan = loanService.updateLoan(id, loanUpdate);
        Map<String, Object> loanMap = createLoanMap(loan);
        return ResponseEntity.ok(loanMap);
    }

    // Get penalty for a loan
    @GetMapping("/{id}/penalty")
    public ResponseEntity<Double> getLoanPenalty(@PathVariable Long id) {
        LoanEntity loan = loanService.getLoanById(id);
        double penalty = loanService.calculatePenalty(loan);
        return ResponseEntity.ok(penalty);
    }

    // Helper method to create loan map with penalty
    private Map<String, Object> createLoanMap(LoanEntity loan) {
        Map<String, Object> loanMap = new LinkedHashMap<>();
        loanMap.put("id", loan.getId());
        loanMap.put("equipment", loan.getEquipment());
        loanMap.put("student", loan.getStudent());
        loanMap.put("startDate", loan.getStartDate());
        loanMap.put("dueDate", loan.getDueDate());
        loanMap.put("returnDate", loan.getReturnDateForJson());
        loanMap.put("status", loan.getStatus());
        loanMap.put("daysLate", loanService.calculateDaysLate(loan));
        loanMap.put("penalty", loanService.calculatePenalty(loan));
        return loanMap;
    }
}
