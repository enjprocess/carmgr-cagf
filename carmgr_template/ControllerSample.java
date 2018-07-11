package @PACKAGE_NAME@;

import cn.com.workapp.carmgr.application.@CLASS_NAME@Service;
import cn.com.workapp.carmgr.application.data.PageData;
import @CLASS_DATA_FULL_NAME@;
import @CLASS_FORM_FULL_NAME@;
import cn.com.workapp.carmgr.web.restful.QueryParam;
import cn.com.workapp.carmgr.web.restful.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RequestMapping("/@classname@")
@RestController
public class @CLASS_NAME@Controller {


    @Autowired
    private @CLASS_NAME@Service @classname@Service;

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public Result add@CLASS_NAME@(@RequestBody @CLASS_NAME@Form form) {
        return Result.success(@classname@Service.add@CLASS_NAME@(form));
    }

    @RequestMapping(value = "/delete/{id}", method = RequestMethod.POST)
    public Result del@CLASS_NAME@(@PathVariable long id) {
        @classname@Service.del@CLASS_NAME@(id);
        return Result.success();
    }

    @RequestMapping(value = "/update/{id}", method = RequestMethod.POST)
    public Result update@CLASS_NAME@(@PathVariable long id, @RequestBody @CLASS_NAME@Form form) {
        @classname@Service.update@CLASS_NAME@(id, form);
        return Result.success();
    }

    @RequestMapping(value = "/query/{id}", method = RequestMethod.GET)
    public Result findOne@CLASS_NAME@ById(@PathVariable long id) {
        @CLASS_NAME@Data @classname@Data = @classname@Service.findOne@CLASS_NAME@ById(id);
        return Result.success(@classname@Data);
    }

@MANY_TO_ONE_CONTROLLER_METHOD@


}
