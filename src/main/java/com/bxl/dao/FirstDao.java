package com.bxl.dao;

import com.bxl.entity.Goods;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

/**
 * Created by root on 2019/2/21.
 */

@Repository //这个可以不加,但是service在注入的时候会有警告
public interface FirstDao {

    Goods findGoodById(Integer gid);
}
