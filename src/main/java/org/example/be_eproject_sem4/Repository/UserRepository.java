package org.example.be_eproject_sem4.Repository;

import org.example.be_eproject_sem4.Entity.User;
import org.example.be_eproject_sem4.Entity.User.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.io.Reader;
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
    // Dùng cho Matching thông minh
    @Query("SELECT u FROM User u WHERE u.role = 'READER' " +
            "AND u.isActive = true " +
            "AND u.isVerified = true " +
            "AND u.isBlocked = false " +
            "AND u.isBusy = false " +
            "AND u.id NOT IN :excludedIds")
    List<User> findAvailableReadersForMatching(@Param("excludedIds") List<Long> excludedIds);

    
}
