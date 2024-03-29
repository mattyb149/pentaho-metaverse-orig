/*
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

package com.pentaho.metaverse.analyzer.kettle.step.tableoutput;

import com.pentaho.metaverse.api.model.IExternalResourceInfo;
import org.junit.Before;
import org.junit.Test;
import org.pentaho.di.core.database.DatabaseInterface;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.trans.steps.tableoutput.TableOutputMeta;

import java.util.Collection;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TableOutputExternalResourceConsumerTest {

  TableOutputExternalResourceConsumer consumer;

  @Before
  public void setUp() throws Exception {
    consumer = new TableOutputExternalResourceConsumer();
  }

  @Test
  public void testGetStepMetaClass() throws Exception {
    assertEquals( TableOutputMeta.class, consumer.getMetaClass() );
  }

  @Test
  public void testGetResourcesFromMeta() throws Exception {
    TableOutputMeta meta = mock( TableOutputMeta.class );
    DatabaseMeta dbMeta = mock( DatabaseMeta.class );
    DatabaseInterface dbi = mock( DatabaseInterface.class );

    when( meta.getDatabaseMeta() ).thenReturn( dbMeta );
    when( dbMeta.getAccessTypeDesc() ).thenReturn( "JNDI" );
    when( dbMeta.getName() ).thenReturn( "TestConnection" );
    when( dbMeta.getDescription() ).thenReturn( "my conn description" );
    when( dbMeta.getDatabaseInterface() ).thenReturn( dbi );
    when( dbi.getPluginId() ).thenReturn( "POSTGRESQL" );

    Collection<IExternalResourceInfo> resources = consumer.getResourcesFromMeta( meta );
    assertEquals( 1, resources.size() );
    IExternalResourceInfo res = resources.iterator().next();
    assertEquals( "TestConnection", res.getName() );
  }
}
