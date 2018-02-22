package com.feiliks.testapp2.t1;

import com.feiliks.testapp2.dto.Message;
import java.net.URI;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.sql.DataSource;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
//import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.util.UriComponents;

@RestController
@RequestMapping(value = "/person")
class PersonController {

    @Autowired
    private DataSource dataSource;

    @ExceptionHandler(EmptyResultDataAccessException.class)
    // @ResponseStatus
    public ResponseEntity<Message> handleNotFound(
            EmptyResultDataAccessException ex) {
        Message msg = new Message("failure", "person not found:" + ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(msg);
    }

    @ExceptionHandler(DuplicateKeyException.class)
    public ResponseEntity<Message> handleDuplicateKey(
            DuplicateKeyException ex) {
        Message msg = new Message("failure", "person already exists:" + ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(msg);
    }

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<Message> handleDataAccessException(
            DataAccessException ex) {
        Message msg = new Message("failure", "person could not be stored:" + ex.getMessage());
        return ResponseEntity.unprocessableEntity().body(msg);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Message> handleUnknownException(
            Exception ex) {
        Message msg = new Message("failure", "unknown error occurred:" + ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(msg);
    }

    @PostMapping
    public ResponseEntity<Message> createPerson(
            @RequestBody @Valid Person person,
            BindingResult bindingResult) {
        if (bindingResult.getErrorCount() == 0) {
            new JdbcTemplate(dataSource).update(
                    "insert into test1 (username,password) values (?,?)",
                    new Object[]{person.getUsername(), person.getPassword()});
            UriComponents uriComponents = MvcUriComponentsBuilder.fromMethodName(
                    getClass(), "getPerson", person.getUsername()).build();
            URI uri = uriComponents.encode().toUri();
            Message msg = new Message("success", "person created");
            return ResponseEntity.created(uri).body(msg);
        } else {
            FieldError fe = bindingResult.getFieldError();
            Message msg;
            if (fe == null) {
                msg = new Message("failure", bindingResult.toString());
            } else {
                msg = new Message("failure", fe.getDefaultMessage());
            }
            return ResponseEntity.unprocessableEntity().body(msg);
        }
    }

    @PutMapping("/{username}")
    public ResponseEntity<Message> updatePerson(
            @PathVariable String username,
            @RequestBody @Valid Person person,
            BindingResult bindingResult) {
        if (bindingResult.getErrorCount() == 0) {
            JdbcTemplate db = new JdbcTemplate(dataSource);
            int r = db.update("update test1 set password=?,username=? where username=?",
                    new Object[]{person.getPassword(), person.getUsername(), username});
            if (r > 0) {
                Message msg = new Message("success", "person updated");
                return ResponseEntity.accepted().body(msg);
            }
            return handleNotFound(null);
        } else {
            FieldError fe = bindingResult.getFieldError();
            Message msg;
            if (fe == null) {
                msg = new Message("failure", bindingResult.toString());
            } else {
                msg = new Message("failure", fe.getDefaultMessage());
            }
            return ResponseEntity.unprocessableEntity().body(msg);
        }
    }

    @DeleteMapping("/{username}")
    public ResponseEntity<Message> deletePerson(@PathVariable String username) {
        JdbcTemplate db = new JdbcTemplate(dataSource);
        int r = db.update("delete from test1 where username=?",
                new Object[]{username});
        if (r > 0) {
            return ResponseEntity.noContent().build();
        }
        return handleNotFound(null);
    }

    @GetMapping("/{username}")
    public Person getPerson(@PathVariable String username) {
        return new JdbcTemplate(dataSource).queryForObject(
                "select * from test1 where username=?",
                new Object[]{username},
                new RowMapper<Person>() {
            @Override
            public Person mapRow(ResultSet rs, int rowNum) throws SQLException {
                Person p = new Person();
                p.setUsername(rs.getString("username"));
                p.setPassword(rs.getString("password"));
                return p;
            }
        });
    }

    @GetMapping("/{name:[a-z-]+}-{version:\\d\\.\\d\\.\\d}{ext:\\.[a-z]+}")
    public void handle(@PathVariable String version, @PathVariable String ext) {
        // ...
    }
}
