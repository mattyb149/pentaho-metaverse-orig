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

package com.pentaho.metaverse.service;

import com.tinkerpop.blueprints.Graph;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.*;

@RunWith( MockitoJUnitRunner.class )
public class MetaversePluginLifecycleListenerTest {

  @Mock private Graph mockGraph;

  private MetaversePluginLifecycleListener metaversePluginLifecycleListener;

  @Before
  public void setUp() throws Exception {
    metaversePluginLifecycleListener = new MetaversePluginLifecycleListener();
  }

  @Test
  public void testUnload() throws Exception {
    metaversePluginLifecycleListener.setGraph( mockGraph );
    metaversePluginLifecycleListener.unLoaded();

    verify( mockGraph, times ( 1 ) ).shutdown();
  }

  @Test
  public void testUnload_NullGraph() throws Exception {
    MetaversePluginLifecycleListener spyListener = spy( metaversePluginLifecycleListener );
    when( spyListener.getGraph() ).thenReturn( null );
    metaversePluginLifecycleListener.unLoaded();

    verify( mockGraph, times ( 0 ) ).shutdown();
  }

  @Test
  public void testLoaded() throws Exception {
    metaversePluginLifecycleListener.loaded();
  }

  @Test
  public void testInit() throws Exception {
    metaversePluginLifecycleListener.init();
  }
}
