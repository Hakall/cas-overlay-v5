package org.apereo.cas.adaptors.esupotp.web.flow;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.xml.bind.annotation.adapters.HexBinaryAdapter;

import org.apereo.cas.web.support.WebUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;
import org.springframework.webflow.action.AbstractAction;
import org.springframework.webflow.action.EventFactorySupport;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;
import org.springframework.webflow.execution.RequestContextHolder;

import org.apereo.cas.adaptors.esupotp.EsupOtpMethod;

/**
 * This is {@link EsupOtpGetTransportsAction}.
 *
 * @author Alex Bouskine
 * @since 5.0.0
 */
@RefreshScope
@Component("esupotpGetTransportsAction")
public class EsupOtpGetTransportsAction extends AbstractAction {

	@Value("${cas.mfa.esupotp.urlApi:CAS}")
	private String urlApi;

	@Value("${cas.mfa.esupotp.usersSecret:CAS}")
	private String usersSecret;

	@Value("${cas.mfa.esupotp.apiPassword:CAS}")
	private String apiPassword;

	@Override
	protected Event doExecute(final RequestContext requestContext) throws Exception {
		final RequestContext context = RequestContextHolder.getRequestContext();
		final String uid = WebUtils.getAuthentication(context).getPrincipal().getId();

		requestContext.getFlowScope().put("uid", uid);
		requestContext.getFlowScope().put("userHash", getUserHash(uid));
		
		JSONObject userInfos = getUserInfos(uid);
		List<EsupOtpMethod> listMethods = new ArrayList<EsupOtpMethod>();
		try {
			JSONObject methods = (JSONObject) ((JSONObject) userInfos.get("user")).get("methods");
			for (Object method : methods.keySet()) {
				listMethods.add(new EsupOtpMethod((String) method, (JSONObject) methods.get((String) method)));
			}
			if(bypass(listMethods))return new EventFactorySupport().event(this, "bypass");
			if(skipTransports(listMethods))return new EventFactorySupport().event(this, "skip");
		} catch (JSONException e) {
			System.out.println(e);
		}

		requestContext.getFlowScope().put("userInfos", userInfos.toString());
		requestContext.getFlowScope().put("methods", listMethods);

		return new EventFactorySupport().event(this, "transports");
	}

	private JSONObject getUserInfos(String uid) throws IOException, NoSuchAlgorithmException {
		String url = urlApi + "/users/" + uid + "/" + getUserHash(uid);
		URL obj = new URL(url);
		int responseCode;
		HttpURLConnection con = null;
		con = (HttpURLConnection) obj.openConnection();
		con.setRequestMethod("GET");
		responseCode = con.getResponseCode();
		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();

		return new JSONObject(response.toString());
	}

	public String getUserHash(String uid) throws NoSuchAlgorithmException, UnsupportedEncodingException {
		MessageDigest md5Md = MessageDigest.getInstance("MD5");
		String md5 = (new HexBinaryAdapter()).marshal(md5Md.digest(usersSecret.getBytes()));
		md5 = md5.toLowerCase();
		String salt = md5 + getSalt(uid);
		MessageDigest sha256Md = MessageDigest.getInstance("SHA-256");
		String userHash = (new HexBinaryAdapter()).marshal(sha256Md.digest(salt.getBytes()));
		userHash = userHash.toLowerCase();
		return userHash;
	}

	public String getSalt(String uid) {
		Calendar calendar = Calendar.getInstance();
		int day = calendar.get(Calendar.DAY_OF_MONTH);
		int hour = calendar.get(Calendar.HOUR_OF_DAY);
		String salt = uid + day + hour;
		return salt;
	}
	
	public Boolean skipTransports(List<EsupOtpMethod> methods) throws JSONException, IOException{
		Boolean skip = true;
		for (EsupOtpMethod method : methods) {
			if(method.getActive() && method.getTransports().size()>0){
				skip = false;
			}
		}
		return skip;
	}
	
	public Boolean bypass(List<EsupOtpMethod> methods) throws JSONException, IOException{
		Boolean bypass = true;
		for (EsupOtpMethod method : methods) {
			if(method.getActive()){
				bypass = false;
			}
		}
		return bypass;
	}
}
