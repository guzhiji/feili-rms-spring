package com.feiliks.common.dto;

import com.feiliks.common.entities.User;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public class UserDTO {

    private Long id;

    @NotNull(message = "Username must not be null.")
    @Pattern(regexp = "^[a-zA-Z0-9\\-_]{3,64}$", message = "Username is invalid.")
    private String username;

    @Pattern(regexp = "^[ \\-+0-9]{3,16}$", message = "Phone number is invalid.")
    private String phone;

    @Pattern(regexp = "^.+@.+$", message = "Email is invalid.")
    @Size(max = 128, message = "E-mail address must not be longer than 128.")
    private String email;

    public UserDTO() {
    }

    public UserDTO(User user) {
        id = user.getId();
        username = user.getUsername();
        phone = user.getPhone();
        email = user.getEmail();
    }

    public User toEntity() {
        User e = new User();
        e.setId(id);
        e.setUsername(username);
        e.setPhone(phone);
        e.setEmail(email);
        return e;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone == null ? null : phone.trim();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email == null ? null : email.trim();
    }

}
