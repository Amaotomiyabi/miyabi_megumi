package com.megumi.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

/**
 * 2021/2/21
 *
 * @author miyabi
 * @since 1.0
 */
@Data
@Table(name = "role", schema = "miyabi_megumi")
@Entity
public class Role implements Serializable {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name")
    private String name;

    @Column(name = "r_desc")
    private String desc;

    @JsonIgnore
    @ManyToMany
    @JoinTable(name = "roles_modules", joinColumns = @JoinColumn(name = "role_id"), inverseJoinColumns = @JoinColumn(name = "module_id"))
    private List<Module> modules;
}
