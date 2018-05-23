/*
 * ManageRegister.java -- Gestiona la base de datos de ....
 * Copyright (C) 2016  Alfredo Valero Rodríguez, Javier Criado Rodríguez and Jesús Vallecillos Ruíz
 *
 * ManageRegister.java is part of COScore Community.
 * 
 * COScore Community is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Foobar.  If not, see <http://www.gnu.org/licenses/gpl.html> or
 * see  <https://github.com/acgtic211/COScore-Community>.  Or write to
 * the Free Software Foundation, I51 Franklin Street, Fifth Floor,
 * Boston, MA 02110-1335, USA.
 *
 *  Authors: Alfredo Valero Rodríguez  Software Developer 
 *           Javier Criado Rodríguez   Doctor/Researcher/Software Developer
 *           Jesús Vallecillos Ruíz    Pre-doctoral scholarship holders/Researcher/Software Developer
 *    Group: ACG 		               Applied Computing Group
 * Internet: http://acg.ual.es/        
 *   E-mail: acg.tic211@ual.es        
 *   Adress: Edif. Científico Técnico, CITE-III
 *           Universidad de Almería
 *           Almeria, España
 *           04120
*/
package es.ual.acg.cos.controllers;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;
import javax.ejb.Startup;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceImpl;
import org.eclipse.emf.teneo.PersistenceOptions;
import org.eclipse.emf.teneo.hibernate.HbDataStore;
import org.eclipse.emf.teneo.hibernate.HbHelper;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Environment;
import org.jboss.logging.Logger;
import org.xml.sax.InputSource;

import acmm.AbstractComponentSpecification;
import acmm.AcmmPackage;
import architectural_metamodel.Architectural_metamodelFactory;
import architectural_metamodel.RuntimeProperty;
import ccmm.impl.PackagingImpl;
import ccmm.CcmmPackage;
import ccmm.ConcreteComponentSpecification;
import ccmm.Contact;
import ccmm.ControllerInterface;
import ccmm.Dependency;
import ccmm.ExtraFunctional;
import ccmm.Functional;
import ccmm.Implementation;
import ccmm.Input;
import ccmm.Location;
import ccmm.Marketing;
import ccmm.Operation;
import ccmm.Packaging;
import ccmm.PlatformType;
import ccmm.PortType;
import ccmm.Property;
import ccmm.PropertyID;
import ccmm.ProvidedInterface;
import ccmm.RepositoryType;
import ccmm.RequiredInterface;
import ccmm.Version;
import ccmm.WSDLSpecification;
import ccmm.impl.ConcreteComponentSpecificationImpl;
import ccmm.impl.ContactImpl;
import ccmm.impl.ControllerInterfaceImpl;
import ccmm.impl.DependencyImpl;
import ccmm.impl.ExtraFunctionalImpl;
import ccmm.impl.FunctionalImpl;
import ccmm.impl.ImplementationImpl;
import ccmm.impl.InputImpl;
import ccmm.impl.LocationImpl;
import ccmm.impl.MarketingImpl;
import ccmm.impl.OperationImpl;
import ccmm.impl.PortTypeImpl;
import ccmm.impl.PropertyImpl;
import ccmm.impl.ProvidedInterfaceImpl;
import ccmm.impl.RequiredInterfaceImpl;
import ccmm.impl.VersionImpl;
import ccmm.impl.WSDLSpecificationImpl;

@Singleton
@Startup
@Lock(LockType.READ)
public class ManageComponentSpecifications
{
	private HbDataStore dataStoreAC;
	private HbDataStore dataStoreCC;
	private boolean dataStoreACOn = false;
	private boolean dataStoreCCOn = true;
	
	private static final Logger LOGGER = Logger.getLogger(ManageComponentSpecifications.class);
	
	@PostConstruct
	private void initializateDataStores()
	{
		if(dataStoreCCOn)
		  this.initializeDataStoreCC();
		if(dataStoreACOn)
		  this.initializeDataStoreAC();		
	}
	//There aren't error to return
	private void initializeDataStoreAC() {
			
		LOGGER.info("[ManageDB - initializeDataStoreAC] Creating DataStoreAC...");
		
		Properties hibernateProperties = new Properties();

		String dbName = "abstractcomponents";
		
		hibernateProperties.setProperty(Environment.DRIVER, "org.postgresql.Driver");
		hibernateProperties.setProperty(Environment.USER, "postgres");
		hibernateProperties.setProperty(Environment.URL, "jdbc:postgresql://150.214.150.116:5432/" + dbName);
		hibernateProperties.setProperty(Environment.PASS, "root");
		hibernateProperties.setProperty(Environment.DIALECT, org.hibernate.dialect.PostgreSQL81Dialect.class.getName());

		hibernateProperties.setProperty(PersistenceOptions.CASCADE_POLICY_ON_NON_CONTAINMENT, "REFRESH,PERSIST,MERGE");
		hibernateProperties.setProperty(PersistenceOptions.INHERITANCE_MAPPING, "JOINED");		
		
		hibernateProperties.setProperty("hibernate.c3p0.idle_test_period", "1800" );

		final String dataStoreName = "AbstractComponents";
		dataStoreAC = HbHelper.INSTANCE.createRegisterDataStore(dataStoreName);
		dataStoreAC.setDataStoreProperties(hibernateProperties);

		dataStoreAC.setEPackages(new EPackage[] { AcmmPackage.eINSTANCE });

		dataStoreAC.initialize();
		
		LOGGER.info("[ManageDB] DataStoreAC has been created");
	}
	
