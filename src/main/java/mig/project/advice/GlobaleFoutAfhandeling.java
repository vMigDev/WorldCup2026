package mig.project.advice;

import lombok.RequiredArgsConstructor;
import mig.project.exceptions.WedstrijdNietGevondenException;
import org.springframework.context.MessageSource;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.Locale;

@ControllerAdvice
@RequiredArgsConstructor
public class GlobaleFoutAfhandeling {

    private final MessageSource messageSource;

    @ExceptionHandler(WedstrijdNietGevondenException.class)
    public String handleWedstrijdNietGevonden(WedstrijdNietGevondenException ex, Model model, Locale locale) {
        model.addAttribute("foutmelding", resolveMessage(ex.getMessage(), locale));
        return "error";
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public String handle404(Model model, Locale locale) {
        model.addAttribute("foutmelding", messageSource.getMessage("error.http.404", null, locale));
        return "error";
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public String handleIllegalArgument(IllegalArgumentException ex, Model model, Locale locale) {
        model.addAttribute("foutmelding", resolveMessage(ex.getMessage(), locale));
        return "error";
    }

    @ExceptionHandler(Exception.class)
    public String handleAlgemeneFout(Exception ex, Model model, Locale locale) {
        model.addAttribute("foutmelding", messageSource.getMessage("error.algemeen", null, locale));
        return "error";
    }

    private String resolveMessage(String keyOrMessage, Locale locale) {
        return messageSource.getMessage(keyOrMessage, null, keyOrMessage, locale);
    }
}
