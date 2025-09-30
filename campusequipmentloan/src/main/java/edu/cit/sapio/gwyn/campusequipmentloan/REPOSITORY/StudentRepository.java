package edu.cit.sapio.gwyn.campusequipmentloan.REPOSITORY;

import edu.cit.sapio.gwyn.campusequipmentloan.DOMAIN.StudentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentRepository extends JpaRepository<StudentEntity, Long> {
}
