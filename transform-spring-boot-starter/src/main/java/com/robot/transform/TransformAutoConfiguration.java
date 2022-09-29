package com.robot.transform;


import com.robot.transform.annotation.Transform;
import com.robot.transform.annotation.TransformDict;
import com.robot.transform.util.SpringContextUtil;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.event.ContextRefreshedEvent;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.Map;

import static com.robot.transform.util.LambdaUtil.sure;


/**
 * 转换器配置类
 *
 * @author R
 */
@Configuration
@ComponentScan("com.**.transformer")
@Import({TranslatorAspect.class, SpringContextUtil.class})
public class TransformAutoConfiguration implements ApplicationListener<ContextRefreshedEvent> {

//    @ConditionalOnBean(IDictTransformer.class)


    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        ApplicationContext applicationContext = event.getApplicationContext();
        String dictTransformerBeanName = "dictTransformer";
        if (applicationContext.containsBean(dictTransformerBeanName)) {
            Transform annotation = TransformDict.class.getAnnotation(Transform.class);
            InvocationHandler invocationHandler = Proxy.getInvocationHandler(annotation);
            Field field = sure(() -> invocationHandler.getClass().getDeclaredField("memberValues"));
            field.setAccessible(true);
            Map<String, Object> memberValues = (Map<String, Object>) sure(() -> field.get(invocationHandler));
            memberValues.put("transformer", applicationContext.getBean(dictTransformerBeanName).getClass());
            field.setAccessible(false);
            System.out.println(annotation.transformer().getSimpleName());
        }
    }
}