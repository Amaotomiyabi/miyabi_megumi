package com.miyabi.repo;

import com.megumi.pojo.PTag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TagRepo extends JpaRepository<PTag, Long> {

    List<PTag> findAllByNameIn(List<String> tagNames);
}
