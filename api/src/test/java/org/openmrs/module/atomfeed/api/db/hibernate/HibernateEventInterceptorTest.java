package org.openmrs.module.atomfeed.api.db.hibernate;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.OpenmrsObject;
import org.openmrs.test.BaseModuleContextSensitiveTest;

import java.util.HashSet;
import java.util.Stack;

import static org.mockito.Mockito.mock;

public class HibernateEventInterceptorTest extends BaseModuleContextSensitiveTest {
	
	// @Mock
	// private EventManager eventManager;
	
	private ThreadLocal<Stack<HashSet<OpenmrsObject>>> inserts;
	
	private ThreadLocal<Stack<HashSet<OpenmrsObject>>> updates;
	
	private ThreadLocal<Stack<HashSet<OpenmrsObject>>> deletes;
	
	private ThreadLocal<Stack<HashSet<OpenmrsObject>>> retiredObjects;
	
	private ThreadLocal<Stack<HashSet<OpenmrsObject>>> unretiredObjects;
	
	private ThreadLocal<Stack<HashSet<OpenmrsObject>>> voidedObjects;
	
	private ThreadLocal<Stack<HashSet<OpenmrsObject>>> unvoidedObjects;
	
	private HibernateEventInterceptor hibernateEventInterceptor = new HibernateEventInterceptor();
	
	@Before
	public void setup() {
		initVariables();
	}
	
	@Test
	public void afterTransactionBegin_shouldInitStackWithOneEmptyHashSet() {
		hibernateEventInterceptor.afterTransactionBegin(null);
		
		Assert.assertEquals(1, inserts.get().size());
		Assert.assertEquals(1, updates.get().size());
		Assert.assertEquals(1, deletes.get().size());
		Assert.assertEquals(1, retiredObjects.get().size());
		Assert.assertEquals(1, unretiredObjects.get().size());
		Assert.assertEquals(1, voidedObjects.get().size());
		Assert.assertEquals(1, unvoidedObjects.get().size());
		
		Assert.assertEquals(0, inserts.get().peek().size());
		Assert.assertEquals(0, updates.get().peek().size());
		Assert.assertEquals(0, deletes.get().peek().size());
		Assert.assertEquals(0, retiredObjects.get().peek().size());
		Assert.assertEquals(0, unretiredObjects.get().peek().size());
		Assert.assertEquals(0, voidedObjects.get().peek().size());
		Assert.assertEquals(0, unvoidedObjects.get().peek().size());
	}
	
	@Test
	public void onSave_shouldAddOpenmrsObjectToInsertSet() {
		final OpenmrsObject expected = mock(OpenmrsObject.class);
		hibernateEventInterceptor.afterTransactionBegin(null); // init structure
		
		hibernateEventInterceptor.onSave(expected, null, null, null, null);
		Assert.assertTrue(inserts.get().peek().contains(expected));
	}
	
	@Test
	public void onSave_shouldNotAddNotOpenmrsObjectToInsertSet() {
		final Object notOpenmrsObject = mock(Object.class);
		hibernateEventInterceptor.afterTransactionBegin(null); // init structure
		
		hibernateEventInterceptor.onSave(notOpenmrsObject, null, null, null, null);
		Assert.assertFalse(inserts.get().peek().contains(notOpenmrsObject));
	}
	
	@Test
	public void onFlushDirty() {
	}
	
	@Test
	public void onDelete_shouldAddOpenmrsObjectToDeleteSet() {
		final OpenmrsObject expected = mock(OpenmrsObject.class);
		hibernateEventInterceptor.afterTransactionBegin(null); // init structure

		hibernateEventInterceptor.onDelete(expected, null, null, null, null);
		Assert.assertTrue(deletes.get().peek().contains(expected));
	}

	@Test
	public void onDelete_shouldNotAddNotOpenmrsObjectToDeleteSet() {
		final Object notOpenmrsObject = mock(Object.class);
		hibernateEventInterceptor.afterTransactionBegin(null); // init structure

		hibernateEventInterceptor.onSave(notOpenmrsObject, null, null, null, null);
		Assert.assertFalse(deletes.get().peek().contains(notOpenmrsObject));
	}

	@Test
	public void onCollectionUpdate() {
	}
	
	@Test
	public void afterTransactionCompletion() {
	}
	
	private void initVariables() {
        inserts = new ThreadLocal<>();
        updates = new ThreadLocal<>();
        deletes = new ThreadLocal<>();
        retiredObjects = new ThreadLocal<>();
        unretiredObjects = new ThreadLocal<>();
        voidedObjects = new ThreadLocal<>();
        unvoidedObjects = new ThreadLocal<>();

		HibernateEventInterceptor.setInserts(inserts);
		HibernateEventInterceptor.setUpdates(updates);
		HibernateEventInterceptor.setDeletes(deletes);
		HibernateEventInterceptor.setRetiredObjects(retiredObjects);
		HibernateEventInterceptor.setUnretiredObjects(unretiredObjects);
		HibernateEventInterceptor.setVoidedObjects(voidedObjects);
		HibernateEventInterceptor.setUnvoidedObjects(unvoidedObjects);
    }
}
