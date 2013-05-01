/**
 * Copyright (c) 2005-2012 https://github.com/zhangkaitao
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.sishuok.es.sys.auth.service;

import com.sishuok.es.common.service.BaseService;
import com.sishuok.es.sys.auth.entity.Auth;
import com.sishuok.es.sys.auth.repository.AuthRepository;
import com.sishuok.es.sys.group.entity.Group;
import com.sishuok.es.sys.group.service.GroupService;
import com.sishuok.es.sys.organization.entity.Job;
import com.sishuok.es.sys.organization.entity.Organization;
import com.sishuok.es.sys.user.entity.User;
import com.sishuok.es.sys.user.entity.UserOrganizationJob;
import com.sishuok.es.sys.user.service.UserService;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.shiro.util.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;

/**
 * <p>User: Zhang Kaitao
 * <p>Date: 13-2-4 下午3:01
 * <p>Version: 1.0
 */
@Service
public class AuthService extends BaseService<Auth, Long> {

    private AuthRepository authRepository;

    @Autowired
    private UserService userService;
    @Autowired
    private GroupService groupService;

    @Autowired
    public void setAuthRepository(AuthRepository authRepository) {
        setBaseRepository(authRepository);
        this.authRepository = authRepository;
    }

    //why m is po??
    public void addUserAuth(Long[] userIds, Auth m) {

        if(ArrayUtils.isEmpty(userIds)) {
            return;
        }

        for(Long userId : userIds) {

            User user = userService.findOne(userId);
            if(user == null) {
                continue;
            }

            Auth auth = authRepository.findByUserId(userId);
            if(auth != null) {
                auth.addRoleIds(m.getRoleIds());
                continue;
            }
            auth = new Auth();
            auth.setUserId(userId);
            auth.setType(m.getType());
            auth.setRoleIds(m.getRoleIds());
            save(auth);
        }
    }

    public void addGroupAuth(Long[] groupIds, Auth m) {
        if(ArrayUtils.isEmpty(groupIds)) {
            return;
        }

        for(Long groupId : groupIds) {
            Group group = groupService.findOne(groupId);
            if(group == null) {
                continue;
            }

            Auth auth = authRepository.findByGroupId(groupId);
            if(auth != null) {
                auth.addRoleIds(m.getRoleIds());
                continue;
            }
            auth = new Auth();
            auth.setGroupId(groupId);
            auth.setType(m.getType());
            auth.setRoleIds(m.getRoleIds());
            save(auth);
        }
    }

    public void addOrganizationJobAuth(Long[] organizationIds, Long[][] jobIds, Auth m) {

        if(ArrayUtils.isEmpty(organizationIds)) {
            return;
        }
        for (int i = 0, l = organizationIds.length; i < l; i++) {
            Long organizationId = organizationIds[i];
            if(jobIds[i].length == 0) {
                addOrganizationJobAuth(organizationId, null, m);
                continue;
            }

            //仅新增/修改一个 spring会自动split（“，”）--->给数组
            if (l == 1) {
                for (int j = 0, l2 = jobIds.length; j < l2; j++) {
                    Long jobId = jobIds[i][0];
                    addOrganizationJobAuth(organizationId, jobId, m);
                }
            } else {
                for (int j = 0, l2 = jobIds[i].length; j < l2; j++) {
                    Long jobId = jobIds[i][0];
                    addOrganizationJobAuth(organizationId, jobId, m);
                }
            }

        }
    }

    private void addOrganizationJobAuth(Long organizationId, Long jobId, Auth m) {
        if(organizationId == null) {
            organizationId = 0L;
        }
        if(jobId == null) {
            jobId = 0L;
        }


        Auth auth = authRepository.findByOrganizationIdAndJobId(organizationId, jobId);
        if(auth != null) {
            auth.addRoleIds(m.getRoleIds());
            return;
        }

        auth = new Auth();
        auth.setOrganizationId(organizationId);
        auth.setJobId(jobId);
        auth.setType(m.getType());
        auth.setRoleIds(m.getRoleIds());
        save(auth);


    }
}