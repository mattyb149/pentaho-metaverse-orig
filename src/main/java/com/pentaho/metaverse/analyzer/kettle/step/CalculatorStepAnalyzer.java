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

package com.pentaho.metaverse.analyzer.kettle.step;

import com.pentaho.dictionary.DictionaryConst;
import com.pentaho.metaverse.analyzer.kettle.ComponentDerivationRecord;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.di.core.row.value.ValueMetaFactory;
import org.pentaho.di.trans.step.BaseStepMeta;
import org.pentaho.di.trans.steps.calculator.CalculatorMeta;
import org.pentaho.platform.api.metaverse.IMetaverseComponentDescriptor;
import org.pentaho.platform.api.metaverse.IMetaverseNode;
import org.pentaho.platform.api.metaverse.MetaverseAnalyzerException;

import java.util.HashSet;
import java.util.Set;

/**
 * The CalculatorStepAnalyzer is responsible for providing nodes and links (i.e. relationships) between for the
 * fields operated on by Select Values steps.
 */
public class CalculatorStepAnalyzer extends BaseStepAnalyzer<CalculatorMeta> {

  /*
   * (non-Javadoc)
   * 
   * @see org.pentaho.platform.api.metaverse.IAnalyzer#analyze(IMetaverseComponentDescriptor,java.lang.Object)
   */
  @Override
  public IMetaverseNode analyze(
      IMetaverseComponentDescriptor descriptor, CalculatorMeta calculatorMeta ) throws MetaverseAnalyzerException {

    // Do common analysis for all steps
    IMetaverseNode node = super.analyze( descriptor, calculatorMeta );

    IMetaverseNode fieldNode = null;
    String inputFieldName = null;
    String outputFieldName = null;
    ComponentDerivationRecord changeRecord = null;

    return rootNode;
  }

  @Override
  public Set<Class<? extends BaseStepMeta>> getSupportedSteps() {
    return new HashSet<Class<? extends BaseStepMeta>>() {
      {
        add( CalculatorMeta.class );
      }
    };
  }
}
