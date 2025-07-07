package com.discrowd.server.repository;

import com.discrowd.server.model.entity.UserServerMembership;
import feign.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserServerMembershipRepository extends JpaRepository<UserServerMembership, Long> {
    // Znajdź członkostwa dla danego użytkownika
    @Query("SELECT m FROM UserServerMembership m JOIN FETCH m.server WHERE m.userId = :userId")
    List<UserServerMembership> findByUserId(@Param("userId") Long userId);

    // Sprawdź, czy użytkownik jest członkiem danego serwera
    Optional<UserServerMembership> findByUserIdAndServerId(Long userId, Long serverId);

    // Usuń członkostwo
    void deleteByUserIdAndServerId(Long userId, Long serverId);
}
