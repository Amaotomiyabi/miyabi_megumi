package com.megumi.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Objects;

/**
 * 2021/2/21
 *
 * @author miyabi
 * @since 1.0
 */
@Data
@Entity
@Table(name = "p_tag", schema = "miyabi_megumi")
@EntityListeners(AuditingEntityListener.class)
@JsonIgnoreProperties({"uploader"})
@AllArgsConstructor
@NoArgsConstructor
public class PTag implements Serializable {

    @Id
    @Column(name = "id")
    @GenericGenerator(name = "snowflakeG", strategy = "com.megumi.common.JpaIdGenerator")
    @GeneratedValue(generator = "snowflakeG")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "views", columnDefinition = "int default 0")
    private Integer views;

    @CreatedDate
    @Column(name = "create_time")
    private LocalDateTime createTime;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User uploader;

    public PTag(Long id) {
        this.id = id;
    }

    public PTag(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PTag pTag = (PTag) o;
        return name.equalsIgnoreCase(pTag.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name.toLowerCase(Locale.ROOT));
    }
}
