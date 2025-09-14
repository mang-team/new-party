package com.itmang.service.study.Impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.itmang.constant.DeleteConstant;
import com.itmang.constant.MessageConstant;
import com.itmang.constant.StatusConstant;
import com.itmang.context.BaseContext;
import com.itmang.exception.BaseException;
import com.itmang.mapper.study.DatasMapper;
import com.itmang.pojo.dto.DatasDTO;
import com.itmang.pojo.dto.DatasPageDTO;
import com.itmang.pojo.entity.Datas;
import com.itmang.pojo.entity.PageResult;
import com.itmang.pojo.vo.DatasPageVO;
import com.itmang.pojo.vo.DatasVO;
import com.itmang.service.study.DatasService;
import com.itmang.utils.CodeUtil;
import com.itmang.utils.IdGenerate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class DatasServiceImpl extends ServiceImpl<DatasMapper, Datas> implements DatasService {

    @Autowired
    private DatasMapper datasMapper;

    /**
     * 发布资料
     * @param dataId
     */
    public void releaseData(String dataId) {
        //1、判断资料是否存在
        Datas data = datasMapper.selectById(dataId);
        if (data == null || data.getIsDelete().equals(DeleteConstant.YES)){
            throw new BaseException(MessageConstant.DATA_NOT_EXISTS);
        }
        //2、判断资料的当前状态
        if (data.getStatus().equals(StatusConstant.ENABLE) ) {
            //资料已经被发布，不能重复发布
            throw new BaseException(MessageConstant.DATA_ALREADY_RELEASED);
        }
        //3、修改资料状态
        //重新创建一个类用于修改
        Datas updateData = Datas.builder()
                .id(dataId)
                .status(StatusConstant.ENABLE)
                .releaseBy(BaseContext.getCurrentId())
                .releaseTime(LocalDateTime.now())
                .build();
        datasMapper.updateDatasById(updateData);
    }

    /**
     * 资料缓存
     * @param addDatasDTO
     */
    public void addData(DatasDTO addDatasDTO) {
        //1、判断资料是否存在
        Datas data = datasMapper.selectById(addDatasDTO.getId());
        if(data != null && data.getIsDelete().equals(DeleteConstant.NO)){
            //资料已经存在，检查资料转态
            if (data.getStatus().equals(StatusConstant.ENABLE)) {//资料已经发布，不可以修改
                throw new BaseException(MessageConstant.DATA_ALREADY_RELEASED);
            }
            //重新创建一个类用于修改，直接拷贝
            Datas updateData = BeanUtil.copyProperties(addDatasDTO, Datas.class);
            datasMapper.updateDatasById(updateData);
        } else{//2、资料不存在，直接添加
            //先生成id
            IdGenerate idGenerate = new IdGenerate();
            String dataId = idGenerate.nextUUID(Datas.class);
            //生成number
            String dataNumber = CodeUtil.getCode();
            Datas newData = BeanUtil.copyProperties(addDatasDTO, Datas.class);
            newData.setStatus(StatusConstant.DISABLE);
            newData.setId(dataId);
            newData.setNumber(dataNumber);
            newData.setIsDelete(DeleteConstant.NO);
            datasMapper.insertDatas(newData);
        }

    }

    /**
     * 删除资料（可批量）
     * @param dataIds
     */
    public void deleteData(String[] dataIds) {
        List<String> canDeleteIds = new ArrayList<>();
        List<String> canNotDeleteIds = new ArrayList<>();
        //判断资料是否存在
        for (String id : dataIds) {
            Datas data = datasMapper.selectById(id);
            if(data == null || data.getIsDelete().equals(DeleteConstant.YES)){
                //将空的存入canNotDeleteIds集合中
                canNotDeleteIds.add(id);
                continue;
            }
            //不是空值，判断状态
            if(data.getStatus().equals(StatusConstant.ENABLE)){
                //资料是可阅读状态，不可以删除，加入canNotDeleteIds集合中
                canNotDeleteIds.add(id);
                continue;
            }
            //将满足条件的存入canDeleteIds集合中
            canDeleteIds.add(id);
        }
        // 执行删除逻辑
        if(!canDeleteIds.isEmpty()) {
            datasMapper.removeBatchByIds(canDeleteIds.toArray(new String[0]));
        }
        // 处理删除结果
        if(!canNotDeleteIds.isEmpty()) {
            if(canDeleteIds.isEmpty()) {
                throw new BaseException(MessageConstant.DATA_FAIL_DELETED);
            } else {
                throw new BaseException(MessageConstant.DATA_PART_DELETED);
            }
        }
    }

    /**
     * 修改资料状态
     * @param id
     * @param status
     */
    public void updateStatus(String id, Integer status) {
        //1、判断资料是否存在
        Datas data = datasMapper.selectById(id);
        if (data == null || data.getIsDelete().equals(DeleteConstant.YES)){
            throw new BaseException(MessageConstant.DATA_NOT_EXISTS);
        }
        //2、判断资料的当前状态
        if (data.getStatus().equals(status) ) {
            //资料已是当前状态
            throw new BaseException(status.equals(StatusConstant.ENABLE)
                    ? MessageConstant.DATA_HAVE_ALREADY_RELEASED : MessageConstant.DATA_HAVE_NOT_READ);
        }
        Integer newReleaseBy = null;
        LocalDateTime newReleaseTime = null;
        //3、修改资料状态
        //重新创建一个类用于修改
        Datas updateData = Datas.builder()
                .id(id)
                .status(StatusConstant.ENABLE)
                .releaseBy(status.equals(StatusConstant.ENABLE) ? BaseContext.getCurrentId() : null)
                .releaseTime(status.equals(StatusConstant.ENABLE) ? LocalDateTime.now() : null)
                .build();
        datasMapper.updateDatasById(updateData);
    }

    /**
     * 查询资料
     * @param id
     */
    public DatasVO searchData(String id) {
        //判断资料是否被逻辑删除
        //直接查询返回
        DatasVO dataVO = datasMapper.selectInformationById(id);
        if (dataVO == null) {
            throw new BaseException(MessageConstant.DATA_NOT_EXISTS);
        }
        return dataVO;
    }

    /**
     * 分页查询资料
     * @param datasPageDTO
     * @return
     */
    public PageResult pageData(DatasPageDTO datasPageDTO) {
        //使用pageHelper工具进行分页查询
        PageHelper.startPage(datasPageDTO.getPage(),datasPageDTO.getPageSize());
        List<DatasPageVO> datasList= datasMapper.pageSearch(datasPageDTO);//进行条件查询
        PageInfo<DatasPageVO> pageInfo = new PageInfo<>(datasList);
        return new PageResult(pageInfo.getTotal(),pageInfo.getList());
    }


}
