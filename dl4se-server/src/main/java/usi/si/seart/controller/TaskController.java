package usi.si.seart.controller;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
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
import usi.si.seart.dto.task.CodeTaskDto;
import usi.si.seart.dto.task.TaskSearchDto;
import usi.si.seart.dto.task.processing.CodeProcessingDto;
import usi.si.seart.dto.task.query.CodeQueryDto;
import usi.si.seart.model.Language;
import usi.si.seart.model.task.Status;
import usi.si.seart.model.task.Task;
import usi.si.seart.model.task.Task_;
import usi.si.seart.model.task.processing.CodeProcessing;
import usi.si.seart.model.task.query.CodeQuery;
import usi.si.seart.model.user.Role;
import usi.si.seart.model.user.User;
import usi.si.seart.model.user.User_;
import usi.si.seart.model.user.token.Token;
import usi.si.seart.security.UserPrincipal;
import usi.si.seart.service.ConfigurationService;
import usi.si.seart.service.DownloadService;
import usi.si.seart.service.FileSystemService;
import usi.si.seart.service.LanguageService;
import usi.si.seart.service.TaskService;
import usi.si.seart.service.UserService;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.io.File;
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
    LanguageService languageService;
    DownloadService downloadService;
    FileSystemService fileSystemService;
    ConversionService conversionService;
    ConfigurationService configurationService;

    @GetMapping(value = "/{uuid}")
    public ResponseEntity<?> task(@PathVariable UUID uuid) {
        Task task = taskService.getWithUUID(uuid);
        return ResponseEntity.ok(task);
    }

    @SuppressWarnings({"unchecked", "ConstantConditions"})
    @GetMapping
    public ResponseEntity<?> tasks(
            TaskSearchDto taskSearchDto,
            @SortDefault(sort = Task_.SUBMITTED, direction = Sort.Direction.DESC) Pageable pageable,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        Specification<Task> specification = (Specification<Task>) conversionService.convert(taskSearchDto, Specification.class);
        specification = specification.and(
                (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.join(Task_.USER).get(User_.ID), principal.getId())
        );
        Page<Task> tasks = taskService.getAll(specification, pageable);
        return ResponseEntity.ok(tasks);
    }

    @SuppressWarnings("ConstantConditions")
    @PostMapping("/create")
    public ResponseEntity<?> create(
            @Valid @RequestBody CodeTaskDto codeTaskDto, @AuthenticationPrincipal UserPrincipal principal
    ) {
        LocalDateTime requestedAt = LocalDateTime.now(ZoneOffset.UTC);
        User requester = userService.getWithEmail(principal.getEmail());

        Integer taskLimit = configurationService.get("request_limit", Integer.class);
        if (!taskService.canCreateTask(requester, taskLimit))
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();

        CodeQueryDto queryDto = codeTaskDto.getQuery();
        CodeQuery query = conversionService.convert(queryDto, CodeQuery.class);

        Language language = languageService.getWithName(queryDto.getLanguageName());
        query.setLanguage(language);

        CodeProcessingDto processingDto = codeTaskDto.getProcessing();
        CodeProcessing processing = conversionService.convert(processingDto, CodeProcessing.class);

        if (taskService.activeTaskExists(requester, query, processing))
            return ResponseEntity.status(HttpStatus.CONFLICT).build();

        taskService.create(requester, requestedAt, query, processing);
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }

    @PostMapping(value = "/cancel/{uuid}")
    public ResponseEntity<?> cancel(@PathVariable UUID uuid, @AuthenticationPrincipal UserPrincipal principal) {
        User requester = userService.getWithEmail(principal.getEmail());
        Task task = taskService.getWithUUID(uuid);

        Status status = task.getStatus();
        if (Status.Category.INACTIVE.contains(status))
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

        Token token = downloadService.generate(requester);
        return ResponseEntity.ok(token.getValue());
    }

    @GetMapping(value = "/download/{uuid}")
    public ResponseEntity<?> download(@PathVariable UUID uuid, @RequestParam @NotBlank String token) {
        downloadService.consume(token);
        Task task = taskService.getWithUUID(uuid);

        Path exportFilePath = fileSystemService.getTaskArchive(task);
        String exportFileName = exportFilePath.getFileName().toString();
        File exportFile = exportFilePath.toFile();
        long exportFileSize = exportFile.length();
        long exportFileLastModified = exportFile.lastModified();
        Resource resource = new FileSystemResource(exportFile);
        ContentDisposition disposition = ContentDisposition.attachment().filename(exportFileName).build();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("application", "gzip"));
        headers.setContentLength(exportFileSize);
        headers.setContentDisposition(disposition);
        headers.setLastModified(exportFileLastModified);

        return new ResponseEntity<>(resource, headers, HttpStatus.OK);
    }
}
