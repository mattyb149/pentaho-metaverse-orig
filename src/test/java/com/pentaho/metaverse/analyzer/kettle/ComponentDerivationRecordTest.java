package com.pentaho.metaverse.analyzer.kettle;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class ComponentDerivationRecordTest {

  ComponentDerivationRecord record;

  @Before
  public void setUp() throws Exception {
    record = new ComponentDerivationRecord( "myRecord" );
  }

  @Test
  public void testNonDefaultConstructor() {
    record = new ComponentDerivationRecord( "originalRecord", "myRecord" );
  }

  @Test
  public void testGetEntityName() throws Exception {
    assertEquals( "myRecord", record.getChangedEntityName() );
    assertNull( new ComponentDerivationRecord().getChangedEntityName() );

  }

  @Test
  public void testSetEntityName() throws Exception {
    assertEquals( "myRecord", record.getChangedEntityName() );
    record.setChangedEntityName( "newName" );
    assertEquals( "newName", record.getChangedEntityName() );
  }

  @Test
  public void testGetSetOriginalEntityName() throws Exception {
    assertEquals( "myRecord", record.getChangedEntityName() );
    record.setOriginalEntityName( "originalName" );
    assertEquals( "originalName", record.getOriginalEntityName() );
  }

  @Test
  public void testGetOperations() throws Exception {
    Map<String, List<String>> operations = record.getOperations();
    assertNotNull( operations );
    assertTrue( operations.isEmpty() );
  }

  @Test
  public void testPutOperation() throws Exception {
    Map<String, List<String>> operations = record.getOperations();
    assertNotNull( operations );
    assertTrue( operations.isEmpty() );
    List<String> operands = Arrays.asList( "testOperand1", "testOperand2" );
    record.putOperation( "testOperation", operands );
    operations = record.getOperations();
    assertNotNull( operations );
    List<String> checkOperands = operations.get( "testOperation" );
    assertNotNull( checkOperands );
    assertEquals( 2, checkOperands.size() );
    assertTrue( "testOperand1 not in operands!", checkOperands.contains( "testOperand1" ) );
    assertTrue( "testOperand2 not in operands!", checkOperands.contains( "testOperand2" ) );
  }

  @Test
  public void testPutOperationNull() throws Exception {
    record.operations = null;

    List<String> operands = Arrays.asList( "testOperand1", "testOperand2" );
    record.putOperation( "testOperation", operands );
    Map<String, List<String>> operations = record.getOperations();
    assertNotNull( operations );
    List<String> checkOperands = operations.get( "testOperation" );
    assertNotNull( checkOperands );
    assertEquals( 2, checkOperands.size() );
    assertTrue( "testOperand1 not in operands!", checkOperands.contains( "testOperand1" ) );
    assertTrue( "testOperand2 not in operands!", checkOperands.contains( "testOperand2" ) );
  }

  @Test
  public void testPutOperationNullOperation() throws Exception {
    Map<String, List<String>> operations = record.getOperations();
    assertNotNull( operations );
    assertTrue( operations.isEmpty() );
    List<String> operands = Arrays.asList( "testOperand1", "testOperand2" );
    record.putOperation( null, operands );
    operations = record.getOperations();
    assertNotNull( operations );
    List<String> checkOperands = operations.get( "testOperation" );
    assertNull( checkOperands );
  }

  @Test
  public void testPutOperationNullOperands() throws Exception {
    Map<String, List<String>> operations = record.getOperations();
    assertNotNull( operations );
    assertTrue( operations.isEmpty() );
    record.putOperation( "testOperation", null );
    operations = record.getOperations();
    assertNotNull( operations );
    List<String> checkOperands = operations.get( "testOperation" );
    assertNull( checkOperands );
  }

  @Test
  public void testAddOperand() throws Exception {
    record.operations = null;
    record.addOperand( "testOperation", "testOperand" );
    Map<String, List<String>> operations = record.getOperations();
    assertNotNull( operations );
    List<String> checkOperands = operations.get( "testOperation" );
    assertNotNull( checkOperands );
    assertEquals( 1, checkOperands.size() );
    assertTrue( "testOperand not in operands!", checkOperands.contains( "testOperand" ) );
  }

  @Test
  public void testAddOperandNullOperand() throws Exception {
    Map<String, List<String>> operations = record.getOperations();
    assertNotNull( operations );
    assertTrue( operations.isEmpty() );
    record.addOperand( "testOperation", null );
    List<String> checkOperands = record.getOperations().get( "testOperation" );
    assertNull( checkOperands );
  }

  @Test
  public void testHasDelta() throws Exception {
    Map<String, List<String>> operations = record.getOperations();
    assertNotNull( operations );
    assertTrue( operations.isEmpty() );
    assertFalse( "This record should not say it has been changed!", record.hasDelta() );
    record.addOperand( "testOperation", "testOperand" );
    assertTrue( "This record should say it has been changed!", record.hasDelta() );
  }

  @Test
  public void testToString() throws Exception {
    Map<String, List<String>> operations = record.getOperations();
    assertNotNull( operations );
    assertTrue( operations.isEmpty() );
    assertEquals( record.toString(), "{}" );
    record.addOperand( "testOperation", "testOperand" );
    assertEquals( record.toString(), "{\"testOperation\":[\"testOperand\"]}" );
  }

  @Test
  public void testGetOperationsWithNullOperations() {
    record.operations = null;
    assertNotNull( record.getOperations() );
    assertTrue( record.getOperations().isEmpty() );
  }

}
