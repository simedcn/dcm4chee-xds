/* ***** BEGIN LICENSE BLOCK *****
 * Version: MPL 1.1/GPL 2.0/LGPL 2.1
 *
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * The Original Code is part of dcm4che, an implementation of DICOM(TM) in
 * Java(TM), hosted at https://github.com/gunterze/dcm4che.
 *
 * The Initial Developer of the Original Code is
 * Agfa Healthcare.
 * Portions created by the Initial Developer are Copyright (C) 2011
 * the Initial Developer. All Rights Reserved.
 *
 * Contributor(s):
 * See @authors listed below
 *
 * Alternatively, the contents of this file may be used under the terms of
 * either the GNU General Public License Version 2 or later (the "GPL"), or
 * the GNU Lesser General Public License Version 2.1 or later (the "LGPL"),
 * in which case the provisions of the GPL or the LGPL are applicable instead
 * of those above. If you wish to allow use of your version of this file only
 * under the terms of either the GPL or the LGPL, and not to allow others to
 * use your version of this file under the terms of the MPL, indicate your
 * decision by deleting the provisions above and replace them with the notice
 * and other provisions required by the GPL or the LGPL. If you do not delete
 * the provisions above, a recipient may use your version of this file under
 * the terms of any one of the MPL, the GPL or the LGPL.
 *
 * ***** END LICENSE BLOCK ***** */

package org.dcm4chee.xds2.ws.registry.query;

import java.util.List;

import org.dcm4chee.xds2.common.XDSConstants;
import org.dcm4chee.xds2.common.exception.XDSException;
import org.dcm4chee.xds2.infoset.rim.AdhocQueryRequest;
import org.dcm4chee.xds2.infoset.rim.AdhocQueryResponse;
import org.dcm4chee.xds2.persistence.QAssociation;
import org.dcm4chee.xds2.persistence.RegistryObject;
import org.dcm4chee.xds2.persistence.XDSDocumentEntry;
import org.dcm4chee.xds2.persistence.XDSFolder;
import org.dcm4chee.xds2.ws.registry.XDSPersistenceWrapper;
import org.dcm4chee.xds2.ws.registry.XDSRegistryBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mysema.query.jpa.impl.JPAQuery;

/**
 * Stored Query Implementation for GetFoldersForDocument 
 * (urn:uuid:10cae35a-c7f9-4cf5-b61e-fc3278ffb578)
 * 
 * @author franz.willer@gmail.com
 *
 */
public class GetFoldersForDocumentQuery extends StoredQuery {

    private static Logger log = LoggerFactory.getLogger(GetFoldersForDocumentQuery.class);

    public GetFoldersForDocumentQuery(AdhocQueryRequest req, XDSRegistryBean session) throws XDSException {
        super(req, session);
    }
    
    public AdhocQueryResponse query() throws XDSException {
        AdhocQueryResponse rsp = initAdhocQueryResponse();
        XDSDocumentEntry doc = getDocumentEntry();
        if (doc != null) {
            List<RegistryObject> objects = new JPAQuery(getSession().getEntityManager())
            .from(QAssociation.association)
            .where(QAssociation.association.targetObject.eq(doc), 
                    QAssociation.association.sourceObject.instanceOf(XDSFolder.class),
                    QAssociation.association.assocType.id.eq(XDSConstants.HAS_MEMBER))
            .list(QAssociation.association.sourceObject);
            log.info("#### Found Folders:"+objects);
            rsp.setRegistryObjectList(new XDSPersistenceWrapper(getSession()).toRegistryObjectListType(objects, isLeafClass()));
        }
        return rsp;
    }

    @Override
    public String[] getRequiredParameterNames() {
        return new String[]{XDSConstants.QRY_DOCUMENT_UNIQUE_ID+"|"+XDSConstants.QRY_DOCUMENT_ENTRY_UUID};
    }
    
}
