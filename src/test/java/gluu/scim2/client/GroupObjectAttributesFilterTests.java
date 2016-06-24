/*
 * SCIM-Client is available under the MIT License (2008). See http://opensource.org/licenses/MIT for full text.
 *
 * Copyright (c) 2014, Gluu
 */
package gluu.scim2.client;

import gluu.BaseScimTest;
import gluu.scim.client.ScimResponse;
import gluu.scim2.client.util.Util;
import org.apache.commons.io.FileUtils;
import org.gluu.oxtrust.model.scim2.*;
import org.junit.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import javax.ws.rs.core.MediaType;
import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Set;

import static org.testng.Assert.assertEquals;

/**
 * @author Val Pecaoco
 */
public class GroupObjectAttributesFilterTests extends BaseScimTest {

    String domainURL;
    Scim2Client client;

    String id;

    @BeforeTest
    @Parameters({"domainURL", "umaMetaDataUrl", "umaAatClientId", "umaAatClientJksPath", "umaAatClientJksPassword", "umaAatClientKeyId"})
    public void init(final String domainURL, final String umaMetaDataUrl, final String umaAatClientId, final String umaAatClientJksPath, final String umaAatClientJksPassword, @Optional final String umaAatClientKeyId) throws Exception {
        this.domainURL = domainURL;
        
        client = Scim2Client.umaInstance(domainURL, umaMetaDataUrl, umaAatClientId, umaAatClientJksPath, umaAatClientJksPassword, umaAatClientKeyId);
    }

    @Test(groups = "a")
    public void testCreateGroup() throws Exception {

        System.out.println("IN testCreateGroup...");

        String displayName = "Test Group " + new Date().getTime();

        Group group = new Group();
        group.setDisplayName(displayName);

        String[] attributesArray = new String[]{"urn:ietf:params:scim:schemas:core:2.0:Group:displayName"};
        ScimResponse response = client.createGroup(group, attributesArray);
        System.out.println("response body = " + response.getResponseBodyString());

        Assert.assertEquals(201, response.getStatusCode());

        Group groupCreated = Util.toGroup(response);
        assertEquals(groupCreated.getDisplayName(), displayName, "Group not added or retrieved");

        this.id = groupCreated.getId();

        System.out.println("groupCreated.getId() = " + groupCreated.getId());
        System.out.println("groupCreated.getDisplayName() = " + groupCreated.getDisplayName());

        System.out.println("LEAVING testCreateGroup..." + "\n");
    }

    @Test(groups = "b", dependsOnGroups = "a")
    public void testRetrieveNewGroup() throws Exception {

        System.out.println("IN testRetrieveNewGroup...");

        String[] attributesArray = new String[]{"displayName"};
        ScimResponse response = client.retrieveGroup(this.id, attributesArray);
        System.out.println("response body = " + response.getResponseBodyString());

        Assert.assertEquals(200, response.getStatusCode());

        Group groupRetrieved = Util.toGroup(response);
        assertEquals(groupRetrieved.getId(), this.id, "Group could not be retrieved");

        System.out.println("groupRetrieved.getId() = " + groupRetrieved.getId());
        System.out.println("groupRetrieved.getDisplayName() = " + groupRetrieved.getDisplayName());

        System.out.println("LEAVING testRetrieveNewGroup..." + "\n");
    }

    @Test(groups = "c", dependsOnGroups = "b")
    public void testUpdateNewGroup() throws Exception {

        System.out.println("IN testUpdateNewGroup...");

        Thread.sleep(3000);  // Sleep for 3 seconds

        String[] attributesArray = new String[]{"displayName"};
        ScimResponse response = client.retrieveGroup(this.id, attributesArray);
        System.out.println("response body = " + response.getResponseBodyString());

        Assert.assertEquals(200, response.getStatusCode());

        Group groupRetrieved = Util.toGroup(response);

        groupRetrieved.setDisplayName(groupRetrieved.getDisplayName() + " UPDATED");

        ScimResponse responseUpdated = client.updateGroup(groupRetrieved, this.id, attributesArray);
        System.out.println("UPDATED response body = " + responseUpdated.getResponseBodyString());

        Assert.assertEquals(200, responseUpdated.getStatusCode());

        Group groupUpdated = Util.toGroup(responseUpdated);

        assertEquals(groupUpdated.getId(), this.id, "Group could not be retrieved");
        assert(groupUpdated.getMeta().getLastModified().getTime() > groupUpdated.getMeta().getCreated().getTime());

        System.out.println("groupUpdated.getId() = " + groupUpdated.getId());
        System.out.println("groupUpdated.getDisplayName() = " + groupUpdated.getDisplayName());
        System.out.println("groupUpdated.getMeta().getLastModified().getTime() = " + groupUpdated.getMeta().getLastModified().getTime());
        System.out.println("groupUpdated.getMeta().getCreated().getTime() = " + groupUpdated.getMeta().getCreated().getTime());

        System.out.println("LEAVING testUpdateNewGroup..." + "\n");
    }

