package com.feiliks.testapp2.controllers;

import com.feiliks.testapp2.AuthTokenUtil;
import com.feiliks.testapp2.AuthorizationException;
import com.feiliks.testapp2.NotFoundException;
import com.feiliks.testapp2.ValidationException;
import com.feiliks.testapp2.dto.EntityMessage;
import com.feiliks.testapp2.dto.Message;
import com.feiliks.testapp2.jpa.entities.User;
import java.net.URI;
import javax.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.util.UriComponents;

abstract class AbstractController {

    @ExceptionHandler(NotFoundException.class)
    protected ResponseEntity<Message> handleNotFound(NotFoundException ex) {
        Message msg = new Message("failure", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(msg);
    }

    @ExceptionHandler(ValidationException.class)
    protected ResponseEntity<Message> handleValidationError(ValidationException ex) {
        Message msg = new Message("failure", ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(msg);
    }

    @ExceptionHandler(AuthorizationException.class)
    protected ResponseEntity<Message> handleAuthorization(AuthorizationException ex) {
        Message msg = new Message("failure", "not authorized");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(msg);
    }

    protected <T> ResponseEntity<EntityMessage<T>> respondCreatedStatus(
            T entity, Class<?> controllerCls, String httpGetMethod, Object id) {
        UriComponents uriComponents = MvcUriComponentsBuilder.fromMethodName(
                controllerCls, httpGetMethod, id).build();
        URI uri = uriComponents.encode().toUri();
        EntityMessage<T> msg = new EntityMessage<>("success", entity);
        return ResponseEntity.created(uri).body(msg);
    }

    protected void requiresPermissions(HttpServletRequest req, User.Permission... perms) {
        User curUser = AuthTokenUtil.getUser(req);
        if (!curUser.hasPermissions(perms)) {
            throw new AuthorizationException();
        }
    }
}
