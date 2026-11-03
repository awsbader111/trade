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
        return students.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found"));
    }

    @PostMapping
    public ResponseEntity<Student> create(@Valid @RequestBody Student student) {
        student.setId(null); // no client-controlled ID on creation
        return ResponseEntity.status(HttpStatus.CREATED).body(students.save(student));
    }

    @PutMapping("/{id}")
    public Student update(@PathVariable Long id, @Valid @RequestBody Student incoming) {
        Student current = one(id);
        current.setFirstName(incoming.getFirstName());
        current.setLastName(incoming.getLastName());
        current.setEmail(incoming.getEmail());
        current.setProgram(incoming.getProgram());
        current.setCredits(incoming.getCredits());
        return students.save(current);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        Student current = one(id);
        students.delete(current);
        return ResponseEntity.noContent().build();
    }
}
