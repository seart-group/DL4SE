package usi.si.seart.controller;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
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
import usi.si.seart.service.FileSystemService;
import usi.si.seart.service.LanguageService;
import usi.si.seart.service.TaskService;
import usi.si.seart.service.UserService;

import javax.validation.Valid;
import java.io.FileInputStream;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/code")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor(onConstructor_ = @Autowired)
public class CodeController {

    TaskService taskService;
    UserService userService;
    LanguageService languageService;
    FileSystemService fileSystemService;
    ConversionService conversionService;

    @SuppressWarnings("ConstantConditions")
    @PostMapping("/create")
    public ResponseEntity<?> createTask(
            @Valid @RequestBody CodeTaskDto codeTaskDto, @AuthenticationPrincipal UserPrincipal principal
    ) {
        LocalDateTime requestedAt = LocalDateTime.now(ZoneOffset.UTC);
        User requester = userService.getWithEmail(principal.getEmail());

        if (!taskService.canCreateTask(requester))
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
    public ResponseEntity<?> cancelTask(@PathVariable UUID uuid, @AuthenticationPrincipal UserPrincipal principal) {
        User requester = userService.getWithEmail(principal.getEmail());
        Optional<Task> optional = taskService.getWithUUID(uuid);
        if (optional.isEmpty()) return ResponseEntity.notFound().build();

        Task task = optional.get();
        Status status = task.getStatus();
        if (Status.Category.INACTIVE.contains(status))
            return ResponseEntity.badRequest().build();

        User owner = task.getUser();
        boolean canCancel = requester.equals(owner) || requester.getRole().equals(Role.ADMIN);
        if (!canCancel)
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

        taskService.cancel(task);
        return ResponseEntity.ok().build();
    }

    @SneakyThrows
    @GetMapping(value = "/download/{uuid}", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<?> downloadTaskResults(
            @PathVariable UUID uuid, @AuthenticationPrincipal UserPrincipal principal
    ) {
        User requester = userService.getWithEmail(principal.getEmail());
        Optional<Task> optional = taskService.getWithUUID(uuid);
        if (optional.isEmpty()) return ResponseEntity.notFound().build();

        Task task = optional.get();
        if (task.getExpired()) return ResponseEntity.status(HttpStatus.GONE).build();

        User owner = task.getUser();
        Status status = task.getStatus();
        if (!requester.equals(owner) || status != Status.FINISHED)
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

        Path exportFilePath = fileSystemService.getExportFile(task);
        String exportFileName = exportFilePath.getFileName().toString();
        long exportFileSize = exportFilePath.toFile().length();
        InputStreamResource resource = new InputStreamResource(new FileInputStream(exportFilePath.toFile()));
        ContentDisposition disposition = ContentDisposition.attachment().filename(exportFileName).build();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_PLAIN);
        headers.setContentLength(exportFileSize);
        headers.setContentDisposition(disposition);

        return new ResponseEntity<>(resource, headers, HttpStatus.OK);
    }
}
