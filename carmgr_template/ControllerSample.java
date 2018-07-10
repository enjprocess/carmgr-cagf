package cn.com.workapp.carmgr.web.restful.handovercar;

import cn.com.workapp.carmgr.application.@CLASSNAME@Service;
import cn.com.workapp.carmgr.application.data.PageData;
import cn.com.workapp.carmgr.application.data.handovercar.@CLASSNAME@Data;
import cn.com.workapp.carmgr.application.query.handovercar.@CLASSNAME@Form;
import cn.com.workapp.carmgr.web.restful.QueryParam;
import cn.com.workapp.carmgr.web.restful.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RequestMapping("/@classname@")
@RestController
public class @CLASSNAME@Controller {


    @Autowired
    private @CLASSNAME@Service @classname@Service;

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public Result add@CLASSNAME@(@RequestBody @CLASSNAME@Form form) {
        return Result.success(@classname@Service.add@CLASSNAME@(form));
    }

    @RequestMapping(value = "/delete/{id}", method = RequestMethod.POST)
    public Result del@CLASSNAME@(@PathVariable long id) {
        @classname@Service.del@CLASSNAME@(id);
        return Result.success();
    }

    @RequestMapping(value = "/update/{id}", method = RequestMethod.POST)
    public Result update@CLASSNAME@(@PathVariable long id, @RequestBody @CLASSNAME@Form form) {
        @classname@Service.update@CLASSNAME@(id, form);
        return Result.success();
    }

    @RequestMapping(value = "/query/{id}", method = RequestMethod.GET)
    public Result findOne@CLASSNAME@ById(@PathVariable long id) {
        @CLASSNAME@Data @classname@Data = @classname@Service.findOne@CLASSNAME@ById(id);
        return Result.success(@classname@Data);
    }

    @RequestMapping(value = "/queryList/{@many_to_one_property@}", method = RequestMethod.GET)
    public Result find@CLASSNAME@ListBy@MANY_TO_ONE_PROPERTY@(@PathVariable  long @many_to_one_property@) {
        List<@CLASSNAME@Data> ret = @classname@Service.find@CLASSNAME@ListBy@MANY_TO_ONE_PROPERTY@(@many_to_one_property@);
        return Result.success(ret);
    }

    @RequestMapping(value = "/queryPage/{@many_to_one_property@}", method = RequestMethod.GET)
    public Result find@CLASSNAME@PageBy@MANY_TO_ONE_PROPERTY@(@PathVariable long @many_to_one_property@, QueryParam queryParam) {
        PageData<@CLASSNAME@Data> ret = @classname@Service.find@CLASSNAME@PageBy@MANY_TO_ONE_PROPERTY@(@many_to_one_property@, queryParam.getPageable());
        return Result.success(ret);
    }
}