	//There aren't error to return
	private void initializeDataStoreCC() {
		
		LOGGER.info("[ManageDB - initializeDataStoreAC] Creating DataStoreCC...");
		
		Properties hibernateProperties = new Properties();

		String dbName = "concretecomponentsjesus33";
		
		hibernateProperties.setProperty(Environment.DRIVER, "org.postgresql.Driver");
		hibernateProperties.setProperty(Environment.USER, "postgres");
		hibernateProperties.setProperty(Environment.URL, "jdbc:postgresql://150.214.150.116:5432/" + dbName);
		hibernateProperties.setProperty(Environment.PASS, "root");
		hibernateProperties.setProperty(Environment.DIALECT, org.hibernate.dialect.PostgreSQL81Dialect.class.getName());

		hibernateProperties.setProperty(PersistenceOptions.CASCADE_POLICY_ON_NON_CONTAINMENT, "REFRESH,PERSIST,MERGE");
		hibernateProperties.setProperty(PersistenceOptions.INHERITANCE_MAPPING, "JOINED");
		
		// No crear tablas intermedias
		hibernateProperties.setProperty(PersistenceOptions.JOIN_TABLE_FOR_NON_CONTAINED_ASSOCIATIONS,"false");
		
		// Without e_version in the tables
	    hibernateProperties.setProperty("teneo.mapping.always_version","false");
	    
	    // Without e_container in the tables
	    hibernateProperties.setProperty("teneo.mapping.disable_econtainer","true");
	    
	    hibernateProperties.setProperty("hibernate.c3p0.idle_test_period", "1800" );

		final String dataStoreName = "ConcreteComponents";
		dataStoreCC = HbHelper.INSTANCE.createRegisterDataStore(dataStoreName);
		dataStoreCC.setDataStoreProperties(hibernateProperties);

		dataStoreCC.setEPackages(new EPackage[] { CcmmPackage.eINSTANCE });

		dataStoreCC.initialize();
		
		LOGGER.info("[ManageDB] DataStoreCC has been created");
	}
	
	public String exportCCFromURI(String ccFileType, String ccFileURI)
	{
		String result = "";
		
		ConcreteComponentSpecification cc = readCCFromURI(ccFileURI);		
		String componentName = cc.getComponentName();
		LOGGER.info("Read CC ID: " + componentName);
		
		SessionFactory sessionFactory = dataStoreCC.getSessionFactory();

		//Open a new Session
		Session session = sessionFactory.openSession();
		
		//Check if the CC ID does exist
		Query query  = session.createQuery("FROM ConcreteComponentSpecification ccs WHERE ccs.componentName = '" +componentName + "'");
		List<?> ccsList = query.list();
		if(ccsList.size()==0){
			result = componentName + " ID does not exist --> Insert CC Specification";
			LOGGER.info(result);

			//Start transaction
			session.beginTransaction();
			
			session.save(cc);
			
			//Commit the changes to the database.
			session.getTransaction().commit();
			
		}else{
			result = componentName + " ID exist --> CC Specification is not inserted";
			LOGGER.info(result);
		}
		
		//Close the session.
		session.close();
	    
		return result;
	}
	
	public String exportCCFromString(String ccFileType, String ccFileString)
	{
		// It is necessary for the SOAP message, that the input String has
		// <![CDATA["" at the start and
		// "]]>" at the end
		
		String result = "";
		
		ConcreteComponentSpecification cc = null;
		String componentName = null;
		try {
			cc = (ConcreteComponentSpecification) convertXMIStringToEObject(ccFileString);
			componentName = cc.getComponentName();
			LOGGER.info("Read CC NAME: " + componentName);
		} catch (IOException e) {
			LOGGER.error(e.toString());
		}
		
		if(cc != null){
			SessionFactory sessionFactory = dataStoreCC.getSessionFactory();

			//Open a new Session
			Session session = sessionFactory.openSession();
			
			//Check if the CC ID does exist
			Query query  = session.createQuery("FROM ConcreteComponentSpecification ccs WHERE ccs.componentName = '" +componentName + "'");
			List<?> ccsList = query.list();
			if(ccsList.size()==0){
				result = componentName + " ID does not exist --> Insert CC Specification";
				LOGGER.info(result);

				//Start transaction
				session.beginTransaction();
				
				session.save(cc);
				
				//Commit the changes to the database.
				session.getTransaction().commit();
				
			}else{
				result = componentName + " ID exist --> CC Specification is not inserted";
				LOGGER.info(result);
			}
			
			//Close the session.
			session.close();
			
		}		
		
		return result;		
	}
	
