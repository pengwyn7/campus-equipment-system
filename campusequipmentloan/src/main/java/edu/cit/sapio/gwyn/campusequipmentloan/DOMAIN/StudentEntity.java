package edu.cit.sapio.gwyn.campusequipmentloan.DOMAIN;

import jakarta.persistence.*;

@Entity
public class StudentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String studentNo;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String email;

    public StudentEntity() {}

    public StudentEntity(Long id, String studentNo, String name, String email) {
        this.id = id;
        this.studentNo = studentNo;
        this.name = name;
        this.email = email;
    }

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getStudentNo() { return studentNo; }
    public void setStudentNo(String studentNo) { this.studentNo = studentNo; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}
