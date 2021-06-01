package com.megumi.pojo.middle;

import com.megumi.pojo.User;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 2021/2/28
 *
 * @author miyabi
 * @since 1.0
 */
@Data
@Entity
@Table(name = "follower", schema = "miyabi_megumi")
@EntityListeners(AuditingEntityListener.class)
public class Follower implements Serializable {

    @Id
    @Column(name = "id")
    @GenericGenerator(name = "snowflakeG", strategy = "com.megumi.common.JpaIdGenerator")
    @GeneratedValue(generator = "snowflakeG")
    private Long id;

    @OneToOne
    @JoinColumn(name = "follower_id")
    private User follower;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    @CreatedDate
    @Column(name = "create_time")
    private LocalDateTime createTime;

    public Follower(Long userId, Long followerId) {
        user = new User(userId);
        follower = new User(followerId);
    }

    public Follower(User user, User follower) {
        this.user = user;
        this.follower = follower;
    }

    public Follower() {
    }
}
