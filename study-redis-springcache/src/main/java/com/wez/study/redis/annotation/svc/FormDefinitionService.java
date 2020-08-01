package com.wez.study.redis.annotation.svc;

import com.wez.study.redis.annotation.vo.FormDefinitionVO;

import java.util.List;

public interface FormDefinitionService {

    List<FormDefinitionVO> list();

    FormDefinitionVO getById(String id);

    FormDefinitionVO deleteById(String id);
}
