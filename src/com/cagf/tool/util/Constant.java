package com.cagf.tool.util;

public class Constant {

    //ant 参数常量
    public static final String OUT_PUT_DIR = "--outputDir=";
    public static final String TEMPLATE_FILE = "--templateFile=";

    //常见操作符

    public static final String DOT = ".";

    //八匹马中的一些包名

    public static final String QUERY = "query";
    public static final String APPLICATION = "application";
    public static final String FORM = "Form";
    public static final String Long = "Long";
    public static final String ID = "Id";
    public static final String DATE = "Date";
    public static final String BIGDECIMAL = "BigDecimal";
    public static final String DATA = "data";


    //模板 参数常量

    public static final String PACKAGE_NAME = "@PACKAGE_NAME@";                                             //当前类所属包名
    public static final String CLASS_FORM_FULL_NAME = "@CLASS_FORM_FULL_NAME@";                             //ORM类所需要的Form实体
    public static final String CLASS_DATA_FULL_NAME = "@CLASS_DATA_FULL_NAME@";                             //ORM类所需要的Data实体
    public static final String IMPORT_TYPE = "@IMPORT_TYPE@";                                               //使用字段所产生的类型
    public static final String TABLE_NAME = "@TABLE_NAME@";                                                 //实体对应的表名
    public static final String CLASS_NAME = "@CLASS_NAME@";                                                 //实体名字
    public static final String CLASS_FULL_NAME = "@CLASS_FULL_NAME@";                                       //实体全限定名
    public static final String PROPERTY = "@PROPERTY@";                                                     //私有属性
    public static final String DEFAULT_CONSTRUCTOR = "@DEFAULT_CONSTRUCTOR@";                               //默认构造器
    public static final String HAVE_FORM_ARGS_CONSTRUCTOR = "@HAVE_FORM_ARGS_CONSTRUCTOR@";                 //拥有Form属性构造器
    public static final String HAVE_CLASS_ARG_CONSTRUCTOR = "@HAVE_CLASS_ARG_CONSTRUCTOR@";                 //拥有CLASS参数的构造器
    public static final String GETTER_AND_SETTER = "@GETTER_AND_SETTER@";                                   //getter和setter方法
    public static final String UPDATE_METHOD = "@UPDATE_METHOD@";                                           //更新方法
    public static final String MANY_TO_ONE_SERVICE_METHOD = "@MANY_TO_ONE_SERVICE_METHOD@";                 //ManyToOne所产生service接口查询方法
    public static final String MANY_TO_ONE_SERVICE_IMPL_METHOD = "@MANY_TO_ONE_SERVICE_IMPL_METHOD@";       //ManyToOne所产生serviceImpl查询方法
    public static final String REPOSITORY_CLASS_FULL_NAME = "@REPOSITORY_CLASS_FULL_NAME@";                 //Repository类全限定名
    public static final String MANY_TO_ONE_REPOSITORY_QUERY = "@MANY_TO_ONE_REPOSITORY_QUERY@";             //ManyToOne所产生的Dao查询
    public static final String REPOSITORY_SAVE_OPERATOR = "@REPOSITORY_SAVE_OPERATOR@";                     //Repository保存操作
    public static final String ORM_UPDATE_OPERATOR = "@ORM_UPDATE_OPERATOR@";                               //ORM更新操作
}
