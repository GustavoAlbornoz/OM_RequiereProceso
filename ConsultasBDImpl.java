import java.math.BigDecimal;
import java.sql.*;
import java.util.*;

import commonj.sdo.DataObject;
import com.ibm.websphere.sca.ServiceManager;
import com.main.DataSourceConector;
import com.main.logger.Log;

public class ConsultasBDImpl {

	public ConsultasBDImpl() {
		super();
	}

	@SuppressWarnings("unused")
	private Object getMyService() {
		return (Object) ServiceManager.INSTANCE.locateService("self");
	}

	public String insertarChecklist(DataObject inputInsertarChecklist) {
		String infoLog = inputInsertarChecklist.getString("uuid") + ";JAVA;insertarChecklist;";
		Log.logTomAplicaciones_info(infoLog + "Inicio");

		String solicitud = inputInsertarChecklist.getString("nroSolicitud");
		int idCircuito = inputInsertarChecklist.getInt("idCircuito");
		String semaforo = inputInsertarChecklist.getString("semaforo");
		String canal = inputInsertarChecklist.getString("canal");
		int idAccion = inputInsertarChecklist.getInt("idAccion");

		List<DataObject> listaDocumentos = inputInsertarChecklist.getList("documentos");
		com.ibm.websphere.bo.BOFactory boFactory = (com.ibm.websphere.bo.BOFactory) ServiceManager.INSTANCE.locateService("com/ibm/websphere/bo/BOFactory");

		String xml = "<XML><DOCUMENTOS>";
		try {
			for (DataObject documento : listaDocumentos) {
				BigDecimal idHost = documento.getDataObject("integrante") != null ? documento.getDataObject("integrante").getBigDecimal("idHost") : null;
				Integer idTipoDocumental = documento.getInt("idDocumento");
				int visibleCanal = documento.getBoolean("interno") ? 0 : 1; //Si es interno, entonces el canal no lo puede ver
				int nroIntegrante = documento.getDataObject("integrante") != null && documento.getDataObject("integrante").getBoolean("responsableImpositivo") ? 1 : 2;
				String idEstado = documento.getString("etapaProceso");
				String obligatoriedad = documento.getString("obligatoriedad");
				String descripcionGenerica = documento.getString("nombreDocumento");
				String descripcion = documento.getString("descripcionExtendida");
				xml += "<DOCUMENTO>";
				xml += descripcion != null ? "<DESCRIPCION>" + descripcion + "</DESCRIPCION>" : "<DESCRIPCION></DESCRIPCION>";
				xml += idTipoDocumental != null ? "<ID_TIPO_DOCUMENTAL>" + idTipoDocumental + "</ID_TIPO_DOCUMENTAL>" : "<ID_TIPO_DOCUMENTAL></ID_TIPO_DOCUMENTAL>";
				xml += idHost != null ? "<IDHOST>" + idHost + "</IDHOST>" : "<IDHOST></IDHOST>";
				xml += "<NRO_INTEGRANTE>" + nroIntegrante + "</NRO_INTEGRANTE>";
				xml += idEstado != null ? "<ID_ESTADO>" + idEstado + "</ID_ESTADO>" : "<ID_ESTADO></ID_ESTADO>";
				xml += obligatoriedad != null ? "<OBLIGATORIEDAD>" + obligatoriedad + "</OBLIGATORIEDAD>" : "<OBLIGATORIEDAD></OBLIGATORIEDAD>";
				xml += descripcionGenerica != null ? "<DESCRIPCION_GENERICA>" + descripcionGenerica + "</DESCRIPCION_GENERICA>" : "<DESCRIPCION_GENERICA></DESCRIPCION_GENERICA>";
				xml += "<VISIBLE_CANAL>" + visibleCanal + "</VISIBLE_CANAL>";
				xml += "</DOCUMENTO>";
			}
			xml += ("</DOCUMENTOS></XML>");
			Log.logTomAplicaciones_info(infoLog + "XML a la bd: " + xml);
		} catch (Exception e) {
			e.printStackTrace();
			Log.logTomAplicaciones_error(infoLog + "Rompio armado de XML: " + e.toString());
			return "0";
		}

		String queryString = "{CALL [dbo].[sp_InsertarDocumentosADigitalizar](?,?,?,?,?,?,?)}";
		DataSourceConector connector = new DataSourceConector();
		Connection connection = connector.crearConexion_procesoCILIMASIVO();
		CallableStatement callableStatement = null;
		try {
			callableStatement = connection.prepareCall(queryString);
			callableStatement.setString(1, xml);
			callableStatement.setString(2, solicitud);
			callableStatement.setInt(3, idCircuito);
			callableStatement.setString(4, semaforo);
			callableStatement.setString(5, canal);
			callableStatement.setInt(6, idAccion);
			callableStatement.registerOutParameter(7, java.sql.Types.VARCHAR);
			callableStatement.execute();
			Log.logTomAplicaciones_info(infoLog + "Captura bd: " + callableStatement.getString("Captura"));
		} catch (SQLException e) {
			e.printStackTrace();
			Log.logTomAplicaciones_error(infoLog + "Rompio BD: " + e.toString());
			return "0";
		} finally {
			try {
				callableStatement.close();
			} catch (SQLException e) {
			}
			try {
				connection.close();
			} catch (SQLException e) {
			}
		}
		Log.logTomAplicaciones_info(infoLog + "Fin");
		return "1";
	}

	public void updateInstancia(DataObject updateInstancia) {
		String infoLog = updateInstancia.getString("uuid") + ";JAVA;updateInstancia;";
		Log.logTomAplicaciones_info(infoLog + "Inicio");

		DataSourceConector dsconn = new DataSourceConector();
		Connection conn = dsconn.crearConexion_procesoCILIMASIVO();
		String query = "UPDATE [ProcesoCILI].[dbo].[OM_SOLICITUD] " +
					   "SET INSTANCIA = "	+ updateInstancia.getString("nroInstancia")	+ ", VERSION_BPM = '857', PROCESO = (SELECT TOP 1 PROCESO FROM OM_CIRCUITO WHERE ACRONIMO_PROCESO = '" + updateInstancia.getString("nombreProceso") + "') " +
					   "WHERE NRO_SOLICITUD = '" + updateInstancia.getString("nroSolicitud") + "'";
		Log.logTomAplicaciones_info(infoLog + "Query: " + query);
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement(query);
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
			Log.logTomAplicaciones_error(infoLog + "Rompio BD: " + e.toString());
		} finally {
			try {
				ps.close();
			} catch (SQLException e) {
			}
			try {
				conn.close();
			} catch (SQLException e) {
			}
		}
	}

}