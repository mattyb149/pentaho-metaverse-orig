/*!
 * PENTAHO CORPORATION PROPRIETARY AND CONFIDENTIAL
 *
 * Copyright 2002 - 2014 Pentaho Corporation (Pentaho). All rights reserved.
 *
 * NOTICE: All information including source code contained herein is, and
 * remains the sole property of Pentaho and its licensors. The intellectual
 * and technical concepts contained herein are proprietary and confidential
 * to, and are trade secrets of Pentaho and may be covered by U.S. and foreign
 * patents, or patents in process, and are protected by trade secret and
 * copyright laws. The receipt or possession of this source code and/or related
 * information does not convey or imply any rights to reproduce, disclose or
 * distribute its contents, or to manufacture, use, or sell anything that it
 * may describe, in whole or in part. Any reproduction, modification, distribution,
 * or public display of this information without the express written authorization
 * from Pentaho is strictly prohibited and in violation of applicable laws and
 * international treaties. Access to the source code contained herein is strictly
 * prohibited to anyone except those individuals and entities who have executed
 * confidentiality and non-disclosure agreements or other agreements with Pentaho,
 * explicitly covering such access.
 */

package com.pentaho.dictionary;

import com.pentaho.metaverse.impl.MetaverseLogicalIdGenerator;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.pentaho.platform.api.metaverse.IHasProperties;
import org.pentaho.platform.api.metaverse.ILogicalIdGenerator;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author mburgess
 * 
 */
public class MetaverseTransientNodeTest {

  MetaverseTransientNode node;

  /**
   * @throws java.lang.Exception
   */
  @BeforeClass
  public static void setUpBeforeClass() throws Exception {
  }

  /**
   * @throws java.lang.Exception
   */
  @AfterClass
  public static void tearDownAfterClass() throws Exception {
  }

  /**
   * @throws java.lang.Exception
   */
  @Before
  public void setUp() throws Exception {
    node = new MetaverseTransientNode();
  }

  /**
   * @throws java.lang.Exception
   */
  @After
  public void tearDown() throws Exception {
  }

  @Test
  public void testGetName() {
    assertNull( node.getName() );
  }

  public void testSetName() {
    MetaverseTransientNode myNode = new MetaverseTransientNode();
    myNode.setName( "myName" );
    assertEquals( myNode.getName(), "myName" );
  }

  @Test
  public void testGetType() {
    assertNull( node.getType() );
  }

  @Test
  public void testSetType() {
    MetaverseTransientNode myNode = new MetaverseTransientNode();
    myNode.setType( "myType" );
    assertEquals( myNode.getType(), "myType" );
  }

  @Test
  public void testGetLogicalId() throws Exception {
    MetaverseTransientNode myNode = new MetaverseTransientNode();

    myNode.setName( "testName" );
    myNode.setType( "testType" );
    myNode.setProperty( "zzz", "last" );
    myNode.setProperty( DictionaryConst.PROPERTY_NAMESPACE, "" );
    myNode.setStringID( "myId" );

    // should be using the default logical id generator initially
    assertEquals( "{\"name\":\"testName\",\"namespace\":\"\",\"type\":\"testType\"}", myNode.getLogicalId() );

    ILogicalIdGenerator idGenerator = new MetaverseLogicalIdGenerator( "type", "zzz", "name" );
    myNode.setLogicalIdGenerator( idGenerator );
    assertNotNull( myNode.logicalIdGenerator );

    // logical id should be sorted based on key
    assertEquals( "{\"name\":\"testName\",\"type\":\"testType\",\"zzz\":\"last\"}", myNode.getLogicalId() );
  }

  @Test
  public void testGetLogicalId_nullIdGenerator() throws Exception {
    MetaverseTransientNode myNode = new MetaverseTransientNode( "myId" );
    myNode.setName( "testName" );
    myNode.setType( "testType" );
    myNode.setLogicalIdGenerator( null );

    assertEquals( "myId", myNode.getLogicalId() );
  }

  @Test
  public void testGetLogicalId_nullLogicalIdGeneration() throws Exception {
    MetaverseTransientNode myNode = new MetaverseTransientNode( "myId" );
    ILogicalIdGenerator generator = mock( ILogicalIdGenerator.class );
    when( generator.generateId( any( IHasProperties.class ) ) ).thenReturn( null );

    myNode.setName( "testName" );
    myNode.setType( "testType" );
    myNode.setLogicalIdGenerator( generator );

    assertEquals( "myId", myNode.getLogicalId() );
  }
}
