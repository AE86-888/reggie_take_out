package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.MyException;
import com.itheima.reggie.common.R;
import com.itheima.reggie.dto.SetmealDto;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.entity.Setmeal;
import com.itheima.reggie.entity.SetmealDish;
import com.itheima.reggie.service.CategoryService;
import com.itheima.reggie.service.SetmealDishService;
import com.itheima.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@Slf4j
@RequestMapping("/setmeal")
public class SetmealController {
    @Autowired
    private SetmealDishService setmealDishService;
    @Autowired
    private SetmealService setmealService;
    @Autowired
    private CategoryService categoryService;

    @PostMapping
    public R<String> save(@RequestBody SetmealDto setmealDto) {
        log.info("进入新增套餐。。。。。");
        setmealService.saveSetmealWithDish(setmealDto);
        return R.success("新增套餐成功");
    }

    /**
     * 套餐的分页查询
     *
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name) {
        log.info("进入套餐分页查询");
        Page<Setmeal> pageInfo = new Page<>(page, pageSize);
        Page<SetmealDto> setmealDtoPage = new Page<>();

        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealLambdaQueryWrapper.orderByDesc(Setmeal::getUpdateTime);
        setmealLambdaQueryWrapper.like(name != null, Setmeal::getName, name);
        setmealService.page(pageInfo, setmealLambdaQueryWrapper);

        //对象拷贝
        BeanUtils.copyProperties(pageInfo, setmealDtoPage, "records");
        List<Setmeal> records = pageInfo.getRecords();
        List<SetmealDto> list = records.stream().map((item) -> {
            SetmealDto setmealDto = new SetmealDto();
            BeanUtils.copyProperties(item, setmealDto);
            Long categoryId = item.getCategoryId();
            Category category = categoryService.getById(categoryId);
            if (category != null) {
                String categoryName = category.getName();
                setmealDto.setCategoryName(categoryName);
            }
            return setmealDto;
        }).collect(Collectors.toList());
        setmealDtoPage.setRecords(list);
        return R.success(setmealDtoPage);
    }

    /**
     * 删除套餐功能
     *
     * @param ids
     * @return
     */
    @DeleteMapping()
    public R<String> delete(@RequestParam List<Long> ids) {
        log.info("进入删除套餐。。。");
        setmealService.deleteSetmealWithDish(ids);
        return R.success("套餐删除成功");
    }

    /**
     * 停售套餐
     *
     * @param ids
     * @return
     */
    @PostMapping("/status/0")
    public R<String> stop(@RequestParam List<Long> ids) {
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Setmeal::getStatus, 1);
        queryWrapper.in(Setmeal::getId, ids);
        List<Setmeal> setmeals = setmealService.list(queryWrapper);
        if (setmeals == null || setmeals.size() == 0){
            throw new MyException("所选套餐已经处于停售卖状态");
        }
        for (Setmeal setmeal : setmeals) {
            setmeal.setStatus(0);
        }
        setmealService.updateBatchById(setmeals);
        return R.success("停售成功");
    }

    /**
     * 批量启售
     *
     * @param ids
     * @return
     */
    @PostMapping("/status/1")
    public R<String> start(@RequestParam List<Long> ids) {
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Setmeal::getStatus, 0);//找到停售的
        queryWrapper.in(Setmeal::getId, ids);
        List<Setmeal> setmeals = setmealService.list(queryWrapper);
        if (setmeals == null || setmeals.size() == 0){
            throw new MyException("所选套餐已经处于启售状态");
        }
        for (Setmeal setmeal : setmeals) {
            setmeal.setStatus(1);
        }
        setmealService.updateBatchById(setmeals);
        return R.success("批量启售成功！");
    }

    @GetMapping("/{id}")
    public R<SetmealDto> getById(@PathVariable(value = "id") Long id){
        R<SetmealDto> setmealWithCategory = setmealService.getSetmealWithCategory(id);
        return setmealWithCategory;
    }

    @PutMapping
    public R<String> update(@RequestBody SetmealDto setmealDto){
        log.info("进入修改套餐。。。。");
        setmealService.updateSetmealWithDish(setmealDto);
        return R.success("修改套餐成功");
    }

    @GetMapping("/list")
    public R<List<SetmealDto>> list(Setmeal setmeal){
        log.info("");
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Setmeal::getCategoryId, setmeal.getCategoryId());
        queryWrapper.eq(Setmeal::getStatus, setmeal.getStatus());
        List<Setmeal> list = setmealService.list(queryWrapper);
        List<SetmealDto> setmealDtoList = list.stream().map((item) ->{
            SetmealDto setmealDto = new SetmealDto();
            BeanUtils.copyProperties(item, setmealDto);
            LambdaQueryWrapper<SetmealDish> setmealDishLambdaQueryWrapper = new LambdaQueryWrapper<>();
            setmealDishLambdaQueryWrapper.eq(SetmealDish::getSetmealId,item.getId());
            List<SetmealDish> setmealDishes = setmealDishService.list(setmealDishLambdaQueryWrapper);
            setmealDto.setSetmealDishes(setmealDishes);
            return setmealDto;
        }).collect(Collectors.toList());
        return R.success(setmealDtoList);
    }

}