	public String exportCCFromParams(String componentName, String componentAlias, String componentDescription, String entityId, String entityName,
			String entityDescription, String contactDescription, String personName, String email, String phone, String address,
			String versionId, String versionDate, String programmingLanguage, String platformType, String repositoryId, String repositoryType,
			String repositoryURI, String componentURI, String[] propertyId, String[] propertyValue, boolean[] isEditable,
			String dependencyInterfaceId, String[] requiredProvided, String[] interfaceId,	String[] interfaceDescription, String[] anyUri)	{
		
		SessionFactory sessionFactory = dataStoreCC.getSessionFactory();

		//Open a new Session
		Session session = sessionFactory.openSession();
				
		String result = "";

		ConcreteComponentSpecification cc = new ConcreteComponentSpecificationImpl();
		cc.setAbstractComponentID("In revision");
		cc.setComponentDescription(componentDescription);
		cc.setComponentAlias(componentAlias);
		cc.setComponentName(componentName);

		//################################# ExtraFunctional
		ExtraFunctional ef = new ExtraFunctionalImpl();

		for(int i = 0; i < propertyId.length; i++){
		//for(int i = 0; i < 0; i++){

			Property p = new PropertyImpl();
			
			p.setIsEditable(isEditable[i]);
			
			p.setPropertyValue(propertyValue[i]);

			if(propertyId[i].equalsIgnoreCase("height")){
				p.setPropertyID(PropertyID.HEIGHT);
			}

			if(propertyId[i].equalsIgnoreCase("width")){
				p.setPropertyID(PropertyID.WIDTH);
			}

			if(propertyId[i].equalsIgnoreCase("isResizable")){
				p.setPropertyID(PropertyID.IS_RESIZABLE);
			}

			if(propertyId[i].equalsIgnoreCase("minimumSize")){
				p.setPropertyID(PropertyID.MINIMUM_SIZE);
			}

			if(propertyId[i].equalsIgnoreCase("maximumSize")){
				p.setPropertyID(PropertyID.MAXIMUM_SIZE);
			}

			if(propertyId[i].equalsIgnoreCase("layer")){
				p.setPropertyID(PropertyID.LAYER);
			}

			if(propertyId[i].equalsIgnoreCase("URLWMS")){
				p.setPropertyID(PropertyID.URLWMS);
			}
			
			if(propertyId[i].equalsIgnoreCase("groupable")){
				p.setPropertyID(PropertyID.GROUPABLE);
			}
			
			if(propertyId[i].equalsIgnoreCase("maximizable")){
				p.setPropertyID(PropertyID.MAXIMIZABLE);
			}
			
			ef.getProperty().add(p);
		}
		
		if(dependencyInterfaceId != null && dependencyInterfaceId.equalsIgnoreCase("") == false){

			Dependency d = new DependencyImpl();
			d.setInterfaceID(dependencyInterfaceId);
			
			ef.getDependency().add(d);
		}
		
		cc.setExtraFunctional(ef);

		//################################# Functional
		Functional fi = new FunctionalImpl();
		
		ControllerInterface cii = new ControllerInterfaceImpl();

		for(int i = 0; i < interfaceId.length; i++) {

			ProvidedInterface pi = new ProvidedInterfaceImpl();
			RequiredInterface ri = new RequiredInterfaceImpl();
			if(requiredProvided[i].equalsIgnoreCase("Provided")) {
				// Provided

				pi.setInterfaceDescription(interfaceDescription[i]);
				pi.setInterfaceID(interfaceId[i]);

				WSDLSpecification ws = new WSDLSpecificationImpl();
				ws.setUri(anyUri[i]);
				PortType pt = new PortTypeImpl();
				pt.setName("tipoPuerto");
				Operation o = new OperationImpl();
				o.setName("nombre");
				Input in = new InputImpl();
				in.setName("entrada");
				in.setType("entrada");
				o.setInput(in);
				pt.getOperation().add(o);
				ws.setPortType(pt);
				
				pi.setInterfaceSpecification(ws);
				
				cii.getProvidedInterface().add(pi);
			} else {
				// Required

				ri.setInterfaceDescription(interfaceDescription[i]);
				ri.setInterfaceID(interfaceId[i]);
				
				WSDLSpecification wsr = new WSDLSpecificationImpl();
				wsr.setUri(anyUri[i]);
				PortType ptr = new PortTypeImpl();
				ptr.setName("tipoPuerto");
				Operation or = new OperationImpl();
				or.setName("nombre");
				Input inr = new InputImpl();
				inr.setName("entrada");
				inr.setType("entrada");
				or.setInput(inr);
				ptr.getOperation().add(or);
				wsr.setPortType(ptr);
				
				ri.setInterfaceSpecification(wsr);
				
				cii.getRequiredInterface().add(ri);
			}		
			
		}		

		cii.setFilesAddress("controllerInterface");
		cii.setFilesLanguage("Java");
		
		fi.setControllerInterface(cii);
		fi.setCoreContent(null);
		fi.setInteractionContent(null);
		fi.setInteractionInterface(null);
		
		cc.setFunctional(fi);


		//################################# MARKETING
		
		Marketing mi = queryMarketing(entityId);
		
		if(mi == null){
			mi = new MarketingImpl();
			
			Contact c = queryContact(personName);
			
			if(c == null){
				c = new ContactImpl();
				c.setAddress(address);
				c.setDescription(contactDescription);
				c.setEmail(email);
				c.setPersonName(personName);
				c.setPhone(phone);
			}			
			
			mi.getContact().add(c);
			mi.setEntityDescription(entityDescription);
			mi.setEntityID(entityId);
			mi.setEntityName(entityName);
		}
		
		cc.setMarketing(mi);


		//################################# PACKAGING
		Packaging p = new PackagingImpl();
		
		Implementation ii = queryImplementation(programmingLanguage);

		if(ii == null){
			ii = new ImplementationImpl();
			if(platformType.equalsIgnoreCase("Web")){


				ii.setPlatformType(PlatformType.WEB);
			}else{

				ii.setPlatformType(PlatformType.JAVA);
			}
			ii.setProgrammingLanguage(programmingLanguage);
		}
		
		Location li = queryLocation(repositoryId);
		
		if(li == null){
			li = new LocationImpl();
			li.setComponentURI(componentURI);
			li.setRepositoryID(repositoryId);
			if(repositoryType.equalsIgnoreCase("JavaRepository") || repositoryType.equalsIgnoreCase("Java_Repository"))
				li.setRepositoryType(RepositoryType.JAVA_REPOSITORY);
			else
				li.setRepositoryType(RepositoryType.APACHE_WOOKIE_100);
			li.setRepositoryURI(repositoryURI);
		}
		
		Version vi = new VersionImpl();
		vi.setVersionID(versionId);
		vi.setVersionDate(versionDate);
		
		p.setImplementation(ii);
		p.setLocation(li);
		p.setVersion(vi);

		cc.setPackaging(p);

	
		componentName = cc.getComponentName();
		LOGGER.info("Read CC ID: " + componentName);
		
		// save the model in the database
			
		//Check if the CC ID does exist
		Query query  = session.createQuery("FROM ConcreteComponentSpecification ccs WHERE ccs.componentName = '" + componentName + "'");
		List<?> ccsList = query.list();
		if(ccsList.size()==0){
			result = componentName + " ID does not exist --> Insert CC Specification";
			LOGGER.info(result);

			//Start transaction
			session.beginTransaction();
				
			session.save(cc);
				
			//Commit the changes to the database.
			session.getTransaction().commit();
				
		}else{
			result = componentName + " ID exist --> CC Specification is not inserted";
			LOGGER.info(result);
		}
			
		//Close the session.
		session.close();
					
		return result;
	}
	
