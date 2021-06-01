package com.megumi.repository;

import com.megumi.pojo.middle.Follower;
import com.megumi.pojo.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

/**
 * 2021/2/28
 *
 * @author miyabi
 * @since 1.0
 */
public interface FollowerRepo extends JpaRepository<Follower, Long> {
    @Modifying
    @Query(value = """
            insert into follower values(?1,?3,?2,?4)
            """, nativeQuery = true)
    void insertSubscribe(Long id, Long followerId, Long subscribeId, String time);

    @Modifying
    @Query(value = """
            delete from follower where follower_id=?1 and user_id=?2
            """, nativeQuery = true)
    void deleteSubscribe(Long followerId, Long subscribeId);

/*    @Query(value = """
            select * from (select u.id,u.username,u.works_count,u.photo,p.small_path,p.path from user u left join photo p 
            on (u.id,u.photo)=(p.user_id,p.id)) t1 right join (select follower_id,create_time from follower where user_id=?1)
            t2 on t1.id=t2.follower_id order by create_time desc
            """, nativeQuery = true)
    Page<Follower> findFollowerById(Long userId, Pageable pageable);*/

    Page<Follower> findFollowersByUser(User user, Pageable pageable);

    Page<Follower> findFollowersByFollower(User follower, Pageable pageable);

    Integer countFollowerByUserAndFollower(User user, User follower);


}
