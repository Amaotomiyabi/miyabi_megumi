package com.miyabi.repo;

import com.megumi.pojo.Picture;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PictureRepo extends JpaRepository<Picture,Long> {
}
