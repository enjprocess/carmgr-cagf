package com.cagf.tool.hbm2serviceimpl;

import com.cagf.tool.util.Constant;
import com.cagf.tool.util.FileUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import javax.persistence.Id;
import javax.servlet.jsp.tagext.PageData;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

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
    public void generateServiceImpl(String outputDir, List<String> hbmList, String templateFile) throws IOException, JDOMException {
        int i = 0;
        for (; i < hbmList.size(); i++) {
            FileUtils.manyToOnePropertyMap.clear();
            FileUtils.ordinaryPropertyMap.clear();
            //设置基本数据
            setBasicData(hbmList, i);
            //读取模板文件内容
            String templateFileContent = FileUtils.getFileData(templateFile);
            //创造出orm对象
            String newOrm = doReplace(templateFileContent, hbmList.get(i));

            //写到磁盘
            javaFileDiskPath = outputDir + "/" + getPackagePath(getPackageName()) + "/" + className + "ServiceImpl" + ".java";
            fileParentDir = outputDir + "/" + getPackagePath(getPackageName());
            writeToDisk(fileParentDir, javaFileDiskPath, newOrm);

        }
        System.out.println("generate " + i + " serviceImpl files");
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
        return templateContent
                .replace(REPOSITORY_CLASS_NAME, getRepositoryClassFullName())
                .replace(SERVICE_FULL_NAME, getServiceFullName())
                .replace(PACKAGE_NAME, getPackageName())
                .replace(CLASS_DATA_FULL_NAME, getClassDataFullName())
                .replace(CLASS_FORM_FULL_NAME, getClassFormFullName())
                .replace(CLASS_FULL_NAME, classFullName)
                .replace(PROPERTY, getProperty(hbmPath))
                .replace(IMPORT_TYPE, FileUtils.getImportType())
                .replace(CLASS_NAME, className)
                .replace(MANY_TO_ONE_REPOSITORY_QUERY, getManyToOneRepositoryQuery())
                .replace(REPOSITORY_SAVE_OPERATOR, getRepositorySaveOperator())
                .replace(ORM_UPDATE_OPERATOR, getOrmUpdateOperator())
                .replace(MANY_TO_ONE_SERVICE_IMPL_METHOD, getManyToOneServiceImplMethod())
                .replace(CLASS_LOWER_NAME, FileUtils.getLowerClassName(className))
                .replace(CLASS_FULL_NAME, classFullName).trim();




    }

    //一端进行查询
    private String getManyToOneServiceImplMethod() {



       StringBuilder ret = new StringBuilder();
       //未分页的
        setManyQuery(ret);

        //分页的
        setManyPageQuery(ret);
       return ret.toString();
    }

    private void setManyQuery(StringBuilder ret) {
         /* public List<@CLASSNAME@> find@CLASSNAME@ListBy@MANY_TYPE@Id(long @many_type@Id) {
            List<@CLASSNAME@> @classname@List = @classname@Repository.findAll((r, q, cb) -> {
                Predicate p = cb.and(cb.equal(r.get("@many_type@").get("id"), @many_type@Id), cb.equal(r.get("deleted"),false));
                q.where(p);
                q.orderBy(cb.desc(r.get("id")));
                return q.getRestriction();
            });

            if (@classname@List == null) {
                throw new CodeDefinedException(ExceptionCode.EX_NOT_FOUND_ERROR);
            }


            return @classname@List.stream().map(p -> new @CLASS_NAME@Data(p)).collect(Collectors.toList());
        }*/
        FileUtils.manyToOnePropertyMap.forEach((name, type) -> {
            String shortType = FileUtils.getShortType(type);
            String lowType = FileUtils.getLowerClassName(shortType);
            ret.append("\n\t").append("@Override").append("\n")
                    .append("\t").append("public List<").append(className).append("Data>").append(" find")
                    .append(className).append("ListBy").append(shortType).append("Id").append("(long ")
                    .append(lowType).append("Id").append(") {")
                    .append("\n\t\t")
                    .append("List<").append(className).append(">").append(" ").append(FileUtils.getLowerClassName(className))
                    .append("List").append(" = ").append(FileUtils.getLowerClassName(className)).append("Repository").append(".findAll")
                    .append("((r, q, cb)").append(" -> ").append(" {")
                    .append("\n\t\t\t")
                    .append("Predicate p").append(" = ").append("cb.and(cb.equal(r.get(\"").append(lowType).append("\"").append(")")
                    .append(".get(\"id\")").append(", ").append(lowType).append("Id").append("), ").append("cb.equal(r.get(\"deleted\"), false));")
                    .append("\n\t\t\t")
                    .append("q.where(p);").append("\n\t\t\t").append("q.orderBy(cb.desc(r.get(\"id\")));")
                    .append("\n\t\t\t").append("return q.getRestriction();").append("\n\t\t});")
                    .append("\n\t\t")
                    .append("if (").append(FileUtils.getLowerClassName(className)).append("List == null) {").append("\n\t\t\t")
                    .append("throw new CodeDefinedException(ExceptionCode.EX_NOT_FOUND_ERROR);").append("\n\t\t}").append("\n\t\t")
                    .append("return ").append(FileUtils.getLowerClassName(className)).append("List").append(".").append("stream().map(p -> new ")
                    .append(className).append("Data").append("(p))").append(".").append("collect").append("(Collectors.toList());")
                    .append("\n\t}");

        });
    }

    private void setManyPageQuery(StringBuilder ret) {
        /*@Override
        public PageData<@CLASSNAME@Data> find@CLASSNAME@PageBy@MANY_TYPE@Id(long @many_type@Id, Pageable pageable) {
            Page<@CLASSNAME@> @CLASSNAME@Page = @classname@Repository.findAll((r, q, cb) -> {
                Predicate p = cb.and(cb.equal(r.get("@many_type@").get("id"), @many_type@Id), cb.equal(r.get("deleted"),false));
                q.where(p);
                q.orderBy(cb.desc(r.get("id")));
                return q.getRestriction();
            }, pageable);


            if (@classname@Page == null) {
                throw new CodeDefinedException(ExceptionCode.EX_NOT_FOUND_ERROR);
            }

            return new PageData(@classname@Page.map(p -> new @CLASSNAME@Data(p)));
        }*/

        FileUtils.manyToOnePropertyMap.forEach((name, type) -> {
            String shortType = FileUtils.getShortType(type);
            String lowType = FileUtils.getLowerClassName(shortType);
            ret.append("\n\t").append("@Override").append("\n")
                    .append("\t").append("public PageData<").append(className).append("Data>").append(" find")
                    .append(className).append("PageBy").append(shortType).append("Id").append("(long ")
                    .append(lowType).append("Id").append(", Pageable pageable").append(") {")
                    .append("\n\t\t")
                    .append("Page<").append(className).append(">").append(" ").append(FileUtils.getLowerClassName(className))
                    .append("Page").append(" = ").append(FileUtils.getLowerClassName(className)).append("Repository").append(".findAll")
                    .append("((r, q, cb)").append(" -> ").append(" {")
                    .append("\n\t\t\t")
                    .append("Predicate p").append(" = ").append("cb.and(cb.equal(r.get(\"").append(lowType).append("\"").append(")")
                    .append(".get(\"id\")").append(", ").append(lowType).append("Id").append("), ").append("cb.equal(r.get(\"deleted\"), false));")
                    .append("\n\t\t\t")
                    .append("q.where(p);").append("\n\t\t\t").append("q.orderBy(cb.desc(r.get(\"id\")));")
                    .append("\n\t\t\t").append("return q.getRestriction();").append("\n\t\t}, pageable);")
                    .append("\n\t\t")
                    .append("if (").append(FileUtils.getLowerClassName(className)).append("Page").append(" == null) {").append("\n\t\t\t")
                    .append("throw new CodeDefinedException(ExceptionCode.EX_NOT_FOUND_ERROR);").append("\n\t\t}").append("\n\t\t")
                    .append("return ").append("new PageData(").append(FileUtils.getLowerClassName(className)).append("Page").append(".").append("map(p -> new ")
                    .append(className).append("Data").append("(p)));")
                    .append("\n\t}");

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
        FileUtils.manyToOnePropertyMap.forEach((name, type) -> {
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
        FileUtils.manyToOnePropertyMap.forEach((name, type) -> {
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
        FileUtils.manyToOnePropertyMap.forEach((name, type) -> {
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
        return FileUtils.getPropertyOfServiceImpl(hbmPath);
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
        return FileUtils.getBasicPackageName(classFullName) + ".application.impl";
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
