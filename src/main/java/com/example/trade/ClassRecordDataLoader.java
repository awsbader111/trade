package com.example.trade;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * One-time import of the professor's Class record dataset into file-backed H2.
 */
@Component
public class ClassRecordDataLoader implements ApplicationRunner {
    private static final String DATASET_ID = "professor-class-record-students-v1";
    private final StudentRepository students;
    private final JdbcTemplate jdbc;

    @Value("${atlasfx.seed-class-record:true}")
    private boolean enabled;

    public ClassRecordDataLoader(StudentRepository students, JdbcTemplate jdbc) {
        this.students = students;
        this.jdbc = jdbc;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        if (!enabled) return;

        jdbc.execute("CREATE TABLE IF NOT EXISTS atlasfx_imports (dataset_id VARCHAR(100) PRIMARY KEY)");
        Integer alreadyImported = jdbc.queryForObject(
                "SELECT COUNT(*) FROM atlasfx_imports WHERE dataset_id = ?", Integer.class, DATASET_ID);
        if (alreadyImported != null && alreadyImported > 0) return;

        if (students.count() > 0) {
            // Prevent silently mixing the official data with a pre-existing students dataset.
            throw new IllegalStateException("The selected H2 students table isn't empty. "
                    + "Use a fresh Class record database file for the official dataset.");
        }

        List<Student> imported = new ArrayList<>();
        ClassPathResource resource = new ClassPathResource("data/class-record-students.tsv");
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                resource.getInputStream(), StandardCharsets.UTF_8))) {
            String header = reader.readLine();
            if (!"id\tsname\tclass\tboy".equals(header)) {
                throw new IllegalStateException("Unexpected Class record students header");
            }
            String line;
            int expectedId = 1;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) continue;
                String[] parts = line.split("\\t", -1);
                if (parts.length != 4 || Integer.parseInt(parts[0]) != expectedId) {
                    throw new IllegalStateException("Invalid Class record row " + expectedId);
                }
                Student student = new Student();
                student.setSname(parts[1]);
                student.setStudentClass(parts[2]);
                if (!"-1".equals(parts[3]) && !"0".equals(parts[3])) {
                    throw new IllegalStateException("Unexpected gender flag at row " + expectedId);
                }
                student.setBoy("-1".equals(parts[3]));
                imported.add(student);
                expectedId++;
            }
        }
        if (imported.size() != 429) throw new IllegalStateException("Expected 429 professor-supplied students");
        students.saveAll(imported);
        jdbc.update("INSERT INTO atlasfx_imports(dataset_id) VALUES (?)", DATASET_ID);
    }
}
