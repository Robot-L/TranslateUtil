package com.robot.transform.extend.transformer;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.robot.transform.extend.annotation.TransformMapper;
import com.robot.transform.transformer.Transformer;
import com.robot.transform.util.SpringContextUtil;
import com.robot.transform.util.TransformUtil;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Nonnull;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;

/**
 * Mapper转换器
 * 可在本地数据将id转换成另一个指定属性
 *
 * @author R
 * @see TransformMapper（Mapper转换注解）
 */
@Slf4j
public class MapperTransformer implements Transformer<Serializable, TransformMapper> {

    @Override
    public String transform(@Nonnull Serializable id, @Nonnull TransformMapper annotation) {
        Class<? extends BaseMapper<?>> clazz = annotation.value();
        BaseMapper<?> mapper = SpringContextUtil.getBean(clazz);
        Object entity = mapper.selectById(id);
        if (entity == null) {
            log.warn("转换警告：{}类根据id={}，找不到任何数据！", clazz.getSimpleName(), id);
            return null;
        }
        try {
            return (String) TransformUtil.readMethodInvoke(entity.getClass(), annotation.targetField());
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException("找不到该属性，请检查注解@TransformService的field字段配置：" + annotation);
        } catch (InvocationTargetException | IllegalAccessException e) {
            log.error("转换异常：通过{}转换失败，参数：{}", mapper.getClass().getSimpleName(), annotation, e);
            return null;
        }
    }
}