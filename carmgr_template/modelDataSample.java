package @PACKAGE_NAME@;

import @CLASS_FULL_NAME@;
@IMPORT_TYPE@

import java.util.*;

public class @CLASS_NAME@Data {

    private Long id;//编号
@PROPERTY@



    public @CLASS_NAME@Data() {
    }

@HAVE_CLASS_ARG_CONSTRUCTOR@

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

@GETTER_AND_SETTER@

}
