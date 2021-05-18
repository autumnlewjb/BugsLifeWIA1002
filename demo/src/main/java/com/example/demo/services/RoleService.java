package com.example.demo.services;

import com.example.demo.models.Role;
import com.example.demo.repository.RoleRepository;
import java.util.List;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class RoleService {
    @Autowired
    private RoleRepository roleRepository;
    
    public List<Role> getRoles() {
        return roleRepository.findAll();
    }
    
    public Role createRole(Role role) {
        return roleRepository.save(role);
    }
    
    public Role searchRoleByName(String roleName) {
        return roleRepository.findByName(roleName);
    }

}
