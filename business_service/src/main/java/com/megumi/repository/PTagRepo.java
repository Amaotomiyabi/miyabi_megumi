package com.megumi.repository;

import com.megumi.pojo.PTag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

/**
 * 2021/2/21
 *
 * @author miyabi
 * @since 1.0
 */
public interface PTagRepo extends JpaRepository<PTag, Long> {
    @Query(value = "select tuh.id,p.id,p.name,tuh.browse_time from tags_users_history tuh join p_tag p on tuh.tag_id = p.id where tuh.user_id=?1", nativeQuery = true)
    Page<PTag> findHistoryTagsByUserId(Long userId, Pageable pageable);

    PTag findByName(String name);

    List<PTag> findByNameIn(Collection<String> name);

    @Query(value = "select * from p_tag where id in (select tag_id from tag_picture where picture_id=?1)", nativeQuery = true)
    List<PTag> findTagsByPicture(Long id);

    Page<PTag> findByNameContains(String name, Pageable pageable);

    @Query(value = """
            select * from p_tag where id in 
            (
            select pt.id from p_tag pt join tags_users_history tuh on pt.id = tuh.tag_id 
            where tuh.browse_time>=?1 
            and IF(?2!=null,pt.name=?2,1=1) 
            group by pt.id
            order by count(distinct tuh.id),pt.id desc 
            ) 
            """,
            countQuery = """
                            select count(*) from 
                    (
                    select pt.id from p_tag pt join tags_users_history tuh on pt.id = tuh.tag_id 
                    where tuh.browse_time>=?1 
                    and IF(?2!=null,pt.name=?2,1=1) 
                    group by pt.id
                    ) t1
                            """, nativeQuery = true)
    Page<PTag> findHotTag(LocalDateTime time, String name, Pageable pageable);

    @Query(value = """
            select pt.*
            from tag_picture
                     join p_tag pt on pt.id = tag_picture.tag_id
            where picture_id in (
                select picture_id
                from tag_picture
                where tag_id in (?1))
                and tag_id not in (?1)
            group by tag_id
            order by count(*) desc
            limit 0,49;
            """, nativeQuery = true)
    List<PTag> findByPredicateTag(List<Long> tagIds);

    List<PTag> findAllByNameIn(List<String> names);
}
