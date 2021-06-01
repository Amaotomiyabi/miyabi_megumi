package com.megumi.pojo;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 2021/2/21
 *
 * @author miyabi
 * @since 1.0
 */

@Data
@Entity
@Table(name = "photo", schema = "miyabi_megumi")
@EntityListeners(AuditingEntityListener.class)
public class Photo implements Serializable {

    @Id
    @Column(name = "id")
    @GenericGenerator(name = "snowflakeG", strategy = "com.megumi.common.JpaIdGenerator")
    @GeneratedValue(generator = "snowflakeG")
    private Long id;

    @Column(name = "path")
    private String path;

    @Column(name = "small_path")
    private String smallPath;

    @CreatedDate
    @Column(name = "create_time")
    private LocalDateTime createTime;

    @Column(name = "user_id")
    private Long userId;
}
