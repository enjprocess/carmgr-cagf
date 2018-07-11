package com.cagf.tool.hbm2form;

import com.cagf.tool.util.FileUtils;
import org.jdom.JDOMException;

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

    private  Helper() {
    }

    public static Helper getInstance() {
        return helper;
    }

    //根据hbm生成ORM
    public void generateForm(String outputDir, List<String> hbmList, String templateFile) throws IOException, JDOMException {
        int i = 0;
        for (; i < hbmList.size(); i++) {
            FileUtils.manyToOnePropertyMap.clear();
            FileUtils.ordinaryPropertyMap.clear();
            //设置基本数据
            setBasicData(hbmList, i);
            //读取模板文件内容
            String templateFileContent = FileUtils.getFileData(templateFile);
            //创造出orm对象
            String newOrm = doReplace(templateFileContent);

            //写到磁盘
            javaFileDiskPath = outputDir + "/" + classPackagePath + "/" + className + FORM + ".java";
            fileParentDir = outputDir + "/" + classPackagePath;
            writeToDisk(fileParentDir, javaFileDiskPath, newOrm);

        }

        System.out.println("generate " + i + " form files");
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
    private String doReplace(String templateContent) {
        return templateContent.replace(PACKAGE_NAME, classPackageName)
                .replace(CLASS_NAME, className)
                .replace(PROPERTY, property)
                .replace(IMPORT_TYPE, importType)
                .replace(DEFAULT_CONSTRUCTOR, defaultConstructor)
                .replace(GETTER_AND_SETTER, getterAndSetter).trim();

    }

    //设置基本数据，为之后处理做准备
    private void setBasicData(List<String> hbmList, int i) throws IOException, JDOMException {
        //得到hbm文件的内容
        hbmContent = FileUtils.getFileData(hbmList.get(i));
        //得到ORM类的全限定名
        classFullName = FileUtils.getClassFullName(hbmContent);


        //得到Orm简名
        className = FileUtils.getClassName(classFullName);
        //得到首字母小写的Orm简名
        lowerClassName = FileUtils.getLowerClassName(className);
        //得到模块名字
        moduleName = FileUtils.getModuleName(classFullName);

        //得到基础包名
        basicPackageName = FileUtils.getBasicPackageName(classFullName);

        //得到ORM form名字
        classFormFullName = basicPackageName + DOT + APPLICATION + DOT + QUERY + DOT + moduleName + DOT
                + className + FORM;

        //得到FORM类的包名
        classPackageName = FileUtils.getClassPackageName(classFormFullName);

        //得到FORM类的包名的物理路径
        classPackagePath = FileUtils.getClassPackagePath(classPackageName);

        //得到table_name
        tableName = FileUtils.getTableName(hbmContent);

        property = FileUtils.getPropertyOfForm(hbmList.get(i));
        importType = FileUtils.getImportType();
        defaultConstructor = FileUtils.getDefaultConstructor(FileUtils.getShortType(classFormFullName));

        getterAndSetter = FileUtils.getGetterAndSetterOfForm();
    }
}
