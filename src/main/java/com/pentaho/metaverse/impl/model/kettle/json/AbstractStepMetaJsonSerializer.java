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
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.pentaho.metaverse.analyzer.kettle.ComponentDerivationRecord;
import com.pentaho.metaverse.analyzer.kettle.extensionpoints.ExternalResourceConsumerMap;
import com.pentaho.metaverse.analyzer.kettle.extensionpoints.IStepExternalResourceConsumer;
import com.pentaho.metaverse.analyzer.kettle.step.GenericStepMetaAnalyzer;
import com.pentaho.metaverse.analyzer.kettle.step.IStepAnalyzer;
import com.pentaho.metaverse.analyzer.kettle.step.IStepAnalyzerProvider;
import com.pentaho.metaverse.analyzer.kettle.step.IStepModifiesFields;
import com.pentaho.metaverse.api.model.IExternalResourceInfo;
import com.pentaho.metaverse.api.model.IInfo;
import com.pentaho.metaverse.impl.model.kettle.FieldInfo;
import com.pentaho.metaverse.impl.model.kettle.LineageRepository;
import com.pentaho.metaverse.messages.Messages;
import org.pentaho.di.core.exception.KettleStepException;
import org.pentaho.di.core.plugins.PluginRegistry;
import org.pentaho.di.core.plugins.StepPluginType;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.di.repository.ObjectId;
import org.pentaho.di.repository.StringObjectId;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.BaseStepMeta;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.platform.api.metaverse.MetaverseAnalyzerException;
import org.pentaho.platform.engine.core.system.PentahoSystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * User: RFellows Date: 11/17/14
 */
public abstract class AbstractStepMetaJsonSerializer<T extends BaseStepMeta> extends StdSerializer<T> {

  public static final String JSON_PROPERTY_TYPE = "type";
  public static final String JSON_PROPERTY_TRANSFORMS = "transforms";
  public static final String JSON_PROPERTY_ATTRIBUTES = "attributes";
  public static final String JSON_PROPERTY_FIELDS = "fields";
  public static final String JSON_PROPERTY_INPUT_FIELDS = "inputFields";
  public static final String JSON_PROPERTY_OUTPUT_FIELDS = "outputFields";
  public static final String JSON_PROPERTY_EXTERNAL_RESOURCES = "externalResources";

  private static final Logger LOGGER = LoggerFactory.getLogger( AbstractStepMetaJsonSerializer.class );

  private ExternalResourceConsumerMap map = ExternalResourceConsumerMap.getInstance();

  protected AbstractStepMetaJsonSerializer( Class<T> aClass ) {
    super( aClass );
  }

  protected AbstractStepMetaJsonSerializer( Class<T> aClass, LineageRepository repo ) {
    super( aClass );
    setLineageRepository( repo );
  }

  private LineageRepository lineageRepository;
  private IStepAnalyzerProvider stepAnalyzerProvider;

  public LineageRepository getLineageRepository() {
    return lineageRepository;
  }

  public void setLineageRepository( LineageRepository repo ) {
    this.lineageRepository = repo;
  }

  protected IStepAnalyzerProvider getStepAnalyzerProvider() {
    return stepAnalyzerProvider;
  }

  protected void setStepAnalyzerProvider( IStepAnalyzerProvider stepAnalyzerProvider ) {
    this.stepAnalyzerProvider = stepAnalyzerProvider;
  }

  @Override public void serialize( T meta, JsonGenerator json,
                                   SerializerProvider serializerProvider ) throws IOException, JsonGenerationException {
    json.writeStartObject();

    StepMeta parentStepMeta = meta.getParentStepMeta();
    if ( parentStepMeta != null ) {
      json.writeStringField( IInfo.JSON_PROPERTY_CLASS, meta.getClass().getName() );
      json.writeStringField( IInfo.JSON_PROPERTY_NAME, parentStepMeta.getName() );
      json.writeStringField( JSON_PROPERTY_TYPE, getStepType( parentStepMeta ) );

      writeRepoAttributes( meta, json );

      writeCustomProperties( meta, json, serializerProvider );

      writeInputFields( parentStepMeta, json );
      writeOutputFields( parentStepMeta, json );

      json.writeArrayFieldStart( JSON_PROPERTY_TRANSFORMS );
      writeFieldTransforms( meta, json, serializerProvider );
      json.writeEndArray();

      writeExternalResources( meta, json, serializerProvider );

    }

    json.writeEndObject();

  }

  protected void writeRepoAttributes( T meta, JsonGenerator json ) throws IOException {

    String id = meta.getObjectId() == null ? meta.getParentStepMeta().getName() : meta.getObjectId().toString();
    ObjectId stepId = new StringObjectId( id );

    LineageRepository repo = getLineageRepository();
    if ( repo != null ) {
      Map<String, Object> attrs = repo.getStepAttributesCache( stepId );
      json.writeObjectField( JSON_PROPERTY_ATTRIBUTES, attrs );

      List<Map<String, Object>> fields = repo.getStepFieldsCache( stepId );
      json.writeObjectField( JSON_PROPERTY_FIELDS, fields );
    }
  }

