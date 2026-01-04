package com.star.swiftAi.mapper.postgresql;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.star.swiftAi.entity.AiModel;
import org.apache.ibatis.annotations.Mapper;

/**
 * AI模型Mapper
 *
 * @author SHOOTING_STAR_C
 */
@Mapper
public interface AiModelMapper extends BaseMapper<AiModel> {
}
