package edu.cit.sapio.gwyn.campusequipmentloan.DOMAIN;

import jakarta.persistence.*;

@Entity
public class EquipmentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String type;

    @Column(unique = true, nullable = false)
    private String serialNumber;

    @Column(nullable = false)
    private boolean available = true;

    public EquipmentEntity() {}

    public EquipmentEntity(Long id, String name, String type, String serialNumber, boolean available) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.serialNumber = serialNumber;
        this.available = available;
    }

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getSerialNumber() { return serialNumber; }
    public void setSerialNumber(String serialNumber) { this.serialNumber = serialNumber; }

    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }
}