    @Test(groups = "d", dependsOnGroups = "c")
    public void testUpdateDisplayNameDifferentId() throws Exception {

        System.out.println("IN testUpdateDisplayNameDifferentId...");

        String[] attributesArray = new String[]{"displayName"};
        ScimResponse response = client.retrieveGroup(this.id, attributesArray);
        System.out.println("response body = " + response.getResponseBodyString());

        Assert.assertEquals(200, response.getStatusCode());

        Group groupRetrieved = Util.toGroup(response);

        groupRetrieved.setDisplayName("Gluu Manager Group");

        ScimResponse responseUpdated = client.updateGroup(groupRetrieved, this.id, attributesArray);
        System.out.println("UPDATED response body = " + responseUpdated.getResponseBodyString());

        Assert.assertEquals(409, responseUpdated.getStatusCode());

        System.out.println("LEAVING testUpdateDisplayNameDifferentId..." + "\n");
    }

    @Test(groups = "e", dependsOnGroups = "d", alwaysRun = true)
    public void testDeleteGroup() throws Exception {

        System.out.println("IN testDeleteGroup...");

        ScimResponse response = client.deleteGroup(this.id);
        assertEquals(response.getStatusCode(), 200, "Group could not be deleted, status != 200");

        System.out.println("LEAVING testDeleteGroup..." + "\n");
    }

    @Test(dependsOnGroups = "e", alwaysRun = true)
    public void testGroupDeserializerUsers() throws Exception {

        System.out.println("IN testGroupDeserializerUsers...");

        String filter = "userName eq \"admin\"";
        int startIndex = 1;
        int count = 2;
        String sortBy = "";
        String sortOrder = "";
        String[] attributes = null;

        ScimResponse response = client.searchUsers(filter, startIndex, count, sortBy, sortOrder, attributes);
        System.out.println("response body = " + response.getResponseBodyString());

        Assert.assertEquals(200, response.getStatusCode());

        ListResponse listResponse = Util.toListResponseUser(response, client.getUserExtensionSchema());
        assertEquals(listResponse.getTotalResults(), 1);

        User userRetrieved = (User) listResponse.getResources().get(0);
        assertEquals(userRetrieved.getUserName(), "admin", "User could not be retrieved");

        System.out.println("userRetrieved.getId() = " + userRetrieved.getId());
        System.out.println("userRetrieved.getDisplayName() = " + userRetrieved.getDisplayName());

        List<GroupRef> groups = userRetrieved.getGroups();
        for (GroupRef group : groups) {

            if (group.getDisplay().startsWith("Gluu")) {

                System.out.println("group display = " + group.getDisplay());
                System.out.println("group inum = " + group.getValue());
                System.out.println("group $ref = " + group.getReference());
                Assert.assertNotNull(group.getReference());

                String[] attributesArray = new String[]{"displayName,members.value,urn:ietf:params:scim:schemas:core:2.0:Group:members.$ref"};
                ScimResponse groupRetrievedResponse = client.retrieveGroup(group.getValue(), attributesArray);
                System.out.println("groupRetrievedResponse response body = " + groupRetrievedResponse.getResponseBodyString());

                Assert.assertEquals(200, groupRetrievedResponse.getStatusCode());

                Group adminGroup = Util.toGroup(groupRetrievedResponse);

                Set<MemberRef> members = adminGroup.getMembers();
                for (MemberRef member : members) {
                    System.out.println("member inum = " + member.getValue());
                    System.out.println("member $ref = " + member.getReference());
                    Assert.assertNotNull(member.getReference());
                }

                break;
            }
        }

        System.out.println("LEAVING testGroupDeserializerUsers..." + "\n");
    }
}
