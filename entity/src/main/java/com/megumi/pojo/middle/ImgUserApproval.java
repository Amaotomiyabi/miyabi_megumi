package com.megumi.pojo.middle;

import com.megumi.pojo.Picture;
import com.megumi.pojo.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
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
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "pictures_users_approval", schema = "miyabi_megumi")
@EntityListeners(AuditingEntityListener.class)
public class ImgUserApproval implements Serializable {

    @Id
    @Column(name = "id")
    @GenericGenerator(name = "snowflakeG", strategy = "com.megumi.common.JpaIdGenerator")
    @GeneratedValue(generator = "snowflakeG")
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToOne
    @JoinColumn(name = "picture_id")
    private Picture pictureId;

    @CreatedDate
    @Column(name = "create_time")
    private LocalDateTime createTime;

}
