/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.atomfeed.api.db.hibernate;

import org.hibernate.CallbackException;
import org.hibernate.EmptyInterceptor;
import org.hibernate.HibernateException;
import org.hibernate.Transaction;
import org.hibernate.type.Type;
import org.openmrs.OpenmrsObject;
import org.openmrs.Retireable;
import org.openmrs.Voidable;
import org.openmrs.api.context.Context;
import org.openmrs.module.atomfeed.api.db.EventAction;
import org.openmrs.module.atomfeed.api.db.EventManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Stack;

/**
 * A hibernate {@link Interceptor} implementation, intercepts any database inserts, updates and
 * deletes in a single hibernate session and fires the necessary events. Any changes/inserts/deletes
 * made to the DB that are not made through the application won't be detected by the module. We use
 * a Stack here to handle any nested transactions that may occur within a single thread
 */

@Component("atomfeed-hibernate-interceptor")
public class HibernateEventInterceptor extends EmptyInterceptor {
	
	private static final long serialVersionUID = 1L;

	private static ThreadLocal<Stack<HashSet<OpenmrsObject>>> inserts = new ThreadLocal<>();

	private static ThreadLocal<Stack<HashSet<OpenmrsObject>>> updates = new ThreadLocal<>();

	private static ThreadLocal<Stack<HashSet<OpenmrsObject>>> deletes = new ThreadLocal<>();

	private static ThreadLocal<Stack<HashSet<OpenmrsObject>>> retiredObjects = new ThreadLocal<>();

	private static ThreadLocal<Stack<HashSet<OpenmrsObject>>> unretiredObjects = new ThreadLocal<>();

	private static ThreadLocal<Stack<HashSet<OpenmrsObject>>> voidedObjects = new ThreadLocal<>();

	private static ThreadLocal<Stack<HashSet<OpenmrsObject>>> unvoidedObjects = new ThreadLocal<>();

	@Autowired
	private transient EventManager eventManager;

	/**
	 * @see EmptyInterceptor#afterTransactionBegin(Transaction)
	 */
	@Override
	public void afterTransactionBegin(Transaction tx) {
		
		initializeStackIfNecessary();
		
		inserts.get().push(new HashSet<>());
		updates.get().push(new HashSet<>());
		deletes.get().push(new HashSet<>());
		retiredObjects.get().push(new HashSet<>());
		unretiredObjects.get().push(new HashSet<>());
		voidedObjects.get().push(new HashSet<>());
		unvoidedObjects.get().push(new HashSet<>());
	}
	
	/**
	 * @see EmptyInterceptor#onSave(Object, Serializable, Object[], String[], Type[])
	 */
	@Override
	public boolean onSave(Object entity, Serializable id, Object[] state, String[] propertyNames, Type[] types) {
		if (entity instanceof OpenmrsObject) {
			inserts.get().peek().add((OpenmrsObject) entity);
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
			updates.get().peek().add(object);
			
			if (entity instanceof Retireable) {
				State state = hasPropertyChanged("retired", currentState, previousState, propertyNames);
				if (state == State.UNDO) {
					unretiredObjects.get().peek().add(object);
				} else if (state == State.CHANGED) {
					retiredObjects.get().peek().add(object);
				}
			}
			if (entity instanceof Voidable) {
				State state = hasPropertyChanged("voided", currentState, previousState, propertyNames);
				if (state == State.UNDO) {
					unvoidedObjects.get().peek().add(object);
				} else if (state == State.CHANGED) {
					voidedObjects.get().peek().add(object);
				}
			}
		}
		return false; // tells hibernate that there are no changes made here that
	}
	
	private State hasPropertyChanged(String propertyNameToCheck, Object[] currentState, Object[] previousState,
	                           String[] propertyNames) {
		for (int i = 0; i < propertyNames.length; ++i) {
			if (propertyNameToCheck.equals(propertyNames[i])) {
				boolean previousValue = false;
				if (previousState != null && previousState[i] != null) {
					previousValue = Boolean.valueOf(previousState[i].toString());
				}
				
				boolean currentValue = false;
				if (currentState != null && currentState[i] != null) {
					currentValue = Boolean.valueOf(currentState[i].toString());
				}
				
				if (previousValue == currentValue) {
					return State.NOT_CHANGED;
				} else if (previousValue) {
					return State.UNDO;
				} else {
					return State.CHANGED;
				}
			}
		}
		return State.PROPERTY_NOT_FOUND;
	}
	
