package dev.local.gtm.api.web.rest;

import dev.local.gtm.api.domain.Task;
import dev.local.gtm.api.repository.TaskRepo;
import dev.local.gtm.api.web.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Log4j2
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class TaskResource {
    private final TaskRepo taskRepo;

    @GetMapping("/tasks")
    public List<Task> getAllTasks(Pageable pageable, @RequestParam(required = false) String desc) {
        log.debug("REST 请求 -- 查询所有 Task");
        return desc == null ?
                taskRepo.findAll(pageable).getContent() :
                taskRepo.findByDescLike(pageable, desc).getContent();
    }

    @GetMapping("/tasks/search/findByUserMobile")
    public List<Task> findByUserMobile(Pageable pageable, @RequestParam String mobile) {
        log.debug("REST 请求 -- 查询所有手机号为 {} Task", mobile);
        return taskRepo.findByOwnerMobile(pageable, mobile).getContent();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/tasks")
    Task addTask(@RequestBody Task task) {
        log.debug("REST 请求 -- 新增 Task {}", task);
        return taskRepo.insert(task);
    }

    @PostAuthorize("returnObject.owner.login == principal.username or hasRole('ROLE_ADMIN')")
    @PutMapping("/tasks/{id}")
    public Task updateTask(@PathVariable String id, @RequestBody Task toUpdate) {
        log.debug("REST 请求 -- 更新 id: {} 的 Task {}", id, toUpdate);
        val task = taskRepo.findById(id);
        return task.map(res -> {
            res.setDesc(toUpdate.getDesc());
            res.setCompleted(toUpdate.isCompleted());
            res.setOwner(toUpdate.getOwner());
            return taskRepo.save(res);
        }).orElseThrow(() -> new ResourceNotFoundException("id 为 "+ id + " 的 Task 没有找到"));
    }

    @GetMapping("/tasks/{id}")
    public Task getTask(@PathVariable String id) {
        log.debug("REST 请求 -- 取得 id: {} 的 Task", id);
        val task = taskRepo.findById(id);
        return task.orElseThrow(() -> new ResourceNotFoundException("id 为 "+ id + " 的 Task 没有找到"));
    }

    @DeleteMapping("/tasks/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteTask(@PathVariable String id) {
        log.debug("REST 请求 -- 删除 id 为 {} 的Task", id);
        val task = taskRepo.findById(id);
        task.ifPresent(taskRepo::delete);
    }
}
