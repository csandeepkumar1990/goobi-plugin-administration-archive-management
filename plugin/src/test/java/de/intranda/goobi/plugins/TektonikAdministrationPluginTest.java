package de.intranda.goobi.plugins;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import de.intranda.goobi.plugins.model.EadEntry;
import de.intranda.goobi.plugins.model.EadMetadataField;
import de.sub.goobi.config.ConfigurationHelper;
import de.sub.goobi.helper.HttpClientHelper;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ ConfigurationHelper.class, HttpClientHelper.class })
@PowerMockIgnore({ "javax.management.*", "javax.net.ssl.*" })

public class TektonikAdministrationPluginTest {

    private String resourcesFolder;

    @Before
    public void setUp() throws Exception {
        resourcesFolder = "src/test/resources/"; // for junit tests in eclipse

        if (!Files.exists(Paths.get(resourcesFolder))) {
            resourcesFolder = "target/test-classes/"; // to run mvn test from cli or in jenkins
        }

        PowerMock.mockStatic(HttpClientHelper.class);
        EasyMock.expect(HttpClientHelper.getStringFromUrl("http://localhost:8984/databases")).andReturn(getDatabaseResponse()).anyTimes();
        EasyMock.expect(HttpClientHelper.getStringFromUrl("http://localhost:8984/db/fixture")).andReturn(readDatabaseResponse()).anyTimes();
        PowerMock.replay(HttpClientHelper.class);



        PowerMock.mockStatic(ConfigurationHelper.class);
        ConfigurationHelper configurationHelper = EasyMock.createMock(ConfigurationHelper.class);
        EasyMock.expect(ConfigurationHelper.getInstance()).andReturn(configurationHelper).anyTimes();
        EasyMock.expect(configurationHelper.getConfigurationFolder()).andReturn(resourcesFolder).anyTimes();

        EasyMock.replay(configurationHelper);
        PowerMock.replay(ConfigurationHelper.class);
    }



    private String getDatabaseResponse() {
        StringBuilder sb = new StringBuilder();
        sb.append("<databases xmlns=\"urn:isbn:1-931666-22-9\">");
        sb.append("    <database>first database</database>");
        sb.append("    <database>second database</database>");
        sb.append("</databases>");
        return sb.toString();
    }

    private String readDatabaseResponse() throws IOException {
        Path eadSource = Paths.get(resourcesFolder + "EAD.XML");

        List<String> lines = Files.readAllLines(eadSource, StandardCharsets.UTF_8);
        StringBuilder sb = new StringBuilder();
        for (String line : lines) {
            sb.append(line);
        }

        return sb.toString();
    }

    @Test
    public void testConstructor() throws IOException {
        TektonikAdministrationPlugin plugin = new TektonikAdministrationPlugin();
        assertNotNull(plugin);
    }

    @Test
    public void testListDatabases() {
        TektonikAdministrationPlugin plugin = new TektonikAdministrationPlugin();
        plugin.setDatastoreUrl("http://localhost:8984/");
        List<String> databases = plugin.getPossibleDatabases();
        assertEquals(2, databases.size());
        assertEquals("first database", databases.get(0));
        assertEquals("second database", databases.get(1));
    }

    @Test
    public void testFlatList() {
        TektonikAdministrationPlugin plugin = new TektonikAdministrationPlugin();
        plugin.setDatastoreUrl("http://localhost:8984/");
        plugin.getPossibleDatabases();
        plugin.setSelectedDatabase("fixture");
        plugin.loadSelectedDatabase();
        //  hierarchy contains only root element
        List<EadEntry> el = plugin.getHierarchialList();
        assertEquals(1, el.size());
        // flat list contains root element + first hierarchy
        List<EadEntry> flat = plugin.getFlatEntryList();
        assertEquals(2, flat.size());
        // display second hierarchy
        flat.get(1).setDisplayChildren(true);
        plugin.resetFlatList();
        flat = plugin.getFlatEntryList();
        // now flat list contains root element + first + second hierarchy
        assertEquals(7, flat.size());
    }

