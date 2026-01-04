package com.star.swiftAi.mapper.postgresql;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.star.swiftAi.entity.AiChatMessage;
import org.apache.ibatis.annotations.Mapper;

/**
 * AI聊天消息Mapper
 *
 * @author SHOOTING_STAR_C
 */
@Mapper
public interface AiChatMessageMapper extends BaseMapper<AiChatMessage> {
}
