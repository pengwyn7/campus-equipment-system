package edu.cit.sapio.gwyn.campusequipmentloan.SERVICE;

import edu.cit.sapio.gwyn.campusequipmentloan.DOMAIN.*;
import edu.cit.sapio.gwyn.campusequipmentloan.EXCEPTION.NotFoundException;
import edu.cit.sapio.gwyn.campusequipmentloan.REPOSITORY.EquipmentRepository;
import edu.cit.sapio.gwyn.campusequipmentloan.REPOSITORY.LoanRepository;
import edu.cit.sapio.gwyn.campusequipmentloan.REPOSITORY.StudentRepository;
import edu.cit.sapio.gwyn.campusequipmentloan.STRAT.PenaltyStrat;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class LoanService {

    private final LoanRepository loanRepository;
    private final StudentRepository studentRepository;
    private final EquipmentRepository equipmentRepository;
    private final PenaltyStrat penaltyStrat;

    public LoanService(LoanRepository loanRepository, StudentRepository studentRepository,
                      EquipmentRepository equipmentRepository, PenaltyStrat penaltyStrat) {
        this.loanRepository = loanRepository;
        this.studentRepository = studentRepository;
        this.equipmentRepository = equipmentRepository;
        this.penaltyStrat = penaltyStrat;
    }

    @Transactional
    public LoanEntity borrowEquipment(Long studentId, Long equipmentId) {
        StudentEntity student = studentRepository.findById(studentId)
                .orElseThrow(() -> new NotFoundException("Student not found"));
        EquipmentEntity equipment = equipmentRepository.findById(equipmentId)
                .orElseThrow(() -> new NotFoundException("Equipment not found"));

        // Check if equipment is available
        if (!equipment.isAvailable()) {
            throw new IllegalArgumentException("Equipment is not available");
        }

        // Check max 2 active loans
        long activeLoans = loanRepository.countByStudentAndStatus(student, LoanStatus.ACTIVE);
        if (activeLoans >= 2) {
            throw new IllegalArgumentException("Student cannot have more than 2 active loans");
        }

        // Create loan
        LocalDate startDate = LocalDate.now();
        LocalDate dueDate = startDate.minusDays(7);
        LoanEntity loan = new LoanEntity();
        loan.setStudent(student);
        loan.setEquipment(equipment);
        loan.setStartDate(startDate);
        loan.setDueDate(dueDate);
        loan.setStatus(LoanStatus.ACTIVE);

        // Set equipment unavailable
        equipment.setAvailable(false);
        equipmentRepository.save(equipment);

        return loanRepository.save(loan);
    }

    @Transactional
    public LoanEntity returnEquipment(Long loanId) {
        LoanEntity loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new NotFoundException("Loan not found"));

        if (loan.getStatus() == LoanStatus.RETURNED) {
            throw new IllegalArgumentException("Loan already returned");
        }

        // Set return date
        loan.setReturnDate(LocalDate.now());

        // Set equipment available
        EquipmentEntity equipment = loan.getEquipment();
        equipment.setAvailable(true);
        equipmentRepository.save(equipment);

        // Set status
        loan.setStatus(LoanStatus.RETURNED);

        return loanRepository.save(loan);
    }

    public List<LoanEntity> getAllLoans() {
        return loanRepository.findAll();
    }

    public LoanEntity getLoanById(Long id) {
        return loanRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Loan not found"));
    }

    // Additional method to check and update overdue loans
    @Scheduled(cron = "0 0 0 * * ?") // Run daily at midnight
    @Transactional
    public void updateOverdueLoans() {
        List<LoanEntity> activeLoans = loanRepository.findByStatus(LoanStatus.ACTIVE);
        LocalDate today = LocalDate.now();
        for (LoanEntity loan : activeLoans) {
            if (loan.getDueDate().isBefore(today)) {
                loan.setStatus(LoanStatus.OVERDUE);
                loanRepository.save(loan);
            }
        }
    }

    // Method to calculate penalty for a loan
    public double calculatePenalty(LoanEntity loan) {
        if (loan.getStatus() != LoanStatus.OVERDUE && loan.getStatus() != LoanStatus.RETURNED) {
            return 0.0;
        }
        if (loan.getReturnDate() == null || loan.getDueDate().isAfter(loan.getReturnDate())) {
            return 0.0;
        }
        long daysLate = loan.getDueDate().until(loan.getReturnDate()).getDays();
        return penaltyStrat.calculatePenalty(daysLate);
    }

    // Method to calculate days late for a loan
    public long calculateDaysLate(LoanEntity loan) {
        if (loan.getReturnDate() == null || loan.getDueDate().isAfter(loan.getReturnDate())) {
            return 0;
        }
        return loan.getDueDate().until(loan.getReturnDate()).getDays();
    }

    // Method for available equipment
    public List<EquipmentEntity> getAvailableEquipment() {
        return equipmentRepository.findByAvailableTrue();
    }

    // Update loan (allows updating dates only - status is managed automatically)
    public LoanEntity updateLoan(Long id, LoanEntity loanUpdate) {
        LoanEntity existing = getLoanById(id);

        // Update date fields only (status changes automatically via business logic)
        if (loanUpdate.getStartDate() != null) {
            existing.setStartDate(loanUpdate.getStartDate());
        }
        if (loanUpdate.getDueDate() != null) {
            existing.setDueDate(loanUpdate.getDueDate());
        }
        if (loanUpdate.getReturnDate() != null) {
            existing.setReturnDate(loanUpdate.getReturnDate());
            // If return date is set and it's late, mark as OVERDUE for penalty calculation
            if (existing.getReturnDate().isAfter(existing.getDueDate())) {
                existing.setStatus(LoanStatus.OVERDUE);
            } else {
                existing.setStatus(LoanStatus.RETURNED);
            }
            // Make equipment available when returned
            EquipmentEntity equipment = existing.getEquipment();
            equipment.setAvailable(true);
            equipmentRepository.save(equipment);
        }
        // Equipment, Student, and Status are managed by business rules and cannot be manually updated

        return loanRepository.save(existing);
    }
}
