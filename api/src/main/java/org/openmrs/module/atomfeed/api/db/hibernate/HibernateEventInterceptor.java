package org.openmrs.module.atomfeed.api.db.hibernate;

import java.io.Serializable;
import java.lang.reflect.Method;

import java.util.HashSet;
import java.util.Stack;

import org.hibernate.CallbackException;
import org.hibernate.EmptyInterceptor;
import org.hibernate.HibernateException;
import org.hibernate.Transaction;
import org.hibernate.type.Type;

import org.openmrs.OpenmrsObject;
import org.openmrs.Retireable;
import org.openmrs.Voidable;
import org.openmrs.api.context.Context;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

/**
 * A hibernate {@link Interceptor} implementation, intercepts any database inserts, updates and
 * deletes in a single hibernate session and fires the necessary events. Any changes/inserts/deletes
 * made to the DB that are not made through the application won't be detected by the module. We use
 * a Stack here to handle any nested transactions that may occur within a single thread
 */
@Component
@Scope("prototype")
public class HibernateEventInterceptor extends EmptyInterceptor {
	
	private static final long serialVersionUID = 1L;
	
	protected static final Logger LOGGER = LoggerFactory.getLogger(HibernateEventInterceptor.class);
	
	private Stack<HashSet<OpenmrsObject>> inserts = new Stack<>();
	
	private Stack<HashSet<OpenmrsObject>> updates = new Stack<>();
	
	private Stack<HashSet<OpenmrsObject>> deletes = new Stack<>();
	
	private Stack<HashSet<OpenmrsObject>> retiredObjects = new Stack<>();
	
	private Stack<HashSet<OpenmrsObject>> unretiredObjects = new Stack<>();
	
	private Stack<HashSet<OpenmrsObject>> voidedObjects = new Stack<>();
	
	private Stack<HashSet<OpenmrsObject>> unvoidedObjects = new Stack<>();
	
	/**
	 * @see EmptyInterceptor#afterTransactionBegin(Transaction)
	 */
	@Override
	public void afterTransactionBegin(Transaction tx) {
		
		initializeStackIfNecessary();
		
		inserts.push(new HashSet<>());
		updates.push(new HashSet<>());
		deletes.push(new HashSet<>());
		retiredObjects.push(new HashSet<>());
		unretiredObjects.push(new HashSet<>());
		voidedObjects.push(new HashSet<>());
		unvoidedObjects.push(new HashSet<>());
	}
	
	/**
	 * @see EmptyInterceptor#onSave(Object, Serializable, Object[], String[], Type[])
	 */
	@Override
	public boolean onSave(Object entity, Serializable id, Object[] state, String[] propertyNames, Type[] types) {
		if (entity instanceof OpenmrsObject) {
			inserts.peek().add((OpenmrsObject) entity);
		}
		
		//tells hibernate that there are no changes made here that
		//need to be propagated to the persistent object and DB
		return false;
	}
	
	/**
	 * @see EmptyInterceptor#onFlushDirty(Object, Serializable, Object[], Object[], String[],
	 *      Type[])
	 */
	@Override
	@SuppressWarnings("parameternumber")
	public boolean onFlushDirty(Object entity, Serializable id, Object[] currentState, Object[] previousState,
	        String[] propertyNames, Type[] types) {
		
		if (entity instanceof OpenmrsObject) {
			OpenmrsObject object = (OpenmrsObject) entity;
			updates.peek().add(object);
			//Fire events for retired/unretired and voided/unvoided objects
			if (entity instanceof Retireable || entity instanceof Voidable) {
				for (int i = 0; i < propertyNames.length; i++) {
					String auditableProperty = (entity instanceof Retireable) ? "retired" : "voided";
					if (auditableProperty.equals(propertyNames[i])) {
						boolean previousValue = false;
						if (previousState != null && previousState[i] != null) {
							previousValue = Boolean.valueOf(previousState[i].toString());
						}
						
						boolean currentValue = false;
						if (currentState != null && currentState[i] != null) {
							currentValue = Boolean.valueOf(currentState[i].toString());
						}
						
						addToRetriedOrVoided(object, auditableProperty, previousValue, currentValue);
						break;
					}
				}
			}
		}
		
		return false;
	}
	
	private void addToRetriedOrVoided(OpenmrsObject object, String auditableProperty, boolean previousValue,
	        boolean currentValue) {
		if (previousValue != currentValue) {
			if ("retired".equals(auditableProperty)) {
				if (previousValue) {
					unretiredObjects.peek().add(object);
				} else {
					retiredObjects.peek().add(object);
				}
			} else {
				if (previousValue) {
					unvoidedObjects.peek().add(object);
				} else {
					voidedObjects.peek().add(object);
				}
			}
		}
	}
	
