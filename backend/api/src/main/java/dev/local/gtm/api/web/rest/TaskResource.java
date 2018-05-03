package dev.local.gtm.api.web.rest;

import dev.local.gtm.api.domain.Task;
import dev.local.gtm.api.repository.mongo.TaskRepository;
import dev.local.gtm.api.repository.mongo.UserRepository;
import dev.local.gtm.api.security.AuthoritiesConstants;
import dev.local.gtm.api.security.SecurityUtils;
import dev.local.gtm.api.web.exception.InternalServerErrorException;
import dev.local.gtm.api.web.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Log4j2
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class TaskResource {
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    @GetMapping("/tasks")
    public Page<Task> getAllTasks(Pageable pageable) {
        log.debug("REST 请求 -- 查询所有 Task");
        return SecurityUtils.isCurrentUserInRole(AuthoritiesConstants.ADMIN) ?
                taskRepository.findAll(pageable) :
                SecurityUtils.getCurrentUserLogin().map(login -> taskRepository.findByOwnerLogin(pageable, login))
                        .orElseThrow(() -> new InternalServerErrorException("未找到当前登录用户的登录名"));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/tasks/search/findByUserMobile")
    public List<Task> findByUserMobile(Pageable pageable, @RequestParam String mobile) {
        log.debug("REST 请求 -- 查询所有手机号为 {} Task", mobile);
        return taskRepository.findByOwnerMobile(pageable, mobile).getContent();
    }

    @PostMapping("/tasks")
    Task addTask(@RequestBody Task task) {
        log.debug("REST 请求 -- 新增 Task {}", task);
        return SecurityUtils.getCurrentUserLogin()
                .flatMap(userRepository::findOneByLoginIgnoreCase)
                .map(user -> {
                    task.setOwner(user);
                    return taskRepository.save(task);
                })
                .orElseThrow(() -> new ResourceNotFoundException("未找到用户鉴权信息"));
    }

    @PreAuthorize("#toUpdate.owner.login == principal.username or hasRole('ADMIN')")
    @PutMapping("/tasks/{id}")
    public Task updateTask(@PathVariable String id, @RequestBody Task toUpdate) {
        log.debug("REST 请求 -- 更新 id: {} 的 Task {}", id, toUpdate);
        val task = taskRepository.findById(id);
        return task.map(res -> {
            res.setDesc(toUpdate.getDesc());
            res.setCompleted(toUpdate.isCompleted());
            val user = userRepository.findById(toUpdate.getOwner().getId());
            if (user.isPresent()) {
                res.setOwner(user.get());
            } else {
                throw new ResourceNotFoundException("id 为 "+ id + " 的 User 没有找到");
            }
            return taskRepository.save(res);
        }).orElseThrow(() -> new ResourceNotFoundException("id 为 "+ id + " 的 Task 没有找到"));
    }

    @PostAuthorize("returnObject.owner.login == principal.username or hasRole('ADMIN')")
    @GetMapping("/tasks/{id}")
    public Task getTask(@PathVariable String id) {
        log.debug("REST 请求 -- 取得 id: {} 的 Task", id);
        val task = taskRepository.findById(id);
        return task.orElseThrow(() -> new ResourceNotFoundException("id 为 "+ id + " 的 Task 没有找到"));
    }

    @DeleteMapping("/tasks/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteTask(@PathVariable String id) {
        log.debug("REST 请求 -- 删除 id 为 {} 的Task", id);
        val task = taskRepository.findById(id);
        task.ifPresent(taskRepository::delete);
    }
}
