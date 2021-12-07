package xyz.iconc.dev.server.security;

import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.UsernamePasswordToken;

public class CustomAuthenticationToken implements AuthenticationToken {
    private long principal; // User identifier
    private DatabaseRealm.PasswordSaltObject credentials; // User password + salt


    /**
     * Creates new instance of authentication token.
     * @param _principal user identifier
     * @param _credentials user credentials
     */
    public CustomAuthenticationToken(long _principal, DatabaseRealm.PasswordSaltObject _credentials) {
        principal = _principal;
        credentials = _credentials;
    }


    /**
     * Returns the account identity submitted during the authentication process.
     * <p/>
     * <p>Most application authentications are username/password based and have this
     * object represent a username.  If this is the case for your application,
     * take a look at the {@link UsernamePasswordToken UsernamePasswordToken}, as it is probably
     * sufficient for your use.
     * <p/>
     * <p>Ultimately, the object returned is application specific and can represent
     * any account identity (user id, X.509 certificate, etc).
     *
     * @return the account identity submitted during the authentication process.
     * @see UsernamePasswordToken
     */
    @Override
    public Object getPrincipal() {
        return principal;
    }

    /**
     * Returns the credentials submitted by the user during the authentication process that verifies
     * the submitted {@link #getPrincipal() account identity}.
     * <p/>
     * <p>Most application authentications are username/password based and have this object
     * represent a submitted password.  If this is the case for your application,
     * take a look at the {@link UsernamePasswordToken UsernamePasswordToken}, as it is probably
     * sufficient for your use.
     * <p/>
     * <p>Ultimately, the credentials Object returned is application specific and can represent
     * any credential mechanism.
     *
     * @return the credential submitted by the user during the authentication process.
     */
    @Override
    public Object getCredentials() {
        return credentials;
    }
}