  /**
   * The wrapping array will already be constructed for you, just add serializations of the transforms
   * as objects.
   * <code>
   *   ...
   *   "transforms" : [
   *     // your transform objects here
   *   ]
   *   ...
   * </code>
   * @param meta
   * @param json
   * @param serializerProvider
   * @throws IOException
   * @throws JsonGenerationException
   */
  protected void writeFieldTransforms( T meta, JsonGenerator json, SerializerProvider serializerProvider )
    throws IOException, JsonGenerationException {
    IStepModifiesFields mapper = getStepModifiesFieldsHandler( meta );
    try {
      Set<ComponentDerivationRecord> changes = mapper.getChangeRecords( meta );
      if ( changes != null ) {
        for ( ComponentDerivationRecord change : changes ) {
          if ( change.hasDelta() ) {
            json.writeObject( change );
          }
        }
      }
    } catch ( MetaverseAnalyzerException e ) {
      LOGGER.warn( Messages.getString( "WARNING.Serialization.Step.WriteFieldTransforms",
          meta.getParentStepMeta().getName() ), e );
    }
  }

  /**
   * Free-for-all. put anything specific to your StepMeta here.
   * @param meta
   * @param json
   * @param serializerProvider
   * @throws IOException
   * @throws JsonGenerationException
   */
  protected abstract void writeCustomProperties( T meta, JsonGenerator json, SerializerProvider serializerProvider )
    throws IOException, JsonGenerationException;


  protected void writeExternalResources( T meta, JsonGenerator json, SerializerProvider serializerProvider )
    throws IOException, JsonGenerationException {
    List<IStepExternalResourceConsumer> resourceConsumers =
        getExternalResourceConsumerMap().getStepExternalResourceConsumers( meta.getClass() );

    json.writeArrayFieldStart( JSON_PROPERTY_EXTERNAL_RESOURCES );
    if ( resourceConsumers != null ) {
      for ( IStepExternalResourceConsumer resourceConsumer : resourceConsumers ) {

        Collection<IExternalResourceInfo> infos = resourceConsumer.getResourcesFromMeta( meta );
        for ( IExternalResourceInfo info : infos ) {
          json.writeObject( info );
        }
      }
    }
    json.writeEndArray();
  }

  protected void writeInputFields( StepMeta parentStepMeta, JsonGenerator json ) throws IOException {
    TransMeta parentTransMeta = parentStepMeta.getParentTransMeta();
    if ( parentTransMeta != null ) {
      try {
        RowMetaInterface prevStepFields = parentTransMeta.getPrevStepFields( parentStepMeta );
        writeFields( json, prevStepFields, JSON_PROPERTY_INPUT_FIELDS );
      } catch ( KettleStepException e ) {
        LOGGER.warn( Messages.getString( "WARNING.Serialization.Step.InputFields",
            parentStepMeta.getName() ), e );
      }
    }
  }

  protected void writeOutputFields( StepMeta parentStepMeta, JsonGenerator json ) throws IOException {
    TransMeta parentTransMeta = parentStepMeta.getParentTransMeta();
    if ( parentTransMeta != null ) {
      try {
        RowMetaInterface stepFields = parentTransMeta.getStepFields( parentStepMeta );
        writeFields( json, stepFields, JSON_PROPERTY_OUTPUT_FIELDS );
      } catch ( KettleStepException e ) {
        LOGGER.warn( Messages.getString( "WARNING.Serialization.Step.OutputFields",
            parentStepMeta.getName() ), e );
      }
    }
  }

  protected void writeFields( JsonGenerator json, RowMetaInterface fields, String arrayObjectName ) throws IOException {
    json.writeArrayFieldStart( arrayObjectName );
    List<ValueMetaInterface> valueMetaInterfaces = fields.getValueMetaList();
    for ( ValueMetaInterface valueMetaInterface : valueMetaInterfaces ) {
      FieldInfo fieldInfo = new FieldInfo( valueMetaInterface );
      json.writeObject( fieldInfo );
    }
    json.writeEndArray();
  }

  protected String getStepType( StepMeta parentStepMeta ) {
    String stepType = null;
    try {
      stepType = PluginRegistry.getInstance().findPluginWithId(
        StepPluginType.class, parentStepMeta.getStepID() ).getName();
    } catch ( Throwable t ) {
      stepType = parentStepMeta.getStepID();
    }
    return stepType;
  }



  protected IStepModifiesFields getStepModifiesFieldsHandler( T meta ) {
    IStepAnalyzerProvider provider = getStepAnalyzerProvider();
    if ( provider == null ) {
      // try to get it from PentahoSystem
      provider = PentahoSystem.get( IStepAnalyzerProvider.class );
    }

    if ( provider != null ) {
      Set<Class<?>> types = new HashSet<Class<?>>();
      types.add( meta.getClass() );
      Set<IStepAnalyzer> analyzers = provider.getAnalyzers( types );
      if ( analyzers != null ) {
        for ( IStepAnalyzer analyzer : analyzers ) {
          if ( analyzer instanceof IStepModifiesFields ) {
            return (IStepModifiesFields) analyzer;
          }
        }
      }
    }
    return new GenericStepMetaAnalyzer();
  }

  protected ExternalResourceConsumerMap getExternalResourceConsumerMap() {
    return map;
  }

  protected void setExternalResourceConsumerMap( ExternalResourceConsumerMap map ) {
    this.map = map;
  }
}