    @Test
    public void testLoadDatabase() {
        TektonikAdministrationPlugin plugin = new TektonikAdministrationPlugin();
        plugin.setDatastoreUrl("http://localhost:8984/");
        plugin.getPossibleDatabases();
        plugin.setSelectedDatabase("fixture");
        plugin.loadSelectedDatabase();

        List<EadEntry> el = plugin.getHierarchialList();
        assertEquals(1, el.size());

        EadEntry entry = el.get(0);
        List<EadMetadataField> fieldList = entry.getIdentityStatementAreaList();
        EadMetadataField agencycode = null;
        EadMetadataField eadid = null;
        EadMetadataField recordid = null;
        EadMetadataField unitid = null;
        EadMetadataField unittitle = null;
        EadMetadataField unitdate = null;
        EadMetadataField unitdatestructured = null;
        EadMetadataField descriptionLevel = null;
        EadMetadataField physdesc = null;
        EadMetadataField physdescstructured = null;

        for (EadMetadataField emf : entry.getIdentityStatementAreaList()) {
            switch (emf.getName()) {
                case "agencycode":
                    agencycode = emf;
                    break;
                case "eadid":
                    eadid = emf;
                    break;
                case "recordid":
                    recordid = emf;
                    break;
                case "unitid":
                    unitid = emf;
                    break;
                case "unittitle":
                    unittitle = emf;
                    break;
                case "unitdate":
                    unitdate = emf;
                    break;
                case "physdesc":
                    physdesc = emf;
                    break;
                case "unitdatestructured":
                    unitdatestructured = emf;
                    break;
                case "descriptionLevel":
                    descriptionLevel = emf;
                    break;
                case "physdescstructured":
                    physdescstructured = emf;
                    break;
            }
        }
        EadMetadataField origination = null;
        EadMetadataField bioghist = null;
        EadMetadataField custodhist = null;
        EadMetadataField acqinfo = null;
        for (EadMetadataField emf : entry.getContextAreaList()) {
            switch (emf.getName()) {
                case "origination":
                    origination = emf;
                    break;
                case "bioghist":
                    bioghist = emf;
                    break;
                case "custodhist":
                    custodhist = emf;
                    break;
                case "acqinfo":
                    acqinfo = emf;
                    break;
            }
        }

        EadMetadataField scopecontent = null;
        EadMetadataField appraisal = null;
        EadMetadataField accruals = null;
        EadMetadataField arrangement = null;
        for (EadMetadataField emf : entry.getContentAndStructureAreaAreaList()) {
            switch (emf.getName()) {
                case "scopecontent":
                    scopecontent = emf;
                    break;
                case "appraisal":
                    appraisal = emf;
                    break;
                case "accruals":
                    accruals = emf;
                    break;
                case "arrangement":
                    arrangement = emf;
                    break;
            }
        }
        EadMetadataField accessrestrict = null;
        EadMetadataField userestrict = null;
        EadMetadataField langmaterial = null;
        EadMetadataField phystech = null;
        EadMetadataField otherfindaid = null;
        for (EadMetadataField emf : entry.getAccessAndUseAreaList()) {
            switch (emf.getName()) {
                case "accessrestrict":
                    accessrestrict = emf;
                    break;
                case "userestrict":
                    userestrict = emf;
                    break;
                case "langmaterial":
                    langmaterial = emf;
                    break;
                case "phystech":
                    phystech = emf;
                    break;
                case "otherfindaid":
                    otherfindaid = emf;
                    break;
            }

        }
        EadMetadataField originalsloc = null;
        EadMetadataField altformavail = null;
        EadMetadataField relatedmaterial = null;
        EadMetadataField separatedmaterial = null;
        EadMetadataField bibliography = null;
        for (EadMetadataField emf : entry.getAlliedMaterialsAreaList()) {
            switch (emf.getName()) {
                case "originalsloc":
                    originalsloc = emf;
                    break;
                case "altformavail":
                    altformavail = emf;
                    break;
                case "relatedmaterial":
                    relatedmaterial = emf;
                    break;
                case "separatedmaterial":
                    separatedmaterial = emf;
                    break;
                case "bibliography":
                    bibliography = emf;
                    break;
            }
        }

        EadMetadataField didnote = null;
        EadMetadataField odd = null;
        for (EadMetadataField emf : entry.getNotesAreaList()) {
            switch (emf.getName()) {
                case "didnote":
                    didnote = emf;
                    break;
                case "odd":
                    odd = emf;
                    break;
            }
        }

        EadMetadataField processinfo = null;
        EadMetadataField conventiondeclaration = null;
        EadMetadataField maintenanceevent = null;
        EadMetadataField eventdatetime = null;
        for (EadMetadataField emf : entry.getDescriptionControlAreaList()) {
            switch (emf.getName()) {
                case "processinfo":
                    processinfo = emf;
                    break;
                case "conventiondeclaration":
                    conventiondeclaration = emf;
                    break;
                case "maintenanceevent":
                    maintenanceevent = emf;
                    break;
                case "eventdatetime":
                    eventdatetime = emf;
                    break;

            }
        }

        assertEquals(1, entry.getOrderNumber().intValue());
        assertEquals(0, entry.getHierarchy().intValue());
        assertEquals("tectonics header title", entry.getLabel());

        assertEquals("eadid", eadid.getValue());
        assertEquals("recordid", recordid.getValue());
        assertEquals("agencycode", agencycode.getValue());
        assertEquals("unitid", unitid.getValue());
        assertEquals("unittitle", unittitle.getValue());

        assertEquals("conventiondeclaration", conventiondeclaration.getValue());
        assertEquals("eventtype", maintenanceevent.getValue());
        assertEquals("eventdatetime", eventdatetime.getValue());
        assertEquals("collection", descriptionLevel.getValue());

        assertEquals("unitdate", unitdate.getValue());
        assertEquals("unitdatestructured", unitdatestructured.getValue());
        assertEquals("physdesc", physdesc.getValue());
        assertEquals("physdescstructured", physdescstructured.getValue());
        assertEquals("origination", origination.getValue());
        assertEquals("langmaterial", langmaterial.getValue());
        assertEquals("didnote", didnote.getValue());

        assertEquals("bioghist", bioghist.getValue());
        assertEquals("custodhist", custodhist.getValue());
        assertEquals("acqinfo", acqinfo.getValue());
        assertEquals("scopecontent", scopecontent.getValue());
        assertEquals("appraisal", appraisal.getValue());
        assertEquals("accruals", accruals.getValue());
        assertEquals("arrangement", arrangement.getValue());
        assertEquals("accessrestrict", accessrestrict.getValue());
        assertEquals("userestrict", userestrict.getValue());
        assertEquals("phystech", phystech.getValue());
        assertEquals("otherfindaid", otherfindaid.getValue());

        assertEquals("originalsloc", originalsloc.getValue());
        assertEquals("altformavail", altformavail.getValue());
        assertEquals("relatedmaterial", relatedmaterial.getValue());
        assertEquals("separatedmaterial", separatedmaterial.getValue());
        assertEquals("bibliography", bibliography.getValue());
        assertEquals("odd", odd.getValue());
        assertEquals("processinfo", processinfo.getValue());

        EadEntry firstSub = entry.getSubEntryList().get(0);
        assertEquals(1, firstSub.getOrderNumber().intValue());
        assertEquals(1, firstSub.getHierarchy().intValue());
        fieldList = firstSub.getIdentityStatementAreaList();
        for (EadMetadataField emf : fieldList) {
            switch (emf.getName()) {
                case "unitid":
                    unitid = emf;
                    break;
                case "unittitle":
                    unittitle = emf;
                    break;
                case "descriptionLevel":
                    descriptionLevel = emf;
                    break;
            }
        }

        assertEquals("file", descriptionLevel.getValue());
        assertEquals("1234uniqueId", firstSub.getId());
        assertEquals("first level id", unitid.getValue());
        assertEquals("first level title", unittitle.getValue());

        List<EadEntry> secondSubList = firstSub.getSubEntryList();

        EadEntry second = secondSubList.get(0);
        assertEquals(1, second.getOrderNumber().intValue());
        assertEquals(2, second.getHierarchy().intValue());
        fieldList = second.getIdentityStatementAreaList();
        for (EadMetadataField emf : fieldList) {
            switch (emf.getName()) {
                case "unitid":
                    unitid = emf;
                    break;
                case "unittitle":
                    unittitle = emf;
                    break;
            }
        }

        assertEquals("1 Werke", unittitle.getValue());
        second = secondSubList.get(1);
        assertEquals(2, second.getOrderNumber().intValue());
        assertEquals(2, second.getHierarchy().intValue());
        fieldList = second.getIdentityStatementAreaList();
        for (EadMetadataField emf : fieldList) {
            switch (emf.getName()) {
                case "unitid":
                    unitid = emf;
                    break;
                case "unittitle":
                    unittitle = emf;
                    break;
            }
        }
        assertEquals("2 Korrespondenz", unittitle.getValue());

        second = secondSubList.get(4);
        assertEquals(5, second.getOrderNumber().intValue());
        assertEquals(2, second.getHierarchy().intValue());
        fieldList = second.getIdentityStatementAreaList();
        for (EadMetadataField emf : fieldList) {
            switch (emf.getName()) {
                case "unitid":
                    unitid = emf;
                    break;
                case "unittitle":
                    unittitle = emf;
                    break;
            }
        }
        assertEquals("5 Sammlungen / Objekte", unittitle.getValue());

        EadEntry third = second.getSubEntryList().get(2);
        assertEquals(3, third.getOrderNumber().intValue());
        assertEquals(3, third.getHierarchy().intValue());
        fieldList = third.getIdentityStatementAreaList();
        for (EadMetadataField emf : fieldList) {
            switch (emf.getName()) {
                case "unitid":
                    unitid = emf;
                    break;
                case "unittitle":
                    unittitle = emf;
                    break;
            }
        }
        assertEquals("5.3 Fotosammlung", unittitle.getValue());

        EadEntry fourth = third.getSubEntryList().get(2);
        fieldList = fourth.getIdentityStatementAreaList();
        for (EadMetadataField emf : fieldList) {
            switch (emf.getName()) {
                case "unitid":
                    unitid = emf;
                    break;
                case "unittitle":
                    unittitle = emf;
                    break;
                case "descriptionLevel":
                    descriptionLevel = emf;
                    break;
            }
        }
        assertEquals("5.3.3 Porträts, andere", unittitle.getValue());
        assertEquals("class", descriptionLevel.getValue());

        EadEntry fifth = fourth.getSubEntryList().get(0);

        fieldList = fifth.getIdentityStatementAreaList();
        for (EadMetadataField emf : fieldList) {
            switch (emf.getName()) {
                case "unitid":
                    unitid = emf;
                    break;
                case "unittitle":
                    unittitle = emf;
                    break;
                case "descriptionLevel":
                    descriptionLevel = emf;
                    break;
            }
        }
        assertEquals("file", descriptionLevel.getValue());
    }
}
