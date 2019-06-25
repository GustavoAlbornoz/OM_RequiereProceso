package requiereProceso;

import commonj.sdo.DataObject;
import com.ibm.websphere.sca.ServiceManager;

public class Component1Impl {
	/**
	 * Default constructor.
	 */
	public Component1Impl() {
		super();
	}

	/**
	 * Return a reference to the component service instance for this implementation
	 * class.  This method should be used when passing this service to a partner reference
	 * or if you want to invoke this component service asynchronously.    
	 *
	 * @generated (com.ibm.wbit.java)
	 */
	@SuppressWarnings("unused")
	private Object getMyService() {
		return (Object) ServiceManager.INSTANCE.locateService("self");
	}

	/**
	 * Method generated to support implementation of operation "requiereProceso" defined for WSDL port type 
	 * named "RequiereProceso".
	 * 
	 * The presence of commonj.sdo.DataObject as the return type and/or as a parameter 
	 * type conveys that it is a complex type. Please refer to the WSDL Definition for more information 
	 * on the type of input, output and fault(s).
	 */
	public DataObject requiereProceso(DataObject inputRequiereProceso) {
		// To create a DataObject, use the creation methods on the BOFactory:
		// com.ibm.websphere.bo.BOFactory boFactory = (com.ibm.websphere.bo.BOFactory) ServiceManager.INSTANCE.locateService("com/ibm/websphere/bo/BOFactory");
		//
		// To get or set attributes for a DataObject such as inputRequiereProceso, use the APIs as shown below:
		// To set a string attribute in inputRequiereProceso, use inputRequiereProceso.setString(stringAttributeName, stringValue)
		// To get a string attribute in inputRequiereProceso, use inputRequiereProceso.getString(stringAttributeName)
		// To set a dataObject attribute in inputRequiereProceso, use inputRequiereProceso.setDataObject(stringAttributeName, dataObjectValue)
		// To get a dataObject attribute in inputRequiereProceso, use inputRequiereProceso.getDataObject(stringAttributeName)
		
		com.ibm.websphere.bo.BOFactory boFactory = (com.ibm.websphere.bo.BOFactory) ServiceManager.INSTANCE.locateService("com/ibm/websphere/bo/BOFactory");
		DataObject salida = boFactory.create("http://OM_RequiereProceso_Lib","out_requiereProceso");
		salida.setString("circuitoExclusa", "circuitoExclusa");
		salida.setString("circuitoOriginal", "circuitoOriginal");
		salida.setString("idSolicitud", "idSolicitud");
		salida.setString("porcentajeDeExclusa", "porcentajeDeExclusa");
		return salida;
	}

}