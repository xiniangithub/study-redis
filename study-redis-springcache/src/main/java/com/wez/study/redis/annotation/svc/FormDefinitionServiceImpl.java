package com.wez.study.redis.annotation.svc;

import com.wez.study.redis.annotation.vo.FormDefinitionVO;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class FormDefinitionServiceImpl implements FormDefinitionService {

    private Map<String, FormDefinitionVO> formDefinitionMap;

    @PostConstruct
    private void init() {
        FormDefinitionVO vo1 = FormDefinitionVO.build("1", "普通任务表单");
        FormDefinitionVO vo2 = FormDefinitionVO.build("2", "警情处置表单");
        FormDefinitionVO vo3 = FormDefinitionVO.build("3", "警情处置表单");

        formDefinitionMap = new HashMap<>();
        formDefinitionMap.put(vo1.getId(), vo1);
        formDefinitionMap.put(vo2.getId(), vo2);
        formDefinitionMap.put(vo3.getId(), vo3);
    }

    @Cacheable(value = "form_definition", key = "'list'", unless="#result == null")
    @Override
    public List<FormDefinitionVO> list() {
        System.out.println("从数据库中获取数据到表单定义列表");
        return new ArrayList<>(formDefinitionMap.values());
    }

    @Cacheable(value = "form_definition", key = "#id", unless="#result == null")
    @Override
    public FormDefinitionVO getById(String id) {
        System.out.println("从数据库中获取数据到" + id);
        return formDefinitionMap.get(id);
    }

    // 清除form_definition::list缓存和form_definition::#id缓存
    // 方式一：使用@Caching
    @Caching(evict = {
            @CacheEvict(value="form_definition", key = "'list'"),
            @CacheEvict(value="form_definition", key = "#id")
    })
    // 方式二：使用@CacheEvict的allEntries设置为true
//    @CacheEvict(value="form_definition", allEntries = true)
    @Override
    public FormDefinitionVO deleteById(String id) {
        return formDefinitionMap.remove(id);
    }


}
