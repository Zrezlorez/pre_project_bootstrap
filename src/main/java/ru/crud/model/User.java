package ru.crud.model;




import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Table(
        name = "users",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "username")
        }
)
@Component
public class User implements UserDetails {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String username;

    @Column
    private String lastname;

    @Column
    private String email;

    @Column
    private String password;

    @Column
    private Integer age;

    @ManyToMany(cascade = CascadeType.MERGE)
    @JoinTable(name = "users_roles",
            joinColumns = @JoinColumn(name = "username"),
            inverseJoinColumns = @JoinColumn(name = "role"))
    private List<Role> roles;



    public User() {

    }

    public User(String username, String lastname, String email, String password, Integer age, List<Role> roles) {
        this.username = username;
        this.lastname = lastname;
        this.email = email;
        this.password = password;
        this.age = age;
        this.roles = roles;
    }

    public User(String username, String lastname, String email, String password, List<Role> roles) {
        this.username = username;
        this.lastname = lastname;
        this.email = email;
        this.password = password;
        this.roles = roles;
    }

    public User(Long id, String username, String lastname, String email, String password, Integer age, List<Role> roles) {
        this.id = id;
        this.lastname = lastname;
        this.email = email;
        this.username = username;
        this.password = password;
        this.age = age;
        this.roles = roles;
    }

    public String getUsername() {
        return username;
    }

    @Override
    public List<Role> getAuthorities() {
        return roles.stream()
                .map(role -> new Role(role.getRole()))
                .collect(Collectors.toList());
    }

    public void addRole(Role role) {
        this.roles.add(role);
    }

    public void deleteRole(Role role) {
        this.roles.remove(role);
    }
    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }


    public String getPassword() {
        return password;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public List<Role> getRoles() {
        return roles;
    }

    public List<String> getStringRoles() {
        List<String> roles = new ArrayList<>();
        for(Role role : this.roles) {
            roles.add(role.toString());
        }
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", age=" + age +
                ", roles=" + roles +
                ", isAdmin=" +
                '}';
    }
}
