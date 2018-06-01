package com.feiliks.testapp2.blog;

import com.feiliks.testapp2.AuthorizationException;
import com.feiliks.testapp2.NotFoundException;
import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import com.feiliks.testapp2.dto.LoginDTO;
import com.feiliks.testapp2.jpa.entities.User;
import com.feiliks.testapp2.jpa.repositories.UserRepository;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping(value = "/blog")
public class BlogController {

    @Autowired
    private BlogRepository blogRepo;

    @Autowired
    private UserRepository userRepo;

    @ExceptionHandler(AuthorizationException.class)
    protected String handleAuthorization(AuthorizationException ex) {
        return "redirect:/app/blog/logout";
    }

    @ExceptionHandler(NotFoundException.class)
    protected String handleNotFound(NotFoundException ex) {
        return "redirect:/app/blog/";
    }

    protected void checkAuth(HttpServletRequest req) {
        HttpSession session = req.getSession();
        if (session == null || session.getAttribute("username") == null) {
            throw new AuthorizationException();
        }
    }

    protected User getUser(HttpServletRequest req) {
        HttpSession session = req.getSession();
        if (session == null) {
            return null;
        }
        Object username = session.getAttribute("username");
        if (username == null) {
            return null;
        }
        return userRepo.findByUsername((String) username);
    }

    @GetMapping
    public ModelAndView indexPage() {
        Map<String, Object> data = new HashMap<>();
        data.put("blogs", blogRepo.findPublished());
        return new ModelAndView("index", data);
    }

    @GetMapping("/admin")
    public ModelAndView adminPage(HttpServletRequest req, @RequestParam("page") Integer page) {
        checkAuth(req);
        Map<String, Object> data = new HashMap<>();
        User user = getUser(req);
        List<Blog> blogs = new LinkedList<>();
        PageRequest pager = new PageRequest(page == null || page < 1 ? 0 : page - 1, 10);
        if (user != null) {
            Page<Blog> blogsPage = blogRepo.findByOwner(user, pager);
            for (Blog blog : blogsPage.getContent()) {
                blogs.add(blog);
            }
        }
        data.put("page_number", pager.getPageNumber());
        data.put("page_size", pager.getPageSize());
        data.put("blogs", blogs);
        return new ModelAndView("admin", data);
    }

    @GetMapping("/edit")
    public ModelAndView createBlogPage(HttpServletRequest req) {
        checkAuth(req);
        Map<String, Object> data = new HashMap<>();
        data.put("blog", new Blog());
        return new ModelAndView("edit", data);
    }

    @PostMapping("/edit")
    public String createBlog(HttpServletRequest req, @ModelAttribute Blog blog) {
        checkAuth(req);
        User user = getUser(req);
        blog.setSlug(blog.getTitle());
        blog.setOwner(user);
        Date now = new Date();
        blog.setCreated(now);
        blog.setModified(now);
        blogRepo.save(blog);
        return "redirect:/app/blog/";
    }

    @GetMapping("/edit/{id}")
    public ModelAndView editBlogPage(
            HttpServletRequest req,
            @PathVariable Long id) {
        checkAuth(req);
        Blog blog = blogRepo.findOne(id);
        if (blog == null) {
            throw new NotFoundException(Blog.class, id.toString());
        }
        Map<String, Object> data = new HashMap<>();
        data.put("blog", blog);
        return new ModelAndView("edit", data);
    }

    @PostMapping("/edit/{id}")
    public String updateBlog(
            HttpServletRequest req,
            @PathVariable Long id,
            @ModelAttribute Blog blog) {
        checkAuth(req);
        Blog original = blogRepo.findOne(id);
        if (original == null) {
            throw new NotFoundException(Blog.class, id.toString());
        }
        original.setTitle(blog.getTitle());
        original.setContent(blog.getContent());
        original.setPublished(blog.isPublished());
        original.setModified(new Date());
        blogRepo.save(original);
        return "redirect:/app/blog/edit/" + id;
    }

    @GetMapping("/view/{slug}")
    public ModelAndView viewBlogPage(HttpServletRequest req, @PathVariable String slug) {
        Blog blog = blogRepo.findBySlug(slug);
        if (blog == null) {
            throw new NotFoundException(Blog.class, slug);
        }
        Map<String, Object> data = new HashMap<>();
        data.put("blog", blog);
        return new ModelAndView("blog", data);
    }

    @GetMapping("/login")
    public String loginPage(HttpServletRequest req) {
        HttpSession session = req.getSession();
        if (session != null && session.getAttribute("username") != null) {
            return "redirect:/app/blog/admin";
        }
        return "login";
    }

    @PostMapping("/login")
    public String login(
            HttpServletRequest req,
            @ModelAttribute LoginDTO login) {
        if (login.getPassword().equals("abcabc")) {
            HttpSession session = req.getSession(true);
            session.setAttribute("username", login.getUsername());
            return "redirect:/app/blog/admin?page=1";
        }
        return logout(req);
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest req) {
        HttpSession session = req.getSession();
        if (session != null) {
            session.invalidate();
        }
        return "redirect:/app/blog/login";
    }

}
