package com.itheima.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.common.R;
import com.itheima.reggie.dto.SetmealDto;
import com.itheima.reggie.entity.Setmeal;

import java.util.List;

public interface SetmealService extends IService<Setmeal> {
    public void saveSetmealWithDish(SetmealDto setmealDto);
    public void deleteSetmealWithDish(List<Long> ids);

    /**
     * 查找套餐和套餐分类
     * @param id
     * @return
     */
    public R<SetmealDto> getSetmealWithCategory(Long id);

    public void updateSetmealWithDish(SetmealDto setmealDto);
}
