package xyz.iconc.dev.server.security;

import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.codec.Base64;
import org.apache.shiro.config.ConfigurationException;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.realm.jdbc.JdbcRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.apache.shiro.util.JdbcUtils;
import org.apache.shiro.util.Nameable;
import org.slf4j.Logger;
import xyz.iconc.dev.server.DatabaseManager;
import xyz.iconc.dev.server.Server;
import xyz.iconc.dev.server.utilities.Utility;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Objects;
import java.util.Set;

public class DatabaseRealm extends AuthorizingRealm implements Realm, Nameable  {

    private String name;


    private static final Logger log = Utility.GetLogger(DatabaseRealm.class);

    /**
     * The default query used to retrieve account data for the user.
     */
    private static final String AUTHENTICATION_QUERY = "SELECT hashed_password FROM accounts " +
            "where user_identifier = ? LIMIT 1";

    /**
     * The default query used to retrieve account data for the user when
     */
    private static final String SALTED_AUTHENTICATION_QUERY = "SELECT hashed_password, salt FROM accounts" +
            " WHERE user_identifier = ? LIMIT 1";

    /**
     * The default query used to retrieve the roles that apply to a user.
     */
    private static final String USER_ROLES_QUERY = "SELECT user_role FROM accounts" +
            " WHERE user_identifier = ? LIMIT 1";

    /**
     * The default query used to retrieve permissions that apply to a particular role.
     */
    private static final String PERMISSIONS_QUERY = "SELECT permission FROM roles_permissions" +
            " WHERE role_name = ? LIMIT 1";

    private DatabaseManager databaseManager;
    private Connection connection;



    public DatabaseRealm() {
        super();
        databaseManager = Objects.requireNonNull(Server.getServerInstance()).getDatabaseManager();
        connection = databaseManager.getConnection();
    }

    private boolean isReady() {
        return databaseManager.isReady();
    }


    /**
     * Query's database for hashed password and salt, returns password and salt
     * @param userIdentifier The user identifier to get password and salt from
     * @return password and salt
     * @throws SQLException Thrown if issues with fetching results.
     */
    private PasswordSaltObject getPasswordForUserIdentifier(long userIdentifier) throws SQLException {
        if (!isReady()) throw new SQLException("Database connector not initialized.");


        PasswordSaltObject result;

        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = connection.prepareStatement(AUTHENTICATION_QUERY);
            ps.setLong(1, userIdentifier);

            rs = ps.executeQuery();

            boolean resultFound = rs.next();
            if (!resultFound) throw new SQLException();

            result = new PasswordSaltObject(rs.getString(1),
                    rs.getString(2));

        } finally {
            JdbcUtils.closeResultSet(rs);
            JdbcUtils.closeStatement(ps);
        }

        return result;
    }


    /**
     *
     * @param token CustomAuthentication Token Containing the User Identifier, Password, and Salt
     * @return Authentication Information
     * @throws AuthenticationException Thrown if account identifier not specified
     */
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        if (!supports(token))
            throw new AuthenticationException("Token type of CustomAuthenticationToken required");

        long userIdentifier = (long) token.getPrincipal();

        if (userIdentifier == 0L) {
            throw new AccountException("Account identifier required.");
        }

        PasswordSaltObject credentials = (PasswordSaltObject) token.getCredentials();

        SimpleAuthenticationInfo info;
        String password = credentials.getHashedPassword();
        String salt = credentials.getSalt();

        info = new SimpleAuthenticationInfo(userIdentifier, password.toCharArray(), getName());

        info.setCredentials(credentials);
        info.setCredentialsSalt(ByteSource.Util.bytes(salt.toCharArray()));

        return info;
    }



    /**
     * This implementation of the interface expects the principals collection to return a String username keyed off of
     * this realm's {@link #getName() name}
     *
     * @param principals
     * @see #getAuthorizationInfo(PrincipalCollection)
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {


        principals.asList().get(0);
        //null usernames are invalid
        if (principals == null) {
            throw new AuthorizationException("PrincipalCollection method argument cannot be null.");
        }
        return null;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public void setName(String s) {
        this.name = s;
    }

    @Override
    public boolean supports(AuthenticationToken token) {
        return token instanceof CustomAuthenticationToken;
    }


    public static class PasswordSaltObject {
        private final String hashedPassword;
        private final String salt;

        public PasswordSaltObject(String _hashedPassword, String _salt) {
            hashedPassword = _hashedPassword;
            salt = _salt;
        }

        public String getHashedPassword() {
            return hashedPassword;
        }

        public String getSalt() {
            return salt;
        }
    }
}
