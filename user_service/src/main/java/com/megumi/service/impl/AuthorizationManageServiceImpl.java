package com.megumi.service.impl;

import com.megumi.common.StateCode;
import com.megumi.pojo.Module;
import com.megumi.pojo.Role;
import com.megumi.repository.ModuleRepo;
import com.megumi.repository.RoleRepo;
import com.megumi.service.AuthorizationManageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthorizationManageServiceImpl implements AuthorizationManageService {
    public final Integer DEFAULT_ROLE = 1;
    private final RoleRepo roleRepo;
    private final ModuleRepo moduleRepo;

    @Autowired
    public AuthorizationManageServiceImpl(RoleRepo roleRepo, ModuleRepo moduleRepo) {
        this.roleRepo = roleRepo;
        this.moduleRepo = moduleRepo;
    }


    @Override
    public String addRole(String name, String desc, List<Integer> modules) {
        var role = new Role();
        role.setName(name);
        role.setDesc(desc);
        roleRepo.saveAndFlush(role);
        modules.forEach(module -> roleRepo.insertRoleModule(role.getId(), module));
        return StateCode.TRUE;
    }

    @Override
    public String addModule(Module module) {
        moduleRepo.save(module);
        return StateCode.TRUE;
    }

    @Override
    public String addModules(List<Module> modules) {
        moduleRepo.saveAll(modules);
        return StateCode.TRUE;
    }

    @Override
    public String modifyRole(Integer id, List<Integer> modules) {
        roleRepo.deleteRoleModuleByRId(id);
        modules.forEach(module -> roleRepo.insertRoleModule(id, module));
        return StateCode.TRUE;
    }

    @Override
    public String removeRole(Integer id) {
        roleRepo.deleteById(id);
        roleRepo.deleteRoleModuleByRId(id);
        return StateCode.TRUE;
    }

    @Override
    public String removeModule(Integer id) {
        moduleRepo.deleteById(id);
        moduleRepo.deleteRoleModuleByMId(id);
        return StateCode.TRUE;
    }

    @Override
    public String modifyUserRole(Long uId, List<Integer> roles) {
        roleRepo.deleteUserRoleByUId(uId);
        roles.forEach(role -> roleRepo.insertUserRole(uId, role));
        return StateCode.TRUE;
    }

    @Override
    public String setUserDefaultRole(Long uId) {
        roleRepo.insertUserRole(uId, DEFAULT_ROLE);
        return StateCode.TRUE;
    }

}
