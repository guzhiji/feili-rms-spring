package com.feiliks.blog

import com.feiliks.blog.dtos.BlogDto
import com.feiliks.blog.entities.BlogEntity
import com.feiliks.blog.entities.TagEntity
import com.feiliks.blog.entities.UserEntity
import com.feiliks.blog.repositories.BlogRepository
import com.feiliks.blog.repositories.TagRepository
import com.feiliks.blog.repositories.UserRepository
import com.feiliks.common.AuthorizationException
import com.feiliks.common.NotFoundException
import com.feiliks.common.PasswordUtil
import com.feiliks.common.dto.LoginDto
import com.feiliks.common.dto.PasswordDto
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.ModelAndView
import org.springframework.web.servlet.mvc.support.RedirectAttributes
import java.util.*
import javax.servlet.http.HttpServletRequest
import kotlin.collections.HashMap
import kotlin.collections.HashSet


@Controller
@RequestMapping(value = ["/"])
class BlogController {

    @Autowired
    private lateinit var blogRepo: BlogRepository

    @Autowired
    private lateinit var userRepo: UserRepository

    @Autowired
    private lateinit var tagRepo: TagRepository

    @ExceptionHandler(AuthorizationException::class)
    protected fun handleAuthorization(ex: AuthorizationException) : String {
        return "redirect:/logout"
    }

    @ExceptionHandler(NotFoundException::class)
    protected fun handleNotFound(ex: NotFoundException): String {
        return "redirect:/"
    }

    protected fun checkAuth(req: HttpServletRequest, user: UserEntity? = null) {
        val session = req.session
        if (session?.getAttribute("username") == null)
            throw AuthorizationException()
        if (user != null) {
            if (user.username != session.getAttribute("username"))
                throw AuthorizationException()
        }
    }

    protected fun getUser(req: HttpServletRequest): UserEntity? {
        val username = req.session?.getAttribute("username") ?: return null
        return userRepo.findByUsername(username as String)
    }

    protected fun checkTags(
            original: MutableSet<TagEntity>,
            updated: Set<TagEntity>) {
        val tagsToAdd = HashSet<TagEntity>()
        val tagsToRemove = HashSet<TagEntity>()
        val updatedFetched = HashSet<TagEntity>()
        for (tag in updated) {
            val tname = tag.name.trim()
            if (tname.isEmpty()) continue
            var old = tagRepo.findByName(tname)
            if (old == null) { // new tag
                old = tagRepo.save(tag)
                tagsToAdd += old
                updatedFetched += old
            } else { // existing tag but not in original
                if (old !in original)
                    tagsToAdd += old
                updatedFetched += old
            }
        }
        for (tag in original) {
            if (tag !in updatedFetched)
                tagsToRemove += tag
        }
        original += tagsToAdd
        original -= tagsToRemove
    }

    @GetMapping
    fun indexPage(req: HttpServletRequest): ModelAndView {
        val data = HashMap<String, Any?>()
        data["blogs"] = blogRepo.findPublished()
        data["user"] = getUser(req)
        data["contextPath"] = req.contextPath
        return ModelAndView("index", data)
    }

    @GetMapping("/admin")
    fun adminPage(
            req: HttpServletRequest,
            @RequestParam(name = "page", required = false) page: Int?
    ): ModelAndView {
        checkAuth(req)
        val data = HashMap<String, Any?>()
        val user = getUser(req)
        val blogs = LinkedList<BlogEntity>()
        val p = page?.minus(1) ?: 0
        val pager = PageRequest.of(if (p > 0) p else 0, 10)
        if (user != null) {
            val blogsPage = blogRepo.findByOwner(user, pager)
            blogs += blogsPage.content
        }
        data["page_number"] = pager.pageNumber
        data["page_size"] = pager.pageSize
        data["blogs"] = blogs
        data["contextPath"] = req.contextPath
        return ModelAndView("admin", data)
    }

    @GetMapping("/edit")
    fun createBlogPage(req: HttpServletRequest): String {
        checkAuth(req)
        val user = getUser(req)
        var entity = blogRepo.findBySlugAndOwner(null, user)
        if (entity == null) {
            entity = BlogEntity()
            entity.slug = null
            entity.title = "[new]"
            entity.published = false
            entity.owner = user
            val now = Date()
            entity.created = now
            entity.modified = now
            entity = blogRepo.save(entity)
        }
        return "redirect:/edit/${entity.id}"
    }

