package com.star.swiftAi.mapper.postgresql;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.star.swiftAi.entity.AiChatSession;
import org.apache.ibatis.annotations.Mapper;

/**
 * AI聊天会话Mapper
 *
 * @author SHOOTING_STAR_C
 */
@Mapper
public interface AiChatSessionMapper extends BaseMapper<AiChatSession> {
}