	private Contact queryContact(String personName){
		
		SessionFactory sessionFactory = dataStoreCC.getSessionFactory();

		//Open a new Session
		Session session = sessionFactory.openSession();
		
		//Check if the CC ID does exist
		Query query  = session.createQuery("FROM Contact c WHERE c.personName = '" + personName + "'");
		List<?> cList = query.list();
		if(cList.size()==0)		{
			//Close the session.
			session.close();

			return null;
		} else {
			Contact c = (Contact) cList.get(0);
			
			//Close the session.
			session.close();

			return c;
		}
	}
	
    private Marketing queryMarketing(String entityId){
		
		SessionFactory sessionFactory = dataStoreCC.getSessionFactory();

		//Open a new Session
		Session session = sessionFactory.openSession();
		
		//Check if the CC ID does exist
		Query query  = session.createQuery("FROM Marketing m WHERE m.entityID = '" + entityId + "'");
		List<?> mList = query.list();
		if(mList.size()==0) {
			//Close the session.
			session.close();

			return null;
		} else {
			Marketing m = (Marketing) mList.get(0);
			
			//Close the session.
			session.close();

			return m;
		}
	}
    
    private Implementation queryImplementation(String programmingLanguage){
		
		SessionFactory sessionFactory = dataStoreCC.getSessionFactory();

		//Open a new Session
		Session session = sessionFactory.openSession();
		
		//Check if the CC ID does exist
		Query query  = session.createQuery("FROM Implementation i WHERE i.programmingLanguage = '" + programmingLanguage + "'");
		List<?> mList = query.list();
		if(mList.size()==0) {
			//Close the session.
			session.close();

			return null;
		} else {
			Implementation i = (Implementation) mList.get(0);
			
			//Close the session.
			session.close();

			return i;
		}
	}
    
    private Location queryLocation(String repositoryID){
		
		SessionFactory sessionFactory = dataStoreCC.getSessionFactory();

		//Open a new Session
		Session session = sessionFactory.openSession();
		
		//Check if the CC ID does exist
		Query query  = session.createQuery("FROM Location l WHERE l.repositoryID = '" + repositoryID + "'");
		List<?> mList = query.list();
		if(mList.size()==0) {
			//Close the session.
			session.close();

			return null;
		} else {
			Location l = (Location) mList.get(0);
			
			//Close the session.
			session.close();

			return l;
		}
	}
	
	public String exportACFromString(String acFileType, String acFileString) {
		
		String result = "";
		
		AbstractComponentSpecification acs = null;
		String componentID = null;
		try {
			acs = (AbstractComponentSpecification) convertXMIStringToEObject(acFileString);
			componentID = acs.getComponentID();
			LOGGER.info("Read AC ID: " + componentID);
		} catch (IOException e) {
			LOGGER.error(e.toString());
		}
		
		if(acs != null){
			
			SessionFactory sessionFactory = dataStoreAC.getSessionFactory();

			//Open a new Session
			Session session = sessionFactory.openSession();
			
			//Check if the AC ID does exist
			Query query  = session.createQuery("FROM AbstractComponentSpecification acs " +
					"WHERE acs.componentID = '" + componentID + "'");
			List<?> acsList = query.list();
			if(acsList.size()==0){
				result = componentID + " ID does not exist --> Insert AC Specification";
				LOGGER.info(result);

				//Start transaction
				session.beginTransaction();
				
				session.save(acs);
				
				//Commit the changes to the database.
				session.getTransaction().commit();
				
			}else{
				result = componentID + " ID exist --> AC Specification is not inserted";
				LOGGER.info(result);
			}
			
			//Close the session.
			session.close();
			
		}		
		
		return result;		
	}
	
