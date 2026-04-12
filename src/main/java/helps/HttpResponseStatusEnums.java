package helps;

import javax.ws.rs.core.Response.StatusType;
import javax.ws.rs.core.Response.Status.Family;


public enum HttpResponseStatusEnums implements StatusType {
	STOPPEDCONTEST(410,"Contest is stopped"),
	NOTVALIDPROBLEM(411,"Not a valid problem"),
	NOTVALIDLANGUAGE(412,"Not a valid language"),
	NOTVALIDCLARIFICATION(413,"Not a valid clarificatio"),
	NOTVALIDMESSAGE(414,"Not a valid message string"),
	NOTVALIDCOOKIEID(415,"Not a valid cookie id"),
	NOCOOCKIESFOUND(416,"No cookies found"),
	NOCOOKIEFIELDFOUND(417,"Cookies requierd field not found"),
	PC2CONNECTIONFAILED(503,"Can not connect to pc2");
	
	private final int _code;
	private final String _reason;
	private final Family _family;

	HttpResponseStatusEnums(final int statusCode,
	       final String reasonPhrase) {
		_code = statusCode;
		_reason = reasonPhrase;
		_family = Family.familyOf(statusCode);
	}

	@Override
	public Family getFamily() {
		// TODO Auto-generated method stub
		return _family;
	}

	@Override
	public String getReasonPhrase() {
		// TODO Auto-generated method stub
		return toString();
	}

	@Override
	public int getStatusCode() {
		// TODO Auto-generated method stub
		return _code;
	}
	
	@Override
	public String toString() {
		return _reason;
	}

	/**
	 * Converts a numerical status code into the corresponding Status. If
	 * this returns <code>null</code>, ask
	 * {@link javax.ws.rs.core.Response.Status#fromStatusCode(int) javax's
	 * implementation}, maybe it will know what Status the
	 * <code>statusCode</code> is for.
	 *
	 * @param statusCode
	 *            the numerical status code.
	 * @return the matching Status or <code>null</code> is no matching
	 *         Status is defined.
	 */
	public static HttpResponseStatusEnums fromStatusCode(final int statusCode) {
		for (HttpResponseStatusEnums status : HttpResponseStatusEnums.values()) {
			if (status.getStatusCode() == statusCode) {
				return status;
			}
		}
		return null;
	}
}
