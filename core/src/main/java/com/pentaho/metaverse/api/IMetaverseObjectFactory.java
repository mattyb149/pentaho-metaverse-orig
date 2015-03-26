/*!
 * This program is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
 * Foundation.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 * or from the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * Copyright (c) 2002-2013 Pentaho Corporation..  All rights reserved.
 */

package com.pentaho.metaverse.api;

import org.pentaho.platform.api.metaverse.IMetaverseLink;
import org.pentaho.platform.api.metaverse.IMetaverseNode;

import java.util.Map;

/**
 * A factory interface for creating metaverse objects.
 */
public interface IMetaverseObjectFactory {

  /**
   * Creates a new metaverse document object.
   * 
   * @return the new IDocument instance
   */
  IDocument createDocumentObject();

  /**
   * Creates a new metaverse node object and adds it to the current metaverse
   * @param id id of the new node
   * @return the new IMetaverseNode instance
   */
  org.pentaho.platform.api.metaverse.IMetaverseNode createNodeObject( String id );

  /**
   * Creates a new metaverse node and sets its name and type properties as well
   * @param id id of the new node
   * @param name name of the new node
   * @param type type of the new node
   * @return
   */
  org.pentaho.platform.api.metaverse.IMetaverseNode createNodeObject( String id, String name, String type );

  /**
   * Creates a new metaverse node and sets its name and type properties as well.
   * The id of the node will be a UUID string.
   * @param namespace namespace of the new node
   * @param name name of the new node
   * @param type type of the new node
   * @return
   */
  org.pentaho.platform.api.metaverse.IMetaverseNode createNodeObject( INamespace namespace, String name, String type );

  /**
   * Creates a new metaverse node and sets its name and type properties as well
   * @param namespace namespace of the new node
   * @param idGenerator ILogicalIdGenerator for the node
   * @param properties Map of properties to set on the node
   * @return
   */
  IMetaverseNode createNodeObject( INamespace namespace,
                                   ILogicalIdGenerator idGenerator,
                                   Map<String, Object> properties );

  /**
   * Creates a new metaverse link object and adds it to the current metaverse
   * 
   * @return the new IMetaverseLink instance
   */
  IMetaverseLink createLinkObject();
}