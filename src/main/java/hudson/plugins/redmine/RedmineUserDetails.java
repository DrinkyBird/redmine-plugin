package hudson.plugins.redmine;

import java.util.Arrays;
import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class RedmineUserDetails implements UserDetails {

    /** */
    private static final long serialVersionUID = 1L;

    private GrantedAuthority[] authorities;
    private String password;
    private String username;
    private boolean accountNotExpired;
    private boolean accountNotLocked;
    private boolean credentialsNotExpired;
    private boolean enabled;

    /**
     *
     * @param username
     * @param password
     * @param enabled
     * @param accountNonExpired
     * @param credentialsNonExpired
     * @param accountNonLocked
     * @param authorities
     */
    public RedmineUserDetails(String username, String password, boolean enabled,
            boolean accountNonExpired, boolean credentialsNonExpired,
            boolean accountNonLocked, GrantedAuthority[] authorities) {
        this.username              = username;
        this.password              = password;
        this.enabled               = enabled;
        this.accountNotExpired     = accountNonExpired;
        this.credentialsNotExpired = credentialsNonExpired;
        this.accountNotLocked      = accountNonLocked;
        this.authorities           = authorities;
    }

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
        return Arrays.asList(authorities);
	}

    public String getPassword() {
        return password;
    }

    public String getUsername() {
        return username;
    }

    public boolean isAccountNonExpired() {
        return accountNotExpired;
    }

    public boolean isAccountNonLocked() {
        return accountNotLocked;
    }

    public boolean isCredentialsNonExpired() {
        return credentialsNotExpired;
    }

    public boolean isEnabled() {
        return enabled;
    }

}
