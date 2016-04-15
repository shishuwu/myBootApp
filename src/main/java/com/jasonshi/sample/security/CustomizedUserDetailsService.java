package com.jasonshi.sample.security;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import com.jasonshi.sample.entity.Privilege;
import com.jasonshi.sample.entity.Role;
import com.jasonshi.sample.entity.User;
import com.jasonshi.sample.repository.RoleRepository;
import com.jasonshi.sample.repository.UserRepository;

@Component
public class CustomizedUserDetailsService implements UserDetailsService {

	@Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
 
    @Override
    public UserDetails loadUserByUsername(String lastname) throws UsernameNotFoundException {
        User user = userRepository.findByLastName(lastname);
        if (user == null) {
            return new org.springframework.security.core.userdetails.User(
              " ", " ", true, true, true, true, 
              getAuthorities(Arrays.asList(roleRepository.findByName("ROLE_USER"))));
        }
 
        return new org.springframework.security.core.userdetails.User(
          user.getEmail(), user.getPassword(), user.isEnabled(), true, true, 
          true, getAuthorities(user.getRoles()));
    }
	
	private Collection<? extends GrantedAuthority> getAuthorities(Collection<Role> roles) {
		return getGrantedAuthorities(getPrivileges(roles));
	}

	private List<String> getPrivileges(Collection<Role> roles) {
		List<String> privileges = new ArrayList<String>();
		List<Privilege> collection = new ArrayList<Privilege>();
		for (Role role : roles) {
			collection.addAll(role.getPrivileges());
		}
		for (Privilege item : collection) {
			privileges.add(item.getName());
		}
		return privileges;
	}

	private List<GrantedAuthority> getGrantedAuthorities(List<String> privileges) {
		List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
		for (String privilege : privileges) {
			authorities.add(new SimpleGrantedAuthority(privilege));
		}
		return authorities;
	}
	
	// @Autowired
	// private UserService userService;
	//
	// @Override
	// public UserDetails loadUserByUsername(String username) throws
	// UsernameNotFoundException {
	//
	// // creating dummy user details, should do JDBC/JPA operations
	// return new UserDetails() {
	//
	// private static final long serialVersionUID = 2059202961588104658L;
	//
	// @Override
	// public boolean isEnabled() {
	// return true;
	// }
	//
	// @Override
	// public boolean isCredentialsNonExpired() {
	// return true;
	// }
	//
	// @Override
	// public boolean isAccountNonLocked() {
	// return true;
	// }
	//
	// @Override
	// public boolean isAccountNonExpired() {
	// return true;
	// }
	//
	// @Override
	// public String getUsername() {
	// // any user could be login
	// return username;
	// }
	//
	// @Override
	// public String getPassword() {
	// // same as user name
	// return username;
	// }
	//
	// @Override
	// public Collection<? extends GrantedAuthority> getAuthorities() {
	// List<SimpleGrantedAuthority> auths = new
	// java.util.ArrayList<SimpleGrantedAuthority>();
	// auths.add(new SimpleGrantedAuthority("ROLE_USER"));
	// auths.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
	// return auths;
	// }
	// };
	// }

}
