package hudson.plugins.redmine.dao;

import hudson.plugins.redmine.RedmineAuthenticationException;
import hudson.plugins.redmine.RedmineGroupData;
import hudson.plugins.redmine.RedmineUserData;
import hudson.plugins.redmine.util.Constants;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

/**
 * @author Yasuyuki Saito
 */
public class MySQLAuthDao extends AbstractAuthDao {

    @Override
    public void open(String dbServer, String port, String databaseName, String dbUserName, String dbPassword)
            throws RedmineAuthenticationException {
        try {
            String connectionString = String.format(Constants.CONNECTION_STRING_FORMAT_MYSQL, dbServer, port, databaseName);

            Class.forName(Constants.JDBC_DRIVER_NAME_MYSQL).newInstance();
            conn = DriverManager.getConnection(connectionString, dbUserName, dbPassword);
        } catch (SQLException e) {
            throw new RedmineAuthenticationException("RedmineSecurity: Connection Error", e);
        } catch (Exception e) {
            throw new RedmineAuthenticationException("RedmineSecurity: Connection Error", e);
        }
    }

    @Override
    public boolean isTable(String table) throws RedmineAuthenticationException {
        PreparedStatement state = null;
        ResultSet results = null;

        try {
            String query = "SHOW TABLES";
            state = conn.prepareStatement(query);
            results = state.executeQuery();

            if (results == null)
                return false;

            while (results.next()) {
                if (results.getString(1).equals(table))
                    return true;
            }

            return false;
        } catch (RedmineAuthenticationException e) {
            throw e;
        } catch (SQLException e) {
            throw new RedmineAuthenticationException("RedmineSecurity: Table Check Error", e);
        } catch (Exception e) {
            throw new RedmineAuthenticationException("RedmineSecurity: Table Check Error", e);
        } finally {
            if (results != null) {
                try { results.close(); } catch (Exception e) {}
            }
            if (state != null) {
                try { state.close(); } catch (Exception e) {}
            }
        }
    }

    @Override
    public boolean isField(String table, String field) throws RedmineAuthenticationException {
        PreparedStatement state = null;
        ResultSet results = null;

        try {
            String query = String.format("SHOW FIELDS FROM %s", table);
            state = conn.prepareStatement(query);
            results = state.executeQuery();

            if (results == null)
                return false;

            while (results.next()) {
                if (results.getString(1).equals(field))
                    return true;
            }

            return false;
        } catch (RedmineAuthenticationException e) {
            throw e;
        } catch (SQLException e) {
            throw new RedmineAuthenticationException("RedmineSecurity: Field Check Error", e);
        } catch (Exception e) {
            throw new RedmineAuthenticationException("RedmineSecurity: Field Check Error", e);
        } finally {
            if (results != null) {
                try { results.close(); } catch (Exception e) {}
            }
            if (state != null) {
                try { state.close(); } catch (Exception e) {}
            }
        }
    }

    @Override
    public RedmineUserData getRedmineUserData(String loginTable, String userField, String passField, String saltField, String username)
            throws RedmineAuthenticationException {
        PreparedStatement state = null;
        ResultSet results = null;

        try {
            String query = String.format("SELECT * FROM %s WHERE %s = ?", loginTable, userField);

            state = conn.prepareStatement(query);
            state.setString(1, username);

            results = state.executeQuery();

            if (results == null)
                return null;

            if (results.next()) {
                RedmineUserData userData = new RedmineUserData();
                userData.setUsername(results.getString(userField));
                userData.setPassword(results.getString(passField));

                if (!StringUtils.isBlank(saltField))
                    userData.setSalt(results.getString(saltField));

                return userData;
            } else
                return null;
        } catch (RedmineAuthenticationException e) {
            throw e;
        } catch (SQLException e) {
            throw new RedmineAuthenticationException("RedmineSecurity: Query Error", e);
        } catch (Exception e) {
            throw new RedmineAuthenticationException("RedmineSecurity: Query Error", e);
        } finally {
            if (results != null) {
                try { results.close(); } catch (Exception e) {}
            }
            if (state != null) {
                try { state.close(); } catch (Exception e) {}
            }
        }
    }

    @Override
	public RedmineGroupData getRedmineGroupData(String loginTable, String field, String name) throws RedmineAuthenticationException {
    	PreparedStatement state = null;
        ResultSet results = null;

        try {
            String query = String.format("SELECT * FROM %s WHERE %s = ? AND type LIKE 'Group%%'", loginTable, field);

            state = conn.prepareStatement(query);
            state.setString(1, name);

            results = state.executeQuery();

            if (results == null)
                return null;

            if (results.next()) {
            	RedmineGroupData userData = new RedmineGroupData();
                userData.setId(results.getInt("id"));
                userData.setName(results.getString("lastname"));

                return userData;
            } else
                return null;
        } catch (RedmineAuthenticationException e) {
            throw e;
        } catch (SQLException e) {
            throw new RedmineAuthenticationException("RedmineSecurity: Query Error", e);
        } catch (Exception e) {
            throw new RedmineAuthenticationException("RedmineSecurity: Query Error", e);
        } finally {
            if (results != null) {
                try { results.close(); } catch (Exception e) {}
            }
            if (state != null) {
                try { state.close(); } catch (Exception e) {}
            }
        }
	}
    
    @Override
	public RedmineGroupData[] getRedmineUserGroups(String loginTable, String userField, String username)
			throws RedmineAuthenticationException {
		PreparedStatement state = null;
	    ResultSet results = null;
	    
	    try {
	    	String query = String.format("SELECT %s.id, %s.%s, groups_users.* FROM %s, groups_users WHERE users.login = ? AND groups_users.user_id = %s.id", loginTable, loginTable, userField, loginTable, loginTable);

            state = conn.prepareStatement(query);
            state.setString(1, username);

            results = state.executeQuery();
            
            if (results == null)
            	return new RedmineGroupData[0];
            
            List<RedmineGroupData> groups = new ArrayList<>();
            while (results.next()) {
            	RedmineGroupData group = getRedmineGroupData(loginTable, "id", results.getString("groups_users.group_id"));
            	if (group != null) {
            		groups.add(group);
            	}
            }
            
            return groups.toArray(new RedmineGroupData[0]);
	    } catch (RedmineAuthenticationException e) {
            throw e;
        } catch (SQLException e) {
            throw new RedmineAuthenticationException("RedmineSecurity: Query Error", e);
        } catch (Exception e) {
            throw new RedmineAuthenticationException("RedmineSecurity: Query Error", e);
        } finally {
            if (results != null) {
                try { results.close(); } catch (Exception e) {}
            }
            if (state != null) {
                try { state.close(); } catch (Exception e) {}
            }
        }
	}
}
