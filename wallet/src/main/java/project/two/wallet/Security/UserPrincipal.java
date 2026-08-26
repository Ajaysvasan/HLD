package project.two.wallet.Security;

import java.util.Collection;
import java.util.List;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import project.two.wallet.Entity.User;

public class UserPrincipal implements UserDetails {
	private final User user;

	public UserPrincipal(User user) {
		this.user = user;
	}

	public Long getUserId() {
		return user.getUserId();
	}

	public String getUserName() {
		return user.getUserName();
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return List.of();
	}

	@Override
	public String getPassword() {
		return user.getPassword();
	}

	@Override
	public String getUsername() {
		return user.getEmail();
	}
}
