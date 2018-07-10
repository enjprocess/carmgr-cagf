package @PACKAGE_NAME@;

import @REPOSITORY_CLASS_NAME@;
import @SERVICE_FULL_NAME@;
import @CLASS_DATA_FULL_NAME@;
import @CLASS_FORM_FULL_NAME@;
import @CLASS_FULL_NAME@;



@IMPORT_TYPE@

import cn.com.workapp.carmgr.application.@CLASS_NAME@Service;
import cn.com.workapp.carmgr.application.data.PageData;
import cn.com.workapp.carmgr.persistence.jpa.BaseRepository;
import cn.com.workapp.carmgr.util.exception.CodeDefinedException;
import cn.com.workapp.carmgr.util.exception.ExceptionCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.Predicate;
import java.util.*;
import java.util.stream.Collectors;



@Transactional
@Service
public class @CLASS_NAME@ServiceImpl implements @CLASS_NAME@Service {

    @Autowired
    private @CLASS_NAME@Repository @classname@Repository;

@PROPERTY@

    @Override
    public long add@CLASS_NAME@(@CLASS_NAME@Form form) {
@MANY_TO_ONE_REPOSITORY_QUERY@
@REPOSITORY_SAVE_OPERATOR@
    }

    @Override
    public void del@CLASS_NAME@(long id) {
        List<@CLASS_NAME@> @classname@List = checkData(id, @classname@Repository);

        @CLASS_NAME@ @classname@ = @classname@List.get(0);
        @classname@.delete();
    }

    @SuppressWarnings("unchecked")
    private <T> List<T> checkData(long id, BaseRepository br) {

        List<T> list = null;
        try {
            list = br.findAll((r, q, cb) -> {
                Predicate p = cb.equal(r.get("id"), id);
                q.where(p);
                return q.getRestriction();
            });

        } catch (Exception e) {
            throw new CodeDefinedException(ExceptionCode.EX_NOT_FOUND_ERROR);
        }

        if (list.size() > 1) {
            throw new CodeDefinedException(ExceptionCode.EX_REPEAT_ERROR);
        }
        return list;
    }

    @Override
    public void update@CLASS_NAME@(long id, @CLASS_NAME@Form form) {
        List<@CLASS_NAME@> @classname@List = checkData(id, @classname@Repository);

        @CLASS_NAME@ @classname@ = @classname@List.get(0);

@ORM_UPDATE_OPERATOR@
    }

    @Override
    public @CLASS_NAME@Data findOne@CLASS_NAME@ById(long id) {
        List<@CLASS_NAME@> @classname@List = checkData(id, @classname@Repository);
        return new @CLASS_NAME@Data(@classname@List.get(0));
    }
    
@MANY_TO_ONE_SERVICE_IMPL_METHOD@
}
