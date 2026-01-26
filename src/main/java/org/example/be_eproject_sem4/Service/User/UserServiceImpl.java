package org.example.be_eproject_sem4.Service.User;

import org.example.be_eproject_sem4.Dto.Auth.UserDto;
import org.example.be_eproject_sem4.Entity.User;
import org.example.be_eproject_sem4.Repository.UserRepository;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepo;

    @Transactional
    public User getById(Long id){
        User user = userRepo.findById(id).orElseThrow(() -> new RuntimeException("Không tìm thấy user"));
        return user;
    }

}