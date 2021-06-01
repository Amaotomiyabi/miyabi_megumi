package com.megumi.repository;

import com.megumi.pojo.Photo;
import com.megumi.pojo.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 2021/2/21
 *
 * @author miyabi
 * @since 1.0
 */
public interface PhotoRepo extends JpaRepository<Photo, Long> {
    Page<Photo> findPhotosByUserId(Long userId, Pageable pageable);
}
