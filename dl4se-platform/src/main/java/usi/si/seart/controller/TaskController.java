package usi.si.seart.controller;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.io.InputStreamResource;
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
import usi.si.seart.dto.task.processing.CodeProcessingDto;
import usi.si.seart.dto.task.query.CodeQueryDto;
import usi.si.seart.model.Language;
import usi.si.seart.model.task.Status;
import usi.si.seart.model.task.Task;
import usi.si.seart.model.task.processing.CodeProcessing;
import usi.si.seart.model.task.query.CodeQuery;
import usi.si.seart.model.user.Role;
import usi.si.seart.model.user.User;
import usi.si.seart.security.UserPrincipal;
import usi.si.seart.service.ConfigurationService;
import usi.si.seart.service.FileSystemService;
import usi.si.seart.service.LanguageService;
import usi.si.seart.service.TaskService;
import usi.si.seart.service.UserService;

import javax.validation.Valid;
import java.io.FileInputStream;
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
    FileSystemService fileSystemService;
    ConversionService conversionService;
    ConfigurationService configurationService;

    @GetMapping(value = "/{uuid}")
    public ResponseEntity<?> task(@PathVariable UUID uuid) {
        Task task = taskService.getWithUUID(uuid);
        return ResponseEntity.ok(task);
    }

    @GetMapping
    public ResponseEntity<?> tasks(
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "submitted") String column,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        User requester = userService.getWithId(principal.getId());
        Integer pageSize = configurationService.get("page_size", Integer.class);
        return ResponseEntity.ok(taskService.getAll(requester, page, pageSize, column));
    }

    @GetMapping("/stats")
    public ResponseEntity<?> statsTasks(@AuthenticationPrincipal UserPrincipal principal) {
        User requester = userService.getWithId(principal.getId());
        return ResponseEntity.ok(taskService.getSummary(requester));
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
        boolean canCancel = requester.equals(owner) || requester.getRole().equals(Role.ADMIN);
        if (!canCancel)
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

        taskService.cancel(task);
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }

    @SneakyThrows
    @GetMapping(value = "/download/{uuid}")
    public ResponseEntity<?> downloadResults(
            @PathVariable UUID uuid, @AuthenticationPrincipal UserPrincipal principal
    ) {
        User requester = userService.getWithEmail(principal.getEmail());
        Task task = taskService.getWithUUID(uuid);
        if (task.getExpired()) return ResponseEntity.status(HttpStatus.GONE).build();

        User owner = task.getUser();
        Status status = task.getStatus();
        boolean canDownload = requester.equals(owner) && status == Status.FINISHED;
        if (!canDownload) return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

        Path exportFilePath = fileSystemService.getTaskArchive(task);
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
