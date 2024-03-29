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

package com.pentaho.metaverse.analyzer.kettle.step.textfileinput;

import com.pentaho.dictionary.DictionaryConst;
import com.pentaho.metaverse.analyzer.kettle.step.BaseStepAnalyzer;
import com.pentaho.metaverse.impl.MetaverseComponentDescriptor;
import org.pentaho.di.core.Const;
import org.pentaho.di.trans.step.BaseStepMeta;
import org.pentaho.di.trans.steps.textfileinput.TextFileInputField;
import org.pentaho.di.trans.steps.textfileinput.TextFileInputMeta;
import org.pentaho.platform.api.metaverse.IMetaverseComponentDescriptor;
import org.pentaho.platform.api.metaverse.IMetaverseNode;
import org.pentaho.platform.api.metaverse.MetaverseAnalyzerException;
import org.pentaho.platform.api.metaverse.MetaverseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;

/**
 * The TextFileInputStepAnalyzer is responsible for providing nodes and links (i.e. relationships) between itself and
 * other metaverse entities
 */
public class TextFileInputStepAnalyzer extends BaseStepAnalyzer<TextFileInputMeta> {
  private Logger log = LoggerFactory.getLogger( TextFileInputStepAnalyzer.class );

  @Override
  public IMetaverseNode analyze( IMetaverseComponentDescriptor descriptor, TextFileInputMeta textFileInputMeta )
    throws MetaverseAnalyzerException {

    // do the common analysis for all step
    IMetaverseNode node = super.analyze( descriptor, textFileInputMeta );

    // add the fields as nodes, add the links too
    TextFileInputField[] fields = textFileInputMeta.getInputFields();
    if ( fields != null ) {
      for ( TextFileInputField field : fields ) {
        String fieldName = field.getName();
        IMetaverseComponentDescriptor fileFieldDescriptor = new MetaverseComponentDescriptor(
          fieldName,
          DictionaryConst.NODE_TYPE_FILE_FIELD,
          descriptor.getNamespace(),
          descriptor.getContext() );
        IMetaverseNode fieldNode = createNodeFromDescriptor( fileFieldDescriptor );
        metaverseBuilder.addNode( fieldNode );

        // Get the stream field output from this step. It should've already been created when we called super.analyze()
        IMetaverseComponentDescriptor transFieldDescriptor = getStepFieldOriginDescriptor( descriptor, fieldName );
        IMetaverseNode outNode = createNodeFromDescriptor( transFieldDescriptor );

        metaverseBuilder.addLink( fieldNode, DictionaryConst.LINK_POPULATES, outNode );

        // add a link from the fileField to the text file input step node
        metaverseBuilder.addLink( node, DictionaryConst.LINK_USES, fieldNode );
      }
    }

    if ( textFileInputMeta.isAcceptingFilenames() ) {
      String acceptingFieldName = textFileInputMeta.getAcceptingField();
      IMetaverseComponentDescriptor transFieldDescriptor = getPrevStepFieldOriginDescriptor( descriptor,
        acceptingFieldName );
      IMetaverseNode acceptingFieldNode = createNodeFromDescriptor( transFieldDescriptor );

      // add a link from the fileField to the text file input step node
      metaverseBuilder.addLink( node, DictionaryConst.LINK_USES, acceptingFieldNode );
    } else {
      String[] fileNames = parentTransMeta.environmentSubstitute( textFileInputMeta.getFileName() );

      // add a link from the file(s) being read to the step
      for ( String fileName : fileNames ) {
        if ( !Const.isEmpty( fileName ) ) {
          try {
            // first add the node for the file
            IMetaverseNode textFileNode = createFileNode( fileName, descriptor );
            metaverseBuilder.addNode( textFileNode );

            metaverseBuilder.addLink( textFileNode, DictionaryConst.LINK_READBY, node );
          } catch ( MetaverseException e ) {
            log.error( e.getMessage(), e );
          }
        }
      }
    }

    return node;
  }

  @Override
  public Set<Class<? extends BaseStepMeta>> getSupportedSteps() {
    return new HashSet<Class<? extends BaseStepMeta>>() {
      {
        add( TextFileInputMeta.class );
      }
    };
  }
}
