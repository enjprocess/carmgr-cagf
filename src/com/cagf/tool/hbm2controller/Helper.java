package com.cagf.tool.hbm2controller;

import com.cagf.tool.util.FileUtils;
import org.jdom.JDOMException;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import static com.cagf.tool.util.Constant.*;

public class Helper {

    public static final Helper helper = new Helper();
    private String hbmContent;
    private String lowerClassName;
    private String classPackageName;
    private String classPackagePath;
    private String className;
    private String classFullName;
    private String moduleName;
    private String classFormFullName;
    private String property;
    private String importType;
    private String defaultConstructor;
    private String haveFormArgsConstructor;
    private String getterAndSetter;
    private String updateMethod;
    private String javaFileDiskPath;
    private String fileParentDir;
    private String tableName;
    private String basicPackageName;
    private String propertyAssign;
    private String classDataFullName;
    private String classFormFullName1;
    private String manyToOneServiceMethod;

    private  Helper() {
    }

    public static Helper getInstance() {
        return helper;
    }

    //根据hbm生成ORM
    public void generateController(String outputDir, List<String> hbmList, String templateFile) throws IOException, JDOMException {
        int i = 0;
        for (; i < hbmList.size(); i++) {
            //设置基本数据
            setBasicData(hbmList, i);
            //读取模板文件内容
            String templateFileContent = FileUtils.getFileData(templateFile);
            //创造出orm对象
            String newOrm = doReplace(templateFileContent, hbmList.get(i));

            //写到磁盘
            javaFileDiskPath = outputDir + "/" + getPackagePath(getPackageName()) + "/" + className + "Controller" + ".java";
            fileParentDir = outputDir + "/" + getPackagePath(getPackageName());
            writeToDisk(fileParentDir, javaFileDiskPath, newOrm);

        }
        System.out.println("generate " + i + " controller files");
    }

    private void writeToDisk(String fileParentDir, String javaFileDiskPath, String ormContent) throws IOException {
        //先创建目录
        File file = new File(fileParentDir);
        file.mkdirs();
        FileWriter out = new FileWriter(new File(javaFileDiskPath));
        out.write(ormContent);
        out.close();
    }

/*

        @IMPORT_TYPE@ 通过ordinaryPropertyMap获得，如果有多对一的话，那么要拼private Long @classname@Id;
        @CLASS_NAME@ 通过CLASS_NAME获得
        @PROPERTY@ 通过ordinaryPropertyMap获得
        @DEFAULT_CONSTRUCTOR@ 通过getDefaultConstructor获得
        @GETTER_AND_SETTER@ getGetterAndSetter方法需要改造下
*
*
* */
    private String doReplace(String templateContent, String hbmPath) throws JDOMException, IOException {

        //设置manyMap;
        FileUtils.getProperty(hbmPath);

        return templateContent
                .replace(PACKAGE_NAME, getPackageName())
                .replace(CLASS_NAME, className)
                .replace(CLASS_DATA_FULL_NAME, getClassDataFullName())
                .replace(CLASS_FORM_FULL_NAME, getClassFormFullName())
                .replace(CLASS_LOWER_NAME, FileUtils.getLowerClassName(className))
                .replace(MANY_TO_ONE_CONTROLLER_METHOD, getManyToOneControllerMethod())
                .trim();







    }

    private CharSequence getManyToOneControllerMethod() {

        StringBuilder ret = new StringBuilder();
        //未分页的
        setManyQuery(ret);

        //分页的
        setManyPageQuery(ret);
        return ret.toString();
    }


    private void setManyQuery(StringBuilder ret) {
/*
        @RequestMapping(value = "/queryList/{repaymentPlanId}", method = RequestMethod.GET)
        public Result findRepaymentPlanDetListByRepaymentPlanId(@PathVariable long repaymentPlanId) {
            List<RepaymentPlanDetData> ret = repaymentPlanDetService.findRepaymentPlanDetListByRepaymentPlanId(repaymentPlanId);
            return Result.success(ret);
        }
*/
        FileUtils.manyToOnePropertyMap.forEach((type, name) -> {
            String shortType = FileUtils.getShortType(type);
            String lowType = FileUtils.getLowerClassName(shortType);
            ret.append("\t@RequestMapping(value = \"/queryList/{").append(lowType).append("Id").append("}\", method = RequestMethod.GET)")
                    .append("\n\t").append("public Result findRepaymentPlanDetListByRepaymentPlanId(@PathVariable long ").append(lowType).append("Id) {")
                    .append("\n\t\t").append("List<").append(className).append("Data").append("> ret = ").append(FileUtils.getLowerClassName(className))
                    .append("Service").append(".find").append(className).append("ListBy").append(shortType).append("Id(").append(lowType).append("Id);")
                    .append("\n\t\t").append("return Result.success(ret);\n\t").append("}\n");

        });
    }

