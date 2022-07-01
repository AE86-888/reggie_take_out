package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/category")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    /**
     * 新增菜品
     * @param category
     * @return
     */
    @PostMapping()
    public R<String> save(@RequestBody Category category) {
        log.info("进入save方法====");
        categoryService.save(category);
        return R.success("新增菜品成功");
    }

    /**
     * 分页查询
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize) {
        log.info("进入菜品分页查询");
        Page<Category> pageInfo = new Page(page, pageSize);
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByAsc(Category::getSort);
        categoryService.page(pageInfo,queryWrapper);
        return R.success(pageInfo);
    }

    /**
     * 删除
     * @param ids
     * @return
     */
    @DeleteMapping
    public R<String> delete(Long ids){
        log.info("进入删除菜品。。。。");
        //categoryService.removeById(ids);
        categoryService.remove(ids);
        return R.success("删除菜品成功");
    }
    /**
     * 修改
     */

    @PutMapping
    public R<String> update(@RequestBody Category category){
        log.info("category ：" + category.toString());
        categoryService.updateById(category);
        return R.success("修改分类信息成功");
    }

    /**
     * 查询菜品分类
     * @param
     * @return
     */
    @GetMapping("/list")
    public R<List<Category>> list(Category category){
        //构造查询条件
        LambdaQueryWrapper<Category> categoryLambdaQueryWrapper = new LambdaQueryWrapper<>();
        categoryLambdaQueryWrapper.eq(category.getType() != null, Category::getType, category.getType());
        categoryLambdaQueryWrapper.orderByAsc(Category::getSort).orderByDesc(Category::getUpdateTime);
        //执行查询
        List<Category> list = categoryService.list(categoryLambdaQueryWrapper);
        return R.success(list);
    }


}
