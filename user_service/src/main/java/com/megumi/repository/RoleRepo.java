package com.megumi.repository;

import com.megumi.pojo.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface RoleRepo extends JpaRepository<Role, Integer> {
    @Modifying
    @Query(value = "insert into roles_modules values(?1,?2)", nativeQuery = true)
    void insertRoleModule(Integer rId, Integer mId);

    @Modifying
    @Query(value = "delete from roles_modules where role_id=?1", nativeQuery = true)
    void deleteRoleModuleByRId(Integer rId);

    @Modifying
    @Query(value = "insert into user_role values(?1,?2)", nativeQuery = true)
    void insertUserRole(Long uId, Integer rId);

    @Modifying
    @Query(value = "delete from user_role where role_id=?1 and user_id=?2", nativeQuery = true)
    void deleteUserRoleByRIdAndUId(Integer rId, Long uId);

    @Modifying
    @Query(value = "delete from user_role where user_id=?1", nativeQuery = true)
    void deleteUserRoleByUId(Long uId);
}
