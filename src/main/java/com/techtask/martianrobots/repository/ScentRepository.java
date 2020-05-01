package com.techtask.martianrobots.repository;

import com.techtask.martianrobots.domain.Scent;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface ScentRepository extends CrudRepository<Scent, Long> {

    Optional<Scent> findFirstByCoordinateXAndCoordinateYAndUnsafeOrientation(int coordinateX, int coordinateY, String unsafeOrientation);
}
