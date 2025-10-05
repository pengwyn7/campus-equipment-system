package edu.cit.sapio.gwyn.campusequipmentloan;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;

import edu.cit.sapio.gwyn.campusequipmentloan.DOMAIN.EquipmentEntity;
import edu.cit.sapio.gwyn.campusequipmentloan.DOMAIN.LoanEntity;
import edu.cit.sapio.gwyn.campusequipmentloan.DOMAIN.LoanStatus;
import edu.cit.sapio.gwyn.campusequipmentloan.DOMAIN.StudentEntity;
import edu.cit.sapio.gwyn.campusequipmentloan.REPOSITORY.EquipmentRepository;
import edu.cit.sapio.gwyn.campusequipmentloan.REPOSITORY.LoanRepository;
import edu.cit.sapio.gwyn.campusequipmentloan.REPOSITORY.StudentRepository;
import edu.cit.sapio.gwyn.campusequipmentloan.SERVICE.LoanService;
import edu.cit.sapio.gwyn.campusequipmentloan.STRAT.PenaltyStrat;

class LoanServiceTest {

    @Mock
    private LoanRepository loanRepository;

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private EquipmentRepository equipmentRepository;

    @Mock
    private PenaltyStrat penaltyStrat;

    @InjectMocks
    private LoanService loanService;

    private StudentEntity student;
    private EquipmentEntity equipment;
    private LoanEntity loan;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        student = new StudentEntity();
        student.setId(1L);

        equipment = new EquipmentEntity();
        equipment.setId(1L);
        equipment.setAvailable(true);

        loan = new LoanEntity();
        loan.setId(1L);
        loan.setStudent(student);
        loan.setEquipment(equipment);
        loan.setStatus(LoanStatus.ACTIVE);
        loan.setStartDate(LocalDate.now());
        loan.setDueDate(LocalDate.now().plusDays(7));
    }

    @Test
    void borrowEquipment_success() {
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        when(equipmentRepository.findById(1L)).thenReturn(Optional.of(equipment));
        when(loanRepository.countByStudentAndStatus(student, LoanStatus.ACTIVE)).thenReturn(0L);
        when(loanRepository.save(any(LoanEntity.class))).thenAnswer(i -> i.getArguments()[0]);


        LoanEntity result = loanService.borrowEquipment(1L, 1L);

        assertNotNull(result);
        assertEquals(student, result.getStudent());
        assertEquals(equipment, result.getEquipment());
        assertEquals(LoanStatus.ACTIVE, result.getStatus());
        assertEquals(result.getStartDate().plusDays(7), result.getDueDate());
        verify(equipmentRepository).save(any(EquipmentEntity.class));
        verify(loanRepository).save(any(LoanEntity.class));
    }

    @Test
    void borrowEquipment_equipmentNotAvailable() {
        equipment.setAvailable(false);
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        when(equipmentRepository.findById(1L)).thenReturn(Optional.of(equipment));

        assertThrows(IllegalArgumentException.class, () -> loanService.borrowEquipment(1L, 1L));
    }

    @Test
    void calculatePenalty_lateReturn() {
        loan.setDueDate(LocalDate.now().minusDays(5));
        loan.setReturnDate(LocalDate.now());
        loan.setStatus(LoanStatus.RETURNED);

        when(penaltyStrat.calculatePenalty(5)).thenReturn(250.0);

        double penalty = loanService.calculatePenalty(loan);

        assertEquals(250.0, penalty);
        verify(penaltyStrat).calculatePenalty(5);
    }

    @Test
    void getAvailableEquipment_success() {
        when(equipmentRepository.findByAvailableTrue()).thenReturn(List.of(equipment));
        List<EquipmentEntity> available = loanService.getAvailableEquipment();
        assertEquals(1, available.size());
        assertTrue(available.contains(equipment));
    }

    @Test
    void updateOverdueLoans_marksOverdue() {
        loan.setDueDate(LocalDate.now().minusDays(3)); // already overdue
        loan.setStatus(LoanStatus.ACTIVE);

        when(loanRepository.findByStatus(LoanStatus.ACTIVE)).thenReturn(List.of(loan));
        when(loanRepository.save(any(LoanEntity.class))).thenAnswer(i -> i.getArguments()[0]);

        loanService.updateOverdueLoans();

        assertEquals(LoanStatus.OVERDUE, loan.getStatus());
        verify(loanRepository).save(loan);
    }

    @Test
    void borrowEquipment_maxActiveLoansExceeded() {
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        when(equipmentRepository.findById(1L)).thenReturn(Optional.of(equipment));
        when(loanRepository.countByStudentAndStatus(student, LoanStatus.ACTIVE)).thenReturn(2L);

        assertThrows(IllegalArgumentException.class, () -> loanService.borrowEquipment(1L, 1L));
    }
}
