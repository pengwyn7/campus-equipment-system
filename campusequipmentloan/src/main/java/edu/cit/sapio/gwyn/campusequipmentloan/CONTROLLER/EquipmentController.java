package edu.cit.sapio.gwyn.campusequipmentloan.CONTROLLER;

import edu.cit.sapio.gwyn.campusequipmentloan.DOMAIN.EquipmentEntity;
import edu.cit.sapio.gwyn.campusequipmentloan.SERVICE.EquipmentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/equipment")
public class EquipmentController {

    private final EquipmentService equipmentService;

    public EquipmentController(EquipmentService equipmentService) {
        this.equipmentService = equipmentService;
    }

    @GetMapping
    public ResponseEntity<List<EquipmentEntity>> getAllEquipment() {
        return ResponseEntity.ok(equipmentService.getAllEquipment());
    }

    @GetMapping("/available")
    public ResponseEntity<List<EquipmentEntity>> getAvailableEquipment() {
        return ResponseEntity.ok(equipmentService.getAvailableEquipment());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EquipmentEntity> getEquipmentById(@PathVariable Long id) {
        return ResponseEntity.ok(equipmentService.getEquipmentById(id));
    }

    @PostMapping
    public ResponseEntity<EquipmentEntity> createEquipment(@RequestBody EquipmentEntity equipment) {
        return ResponseEntity.ok(equipmentService.createEquipment(equipment));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EquipmentEntity> updateEquipment(@PathVariable Long id, @RequestBody EquipmentEntity equipment) {
        return ResponseEntity.ok(equipmentService.updateEquipment(id, equipment));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEquipment(@PathVariable Long id) {
        equipmentService.deleteEquipment(id);
        return ResponseEntity.noContent().build();
    }
}
