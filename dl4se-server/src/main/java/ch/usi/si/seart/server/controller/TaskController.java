package ch.usi.si.seart.server.controller;

import ch.usi.si.seart.model.dataset.Dataset;
import ch.usi.si.seart.model.task.Status;
import ch.usi.si.seart.model.task.Statuses;
import ch.usi.si.seart.model.task.Task;
import ch.usi.si.seart.model.task.Task_;
import ch.usi.si.seart.model.user.Role;
import ch.usi.si.seart.model.user.User;
import ch.usi.si.seart.model.user.token.DownloadToken;
import ch.usi.si.seart.server.dto.task.TaskDto;
import ch.usi.si.seart.server.dto.task.TaskSearchDto;
import ch.usi.si.seart.server.security.UserPrincipal;
import ch.usi.si.seart.server.service.ConfigurationService;
import ch.usi.si.seart.server.service.FileSystemService;
import ch.usi.si.seart.server.service.TaskService;
import ch.usi.si.seart.server.service.TokenService;
import ch.usi.si.seart.server.service.UserService;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.web.SortDefault;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

@RestController
@RequestMapping("/task")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor(onConstructor_ = @Autowired)
public class TaskController {

    TaskService taskService;
    UserService userService;

    TokenService<DownloadToken> downloadService;

    FileSystemService fileSystemService;
    ConversionService conversionService;
    ConfigurationService configurationService;

    @GetMapping(value = "/{uuid}")
    public ResponseEntity<?> task(@PathVariable UUID uuid) {
        Task task = taskService.getWithUUID(uuid);
        return ResponseEntity.ok(task);
    }

    @SuppressWarnings({"unchecked", "DataFlowIssue"})
    @GetMapping
    public ResponseEntity<?> tasks(
            TaskSearchDto taskSearchDto,
            @SortDefault(sort = Task_.SUBMITTED, direction = Sort.Direction.DESC) Pageable pageable,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        User requester = userService.getWithEmail(principal.getEmail());
        Specification<Task> query = conversionService.convert(taskSearchDto, Specification.class);
        Specification<Task> authorities = conversionService.convert(requester, Specification.class);
        Page<Task> tasks = taskService.getAll(query.and(authorities), pageable);
        return ResponseEntity.ok(tasks);
    }

    @PostMapping("/{dataset}/create")
    public ResponseEntity<?> create(
            @PathVariable Dataset dataset,
            @Valid @RequestBody TaskDto taskDto,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        User requester = userService.getWithEmail(principal.getEmail());
        JsonNode query = taskDto.getQuery();
        JsonNode processing = taskDto.getProcessing();
        LocalDateTime requestedAt = LocalDateTime.now(ZoneOffset.UTC);

        long limit = configurationService.get("request_limit", Long.class);
        long active = taskService.countActiveTasks(requester);
        if (active >= limit)
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();

        if (taskService.activeTaskExists(requester, dataset, query, processing))
            return ResponseEntity.status(HttpStatus.CONFLICT).build();

        taskService.create(requester, dataset, query, processing, requestedAt);
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }

    @PostMapping(value = "/cancel/{uuid}")
    public ResponseEntity<?> cancel(@PathVariable UUID uuid, @AuthenticationPrincipal UserPrincipal principal) {
        User requester = userService.getWithEmail(principal.getEmail());
        Task task = taskService.getWithUUID(uuid);

        Status status = task.getStatus();
        if (Statuses.INACTIVE.contains(status))
            return ResponseEntity.badRequest().build();

        User owner = task.getUser();
        boolean isOwner = requester.equals(owner);
        boolean isAdmin = requester.getRole().equals(Role.ADMIN);
        boolean canCancel = isOwner || isAdmin;
        if (!canCancel)
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

        taskService.cancel(task);
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }

    @GetMapping(value = "/token/{uuid}")
    public ResponseEntity<?> requestAccess(@PathVariable UUID uuid, @AuthenticationPrincipal UserPrincipal principal) {
        User requester = userService.getWithEmail(principal.getEmail());
        Task task = taskService.getWithUUID(uuid);

        User owner = task.getUser();
        boolean isOwner = requester.equals(owner);
        boolean isAdmin = Role.ADMIN == requester.getRole();
        Status status = task.getStatus();
        boolean isFinished = status == Status.FINISHED;
        boolean canDownload = isFinished && (isOwner || isAdmin);
        if (!canDownload) return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

        DownloadToken token = downloadService.generate(requester);
        return ResponseEntity.ok(token.getValue());
    }

    @GetMapping(value = "/download/{uuid}")
    public ResponseEntity<?> download(
            @PathVariable UUID uuid, @RequestParam @NotBlank String token
    ) throws FileNotFoundException {
        downloadService.verify(token);
        Task task = taskService.getWithUUID(uuid);

        Path exportFilePath = fileSystemService.getArchive(task);
        String exportFileName = exportFilePath.getFileName().toString();
        long exportFileSize = exportFilePath.toFile().length();
        InputStreamResource resource = new InputStreamResource(new FileInputStream(exportFilePath.toFile()));
        ContentDisposition disposition = ContentDisposition.attachment().filename(exportFileName).build();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("application", "gzip"));
        headers.setContentLength(exportFileSize);
        headers.setContentDisposition(disposition);

        return new ResponseEntity<>(resource, headers, HttpStatus.OK);
    }
}