	private ConcreteComponentSpecification readCCFromURI(String ccFileURI)
	{
		// Initialize the model
	    CcmmPackage.eINSTANCE.eClass();
	    
	    // Register the XMI resource factory for the .xmi extension
	    Resource.Factory.Registry reg = Resource.Factory.Registry.INSTANCE;
	    Map<String, Object> m = reg.getExtensionToFactoryMap();
	    m.put("xmi", new XMIResourceFactoryImpl());

	    // Obtain a new resource set
	    ResourceSet resSet = new ResourceSetImpl();

	    // Get the resource
	    Resource resource = resSet.getResource(URI.createURI(ccFileURI), true);
	    
	    // Get the first model element and cast it to the right type	    
	    ConcreteComponentSpecification cc = (ConcreteComponentSpecification) resource.getContents().get(0);
		
	    return cc;
	}
	
	@Lock(LockType.READ)
	public HbDataStore getDataStoreCC() {
		return dataStoreCC;
	}

	public void setDataStoreCC(HbDataStore dataStoreCC) {
		this.dataStoreCC = dataStoreCC;
	}

	@Lock(LockType.READ)
	public HbDataStore getDataStoreAC() {
		//initializeDataStoreAC();
		return dataStoreAC;
	}

	public void setDataStoreAC(HbDataStore dataStoreAC) {
		this.dataStoreAC = dataStoreAC;
	}
	
	public EObject convertXMIStringToEObject(String xmiString) throws IOException {
        XMIResourceImpl resource = new XMIResourceImpl();
        resource.setEncoding("UTF-8");
        resource.load(new InputSource(new StringReader(xmiString)), null);
 
        return resource.getContents().get(0);
    }

	public String withdrawCC(String componentName)
	{
		String result = "";
		
		SessionFactory sessionFactory = dataStoreCC.getSessionFactory();

		//Open a new Session
		Session session = sessionFactory.openSession();
		
		//Check if the CC ID does exist
		Query query  = session.createQuery("FROM ConcreteComponentSpecification ccs WHERE ccs.componentName = '" +
				componentName + "'");
		List<?> ccsList = query.list();
		if(ccsList.size()==0)
		{
			result = componentName + " ID does not exist --> Cannot delete CC Specification";
			LOGGER.info(result);			
		}
		else
		{
			result = componentName + " ID exist --> Deleting CC Specification...";
			LOGGER.info(result);
			
			ConcreteComponentSpecification ccs = (ConcreteComponentSpecification) ccsList.get(0);

			
			//Start transaction
			session.beginTransaction();
			
			session.delete(ccs);
			
			//Commit the changes to the database.
			session.getTransaction().commit();
			
			LOGGER.info(" CC Specification has been deleted");
		}
		
		//Close the session.
		session.close();
		
		return result;
	}
	
	public String withdrawAC(String acID)
	{
		String result = "";
		
		SessionFactory sessionFactory = dataStoreAC.getSessionFactory();

		//Open a new Session
		Session session = sessionFactory.openSession();
		
		//Check if the CC ID does exist
		Query query  = session.createQuery("FROM AbstractComponentSpecification acs WHERE acs.componentID = '" +acID + "'");
		List<?> acsList = query.list();
		if(acsList.size()==0){
			result = acID + " ID does not exist --> Cannot delete AC Specification";
			LOGGER.info(result);			
		}else{
			result = acID + " ID exist --> Deleting CC Specification...";
			LOGGER.info(result);
			
			AbstractComponentSpecification acs = (AbstractComponentSpecification) acsList.get(0);

			//Start transaction
			session.beginTransaction();
			
			session.delete(acs);
			
			//Commit the changes to the database.
			session.getTransaction().commit();
			
			LOGGER.info(" AC Specification has been deleted");
		}
		
		//Close the session.
		session.close();
		
		return result;
	}
	
	public String queryComponentPlatform(String componentName) throws Exception{

		SessionFactory sessionFactory = dataStoreCC.getSessionFactory();

		//Open a new Session
		Session session = sessionFactory.openSession();
		  
		//Start transaction
		session.beginTransaction();
				
		Query query = session.createQuery("FROM ConcreteComponentSpecification "
				+ "WHERE componentName = '" + componentName + "'");
		
		List<?> ccs = query.list();

		LOGGER.info("PlataformType -> " + ((ConcreteComponentSpecification) ccs.get(0)).getPackaging().getImplementation().getPlatformType());
				  
		//Close the session.
		session.close();
		
		if(ccs.size() == 1)
			return ((ConcreteComponentSpecification) ccs.get(0)).getPackaging().getImplementation().getPlatformType().toString();
		else
			return null;
	}
	
