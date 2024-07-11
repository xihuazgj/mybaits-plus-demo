package com.itheima.mp.domain.dto;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Data
@ApiModel(description = "通用分页结果")
public class PageDTO<T> {
    @ApiModelProperty("总条数")
    private Long total;
    @ApiModelProperty("总页数")
    private Long pages;
    @ApiModelProperty("集合")
    private List<T> list;

    public static <PO,VO> PageDTO<VO> of(Page<PO> page, Function<PO,VO> convertor){
        //3.封装VO结果
        PageDTO<VO> dto = new PageDTO<>();
        dto.setTotal(page.getTotal());
        dto.setPages(page.getPages());
        List<PO> records = page.getRecords();
        if (CollUtil.isEmpty(records)){
            dto.setList(Collections.emptyList());
            return dto;
        }
        //拷贝vo
        dto.setList(records.stream().map(convertor).collect(Collectors.toList()));
        //4.返回
        return dto;
    }

    public static <PO,VO> PageDTO<VO> of(Page<PO> page, Class<VO> clazz){
        //3.封装VO结果
        PageDTO<VO> dto = new PageDTO<>();
        dto.setTotal(page.getTotal());
        dto.setPages(page.getPages());
        List<PO> records = page.getRecords();
        if (CollUtil.isEmpty(records)){
            dto.setList(Collections.emptyList());
            return dto;
        }
        //拷贝vo
        dto.setList(BeanUtil.copyToList(records,clazz));
        //4.返回
        return dto;
    }
}
