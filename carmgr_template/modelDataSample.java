package @PACKAGE_NAME@;

import @ORM_CLASS_NAME@;


@IMPORT_TYPE@

import java.util.*;

public class @CLASS_NAME@Data {

    private Long id;//编号
@PROPERTY@



    public @CLASS_NAME@Data() {
    }

    public @CLASS_NAME@Data(@CLASS_NAME@ @classname@) {
        this.id = @classname@.id();
@PROPERTY_ASSIGN@

    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

@GETTER_AND_SETTER@

}
