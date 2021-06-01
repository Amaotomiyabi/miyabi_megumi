package com.megumi.pojo;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

/**
 * 2021/2/21
 *
 * @author miyabi
 * @since 1.0
 */
@Data
@Entity
@Table(name = "module", schema = "miyabi_megumi")
public class Module implements Serializable {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "m_desc")
    private String desc;

    @Column(name = "url")
    private String url;

}
