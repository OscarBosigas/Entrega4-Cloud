package org.example.persistence.repositories;

import org.example.persistence.entities.TaskEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<TaskEntity, Integer> {

    @Query(value = "select * from tasks", nativeQuery = true)
    List<TaskEntity> all();

    @Query(value = "select * from tasks where email = :email", nativeQuery = true)
    List<TaskEntity> getTasksByUser(String email);

    @Query(value = "UPDATE tasks SET \"status\" = '“processed”' WHERE id = :id", nativeQuery = true)
    void updateStatus(int id);

    @Query(value="select * from tasks where status = 'uploaded'", nativeQuery = true)
    List<TaskEntity> sinComprimir();

}
