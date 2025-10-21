package edu.cit.sapio.gwyn.campusequipmentloan.DOMAIN;

public class LoanRequest {
    private Long studentId;
    private Long equipmentId;

    public LoanRequest() {}

    public LoanRequest(Long studentId, Long equipmentId) {
        this.studentId = studentId;
        this.equipmentId = equipmentId;
    }

    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }

    public Long getEquipmentId() { return equipmentId; }
    public void setEquipmentId(Long equipmentId) { this.equipmentId = equipmentId; }
}
