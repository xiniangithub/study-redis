package com.wez.study.redis.annotation.ctrl;

import com.wez.study.redis.annotation.svc.FormDefinitionService;
import com.wez.study.redis.annotation.vo.FormDefinitionVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/form_definition")
public class FromDefinitionController {

    @Autowired
    private FormDefinitionService service;

    @GetMapping(value = "/list")
    public List<FormDefinitionVO> listFormDefinition() {
        return service.list();
    }

    @GetMapping(value = "/getById")
    public FormDefinitionVO getById(String id) {
        return service.getById(id);
    }

    @GetMapping(value = "/deleteById")
    public FormDefinitionVO deleteById(String id) {
        return service.deleteById(id);
    }

}
