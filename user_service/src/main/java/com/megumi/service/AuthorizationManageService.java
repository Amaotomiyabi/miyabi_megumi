package com.megumi.service;

import com.megumi.pojo.Module;

import java.util.List;

public interface AuthorizationManageService {

    String addRole(String name, String desc, List<Integer> modules);

    String addModule(Module module);

    String addModules(List<Module> modules);

    String modifyRole(Integer id, List<Integer> modules);

    String removeRole(Integer id);

    String removeModule(Integer id);

    String modifyUserRole(Long uId, List<Integer> roles);

    String setUserDefaultRole(Long uId);

}
