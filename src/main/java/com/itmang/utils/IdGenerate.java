package com.itmang.utils;

import com.baomidou.mybatisplus.core.incrementer.IdentifierGenerator;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class IdGenerate implements IdentifierGenerator {

    // 使用雪花算法生成ID
    @Override
    public Number nextId(Object entity) {
        long id = IdWorker.getId(entity); // 生成雪花ID
        log.info("为{}生成雪花ID->:{}", entity.getClass().getName(), id);
        return id;
    }

    // 如果需要字符串形式的UUID，仍可以提供此方法
    @Override
    public String nextUUID(Object entity) {
        return IdWorker.get32UUID(); // 生成32位UUID（不带连字符）
    }

}