package com.feiliks.blog;

import com.feiliks.common.AuthorizationException;
import com.feiliks.common.NotFoundException;
import com.feiliks.common.PasswordUtil;
import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import com.feiliks.common.dto.LoginDTO;
import com.feiliks.common.dto.PasswordDTO;
import com.feiliks.rms.entities.Tag;
import com.feiliks.common.entities.User;
import com.feiliks.rms.repositories.TagRepository;
import com.feiliks.common.repositories.UserRepository;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping(value = "/blog")
public class BlogController {

    @Autowired
    private BlogRepository blogRepo;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private TagRepository tagRepo;

    @ExceptionHandler(AuthorizationException.class)
    protected String handleAuthorization(AuthorizationException ex) {
        return "redirect:/app/blog/logout";
    }

    @ExceptionHandler(NotFoundException.class)
    protected String handleNotFound(NotFoundException ex) {
        return "redirect:/app/blog/";
    }

    protected void checkAuth(HttpServletRequest req) {
        checkAuth(req, null);
    }

    protected void checkAuth(HttpServletRequest req, User user) {
        HttpSession session = req.getSession();
        if (session == null || session.getAttribute("username") == null) {
            throw new AuthorizationException();
        }
        if (user != null) {
            if (user.getUsername() == null
                    || !user.getUsername().equals(session.getAttribute("username"))) {
                throw new AuthorizationException();
            }
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

    protected void checkTags(Collection<Tag> original, Collection<Tag> updated) {
        Set<Tag> tagsToAdd = new HashSet<>(), tagsToRemove = new HashSet<>(),
                updatedFetched = new HashSet<>();
        for (Tag tag : updated) {
            String tname = tag.getName();
            if (tname == null || tname.trim().isEmpty()) {
                continue;
            }
            Tag old = tagRepo.findByName(tname);
            if (old == null) { // completely new tag
                old = tagRepo.save(tag);
                tagsToAdd.add(old);
                updatedFetched.add(old);
            } else {
                if (!original.contains(old)) { // existing new tag
                    tagsToAdd.add(old);
                }
                updatedFetched.add(old);
            }
        }
        for (Tag tag : original) {
            if (!updatedFetched.contains(tag)) {
                tagsToRemove.add(tag);
            }
        }
        original.addAll(tagsToAdd);
        original.removeAll(tagsToRemove);
    }

    @GetMapping
    public ModelAndView indexPage(HttpServletRequest req) {
        Map<String, Object> data = new HashMap<>();
        data.put("blogs", blogRepo.findPublished());
        data.put("user", getUser(req));
        data.put("contextPath", req.getContextPath() + "/app/blog");
        return new ModelAndView("index", data);
    }

    @GetMapping("/admin")
    public ModelAndView adminPage(
            HttpServletRequest req,
            @RequestParam(name = "page", required = false) Integer page) {
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
        data.put("contextPath", req.getContextPath() + "/app/blog");
        return new ModelAndView("admin", data);
    }

    @GetMapping("/edit")
    public String createBlogPage(HttpServletRequest req) {
        checkAuth(req);
        User user = getUser(req);
        Blog entity = blogRepo.findBySlugAndOwner(null, user);
        if (entity == null) {
            entity = new Blog();
            entity.setSlug(null);
            entity.setTitle("[new]");
            entity.setPublished(false);
            entity.setOwner(user);
            Date now = new Date();
            entity.setCreated(now);
            entity.setModified(now);
            entity = blogRepo.save(entity);
        }
        return "redirect:/app/blog/edit/" + entity.getId();
    }

    @GetMapping("/edit/{id}")
    public ModelAndView editBlogPage(
            HttpServletRequest req,
            @PathVariable Long id) {
        Blog blog = blogRepo.findOne(id);
        if (blog == null) {
            throw new NotFoundException(Blog.class, id.toString());
        }
        checkAuth(req, blog.getOwner());
        Map<String, Object> data = new HashMap<>();
        data.put("blog", new BlogDTO(blog));
        data.put("contextPath", req.getContextPath() + "/app/blog");
        return new ModelAndView("edit", data);
    }

    @PostMapping("/edit/{id}")
    public String updateBlog(
            HttpServletRequest req,
            @PathVariable Long id,
            @ModelAttribute BlogDTO blog) {
        Blog original = blogRepo.findOne(id);
        if (original == null) {
            throw new NotFoundException(Blog.class, id.toString());
        }
        checkAuth(req, original.getOwner());
        Blog entity = blog.toEntity();
        original.setSlug(entity.getSlug());
        original.setPublished(entity.isPublished());
        original.setTitle(entity.getTitle());
        original.setContent(entity.getContent());
        original.setModified(new Date());
        checkTags(original.getTags(), entity.getTags());
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
        data.put("user", getUser(req));
        data.put("contextPath", req.getContextPath() + "/app/blog");
        return new ModelAndView("blog", data);
    }

    @GetMapping("/login")
    public ModelAndView loginPage(HttpServletRequest req) {
        HttpSession session = req.getSession();
        if (session != null && session.getAttribute("username") != null) {
            return new ModelAndView("redirect:/app/blog/admin");
        }
        Map<String, Object> data = new HashMap<>();
        data.put("contextPath", req.getContextPath() + "/app/blog");
        return new ModelAndView("login", data);
    }

    @PostMapping("/login")
    public String login(
            HttpServletRequest req,
            @ModelAttribute LoginDTO login,
            RedirectAttributes redirectAtts) {
        User user = userRepo.findByUsername(login.getUsername());
        if (user != null && PasswordUtil.hash(user.getUsername(), login.getPassword()).equals(user.getPassword())) {
            HttpSession session = req.getSession(true);
            session.setAttribute("username", login.getUsername());
            return "redirect:/app/blog/admin";
        }
        redirectAtts.addFlashAttribute("error_message", "Failed to login");
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

    @GetMapping("/chpwd")
    public ModelAndView chPwdPage(HttpServletRequest req) {
        checkAuth(req);
        Map<String, Object> data = new HashMap<>();
        data.put("contextPath", req.getContextPath() + "/app/blog");
        return new ModelAndView("chpwd", data);
    }

    @PostMapping("/chpwd")
    public String chPwd(
            HttpServletRequest req,
            @ModelAttribute PasswordDTO pass,
            RedirectAttributes redirectAtts) {
        checkAuth(req);
        User user = getUser(req);
        if (PasswordUtil.hash(user.getUsername(), pass.getOriginal()).equals(user.getPassword())) {
            user.setPassword(PasswordUtil.hash(user.getUsername(), pass.getPassword()));
            userRepo.save(user);
            redirectAtts.addFlashAttribute("message", "Your password has been updated.");
        } else {
            redirectAtts.addFlashAttribute("error_message", "Original password is incorrect.");
        }
        return "redirect:/app/blog/chpwd";
    }

    @GetMapping("/adduser")
    public ModelAndView addUserPage(HttpServletRequest req) {
        Map<String, Object> data = new HashMap<>();
        data.put("contextPath", req.getContextPath() + "/app/blog");
        return new ModelAndView("adduser", data);
    }

    @PostMapping("/adduser")
    public String addUser(
            HttpServletRequest req,
            @ModelAttribute LoginDTO login,
            RedirectAttributes redirectAtts) {
        User user = new User();
        user.setUsername(login.getUsername());
        user.setPassword(PasswordUtil.hash(login.getUsername(), login.getPassword()));
        userRepo.save(user);
        redirectAtts.addFlashAttribute("message", "User " + user.getUsername() + " is created.");
        return "redirect:/app/blog/adduser";
    }

}
