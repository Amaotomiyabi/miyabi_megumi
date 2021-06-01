package com.megumi.repository;

import com.megumi.pojo.PTag;
import com.megumi.pojo.Picture;
import com.megumi.pojo.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 2021/2/21
 *
 * @author miyabi
 * @since 1.0
 */
public interface PictureRepo extends JpaRepository<Picture, Long>, JpaSpecificationExecutor<Picture> {
    Page<Picture> findPicturesByUploader(User uploader, Pageable pageable);

    Page<Picture> findPicturesByUploaderAndIsSexy(User uploader, String isSexy, Pageable pageable);

    @Modifying
    @Query(value = """
            update picture set approval=approval+?1 where id=?2
            """, nativeQuery = true)
    void incrementApproval(Integer approval, Long pId);

    @Modifying
    @Query(value = """
            update picture set approval=approval+?1 where id in (?2)
            """, nativeQuery = true)
    void incrementApprovals(Integer approval, List<Long> pIds);

    @Modifying
    @Query(value = """
            update picture set collect=collect+?1 where id=?2
            """, nativeQuery = true)
    void incrementCollect(Integer collect, Long pId);

    @Modifying
    @Query(value = """
            update picture set collect=collect+?1 where id in (?2)
            """, nativeQuery = true)
    void incrementCollects(Integer collect, List<Long> pIds);

    @Modifying
    @Query(value = """
            update picture set views=?1+1 where id=?2
            """, nativeQuery = true)
    void incrementViews(Integer views, Long pId);

    @Modifying
    @Query(value = """
            update picture set views=?1+views where id in (?2)
            """, nativeQuery = true)
    void incrementViews(Integer views, List<Long> pIds);

    @Query(value = """
            select * from picture p1 join
                  (select p.id,count(distinct puh.id) cnt
                   from pictures_users_history puh
                            join picture p on puh.picture_id = p.id
                            left join tag_picture tp on tp.picture_id = p.id
                            left join p_tag pt on tp.tag_id = pt.id
                   where puh.browse_time >= ?5
                     and IF(?1 != null, pt.name in (?1), 1 = 1)
                     and IF(?2 != null, p.x >= ?2, 1 = 1)
                     and IF(?3 != null, p.y >= ?3, 1 = 1)
                     and IF(?4 != null || ?4 = '0', p.is_sexy = ?4, 1 = 1)
                   group by p.id
                  ) t1 on p1.id=t1.id order by cnt,p1.id desc 
                  """,
            countQuery = """
                    select count(*) from (
                        select p.id
                         from pictures_users_history puh
                                  join picture p on puh.picture_id = p.id
                                  left join tag_picture tp on tp.picture_id = p.id
                                  left join p_tag pt on tp.tag_id = pt.id
                         where puh.browse_time >= ?5
                           and IF(?1 != null, pt.name in (?1), 1 = 1)
                           and IF(?2 != null, p.x >= ?2, 1 = 1)
                           and IF(?3 != null, p.y >= ?3, 1 = 1)
                           and IF(?4 != null || ?4 = '0', p.is_sexy = ?4, 1 = 1)
                         group by p.id) t1             
                      """
            , nativeQuery = true)
    Page<Picture> findHotImg(String[] tags, Integer x, Integer y, String isSexy, LocalDateTime time, Pageable pageable);

    @Query(value = """
            select tp.picture from TagPicture tp where tp.pTag in (?1) and tp.picture.isSexy='0' group by tp.picture order by count (tp.id) desc 
            """)
    Page<Picture> findRecommendedPictures(List<PTag> tags, Pageable pageable);

}
