/**
 */
package ccmm.impl;

import ccmm.CcmmPackage;
import ccmm.Contact;
import ccmm.Marketing;

import java.util.Collection;

import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;

import org.eclipse.emf.ecore.util.EObjectEList;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Marketing</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link ccmm.impl.MarketingImpl#getEntityID <em>Entity ID</em>}</li>
 *   <li>{@link ccmm.impl.MarketingImpl#getEntityName <em>Entity Name</em>}</li>
 *   <li>{@link ccmm.impl.MarketingImpl#getEntityDescription <em>Entity Description</em>}</li>
 *   <li>{@link ccmm.impl.MarketingImpl#getContact <em>Contact</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class MarketingImpl extends MinimalEObjectImpl.Container implements Marketing {
	/**
	 * The default value of the '{@link #getEntityID() <em>Entity ID</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getEntityID()
	 * @generated
	 * @ordered
	 */
	protected static final String ENTITY_ID_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getEntityID() <em>Entity ID</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getEntityID()
	 * @generated
	 * @ordered
	 */
	protected String entityID = ENTITY_ID_EDEFAULT;

	/**
	 * The default value of the '{@link #getEntityName() <em>Entity Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getEntityName()
	 * @generated
	 * @ordered
	 */
	protected static final String ENTITY_NAME_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getEntityName() <em>Entity Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getEntityName()
	 * @generated
	 * @ordered
	 */
	protected String entityName = ENTITY_NAME_EDEFAULT;

	/**
	 * The default value of the '{@link #getEntityDescription() <em>Entity Description</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getEntityDescription()
	 * @generated
	 * @ordered
	 */
	protected static final String ENTITY_DESCRIPTION_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getEntityDescription() <em>Entity Description</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getEntityDescription()
	 * @generated
	 * @ordered
	 */
	protected String entityDescription = ENTITY_DESCRIPTION_EDEFAULT;

	/**
	 * The cached value of the '{@link #getContact() <em>Contact</em>}' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getContact()
	 * @generated
	 * @ordered
	 */
	protected EList<Contact> contact;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public MarketingImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return CcmmPackage.Literals.MARKETING;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getEntityID() {
		return entityID;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setEntityID(String newEntityID) {
		String oldEntityID = entityID;
		entityID = newEntityID;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, CcmmPackage.MARKETING__ENTITY_ID, oldEntityID, entityID));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getEntityName() {
		return entityName;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setEntityName(String newEntityName) {
		String oldEntityName = entityName;
		entityName = newEntityName;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, CcmmPackage.MARKETING__ENTITY_NAME, oldEntityName, entityName));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getEntityDescription() {
		return entityDescription;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setEntityDescription(String newEntityDescription) {
		String oldEntityDescription = entityDescription;
		entityDescription = newEntityDescription;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, CcmmPackage.MARKETING__ENTITY_DESCRIPTION, oldEntityDescription, entityDescription));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<Contact> getContact() {
		if (contact == null) {
			contact = new EObjectEList<Contact>(Contact.class, this, CcmmPackage.MARKETING__CONTACT);
		}
		return contact;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case CcmmPackage.MARKETING__ENTITY_ID:
				return getEntityID();
			case CcmmPackage.MARKETING__ENTITY_NAME:
				return getEntityName();
			case CcmmPackage.MARKETING__ENTITY_DESCRIPTION:
				return getEntityDescription();
			case CcmmPackage.MARKETING__CONTACT:
				return getContact();
		}
		return super.eGet(featureID, resolve, coreType);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void eSet(int featureID, Object newValue) {
		switch (featureID) {
			case CcmmPackage.MARKETING__ENTITY_ID:
				setEntityID((String)newValue);
				return;
			case CcmmPackage.MARKETING__ENTITY_NAME:
				setEntityName((String)newValue);
				return;
			case CcmmPackage.MARKETING__ENTITY_DESCRIPTION:
				setEntityDescription((String)newValue);
				return;
			case CcmmPackage.MARKETING__CONTACT:
				getContact().clear();
				getContact().addAll((Collection<? extends Contact>)newValue);
				return;
		}
		super.eSet(featureID, newValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void eUnset(int featureID) {
		switch (featureID) {
			case CcmmPackage.MARKETING__ENTITY_ID:
				setEntityID(ENTITY_ID_EDEFAULT);
				return;
			case CcmmPackage.MARKETING__ENTITY_NAME:
				setEntityName(ENTITY_NAME_EDEFAULT);
				return;
			case CcmmPackage.MARKETING__ENTITY_DESCRIPTION:
				setEntityDescription(ENTITY_DESCRIPTION_EDEFAULT);
				return;
			case CcmmPackage.MARKETING__CONTACT:
				getContact().clear();
				return;
		}
		super.eUnset(featureID);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean eIsSet(int featureID) {
		switch (featureID) {
			case CcmmPackage.MARKETING__ENTITY_ID:
				return ENTITY_ID_EDEFAULT == null ? entityID != null : !ENTITY_ID_EDEFAULT.equals(entityID);
			case CcmmPackage.MARKETING__ENTITY_NAME:
				return ENTITY_NAME_EDEFAULT == null ? entityName != null : !ENTITY_NAME_EDEFAULT.equals(entityName);
			case CcmmPackage.MARKETING__ENTITY_DESCRIPTION:
				return ENTITY_DESCRIPTION_EDEFAULT == null ? entityDescription != null : !ENTITY_DESCRIPTION_EDEFAULT.equals(entityDescription);
			case CcmmPackage.MARKETING__CONTACT:
				return contact != null && !contact.isEmpty();
		}
		return super.eIsSet(featureID);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String toString() {
		if (eIsProxy()) return super.toString();

		StringBuffer result = new StringBuffer(super.toString());
		result.append(" (entityID: ");
		result.append(entityID);
		result.append(", entityName: ");
		result.append(entityName);
		result.append(", entityDescription: ");
		result.append(entityDescription);
		result.append(')');
		return result.toString();
	}

} //MarketingImpl
