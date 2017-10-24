package org.openmrs.module.atomfeed.api.db.hibernate;

import org.hibernate.Transaction;
import org.hibernate.collection.spi.PersistentCollection;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openmrs.OpenmrsObject;
import org.openmrs.module.atomfeed.api.db.EventAction;
import org.openmrs.module.atomfeed.api.db.EventManager;

import java.util.HashSet;
import java.util.Stack;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class HibernateEventInterceptorTest {
	
	@Mock
	private EventManager eventManager;
	
	@Mock
	private OpenmrsObject openmrsObject;
	
	@Mock
	private Object notOpenmrsObject;
	
	private ThreadLocal<Stack<HashSet<OpenmrsObject>>> inserts;
	
	private ThreadLocal<Stack<HashSet<OpenmrsObject>>> updates;
	
	private ThreadLocal<Stack<HashSet<OpenmrsObject>>> deletes;
	
	private ThreadLocal<Stack<HashSet<OpenmrsObject>>> retiredObjects;
	
	private ThreadLocal<Stack<HashSet<OpenmrsObject>>> unretiredObjects;
	
	private ThreadLocal<Stack<HashSet<OpenmrsObject>>> voidedObjects;
	
	private ThreadLocal<Stack<HashSet<OpenmrsObject>>> unvoidedObjects;
	
	@InjectMocks
	private HibernateEventInterceptor hibernateEventInterceptor = new HibernateEventInterceptor();
	
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
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
		hibernateEventInterceptor.afterTransactionBegin(null); // init structure
		
		hibernateEventInterceptor.onDelete(openmrsObject, null, null, null, null);
		Assert.assertTrue(deletes.get().peek().contains(openmrsObject));
	}
	
	@Test
	public void onDelete_shouldNotAddNotOpenmrsObjectToDeleteSet() {
		hibernateEventInterceptor.afterTransactionBegin(null); // init structure
		
		hibernateEventInterceptor.onSave(notOpenmrsObject, null, null, null, null);
		Assert.assertFalse(deletes.get().peek().contains(notOpenmrsObject));
	}
	
	@Test
	public void onCollectionUpdate_shouldAddOwnerOfUpdatedCollectionToUpdateSet() {
		final OpenmrsObject expectedCollectionOwner = openmrsObject;
		
		final PersistentCollection updatedCollection = mock(PersistentCollection.class);
		when(updatedCollection.getOwner()).thenReturn(expectedCollectionOwner);
		
		hibernateEventInterceptor.afterTransactionBegin(null); // init structure
		
		hibernateEventInterceptor.onCollectionUpdate(updatedCollection, null);
		Assert.assertTrue(updates.get().peek().contains(expectedCollectionOwner));
	}
	
	@Test
	public void onCollectionUpdate_shouldNotAddOwnerOfUpdatedCollectionWhenTheyAreNotOpenmrsObject() {
		final Object collectionOwner = notOpenmrsObject;
		
		final PersistentCollection updatedCollection = mock(PersistentCollection.class);
		when(updatedCollection.getOwner()).thenReturn(collectionOwner);
		
		hibernateEventInterceptor.afterTransactionBegin(null); // init structure
		
		hibernateEventInterceptor.onCollectionUpdate(updatedCollection, null);
		Assert.assertFalse(updates.get().peek().contains(collectionOwner));
	}
	
	@Test
	public void afterTransactionCompletion_shouldCallServeEventMethodInEventManagerOnEveryObjectInStack() {
		hibernateEventInterceptor.afterTransactionBegin(null); // init structure
		
		addOpenmrsObjectToAllSets();
		
		final Transaction transaction = mock(Transaction.class);
		when(transaction.wasCommitted()).thenReturn(true);
		
		hibernateEventInterceptor.afterTransactionCompletion(transaction);
		
		verify(eventManager, times(1)).serveEvent(eq(openmrsObject), eq(EventAction.CREATED));
		verify(eventManager, times(1)).serveEvent(eq(openmrsObject), eq(EventAction.DELETED));
		verify(eventManager, times(1)).serveEvent(eq(openmrsObject), eq(EventAction.UPDATED));
		verify(eventManager, times(1)).serveEvent(eq(openmrsObject), eq(EventAction.RETIRED));
		verify(eventManager, times(1)).serveEvent(eq(openmrsObject), eq(EventAction.UNRETIRED));
		verify(eventManager, times(1)).serveEvent(eq(openmrsObject), eq(EventAction.VOIDED));
		verify(eventManager, times(1)).serveEvent(eq(openmrsObject), eq(EventAction.UNVOIDED));
	}
	
	@Test
	public void afterTransactionCompletion_shouldNotServeEventsWhenTransationWasNotCommited() {
		hibernateEventInterceptor.afterTransactionBegin(null); // init structure
		
		addOpenmrsObjectToAllSets();
		
		final Transaction transaction = mock(Transaction.class);
		when(transaction.wasCommitted()).thenReturn(false);
		
		hibernateEventInterceptor.afterTransactionCompletion(transaction);
		
		verify(eventManager, never()).serveEvent(any(), any());
	}
	
	@Test
	public void afterTransactionCompletion_shouldCleanupSetsAtTheEndOfMethod() {
		hibernateEventInterceptor.afterTransactionBegin(null); // init structure
		
		addOpenmrsObjectToAllSets();
		
		final Transaction transaction = mock(Transaction.class);
		when(transaction.wasCommitted()).thenReturn(false);
		
		hibernateEventInterceptor.afterTransactionCompletion(transaction);
		
		Assert.assertNull(inserts.get());
		Assert.assertNull(inserts.get());
		Assert.assertNull(inserts.get());
		Assert.assertNull(inserts.get());
		Assert.assertNull(inserts.get());
		Assert.assertNull(inserts.get());
		Assert.assertNull(inserts.get());
	}
	
	private void addOpenmrsObjectToAllSets() {
		inserts.get().peek().add(openmrsObject);
		deletes.get().peek().add(openmrsObject);
		updates.get().peek().add(openmrsObject);
		retiredObjects.get().peek().add(openmrsObject);
		unretiredObjects.get().peek().add(openmrsObject);
		voidedObjects.get().peek().add(openmrsObject);
		unvoidedObjects.get().peek().add(openmrsObject);
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
