package com.megumi.repository;

import com.megumi.pojo.Module;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

/**
 * 2021/2/21
 *
 * @author miyabi
 * @since 1.0
 */
public interface ModuleRepo extends JpaRepository<Module, Integer> {


    @Modifying
    @Query(value = "delete from roles_modules where module_id=?1", nativeQuery = true)
    void deleteRoleModuleByMId(Integer mId);

}
