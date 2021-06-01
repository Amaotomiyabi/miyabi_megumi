package com.megumi.pojo.middle;

import com.megumi.pojo.PTag;
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
@Table(name = "tags_users_history", schema = "miyabi_megumi")
@EntityListeners(AuditingEntityListener.class)
public class TagUserHistory implements Serializable {

    @Id
    @Column(name = "id")
    @GenericGenerator(name = "snowflakeG", strategy = "com.megumi.common.JpaIdGenerator")
    @GeneratedValue(generator = "snowflakeG")
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @OneToOne
    @JoinColumn(name = "tag_id")
    private PTag tag;

    @CreatedDate
    @Column(name = "browse_time")
    private LocalDateTime browseTime;

}
