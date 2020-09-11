package de.intranda.goobi.plugins.model;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import lombok.Data;

@Data
public class EadEntry {

    // parent node
    private EadEntry parentNode;

    // list contains all child elements
    private List<EadEntry> subEntryList = new ArrayList<>();

    // order number of the current element within the current hierarchy
    private Integer orderNumber;

    // hierarchy level
    private Integer hierarchy;

    private String id; // c@id

    // display label
    private String label;
    // node is open/closed
    private boolean displayChildren;
    // node is selected
    private boolean selected;

    // node type -  @level
    private String nodeType;

    /* 1. metadata for Identity Statement Area */
    //    Reference code(s)
    //    Title
    //    private String unittitle; // did/unittitle
    //    Date(s)
    //    Level of description
    //    Extent and medium of the unit of description (quantity, bulk, or size)
    private List<EadMetadataField> identityStatementAreaList = new ArrayList<>();

    /* 2. Context Area */
    //    Name of creator(s)
    //    Administrative | Biographical history
    //    Archival history
    //    Immediate source of acquisition or transfer
    private List<EadMetadataField> contextAreaList = new ArrayList<>();

    /* 3. Content and Structure Area */
    //    Scope and content
    //    Appraisal, destruction and scheduling information
    //    Accruals
    //    System of arrangement
    private List<EadMetadataField> contentAndStructureAreaAreaList = new ArrayList<>();

    /* 4. Condition of Access and Use Area */
    //    Conditions governing access
    //    Conditions governing reproduction
    //    Language | Scripts of material
    //    Physical characteristics and technical requirements
    //    Finding aids
    private List<EadMetadataField> accessAndUseAreaList = new ArrayList<>();

    /* 5. Allied Materials Area */
    //    Existence and location of originals
    //    Existence and location of copies
    //    Related units of description
    //    Publication note
    private List<EadMetadataField> alliedMaterialsAreaList = new ArrayList<>();

    /* 6. Note Area */
    //    Note
    private List<EadMetadataField> notesAreaList = new ArrayList<>();

    /* 7. Description Control Area */
    //    Archivist's Note
    //    Rules or Conventions
    //    Date(s) of descriptions
    private List<EadMetadataField> descriptionControlAreaList = new ArrayList<>();

    public EadEntry(Integer order, Integer hierarchy) {
        this.orderNumber = order;
        this.hierarchy = hierarchy;
    }

    public void addSubEntry(EadEntry other) {
        subEntryList.add(other);
        other.setParentNode(this);
    }

    public void removeSubEntry(EadEntry other) {
        subEntryList.remove(other);
        reOrderElements();
    }

    public void reOrderElements() {
        int order = 0;
        for (EadEntry entry : subEntryList) {
            entry.setOrderNumber(order++);
        }
    }

    public List<EadEntry> getAsFlatList() {
        List<EadEntry> list = new LinkedList<>();
        list.add(this);
        if (displayChildren) {
            if (subEntryList != null) {
                for (EadEntry ds : subEntryList) {
                    list.addAll(ds.getAsFlatList());
                }
            }
        }
        return list;
    }

    public boolean isHasChildren() {
        return !subEntryList.isEmpty();

    }

}
