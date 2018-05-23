/*
 * UIMM.java -- Módulo de Información de Usuarios.
 * Copyright (C) 2016  Alfredo Valero Rodríguez, Javier Criado Rodríguez and Jesús Vallecillos Ruíz
 *
 * UIMM.java is part of COScore Community.
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
package es.ual.acg.cos.modules;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateful;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.commons.codec.binary.Hex;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.jboss.logging.Logger;

import architectural_metamodel.ConcreteArchitecturalModel;
import architectural_metamodel.ConcreteComponent;
import es.ual.acg.cos.controllers.ManageArchitectures;
import es.ual.acg.cos.controllers.ManageUsers;
import es.ual.acg.cos.controllers.ManageWookie;
import es.ual.acg.cos.types.ComponentData;
import es.ual.acg.cos.types.InterModulesData;
import es.ual.acg.cos.wookie.WidgetData;
import es.ual.acg.cos.ws.types.CreateUserParams;
import es.ual.acg.cos.ws.types.CreateUserResult;
import es.ual.acg.cos.ws.types.DeleteUserResult;
import es.ual.acg.cos.ws.types.QueryProfileResult;
import es.ual.acg.cos.ws.types.QueryUserParams;
import es.ual.acg.cos.ws.types.QueryUserResult;
import es.ual.acg.cos.ws.types.UpdateUserResult;

@Stateful
public class UIM {
	
	private static final Logger LOGGER = Logger.getLogger(ManageUsers.class);
	
	/**
	 * Create an user
	 * 
	 * @param userName
	 * @param userPassword
	 * @param userProfile
	 * @return
	 */
	public CreateUserResult createUser(CreateUserParams params) {
		CreateUserResult result = new CreateUserResult();
		InterModulesData resultquerycamprofile = new InterModulesData();
		WidgetData widgetData = null;
		WidgetData widgetData2 = null;
		ConcreteArchitecturalModel cam = null;
		String componentName = "";
    	String componentAlias = "";
		result.setCreated(false);
		
		String userName = params.getUserName();
		String userPassword = params.getUserPassword();
		String userProfile = params.getUserProfile();
	
		try {
		  /** Encriptamos el password **/
			MessageDigest md = null;
			String password = userPassword;
			md= MessageDigest.getInstance("MD5");
		    md.update(password.getBytes());
		    byte[] mb = md.digest();
		    userPassword = new String (Hex.encodeHex(mb));
		    params.setUserPassword(userPassword);
		    
		    resultquerycamprofile = queryCamProfile(userProfile);

		    if(resultquerycamprofile.getValue().compareTo("-1") != 0) {
		    	String camID = resultquerycamprofile.getValue();
		    	String camidforUser = camID+userName; //Nuevo identificado de cam formado por el cam del perfil + el nombre de usuario
			    Context initialContext;
				initialContext = new InitialContext();
				InterModulesData resultDMM = new InterModulesData();
				
				try {
					DMM dmm = (es.ual.acg.cos.modules.DMM)initialContext.lookup("java:app/cos/DMM");
					resultDMM = dmm.ReadModelforcamId(camID);
					cam = resultDMM.getCam();
						
				} catch (Exception e) {
					LOGGER.error(e);
					result.setMessage(resultDMM.getMessage());
				} 

				if(cam != null){
					//Clonar objeto antes de guardarlo en la base de datos
					ConcreteArchitecturalModel camCopy = EcoreUtil.copy(cam);
				  
				  	camCopy.setCamID(camidforUser);
					//Cambiamos las intancias de lo componentes de la copia del modelo para este nuevo usuario

				  	try{
					    for (int i = 0; i < camCopy.getConcreteComponent().size(); i++) {
					    	componentName = camCopy.getConcreteComponent().get(i).getComponentName();
					    	componentAlias = camCopy.getConcreteComponent().get(i).getComponentAlias();
							
					    	//Obtenemos indentificador Wookie
							try {
								DMM dmm = (es.ual.acg.cos.modules.DMM)initialContext.lookup("java:app/cos/DMM");
								resultDMM = dmm.getWidget(userName, componentName, componentAlias);
								widgetData = resultDMM.getWidget();
								LOGGER.info("[DMM - initGUI] instance ID: " + widgetData.getIdentifier());	
									
							} catch (Exception e) {
								LOGGER.error(e);
								result.setMessage(resultDMM.getMessage());
							}
							
							camCopy.getConcreteComponent().get(i).setComponentInstance(widgetData.getIdentifier());
							
							//Cambiamos los servicios tambien
							int numeroServicios = 0;
							for (int j = 0; j < camCopy.getConcreteComponent().get(i).getRuntimeProperty().size(); j++) {
								if ( camCopy.getConcreteComponent().get(i).getRuntimeProperty().get(j).getPropertyID().
									 equalsIgnoreCase("numero_servicios")) {
									
									numeroServicios = Integer.parseInt(camCopy.getConcreteComponent().get(i).
								        			  getRuntimeProperty().get(j).getPropertyValue());
								}
							}
							if(numeroServicios > 1){ //Si hay servicios agrupados
								for (int k = 1; k <= numeroServicios; k++) {
								    String service = "";
								    String serviciopropiedad= "";
								    String instaciaservicio= "";
								    String nameservicio= "";
								    String aliasservicio= "";
									for (int j = 0; j < camCopy.getConcreteComponent().get(i).getRuntimeProperty().size(); j++) {
										String property = camCopy.getConcreteComponent().get(i).getRuntimeProperty().get(j).
												          getPropertyID();
									    if (property.length() > 9)
										        service = property.substring(0, 8);
	
										if (service.equalsIgnoreCase("servicio"+k)) {
										        serviciopropiedad = property.substring(10, 14);
										        if (service.equalsIgnoreCase("name")) 
										        	nameservicio = camCopy.getConcreteComponent().get(i).
								        			  getRuntimeProperty().get(j).getPropertyValue();
										        if (service.equalsIgnoreCase("alia")) 
										        	aliasservicio = camCopy.getConcreteComponent().get(i).
								        			  getRuntimeProperty().get(j).getPropertyValue();
										 }
									}
									//Obtenemos indentificador Wookie
									try {
										DMM dmm = (es.ual.acg.cos.modules.DMM)initialContext.lookup("java:app/cos/DMM");
										resultDMM = dmm.getWidget(userName, nameservicio, aliasservicio);
										widgetData2 = resultDMM.getWidget();
										LOGGER.info("[DMM - initGUI] instance ID: " + widgetData2.getIdentifier());	
											
									} catch (Exception e) {
										LOGGER.error(e);
										result.setMessage(resultDMM.getMessage());
									}

									instaciaservicio=widgetData2.getIdentifier();
									for (int j = 0; j < camCopy.getConcreteComponent().get(i).getRuntimeProperty().size(); j++) {
										String property = camCopy.getConcreteComponent().get(i).getRuntimeProperty().get(j).
												          getPropertyID();
									    if (property.length() > 9)
										        service = property.substring(0, 8);
	
										if (service.equalsIgnoreCase("servicio"+k)) {
										        serviciopropiedad = property.substring(10, 14);
										        if (service.equalsIgnoreCase("inst")) 
										        	camCopy.getConcreteComponent().get(i).
										        			  getRuntimeProperty().get(j).setPropertyValue(instaciaservicio);

										 }
									}
								}
							}
							
							//cambiamos la instamncia del servicio base
							for (int j = 0; j < camCopy.getConcreteComponent().get(i).getRuntimeProperty().size(); j++) {
								String property = camCopy.getConcreteComponent().get(i).getRuntimeProperty().get(j).getPropertyID();
							    
								if (property.equalsIgnoreCase("servicio0.instancia")) {
									camCopy.getConcreteComponent().get(i).getRuntimeProperty().get(j).
								        setPropertyValue(widgetData.getIdentifier());;
								 }
							}
								
						}//For para cada componente

					} catch (Exception e) {
						LOGGER.error("> Error in Wookie");
						result.setMessage("> Internal Server Error");
					}
					
					try {
						DMM dmm = (es.ual.acg.cos.modules.DMM)initialContext.lookup("java:app/cos/DMM");
						resultDMM = dmm.SaveModelforcamId(camCopy);
							
					} catch (Exception e) {
						LOGGER.error(e);
						result.setMessage(resultDMM.getMessage());
					} 
				    
					//Ya tenemos el cam creado asi que creamos el usuario con su cam correspondiente
					ManageUsers mu = (es.ual.acg.cos.controllers.ManageUsers)initialContext.lookup("java:app/cos/ManageUsers");
					mu.createUser(userName, userPassword, userProfile, camidforUser);
					
					result.setCreated(true);
					result.setMessage("> Successfully created");
				}else{
					LOGGER.error("Cam no exist!");
					result.setMessage("> Internal Server Error");
				}
				
		    }else{
				LOGGER.error("Profile no exist!");
				result.setMessage("> Internal Server Error");
		    }
        
		} catch (NoSuchAlgorithmException e) {
			LOGGER.error(e);
			result.setMessage("> Internal Server Error");
		} catch (SQLException e){
			LOGGER.error(e);
			result.setMessage("> " + e);
		} catch (ClassNotFoundException e){
			LOGGER.error(e);
			result.setMessage("> Internal Server Error");
		} catch (NamingException e) {
			LOGGER.error(e);
			result.setMessage("> Internal Server Error");
		} catch (Exception e) {
			LOGGER.error(e);
			result.setMessage("> Internal Server Error");
		}
			
		return result;
	}
	/**
	 * Query user
	 * 
	 * @param userName
	 * @param userPassword
	 * @return
	 */
	public QueryUserResult queryUser(QueryUserParams params) {
		QueryUserResult result = new QueryUserResult();
		result.setValidation(false);
		result.setIduser(-1);
		result.setMessage("> Validation Error user o password incorrect");
		
		String userName = params.getUserName();
		String userPassword = params.getUserPassword();
		int idUser;
		
		try {
		  /** Encriptamos el password **/
			MessageDigest md = null;
			String password = userPassword;
			md= MessageDigest.getInstance("MD5");
		    md.update(password.getBytes());
		    byte[] mb = md.digest();
		    userPassword = new String (Hex.encodeHex(mb));
		    params.setUserPassword(userPassword);
		        
		    Context initialContext;
			initialContext = new InitialContext();
			ManageUsers mu = (es.ual.acg.cos.controllers.ManageUsers)initialContext.lookup("java:app/cos/ManageUsers");
			idUser=mu.queryUser(userName, userPassword);
			
			if(idUser != -1) {
				result.setValidation(true);
				result.setIduser(idUser);
				result.setMessage("> Successfully validation");
			}
        
		} catch (NoSuchAlgorithmException e) {
			LOGGER.error(e);
			result.setMessage("> Internal Server Error");
		} catch (SQLException e){
			LOGGER.error(e);
			result.setMessage("> " + e);
		} catch (ClassNotFoundException e){
			LOGGER.error(e);
			result.setMessage("> Internal Server Error");
		} catch (NamingException e) {
			LOGGER.error(e);
			result.setMessage("> Internal Server Error");
		}
			
		return result;
	}

	public QueryProfileResult queryProfile() {
		QueryProfileResult result = new QueryProfileResult();
		List<String> perfiles = new ArrayList<String>();
		
		result.setValidation(false);
		result.setProfiles(null);
		result.setMessage("> Not found o Empty profile Error");
			
		try {
			        
		    Context initialContext;
			initialContext = new InitialContext();
			ManageUsers mu = (es.ual.acg.cos.controllers.ManageUsers)initialContext.lookup("java:app/cos/ManageUsers");
			perfiles=mu.queryProfile();
			
			if(perfiles.get(0) != null) {
				result.setValidation(true);
				result.setProfiles(perfiles);
				result.setMessage("> Successfully request");
			}

		} catch (SQLException e){
			LOGGER.error(e);
			result.setMessage("> " + e);
		} catch (ClassNotFoundException e){
			LOGGER.error(e);
			result.setMessage("> Internal Server Error");
		} catch (NamingException e) {
			LOGGER.error(e);
			result.setMessage("> Internal Server Error");
		}
		return result;
	}
	
	public InterModulesData queryCamUser(String userID){
		Context initialContext;
		InterModulesData resultquerycam = new InterModulesData();
		resultquerycam.setValue("-1");
		
		try {
			initialContext = new InitialContext();
			ManageUsers mu = (es.ual.acg.cos.controllers.ManageUsers)initialContext.lookup("java:app/cos/ManageUsers");

			String cam = mu.queryCamUser(userID);

			resultquerycam.setValue(cam); //Devuelve el cam
			resultquerycam.setMessage("> CAM OK");

			
		} catch (NamingException e) {
			LOGGER.error(e);
			resultquerycam.setMessage("> Internal Server Error");
		} catch (SQLException e){
			LOGGER.error(e);
			resultquerycam.setMessage("> " + e);
		} catch (ClassNotFoundException e){
			LOGGER.error(e);
			resultquerycam.setMessage("> Internal Server Error");
		}
		return resultquerycam;
	}
	public InterModulesData queryCamProfile(String profileName){
		Context initialContext;
		InterModulesData resultquerycam = new InterModulesData();
		resultquerycam.setValue("-1");
		
		try {
			initialContext = new InitialContext();
			ManageUsers mu = (es.ual.acg.cos.controllers.ManageUsers)initialContext.lookup("java:app/cos/ManageUsers");

			String cam = mu.queryCamProfile(profileName);

			resultquerycam.setValue(cam); //Devuelve el cam
			resultquerycam.setMessage("> CAM OK");

			
		} catch (NamingException e) {
			LOGGER.error(e);
			resultquerycam.setMessage("> Internal Server Error");
		} catch (SQLException e){
			LOGGER.error(e);
			resultquerycam.setMessage("> " + e);
		} catch (ClassNotFoundException e){
			LOGGER.error(e);
			resultquerycam.setMessage("> Internal Server Error");
		}
		return resultquerycam;
	}
	public DeleteUserResult deleteUser(String userID) {
		DeleteUserResult result = new DeleteUserResult();
		boolean queryresult;
		result.setDeleted(false);
		result.setMessage("> Not found o Empty userid Error");
		
		try {
		    Context initialContext;
			initialContext = new InitialContext();
			ManageUsers mu = (es.ual.acg.cos.controllers.ManageUsers)initialContext.lookup("java:app/cos/ManageUsers");
			queryresult=mu.deleteUser(userID);
			
			result.setDeleted(queryresult);
			if (queryresult)
				result.setMessage("> Successfully deleted");
			
		} catch (SQLException e){
			LOGGER.error(e);
			result.setMessage("> " + e);
		} catch (Exception e){
			LOGGER.error(e);
			result.setMessage("> Internal Server Error"+ e);
		}
			
		return result;
	}
	public UpdateUserResult updateUser(String userID, String userName, String userPassword, String userProfile) {
		UpdateUserResult result = new UpdateUserResult();
		InterModulesData resultUIM = new InterModulesData();
		InterModulesData resultDMM = new InterModulesData();
		String camID;
		WidgetData widgetData = null;
		WidgetData widgetData2 = null;
		ConcreteArchitecturalModel cam = null;
		String componentName = "";
    	String componentAlias = "";
		boolean queryresult;
		result.setUpdated(false);
		result.setMessage("> Not found o Empty userid Error");
		
		try {
		  /** Encriptamos el password **/
			MessageDigest md = null;
			String password = userPassword;
			md= MessageDigest.getInstance("MD5");
		    md.update(password.getBytes());
		    byte[] mb = md.digest();
		    userPassword = new String (Hex.encodeHex(mb));
		        
		    Context initialContext;
			initialContext = new InitialContext();
			ManageUsers mu = (es.ual.acg.cos.controllers.ManageUsers)initialContext.lookup("java:app/cos/ManageUsers");
			queryresult=mu.updateUser(userID, userName, userPassword, userProfile);
			
			result.setUpdated(queryresult);
			if (queryresult){
				//ACTUALIZAR ID INSTANCIAS CON EL NUEVO USUARIO
				//Cambiamos las intancias de lo componentes de la copia del modelo para este nuevo usuario

				//Obtenemos el cam para ese usuario actualizado
				resultUIM = this.queryCamUser(userID);
				
				if (!resultUIM.getValue().equalsIgnoreCase("-1")){ //Sino hay error en cam
					camID = resultUIM.getValue();
					

					//Obtenemos el modelo para ese cam
					try {
						DMM dmm = (es.ual.acg.cos.modules.DMM)initialContext.lookup("java:app/cos/DMM");
						resultDMM = dmm.ReadModelforcamId(camID);
						cam = resultDMM.getCam();

						
					} catch (Exception e) {
						LOGGER.error(e);
						result.setMessage(resultDMM.getMessage());
					} 

					if(cam != null){
					//Clonar objeto antes de guardarlo en la base de datos
					ConcreteArchitecturalModel camCopy = EcoreUtil.copy(cam);
				  	camCopy.setCamID(camID);
				  	try{
					    for (int i = 0; i < camCopy.getConcreteComponent().size(); i++) {
					    	componentName = camCopy.getConcreteComponent().get(i).getComponentName();
					    	componentAlias = camCopy.getConcreteComponent().get(i).getComponentAlias();
							
					    	//Obtenemos indentificador Wookie
							try {
								DMM dmm = (es.ual.acg.cos.modules.DMM)initialContext.lookup("java:app/cos/DMM");
								resultDMM = dmm.getWidget(userName, componentName, componentAlias);
								widgetData = resultDMM.getWidget();
									
							} catch (Exception e) {
								LOGGER.error(e);
								result.setMessage(resultDMM.getMessage());
							}
							
							//Cambiamos el id de instancia del componente
							camCopy.getConcreteComponent().get(i).setComponentInstance(widgetData.getIdentifier());
							
							//Cambiamos el id de instancia de los servicios del componente
							
							//Obtenemos el numero de servicios
							int numeroServicios = 0;
							for (int j = 0; j < camCopy.getConcreteComponent().get(i).getRuntimeProperty().size(); j++) {
								if ( camCopy.getConcreteComponent().get(i).getRuntimeProperty().get(j).getPropertyID().
									 equalsIgnoreCase("numero_servicios")) {
									
									numeroServicios = Integer.parseInt(camCopy.getConcreteComponent().get(i).
								        			  getRuntimeProperty().get(j).getPropertyValue());
								}
							}
							
							if(numeroServicios > 1){ //Si hay servicios agrupados
								for (int k = 1; k <= numeroServicios; k++) {
								    String service = "";
								    String serviciopropiedad= "";
								    String instaciaservicio= "";
								    String nameservicio= "";
								    String aliasservicio= "";
									for (int j = 0; j < camCopy.getConcreteComponent().get(i).getRuntimeProperty().size(); j++) {
										String property = camCopy.getConcreteComponent().get(i).getRuntimeProperty().get(j).
												          getPropertyID();
									    if (property.length() > 9)
										        service = property.substring(0, 8);
	
										if (service.equalsIgnoreCase("servicio"+k)) {
										        serviciopropiedad = property.substring(10, 14);
										        if (service.equalsIgnoreCase("name")) 
										        	nameservicio = camCopy.getConcreteComponent().get(i).
								        			  getRuntimeProperty().get(j).getPropertyValue();
										        if (service.equalsIgnoreCase("alia")) 
										        	aliasservicio = camCopy.getConcreteComponent().get(i).
								        			  getRuntimeProperty().get(j).getPropertyValue();
										 }
									}
									//Obtenemos indentificador Wookie
									try {
										DMM dmm = (es.ual.acg.cos.modules.DMM)initialContext.lookup("java:app/cos/DMM");
										resultDMM = dmm.getWidget(userName, nameservicio, aliasservicio);
										widgetData2 = resultDMM.getWidget();
									} catch (Exception e) {
										LOGGER.error(e);
										result.setMessage(resultDMM.getMessage());
									}

									instaciaservicio=widgetData2.getIdentifier();
									for (int j = 0; j < camCopy.getConcreteComponent().get(i).getRuntimeProperty().size(); j++) {
										String property = camCopy.getConcreteComponent().get(i).getRuntimeProperty().get(j).
												          getPropertyID();
									    if (property.length() > 9)
										        service = property.substring(0, 8);
	
										if (service.equalsIgnoreCase("servicio"+k)) {
										        serviciopropiedad = property.substring(10, 14);
										        if (service.equalsIgnoreCase("inst")) 
										        	camCopy.getConcreteComponent().get(i).
										        			  getRuntimeProperty().get(j).setPropertyValue(instaciaservicio);

										 }
									}
								}
							}
							
							//cambiamos la instamncia del servicio base
							for (int j = 0; j < camCopy.getConcreteComponent().get(i).getRuntimeProperty().size(); j++) {
								String property = camCopy.getConcreteComponent().get(i).getRuntimeProperty().get(j).getPropertyID();
							    
								if (property.equalsIgnoreCase("servicio0.instancia")) {
									camCopy.getConcreteComponent().get(i).getRuntimeProperty().get(j).
								        setPropertyValue(widgetData.getIdentifier());;
								 }
							}
								
						}//For para cada componente

					} catch (Exception e) {
						LOGGER.error("> Error in Wookie");
						result.setMessage("> Internal Server Error");
					}
					
					try {
						DMM dmm = (es.ual.acg.cos.modules.DMM)initialContext.lookup("java:app/cos/DMM");
						
						resultDMM = dmm.DeleteModelforcamId(camID); //Borramos el cam anterior para que no haya conflicto con el camID
						
						if (!resultDMM.getValue().equalsIgnoreCase("-1")){
						
							resultDMM = dmm.SaveModelforcamId(camCopy); //Salvamos el cam modificado
						
							if (!resultDMM.getValue().equalsIgnoreCase("-1")){
								result.setMessage("> Successfully updated");
							}else {
								result.setUpdated(false);
								result.setMessage(resultUIM.getMessage());
								LOGGER.error(resultUIM.getMessage());
							}
						}else {
							result.setUpdated(false);
							result.setMessage(resultUIM.getMessage());
							LOGGER.error(resultUIM.getMessage());
						}
							
					} catch (Exception e) {
						LOGGER.error(e);
						result.setMessage(resultDMM.getMessage());
					}
				}
				
			}else{ //Errores producidos en UIM y en ManagerUSer
				result.setUpdated(false);
				result.setMessage(resultUIM.getMessage());
			}
		}
        
		} catch (SQLException e){
			LOGGER.error(e);
			result.setMessage("> " + e);
		} catch (Exception e){
			LOGGER.error(e);
			result.setMessage("> Internal Server Error"+ e);
		}
			
		return result;
	}
}
