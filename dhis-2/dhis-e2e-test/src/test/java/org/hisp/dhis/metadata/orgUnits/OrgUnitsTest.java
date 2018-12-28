/*
 * Copyright (c) 2004-2018, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

/*
 * Copyright (c) 2004-2018, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

/*
 * Copyright (c) 2004-2018, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.hisp.dhis.metadata.orgUnits;

import io.restassured.response.Response;
import org.hisp.dhis.ApiTest;
import org.hisp.dhis.actions.LoginActions;
import org.hisp.dhis.actions.OrgUnitActions;
import org.hisp.dhis.actions.UserActions;
import org.hisp.dhis.dto.OrgUnit;
import org.hisp.dhis.helpers.ResponseValidationHelper;
import org.hisp.dhis.utils.DataGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Gintare Vilkelyte <vilkelyte.gintare@gmail.com>
 */
public class OrgUnitsTest
    extends ApiTest
{
    private LoginActions loginActions;

    private UserActions userActions;

    private OrgUnitActions orgUnitActions;

    @BeforeEach
    public void setUp()
    {
        loginActions = new LoginActions();
        userActions = new UserActions();
        orgUnitActions = new OrgUnitActions();

        loginActions.loginAsDefaultUser();
    }

    @Test
    public void orgUnits_add_withoutPermissions()
    {
        String userName = DataGenerator.randomString();
        String psw = "!XPTOqwerty1";

        userActions.addUser( userName, psw );
        loginActions.loginAsUser( userName, psw );

        OrgUnit orgUnit = dummyOrgUnit();

        Response response = orgUnitActions.sendCreateRequest( orgUnit );

        assertEquals( 403, response.statusCode(), "Wrong status code when creating org unit without permissions" );
        assertEquals( response.jsonPath().get( "message" ), "You don't have the proper permissions to create this object." );
    }

    // todo add tests for creation with level.
    @Test
    public void orgUnits_add_withoutLevel()
    {
        OrgUnit orgUnit = dummyOrgUnit();

        Response response = orgUnitActions.sendCreateRequest( orgUnit );
        ResponseValidationHelper.validateObjectCreation( response );

        String uid = response.jsonPath().getString( "response.uid" );
        assertNotNull( uid );

        response = orgUnitActions.get( uid );

        // todo validate OPEN API 3 schema when it´s ready
        assertEquals( 200, response.statusCode() );
        assertEquals( response.jsonPath().getString( "shortName" ), orgUnit.getShortName() );
        assertEquals( response.jsonPath().getString( "name" ), orgUnit.getName() );
        assertEquals( response.jsonPath().getString( "openingDate" ), orgUnit.getOpeningDate() );
    }

    @Test
    public void orgUnits_update()
    {
        OrgUnit orgUnit = dummyOrgUnit();

        // create
        Response response = orgUnitActions.sendCreateRequest( orgUnit );
        String uid = response.jsonPath().getString( "response.uid" );
        response = orgUnitActions.get( uid );
        String lastUpdatedDate = response.jsonPath().getString( "lastUpdated" );

        // update

        orgUnit.setName( orgUnit.getName() + " updated" );
        orgUnit.setShortName( orgUnit.getShortName() + " updated" );
        orgUnit.setOpeningDate( "2017-09-10T00:00:00.000" );

        response = orgUnitActions.updateOrgUnit( uid, orgUnit );
        assertEquals( 200, response.statusCode(), "Org unit wasn't updated" );

        // validate
        response = orgUnitActions.get( uid );

        assertEquals( 200, response.statusCode() );
        assertEquals( response.jsonPath().getString( "shortName" ), orgUnit.getShortName() );
        assertEquals( response.jsonPath().getString( "name" ), orgUnit.getName() );
        assertEquals( response.jsonPath().getString( "openingDate" ), orgUnit.getOpeningDate() );
        assertNotEquals( response.jsonPath().getString( "lastUpdated" ), lastUpdatedDate );
    }

    private OrgUnit dummyOrgUnit()
    {
        OrgUnit orgUnit = new OrgUnit();
        orgUnit.setName( "Org unit for tests" + DataGenerator.randomString() );
        orgUnit.setShortName( "Short name" );
        orgUnit.setOpeningDate( "2017-09-11T00:00:00.000" );

        return orgUnit;
    }
}