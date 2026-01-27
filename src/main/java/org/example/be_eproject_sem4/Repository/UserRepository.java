package org.example.be_eproject_sem4.Repository;

import org.example.be_eproject_sem4.Entity.User;
import org.example.be_eproject_sem4.Entity.User.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long>{
    @Query("SELECT u FROM User u " +
            "WHERE u.username = :username")
    Optional<User> findByUsername(String username);
    boolean  existsByUsername(String username);
    List<User> findAllByRoleAndIsVerifiedOrderByEloScoreDesc(User.Role role, boolean isVerified);
    Object findByEmail(String email);

    List<User> findTop10ByRoleOrderByEloScoreDesc(User.Role role);
    List<User> findTop11ByRoleAndIdNotOrderByEloScoreDesc(User.Role role, Long userId);

    
}
