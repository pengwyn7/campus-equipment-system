package edu.cit.sapio.gwyn.campusequipmentloan.SERVICE;

import edu.cit.sapio.gwyn.campusequipmentloan.DOMAIN.StudentEntity;
import edu.cit.sapio.gwyn.campusequipmentloan.EXCEPTION.NotFoundException;
import edu.cit.sapio.gwyn.campusequipmentloan.REPOSITORY.StudentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StudentService {

    private final StudentRepository studentRepository;

    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    public List<StudentEntity> getAllStudents() {
        return studentRepository.findAll();
    }

    public StudentEntity getStudentById(Long id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Student not found"));
    }

    public StudentEntity createStudent(StudentEntity student) {
        return studentRepository.save(student);
    }

    public StudentEntity updateStudent(Long id, StudentEntity studentUpdate) {
        StudentEntity existing = getStudentById(id);
        existing.setStudentNo(studentUpdate.getStudentNo());
        existing.setName(studentUpdate.getName());
        existing.setEmail(studentUpdate.getEmail());
        return studentRepository.save(existing);
    }

    public void deleteStudent(Long id) {
        if (!studentRepository.existsById(id)) {
            throw new NotFoundException("Student not found");
        }
        studentRepository.deleteById(id);
    }
}
