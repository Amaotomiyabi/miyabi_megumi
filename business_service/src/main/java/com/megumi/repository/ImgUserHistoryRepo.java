package com.megumi.repository;

import com.megumi.pojo.Picture;
import com.megumi.pojo.User;
import com.megumi.pojo.middle.ImgUserHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

/**
 * 2021/2/21
 *
 * @author miyabi
 * @since 1.0
 */
public interface ImgUserHistoryRepo extends JpaRepository<ImgUserHistory, Long>, JpaSpecificationExecutor<ImgUserHistory> {

    @Modifying
    @Query(value = """
            insert into pictures_users_history values (?1,?4,?3,?2)
            """, nativeQuery = true)
    void insertLookImg(Long id, Long userId, Long pId, LocalDateTime time);

    @Modifying
    @Query(value = """
            delete from pictures_users_history where picture_id=?1 and user_id=?2
            """, nativeQuery = true)
    void deleteLookImg(Long pId, Long userId);

    @Modifying
    @Query(value = """
            delete from pictures_users_history where picture_id in (:pIds) and user_id=:userId
            """, nativeQuery = true)
    void deleteLookImgs(@Param("pIds") List<Long> pIds, @Param("userId") Long userId);

    Page<ImgUserHistory> findByUserIdOrderByBrowseTimeDesc(Long userId, Pageable pageable);

    @Modifying
    @Query(value = "update pictures_users_history set picture_id=null where picture_id in (?1)", nativeQuery = true)
    void updateImgTo0(Collection<Long> pIds);

    @Modifying
    @Query(value = "update pictures_users_history set picture_id=null where picture_id=?1", nativeQuery = true)
    void updateImgTo0(Long pId);

    @EntityGraph("noUser")
    Page<ImgUserHistory> findDistinctByUserIdOrderByBrowseTimeDesc(Long uId, Pageable pageable);

}
