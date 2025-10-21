package edu.cit.sapio.gwyn.campusequipmentloan.SERVICE;

import edu.cit.sapio.gwyn.campusequipmentloan.DOMAIN.EquipmentEntity;
import edu.cit.sapio.gwyn.campusequipmentloan.EXCEPTION.NotFoundException;
import edu.cit.sapio.gwyn.campusequipmentloan.REPOSITORY.EquipmentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EquipmentService {

    private final EquipmentRepository equipmentRepository;

    public EquipmentService(EquipmentRepository equipmentRepository) {
        this.equipmentRepository = equipmentRepository;
    }

    public List<EquipmentEntity> getAllEquipment() {
        return equipmentRepository.findAll();
    }

    public EquipmentEntity getEquipmentById(Long id) {
        return equipmentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Equipment not found"));
    }

    public List<EquipmentEntity> getAvailableEquipment() {
        return equipmentRepository.findByAvailableTrue();
    }

    public EquipmentEntity createEquipment(EquipmentEntity equipment) {
        return equipmentRepository.save(equipment);
    }

    public EquipmentEntity updateEquipment(Long id, EquipmentEntity equipmentUpdate) {
        EquipmentEntity existing = getEquipmentById(id);
        existing.setName(equipmentUpdate.getName());
        existing.setType(equipmentUpdate.getType());
        existing.setSerialNumber(equipmentUpdate.getSerialNumber());
        existing.setAvailable(equipmentUpdate.isAvailable());
        return equipmentRepository.save(existing);
    }

    public void deleteEquipment(Long id) {
        if (!equipmentRepository.existsById(id)) {
            throw new NotFoundException("Equipment not found");
        }
        equipmentRepository.deleteById(id);
    }
}
