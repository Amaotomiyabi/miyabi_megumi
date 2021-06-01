package com.megumi.repository;

import com.megumi.pojo.middle.ImgUserHistory;
import com.megumi.pojo.middle.TagUserHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * 2021/2/21
 *
 * @author miyabi
 * @since 1.0
 */
public interface TagUserHistoryRepo extends JpaRepository<TagUserHistory, Long>, JpaSpecificationExecutor<ImgUserHistory> {
    @Modifying
    @Query(value = """
            insert into tags_users_history values (?1,?2,?3,?4)
            """, nativeQuery = true)
    void insertLookTag(Long id, Long userId, Long tId, String time);

    @Modifying
    @Query(value = """
            delete from pictures_users_history where picture_id=?1 and user_id=?2
            """, nativeQuery = true)
    void deleteLookTag(Long pId, Long userId);

    @Modifying
    @Query(value = """
            delete from pictures_users_history where picture_id in (:pIds) and user_id=:userId
            """, nativeQuery = true)
    void deleteLookTags(@Param("pIds") List<Long> pIds, @Param("userId") Long userId);

    Page<TagUserHistory> findByUserIdOrderByBrowseTimeDesc(Long id, Pageable pageable);
}
