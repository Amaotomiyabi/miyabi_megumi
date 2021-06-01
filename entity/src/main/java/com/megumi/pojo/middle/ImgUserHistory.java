package com.megumi.pojo.middle;

import com.megumi.pojo.Picture;
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
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "pictures_users_history", schema = "miyabi_megumi")
@EntityListeners(AuditingEntityListener.class)
@NamedEntityGraphs({
        @NamedEntityGraph(name = "noUser", attributeNodes = {
                @NamedAttributeNode(value = "picture", subgraph = "picture"),
        }, subgraphs = {
                @NamedSubgraph(name = "picture", attributeNodes = {
                        @NamedAttributeNode(value = "tags", subgraph = "tags")
                }),
                @NamedSubgraph(name = "tags", attributeNodes = {}),
        })
})

public class ImgUserHistory implements Serializable {

    @Id
    @Column(name = "id")
    @GenericGenerator(name = "snowflakeG", strategy = "com.megumi.common.JpaIdGenerator")
    @GeneratedValue(generator = "snowflakeG")
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @OneToOne
    @JoinColumn(name = "picture_id")
    private Picture picture;

    @CreatedDate
    @Column(name = "browse_time")
    private LocalDateTime browseTime;

}
