package com.example.trade;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Student record from the professor's 04-Class record/students.txt dataset.
 */
@Entity
@Table(name = "students")
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 120)
    @Column(name = "sname", nullable = false, length = 120)
    private String sname;

    // 'class' is a Java keyword; quote the physical SQL column name to keep the original schema.
    @NotBlank
    @Size(max = 20)
    @Column(name = "\"class\"", nullable = false, length = 20)
    private String studentClass;

    @NotNull
    @Column(name = "boy", nullable = false)
    private Boolean boy;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSname() {
        return sname;
    }

    public void setSname(String sname) {
        this.sname = sname;
    }

    public String getStudentClass() {
        return studentClass;
    }

    public void setStudentClass(String studentClass) {
        this.studentClass = studentClass;
    }

    public Boolean getBoy() {
        return boy;
    }

    public void setBoy(Boolean boy) {
        this.boy = boy;
    }
}
