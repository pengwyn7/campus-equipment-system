package edu.cit.sapio.gwyn.campusequipmentloan.REPOSITORY;

import edu.cit.sapio.gwyn.campusequipmentloan.DOMAIN.EquipmentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EquipmentRepository extends JpaRepository<EquipmentEntity, Long> {
    List<EquipmentEntity> findByAvailableTrue();
}
