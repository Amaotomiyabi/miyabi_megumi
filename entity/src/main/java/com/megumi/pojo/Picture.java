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
@Table(name = "picture", schema = "miyabi_megumi")
@EntityListeners(AuditingEntityListener.class)
public class Picture implements Serializable {

    @Id
    @Column(name = "id")
    @GenericGenerator(name = "snowflakeG", strategy = "com.megumi.common.JpaIdGenerator")
    @GeneratedValue(generator = "snowflakeG")
    private Long id;

    @Column(name = "size")
    private Long size;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User uploader;

    @CreatedDate
    @Column(name = "up_time")
    private LocalDateTime upTime;

    @Column(name = "path")
    private String path;

    @Column(name = "small_path")
    private String smallPath;

    @Column(name = "src")
    private String src;

    @Column(name = "d_src")
    private String dSrc;

    @Column(name = "views", columnDefinition = "int default 0")
    private Integer views;

    @Column(name = "approval", columnDefinition = "int default 0")
    private Integer approval;

    @Column(name = "collect", columnDefinition = "int default 0")
    private Integer collection;

    @Column(name = "is_sexy", columnDefinition = "varchar(10) default '0'")
    private String isSexy;

    @Column(name = "x")
    private Integer x;

    @Column(name = "y")
    private Integer y;

    @ManyToMany
    @JoinTable(name = "tag_picture", joinColumns = @JoinColumn(name = "picture_id"), inverseJoinColumns = @JoinColumn(name = "tag_id"))
    private List<PTag> tags;

    @Transient
    private List<String> tagNameList;

    public Picture(Long id) {
        this.id = id;
    }

    public Picture() {
    }

    public void init() {
        views = 0;
        approval = 0;
        collection = 0;
    }
}
