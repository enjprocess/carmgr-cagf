package com.cagf.tool.hbm2orm;

import com.cagf.tool.util.Constant;
import org.jdom.JDOMException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.cagf.tool.util.Constant.*;

public class GenerateOrmCode {

    private static List<String> hbmList = new ArrayList<>();

    private static String outputDir = null;

    private static String templateFile = null;

    public static void main(String[] args) throws IOException, JDOMException {

        //将hbm字符串存入hbmList
        for (String arg : args) {
            if (arg.startsWith(OUT_PUT_DIR)) {
                outputDir = arg.substring(OUT_PUT_DIR.length());
            } else if(arg.startsWith(TEMPLATE_FILE)) {
                templateFile = arg.substring(TEMPLATE_FILE.length());
            }
            else {
                hbmList.add(arg);
            }
        }

        Helper.getInstance().generateORM(outputDir, hbmList,templateFile);
    }
}
