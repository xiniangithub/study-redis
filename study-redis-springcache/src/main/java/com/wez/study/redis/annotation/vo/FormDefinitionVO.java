package com.wez.study.redis.annotation.vo;

/**
 * 自定义表单
 */
public class FormDefinitionVO {

    private String id;
    private String formName;

    public FormDefinitionVO() {
    }

    public FormDefinitionVO(String id, String formName) {
        this.id = id;
        this.formName = formName;
    }

    public static FormDefinitionVO build(String id, String formName) {
        return new FormDefinitionVO(id, formName);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFormName() {
        return formName;
    }

    public void setFormName(String formName) {
        this.formName = formName;
    }

    @Override
    public String toString() {
        return "FormDefinitionVO{" +
                "id='" + id + '\'' +
                ", formName='" + formName + '\'' +
                '}';
    }
}
