package com.discrowd.server.repository;

import com.discrowd.server.model.TextChannel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TextChannelRepository extends JpaRepository<TextChannel, Long> {

}
