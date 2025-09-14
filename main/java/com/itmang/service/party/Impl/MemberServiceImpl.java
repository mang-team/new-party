package com.itmang.service.party.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itmang.mapper.party.MemberMapper;
import com.itmang.pojo.entity.Member;
import com.itmang.service.party.MemberService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class MemberServiceImpl extends ServiceImpl<MemberMapper, Member> implements MemberService {
    @Autowired
    private MemberMapper memberMapper;
}
