package com.megumi.pojo.middle;

import com.megumi.pojo.PTag;
import com.megumi.pojo.Picture;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;

/**
 * 2021/3/7
 *
 * @author miyabi
 * @since 1.0
 */
@Data
@Entity
@Table(name = "tag_picture", schema = "miyabi_megumi")
public class TagPicture implements Serializable {

    @Id
    @Column(name = "id")
    @GenericGenerator(name = "snowflakeG", strategy = "com.megumi.common.JpaIdGenerator")
    @GeneratedValue(generator = "snowflakeG")
    private Long id;

    @OneToOne
    @JoinColumn(name = "tag_id")
    private PTag pTag;

    @OneToOne
    @JoinColumn(name = "picture_id")
    private Picture picture;

}