	/**
	 * @see EmptyInterceptor#onDelete(Object, Serializable, Object[], String[], Type[])
	 */
	@Override
	public void onDelete(Object entity, Serializable id, Object[] state, String[] propertyNames, Type[] types) {
		if (entity instanceof OpenmrsObject) {
			deletes.get().peek().add((OpenmrsObject) entity);
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
				updates.get().peek().add((OpenmrsObject) owningObject);
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
	public void beforeTransactionCompletion(Transaction tx) {
		try {
            for (OpenmrsObject delete : deletes.get().peek()) {
                eventManager.serveEvent(delete, EventAction.DELETED);
            }
            for (OpenmrsObject insert : inserts.get().peek()) {
                eventManager.serveEvent(insert, EventAction.CREATED);
            }
            for (OpenmrsObject update : updates.get().peek()) {
                eventManager.serveEvent(update, EventAction.UPDATED);
            }
            for (OpenmrsObject retired : retiredObjects.get().peek()) {
                eventManager.serveEvent(retired, EventAction.RETIRED);
            }
            for (OpenmrsObject unretired : unretiredObjects.get().peek()) {
                eventManager.serveEvent(unretired, EventAction.UNRETIRED);
            }
            for (OpenmrsObject voided : voidedObjects.get().peek()) {
                eventManager.serveEvent(voided, EventAction.VOIDED);
            }
            for (OpenmrsObject unvoided : unvoidedObjects.get().peek()) {
                eventManager.serveEvent(unvoided, EventAction.UNVOIDED);
            }
		} finally {
			//cleanup
			inserts.get().pop();
			updates.get().pop();
			deletes.get().pop();
			retiredObjects.get().pop();
			unretiredObjects.get().pop();
			voidedObjects.get().pop();
			unvoidedObjects.get().pop();
			
			removeStackIfEmpty();
		}
	}
	
	private void initializeStackIfNecessary() {
		if (inserts.get() == null) {
			inserts.set(new Stack<>());
		}
		if (updates.get()  == null) {
			updates.set(new Stack<>());
		}
		if (deletes.get()  == null) {
			deletes.set(new Stack<>());
		}
		if (retiredObjects.get()  == null) {
			retiredObjects.set(new Stack<>());
		}
		if (unretiredObjects.get()  == null) {
			unretiredObjects.set(new Stack<>());
		}
		if (voidedObjects.get()  == null) {
			voidedObjects.set(new Stack<>());
		}
		if (unvoidedObjects.get()  == null) {
			unvoidedObjects.set(new Stack<>());
		}
	}
	
	private void removeStackIfEmpty() {
		if (inserts.get().empty()) {
			inserts.remove();
		}
		if (updates.get().empty()) {
			updates.remove();
		}
		if (deletes.get().empty()) {
			deletes.remove();
		}
		if (retiredObjects.get().empty()) {
			retiredObjects.remove();
		}
		if (unretiredObjects.get().empty()) {
			unretiredObjects.remove();
		}
		if (voidedObjects.get().empty()) {
			voidedObjects.remove();
		}
		if (unvoidedObjects.get().empty()) {
			unvoidedObjects.remove();
		}
	}

	public static void setInserts(ThreadLocal<Stack<HashSet<OpenmrsObject>>> inserts) {
		HibernateEventInterceptor.inserts = inserts;
	}

	public static void setUpdates(ThreadLocal<Stack<HashSet<OpenmrsObject>>> updates) {
		HibernateEventInterceptor.updates = updates;
	}

	public static void setDeletes(ThreadLocal<Stack<HashSet<OpenmrsObject>>> deletes) {
		HibernateEventInterceptor.deletes = deletes;
	}

	public static void setRetiredObjects(ThreadLocal<Stack<HashSet<OpenmrsObject>>> retiredObjects) {
		HibernateEventInterceptor.retiredObjects = retiredObjects;
	}

	public static void setUnretiredObjects(ThreadLocal<Stack<HashSet<OpenmrsObject>>> unretiredObjects) {
		HibernateEventInterceptor.unretiredObjects = unretiredObjects;
	}

	public static void setVoidedObjects(ThreadLocal<Stack<HashSet<OpenmrsObject>>> voidedObjects) {
		HibernateEventInterceptor.voidedObjects = voidedObjects;
	}

	public static void setUnvoidedObjects(ThreadLocal<Stack<HashSet<OpenmrsObject>>> unvoidedObjects) {
		HibernateEventInterceptor.unvoidedObjects = unvoidedObjects;
	}
	
	private enum State {
		NOT_CHANGED, CHANGED, UNDO, PROPERTY_NOT_FOUND
	}
}
