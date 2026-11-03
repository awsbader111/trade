package com.example.trade;

import java.util.List;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/students")
public class StudentRestController {
    private final StudentRepository students;

    public StudentRestController(StudentRepository students) {
        this.students = students;
    }

    @GetMapping
    public List<Student> all() {
        return students.findAll();
    }

    @GetMapping("/{id}")
    public Student one(@PathVariable Long id) {
        return students.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found"));
    }

    @PostMapping
    public ResponseEntity<Student> create(@Valid @RequestBody Student student) {
        student.setId(null);
        return ResponseEntity.status(HttpStatus.CREATED).body(students.save(student));
    }

    @PutMapping("/{id}")
    public Student update(@PathVariable Long id, @Valid @RequestBody Student incoming) {
        Student existing = one(id);
        existing.setSname(incoming.getSname());
        existing.setStudentClass(incoming.getStudentClass());
        existing.setBoy(incoming.getBoy());
        return students.save(existing);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        students.delete(one(id));
        return ResponseEntity.noContent().build();
    }
}
