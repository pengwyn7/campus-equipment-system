package edu.cit.sapio.gwyn.campusequipmentloan.REPOSITORY;

import edu.cit.sapio.gwyn.campusequipmentloan.DOMAIN.LoanEntity;
import edu.cit.sapio.gwyn.campusequipmentloan.DOMAIN.LoanStatus;
import edu.cit.sapio.gwyn.campusequipmentloan.DOMAIN.StudentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LoanRepository extends JpaRepository<LoanEntity, Long> {
    List<LoanEntity> findByStatus(LoanStatus status);
    long countByStudentAndStatus(StudentEntity student, LoanStatus status);
}
