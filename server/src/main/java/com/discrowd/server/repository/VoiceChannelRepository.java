package com.discrowd.server.repository;

import com.discrowd.server.model.VoiceChannel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VoiceChannelRepository extends JpaRepository<VoiceChannel, Long> {
}
