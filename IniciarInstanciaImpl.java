
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import commonj.sdo.DataObject;
import com.ibm.websphere.sca.ServiceManager;

import com.bgba.services.RestService;

import com.bgba.api.genericstartprocess.classes.*;

import com.main.DataSourceConector;
import com.main.logger.Log;

public class IniciarInstanciaImpl {

	public IniciarInstanciaImpl() {
		super();
	}

	@SuppressWarnings("unused")
	private Object getMyService() {
		return (Object) ServiceManager.INSTANCE.locateService("self");
	}

	public DataObject iniciarInstancia(DataObject inputIniciarInstancia) {	
		Log.logTomAplicaciones_info(inputIniciarInstancia.getString("uuid") + ";Java;iniciarInstancia;Inicio");	
		com.ibm.websphere.bo.BOFactory boFactory = (com.ibm.websphere.bo.BOFactory) ServiceManager.INSTANCE.locateService("com/ibm/websphere/bo/BOFactory");
		DataObject out_iniciarInstancia = boFactory.create("http://OM_RequiereProceso_Lib","out_IniciarInstancia");
		try{
			RestService rest = new RestService();
			ProcesoGenerico procesoGenerico = new ProcesoGenerico();	
			
			DataObject getProcessData = obtenerNombreProcesoParametrizado(inputIniciarInstancia.getInt("circuitoProceso"));
			if(getProcessData != null){
				procesoGenerico.setSolicitud(inputIniciarInstancia.getString("solicitud"));
				procesoGenerico.setSolicitudCabecera(inputIniciarInstancia.getString("solicitudCabecera"));
				procesoGenerico.setProximoPaso(inputIniciarInstancia.getString("proximoPaso"));
				procesoGenerico.setCircuito(getProcessData.getString("circuito"));	
				String processID = rest.startProcessByProcessName(getProcessData.getString("nombre"), procesoGenerico, 857);			
				out_iniciarInstancia.setString("codigo", processID);
				out_iniciarInstancia.setString("descripcion", "processID = 0 > Error / processID != 0 > OK");
				out_iniciarInstancia.setString("processId", processID);
				out_iniciarInstancia.setString("processName", getProcessData.getString("nombre"));
			}else{
				Log.logTomAplicaciones_debug(inputIniciarInstancia.getString("uuid") + ";Java;iniciarInstancia;No se encontro ningun proceso parametrizado para el id circuito " + inputIniciarInstancia.getInt("circuitoProceso"));				
			}
		}catch (Exception e){		
			Log.logTomAplicaciones_error(inputIniciarInstancia.getString("uuid") + ";Java;iniciarInstancia;Error al iniciar instancia. " + e.toString());
		}

		Log.logTomAplicaciones_info(inputIniciarInstancia.getString("uuid") + ";Java;iniciarInstancia;Fin");
		return out_iniciarInstancia;
	}
	
	private DataObject obtenerNombreProcesoParametrizado(int circuitoProceso){
		com.ibm.websphere.bo.BOFactory boFactory = (com.ibm.websphere.bo.BOFactory) ServiceManager.INSTANCE.locateService("com/ibm/websphere/bo/BOFactory");
		DataObject salida = boFactory.create("http://OM_RequiereProceso_Lib","DataProcess");
		
		String query = "SELECT ACRONIMO_PROCESO as nombreProceso, CIRCUITO as circuito FROM OM_CIRCUITO WHERE ID = " + circuitoProceso;
		Log.logTomAplicaciones_debug("NON-UUID;Java;obtenerNombreProcesoParametrizado;Query: " + query);
		DataSourceConector dsconn = new DataSourceConector();
		Connection conn = dsconn.crearConexion_procesoCILIMASIVO();
		PreparedStatement ps = null;
		ResultSet rs = null;
		try{
			ps = conn.prepareStatement(query);
			rs = ps.executeQuery();
			while(rs.next()){
				salida.setString("nombre", rs.getString("nombreProceso"));
				salida.setString("circuito", rs.getString("circuito"));
			}
		}catch(SQLException e){
			Log.logTomAplicaciones_error("NON-UUID;Java;iniciarInstancia;Error al obtener nombre de proceso parametrizado. " + e.toString());
		}finally{
			try{
				rs.close();
				ps.close();
				conn.close();
			}catch(SQLException e){
				
			}
		}
		return salida;
	}

}