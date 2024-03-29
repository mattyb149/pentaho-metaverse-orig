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

package com.pentaho.metaverse.impl.model.kettle.json;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.pentaho.metaverse.api.model.IExternalResourceInfo;
import com.pentaho.metaverse.api.model.IInfo;
import com.pentaho.metaverse.impl.model.ExternalResourceInfoFactory;
import com.pentaho.metaverse.impl.model.kettle.LineageRepository;
import org.pentaho.di.core.plugins.JobEntryPluginType;
import org.pentaho.di.core.plugins.PluginRegistry;
import org.pentaho.di.job.JobMeta;
import org.pentaho.di.job.entry.JobEntryBase;
import org.pentaho.di.repository.ObjectId;
import org.pentaho.di.repository.StringObjectId;
import org.pentaho.di.resource.ResourceEntry;
import org.pentaho.di.resource.ResourceReference;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * User: RFellows Date: 12/15/14
 */
public abstract class AbstractJobEntryJsonSerializer<T extends JobEntryBase>
  extends GenericStepOrJobEntryJsonSerializer<T> {

  public AbstractJobEntryJsonSerializer( Class<T> aClass ) {
    super( aClass );
  }

  public AbstractJobEntryJsonSerializer( JavaType javaType ) {
    super( javaType );
  }

  public AbstractJobEntryJsonSerializer( Class<?> aClass, boolean b ) {
    super( aClass, b );
  }

  @Override
  protected void writeBasicInfo( T meta, JsonGenerator json ) throws IOException {
    json.writeStringField( IInfo.JSON_PROPERTY_CLASS, meta.getClass().getName() );
    json.writeStringField( IInfo.JSON_PROPERTY_NAME, meta.getName() );
    json.writeStringField( JSON_PROPERTY_TYPE, getStepType( meta ) );
  }

  protected String getStepType( T entry ) {
    String stepType = null;
    try {
      stepType = PluginRegistry.getInstance().findPluginWithId(
          JobEntryPluginType.class, entry.getPluginId() ).getName();
    } catch ( Throwable t ) {
      stepType = entry.getClass().getSimpleName();
    }
    return stepType;
  }

  protected void writeRepoAttributes( T meta, JsonGenerator json ) throws IOException {

    ObjectId jobId = meta.getObjectId() == null ? new StringObjectId( meta.getName() ) : meta.getObjectId();

    LineageRepository repo = getLineageRepository();
    if ( repo != null ) {
      Map<String, Object> attrs = repo.getJobEntryAttributesCache( jobId );
      json.writeObjectField( JSON_PROPERTY_ATTRIBUTES, attrs );

      List<Map<String, Object>> fields = repo.getJobEntryFieldsCache( jobId );
      json.writeObjectField( JSON_PROPERTY_FIELDS, fields );
    }

  }
  protected void writeExternalResources( T meta, JsonGenerator json, SerializerProvider serializerProvider )
    throws IOException, JsonGenerationException {

    json.writeArrayFieldStart( JSON_PROPERTY_EXTERNAL_RESOURCES );
    JobMeta jobMeta = new JobMeta();
    if ( meta.getParentJob() != null && meta.getParentJob().getJobMeta() != null ) {
      jobMeta = meta.getParentJob().getJobMeta();
    }
    List<ResourceReference> dependencies = meta.getResourceDependencies( jobMeta );
    for ( ResourceReference dependency : dependencies ) {
      for ( ResourceEntry resourceEntry : dependency.getEntries() ) {
        IExternalResourceInfo resourceInfo = ExternalResourceInfoFactory.createResource( resourceEntry );
        json.writeObject( resourceInfo );
      }
    }
    json.writeEndArray();
  }

}