	public void registerExampleComponents()
	{
		exportCCFromURI("xmi", "http://acg.ual.es/trader/cc/cloudStorage/cloudStorage01.xmi");
		exportCCFromURI("xmi", "http://acg.ual.es/trader/cc/cloudStorage/cloudStorage02.xmi");
		exportCCFromURI("xmi", "http://acg.ual.es/trader/cc/cloudStorage/cloudStorage03.xmi");
		exportCCFromURI("xmi", "http://acg.ual.es/trader/cc/cloudStorage/cloudStorage04.xmi");
		exportCCFromURI("xmi", "http://acg.ual.es/trader/cc/cloudStorage/cloudStorage05.xmi");
		exportCCFromURI("xmi", "http://acg.ual.es/trader/cc/cloudStorage/cloudStorage06.xmi");
		exportCCFromURI("xmi", "http://acg.ual.es/trader/cc/cloudStorage/cloudStorage07.xmi");
		exportCCFromURI("xmi", "http://acg.ual.es/trader/cc/cloudStorage/cloudStorage08.xmi");
		exportCCFromURI("xmi", "http://acg.ual.es/trader/cc/cloudStorage/cloudStorage09.xmi");
		exportCCFromURI("xmi", "http://acg.ual.es/trader/cc/cloudStorage/cloudStorage10.xmi");
		exportCCFromURI("xmi", "http://acg.ual.es/trader/cc/cloudStorage/cloudStorage11.xmi");
		exportCCFromURI("xmi", "http://acg.ual.es/trader/cc/cloudStorage/cloudStorage12.xmi");
		exportCCFromURI("xmi", "http://acg.ual.es/trader/cc/cloudStorage/cloudStorage13.xmi");
		exportCCFromURI("xmi", "http://acg.ual.es/trader/cc/cloudStorage/cloudStorage14.xmi");
		exportCCFromURI("xmi", "http://acg.ual.es/trader/cc/cloudStorage/cloudStorage15.xmi");
		exportCCFromURI("xmi", "http://acg.ual.es/trader/cc/cloudStorage/cloudStorage16.xmi");
		exportCCFromURI("xmi", "http://acg.ual.es/trader/cc/cloudStorage/cloudStorage17.xmi");
		exportCCFromURI("xmi", "http://acg.ual.es/trader/cc/cloudStorage/cloudStorage18.xmi");
		exportCCFromURI("xmi", "http://acg.ual.es/trader/cc/cloudStorage/cloudStorage19.xmi");
		exportCCFromURI("xmi", "http://acg.ual.es/trader/cc/cloudStorage/cloudStorage20.xmi");
		
		exportCCFromURI("xmi", "http://acg.ual.es/trader/cc/header/header01.xmi");
		exportCCFromURI("xmi", "http://acg.ual.es/trader/cc/header/header02.xmi");
		exportCCFromURI("xmi", "http://acg.ual.es/trader/cc/header/header03.xmi");
		exportCCFromURI("xmi", "http://acg.ual.es/trader/cc/header/header04.xmi");
		exportCCFromURI("xmi", "http://acg.ual.es/trader/cc/header/header05.xmi");
		exportCCFromURI("xmi", "http://acg.ual.es/trader/cc/header/header06.xmi");
		exportCCFromURI("xmi", "http://acg.ual.es/trader/cc/header/header07.xmi");
		exportCCFromURI("xmi", "http://acg.ual.es/trader/cc/header/header08.xmi");
		exportCCFromURI("xmi", "http://acg.ual.es/trader/cc/header/header09.xmi");
		exportCCFromURI("xmi", "http://acg.ual.es/trader/cc/header/header10.xmi");
		exportCCFromURI("xmi", "http://acg.ual.es/trader/cc/header/header11.xmi");
		exportCCFromURI("xmi", "http://acg.ual.es/trader/cc/header/header12.xmi");
		exportCCFromURI("xmi", "http://acg.ual.es/trader/cc/header/header13.xmi");
		exportCCFromURI("xmi", "http://acg.ual.es/trader/cc/header/header14.xmi");
		exportCCFromURI("xmi", "http://acg.ual.es/trader/cc/header/header15.xmi");
		exportCCFromURI("xmi", "http://acg.ual.es/trader/cc/header/header16.xmi");
		exportCCFromURI("xmi", "http://acg.ual.es/trader/cc/header/header17.xmi");
		exportCCFromURI("xmi", "http://acg.ual.es/trader/cc/header/header18.xmi");
		exportCCFromURI("xmi", "http://acg.ual.es/trader/cc/header/header19.xmi");
		exportCCFromURI("xmi", "http://acg.ual.es/trader/cc/header/header20.xmi");
		
		exportCCFromURI("xmi", "http://acg.ual.es/trader/cc/histogram/histogram01.xmi");
		exportCCFromURI("xmi", "http://acg.ual.es/trader/cc/histogram/histogram02.xmi");
		exportCCFromURI("xmi", "http://acg.ual.es/trader/cc/histogram/histogram03.xmi");
		exportCCFromURI("xmi", "http://acg.ual.es/trader/cc/histogram/histogram04.xmi");
		exportCCFromURI("xmi", "http://acg.ual.es/trader/cc/histogram/histogram05.xmi");
		exportCCFromURI("xmi", "http://acg.ual.es/trader/cc/histogram/histogram06.xmi");
		exportCCFromURI("xmi", "http://acg.ual.es/trader/cc/histogram/histogram07.xmi");
		exportCCFromURI("xmi", "http://acg.ual.es/trader/cc/histogram/histogram08.xmi");
		exportCCFromURI("xmi", "http://acg.ual.es/trader/cc/histogram/histogram09.xmi");
		exportCCFromURI("xmi", "http://acg.ual.es/trader/cc/histogram/histogram10.xmi");
		exportCCFromURI("xmi", "http://acg.ual.es/trader/cc/histogram/histogram11.xmi");
		exportCCFromURI("xmi", "http://acg.ual.es/trader/cc/histogram/histogram12.xmi");
		exportCCFromURI("xmi", "http://acg.ual.es/trader/cc/histogram/histogram13.xmi");
		exportCCFromURI("xmi", "http://acg.ual.es/trader/cc/histogram/histogram14.xmi");
		exportCCFromURI("xmi", "http://acg.ual.es/trader/cc/histogram/histogram15.xmi");
		exportCCFromURI("xmi", "http://acg.ual.es/trader/cc/histogram/histogram16.xmi");
		exportCCFromURI("xmi", "http://acg.ual.es/trader/cc/histogram/histogram17.xmi");
		exportCCFromURI("xmi", "http://acg.ual.es/trader/cc/histogram/histogram18.xmi");
		exportCCFromURI("xmi", "http://acg.ual.es/trader/cc/histogram/histogram19.xmi");
		exportCCFromURI("xmi", "http://acg.ual.es/trader/cc/histogram/histogram20.xmi");
		
		exportCCFromURI("xmi", "http://acg.ual.es/trader/cc/map/map01.xmi");
		exportCCFromURI("xmi", "http://acg.ual.es/trader/cc/map/map02.xmi");
		exportCCFromURI("xmi", "http://acg.ual.es/trader/cc/map/map03.xmi");
		exportCCFromURI("xmi", "http://acg.ual.es/trader/cc/map/map04.xmi");
		exportCCFromURI("xmi", "http://acg.ual.es/trader/cc/map/map05.xmi");
		exportCCFromURI("xmi", "http://acg.ual.es/trader/cc/map/map06.xmi");
		exportCCFromURI("xmi", "http://acg.ual.es/trader/cc/map/map07.xmi");
		exportCCFromURI("xmi", "http://acg.ual.es/trader/cc/map/map08.xmi");
		exportCCFromURI("xmi", "http://acg.ual.es/trader/cc/map/map09.xmi");
		exportCCFromURI("xmi", "http://acg.ual.es/trader/cc/map/map10.xmi");
		exportCCFromURI("xmi", "http://acg.ual.es/trader/cc/map/map11.xmi");
		exportCCFromURI("xmi", "http://acg.ual.es/trader/cc/map/map12.xmi");
		exportCCFromURI("xmi", "http://acg.ual.es/trader/cc/map/map13.xmi");
		exportCCFromURI("xmi", "http://acg.ual.es/trader/cc/map/map14.xmi");
		exportCCFromURI("xmi", "http://acg.ual.es/trader/cc/map/map15.xmi");
		exportCCFromURI("xmi", "http://acg.ual.es/trader/cc/map/map16.xmi");
		exportCCFromURI("xmi", "http://acg.ual.es/trader/cc/map/map17.xmi");
		exportCCFromURI("xmi", "http://acg.ual.es/trader/cc/map/map18.xmi");
		exportCCFromURI("xmi", "http://acg.ual.es/trader/cc/map/map19.xmi");
		exportCCFromURI("xmi", "http://acg.ual.es/trader/cc/map/map20.xmi");
		
		exportCCFromURI("xmi", "http://acg.ual.es/trader/cc/rss/rss01.xmi");
		exportCCFromURI("xmi", "http://acg.ual.es/trader/cc/rss/rss02.xmi");
		exportCCFromURI("xmi", "http://acg.ual.es/trader/cc/rss/rss03.xmi");
		exportCCFromURI("xmi", "http://acg.ual.es/trader/cc/rss/rss04.xmi");
		exportCCFromURI("xmi", "http://acg.ual.es/trader/cc/rss/rss05.xmi");
		exportCCFromURI("xmi", "http://acg.ual.es/trader/cc/rss/rss06.xmi");
		exportCCFromURI("xmi", "http://acg.ual.es/trader/cc/rss/rss07.xmi");
		exportCCFromURI("xmi", "http://acg.ual.es/trader/cc/rss/rss08.xmi");
		exportCCFromURI("xmi", "http://acg.ual.es/trader/cc/rss/rss09.xmi");
		exportCCFromURI("xmi", "http://acg.ual.es/trader/cc/rss/rss10.xmi");
		exportCCFromURI("xmi", "http://acg.ual.es/trader/cc/rss/rss11.xmi");
		exportCCFromURI("xmi", "http://acg.ual.es/trader/cc/rss/rss12.xmi");
		exportCCFromURI("xmi", "http://acg.ual.es/trader/cc/rss/rss13.xmi");
		exportCCFromURI("xmi", "http://acg.ual.es/trader/cc/rss/rss14.xmi");
		exportCCFromURI("xmi", "http://acg.ual.es/trader/cc/rss/rss15.xmi");
		exportCCFromURI("xmi", "http://acg.ual.es/trader/cc/rss/rss16.xmi");
		exportCCFromURI("xmi", "http://acg.ual.es/trader/cc/rss/rss17.xmi");
		exportCCFromURI("xmi", "http://acg.ual.es/trader/cc/rss/rss18.xmi");
		exportCCFromURI("xmi", "http://acg.ual.es/trader/cc/rss/rss19.xmi");
		exportCCFromURI("xmi", "http://acg.ual.es/trader/cc/rss/rss20.xmi");
		
		exportCCFromURI("xmi", "http://acg.ual.es/trader/cc/twitter/twitter01.xmi");
		exportCCFromURI("xmi", "http://acg.ual.es/trader/cc/twitter/twitter02.xmi");
		exportCCFromURI("xmi", "http://acg.ual.es/trader/cc/twitter/twitter03.xmi");
		exportCCFromURI("xmi", "http://acg.ual.es/trader/cc/twitter/twitter04.xmi");
		exportCCFromURI("xmi", "http://acg.ual.es/trader/cc/twitter/twitter05.xmi");
		exportCCFromURI("xmi", "http://acg.ual.es/trader/cc/twitter/twitter06.xmi");
		exportCCFromURI("xmi", "http://acg.ual.es/trader/cc/twitter/twitter07.xmi");
		exportCCFromURI("xmi", "http://acg.ual.es/trader/cc/twitter/twitter08.xmi");
		exportCCFromURI("xmi", "http://acg.ual.es/trader/cc/twitter/twitter09.xmi");
		exportCCFromURI("xmi", "http://acg.ual.es/trader/cc/twitter/twitter10.xmi");
		exportCCFromURI("xmi", "http://acg.ual.es/trader/cc/twitter/twitter11.xmi");
		exportCCFromURI("xmi", "http://acg.ual.es/trader/cc/twitter/twitter12.xmi");
		exportCCFromURI("xmi", "http://acg.ual.es/trader/cc/twitter/twitter13.xmi");
		exportCCFromURI("xmi", "http://acg.ual.es/trader/cc/twitter/twitter14.xmi");
		exportCCFromURI("xmi", "http://acg.ual.es/trader/cc/twitter/twitter15.xmi");
		exportCCFromURI("xmi", "http://acg.ual.es/trader/cc/twitter/twitter16.xmi");
		exportCCFromURI("xmi", "http://acg.ual.es/trader/cc/twitter/twitter17.xmi");
		exportCCFromURI("xmi", "http://acg.ual.es/trader/cc/twitter/twitter18.xmi");
		exportCCFromURI("xmi", "http://acg.ual.es/trader/cc/twitter/twitter19.xmi");
		exportCCFromURI("xmi", "http://acg.ual.es/trader/cc/twitter/twitter20.xmi");
		
	}
	
