package com.theironyard.services;

import com.theironyard.entities.AnonFile;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by jeff on 7/27/16.
 */
public interface AnonFileRepository extends JpaRepository<AnonFile, Integer> {
}