    private void setManyPageQuery(StringBuilder ret) {
        /*@RequestMapping(value = "/queryPage/{repaymentPlanId}", method = RequestMethod.GET)
        public Result findRepaymentPlanDetPageByRepaymentPlanId(@PathVariable long repaymentPlanId, QueryParam queryParam) {
            PageData<RepaymentPlanDetData> ret = repaymentPlanDetService.findRepaymentPlanDetPageByRepaymentPlanId(repaymentPlanId, queryParam.getPageable());
            return Result.success(ret);
        }*/

        FileUtils.manyToOnePropertyMap.forEach((type, name) -> {
            String shortType = FileUtils.getShortType(type);
            String lowType = FileUtils.getLowerClassName(shortType);
            ret.append("\t@RequestMapping(value = \"/queryPage/{").append(lowType).append("Id").append("}\"").append(", method = ")
                    .append("RequestMethod.GET)").append("\n\t").append("public Result find").append(className)
                    .append("PageBy").append(shortType).append("Id(").append("@PathVariable long ").append(lowType).append("Id")
                    .append(", QueryParam queryParam").append(") {")
                    .append("\n\t\t").append("PageData<").append(className).append("Data").append(">").append(" ret = ").append(FileUtils.getLowerClassName(className))
                    .append("Service").append(".").append("find").append(className).append("PageBy").append(shortType).append("Id(")
                    .append(lowType).append("Id").append(", queryParam.getPageable());").append("\n\t\t").append("return Result.success(ret);\n\t}\n");

        });
    }


    //serviceImpl update操作语句
    private String getOrmUpdateOperator() {
        //repaymentPlanDet.update(form, repaymentPlanDet.getRepaymentPlan());
        //List<%MANY_TYPE%> Low(MANY_TYPE)= checkData(form.get@MANY_TYPE@Id(), low(@many_type@)Repository);
        //@classname@.update(form, @low(many_type)@.get(0) ... ... 动态);
        StringBuilder sb = new StringBuilder();
        StringBuilder subUpdateStatement = new StringBuilder();
        StringBuilder updateStatement = new StringBuilder();
        FileUtils.manyToOnePropertyMap.forEach((type, name) -> {
            String shortType = FileUtils.getShortType(type);
            String lowType = FileUtils.getLowerClassName(shortType);
            sb.append("\t\tList<").append(shortType).append(">").append(" ").append(lowType).append("List")
                    .append(" = ").append("checkData").append("(form").append(".get").append(shortType).append("Id()").append(", ")
                    .append(lowType).append("Repository);\n");
            subUpdateStatement.append(", ").append(lowType).append("List").append(".").append("get(0)");
        });
        updateStatement.append("\t\t").append(FileUtils.getLowerClassName(className)).append(".").append("update").append("(form").append(subUpdateStatement).append(");\n");
        return sb.append(updateStatement).toString();
    }

    //获得serviceImpl进行save操作语句
    private String getRepositorySaveOperator() {
        StringBuilder sb = new StringBuilder();
        sb.append("\t\treturn ").append(FileUtils.getLowerClassName(className)).append("Repository").append(".").append("save(")
                .append("new ").append(className).append("(form");

        //动态部分
        FileUtils.manyToOnePropertyMap.forEach((type, name) -> {
            String shortType = FileUtils.getShortType(type);
            String lowType = FileUtils.getLowerClassName(shortType);
            sb.append(", ").append(lowType).append("List").append(".get(0)");
        });
        //收尾
        sb.append(")).").append("id();");

        return sb.toString();
    }

    //对一端对象进行查询
    private String getManyToOneRepositoryQuery() {
        StringBuilder sb = new StringBuilder();
        FileUtils.manyToOnePropertyMap.forEach((type, name) -> {
            String shortType = FileUtils.getShortType(type);
            String lowType = FileUtils.getLowerClassName(shortType);
            sb.append("\t\tList<").append(shortType).append(">")
                    .append(" ").append(lowType).append("List").append(" = ").append("checkData(")
                    .append("form.get").append(shortType).append("Id").append("()")
                    .append(", ").append(lowType).append("Repository").append(");\n");
        });
        return sb.toString();
    }

    //得到一端的Repository
    private String getProperty(String hbmPath) throws JDOMException, IOException {
        return FileUtils.getProperty(hbmPath);
    }


    //得到当前类的Repository
    private String getRepositoryClassFullName() {
        return FileUtils.getBasicPackageName(classFullName) + ".persistence.jpa." + moduleName + "." + className + "Repository";
    }

    //得到Form全限定名
    private String getClassFormFullName() {
        return FileUtils.getBasicPackageName(classFullName) + ".application.query." + moduleName + "." + className + "Form";
    }

    //得到Data全限定名
    private String getClassDataFullName() {
        return FileUtils.getBasicPackageName(classFullName) + ".application.data." + moduleName + "." + className + "Data";
    }


    //得到包名
    private String getPackageName() {
        return FileUtils.getBasicPackageName(classFullName) + ".web.restful." + moduleName;
    }


    private String getPackagePath(String packageName) {
        return packageName.replace(".", "/");
    }

    //得到service全限定名
    private String getServiceFullName() {
        String basicPackageName = FileUtils.getBasicPackageName(classFullName);
        return basicPackageName + ".application" + "." + className + "Service";
    }

    //设置基本数据，为之后处理做准备
    private void setBasicData(List<String> hbmList, int i) throws IOException, JDOMException {
        //得到hbm文件的内容
        hbmContent = FileUtils.getFileData(hbmList.get(i));
        //得到ORM类的全限定名
        classFullName = FileUtils.getClassFullName(hbmContent);

        //得到模块名字
        moduleName = FileUtils.getModuleName(classFullName);

        //得到Orm简名
        className = FileUtils.getClassName(classFullName);
        //得到首字母小写的Orm简名
        lowerClassName = FileUtils.getLowerClassName(className);

    }
}