	/**
	 * @see EmptyInterceptor#onDelete(Object, Serializable, Object[], String[], Type[])
	 */
	@Override
	public void onDelete(Object entity, Serializable id, Object[] state, String[] propertyNames, Type[] types) {
		if (entity instanceof OpenmrsObject) {
			deletes.peek().add((OpenmrsObject) entity);
		}
	}
	
	/**
	 * @see EmptyInterceptor#onCollectionUpdate(Object, Serializable)
	 */
	@Override
	public void onCollectionUpdate(Object collection, Serializable key) throws CallbackException {
		if (collection != null) {
			//If a collection element has been added/removed, fire an update event for the parent entity
			Object owningObject = getOwner(collection);
			if (owningObject instanceof OpenmrsObject) {
				updates.peek().add((OpenmrsObject) owningObject);
			}
		}
	}
	
	/**
	 * Gets the owning object of a persistent collection
	 * 
	 * @param collection the persistent collection
	 * @return the owning object
	 */
	private Object getOwner(Object collection) {
		Class<?> collectionClass;
		try {
			collectionClass = Context.loadClass("org.hibernate.collection.PersistentCollection");
		} catch (ClassNotFoundException oe) {
			//We are running against openmrs core 2.0 or later where it's a later hibernate version
			try {
				collectionClass = Context.loadClass("org.hibernate.collection.spi.PersistentCollection");
			} catch (ClassNotFoundException ie) {
				throw new HibernateException(ie);
			}
		}
		
		Method method = ReflectionUtils.findMethod(collectionClass, "getOwner");
		
		return ReflectionUtils.invokeMethod(method, collection);
	}
	
	/**
	 * @see EmptyInterceptor#afterTransactionCompletion(Transaction)
	 */
	@Override
	@SuppressWarnings("PMD.EmptyTryBlock")
	public void afterTransactionCompletion(Transaction tx) {
		try {
			/*
			if (tx.wasCommitted()) {
				for (OpenmrsObject delete : deletes.peek()) {
					// TODO: add action
				}
				for (OpenmrsObject insert : inserts.peek()) {
					// TODO: add action
				}
				for (OpenmrsObject update : updates.peek()) {
					// TODO: add action
				}
				for (OpenmrsObject retired : retiredObjects.peek()) {
					// TODO: add action
				}
				for (OpenmrsObject unretired : unretiredObjects.peek()) {
					// TODO: add action
				}
				for (OpenmrsObject voided : voidedObjects.peek()) {
					// TODO: add action
				}
				for (OpenmrsObject unvoided : unvoidedObjects.peek()) {
					// TODO: add action
				}
			}
			*/
		} finally {
			//cleanup
			inserts.pop();
			updates.pop();
			deletes.pop();
			retiredObjects.pop();
			unretiredObjects.pop();
			voidedObjects.pop();
			unvoidedObjects.pop();
			
			removeStackIfEmpty();
		}
	}
	
	private void initializeStackIfNecessary() {
		if (inserts == null) {
			inserts = new Stack<HashSet<OpenmrsObject>>();
		}
		if (updates == null) {
			updates = new Stack<HashSet<OpenmrsObject>>();
		}
		if (deletes == null) {
			deletes = new Stack<HashSet<OpenmrsObject>>();
		}
		if (retiredObjects == null) {
			retiredObjects = new Stack<HashSet<OpenmrsObject>>();
		}
		if (unretiredObjects == null) {
			unretiredObjects = new Stack<HashSet<OpenmrsObject>>();
		}
		if (voidedObjects == null) {
			voidedObjects = new Stack<HashSet<OpenmrsObject>>();
		}
		if (unvoidedObjects == null) {
			unvoidedObjects = new Stack<HashSet<OpenmrsObject>>();
		}
	}
	
	private void removeStackIfEmpty() {
		if (inserts.empty()) {
			inserts = null;
		}
		if (updates.empty()) {
			updates = null;
		}
		if (deletes.empty()) {
			deletes = null;
		}
		if (retiredObjects.empty()) {
			retiredObjects = null;
		}
		if (unretiredObjects.empty()) {
			unretiredObjects = null;
		}
		if (voidedObjects.empty()) {
			voidedObjects = null;
		}
		if (unvoidedObjects.empty()) {
			unvoidedObjects = null;
		}
	}
}
