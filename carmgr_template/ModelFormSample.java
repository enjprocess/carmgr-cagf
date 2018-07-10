package @PACKAGE_NAME@;

import com.fasterxml.jackson.annotation.JsonFormat;

@IMPORT_TYPE@

import java.util.*;

public class @CLASS_NAME@Form {

    private Long id;//编号

@PROPERTY@


@DEFAULT_CONSTRUCTOR@

@GETTER_AND_SETTER@

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}