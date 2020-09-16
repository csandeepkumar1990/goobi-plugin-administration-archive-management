package de.intranda.goobi.plugins.model;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import lombok.Data;

@Data

public class EadMetadataField {

    /** contains the internal name of the field. The value can be used to translate the field in the messages files */
    private String name;

    /**
     * metadata level, allowed values are 1-7:
     * <ul>
     * <li>1: metadata for Identity Statement Area</li>
     * <li>2: Context Area</li>
     * <li>3: Content and Structure Area</li>
     * <li>4: Condition of Access and Use Area</li>
     * <li>5: Allied Materials Area</li>
     * <li>6: Note Area</li>
     * <li>7: Description Control Area</li>
     * </ul>
     */
    private Integer level;

    /** contains a relative path to the ead value. The root of the xpath is either the {@code<ead>} element or the {@code<c>} element */
    private String xpath;

    /** type of the xpath return value, can be text, attribute, element (default) */
    private String xpathType;

    /** defines if the field can exist once or multiple times, values can be true/false, default is false */
    private boolean repeatable;
    /** contains the metadata value */
    private String value;
    /** defines if the field is displayed on the UI, values can be true/false, default is true */
    private boolean visible;
    /** defines if the field is displayed as input field (true) or badge (false, default), affects only visible metadata */
    private boolean showField;

    /** defines the type of the input field. Posible values are input (default), textarea, dropdown, multiselect */
    private String fieldType;

    private List<String> selectItemList;

    public EadMetadataField(String name, Integer level, String xpath, String xpathType, boolean repeatable, boolean visible, boolean showField,
            String fieldType) {
        this.name = name;
        this.level = level;
        this.xpath = xpath;
        this.xpathType = xpathType;
        this.repeatable = repeatable;
        this.visible = visible;
        this.showField = showField;
        this.fieldType = fieldType;
    }

    public boolean isFilled() {
        return StringUtils.isNotBlank(value);
    }
}
