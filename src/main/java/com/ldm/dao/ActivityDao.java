package com.ldm.dao;

import com.ldm.entity.EsActivity;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Mapper
public interface ActivityDao {

    // es初始化
    @Select("SELECT activity_id,activity_name,activity_type,location_name,user_nickname FROM t_activity LEFT JOIN t_user ON t_activity.user_id=t_user.user_id")
    List<EsActivity> selectEsActivityList();
}