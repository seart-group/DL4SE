package usi.si.seart.controller;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import usi.si.seart.dto.task.CodeTaskDto;
import usi.si.seart.dto.task.processing.CodeProcessingDto;
import usi.si.seart.dto.task.query.CodeQueryDto;
import usi.si.seart.dto.task.query.FileQueryDto;
import usi.si.seart.dto.task.query.FunctionQueryDto;
import usi.si.seart.model.Language;
import usi.si.seart.model.task.processing.CodeProcessing;
import usi.si.seart.model.task.query.CodeQuery;
import usi.si.seart.model.task.query.FileQuery;
import usi.si.seart.model.task.query.FunctionQuery;
import usi.si.seart.model.user.User;
import usi.si.seart.service.LanguageService;
import usi.si.seart.service.TaskService;
import usi.si.seart.service.UserService;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Slf4j
@RestController
@RequestMapping("/code")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor(onConstructor_ = @Autowired)
public class CodeController {

    TaskService taskService;
    UserService userService;
    LanguageService languageService;
    ConversionService conversionService;

    @SuppressWarnings("ConstantConditions")
    @PostMapping("/create")
    public ResponseEntity<?> createTask(@Valid @RequestBody CodeTaskDto codeTaskDto) {
        LocalDateTime requestedAt = LocalDateTime.now(ZoneOffset.UTC);

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String email = ((UserDetails) principal).getUsername();
        User requester = userService.getWithEmail(email);

        if (!taskService.canCreateTask(requester))
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();

        CodeQueryDto queryDto = codeTaskDto.getQuery();
        CodeQuery query;
        if (queryDto instanceof FileQueryDto) {
            query = conversionService.convert(queryDto, FileQuery.class);
        } else if (queryDto instanceof FunctionQueryDto) {
            query = conversionService.convert(queryDto, FunctionQuery.class);
        } else {
            throw new IllegalStateException(
                    "Converter not defined for DTO type: ["+queryDto.getClass().getName()+"]"
            );
        }

        Language language = languageService.getWithName(queryDto.getLanguageName());
        query.setLanguage(language);

        CodeProcessingDto processingDto = codeTaskDto.getProcessing();
        CodeProcessing processing = conversionService.convert(processingDto, CodeProcessing.class);

        if (taskService.activeTaskExists(requester, query, processing))
            return ResponseEntity.status(HttpStatus.CONFLICT).build();

        taskService.create(requester, requestedAt, query, processing);
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }
}
