package @PACKAGE_NAME@;

import @CLASS_FORM_FULL_NAME@;
import cn.com.workapp.carmgr.domain.model.BaseTrackingEntity;

import javax.persistence.*;
import java.util.*;

@IMPORT_TYPE@

@Entity
@Table(name = "@TABLE_NAME@")
public class @CLASS_NAME@ extends BaseTrackingEntity {

@PROPERTY@

@DEFAULT_CONSTRUCTOR@

@HAVE_FORM_ARGS_CONSTRUCTOR@
    
@GETTER_AND_SETTER@

@UPDATE_METHOD@
}
