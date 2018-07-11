package com.cagf.tool.util;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import javax.persistence.Id;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileUtils {


    //用于存放property type，之后一些model用得到
    public static Map<String, String> ordinaryPropertyMap = new HashMap<>();

    //静态map,维护这hibernate与java类型的映射
    public static Map<String,String> hibernate2JavaMap = new HashMap<>();

    //用于存放many-to-one的属性，在一些构造器或方法中需要用到
    public static Map<String, String> manyToOnePropertyMap = new HashMap<>();

    static {
        hibernate2JavaMap.put("integer","java.lang.Integer");
        hibernate2JavaMap.put("long","java.lang.Long");
        hibernate2JavaMap.put("short","java.lang.Short");
        hibernate2JavaMap.put("float","java.lang.Float");
        hibernate2JavaMap.put("double","java.lang.Double");
        hibernate2JavaMap.put("big_decimal","java.math.BigDecimal");
        hibernate2JavaMap.put("character","java.lang.String");
        hibernate2JavaMap.put("string","java.lang.String");
        hibernate2JavaMap.put("byte","java.lang.Byte");
        hibernate2JavaMap.put("boolean", "java.lang.Boolean");
        hibernate2JavaMap.put("date", "java.util.Date");
        hibernate2JavaMap.put("time", "java.util.Date");
        //flag：在form实体中，我怎么确定，这个Date是要Date还是datetime ，也就是要不要JsonFormat
        //貌似该系统form接受的都是Data，都要加上JsonFormat，如果后续存在问题，那么进行完善

    }

    private static StringBuilder importPart;

    //参数：字符串
    //返回：首字母小写的字符串
    public static String getLowerClassName(String className) {
        String lowerLetter = className.substring(0, 1).toLowerCase();
        return lowerLetter + className.substring(1);
    }

    //参数：字符串
    //返回：首字母大写的字符串
    public static String getUpperName(String str) {
        String upperLetter = str.substring(0, 1).toUpperCase();
        return upperLetter + str.substring(1);
    }

    //参数：类的全限定名
    //返回：基础包名
    //例子：
    //  参数：cn.com.workapp.carmgr.domain.model.handovercar.RepaymentPlanDet
    //  返回：cn.com.workapp.carmgr
    public static String getBasicPackageName(String fullClassName) {
        int nextPos = fullClassName.lastIndexOf(".");
        nextPos = fullClassName.lastIndexOf(".", nextPos - 1);
        nextPos = fullClassName.lastIndexOf(".", nextPos - 1);
        nextPos = fullClassName.lastIndexOf(".", nextPos - 1);
        return fullClassName.substring(0, nextPos);
    }


    //参数：包的名字
    //返回：包的路径
    public static String getClassPackagePath(String packageName) {
        return packageName.replace(".", "/");
    }


    //参数：类的全限定名
    //返回：该类的包名
    //列子：cn.com.workapp.carmgr.domain.model.handovercar.xxx
    public static String getClassPackageName(String fullClassName) {
        int proDotPos = fullClassName.lastIndexOf(".");
        return fullClassName.substring(0, proDotPos);

    }

    //参数：类的全限定名
    //返回：类的小名
    public static String getClassName(String fullClassName) {
        return fullClassName.substring(fullClassName.lastIndexOf(".") + 1);
    }

    //参数：hmb文件内容字符串
    //返回：类的权限定名
    public static String getClassFullName(String hbmContent) {
        int namePos = hbmContent.indexOf("name");
        int startQuotePos = hbmContent.indexOf("\"", namePos);
        int endQuotePos = hbmContent.indexOf("\"", startQuotePos + 1);

        return hbmContent.substring(startQuotePos + 1, endQuotePos);
    }

    //参数：文件完整路径
    //返回：文件字符串内容
    public static String getFileData(String filePath) throws IOException {
        File template = new File(filePath);
        FileReader fileReader = new FileReader(template);
        char[] content = new char[(int)template.length()];
        fileReader.read(content);
        return String.valueOf(content);
    }

    //参数：类的全限定名
    //返回：模块名称
    public static String getModuleName(String classFullName) {
        int endPos = classFullName.lastIndexOf(".");
        int startPos = classFullName.lastIndexOf(".", endPos - 1);
        return classFullName.substring(startPos + 1, endPos);
    }

    //参数:hbm文件路径
    //返回：动态property
    public static String getPropertyOfServiceImpl(String filePath) throws JDOMException, IOException {
        File file = new File(filePath);
        SAXBuilder saxBuilder = new SAXBuilder();
        saxBuilder.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
        Document doc = saxBuilder.build(file);
        Element root = doc.getRootElement();
        //获取propertyList
        Element classEle = root.getChild("class");
        List<Element> children = classEle.getChildren("property");
        List<Element> manyToOne = classEle.getChildren("many-to-one");
        StringBuilder propertyPart = new StringBuilder();
        importPart = new StringBuilder();

        for (Element child : children) {
            String name = child.getAttributeValue("name");
            String column = child.getAttributeValue("column");
            String type = child.getAttributeValue("type");

            //获得java类型
            type = hibernate2JavaMap.get(type);

            //如果有BigDecimal类型那么要加入到importPart中
            if (type.endsWith("BigDecimal")) importPart.append("import ").append(type).append(";\n");

            //获得简写类型
            type = getShortType(type);
            //貌似不需要存储
            ordinaryPropertyMap.put(name, type);
        }

        for (Element child : manyToOne) {
            String name = child.getAttributeValue("name");
            String column = child.getAttributeValue("column");
            String type = child.getAttributeValue("class");

            //如果与当前类是同包下可以省略import,这里就不处理
            importPart.append("import ").append(type).append(";\n");
            String repository = getRepositoryByORM(type);
            importPart.append("import ").append(repository).append(";\n");

            //获得简写类型
            type = getShortType(type);

            //保存many-to-one的Type与name，后续会用到
            manyToOnePropertyMap.put(name, type);

            propertyPart
                    .append("\t").append("@Autowired").append("\n")
                    .append("\t").append("private ").append(type).append("Repository").append(" ").append(getLowerClassName(type)).append("Repository").append(";\n\n");

        }

        return propertyPart.toString();
    }

    public static String getProperty(String filePath) throws JDOMException, IOException {
        File file = new File(filePath);
        SAXBuilder saxBuilder = new SAXBuilder();
        saxBuilder.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
        Document doc = saxBuilder.build(file);
        Element root = doc.getRootElement();
        //获取propertyList
        Element classEle = root.getChild("class");
        List<Element> children = classEle.getChildren("property");
        List<Element> manyToOne = classEle.getChildren("many-to-one");
        StringBuilder propertyPart = new StringBuilder();
        importPart = new StringBuilder();

        for (Element child : children) {
            String name = child.getAttributeValue("name");
            String column = child.getAttributeValue("column");
            String type = child.getAttributeValue("type");
            String notNull = "";
            String length = "";
            if ("true".equalsIgnoreCase(child.getAttributeValue("not-null"))) {
                notNull = ",nullable = false";
            }
            if (child.getAttributeValue("length") != null) {
                length = ", length = " + child.getAttributeValue("length");
            }
            //获得java类型
            type = hibernate2JavaMap.get(type);

            //如果有BigDecimal类型那么要加入到importPart中
            if (type.endsWith("BigDecimal")) importPart.append("import ").append(type).append(";\n");

            //获得简写类型
            type = getShortType(type);
            //貌似不需要存储


            ordinaryPropertyMap.put(name, type);
            propertyPart.append("\t@Column(name = \"").append(column).append("\"").append(notNull).append(length).append(")\n")
                    .append("\t").append("private ").append(type).append(" ").append(name).append(";\n\n");
        }

        for (Element child : manyToOne) {
            String name = child.getAttributeValue("name");
            String column = child.getAttributeValue("column");
            String type = child.getAttributeValue("class");

            //如果与当前类是同包下可以省略import,这里就不处理
            importPart.append("import ").append(type).append(";\n");


            //获得简写类型
            type = getShortType(type);

            //保存many-to-one的Type与name，后续会用到
            manyToOnePropertyMap.put(name, type);

            propertyPart.append("\t@JoinColumn(name = \"").append(column).append("\")\n")
                    .append("\t").append("@ManyToOne").append("\n")
                    .append("\t").append("private ").append(type).append(" ").append(name).append(";\n\n");


        }

        return propertyPart.toString();
    }



    private static String getRepositoryByORM(String type) {
        return getBasicPackageName(type) + ".persistence.jpa." + getModuleName(type) + "." + getShortType(type) + "Repository";
    }

    //参数：类的全限定名
    //返回：类的简写
    public static String getShortType(String type) {
        return getClassName(type);
    }

    public static String getImportType() {
        return importPart.toString();
    }

    //参数：类的简名
    //返回：类的默认构造器
    public static String getDefaultConstructor(String className) {
        StringBuilder sb = new StringBuilder();
        sb.append("\t").append("public ").append(className).append("()").append(" {")
                .append("\n\t").append("}");
        return sb.toString();
    }

    //参数：
    //返回：类的getter和setter方法
    public static String getGetterAndSetter() {
        StringBuilder ret = new StringBuilder();
        ordinaryPropertyMap.forEach((name, type)-> {
            createSetAndGetMethod(ret, type, name);
        });

        //many-to-one的setter and getter方法
        manyToOnePropertyMap.forEach((name, type) -> {
            createSetAndGetMethod(ret, type, name);
        });

        return ret.toString();
    }

    //参数：
    //返回：类的getter和setter方法
    public static String getGetterAndSetterOfForm() {
        StringBuilder ret = new StringBuilder();
        ordinaryPropertyMap.forEach((name, type)-> {
            createSetAndGetMethod(ret, type, name);
        });

        //many-to-one的setter and getter方法
        manyToOnePropertyMap.forEach((name, type) -> {
            createSetAndGetMethod(ret, Constant.Long, name + Constant.ID);
        });

        return ret.toString();
    }

    private static void createSetAndGetMethod(StringBuilder ret, String type, String name) {
        StringBuilder getter = new StringBuilder();
        getter.append("\n\t").append("public ").append(type).append(" get").append(getUpperName(name)).append("() {")
                .append("\n\t\t").append("return ").append(name).append(";").append("\n\t").append("}");

        StringBuilder setter = new StringBuilder();
        setter.append("\n\t").append("public ").append("void").append(" set").append(getUpperName(name)).append("(")
                .append(type).append(" ").append(name).append(") {").append("\n\t\t").append("this.").append(name).append(" = ")
                .append(name).append(";").append("\n\t").append("}");
        ret.append(getter).append(setter);
    }


    //参数：类名 form简名
    //返回：属性赋值表达式
    public static String getHaveFormArgsConstructor(String className,String formName) {
        //首先我要拼出一个form字符串，然后是many-to-one的形参
        StringBuilder ret = new StringBuilder();
        ret.append("\t").append("public ").append(className).append("(").append(formName).append(" ").append("form");
        spellDynamicArgsPart(ret);
        return ret.toString();
    }

    //功能：拼写 方法动态参数，及为类属性赋值
    private static void spellDynamicArgsPart(StringBuilder ret) {
        //拼写many-to-one动态参数部分
        manyToOnePropertyMap.forEach((name, type)-> {
            ret.append(", ").append(type).append(" ").append(name);
        });

        ret.append(")").append(" {");
        //将form中的内容都赋值到当前的属性中
        ordinaryPropertyMap.forEach((name, type) -> {
            ret.append("\n\t\t").append("this.").append(name).append(" = ").append("form").append(".")
                    .append("get").append(getUpperName(name)).append("();");
        });
        //将多对一字段赋值到当前类中
        manyToOnePropertyMap.forEach((name, type) -> {
            ret.append("\n\t\t").append("this.").append(name).append(" = ").append(name).append(";");
        });
        //封闭方法
        ret.append("\n\t}");
    }

    //参数：form简名
    //返回：orm中的update方法
    public static String getUpdateMethod(String formName) {
        StringBuilder ret = new StringBuilder();
        ret.append("\t").append("public ").append("void ").append("update(").append(formName).append(" ").append("form");
        spellDynamicArgsPart(ret);
        return ret.toString();
    }

    //参数：hbm内容字符串
    //返回：table名字
    public static String getTableName(String hbmContent) {
        int namePos = hbmContent.indexOf("table");
        int startQuotePos = hbmContent.indexOf("\"", namePos);
        int endQuotePos = hbmContent.indexOf("\"", startQuotePos + 1);

        return hbmContent.substring(startQuotePos + 1, endQuotePos);
    }

    public static String getPropertyOfForm(String filePath) throws JDOMException, IOException {

        File file = new File(filePath);
        SAXBuilder saxBuilder = new SAXBuilder();
        saxBuilder.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
        Document doc = saxBuilder.build(file);
        Element root = doc.getRootElement();
        //获取propertyList
        Element classEle = root.getChild("class");
        List<Element> children = classEle.getChildren("property");
        List<Element> manyToOne = classEle.getChildren("many-to-one");
        StringBuilder propertyPart = new StringBuilder();
        importPart = new StringBuilder();

        for (Element child : children) {
            String name = child.getAttributeValue("name");
            String column = child.getAttributeValue("column");
            String type = child.getAttributeValue("type");
            //获得java类型
            type = hibernate2JavaMap.get(type);

            //如果有BigDecimal类型那么要加入到importPart中
            if (type.endsWith(Constant.BIGDECIMAL)) importPart.append("import ").append(type).append(";\n");


            //获得简写类型
            type = getShortType(type);
            //貌似不需要存储


            ordinaryPropertyMap.put(name, type);
            //如果有日期类型的话，那么要加上注解
            if (type.equalsIgnoreCase(Constant.DATE)) {
                propertyPart.append("\t").append("@JsonFormat(pattern = \"yyyy-MM-dd\")\n");
            }
            propertyPart.append("\t").append("private ").append(type).append(" ").append(name).append(";\n\n");
        }

        for (Element child : manyToOne) {
            String name = child.getAttributeValue("name");
            String column = child.getAttributeValue("column");
            String type = child.getAttributeValue("class");

            //如果与当前类是同包下可以省略import,这里就不处理
            //importPart.append("import ").append(type).append(";\n");


            //获得简写类型
            type = getShortType(type);

            //保存many-to-one的Type与name，后续会用到
            manyToOnePropertyMap.put(name, type);

            propertyPart.append("\t").append("private ").append(Constant.Long).append(" ").append(name).append(Constant.ID).append(";\n\n");


        }

        return propertyPart.toString();

    }

    //参数：hbm文件内容
    //返回：属性字符串
    public static String getPropertyOfData(String filePath) throws JDOMException, IOException {
        File file = new File(filePath);
        SAXBuilder saxBuilder = new SAXBuilder();
        saxBuilder.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
        Document doc = saxBuilder.build(file);
        Element root = doc.getRootElement();
        //获取propertyList
        Element classEle = root.getChild("class");
        List<Element> children = classEle.getChildren("property");
        List<Element> manyToOne = classEle.getChildren("many-to-one");
        StringBuilder propertyPart = new StringBuilder();
        importPart = new StringBuilder();

        for (Element child : children) {
            String name = child.getAttributeValue("name");
            String column = child.getAttributeValue("column");
            String type = child.getAttributeValue("type");
            //获得java类型
            type = hibernate2JavaMap.get(type);

            //如果有BigDecimal类型那么要加入到importPart中
            if (type.endsWith("BigDecimal")) importPart.append("import ").append(type).append(";\n");

            //获得简写类型
            type = getShortType(type);
            //貌似不需要存储

//            System.out.println("=====================调试=======================");
//            System.out.println("type: " + type + ", name:" + name);

            ordinaryPropertyMap.put(name, type);
            //如果有日期类型的话，那么要加上注解
            if (type.equalsIgnoreCase(Constant.DATE)) {
                propertyPart.append("\t").append("@JsonFormat(pattern = \"yyyy-MM-dd\",timezone=\"GMT+8\")\n");
                importPart.append("import ").append("com.fasterxml.jackson.annotation.JsonFormat;\n");
            }
            propertyPart.append("\t").append("private ").append(type).append(" ").append(name).append(";\n\n");
        }

        for (Element child : manyToOne) {
            String name = child.getAttributeValue("name");
            String column = child.getAttributeValue("column");
            String type = child.getAttributeValue("class");

            //如果与当前类是同包下可以省略import,这里就不处理
            importPart.append("import ").append(type).append(";\n");


            //获得简写类型
            type = getShortType(type);

            //保存many-to-one的Type与name，后续会用到
            manyToOnePropertyMap.put(name, type);

            propertyPart.append("\t").append("private ").append(type).append(" ").append(name).append(";\n\n");


        }

        return propertyPart.toString();
    }

    public static String getPropertyAssign(String className) {

        //迭代orimap与manyto map
        //将form中的内容都赋值到当前的属性中
        StringBuilder ret = new StringBuilder();
        ordinaryPropertyMap.forEach((name, type) -> {
//            System.out.println("type :" + type + ", name :" + name);
            ret.append("\n\t\t").append("this.").append(name).append(" = ").append(getLowerClassName(className)).append(".")
                    .append("get").append(getUpperName(name)).append("();");
        });
        //将多对一字段赋值到当前类中
        manyToOnePropertyMap.forEach((name, type) -> {
            ret.append("\n\t\t").append("this.").append(name).append(" = ").append(getLowerClassName(className)).append(".").append("get").append(getUpperName(name)).append("()").append(";");
        });

        return ret.toString();
    }

    //获得service动态方法
    public static String getManyToOneServiceMethod(String className) {
        //根据manyMap来决定这一组方法的数量
        //List<@CLASS_NAME@Data> find@CLASS_NAME@ListBy@MANY_TYPE@Id(long @MANY_TYPE@Id);
        //PageData<@CLASS_NAME@Data> find@CLASS_NAME@PageBy@MANY_TYPE@Id(long @MANY_TYPE@Id, Pageable pageable);

        StringBuilder serviceMethod = new StringBuilder();
        manyToOnePropertyMap.forEach((name, type) -> {
            serviceMethod.append("\tList<").append(className).append("Data>")
                    .append(" find").append(className).append("ListBy").append(type).append("Id")
                    .append("(").append("long").append(" ").append(getLowerClassName(type)).append("Id").append(");\n");

            serviceMethod.append("\n\tPageData<").append(className).append("Data>").append(" ").append("find").append(className)
                    .append("PageBy").append(type).append("Id").append("(long ").append(getLowerClassName(type)).append("Id, Pageable pageable);\n");
        });

        return serviceMethod.toString();

    }
}
