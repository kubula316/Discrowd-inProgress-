package com.discrowd.server.repository;

import com.discrowd.server.model.entity.Server;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServerRepository extends MongoRepository<Server, String> {

    List<Server> findByMemberships_UserId(Long userId);
}