    @GetMapping("/edit/{id}")
    fun editBlogPage(
            req: HttpServletRequest,
            @PathVariable id: Long
    ): ModelAndView {
        val blog = blogRepo.findById(id).orElseThrow {
            throw NotFoundException(BlogEntity::class.java, id.toString())
        }
        checkAuth(req, blog.owner)
        val data = HashMap<String, Any?>()
        data["blog"] = BlogDto(blog)
        data["contextPath"] = req.contextPath
        return ModelAndView("edit", data)
    }

    @PostMapping("/edit/{id}")
    fun updateBlog(
            req: HttpServletRequest,
            @PathVariable id: Long,
            @ModelAttribute blog: BlogDto
    ): String {
        val original = blogRepo.findById(id).orElseThrow {
            throw NotFoundException(BlogEntity::class.java, id.toString())
        }
        checkAuth(req, original.owner)
        original.slug = blog.slug
        original.published = blog.published
        original.title = blog.title
        original.content = blog.content
        original.modified = Date()
        checkTags(original.tags as MutableSet<TagEntity>, blog.tagsAsSet)
        blogRepo.save(original)
        return "redirect:/edit/$id"
    }

    @GetMapping("/view/{slug}")
    fun viewBlogPage(
            req: HttpServletRequest,
            @PathVariable slug: String): ModelAndView {
        val blog = blogRepo.findBySlug(slug) ?:
            throw NotFoundException(BlogEntity::class.java, slug)
        val data = HashMap<String, Any?>()
        data["blog"] = blog
        data["user"] = getUser(req)
        data["contextPath"] = req.contextPath
        return ModelAndView("blog", data)
    }

    @GetMapping("/login")
    fun loginPage(req: HttpServletRequest): ModelAndView {
        if (req.session?.getAttribute("username") != null)
            return ModelAndView("redirect:/admin")
        val data = HashMap<String, Any?>()
        data["contextPath"] = req.contextPath
        return ModelAndView("login", data)
    }

    @PostMapping("/login")
    fun login(
            req: HttpServletRequest,
            @ModelAttribute login: LoginDto,
            redirectAtts: RedirectAttributes
    ): String {
        val user = userRepo.findByUsername(login.username)
        if (user != null && PasswordUtil.hash(login.username!!, login.password!!) == user.password) {
            req.getSession(true).setAttribute("username", login.username);
            return "redirect:/admin"
        }
        redirectAtts.addFlashAttribute("error_message", "Failed to login")
        return logout(req)
    }

    @GetMapping("/logout")
    fun logout(req: HttpServletRequest): String {
        req.session?.invalidate()
        return "redirect:/login"
    }

    @GetMapping("/chpwd")
    fun chPwdPage(req: HttpServletRequest): ModelAndView {
        checkAuth(req)
        val data = HashMap<String, Any?>()
        data["contextPath"] = req.contextPath
        return ModelAndView("chpwd", data)
    }

    @PostMapping("/chpwd")
    fun chPwd(
            req: HttpServletRequest,
            @ModelAttribute pass: PasswordDto,
            redirectAtts: RedirectAttributes
    ): String {
        checkAuth(req)
        val user = getUser(req)
        if (user != null && PasswordUtil.hash(user.username, pass.original!!) == user.password) {
            user.password = PasswordUtil.hash(user.username, pass.password!!)
            userRepo.save(user)
            redirectAtts.addFlashAttribute("message", "Your password has been updated.")
        } else {
            redirectAtts.addFlashAttribute("error_message", "Original password is incorrect.")
        }
        return "redirect:/chpwd"
    }

    @GetMapping("/adduser")
    fun addUserPage(req: HttpServletRequest): ModelAndView {
        val data = HashMap<String, Any?>()
        data["contextPath"] = req.contextPath
        return ModelAndView("adduser", data)
    }

    @PostMapping("/adduser")
    fun addUser(
            req: HttpServletRequest,
            @ModelAttribute login: LoginDto,
            redirectAtts: RedirectAttributes
    ): String {
        val user = UserEntity()
        user.username = login.username!!
        user.password = PasswordUtil.hash(login.username!!, login.password!!)
        userRepo.save(user)
        redirectAtts.addFlashAttribute("message", "User ${user.username} is created.")
        return "redirect:/adduser"
    }

}
