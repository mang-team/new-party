package com.itmang.service.study.Impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.itmang.constant.*;
import com.itmang.context.BaseContext;
import com.itmang.exception.BaseException;
import com.itmang.mapper.study.DatasMapper;
import com.itmang.pojo.dto.DatasDTO;
import com.itmang.pojo.dto.DatasPageDTO;
import com.itmang.pojo.entity.Datas;
import com.itmang.pojo.entity.PageResult;
import com.itmang.pojo.entity.User;
import com.itmang.pojo.vo.DatasPageVO;
import com.itmang.pojo.vo.DatasVO;
import com.itmang.service.study.DatasService;
import com.itmang.utils.CodeUtil;
import com.itmang.utils.IdGenerate;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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
            //做一个传入数据的拦截，判断数据是否有效
            if (addDatasDTO.getTitle() == null || addDatasDTO.getTitle().equals("")
                    || addDatasDTO.getContent() == null || addDatasDTO.getContent().equals("")){
                throw new BaseException(MessageConstant.DATA_INVALID);
            }
            //数据库中不存在，但是又有传入id，说明这个资料已经不在了，直接报错
            if(addDatasDTO.getId() != null){
                throw new BaseException(MessageConstant.DATA_NOT_EXISTS);
            }
            //先生成id
            IdGenerate idGenerate = new IdGenerate();
            String dataId = idGenerate.nextUUID(Datas.class);
            //生成number
            String dataNumber = CodeUtil.getCode();
            //判断图标是否有传入，如果没有，则使用默认图标
            if(addDatasDTO.getIcon() == null || addDatasDTO.getIcon().equals("")){
                addDatasDTO.setIcon(IconConstant.DatasIcon);
            }
            //判断分数的字段是否是Integer类型，如果是其他类型，则直接设置为0，如果是数字则直接转换为int，并且保证是正数
            //1.判断分数字段是否为Integer类型
            Integer points = null;
            try {
                points = Integer.valueOf(addDatasDTO.getPoints());
                if (points < 0) {
                    points = 0;
                }
            } catch (NumberFormatException e) {
                points = 0;
            }
            //2.设置分数并拷贝其他数据
            Datas newData = Datas.builder()
                    .title(addDatasDTO.getTitle())
                    .content(addDatasDTO.getContent())
                    .points(points)
                    .icon(addDatasDTO.getIcon())
                    .status(StatusConstant.DISABLE)
                    .id(dataId)
                    .number(dataNumber)
                    .isDelete(DeleteConstant.NO)
                    .build();
            datasMapper.insertDatas(newData);
        }

    }

    /**
     * 删除资料（可批量）
     * @param dataIds
     */
    public void deleteData(String[] dataIds) {
        //进行传参判空
        if(dataIds == null || dataIds.length == 0){
            throw new BaseException(MessageConstant.DATA_NOT_EXISTS);
        }
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
        //查看是否有页数据
        //配置默认页码1和分页大小5
        if(datasPageDTO.getPage() == null || datasPageDTO.getPage() < 1){
            datasPageDTO.setPage(PageConstant.PAGE_NUM);
        }
        //查看是否有分页大小
        if(datasPageDTO.getPageSize() == null || datasPageDTO.getPageSize() < 1){
            datasPageDTO.setPageSize(PageConstant.PAGE_SIZE);
        }
        //对传入的参数进行判断
        //1.对积分进行判断，判断是否是Integer类型，如果不是则报错
        if(datasPageDTO.getPoints() != null && !NumberUtils.isCreatable(datasPageDTO.getPoints().toString())){
            throw new BaseException(MessageConstant.POINTS_NOT_NUMBER);
        }
        //2.对状态进行判断，判断是否是Integer类型，如果不是则报错
        if(datasPageDTO.getStatus() != null && !NumberUtils.isCreatable(datasPageDTO.getStatus().toString())){
            throw new BaseException(MessageConstant.STATUS_NOT_NUMBER);
        }
        PageHelper.startPage(datasPageDTO.getPage(),datasPageDTO.getPageSize());
        List<DatasPageVO> datasList= datasMapper.pageSearch(datasPageDTO);//进行条件查询
        PageInfo<DatasPageVO> pageInfo = new PageInfo<>(datasList);
        return new PageResult(pageInfo.getTotal(),pageInfo.getList());
    }


}
