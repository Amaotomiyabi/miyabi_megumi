package com.megumi.pojo;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 2021/2/21
 *
 * @author miyabi
 * @since 1.0
 */
@Data
@Entity
@Table(name = "user", schema = "miyabi_megumi")
@EntityListeners(AuditingEntityListener.class)
//@JsonIgnoreProperties({"pwd", "isCancel", "isLock", "phone", "roles", "userAccount", "email", "createTime", "lastLoginTime"})
public class User implements Serializable {

    @Id
    @Column(name = "id")
    @GenericGenerator(name = "snowflakeG", strategy = "com.megumi.common.JpaIdGenerator")
    @GeneratedValue(generator = "snowflakeG")
    private Long id;

    @Column(name = "user_account")
    private String userAccount;

    @Column(name = "username")
    private String username;

    @Column(name = "pwd")
    private String pwd;

    @Column(name = "email")
    private String email;

    @OneToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH, CascadeType.REMOVE})
    @JoinColumn(name = "photo")
    private Photo photo;

    @Column(name = "phone")
    private String phone;

    @CreatedDate
    @Column(name = "create_time")
    private LocalDateTime createTime;

    @Column(name = "is_lock", columnDefinition = "int default 0")
    private Integer isLock;

    @Column(name = "is_cancel", columnDefinition = "int default 0")
    private Integer isCancel;

    @Column(name = "follower_count", columnDefinition = "int default 0")
    private Integer followerCount;

    @Column(name = "subscribe_count", columnDefinition = "int default 0")
    private Integer subscribeCount;

    @Column(name = "works_count", columnDefinition = "int default 0")
    private Integer worksCount;

    @Column(name = "collect_img_cnt", columnDefinition = "int default 0")
    private Integer collectImgCnt;

    @Column(name = "last_login_time")
    private LocalDateTime lastLoginTime;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_role", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
    private List<Role> roles;

    public User(Long id) {
        this.id = id;
    }

    public User() {
    }

    public void init() {
        isLock = 0;
        isCancel = 0;
        followerCount = 0;
        collectImgCnt = 0;
        worksCount = 0;
        subscribeCount = 0;
    }


}
