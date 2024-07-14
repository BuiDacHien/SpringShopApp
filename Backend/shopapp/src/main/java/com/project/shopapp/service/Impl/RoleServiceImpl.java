package com.project.shopapp.service.Impl;

import com.project.shopapp.entity.Role;
import com.project.shopapp.repository.RoleRepository;
import com.project.shopapp.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {
    private final RoleRepository roleRepository;

    @Override
    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }
}