	public List<RuntimeProperty> readComponentProperty(String componentName)throws Exception{

		SessionFactory sessionFactory = dataStoreCC.getSessionFactory();

		//Open a new Session
		Session session = sessionFactory.openSession();
		  
		//Start transaction
		session.beginTransaction();
				
		Query query = session.createQuery("FROM ConcreteComponentSpecification "+ "WHERE componentName = '" + componentName + "'");
		
		List<?> ccs = query.list();

		List<Property> listProperties = (List<Property>) ((ConcreteComponentSpecification) ccs.get(0)).getExtraFunctional().getProperty();
				  
		List<RuntimeProperty> listRuntimeProperty = new ArrayList<RuntimeProperty>();
		
		RuntimeProperty runtimePropertyPlatform =  Architectural_metamodelFactory.eINSTANCE.createRuntimeProperty();
		runtimePropertyPlatform.setPropertyID("platform");
		runtimePropertyPlatform.setPropertyValue(queryComponentPlatform(componentName));
		listRuntimeProperty.add(runtimePropertyPlatform);
		
		for(int i = 0; i < listProperties.size(); i++) {
			
			if(listProperties.get(i).getPropertyID().getName().equalsIgnoreCase("width")){
				RuntimeProperty runtimePropertyTamanoX =  Architectural_metamodelFactory.eINSTANCE.createRuntimeProperty();
				runtimePropertyTamanoX.setPropertyID("tamanoX");
				runtimePropertyTamanoX.setPropertyValue(listProperties.get(i).getPropertyValue()+"");
				listRuntimeProperty.add(runtimePropertyTamanoX);
			} else {
				if(listProperties.get(i).getPropertyID().getName().equalsIgnoreCase("height")){
					RuntimeProperty runtimePropertyTamanoY =  Architectural_metamodelFactory.eINSTANCE.createRuntimeProperty();
					runtimePropertyTamanoY.setPropertyID("tamanoY");
					runtimePropertyTamanoY.setPropertyValue(listProperties.get(i).getPropertyValue()+"");
					listRuntimeProperty.add(runtimePropertyTamanoY);
				} else {
					if(listProperties.get(i).getPropertyID().getName().equalsIgnoreCase("groupable")){
						RuntimeProperty runtimePropertyGroupable =  Architectural_metamodelFactory.eINSTANCE.createRuntimeProperty();
						runtimePropertyGroupable.setPropertyID("servicio_agrupable");
						runtimePropertyGroupable.setPropertyValue(listProperties.get(i).getPropertyValue());
						listRuntimeProperty.add(runtimePropertyGroupable);
					} else {
						if(listProperties.get(i).getPropertyID().getName().equalsIgnoreCase("maximizable")){
							RuntimeProperty runtimePropertyMaximizable =  Architectural_metamodelFactory.eINSTANCE.createRuntimeProperty();
							runtimePropertyMaximizable.setPropertyID("servicio_maximizable");
							runtimePropertyMaximizable.setPropertyValue(listProperties.get(i).getPropertyValue());
							listRuntimeProperty.add(runtimePropertyMaximizable);
						} else {
							if(listProperties.get(i).getPropertyID().getName().equalsIgnoreCase("URLWMS")){
								RuntimeProperty runtimePropertyURL =  Architectural_metamodelFactory.eINSTANCE.createRuntimeProperty();
								runtimePropertyURL.setPropertyID("servicio0.mapa_KML");
								runtimePropertyURL.setPropertyValue(listProperties.get(i).getPropertyValue()+"");
								listRuntimeProperty.add(runtimePropertyURL);
							} else {
								if(listProperties.get(i).getPropertyID().getName().equalsIgnoreCase("layer")){
									RuntimeProperty runtimePropertyLayer =  Architectural_metamodelFactory.eINSTANCE.createRuntimeProperty();
									runtimePropertyLayer.setPropertyID("servicio0.capa");
									runtimePropertyLayer.setPropertyValue(listProperties.get(i).getPropertyValue()+"");
									listRuntimeProperty.add(runtimePropertyLayer);
								}
							}
						}
					}
				}
			}
		}
		//Close the session.
		session.close();		
		
		return listRuntimeProperty;
	}
}
