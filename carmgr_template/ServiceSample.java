package @PACKAGE_NAME@;

import cn.com.workapp.carmgr.application.data.PageData;

import @CLASS_DATA_FULL_NAME@;
import @CLASS_FORM_FULL_NAME@;

import org.springframework.data.domain.Pageable;


import java.util.*;

public interface @CLASS_NAME@Service {

    long add@CLASS_NAME@(@CLASS_NAME@Form form);

    void del@CLASS_NAME@(long id);

    void update@CLASS_NAME@(long id, @CLASS_NAME@Form form);

    @CLASS_NAME@Data findOne@CLASS_NAME@ById(long id);
    
@MANY_TO_ONE_SERVICE_METHOD@
}
